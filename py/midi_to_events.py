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

    def read(self,count=1):
        if count == 1:
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
    data_length = {8: 1,\
                   9: 1,\
                   0xA: 1,\
                   0xB: 1,\
                   0xC: 1,\
                   0xD: 1,\
                   0xE: 1,\
                   0xF: 1}
    current_command = 8
    current_channel = 0
    while not stream.eof():
        delta_time += stream.rbeWord()
        b = stream.read()
        if b&0x80:
            current_command = b>>4
            current_channel = b&0xF
        else:
            more_data = data_length[current_command]-1
            if more_data == 1:
                data = [b,stream.read()]
            else:
                data = [b] + stream.read(more_data)
            event = tuple([current_channel, current_command]+data)
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
    events = []
    for i in range(tracks):
        track_header = f.read(8)
        if track_header[0:4] != 'MTrk':
            break
        
        data_length = (ord(track_header[4])<<24)+(ord(track_header[5])<<16)\
                      +(ord(track_header[6])<<8)+ord(track_header[7])
        data = readable_stream(map(lambda x:ord(x), f.read(data_length)))
        print(extract_midi_events(data)[:20])
        
        
    return events
    

for name in sys.argv[1:]:
    with open(name, "rb") as f:
        midi = read_midi(f)
        print("midi=",midi)
