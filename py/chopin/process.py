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

#with open("opus10no5star.events","r") as hfile:
with open("melodie.events","r") as hfile:
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
    if not i in bars:
        continue
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
                1440:"   2+⅔·4+8",
                1920:"       2+2"}

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


with open("triplet-movements","w") as hfile:

# get 16th triplet movements

    indices = [i for i in range(len(start)) if \
               stop[i]-start[i] == quarter/6  and \
               start[i]%(quarter/6) == 0   and \
               (channel[i]==1  or \
                64*quarter <= start[i] < 65*quarter)] # written in bass system

    for section_length in [quarter/2]:#[quarter/2, quarter, quarter*2, quarter*4]:

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


        print("\n\n\n",file=hfile)
        print("section length=            ",
              section_length,file=hfile)
        print("note count    =            ",
              section_length/(quarter/6),file=hfile)    
        print("#different occurances=     ",
              len(occurance_count),file=hfile)
        print("#of sections reoccuring=   ",
              sum(map(lambda x:occurance_count[x]-1,\
                                                  occurance_count)),file=hfile)
        print("    ...time span       =   ",
              section_length*sum(map(lambda x:occurance_count[x]-1,\
                                                  occurance_count)),file=hfile)

        print("#different occurances%12=  ",
              len(occurance_count12),file=hfile)
        print("#of sections reoccuring%12=",
              sum(map(lambda x:occurance_count12[x]-1,\
                                                  occurance_count12)),file=hfile)
        print("    ...time span       =   ",
              section_length*sum(map(lambda x:occurance_count12[x]-1,\
                                                  occurance_count12)),file=hfile)
        print("",file=hfile)
        print("#different schemes=        ",
              len(occurance_counts),file=hfile)
        print("#of sections reoccuring=   ",
              sum(map(lambda x:occurance_counts[x]-1,\
                                                  occurance_counts)),file=hfile)
        print("    ...time span       =   ",
              section_length*sum(map(lambda x:occurance_counts[x]-1,\
                                                  occurance_counts)),file=hfile)

        print("#different schemes%12=     ",
              len(occurance_count12s),file=hfile)
        print("#of sections reoccuring%12=",
              sum(map(lambda x:occurance_count12s[x]-1,\
                                                  occurance_count12s)),file=hfile)
        print("    ...time span       =   ",
              section_length*sum(map(lambda x:occurance_count12s[x]-1,\
                                                  occurance_count12s)),file=hfile)

        print("",file=hfile)



        for k in range(max(trip_schemes)+1):
            if not k in trip_schemes:
                print(".",end="",file=hfile)
            else:
                as_tuple = tuple(map(lambda x: (x[0] % (section_length),x[1]), \
                                 trip_schemes[k]))
                print(chr(65+keys.index(as_tuple)),end="",file=hfile)

            if k%2==1:
                print(" ",end="",file=hfile)       
            if k%4==3:
                print(" ",end="",file=hfile)
            if k%32==31:
                print("",file=hfile)

        eightnotes = {}

        with open("triplet-reduction","w") as hfile2:

            for k in range(max(triplets)+1):
                if k%2 ==0:
                    print("",file=hfile2)
                if k%4==0:
                    print("% TAKT ",(k/4)+1,file=hfile2)
                if not k in triplets:
                    print("  r",file=hfile2)
                else:
                    ps = set(map(lambda x: x[1],triplets[k]))
                    ps = list(ps)
                    ps.sort()
                    print("  <"+" ".join(map(lambda z: ly_pitch(z), ps))+">",file=hfile2)
                    eightnotes[k] = ps

        barnotes = {}
        for k in range(max(triplets)+1):
            if not k in triplets:
                continue
            array = barnotes.setdefault(k/4, [])
            array.extend(map(lambda x: x[1],triplets[k]))

        for k in barnotes:
            x =list ( set( barnotes[k]) )
            x.sort()
            barnotes[k] = x

        eightvars = {}
        occurs = {}
        varize = []

        for k in eightnotes:
            barstuff = barnotes[k/4]
            eightvars[k] = map(lambda x:barstuff.index(x),eightnotes[k])
            tup = tuple(eightvars[k])
            occurs[tup] = occurs.setdefault(tup,0)+1
            if not tup in varize:
                varize.append(tup)

        stackering = ""

        stackednbrs = []
        stackindexes = []

        for k in range(max(eightvars)+1):
            if k in eightvars:
                print("%3d"%varize.index(tuple(eightvars[k])),end="")
                stackering += "%3d"%varize.index(tuple(eightvars[k]))
            else:
                print(" xx",end="")
                stackering += " xx"
            if k%4==3:
                if not stackering in stackednbrs:
                    stackednbrs.append(stackering)
                stackindexes.append(stackednbrs.index(stackering))
                stackering = ""
                
                print(" ",end="")
            if k%8==7:
                print(" ",end="")
                
            if k%16==15:
                
                print("")



        bar4notes = {}
            
                    
        with open("bar-reduction","w") as hfile2:
            for k in range(max(barnotes)+1):
                if k%4 == 0:
                    print(" % TAKT ",k+1,file=hfile2)
                if not k in barnotes:
                    print("  r",file=hfile2)
                else:
                    sumarray = bar4notes.setdefault(k/4,[])
                    sumarray.append(barnotes[k][0])
                    
                    print("  <"+" ".join(map(lambda z: ly_pitch(z), barnotes[k]))+">",file=hfile2)

                    
        with open("bar-reduction-b","w") as hfile2:
            for k in range(max(bar4notes)+1):
                if k%4 == 0:
                    print(" % TAKT ",k+1,file=hfile2)
                if not k in bar4notes:
                    print("  r",file=hfile2)
                else:
                    pitchmod = list(set(bar4notes[k]))#list(set(map(lambda x: ((x+6)%12)-6 + 72, bar4notes[k])))
                    pitchmod.sort()
                    
                    #print("  <"+" ".join(map(lambda z: ly_pitch(z), pitchmod))+">",file=hfile2)
                    print("  <"+" ".join(map(lambda z: ly_pitch(z), pitchmod))+">",file=hfile2)
                    


        print("\n\n\nwhere: ",file=hfile)

        for k in keys:
            code = ""
            last = -1
            for x in k:
                if x[0]!=last:
                    code += " "+str(x[1])
                    last = x[0]
                else:
                    code += str(x[1])

            print(chr(65+keys.index(k)),"=",code, end="   ",file=hfile)
            if keys.index(k) % 6 == 5:
                print("",file=hfile)




#eof

    


