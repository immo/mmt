/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mmt.applets.zeitgerueste;

import java.awt.*;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
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
    int x, y, w, h;
    float factor;
    boolean error;

    public imageOnDesk(String imageUrl) {
        this.imageUrl = imageUrl;
        this.error = false;
        this.x = 0;
        this.y = 0;
        this.w = 0;
        this.h = 0;
        this.factor = 1;
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        try {
            if (imageUrl.equals("clipboard://")) {
                this.image = (BufferedImage) clipboard.getData(DataFlavor.imageFlavor);
            } else {
                this.image = ImageIO.read(new URL(imageUrl));
            }
        } catch (UnsupportedFlavorException ufe) {
            this.error = true;
        } catch (IOException e) {
            this.error = true;
        }
        if (!this.error) {
            this.w = this.image.getWidth();
            this.h = this.image.getHeight();
        }
    }

    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void dragPosition(int x, int y) {
        this.x += x;
        this.y += y;
    }

    public void stretchImage(float factor) {
        if (!this.error) {
            this.w = (int) (this.image.getWidth() * factor);
            this.h = (int) (this.image.getHeight() * factor);
        }
    }

    public void scaleByHeight(int delta_h) {
        int new_height = this.h + delta_h;
        if (new_height > 0)
        {
            stretchImage((float)new_height/(float)this.image.getHeight());
        }
    }

    public boolean isOnImage(int x, int y) {
        if (this.error)
            return false;
        if ((this.x > x) || (this.y > y) || (this.x + this.w <= x) || (this.y + this.h <= y)) {
            return false;
        }
        return true;
    }

    public void paint(Graphics g, Component observer) {
        if (!error) {
            g.drawImage(image, x, y, w, h, observer);
        }
    }
}
