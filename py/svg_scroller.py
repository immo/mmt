#!/usr/bin/env python
# coding:utf-8
# python version=2.7

from __future__ import print_function
import sys
import rsvg
import cairo
import libxml2 as xml
import math

#flipped video format :)

v_aspect = 16/9.#4/3.#
h_aspect = 9/16.#3/4.#

video_w = 1080#720#
video_h = int(video_w*v_aspect)#video_w*16/9
rotate90 = 1
line_stroke_width = 1.33#1.2
speed_factor = 0.721875
#1410 = die goetter, 1920=lichtcoro
midi_beats_per_minute = 172 #die_goetter v2=144, lichtcoro=172
midi_frames_per_second = 480*midi_beats_per_minute/60 #int(1920*speed_factor)
movie_frames_per_second = 30
left_window_times = 0#midi_frames_per_second/2
right_window_times = 0#midi_frames_per_second
pre_glow_frames = movie_frames_per_second/10
after_glow_frames = movie_frames_per_second/4

time_performance = [388, 1199, 1810, 2459, 3180, 3911, 4601, 5281, 5907, 6595, #[0, ...
7221, 7911, 8553, 9220, 9928, 10603, 11275, 11969, 12681, 13311, 13985, 14620,
15293, 15972, 16651, 17264, 17934, 18581, 19246, 19885, 20601, 21246, 22005,
22662, 23350, 23981, 24656, 25337, 26027, 26692, 27363, 28039, 28679, 29316,
29966, 30612, 31369, 32064, 32744, 33424, 34116, 34761, 35400, 36097, 36787,
37577, 38312, 39015, 39698, 40425, 41125, 41823, 42512, 43224, 43895, 44611,
45315, 46013, 46742, 47421, 48123, 48832, 49511, 50272, 50938, 51599, 52293,
53033, 53692, 54360, 55064, 55731, 56387, 57127, 57799, 58528, 59244, 59922,
60582, 61278, 61966, 62647, 63315, 64014, 64720, 65398, 66087, 66793, 67455,
68118, 68849, 69516, 70203, 70869, 71581, 72225, 72907, 73593, 74264, 74947,
75657, 76351, 77054, 77720, 78432, 79133, 79794, 80459, 81184, 81847, 82541,
83245, 83946, 84648, 85326, 86021, 86709, 87407, 88110, 88803, 89517, 90202,
90991, 91748, 92473, 93194, 93918, 94648, 95409, 96169, 96924, 97625, 98299,
98984, 99714, 100431, 101123, 101842, 102554, 103218, 103934, 104616, 105348,
106013, 106694, 107348, 107997, 108663, 109272, 109943, 110576, 111267, 111952,
112621, 113300, 113988, 114606, 115328, 116027, 116759, 117456, 118134, 118779,
119456, 120088, 120745, 121392, 122088, 122754, 123430, 124058, 124779, 125451,
126116, 126793, 127483, 128169, 128871, 129574, 130222, 130895, 131601, 132269,
132939, 133591, 134277, 134963, 135635, 136310, 137020, 137722, 138393, 139098,
139780, 140460, 141138, 141833, 142515, 143199, 143845, 144566, 145271, 145980,
146665, 147379, 148040, 148763, 149430, 150072, 150718, 151366, 152073, 152744,
153427, 154078, 154776, 155417, 156084, 156763, 157432, 158114, 158767, 159441,
160085, 160731, 161403, 162060, 162727, 163435, 164072, 164748, 165454, 166173,
166868, 167679, 168387, 168995, 169654, 170326, 170993, 171670, 172351, 172988,
173654, 174358, 175022, 175694, 176353, 177018, 177718, 178398, 179082, 179720,
180392, 181093, 181769, 182431, 183157, 183839, 184519, 185180, 185834, 186445,
187126, 187798, 188451, 189242, 189911, 190539, 191215, 191856, 192540, 193234,
193933, 194644, 195338, 196061, 196722, 197413, 198069, 198720, 199397, 200070,
200743, 201434, 202121, 202778, 203438, 204088, 204769, 205474, 206142, 206786,
207455, 208166, 208811, 209499, 210156, 210792, 211463, 212129, 212763, 213435,
214111, 214755, 215380, 216058, 216722, 217374, 218031, 218723, 219307, 220018,
220653, 221319, 221901, 222565, 223242, 223901, 224585, 225233, 225943, 226676,
227386, 228043, 228713, 229395, 230076, 230741, 231409, 232138, 232819, 233491,
234118, 234822, 235468, 236101, 236834, 237548, 238219, 238878, 239535, 240250,
240962, 241636, 242303, 242981, 243620, 244273, 244909, 245636, 246302, 246960,
247611, 248275, 248947, 249613, 250283, 250979, 251659, 252331, 253001, 253707,
254342, 255036, 255684, 256351, 257040, 257783, 258424, 259075, 259748, 260440,
261128, 261800, 262458, 263142, 263836, 264541, 265187, 265836, 266516, 267227,
267908, 268575, 269259, 269954, 270623, 271271, 271997, 272674, 273316, 273968,
274693, 275405, 276037, 276715, 277442, 278175, 278855, 279540, 280194, 280852,
281504, 282186, 282918, 283584, 284259, 284946, 285603, 286264, 286953, 287642,
288345, 289078, 289738, 290404, 291062, 291727, 292440, 293250, 293924, 294642,
295318, 296054, 296708, 297466, 298158, 298858, 299622, 300424, 301150, 301907,
302729, 303396, 304047, 304739, 305461, 306199, 306861, 307699, 308508, 309343,
310086, 310792, 311574]


