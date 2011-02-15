#!/usr/bin/env python
# coding:utf-8
# python version=2.7

from __future__ import print_function
import sys
import rsvg
import cairo
import libxml2 as xml

video_w = 1920
video_h = 1080

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
for t in times: #TODO check midi for BPM and tempo events
    times_frames[t] = (t*30)/240

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
        points_in_svg[nodename] = (cx,cy)

times_points = {}
for t in times:
    times_points[t] = map(lambda x: points_in_svg[x], times_nodes[t])


surface = cairo.ImageSurface(cairo.FORMAT_ARGB32, video_w, video_h)
context = cairo.Context(surface)
context.translate(-3400,0)
svg.render_cairo(context)
surface.write_to_png(arg_out+"frame.png")
