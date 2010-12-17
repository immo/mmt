/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package jythonapplettest;

import java.applet.*;
import java.awt.*;

import org.python.core.Py;
import org.python.core.PyObject;
import org.python.core.PySystemState;


/**
 *
 * @author immanuel
 */
public class jtapplet extends Applet {
    int m_height, m_width;
    public void paint(Graphics m) {
        m.setColor(Color.black);
        for (int i = 0; i < 10; ++i) m.drawLine(m_width, m_height, i * m_width / 10, 0);
    }
    
public void init() {
    m_width = getSize().width;
    m_height = getSize().height;
    setBackground(Color.green);
    }

}