beats_at_ms = [(i*2*480,time_performance[i]*1024/1000) for i in range(len(time_performance))]
first_beat_ms = min( map(lambda x:x[0],beats_at_ms) )
last_beat_ms = max( map(lambda x:x[0],beats_at_ms) )
beats_at_ms.sort()

def midi_beats_to_frames(tmidi):
    if tmidi < first_beat_ms:
        b0 = beats_at_ms[0][0]
        f0 = beats_at_ms[0][1]
        return (tmidi-b0)*movie_frames_per_second/midi_frames_per_second\
               + f0*movie_frames_per_second/1000
    if tmidi > last_beat_ms:
        b0 = beats_at_ms[-1][0]
        f0 = beats_at_ms[-1][1]
        return (tmidi-b0)*movie_frames_per_second/midi_frames_per_second\
               + f0*movie_frames_per_second/1000
    before = filter(lambda x:x[0]<=tmidi, beats_at_ms)[-1]
    after = filter(lambda x:x[0]>=tmidi, beats_at_ms)[0]
    if before == after:
        return before[1]*movie_frames_per_second/1000

    b0 = before[0]
    f0 = before[1]
    b1 = after[0]
    f1 = after[1]

    f = f0 + ((tmidi-b0)*(f1-f0))/(b1-b0)

    return f*movie_frames_per_second/1000
        
    return (t*movie_frames_per_second)/midi_frames_per_second

def i2str(i):
    s = str(i)
    if (len(s) < 8):
        return "0"*(8-len(s)) + s
    else:
        return s

def hex2(i):
    return hex(i>>4)[2]+hex(i&0xF)[2]

args = sys.argv[1:]
if len(args) != 3:
    print("Usage: svg_scroller.py [SVG-File] [POINTS-File] [Output-Prefix]");
    sys.exit(0)

arg_svg, arg_points, arg_out = args

print("Loading SVG and parsing XML data...")
svg_file = open(arg_svg).read()
svg_xml = xml.parseDoc(svg_file)
print("Backing up original SVG...")
svg_xml_original = svg_xml.copyDoc(True)

xml_count = 0
node_count = 0
edge_count = 0

svg_xml_edges = []
svg_xml_nodes = []

for node in svg_xml_original.xpathEval("//*"):
    xml_count += 1
    node_class = node.hasProp("class")
    if node_class is None:
        pass
    elif node_class.content == "node":
        svg_xml_nodes.append(node)
        node_count += 1
    elif node_class.content == "edge":
        svg_xml_edges.append(node)
        edge_count += 1
print("SVG XML-nodes:",xml_count,"  (",node_count,"/",edge_count,")")

