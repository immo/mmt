#!/usr/bin/env python
# coding:utf-8
# python version=2.7
from __future__ import print_function
import sys

class readable_stream(object):
    def __init__(self,stream):
        self.stream = stream
        self.location = 0
        self.len = len(stream)

    def read(self,count=1, smart=1):
        if (count == 1) and (smart):
            self.location += 1
            return self.stream[self.location-1]
        start = self.location
        self.location += count
        return self.stream[start:start+count]

    def beWord(self):
        """read unsigned big-endian word"""
        return (self.read()<<8) + self.read()

    def beDWord(self):
        """read unsinged big-endian dword"""
        return (self.read()<<24) + (self.read()<<16) + (self.read()<<8) + self.read()

    def rbeWord(self):
        """read unsigned run-encoded big-endian word"""
        dw = 0
        while 1:
            b = self.read()
            dw <<= 7
            dw |= b & 0x7F
            if not b&128:
                return dw

    def eof(self):
        return self.location >= self.len

def extract_midi_events(stream):
    delta_time = 0
    events = []
    data_length = {8: 2, #note off (note velocity) \
                   9: 2, #note on (note velocity) \
                   0xA: 2, #after-touch (note velocity) \
                   0xB: 2, #control change (controler value) \
                   0xC: 1, #program change (prnumber) \
                   0xD: 1, #channel after-touch (channel) \
                   0xE: 1} #pitch wheel change (bottom top) \
    current_command = 8
    current_channel = 0
    while not stream.eof():
        delta_time += stream.rbeWord()
        b = stream.read()
        if stream.eof():
            break
        if b == 0xFF: #meta command
            meta_command = stream.read()
            meta_length = stream.read()
            meta_data = stream.read(meta_length, 0)
            event = tuple([delta_time, 0xFF, meta_command, meta_length]+meta_data)
            events.append(event)
        if b >= 0xF0:
            event = tuple([delta_time, b])
        elif b&0x80:
            current_command = b>>4
            current_channel = b&0xF
            data = stream.read(data_length[current_command],0)
            event = tuple([delta_time, current_command, current_channel]+data)
            events.append(event)
        else:
            more_data = data_length[current_command]-1
            data = [b] + stream.read(more_data, 0)
            event = tuple([delta_time, current_command, current_channel]+data)
            events.append(event)
    return events
        

def read_midi(f):
    header = f.read(14)
    if header[0:8] != 'MThd\x00\x00\x00\x06':
        return None
    format = (ord(header[8])<<8)+ord(header[9]);
    tracks = (ord(header[10])<<8)+ord(header[11]);
    ticks_per_quarter = ord(header[12])<<8+ord(header[13])
    if not format in [0,1]:
        return None
    track_events = []
    for i in range(tracks):
        track_header = f.read(8)
        if track_header[0:4] != 'MTrk':
            break
        
        data_length = (ord(track_header[4])<<24)+(ord(track_header[5])<<16)\
                      +(ord(track_header[6])<<8)+ord(track_header[7])
        data = readable_stream(map(lambda x:ord(x), f.read(data_length)))
        track_events.append(extract_midi_events(data))
        
        
    return track_events

argc = len(sys.argv)
option_count = 1
filter_for_keys = 0
just_notes = 0
while (option_count < argc) and (sys.argv[option_count].startswith("--")):
    option = sys.argv[option_count]
    option_count += 1;
    if option == "--":
        break
    if option == "--keys":
        filter_for_keys = 1
    if option == "--notes":
        just_notes = 1
        filter_for_keys = 1

def filter_key_events(events):
    return filter(lambda x: x[1] in [8,9], events)

def process_note_events(events):
    start_times = {}
    notes = []
    for event in events:
        t, cmd, ch, key, velocity = event
        if cmd == 9: #on
            idx = tuple([ch, key])
            if not idx in start_times:
                start_times[idx] = t
        elif cmd == 8: #off
            idx = tuple([ch, key])
            if idx in start_times:
                t0 = start_times.pop(idx)
                notes.append(tuple([t0, t, ch, key]))
            else:
                print("Missing", idx)
    return notes

for name in sys.argv[option_count:]:
    with open(name, "rb") as f:
        midi = read_midi(f)
        if filter_for_keys:
            midi = map(filter_key_events, midi)
        if just_notes:
            events = []
            for track in midi:
                events.extend(track)
            events.sort()
            events = process_note_events(events)
            for line in events:
                print(", ".join(map(lambda x:str(x), line)))
        else:
            track_nbr = 0
            for track in midi:
                for line in track:
                    print(", ".join([str(track_nbr)]+map(lambda x:str(x), line)))
                track_nbr += 1
                
