/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package jythonapplettest.interfaces;
import java.applet.*;
import java.awt.*;

/**
 *
 * @author immanuel
 */
public interface JyAppletInterface {

 public void setJavaApplet(Applet java_applet);

 public void initHook();

 public void paint(Graphics m);
}
