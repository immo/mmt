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

video_w = 720
video_h = video_w*16/9
rotate90 = 1
midi_frames_per_second = 1920
movie_frames_per_second = 30
left_window_times = midi_frames_per_second/2
right_window_times = midi_frames_per_second
pre_glow_frames = movie_frames_per_second/10
after_glow_frames = movie_frames_per_second/4

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
    times_frames[t] = (t*movie_frames_per_second)/midi_frames_per_second
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
        f_win = [0,0,0,0]
        for idx in range(4):
            alpha = (fi-f)/f_count
            f_win[idx] = win[idx]*(1.0-alpha) + next_win[idx]*alpha

        for node in svg_xml_nodes:
            bbox = svg_xml_nodes_bbox[node]
            if box_intersection(bbox,f_win):
                nodetitle = svg_xml_nodes_titles[node][0]
                if nodetitle in colored_nodes:
                    svg_xml_main_graph.addChild(node.copyNode(1))
                else:
                    if (fi > points_frames[nodetitle][0]-pre_glow_frames) and (fi <= points_frames[nodetitle][0]):
                        greyness = -(fi-points_frames[nodetitle][0])/float(pre_glow_frames)
                        greyed = node.copyNode(1)
                        grey_node(greyed,greyness)
                        svg_xml_main_graph.addChild(greyed)
                    elif (fi < points_frames[nodetitle][1]+after_glow_frames) and (fi >= points_frames[nodetitle][1]):
                        greyness = (fi-points_frames[nodetitle][1])/float(after_glow_frames)
                        greyed = node.copyNode(1)
                        grey_node(greyed,greyness)
                        svg_xml_main_graph.addChild(greyed)
                    else:                       
                        greyed = node.copyNode(1)
                        grey_node(greyed,1.0)
                        svg_xml_main_graph.addChild(greyed)
                    
        for node in svg_xml_edges:
            bbox = svg_xml_edges_bbox[node]
            if box_intersection(bbox,f_win):
                nodetitle = svg_xml_edges_titles[node][0]
                if nodetitle in colored_nodes:
                    svg_xml_main_graph.addChild(node.copyNode(1))
                else:
                    if (fi > points_frames[nodetitle][0]-pre_glow_frames) and (fi <= points_frames[nodetitle][0]):
                        greyness = -(fi-points_frames[nodetitle][0])/float(pre_glow_frames)
                        greyed = node.copyNode(1)
                        grey_node(greyed,greyness)
                        svg_xml_main_graph.addChild(greyed)
                    elif (fi < points_frames[nodetitle][1]+after_glow_frames) and (fi >= points_frames[nodetitle][1]):
                        greyness = (fi-points_frames[nodetitle][1])/float(after_glow_frames)
                        greyed = node.copyNode(1)
                        grey_node(greyed,greyness)
                        svg_xml_main_graph.addChild(greyed)
                    else:                       
                        greyed = node.copyNode(1)
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

    
