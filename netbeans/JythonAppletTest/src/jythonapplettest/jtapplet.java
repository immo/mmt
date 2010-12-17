/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package jythonapplettest;

import java.applet.*;
import java.awt.*;

import org.jython.book.util.*;

import org.python.core.PyObject;
import org.python.core.PyString;
import org.python.util.PythonInterpreter;
import jythonapplettest.interfaces.JyAppletInterface;

/**
 *
 * @author immanuel
 */
public class jtapplet extends Applet {

    PythonInterpreter interp;
    JyAppletInterface applet;


    public void paint(Graphics m) {
        applet.paint(m);
    }
    
public void init() {

    interp = new PythonInterpreter();
    interp.execfile("JyApplet.py");

    PyObject appletClass = interp.get("JyApplet");

    PyObject appletObject = appletClass.__call__();
    applet = (JyAppletInterface) appletObject.__tojava__(JyAppletInterface.class);

    applet.setJavaApplet(this);
    
    applet.initHook();

    }

}
