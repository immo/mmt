/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mmt.applets.zeitgerueste;

import java.awt.*;
import java.applet.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import javax.swing.*;
import java.net.*;
import javax.imageio.*;
import java.util.*;

/**
 *
 * @author immanuel
 */
public class geruestPanel extends JPanel implements MouseListener,
        MouseMotionListener {

    ArrayList<imageOnDesk> sheetImages;
    String mouseAction = "";
    int mouseObject = -1;
    int mouseActionX, mouseActionY;
    

    public geruestPanel() {
        this.sheetImages = new ArrayList<imageOnDesk>();
        addMouseListener(this);
        addMouseMotionListener(this);
        
    }

    
    public int addImageToDesk(String imageUrl) {
        imageOnDesk img = new imageOnDesk(imageUrl);
        if (!sheetImages.add(img)) {
            return -1;
        }
        return sheetImages.indexOf(img);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        for (int i = 0; i < sheetImages.size(); i++) {
            sheetImages.get(i).paint(g, this);
        }

        g.setColor(Color.black);
        for (int i = 0; i < 100; i += 10) {
            g.drawLine(0, 0, i, 100);
        }
    }

    public int getImageNbr(int x, int y) {
        for (int i = sheetImages.size() - 1; i >= 0; i--) {
            if (sheetImages.get(i).isOnImage(x, y)) {
                return i;
            }
        }
        return -1;
    }

    public void mouseClicked(MouseEvent me) {
    }

    public void mouseEntered(MouseEvent me) {
    }

    public void mouseExited(MouseEvent me) {
    }

    public void mousePressed(MouseEvent me) {

        int x = me.getPoint().x;
        int y = me.getPoint().y;

        mouseObject = getImageNbr(x, y);
        if ((me.getButton() == MouseEvent.BUTTON3)
                && (me.getModifiers() & MouseEvent.SHIFT_MASK) != MouseEvent.SHIFT_MASK) {
            mouseAction = "drag";
        } else if (me.getButton() == MouseEvent.BUTTON3) {
            mouseAction = "scale";
        } else if ((me.getButton() == MouseEvent.BUTTON1)
                && (me.getModifiers() & MouseEvent.SHIFT_MASK) != MouseEvent.SHIFT_MASK) {
            mouseAction = "scroll";
        } else {
            mouseAction = "";
        }
        mouseActionX = x;
        mouseActionY = y;
    }

    public void mouseReleased(MouseEvent me) {
        mouseAction = "";




    }

    public void mouseDragged(MouseEvent me) {
        int x = me.getPoint().x;
        int y = me.getPoint().y;

        if (mouseAction == "scroll") {

            Rectangle r = new Rectangle(x, y, 1, 1);
            scrollRectToVisible(r);

        } else if (mouseObject > -1) {
            if (mouseAction == "drag") {
                sheetImages.get(mouseObject).dragPosition(x - mouseActionX,
                        y - mouseActionY);
                mouseActionX = x;
                mouseActionY = y;
                repaint();




            } else if (mouseAction == "scale") {
                sheetImages.get(mouseObject).scaleByHeight(x - mouseActionX
                        + (y - mouseActionY) * 3);
                mouseActionX = x;
                mouseActionY = y;
                repaint();




            }
        }
    }

    public void mouseMoved(MouseEvent me) {
    }
}