def remove_all_children(node):
    child = node.children
    while child:
        child.unlinkNode()        
        child = node.children

def get_bounding_box(node):
    child = node.children
    box = None
    while child:
        cx = child.hasProp("cx")
        cy = child.hasProp("cy")
        rx = child.hasProp("rx")
        ry = child.hasProp("ry")
        if cx and cy and rx and ry:
            cx = float(cx.content)
            cy = float(cy.content)
            rx = float(rx.content)
            ry = float(ry.content)
            if box:
                box = (min(cx-rx,box[0]),min(cy-ry,box[1]),
                       max(cx+rx,box[2]),max(cy+ry,box[3]))
            else:
                box = (cx-rx,cy-ry,cx+rx,cy+ry)
        points = child.hasProp("points")
        if points:
            pts = points.content.strip().split(" ")
            xs = [float(p.split(",")[0]) for p in pts]
            ys = [float(p.split(",")[1]) for p in pts]
            if box:
                xs.append(box[0])
                xs.append(box[2])
                ys.append(box[1])
                ys.append(box[3])
            box = (min(xs),min(ys),max(xs),max(ys))
        path_d = child.hasProp("d")
        if path_d:
            d = path_d.content.replace("M"," ").replace("C"," ")\
                .replace("L"," ").replace("S"," ").strip()
            pts = d.split(" ")
            xs = [float(p.split(",")[0]) for p in pts]
            ys = [float(p.split(",")[1]) for p in pts]
            if box:
                xs.append(box[0])
                xs.append(box[2])
                ys.append(box[1])
                ys.append(box[3])
            box = (min(xs),min(ys),max(xs),max(ys))
        child = child.next
    return transform_box(box)

def get_titles(node):
    titles = []
    child = node.children
    while child:
        if child.name == "title":
            titles.extend(map(lambda x:x.strip(),child.content.split("->")))
        child = child.next
    return titles

def box_intersection(b1,b2):
    return (b1[0]<=b2[2]) and (b1[1]<=b2[3])\
       and (b1[2]>=b2[0]) and (b1[3]>=b2[1])
        
print("Checking SVG elements...")
svg_node = svg_xml.xpathEval("/*")[0]
svg_viewbox = eval("("+svg_node.hasProp("viewBox").content.replace(" ",",")+")")

svg_graphical_elements = svg_xml.xpathEval("/*/*/*")

svg_transform = svg_xml.xpathEval("/*/*")[0].hasProp("transform").content
translate_idx = svg_transform.find("translate(")+len("translate(")
svg_transform = eval("("+svg_transform[translate_idx:].replace(" ",","))

print("Loading XML data into RSVG...")
svg = rsvg.Handle(data=str(svg_xml))

svg_width = svg.props.width
svg_height = svg.props.height
print("SVG size by RSVG:  ",svg_width,"x",svg_height,"pixels");

def transform_point(pt):
    return (float((pt[0]+svg_transform[0])-svg_viewbox[0])\
           /float(svg_viewbox[2]-svg_viewbox[0])*float(svg_width),
           float((pt[1]+svg_transform[1])-svg_viewbox[1])\
           /float(svg_viewbox[3]-svg_viewbox[1])*float(svg_height))

def transform_box(box):
    a = transform_point((box[0],box[1]))
    b = transform_point((box[2],box[3]))
    return (a[0],a[1],b[0],b[1])


#surface = cairo.ImageSurface(cairo.FORMAT_ARGB32, svg_width, svg_height)
#context = cairo.Context(surface)
#svg.render_cairo(context)
#surface.write_to_png(arg_out+"complete.png")

points = eval(open(arg_points).read())
times = map(lambda x:x[0], points.values())+map(lambda x:x[1], points.values())
times = list(set(times))
times.sort()

print("Start/end point times range:",times[0],"to",times[-1])
times.append(times[-1]+midi_frames_per_second) #add another second

times_frames = {}
times_frames_up_to = {}
t_before = 0
for t in times: #should we check midi for BPM and tempo events??
    times_frames[t] = midi_beats_to_frames(t)
    times_frames_up_to[t_before] = times_frames[t]
    t_before = t

