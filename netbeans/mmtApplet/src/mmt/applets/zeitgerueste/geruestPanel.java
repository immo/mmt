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
public class geruestPanel extends JPanel {
    ArrayList<imageOnDesk> sheetImages;

    public geruestPanel() {
        this.sheetImages = new ArrayList<imageOnDesk>();
    }

    public int addImageToDesk(String imageUrl) {
        imageOnDesk img = new imageOnDesk(imageUrl);
        if (!sheetImages.add(img))
            return -1;
        return sheetImages.indexOf(img);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        for (int i=0;i<sheetImages.size();i++)
            sheetImages.get(i).paint(g,this);

        g.setColor(Color.black);
        for (int i=0;i<100;i+=10)
            g.drawLine(0, 0, i, 100);
    }

}
