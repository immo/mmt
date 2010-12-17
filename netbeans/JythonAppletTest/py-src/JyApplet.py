#from __future__ import print_function not implemented!!
from jythonapplettest.interfaces import JyAppletInterface
# Notice the doc string that has been added after the class definition below
from java.awt import Color
from java.applet import Applet

class JyApplet(JyAppletInterface):
    ''' Class to hold building objects '''

    def __init__(self):
        self.java_applet = None

    def setJavaApplet(self,java_applet):
        self.java_applet = java_applet

    def initHook(self):
        self.java_applet.setBackground(Color.BLACK)
        

    def paint(self,g):
        size = self.java_applet.getSize()
        w = size.width
        h = size.height
        g.setColor(Color.GREEN)
        for i in range(10):
            g.drawLine( w, h, i * w / 10, 0 )
        
    def getDoc(self):
        return self.__doc__

