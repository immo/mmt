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
public class chronologischeAbbildung implements Comparable {

    public zeitgeruest source;
    public zeitgeruest target;
    public Map<Integer, Integer> map;

    public chronologischeAbbildung(zeitgeruest source, zeitgeruest target) {
        this.source = source;
        this.target = target;
        this.map = new HashMap<Integer, Integer>();
    }

    public chronologischeAbbildung(chronologischeAbbildung f, chronologischeAbbildung g) {
        this.source = f.source;
        this.target = g.target;
        this.map = new HashMap<Integer, Integer>();
        Iterator<Integer> it = f.map.keySet().iterator();
        while (it.hasNext()) {
            Integer i = it.next();
            Integer fi = f.map.get(i);
            if (g.map.containsKey(fi)) {
                this.map.put(i, g.map.get(fi));
            }
        }
    }

    public chronologischeAbbildung(chronologischeAbbildung f, chronologischeAbbildung g, chronologischeAbbildung h) {
      this.source = f.source;
        this.target = h.target;
        this.map = new HashMap<Integer, Integer>();
        Iterator<Integer> it = f.map.keySet().iterator();
        while (it.hasNext()) {
            Integer i = it.next();
            Integer fi = f.map.get(i);
            if (g.map.containsKey(fi)) {
                Integer gfi = g.map.get(fi);
                if (h.map.containsKey(gfi)) {
                    this.map.put(i, h.map.get(gfi));
                }
            }
        }

    }

    public boolean isEquivalentToByIsoSets(Set<chronologischeAbbildung> source_isos,
            Set<chronologischeAbbildung> target_isos , chronologischeAbbildung f) {
        if (map.size() != f.map.size()) {
            return false;
        }

        /* test whether s*f*t == this for some s,t */

        Iterator<chronologischeAbbildung> st = source_isos.iterator();
        while (st.hasNext()) {
            chronologischeAbbildung s = st.next();
            Iterator<chronologischeAbbildung> tt = target_isos.iterator();
            while (tt.hasNext()) {
                chronologischeAbbildung t = tt.next();
                Iterator<Integer> it = map.keySet().iterator();
                boolean counterexample = false;
                while (it.hasNext()) {
                    Integer i = it.next();
                    Integer si = s.map.get(i);
                    if (f.map.containsKey(si)) {
                        Integer fsi = f.map.get(si);
                        Integer tfsi = t.map.get(fsi);
                        if (tfsi != map.get(i)) {
                            counterexample = true;
                            break;
                        }
                    } else {
                        counterexample = true;
                        break;
                    }
                }
                if (!counterexample) {
                    return true;
                }
            }
        }

        return false;
    }

