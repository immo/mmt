/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mmt.applets.zeitgerueste;

import java.awt.*;
import java.applet.*;
import java.awt.event.*;
import javax.swing.*;
import java.net.*;
import javax.imageio.*;
import javax.swing.filechooser.*;

/**
 *
 * @author immanuel
 */
public class geruestApplet extends Applet {

    int width = 1200;   /// _internal_ width
    int height = 1024;  /// _internal_ height
    geruestPanel workspace; /// workspace panel
    JScrollPane workscroll; /// workspace scroll agent
    JToolBar toolbar;
    JScrollPane toolscroll; /// toolbar scroll agent
    JPanel toolspace; /// toolbar panel
    String addImageLabel = "Add local image...";
    String addUrlImageLabel = "Add image from url...";
    String addClipboardImageLabel = "Add image from clipboard...";
    Applet mySelf;

    @Override
    public void init() {
        mySelf = this;

        int toolbar_height = 25;
        int toolscroll_height = 20;
        int toolbar_width = 400;

        if (getParameter("iwidth") != null) {
            width = Integer.parseInt(getParameter("iwidth"));
        }
        if (getParameter("iheight") != null) {
            height = Integer.parseInt(getParameter("iheight"));
        }

        toolbar = new JToolBar();
        toolbar.setFloatable(false);
        toolbar.setRollover(true);




        toolscroll = new JScrollPane(toolbar);
        toolscroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
        toolscroll.setPreferredSize(new Dimension(getSize().width - 4,
                toolbar_height + toolscroll_height));

        add(toolscroll, BorderLayout.PAGE_START);

        workspace = new geruestPanel();
        workspace.setPreferredSize(new Dimension(width, height));

        workscroll = new JScrollPane(workspace);
        workscroll.setPreferredSize(new Dimension(getSize().width - 4,
                getSize().height - 10 - toolbar_height - toolscroll_height));

        

        add(workscroll, BorderLayout.CENTER);

        if (getParameter("imageurl")!=null) {
            workspace.addImageToDesk(getParameter("imageurl"));
        }


        addToolbarBtn(new JButton(this.addImageLabel), new ActionListener() {

            public void actionPerformed(ActionEvent ae) {
                JFileChooser chooser = new JFileChooser();
                FileNameExtensionFilter filter = new FileNameExtensionFilter(
                        "JPG & GIF & PNG & BMP Images",
                        "jpg", "gif", "png", "bmp");
                chooser.setFileFilter(filter);
                int returnVal = chooser.showOpenDialog(mySelf);
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    workspace.addImageToDesk("file://" + chooser.getSelectedFile().toString());
                    mySelf.repaint();
                }

            }
        });

        addToolbarBtn(new JButton(this.addUrlImageLabel), new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                String urlinput = JOptionPane.showInputDialog(mySelf, "Enter url:", "Add image...", 1);
                if (urlinput != null) {
                    if (!urlinput.contains("://")) {
                        urlinput = "http://" + urlinput;
                    }
                    workspace.addImageToDesk(urlinput);
                    mySelf.repaint();
                }
            }
        });

        addToolbarBtn(new JButton(this.addClipboardImageLabel), new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                workspace.addImageToDesk("clipboard://");
                mySelf.repaint();
            }
        });


    }

    public void addToolbarBtn(JButton btn, ActionListener action) {
        btn.addActionListener(action);
        toolbar.add(btn);

    }
}