points_frames = {}
for p in points:
    points_frames[p] = tuple(map(lambda x:times_frames[x],points[p]))

def get_relevant_points(t):
    fltr = lambda x,t=t:points[x][0]<=t<points[x][1]
    return filter(fltr,points)

def get_relevant_window_points(t):
    fltr = lambda x,t=t:points[x][0]-left_window_times<=\
           t<points[x][1]+right_window_times
    return filter(fltr,points)


print("Grabbing node positions...")

times_nodes = {}
for t in times:
    times_nodes[t] = get_relevant_points(t)

times_nodes_window = {}
for t in times:
    times_nodes_window[t] = get_relevant_window_points(t)


points_in_svg = {}
nodes_in_svg = {}

for el in svg_graphical_elements:
    nodename = el.getContent().strip()
    if nodename in points:
        ellipse_node = svg_xml.xpathEval(el.nodePath()+"/*[@cx]")[0]
        nodes_in_svg[nodename] = ellipse_node.nodePath()
        cx = float(ellipse_node.hasProp("cx").content)
        cy = float(ellipse_node.hasProp("cy").content)
        
        #px = float(cx+svg_transform[0]-svg_viewbox[0])/float(svg_viewbox[2]-\
        #svg_viewbox[0])*float(svg_width)
        #py = float(cy+svg_transform[1]-svg_viewbox[1])/float(svg_viewbox[3]-\
        #svg_viewbox[1])*float(svg_height)
        points_in_svg[nodename] = transform_point((cx,cy))

print("Calculating bounding windows...")

times_points = {}
for t in times:
    times_points[t] = map(lambda x: points_in_svg[x], times_nodes_window[t])

min_width = 400

times_window = {}
times_next_window = {}
t_before = 0
last_window = [0,0,0,0]
for t in times:
    try:
        val = [min(map(lambda x:x[0],times_points[t]))*0.95,
                       min(map(lambda x:x[1],times_points[t]))*0.95,
                       max(map(lambda x:x[0],times_points[t]))*1.05,
                       max(map(lambda x:x[1],times_points[t]))*1.05]
        if val[2]-val[0] < min_width:
            mid = val[0]+val[2]
            val[0] = (mid-min_width)/2
            val[2] = (mid+min_width)/2
        other_x = (val[3]-val[1])*video_w/video_h
        other_y = (val[2]-val[0])*video_h/video_w
        if other_x > (val[2]-val[0]):
            xl = (val[2]+val[0]-other_x)/2
            xr = (val[2]+val[0]+other_x)/2
            val[0] = xl
            val[2] = xr
        else:
            yt = (val[3]+val[1]-other_y)/2
            yb = (val[3]+val[1]+other_y)/2
            val[1] = yt
            val[3] = yb
        
    except Exception,err:
        val = last_window
        print(err)
    times_window[t] = val
    times_next_window[t_before] = times_window[t]
    t_before = t
    last_window = val
        


wins = times_window.keys()
wins.sort()

surface = cairo.ImageSurface(cairo.FORMAT_ARGB32, video_w, video_h)
if rotate90:
    surface2 = cairo.ImageSurface(cairo.FORMAT_ARGB32, video_h, video_w)

def make_brighter(color_value):
    v = str(color_value)
    if v.startswith("#"):
        r = int(v[1:3],16);
        g = int(v[3:5],16);
        b = int(v[5:7],16);
        r = (r|127)>>4
        g = (g|127)>>3
        b = 0xFF
        return "#"+hex2(r)+hex2(g)+hex2(b)
    else:
        print("!",v)
        return "#3333FF"

def make_greyer(color_value,how_much_grey):
    v = str(color_value)
    how_much_color = 1.0 - how_much_grey
    if v.startswith("#"):
        r = int(v[1:3],16);
        g = int(v[3:5],16);
        b = int(v[5:7],16);
        w = (r+g+b)/6
        r = int(float(r)*how_much_color+float(w)*how_much_grey)
        g = int(float(g)*how_much_color+float(w)*how_much_grey)
        b = int(float(b)*how_much_color+float(w)*how_much_grey)        
        return "#"+hex2(r)+hex2(g)+hex2(b)
    elif v=="none":
        return "none"
    else:
        print("!",v)
        return "#333333"

