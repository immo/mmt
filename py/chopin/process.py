#!/usr/bin/env python
# coding:utf-8


from __future__ import print_function
import sys

quarter = 480 #length of quarter beats
bar = quarter*2 #length of bar

start = []
stop = []
channel = []
key = []

with open("opus10no5star.events","r") as hfile:
    for line in hfile:
        data = line.strip().split(",")
        # start(delta), stop(delta), channel, key, start(beat), length(beat)
        start.append(int(data[0]))
        stop.append(int(data[1]))
        channel.append(int(data[2]))
        key.append(int(data[3]))

bars = {}

nbrbars = 0

for i in range(len(start)):
    nbr = (start[i]/bar)+1
    nbrbars = max(nbrbars,nbr)
    bararray = bars.setdefault(nbr,[])
    note = {'o':start[i]%bar, 'p':key[i], 'c':channel[i], 'd':stop[i]-start[i]}
    bararray.append(note)

print(filter(lambda x:x['o']==0, bars[1]))

lily_header = r"""%GERMAN PITCHES!
\version "2.10.15"
\include "deutsch.ly"

"""

upper_template = r"""upper = {
   \clef treble
   \key ges \major \time 2/4
   %s
   \bar "|."
}"""

lower_template = r"""lower = {
   \clef bass
   \key ges \major \time 2/4
   %s
   \bar "|."
}"""

lily_footer = r"""%Score code
\score {
        \new PianoStaff <<
           \set PianoStaff.instrumentName = "Reduction"
           \new Staff = "upper" \upper
           \new Staff = "lower" \lower
        >>
        \layout { }
        \midi { }
     }
"""

pitchnames = ["c","des","d","es","fes","f","ges","g","as","a","b","ces"]
octavenames = [",,,,",",,,",",,",",","","'","''","'''","''''","'''''"]

def ly_pitch(p): #90=ges''' -> 84=c'''
    return pitchnames[p%12]+octavenames[p/12]

#get time schemes barwise

structure_scheme = {}

for i in range(1,nbrbars+1):
    for hand in [1,2]:
        handwise = filter(lambda x:x['c']==hand, bars[i])
        structure = frozenset(map(lambda x:(x['o'], x['o']+x['d']),handwise))
        array = structure_scheme.setdefault(structure,[])
        array.append((i,hand))

lengthnames = {1:"4",2:"8",0.5="2",0.25="1",6:

def lily_length(d):
    return lengthnames[float(quarter)/float(d)]


def lily_rhythm(scheme):
    if len(scheme) > 1:
    else:
        x = scheme[0]
        if x[0] != 0:
            
