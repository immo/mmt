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

#get time schemes 

structure_scheme = {}

for i in range(1,nbrbars+1):
    for p in [0,1]:
        partwise = filter(lambda x:p*quarter <= x['o'] < (p+1)*quarter, bars[i])
        structure = frozenset(map(lambda x:(x['o']-p*quarter, x['d']),partwise))
        array = structure_scheme.setdefault(structure,[])
        array.append((i,p+1))

readable_lens =  { 0:"         0",
                  40:"      ⅔·32",
                  80:"      ⅔·16",
                 120:"   ⅔·16+32",
                 160:"       ⅔·8",
                 240:"         8",
                 320:"       ⅔·4",
                 360:"    ⅔·4+32",
                 400:"    ⅔·4+16",
                 480:"         4",
                 560:"    4+⅔·16",
                 640:"     4+⅔·8",
                 720:"    ⅔·2+16",
                 800:"     ⅔·2+8",
                 840:"  ⅔·2+8+32",
                 880:"  ⅔·2+8+16",
                 960:"         2",
                1040:"    2+⅔·16",
                1440:"   2+⅔·4+8"}

def turn(a):
    b = []
    c = []
    for x in a:
        b.append(x[0])
        c.append(x[1])
    return [tuple(b),tuple(c)]

formatted_data = {}

needed_lens = set([])
                 
for s in structure_scheme:
    form_line = "\n\n**** " + str(len(structure_scheme[s])) + \
                " = " + str(structure_scheme[s]) + "\n"
    x = list(s)
    x.sort()
    x = turn(x);
    form_line += "".join(map(lambda z:readable_lens[z],x[0])) + "\n"
    form_line += "".join(map(lambda z:readable_lens[z],x[1])) + "\n"
    needed_lens = needed_lens.union(x[0]).union(x[1])
    tupling = tuple([len(structure_scheme[s])]+structure_scheme[s])
    formatted_data[tupling] = form_line

keys = formatted_data.keys()
keys.sort()


with open("half-bar-rhythm-schemes","w") as hfile:
    for k in keys:
        hfile.write(formatted_data[k])
    

# get 16th triplet movements

indices = [i for i in range(len(start)) if \
           stop[i]-start[i] == quarter/6  and \
           start[i]%(quarter/6) == 0   and \
           (channel[i]==1  or \
            64*quarter <= start[i] < 65*quarter)] # written in bass system

for section_length in [quarter/2, quarter, quarter*2, quarter*4]:

    triplets = {}

    trip_schemes = {}
    trip_schemes12 = {}    

    for i in indices:
        array = triplets.setdefault((start[i]/(section_length)),[])
        array.append((start[i], key[i]))

    triplets2 = {}
    for k in triplets:
        if len(triplets[k])>=3:
            triplets2[k] = triplets[k]

    triplets = triplets2

    occurance_count = {}
    occurance_count12 = {}    

    occurance_counts = {}
    occurance_count12s = {}

    keys = []

    for k in triplets:
        triplets[k].sort()

        given = {}


        pitches = list(set(map(lambda x:x[1], triplets[k])))
        pitches.sort()
        for i in range(len(pitches)):
            given[pitches[i]] = i+1

        for x in triplets[k]:
            array = trip_schemes.setdefault(k,[])
            array.append((x[0],given[x[1]]))



        given = {}
        current = 1
        

        for x in triplets[k]:
            x12 = x[1]%12
            if not x12 in given:
                given[x12] = current
                current += 1
            array = trip_schemes12.setdefault(k,[])
            array.append((x[0],given[x12]))

        
        as_tuple = tuple(map(lambda x: (x[0] % (section_length),x[1]), \
                             triplets[k]))
        occurance_count[as_tuple] = occurance_count.setdefault(as_tuple,0)+1
        as_tuple = tuple(map(lambda x: (x[0] % (section_length),x[1]%12), \
                             triplets[k]))
        occurance_count12[as_tuple] = occurance_count12.setdefault(as_tuple,0)+1

        as_tuple = tuple(map(lambda x: (x[0] % (section_length),x[1]), \
                             trip_schemes[k]))
        occurance_counts[as_tuple] = occurance_counts.setdefault(as_tuple,0)+1

        if not as_tuple in keys:
            keys.append(as_tuple)
        
        as_tuple = tuple(map(lambda x: (x[0] % (section_length),x[1]), \
                             trip_schemes12[k]))
        occurance_count12s[as_tuple] = occurance_count12s.setdefault(as_tuple,0)+1
        
        
    print("\n\n\n")
    print("section length=            ",
          section_length)
    print("note count    =            ",
          section_length/(quarter/6))    
    print("#different occurances=     ",
          len(occurance_count))
    print("#of sections reoccuring=   ",
          sum(map(lambda x:occurance_count[x]-1,\
                                              occurance_count)))
    print("    ...time span       =   ",
          section_length*sum(map(lambda x:occurance_count[x]-1,\
                                              occurance_count)))
    
    print("#different occurances%12=  ",
          len(occurance_count12))
    print("#of sections reoccuring%12=",
          sum(map(lambda x:occurance_count12[x]-1,\
                                              occurance_count12)))
    print("    ...time span       =   ",
          section_length*sum(map(lambda x:occurance_count12[x]-1,\
                                              occurance_count12)))
    print("")
    print("#different schemes=        ",
          len(occurance_counts))
    print("#of sections reoccuring=   ",
          sum(map(lambda x:occurance_counts[x]-1,\
                                              occurance_counts)))
    print("    ...time span       =   ",
          section_length*sum(map(lambda x:occurance_counts[x]-1,\
                                              occurance_counts)))
    
    print("#different schemes%12=     ",
          len(occurance_count12s))
    print("#of sections reoccuring%12=",
          sum(map(lambda x:occurance_count12s[x]-1,\
                                              occurance_count12s)))
    print("    ...time span       =   ",
          section_length*sum(map(lambda x:occurance_count12s[x]-1,\
                                              occurance_count12s)))

    print("")
    
    

    for k in range(max(trip_schemes)+1):
        if not k in trip_schemes:
            print(" ",end="")
        else:
            as_tuple = tuple(map(lambda x: (x[0] % (section_length),x[1]), \
                             trip_schemes[k]))
            print(chr(65+keys.index(as_tuple)),end="")
            
        if k%2==1:
            print(" ",end="")       
        if k%4==3:
            print(" ",end="")
        if k%32==31:
            print("")

    print("\n\n\nwhere: ")
    
    for k in keys:
        code = ""
        last = -1
        for x in k:
            if x[0]!=last:
                code += " "+str(x[1])
                last = x[0]
            else:
                code += str(x[1])
            
        print(chr(65+keys.index(k)),"=",code, end="   ")
        if keys.index(k) % 6 == 5:
            print("")



#eof

    


