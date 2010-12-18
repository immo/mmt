/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package mmt.applets.zeitgerueste;
import java.awt.*;
import java.applet.*;

/**
 *
 * @author immanuel
 */
public class geruestApplet extends Applet {
    int width, height;

    public void init() {
        setBackground( Color.black );
        width = getSize().width;
        height = getSize().height;
    }

    public void paint(Graphics g) {
        
        g.setColor( Color.green );
        for ( int i = 0; i < 10; ++i ) {
          g.drawLine( width, height, i * width / 10, 0 );
          }

    }


}
