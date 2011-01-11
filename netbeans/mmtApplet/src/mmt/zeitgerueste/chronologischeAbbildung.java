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

    public void addMappingPairs(int[] pairs) {
        for (int i=0;i+1<pairs.length;i+=2) {
            this.map.put(pairs[i],pairs[i+1]);
        }
    }
    
    public chronologischeAbbildung mapCopy() {
        chronologischeAbbildung copy = new chronologischeAbbildung(source, target);
        
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

    public boolean isComplete() {
        Set<Integer> noImage = new TreeSet<Integer>();
        for (int i=0; i < source.T.size(); ++i) {
            noImage.add(i);
        }
        noImage.removeAll(map.keySet());
        return noImage.isEmpty();
    }

    public boolean isSurjective() {
        Set<Integer> notAnImage = new TreeSet<Integer>();
        for (int i = 0; i < target.T.size(); ++i) {
            notAnImage.add(i);
        }

        Iterator<Integer> it = map.keySet().iterator();
        while (it.hasNext()) {
            notAnImage.remove(map.get(it.next()));
        }
        return notAnImage.isEmpty();
    }

    public boolean isInjective() {
        Set<Integer> images = new TreeSet<Integer>();

        Iterator<Integer> it = map.keySet().iterator();
        while (it.hasNext()){
            images.add(map.get(it.next()));
        }
        return map.keySet().size()==images.size();
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


        while(pairs.hasNext()){
            nTuple<Integer> pair = pairs.next();

            if (target.X.isLess(pair)!=isRectangle.get(pair)){
                return false;
            }
        }

        return true;
    }

    public boolean isValid() {
        return isSurjective() && isComplete() && isPartialWeaklyMonotone() && isPartialTargetOrderDefining();
    }

    @Override
    public String toString() {
        String format = "Map from: \n" + this.source 
                + "\n ... to :\n" + this.target + "\n ... by: \n  ";
        Iterator<Integer> it = this.map.keySet().iterator();
        int count = 0;
        while (it.hasNext()) {
            Integer s = it.next();
            Integer t = this.map.get(s);
            if (count > 0) {
                if (count % 6 == 0) {
                    format += "\n  ";
                } else {
                    format += "   ";
                }
            }
            count ++;
            String s_annotations = "";
            String t_annotations = "";
            if (this.source.T.get(s).annotations.size()>0) {
                s_annotations = this.source.T.get(s).annotations.toString();
            }

            if (this.target.T.get(t).annotations.size()>0){
                t_annotations = this.target.T.get(t).annotations.toString();
            }


            format += s +s_annotations + "->" +t+t_annotations;
        }
        
        return format;
    }
    


    public static void main(String args[])
            throws java.io.IOException, java.io.FileNotFoundException {

        
    }
}
