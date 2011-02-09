/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mmt.zeitgerueste;

import java.io.*;
import java.util.*;

/**
 *
 * @author immanuel
 */
public class eventsAnalyzor {

    public static void main(String args[])
            throws java.io.IOException, java.io.FileNotFoundException {
        for (int i=0;i<args.length;++i) {
            System.out.print("Analyzing "+args[i]);
            midiEvents evts = new midiEvents(args[i]);
            System.out.print(".");
            zeitgeruest z = evts.generateZeitgeruest();
            System.out.println("..");
            System.out.println("flat= "+z.flatCountAnnotationsInverse());
            System.out.println("weighted= "+z.weightedCountAnnotationsInverse());
        }
    }
}