    public int compareTo(Object o) {
        chronologischeAbbildung other = (chronologischeAbbildung) o;
        int cmp = source.compareTo(other.source);
        if (cmp != 0) {
            return cmp;
        }
        cmp = target.compareTo(other.target);
        if (cmp != 0) {
            return cmp;
        }
        int ts = map.size();
        int os = map.size();
        if (ts!=os) {
            return ts-os;
        }
        Set<Integer> sorted_keys = new TreeSet<Integer>();
        sorted_keys.addAll(map.keySet());

        Iterator<Integer> it = sorted_keys.iterator();
        while (it.hasNext()) {
            Integer i = it.next();
            if (other.map.containsKey(i)) {
                Integer tv = map.get(i);
                Integer ov = other.map.get(i);
                if (tv != ov) {
                    return tv - ov;
                }
            } else {
                return 1;
            }
        }
        return 0;
    }



    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final chronologischeAbbildung other = (chronologischeAbbildung) obj;
        if (this.source != other.source && (this.source == null || !this.source.equals(other.source))) {
            return false;
        }
        if (this.target != other.target && (this.target == null || !this.target.equals(other.target))) {
            return false;
        }
        if (this.map != other.map && (this.map == null || !this.map.equals(other.map))) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 41 * hash + (this.source != null ? this.source.hashCode() : 0);
        hash = 41 * hash + (this.target != null ? this.target.hashCode() : 0);
        hash = 41 * hash + (this.map != null ? this.map.hashCode() : 0);
        return hash;
    }

    public void addMappingPairs(int[] pairs) {
        for (int i = 0; i + 1 < pairs.length; i += 2) {
            this.map.put(pairs[i], pairs[i + 1]);
        }
    }

    public chronologischeAbbildung mapCopy() {
        chronologischeAbbildung copy = new chronologischeAbbildung(source, target);

        copy.map.putAll(this.map);
        return copy;
    }

    public boolean isPartialWeaklyMonotone() {
        Iterator<intPair> it = source.X.getNeighbors().iterator();
        while (it.hasNext()) {
            intPair pair = it.next();
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

    public boolean isPartialWeaklyMonotone(Integer onlyThisPreimage) {
        Iterator<intPair> it = source.X.getNeighbors().iterator();
        while (it.hasNext()) {
            intPair pair = it.next();
            int s = pair.get(0);
            int t = pair.get(1);

            if ((onlyThisPreimage.equals(s) || onlyThisPreimage.equals(t))
                    && map.containsKey(s) && map.containsKey(t)) {
                if (map.get(s) != map.get(t)) {
                    if (!target.X.isLess(map.get(s), map.get(t))) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    public boolean isPartialGoodChoiceForSurjectivity(Integer onlyThisPreimage) {
        /* this property is utilizing the fact that we want mappings with (s) and (o) */
        Integer image = map.get(onlyThisPreimage);
        Set<Integer> filter = target.X.getFilter(image);
        Set<Integer> ideal = target.X.getIdeal(image);
        Iterator<Integer> it = map.keySet().iterator();
        while (it.hasNext()) {
            Integer other_preimage = it.next();
            if (!other_preimage.equals(onlyThisPreimage)) {
                Integer other_image = map.get(other_preimage);
                if (ideal.contains(other_image)) {
                    if (source.X.getLongestUpPathLength(other_preimage, onlyThisPreimage)
                            < target.X.getLongestUpPathLength(other_image, image)) {
                        return false;
                    }
                } else if (filter.contains(other_image)) {
                    if (source.X.getLongestUpPathLength(onlyThisPreimage, other_preimage)
                            < target.X.getLongestUpPathLength(image, other_image)) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    public chronologischeAbbildung likeInverseMap() {
        chronologischeAbbildung inv = new chronologischeAbbildung(target, source);
        Iterator<Integer> it = map.keySet().iterator();
        while (it.hasNext()) {
            Integer i = it.next();
            inv.map.put(map.get(i), i);
        }
        return inv;
    }

    public boolean isComplete() {
        Set<Integer> noImage = new TreeSet<Integer>();
        for (int i = 0; i < source.T.size(); ++i) {
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
        while (it.hasNext()) {
            images.add(map.get(it.next()));
        }
        return map.keySet().size() == images.size();
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

        Map<intPair, Boolean> isRectangle = new HashMap<intPair, Boolean>();

        it = fibers.keySet().iterator();

        while (it.hasNext()) {
            Integer x = it.next();
            Iterator<Integer> jt = fibers.keySet().iterator();
            while (jt.hasNext()) {
                Integer y = jt.next();
                Iterator<Integer> xinv = fibers.get(x).iterator();
                boolean counterExample = false;
                while (xinv.hasNext()) {
                    Integer xfinv = xinv.next();
                    Iterator<Integer> yinv = fibers.get(y).iterator();
                    while (yinv.hasNext()) {
                        Integer yfinv = yinv.next();
                        if (!source.X.isLess(xfinv, yfinv)) {
                            xinv = (new TreeSet<Integer>()).iterator();
                            yinv = xinv;
                            counterExample = true;
                        }
                    }
                }
                isRectangle.put(new intPair(x, y), !counterExample);
            }
        }

        Iterator<intPair> pairs = isRectangle.keySet().iterator();


        while (pairs.hasNext()) {
            intPair pair = pairs.next();

            if (target.X.isLess(pair) != isRectangle.get(pair)) {
                return false;
            }
        }

        return true;
    }

    public boolean isValid() {
        return isSurjective() && isComplete() && isPartialWeaklyMonotone() && isPartialTargetOrderDefining();
    }

    public ArrayList<Integer> freeDomainElements() {
        ArrayList<Integer> elements = new ArrayList<Integer>();
        for (int i=0;i<source.T.size();++i) {
            if (!map.containsKey(i)) {
                elements.add(i);
            }
        }
        return elements;
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
            count++;
            String s_annotations = "";
            String t_annotations = "";
            if (this.source.T.get(s).annotations.size() > 0) {
                s_annotations = this.source.T.get(s).annotations.toString();
            }

            if (this.target.T.get(t).annotations.size() > 0) {
                t_annotations = this.target.T.get(t).annotations.toString();
            }


            format += s + s_annotations + "->" + t + t_annotations;
        }

        return format;
    }

    public String getDotCode() {
        String dot = "digraph G {\n";
        dot += "subgraph cluster_1 {";
        if (target.X.hasIntervalProperty()) {
            dot += "\ncolor=\"lightgrey\";\n";
            dot += target.getOldDotCode(false, "t", "color=\"#FF0000\"", "color=\"#FF0000\"");
        } else {
            dot += "\ncolor=\"red\";\n";
            dot += target.getOldDotCode(false, "t", "color=\"#880000\"", "color=\"#880000\"");
        }
        
        dot += "}\n";

        dot += "subgraph cluster_2 {";
        if (source.X.hasIntervalProperty()) {
            dot += "\ncolor=\"lightgrey\";\n";
            dot += source.getOldDotCode(false, "s", "color=\"#0000FF\"", "color=\"#0000FF\"");
        } else
        {
            dot += "\ncolor=\"red\";\n";
            dot += source.getOldDotCode(false, "s", "color=\"#000088\"", "color=\"#000088\"");
        }
        
        dot += "}\n";

        Iterator<Integer> it = map.keySet().iterator();
        while (it.hasNext()) {
            Integer s = it.next();
            Integer t = map.get(s);

            dot += "s"+s+" -> "+"t"+t+";\n";
        }

        dot += "}";
        return dot;
    }

    public static void main(String args[])
            throws java.io.IOException, java.io.FileNotFoundException {
    }
}
