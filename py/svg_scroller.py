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

video_w = 1080
video_h = 1920
rotate90 = 1
midi_frames_per_second = 1920
movie_frames_per_second = 30

def i2str(i):
    s = str(i)
    if (len(s) < 8):
        return "0"*(8-len(s)) + s
    else:
        return s

args = sys.argv[1:]
if len(args) != 3:
    print("Usage: svg_scroller.py [SVG-File] [POINTS-File] [Output-Prefix]");
    sys.exit(0)

arg_svg, arg_points, arg_out = args

print("Loading SVG and parsing XML data...")
svg_file = open(arg_svg).read()
svg_xml = xml.parseDoc(svg_file)

svg_node = svg_xml.xpathEval("/*")[0]
svg_viewbox = eval("("+svg_node.hasProp("viewBox").content.replace(" ",",")+")")
print("ViewBox=",svg_viewbox)


svg_graphical_elements = svg_xml.xpathEval("/*/*/*")

svg_transform = svg_xml.xpathEval("/*/*")[0].hasProp("transform").content
translate_idx = svg_transform.find("translate(")+len("translate(")
svg_transform = eval("("+svg_transform[translate_idx:].replace(" ",","))

print("Loading XML data into RSVG...")
svg = rsvg.Handle(data=str(svg_xml))

svg_width = svg.props.width
svg_height = svg.props.height
print("SVG size:  ",svg_width,"x",svg_height,"pixels");

#surface = cairo.ImageSurface(cairo.FORMAT_ARGB32, svg_width, svg_height)
#context = cairo.Context(surface)
#svg.render_cairo(context)
#surface.write_to_png(arg_out+"complete.png")

points = eval(open(arg_points).read())
times = map(lambda x:x[0], points.values())+map(lambda x:x[1], points.values())
times = list(set(times))
times.sort()

print("Start/end point times range:",times[0],"to",times[-1])

times_frames = {}
times_frames_up_to = {}
t_before = 0
for t in times: #should we check midi for BPM and tempo events??
    times_frames[t] = (t*movie_frames_per_second)/midi_frames_per_second
    times_frames_up_to[t_before] = times_frames[t]
    t_before = t

def get_relevant_points(t):
    fltr = lambda x,t=t:points[x][0]<=t<points[x][1]
    return filter(fltr,points)

times_nodes = {}
for t in times:
    times_nodes[t] = get_relevant_points(t)

points_in_svg = {}
nodes_in_svg = {}

for el in svg_graphical_elements:
    nodename = el.getContent().strip()
    if nodename in points:
        ellipse_node = svg_xml.xpathEval(el.nodePath()+"/*[@cx]")[0]
        nodes_in_svg[nodename] = ellipse_node.nodePath()
        cx = float(ellipse_node.hasProp("cx").content)+svg_transform[0]
        cy = float(ellipse_node.hasProp("cy").content)+svg_transform[1]
        px = float(cx-svg_viewbox[0])/float(svg_viewbox[2]-svg_viewbox[0])*float(svg_width)
        py = float(cy-svg_viewbox[1])/float(svg_viewbox[3]-svg_viewbox[1])*float(svg_height)
        points_in_svg[nodename] = (px,py)

times_points = {}
for t in times:
    times_points[t] = map(lambda x: points_in_svg[x], times_nodes[t])

min_width = 400

times_window = {}
times_next_window = {}
t_before = 0
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
        
        times_window[t] = val
        times_next_window[t_before] = times_window[t]
        t_before = t
    except Exception,err:
        print(err)


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
        r = (r|127)>>2
        g = (g|127)>>2
        b = b|127
        return "#"+hex(r)[2:4]+hex(g)[2:4]+hex(b)[2:4]
    else:
        return "#3333FF"

marked_nodes = []
marked_nodes_style = [("fill",make_brighter),("stroke",make_brighter)]

for t in wins:

    for n in marked_nodes:
        demark = svg_xml.xpathEval(n[0])[0]
        for props in n[1:]:
            demark.setProp(props[0],props[1])

    for nodename in times_nodes[t]:
        nodepath = nodes_in_svg[nodename]
        entry = [nodepath]
        mark = svg_xml.xpathEval(nodepath)[0]
        for props in marked_nodes_style:
            old_val = (props[0], mark.hasProp(props[0]).content)
            entry.append(old_val)
            mark.setProp(props[0],props[1](old_val[1]))
        marked_nodes.append(entry)

    svg = rsvg.Handle(data=str(svg_xml))


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

    for fi in xrange(f,f_last):
        f_win = [0,0,0,0]
        for idx in range(4):
            alpha = (fi-f)/f_count
            f_win[idx] = win[idx]*(1.0-alpha) + next_win[idx]*alpha

        context = cairo.Context(surface)
        context.scale( float(video_w)/(f_win[2]-f_win[0]),
                       float(video_h)/(f_win[3]-f_win[1]) )
        context.translate(-f_win[0],-f_win[1])
        svg.render_cairo(context)
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

    
