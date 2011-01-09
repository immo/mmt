/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mmt.zeitgerueste;

import java.util.*;
import java.io.*;

/**
 *
 * @author immanuel
 */
public class chronologischeAbbildung {

    public zeitgeruest source;
    public zeitgeruest target;
    public Map<Integer, Integer> map;

    public boolean isPartialWeaklyMonotone() {
        Iterator<int[]> it = source.X.getNeighbors().iterator();
        while (it.hasNext()) {
            int[] pair = it.next();
            int s = pair[0];
            int t = pair[1];

            if (map.containsKey(s) && map.containsKey(t)) {
                if (map.get(s) != map.get(t)) {
                    if (!target.X.isLess(map.get(s), map.get(t))) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    public boolean isSurjective() {
        Set<Integer> noImage = new TreeSet<Integer>();
        for (int i = 0; i < target.T.size(); ++i) {
            noImage.add(i);
        }

        Iterator<Integer> it = map.keySet().iterator();
        while (it.hasNext()) {
            noImage.remove(map.get(it.next()));
        }
        return noImage.isEmpty();
    }

    public boolean isPartialTargetOrderDefining() {
        Map<Integer, Set<Integer>> fibers = new HashMap<Integer, Set<Integer>>();
        Iterator<Integer> it = map.keySet().iterator();
        while (it.hasNext()) {
            Integer s = it.next();
            Integer fs = map.get(s);
            if (!fibers.containsKey(fs)) {
                fibers.put(fs, new TreeSet<Integer>());
            }
            fibers.get(fs).add(s);
        }

        Map<int[], Boolean> isRectangle = new HashMap<int[], Boolean>();

        it = fibers.keySet().iterator();

        while (it.hasNext()){
            Integer x = it.next();
            Iterator<Integer> jt = fibers.keySet().iterator();
            while (jt.hasNext()){
                Integer y = jt.next();
                Iterator<Integer> xinv = fibers.get(x).iterator();
                boolean counterExample = false;
                while (xinv.hasNext()) {
                    Integer xfinv = xinv.next();
                    Iterator<Integer> yinv = fibers.get(y).iterator();
                    while (yinv.hasNext()){
                        Integer yfinv = yinv.next();
                        if (!source.X.isLess(xfinv, yfinv)){
                            xinv = (new TreeSet<Integer>()).iterator();
                            yinv = xinv;
                            counterExample = true;
                        }
                    }
                }
                isRectangle.put(new int[]{x,y}, counterExample);
            }
        }

        Iterator<int[]> pairs = isRectangle.keySet().iterator();

        while(pairs.hasNext()){
            int[] pair = pairs.next();

            if (target.X.isLess(pair[0], pair[1])!=isRectangle.get(pair)){
                return false;
            }
        }

        return true;
    }

    public static void main(String args[])
            throws java.io.IOException, java.io.FileNotFoundException {
        Set<Integer> mi = new TreeSet<Integer>();
        mi.add(2);
        mi.add(4);
        mi.add(5);
        Iterator<Integer> it = mi.iterator();
        while (it.hasNext()) {
            Integer s = it.next();
            Iterator<Integer> jt = mi.iterator();
            while (jt.hasNext()){
                Integer t = jt.next();
                System.out.println(s+", "+t);
                if (s+t>7){
                    it = (new TreeSet<Integer>()).iterator();
                    jt = it;
                }

            }
        }
    }
}
