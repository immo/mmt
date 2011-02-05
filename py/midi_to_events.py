#!/usr/bin/env python
# coding:utf-8
from __future__ import print_function
import sys

def read_midi(f):
    header = f.read(14)
    if header[0:8] != "MThd\x00\x00\x00\x06":
        return None
    format = (ord(header[8])<<8)+ord(header[9]);
    tracks = (ord(header[10])<<8)+ord(header[11]);
    ticks_per_quarter = ord(header[12])<<8+ord(header[13])
    if not format in [0,1]:
        return None

    return format,tracks,ticks_per_quarter

for name in sys.argv[1:]:
    with open(name, "rb") as f:
        midi = read_midi(f)
        print("midi=",midi)
