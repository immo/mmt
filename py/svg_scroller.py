#!/usr/bin/env python
# coding:utf-8
# python version=2.7

from __future__ import print_function
import sys
import rsvg
import cairo

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

svg = rsvg.Handle(data=open(arg_svg).read())

svg_width = svg.props.width
svg_height = svg.props.height
print("SVG size:  ",svg_width,"x",svg_height,"pixels");

surface = cairo.ImageSurface(cairo.FORMAT_ARGB32, svg_width, svg_height)
context = cairo.Context(surface)
svg.render_cairo(context)
surface.write_to_png(arg_out+"complete.png")
