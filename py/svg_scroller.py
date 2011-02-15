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
        px = float(cx-svg_viewbox[0])/float(svg_viewbox[2]-svg_viewbox[0])*float(svg_width)
        py = float(cy-svg_viewbox[1])/float(svg_viewbox[3]-svg_viewbox[1])*float(svg_height)
        points_in_svg[nodename] = (px,py)

times_points = {}
for t in times:
    times_points[t] = map(lambda x: points_in_svg[x], times_nodes[t])

min_width = 400

times_window = {}
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
    except Exception,err:
        print(err)


wins = times_window.keys()
wins.sort()

marked_nodes = []
marked_nodes_style = [("fill","#0000FF"),("stroke","#0000FF")]
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
            mark.setProp(props[0],props[1])
        marked_nodes.append(entry)

    svg = rsvg.Handle(data=str(svg_xml))
    f= open(arg_out+"frame_"+i2str(t)+".svg","wb")
    f.write(str(svg_xml))
    f.close()
    
    print("frame =",t)
    win = times_window[t]
    print("window =",win)
    print("width =",win[2]-win[0],"height =",win[3]-win[1])
    print("scale =",( float(video_w)/(win[2]-win[0]), float(video_h)/(win[3]-win[1]) ))

    surface = cairo.ImageSurface(cairo.FORMAT_ARGB32, video_w, video_h)
    context = cairo.Context(surface)
    context.scale( float(video_w)/(win[2]-win[0]), float(video_h)/(win[3]-win[1]) )    
    context.translate(-win[0],-win[1])
    svg.render_cairo(context)
    surface.write_to_png(arg_out+"frame_"+i2str(t)+".png")