def grey_node(node, how_much_grey):
    child = node.children
    while child:
        for cprop in ["fill","stroke"]:
            oldcolor = child.hasProp(cprop)
            if oldcolor:
                child.setProp(cprop,make_greyer(oldcolor.content,how_much_grey))
        child = child.next


marked_nodes = []
marked_nodes_style = [("fill",make_brighter),("stroke",make_brighter)]


svg_xml_main_graph = svg_xml.xpathEval("//*[@id='graph1']")[0]
remove_all_children(svg_xml_main_graph)

xml_str = str(svg_xml)


print("Calculating bounding boxes...")
svg_xml_edges_bbox = {}
for node in svg_xml_edges:
    svg_xml_edges_bbox[node] = get_bounding_box(node)
svg_xml_nodes_bbox = {}
for node in svg_xml_nodes:
    svg_xml_nodes_bbox[node] = get_bounding_box(node)
print("Grabbing titles...")
svg_xml_edges_titles = {}
for node in svg_xml_edges:
    svg_xml_edges_titles[node] = get_titles(node)
svg_xml_nodes_titles = {}
for node in svg_xml_nodes:
    svg_xml_nodes_titles[node] = get_titles(node)
    
svg = None

outfile = open('/tmp/times_window','wb')
outfile.write(r"#!/usr/bin/python"+"\n")
outfile.write(r"#coding:utf-8"+"\n")
outfile.write("times_window = "+repr(times_window)+"\n")

def make_smooth_frames_wins():
    times = times_window.keys()
    times.sort()
    #frames = map(lambda x: int(x*float(movie_frames_per_second)/float(midi_frames_per_second)),times)
    frames = map(midi_beats_to_frames,times)

    plotf = []
    plotw = []

    for i in xrange(0,len(times)):
        t0 = times[i]
        if i+1<len(times):
            t1 = times[i+1]
        else:
            t1 = t0
        win0 = times_window[t0]
        win1 = times_window[t1]
        f0 = frames[i]
        if i+1<len(frames):
            f1 = frames[i+1]
        else:
            f1 = f0+1

        flen = float(f1-f0)

        for f in xrange(f0,f1):
            alpha = (f-f0)/flen
            f_win = [0,0,0,0]
            for idx in range(4):
                f_win[idx] = win0[idx]*(1.0-alpha) + win1[idx]*alpha
            plotf.append(f)
            plotw.append(f_win)


    min_x = min(map(lambda x:x[0],plotw))
    max_x = max(map(lambda x:x[2],plotw))
    min_y = min(map(lambda x:x[1],plotw))
    max_y = max(map(lambda x:x[3],plotw))

    ctr_x = (min_x+max_x)/2
    ctr_y = (min_y+max_y)/2
    wdh_x = max_x-min_x
    wdh_y = max_y-min_y
    wdt_y = int(wdh_x*v_aspect)
    wdt_x = int(wdh_y*h_aspect)

    if wdt_y > wdh_y:
        print("More height")
        ifwin = [min_x,ctr_y-wdt_y/2,max_x,ctr_y+wdt_y/2]
    elif wdt_x >= wdh_x:
        print("More width")
        ifwin = [ctr_x-wdt_x/2,min_y,ctr_x+wdt_x/2,max_y]

    print("Initial (still) window = ",ifwin)
    print("Ratio = ",(ifwin[2]-ifwin[0])/(ifwin[3]-ifwin[1]))

    second_d_bound = [0.1]*4

    steptime = 6.0

    smoothf = [int(i*steptime*movie_frames_per_second)
               for i in range(0,int(math.ceil(plotf[-1]/(steptime*float(movie_frames_per_second))))+1)]

    smoothw = []
    for idx in range(len(smoothf)):
        if idx*movie_frames_per_second*steptime < len(plotw):
            smoothw.append(plotw[smoothf[idx]])
        else:
            smoothw.append(plotw[-1])


    move_out = []

    for i in range(len(smoothf)-1):
        f0 = smoothf[i]
        f1 = min(smoothf[i+1],len(plotf)-1)
        move = [0.0,0.0,0.0,0.0]
        flen = float(f1-f0)
        win0 = smoothw[i]
        win1 = smoothw[i+1]
        for f in range(f0,f1):
            alpha = (f-f0)/flen
            f_win = [0,0,0,0]
            for idx in range(4):
                f_win[idx] = win0[idx]*(1.0-alpha) + win1[idx]*alpha
                if idx<2:
                    if f_win[idx] > plotw[f][idx]:
                        move[idx] = min(move[idx],-f_win[idx]+plotw[f][idx])
                else:
                    if f_win[idx] < plotw[f][idx]:
                        move[idx] = max(move[idx],-f_win[idx]+plotw[f][idx])
        move_out.append(move)

    move_out.append(move)
    move_out.append(move)

    for i in range(1,len(smoothf)):
        for idx in range(4):
            if idx<2:
                m = min(move_out[i][idx],move_out[i+1][idx],move_out[i-1][idx])
            else:
                m = max(move_out[i][idx],move_out[i+1][idx],move_out[i-1][idx])
            smoothw[i][idx] += m

    for i in range(len(smoothw)):
        fix_aspect = smoothw[i]
        x = (fix_aspect[0]+fix_aspect[2])/2
        y = (fix_aspect[1]+fix_aspect[3])/2
        w = (fix_aspect[2]-fix_aspect[0])
        h = (fix_aspect[3]-fix_aspect[1])
        if int(w*v_aspect) > h:
            h = int(w*v_aspect)
        elif int(h*h_aspect) > w:
            w = int(h*h_aspect)
        smoothw[i] = [x-w/2,y-h/2,x+w/2,y+h/2]

    smooth_windows = []

    for i in range(len(smoothf)-1):
        f0 = smoothf[i]
        f1 = min(smoothf[i+1],len(plotf)-1)

        flen = float(f1-f0)
        win0 = smoothw[i]
        win1 = smoothw[i+1]
        for f in range(f0,f1):
            alpha = (f-f0)/flen
            f_win = [0,0,0,0]
            for idx in range(4):
                f_win[idx] = win0[idx]*(1.0-alpha) + win1[idx]*alpha
            smooth_windows.append(f_win)

    smooth_windows.append(f_win)
    smooth_windows.append(f_win)
    return smooth_windows

