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
public class midiEvents {

    ArrayList<nTuple<String>> raw_events;

    ArrayList<Integer> key_code;
    ArrayList<Float> start_beat;
    ArrayList<Float> beat_length;
    ArrayList<Integer> start_time;
    ArrayList<Integer> end_time;
    ArrayList<Integer> channel;

    /* Format for notes:
          start(delta), stop(delta), channel, key, start(beat), length(beat)     
     */

    public class byEventTime implements java.util.Comparator {
        public int compare(Object l, Object r) {
            int lstart = Integer.parseInt(((nTuple<String>)l).get(0).trim());
            int rstart = Integer.parseInt(((nTuple<String>)r).get(0).trim());
            if (lstart == rstart) {
                int l2 = Integer.parseInt(((nTuple<String>)l).get(1).trim());
                int r2 = Integer.parseInt(((nTuple<String>)r).get(1).trim());
                return l2 - r2;
            }
            return lstart-rstart;
    }
}

    public midiEvents(String filename) throws FileNotFoundException {
        raw_events = new ArrayList<nTuple<String>>();
        key_code = new ArrayList<Integer>();
        start_time = new ArrayList<Integer>();
        end_time = new ArrayList<Integer>();
        channel = new ArrayList<Integer>();
        start_beat = new ArrayList<Float>();
        beat_length = new ArrayList<Float>();

        File f = new File(filename);
        FileReader fr = new FileReader(f);
        Scanner lines = new Scanner(fr);
        
        try {
            while (lines.hasNextLine()) {
                Scanner parts = new Scanner(lines.nextLine());
                parts.useDelimiter(",");
                nTuple<String> dta = new nTuple<String>(parts);
                if (dta.tuple.size()==6) raw_events.add(dta);
            }
        }
        finally {
            lines.close();
        }

        Collections.sort(raw_events,new byEventTime());

        Iterator<nTuple<String>> it = raw_events.iterator();
        while (it.hasNext()) {
            nTuple<String> t = it.next();
            
                start_time.add(Integer.parseInt(t.get(0).trim()));
                end_time.add(Integer.parseInt(t.get(1).trim()));
                channel.add(Integer.parseInt(t.get(2).trim()));
                key_code.add(Integer.parseInt(t.get(3).trim()));
                start_beat.add(Float.parseFloat(t.get(4).trim()));
                beat_length.add(Float.parseFloat(t.get(5).trim()));

            
        }
    }

    zeitgeruest generateZeitgeruest() {
        zeitgeruest z = new zeitgeruest(raw_events.size());
        Iterator<Integer> ii = key_code.iterator();
        int idx =0;
        while (ii.hasNext()) {
            Integer keycode = ii.next();
            z.T.get(idx).addAnnotation(keyName(keycode));
            z.T.get(idx).addAnnotation(keyClassName(keycode));
            ++idx;
        }

        z.X = new intervallChronologie(start_time, end_time);
        

        return z;
    }

    static String[] noteNames = {"C","C#","D","D#","E","F","F#",
        "G","G#","A","A#","H"};

    public static String keyName(Integer code) {
        return noteNames[code%12] + (code/12 -1);
    }

    public static String keyClassName(Integer code) {
        return noteNames[code%12];
    }


    public static void main(String args[])
            throws java.io.IOException, java.io.FileNotFoundException {
        System.out.println("Testing midi events class...");
        midiEvents evts = new midiEvents("/home/immanuel/git/l.jython/data/stpaul_33.events");
        System.out.println("Generating zeitgeruest...");
        zeitgeruest z = evts.generateZeitgeruest();
        System.out.println("done.");
        System.out.println("flat= "+z.flatCountAnnotationsInverse());
        System.out.println("weighted= "+z.weightedCountAnnotationsInverse());
        
        
        
    }
}
