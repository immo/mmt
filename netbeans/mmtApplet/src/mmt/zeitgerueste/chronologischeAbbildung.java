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

    public chronologischeAbbildung(zeitgeruest source, zeitgeruest target) {
        this.source = source;
        this.target = target;
        this.map = new HashMap<Integer,Integer>();
    }
    
    public chronologischeAbbildung mapCopy() {
        chronologischeAbbildung copy = new chronologischeAbbildung(source, target);
        copy.map = new HashMap<Integer, Integer>();
        copy.map.putAll(this.map);
        return copy;
    }

    public boolean isPartialWeaklyMonotone() {
        Iterator<nTuple<Integer>> it = source.X.getNeighbors().iterator();
        while (it.hasNext()) {
            nTuple<Integer> pair = it.next();
            int s = pair.get(0);
            int t = pair.get(1);

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

        Map<nTuple<Integer>, Boolean> isRectangle = new HashMap<nTuple<Integer>, Boolean>();

        it = fibers.keySet().iterator();

        System.out.println("fibers: " + fibers);

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
                isRectangle.put(new nTuple<Integer>(x,y), !counterExample);
            }
        }

        Iterator<nTuple<Integer>> pairs = isRectangle.keySet().iterator();

        System.out.println("isRectangle: " + isRectangle);

        while(pairs.hasNext()){
            nTuple<Integer> pair = pairs.next();

            System.out.println(pair +" = " + isRectangle.get(pair));
            System.out.println("target.X.isLess = " + target.X.isLess(pair));

            if (target.X.isLess(pair)!=isRectangle.get(pair)){
                return false;
            }
        }

        return true;
    }

    public boolean isValid() {
        return isSurjective() && isPartialWeaklyMonotone() && isPartialTargetOrderDefining();
    }

    public static void main(String args[])
            throws java.io.IOException, java.io.FileNotFoundException {
        
    }
}
