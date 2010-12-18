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

import java.security.*;
import javax.swing.*;

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

    AccessControlContext acc = AccessController.getContext();

    try {
        acc.checkPermission(new AllPermission());
    }
    catch (AccessControlException ace) {
           JOptionPane.showMessageDialog(this, ace);
    }
    
    interp = (PythonInterpreter) AccessController.doPrivileged(
                new PrivilegedAction() {
            public Object run() {
                return new PythonInterpreter();
            }
    });

    interp.execfile("py-src/JyApplet.py");

    PyObject appletClass = interp.get("JyApplet");

    PyObject appletObject = appletClass.__call__();
    applet = (JyAppletInterface) appletObject.__tojava__(JyAppletInterface.class);

    applet.setJavaApplet(this);
    
    applet.initHook();

    }

}