print("Make smooth frame windows....")
smooth_windows = make_smooth_frames_wins()
print("done.")

print("Win range: ",wins[0]," to ",wins[-1])

for t in wins:
    #if None:
    #    for n in marked_nodes:
    #        demark = svg_xml.xpathEval(n[0])[0]
    #        for props in n[1:]:
    #            demark.setProp(props[0],props[1])
    #
    #    for nodename in times_nodes[t]:
    #        try:
    #            nodepath = nodes_in_svg[nodename]
    #            entry = [nodepath]
    #            mark = svg_xml.xpathEval(nodepath)[0]
    #            for props in marked_nodes_style:
    #                old_val = (props[0], mark.hasProp(props[0]).content)
    #                entry.append(old_val)
    #                mark.setProp(props[0],props[1](old_val[1]))
    #            marked_nodes.append(entry)
    #        except:
    #            pass
            

    f = times_frames[t]
    try:
        f_last = times_frames_up_to[t]
    except:
        f_last = f+1;
    
    print("frames =",f,"..",f_last-1)
    win = times_window[t]
    print("window =",win)
    print("width =",win[2]-win[0],"height =",win[3]-win[1])
    print("scale =",( float(video_w)/(win[2]-win[0]), float(video_h)/(win[3]-win[1]) ))
    try:
        next_win = times_next_window[t]
    except:
        next_win = win
    print("next window =",win)

    f_count = float(f_last-f)

    colored_nodes = times_nodes[t]

    for fi in xrange(f,f_last):
        f_win = smooth_windows[fi]

        stroke_width = (f_win[2]-f_win[0])/float(video_w)*line_stroke_width
        
        for node in svg_xml_nodes:
            bbox = svg_xml_nodes_bbox[node]
            if box_intersection(bbox,f_win):
                nodetitle = svg_xml_nodes_titles[node][0]
                nodecopy = node.copyNode(1)
                if nodetitle in colored_nodes:
                    svg_xml_main_graph.addChild(nodecopy)
                else:
                    if (fi > points_frames[nodetitle][0]-pre_glow_frames) and (fi <= points_frames[nodetitle][0]):
                        greyness = -(fi-points_frames[nodetitle][0])/float(pre_glow_frames)
                        greyed = nodecopy
                        grey_node(greyed,greyness)
                        svg_xml_main_graph.addChild(greyed)
                    elif (fi < points_frames[nodetitle][1]+after_glow_frames) and (fi >= points_frames[nodetitle][1]):
                        greyness = (fi-points_frames[nodetitle][1])/float(after_glow_frames)
                        greyed = nodecopy
                        grey_node(greyed,greyness)
                        svg_xml_main_graph.addChild(greyed)
                    else:                       
                        greyed = nodecopy
                        grey_node(greyed,1.0)
                        svg_xml_main_graph.addChild(greyed)
                    
        for node in svg_xml_edges:
            bbox = svg_xml_edges_bbox[node]
            if box_intersection(bbox,f_win):
                nodetitle = svg_xml_edges_titles[node][0]
                nodecopy = node.copyNode(1)
                child = nodecopy.children
                while child:
                    if child.hasProp("d"):
                        child.setProp("stroke-width",str(stroke_width))

                    child = child.next


                if nodetitle in colored_nodes:
                    svg_xml_main_graph.addChild(nodecopy)
                else:
                    if (fi > points_frames[nodetitle][0]-pre_glow_frames) and (fi <= points_frames[nodetitle][0]):
                        greyness = -(fi-points_frames[nodetitle][0])/float(pre_glow_frames)
                        greyed = nodecopy
                        grey_node(greyed,greyness)
                        svg_xml_main_graph.addChild(greyed)
                    elif (fi < points_frames[nodetitle][1]+after_glow_frames) and (fi >= points_frames[nodetitle][1]):
                        greyness = (fi-points_frames[nodetitle][1])/float(after_glow_frames)
                        greyed = nodecopy
                        grey_node(greyed,greyness)
                        svg_xml_main_graph.addChild(greyed)
                    else:                       
                        greyed = nodecopy
                        grey_node(greyed,1.0)
                        svg_xml_main_graph.addChild(greyed)
        context = cairo.Context(surface)
        #clear background
        context.set_source_rgb(0,0,0)
        context.paint()
    
        if svg:
            context.scale( float(video_w)/(old_f_win[2]-old_f_win[0]),
                           float(video_h)/(old_f_win[3]-old_f_win[1]) )
            context.translate(-old_f_win[0],-old_f_win[1])
            svg.render_cairo(context)
            #paint last frame slightly dimmed
            context = cairo.Context(surface)
            context.set_source_rgba(0,0,0,128)
            context.paint()


        svg = rsvg.Handle(data=str(svg_xml))
        svg_xml.freeDoc()
        svg_xml = xml.parseDoc(xml_str)
        svg_xml_main_graph = svg_xml.xpathEval("//*[@id='graph1']")[0]
        
          

        #paint svg
        context.scale( float(video_w)/(f_win[2]-f_win[0]),
                       float(video_h)/(f_win[3]-f_win[1]) )
        context.translate(-f_win[0],-f_win[1])

        old_f_win = f_win
        
        svg.render_cairo(context)
        #rotate
        if rotate90:
            context2 = cairo.Context(surface2)
            
            context2.translate(video_h*0.5,video_w*0.5)
            context2.rotate(-math.pi/2.0)
            context2.translate(-video_w*0.5,-video_h*0.5)

            context2.set_source_surface(surface,0,0)
            context2.set_operator(cairo.OPERATOR_SOURCE)
            context2.paint()
            surface2.write_to_png(arg_out+"frame_"+i2str(fi)+".png")
        else:
            surface.write_to_png(arg_out+"frame_"+i2str(fi)+".png")
        print(".",end="")
        sys.stdout.flush()
    print("")

    
