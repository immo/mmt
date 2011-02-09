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
            Map<Integer, Set<Object>> weightedAnn = z.weightedCountAnnotationsInverse();
            System.out.println("weighted= "+ weightedAnn);
            (new File("/tmp/dot")).mkdirs();
            String name = "/tmp/dot/"+(new File(args[i])).getName()+".dot";
            System.out.print("Creating dot file "+name);
            z.writeToDotFile2(name);
            System.out.println();

        }
    }
}
