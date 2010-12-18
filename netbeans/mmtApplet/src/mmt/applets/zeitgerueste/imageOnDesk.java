/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package mmt.applets.zeitgerueste;

import java.awt.*;
import java.applet.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.swing.*;
import java.net.*;
import javax.imageio.*;
import java.util.*;

/**
 *
 * @author immanuel
 */
public class imageOnDesk {
    
    String imageUrl;
    BufferedImage image;
    int x,y;
    boolean error;

    public imageOnDesk(String imageUrl) {
        this.imageUrl = imageUrl;
        this.error = false;
        this.x = 0;
        this.y = 0;
        try {
            this.image = ImageIO.read(new URL(imageUrl));
        } catch (IOException e) {
            this.error = true;
        }
    }

    public void setPosition(int x, int y) {
        this.x = x; this.y = y;
    }

    public void paint(Graphics g, Component observer) {
        if (!error)
            g.drawImage(image, x, y, observer);
    }


}
