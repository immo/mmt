/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mmt.zeitgerueste;

import java.util.*;
import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author immanuel
 */
public class zeitgeruest implements Comparable {

    ArrayList<traeger> T;
    chronologie X;

    public int compareTo(Object o) {
        zeitgeruest other = (zeitgeruest)o;
        int ts = T.size();
        int os = other.T.size();
        if (ts!=os) {
            return ts-os;
        }
        return X.compareTo(other.X);
    }



    public zeitgeruest() {
        T = new ArrayList<traeger>();
        X = new chronologie();
    }

    public zeitgeruest(int sizeOfT) {
        this.T = new ArrayList<traeger>();
        for (int i = 0; i < sizeOfT; ++i) {
            this.T.add(new traeger());
        }
        X = new chronologie();
    }

    public zeitgeruest(Iterable annotations) {
        this.T = new ArrayList<traeger>();
        for (Iterator it = annotations.iterator(); it.hasNext();) {
            traeger t = new traeger();
            t.addAnotation(it.next());
            this.T.add(t);
        }
        X = new chronologie();
    }

    public zeitgeruest(Object[] annotations) {
        this.T = new ArrayList<traeger>();
        for (int i = 0; i < annotations.length; ++i) {
            traeger t = new traeger();
            t.addAnotation(annotations[i]);
            this.T.add(t);
        }
        X = new chronologie();
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        zeitgeruest c = new zeitgeruest(T.size());
        c.X = (chronologie) this.X.clone();

        return c;
    }

    public boolean addChain(int[] chain) {
        ArrayList<Integer> list = new ArrayList<Integer>(chain.length);

        for (int i = 0; i < chain.length; ++i) {
            list.add(chain[i]);
        }

        return this.X.addChain(list);
    }

    public boolean addPairs(int[] inline_pairs) {
        Set<nTuple<Integer>> pairs = new HashSet<nTuple<Integer>>();

        for (int i = 0; i < inline_pairs.length; i += 2) {
            pairs.add(new nTuple<Integer>(inline_pairs[i], inline_pairs[i + 1]));
        }

        return this.X.addPairs(pairs);
    }

    public boolean isIsomorphicTo(zeitgeruest target) {
        if (T.size() != target.T.size()) {
            return false;
        }
        Set<chronologischeAbbildung> iso = getOneMapOnto(target);
        return !iso.isEmpty(); /* by (o) the inverse itself is also chronological */
    }

    public Set<chronologischeAbbildung> getOneMapOnto(zeitgeruest target) {
        return getAllMapsOnto(target, true);
    }

    public Set<chronologischeAbbildung> getAllMapsOnto(zeitgeruest target) {
        return getAllMapsOnto(target, false);
    }

    public static Set<zeitgeruest> getNextLevelOfIsoClassRepresentations(Set<zeitgeruest> start) {
        Set<zeitgeruest> next_level = new HashSet<zeitgeruest>();
        Iterator<zeitgeruest> it = start.iterator();
        while (it.hasNext()) {
            zeitgeruest z = it.next();


            Iterator<nTuple<Integer>> pair = z.possibleNewNeighborPairs().iterator();
            while (pair.hasNext()) {
                nTuple<Integer> s_t = pair.next();

                zeitgeruest z2 = null;
                try {
                    z2 = (zeitgeruest) z.clone();
                } catch (CloneNotSupportedException ex) {
                    Logger.getLogger(zeitgeruest.class.getName()).log(Level.SEVERE, null, ex);
                }
                z2.X.addPair(s_t.get(0), s_t.get(1));
                if (z.X.getNeighborEdgeCount() < z2.X.getNeighborEdgeCount()) {
                    /* it is possible that another neighbor edge will be deleted by adding a new edge */
                    Iterator<zeitgeruest> current = next_level.iterator();
                    boolean already_in_there = false;
                    while (current.hasNext() && (!already_in_there)) {
                        if (current.next().isIsomorphicTo(z2)) {
                            already_in_there = true;
                        }
                    }
                    if (!already_in_there) {
                        next_level.add(z2);
                    }
                }
            }

        }
        return next_level;
    }

    public Set<chronologischeAbbildung> getAllMapsOnto(zeitgeruest target,
            boolean one_is_enough) {
        boolean early_checkout = one_is_enough && (T.size() == target.T.size());
        Set<chronologischeAbbildung> partial_maps = new HashSet<chronologischeAbbildung>();

        if (T.size() >= target.T.size()) {
            chronologischeAbbildung current_map = new chronologischeAbbildung(this, target);

            /* Idea: take a partial but surjective map and fill out the free spots */

            ArrayList<Integer> current_placement = new ArrayList<Integer>(T.size());
            Set<Integer> taken_images = new TreeSet<Integer>();
            for (int i = 0; i < target.T.size(); ++i) {
                current_placement.add(0);
            }

            int recursion_depth = 0;
            int domain_size = T.size();
            int codomain_size = target.T.size();

            boolean step_back = false;
            while (recursion_depth >= 0) {

                for (int preimage = current_placement.get(recursion_depth);
                        //choose next preimage for current recursion depth
                        preimage <= domain_size;
                        ++preimage) {
                    if (preimage == domain_size) {

                        step_back = true; //this recursion depth step is done
                    } else {
                        boolean taken = false; //check whether preimage is still available
                        for (int j = 0; j < recursion_depth; ++j) {
                            if (current_placement.get(j).equals(preimage)) {
                                taken = true;
                                break;
                            }
                        }
                        if (!taken) { //we can go deeper into recursion from here

                            current_placement.set(recursion_depth, preimage);
                            current_map.map.put(preimage, recursion_depth);

                            //check whether this is a good choice, i.e. a order compatible, etc.

                            if (current_map.isPartialWeaklyMonotone(preimage)
                                    && current_map.isPartialGoodChoiceForSurjectivity(preimage)) {
                                //go deeper one level
                                recursion_depth++;


                                if (recursion_depth == codomain_size) {
                                    //this is the last level of recursion
                                    partial_maps.add(current_map.mapCopy());

                                    if (early_checkout) {
                                        return partial_maps;
                                    }
                                    step_back = true;
                                }

                                break;
                            } else {

                                //not monotone or bad choice, remove this part, no descend

                                current_map.map.remove(preimage);
                            }
                        }
                    }
                }
                if (step_back) {
                    if (recursion_depth > 0) {
                        Integer preimage = current_placement.get(recursion_depth - 1);
                        current_map.map.remove(preimage);
                        current_placement.set(recursion_depth - 1, preimage + 1);
                    }
                    if (recursion_depth < codomain_size) {
                        current_placement.set(recursion_depth, 0);
                    }
                    recursion_depth--;
                    step_back = false;
                }
            }
        }

        Set<chronologischeAbbildung> maps = new HashSet<chronologischeAbbildung>();

        if (T.size() == target.T.size()) {
            /* (o) is automatically fulfilled because of the good choice property
             * (Proof!?!?!) [the longest paths between neighbors always equals 1
             * and since the longest path length between the target supporters
             * has to be less or equal the path lengths between the preimage
             * supporters, fx < fy requires x < y, the other direction is by (m)
             * given, such that x < y requires fx < fy and also by surjectivity,
             * finiteness and equal cardinality of the supporter sets we have that
             * (o) already holds.
             */
            return partial_maps;
        }


        /* since (s) is already fulfilled, we choose the rest of the mapping
         * such that it is compatible with (m) and finally we check whether
         * (o) holds.
         */

        Iterator<chronologischeAbbildung> it = partial_maps.iterator();
        while (it.hasNext()) {
            chronologischeAbbildung partial = it.next();
            ArrayList<Integer> free_elements = partial.freeDomainElements();
            int free_count = free_elements.size();
            int recursion_depth = 0;
            int codomain_size = target.T.size();
            ArrayList<Integer> current_placement = new ArrayList<Integer>(free_count);
            for (int i = 0; i < free_count; ++i) {
                current_placement.add(0);
            }

            boolean step_back = false;

            while (recursion_depth >= 0) {
                int preimage = free_elements.get(recursion_depth);
                for (int image = current_placement.get(recursion_depth);
                        image < codomain_size + 1;
                        ++image) {
                    if (image == codomain_size) {
                        step_back = true;
                    } else {
                        partial.map.put(preimage, image);
                        if (partial.isPartialWeaklyMonotone(preimage)) {
                            recursion_depth++;
                            if (recursion_depth == free_count) {
                                if (partial.isPartialTargetOrderDefining()) {

                                    maps.add(partial.mapCopy());

                                    if (one_is_enough) {
                                        return maps;
                                    }
                                }
                                recursion_depth--;
                            } else {
                                current_placement.set(recursion_depth - 1, image + 1);
                                break;
                            }
                        }
                    }
                }
                if (step_back) {
                    if (recursion_depth > 0) {

                        current_placement.set(recursion_depth, 0);
                    }
                    recursion_depth--;
                    step_back = false;
                }
            }


        }

        return maps;
    }

    public Set<nTuple<Integer>> possibleNewNeighborPairs() {
        return possibleNewNeighborPairs(true);
    }

    public Set<nTuple<Integer>> possibleNewNeighborPairs(boolean smartForIsoClasses) {
        Set<nTuple<Integer>> pairs = new HashSet<nTuple<Integer>>();

        Integer size = T.size();
        if (smartForIsoClasses) {
            Integer limit = 2 * (X.getNeighborEdgeCount() + 1);
            /* If we have (n+1) edges, we can only use up to 2*(n+1) different
             * vertices. w.l.o.g. we can assume that those are the first vertices
             * of the chronologie, if we are interested in representators of iso
             * classes only.
             */
            if (size > limit) {
                size = limit;
            }
        }
        for (int l = 0; l < size; ++l) {
            for (int h = l + 1; h < size; ++h) {
                if (!X.isLess(l, h) && !X.isLess(h, l)) {
                    Set<Integer> intersection = new HashSet<Integer>(X.getFilter(h));
                    intersection.retainAll(X.getFilter(l));
                    if (intersection.isEmpty()) { //No triangles
                        pairs.add(new nTuple<Integer>(l, h));
                        pairs.add(new nTuple<Integer>(h, l));
                    }
                }

            }
        }

        return pairs;
    }

    @Override
    public String toString() {
        String rep = "Traeger: \n";
        for (int i = 0; i < T.size(); ++i) {
            rep += "   " + i + " " + T.get(i) + "\n";
        }
        rep += "Neighbors: \n";
        int count = 0;
        Iterator<nTuple<Integer>> it = X.getNeighbors().iterator();
        while (it.hasNext()) {
            nTuple<Integer> pair = it.next();
            if (count > 0) {
                if (count % 6 == 0) {
                    rep += "\n";
                } else {
                    rep += "   ";
                }
            }
            count++;
            rep += pair.get(0) + "->" + pair.get(1);
        }
        return rep;
    }

    public String getDotCode() {
        return getDotCode(true, "v", "", "");
    }

    public String getDotCode(boolean with_headers, String node_prefix, String node_ops, String arrow_ops) {
        String dot = "";
        if (with_headers) {
            dot += "digraph g {\n";

        }

        for (int i = 0; i < T.size(); ++i) {
            dot += node_prefix + i + "[label=\"\" shape=\"point\" " + node_ops + "];\n";
        }

        Iterator<nTuple<Integer>> it = X.getNeighbors().iterator();
        while (it.hasNext()) {
            nTuple<Integer> edge = it.next();
            if (arrow_ops.length() > 0) {
                dot += node_prefix + edge.get(0) + " -> " + node_prefix + edge.get(1) + "["+arrow_ops+"];\n";
            } else {
                dot += node_prefix + edge.get(0) + " -> " + node_prefix + edge.get(1) + ";\n";
            }
        }

        if (with_headers) {
            dot += "\n}\n";
        }

        return dot;
    }

    public String getGraphML() {
        return getGraphML(true, "v");
    }

    public String getGraphML(boolean with_headers, String node_prefix) {
        String gml = "";
        if (with_headers) {
            gml += "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\n<graphml xmlns=\"http://graphml.graphdrawing.org/xmlns\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:y=\"http://www.yworks.com/xml/graphml\" xsi:schemaLocation=\"http://graphml.graphdrawing.org/xmlns http://www.yworks.com/xml/schema/graphml/1.1/ygraphml.xsd\">\n  <!--Created by yFiles for Java 2.7-->\n  <key for=\"graphml\" id=\"d0\" yfiles.type=\"resources\"/>\n  <key attr.name=\"url\" attr.type=\"string\" for=\"node\" id=\"d1\"/>\n  <key attr.name=\"description\" attr.type=\"string\" for=\"node\" id=\"d2\"/>\n  <key for=\"node\" id=\"d3\" yfiles.type=\"nodegraphics\"/>\n  <key attr.name=\"Description\" attr.type=\"string\" for=\"graph\" id=\"d4\">\n    <default/>\n  </key>\n  <key attr.name=\"url\" attr.type=\"string\" for=\"edge\" id=\"d5\"/>\n  <key attr.name=\"description\" attr.type=\"string\" for=\"edge\" id=\"d6\"/>\n  <key for=\"edge\" id=\"d7\" yfiles.type=\"edgegraphics\"/>";
            gml += "\n<graph edgedefault=\"directed\">";
        }



        for (int i = 0; i < T.size(); ++i) {
            gml += "\n  <node id=\"" + node_prefix + i + "\">"
                    + "\n      <data key=\"d3\">\n        <y:ShapeNode>\n          <y:Geometry height=\"30.0\" width=\"30.0\" x=\"135.0\" y=\"75.0\"/>\n          <y:Fill color=\"#FFCC00\" transparent=\"false\"/>\n          <y:BorderStyle color=\"#000000\" type=\"line\" width=\"1.0\"/>\n          <y:NodeLabel alignment=\"center\" autoSizePolicy=\"content\" fontFamily=\"Dialog\" fontSize=\"12\" fontStyle=\"plain\" hasBackgroundColor=\"false\" hasLineColor=\"false\" height=\"17.962890625\" modelName=\"internal\" modelPosition=\"c\" textColor=\"#000000\" visible=\"true\" width=\"11.587890625\" x=\"9.2060546875\" y=\"6.0185546875\">"
                    + i + "</y:NodeLabel>\n          <y:Shape type=\"ellipse\"/>\n        </y:ShapeNode>\n      </data>"
                    + "\n  </node>";
        }
        Iterator<nTuple<Integer>> it = X.getNeighbors().iterator();
        while (it.hasNext()) {
            nTuple<Integer> edge = it.next();
            gml += "\n  <edge source=\"" + node_prefix + edge.get(0) + "\" target=\"" + node_prefix + edge.get(1) + "\">"
                    + "\n     <data key=\"d7\">\n        <y:QuadCurveEdge straightness=\"0.1\">\n          <y:Path sx=\"0.0\" sy=\"0.0\" tx=\"0.0\" ty=\"0.0\"/>\n          <y:LineStyle color=\"#000000\" type=\"line\" width=\"1.0\"/>\n          <y:Arrows source=\"none\" target=\"standard\"/>\n        </y:QuadCurveEdge>\n      </data>"
                    + "\n  </edge>";
        }


        if (with_headers) {
            gml += "\n</graph>";
            gml += "\n</graphml>";
        }


        return gml;
    }

    public void writeToFile(String filename) throws IOException {
        FileWriter file = new FileWriter(filename);
        file.write(getGraphML());
        file.close();
    }

    public void writeToDotFile(String filename) throws IOException {
        FileWriter file = new FileWriter(filename);
        file.write(getDotCode());
        file.close();
    }

    public static void main(String args[])
            throws java.io.IOException, java.io.FileNotFoundException {
//        System.out.println("test? " + ((new int[]{1, 2}) == (new int[]{1, 2}))); //DAMN YOUR EYES, JAVA!
//        System.out.println("equals? " + (new int[]{1, 2}).equals(new int[]{1, 2})); //EVEN MORE
//
//        zeitgeruest source = new zeitgeruest(new Object[]{"1", "2", "3", "4"});
//        source.addChain(new int[]{0, 1, 2});
//
//        zeitgeruest t1 = new zeitgeruest(new Object[]{"1/2", "3", "4"});
//        t1.addChain(new int[]{0, 1});
//        zeitgeruest t2 = new zeitgeruest(new Object[]{"1", "2/3", "4"});
//        t2.addChain(new int[]{0, 1});
//        zeitgeruest t3 = new zeitgeruest(new Object[]{"1/2/3/4"});
//
//        zeitgeruest t4 = new zeitgeruest(new Object[]{"2", "1", "3", "4"});
//        t4.addChain(new int[]{0, 1, 2});
//        zeitgeruest t5 = new zeitgeruest(new Object[]{"1", "2", "3", "4"});
//        zeitgeruest t6 = new zeitgeruest(new Object[]{"2", "3", "1/4"});
//        t6.addChain(new int[]{0, 1});
//        zeitgeruest t7 = new zeitgeruest(new Object[]{"1", "2", "3", "4", "_"});
//        t7.addChain(new int[]{0, 1, 2});
//
//        ArrayList<chronologischeAbbildung> maps = new ArrayList<chronologischeAbbildung>();
//        maps.add(new chronologischeAbbildung(source, t1));
//        maps.add(new chronologischeAbbildung(source, t2));
//        maps.add(new chronologischeAbbildung(source, t3));
//        maps.add(new chronologischeAbbildung(source, t4));
//        maps.add(new chronologischeAbbildung(source, t5));
//        maps.add(new chronologischeAbbildung(source, t6));
//        maps.add(new chronologischeAbbildung(source, t7));
//
//        maps.get(0).addMappingPairs(new int[]{0, 0, 1, 0, 2, 1, 3, 2});
//        maps.get(1).addMappingPairs(new int[]{0, 0, 1, 1, 2, 1, 3, 2});
//        maps.get(2).addMappingPairs(new int[]{0, 0, 1, 0, 2, 0, 3, 0});
//        maps.get(3).addMappingPairs(new int[]{0, 1, 1, 0, 2, 2, 3, 3});
//        maps.get(4).addMappingPairs(new int[]{0, 0, 1, 1, 2, 2, 3, 3});
//        maps.get(5).addMappingPairs(new int[]{0, 2, 1, 0, 2, 1, 3, 2});
//        maps.get(6).addMappingPairs(new int[]{0, 0, 1, 1, 2, 2, 3, 3});
//
//
//        System.out.println(maps);
//
//        System.out.println("\n\n");
//        for (int i = 0; i < 7; ++i) {
//            System.out.println("Map #" + i);
//            System.out.println("(c)? " + maps.get(i).isComplete()
//                    + " (s)? " + maps.get(i).isSurjective()
//                    + " (m)? " + maps.get(i).isPartialWeaklyMonotone()
//                    + " (o)? " + maps.get(i).isPartialTargetOrderDefining());
//        }
//
//        System.out.println("Maps between source and t1:");
//        Set<chronologischeAbbildung> allMaps = source.getAllMapsOnto(t1);
//        System.out.println(allMaps.size());
//        Iterator<chronologischeAbbildung> mit = allMaps.iterator();
//        while (mit.hasNext()) {
//            System.out.println(mit.next().isValid());
//        }
//        System.out.println(allMaps);
//
//        zeitgeruest two_plus_two = new zeitgeruest(new Object[]{"1", "2", "j", "k"});
//        two_plus_two.addPairs(new int[]{0, 1, 2, 3});
//        System.out.println(two_plus_two);
//
//        allMaps = two_plus_two.getAllMapsOnto(two_plus_two);
//        System.out.println("Endomorphisms:" + allMaps.size());
//        System.out.println(allMaps);
//
//        zeitgeruest other_two_plus_two = new zeitgeruest(new Object[]{"1", "j", "2", "k"});
//        other_two_plus_two.addPairs(new int[]{0, 2, 1, 3});
//        allMaps = two_plus_two.getAllMapsOnto(other_two_plus_two);
//        System.out.println(allMaps);
//
//        zeitgeruest c3 = new zeitgeruest(3);
//        c3.addChain(new int[]{0, 1, 2});
//        System.out.println("3: " + c3);
//
//        System.out.println("Maps between 2+2 and 3:");
//        System.out.println(two_plus_two.getAllMapsOnto(c3));
//
//        System.out.println("Is isomorphic? " + other_two_plus_two.isIsomorphicTo(two_plus_two));
//        System.out.println(two_plus_two);
//        System.out.println("New neighbors? " + two_plus_two.possibleNewNeighborPairs());
//
//        Set<zeitgeruest> s = new HashSet<zeitgeruest>();
//        s.add(two_plus_two);
//        s = zeitgeruest.getNextLevelOfIsoClassRepresentations(s);
//        System.out.println("#next level = " + s.size());
//        s = zeitgeruest.getNextLevelOfIsoClassRepresentations(s);
//        System.out.println("#next level = " + s.size());
//        s = zeitgeruest.getNextLevelOfIsoClassRepresentations(s);
//        System.out.println("#next level = " + s.size());
//        s = zeitgeruest.getNextLevelOfIsoClassRepresentations(s);
//        System.out.println("#next level = " + s.size());
//        s = zeitgeruest.getNextLevelOfIsoClassRepresentations(s);
//        System.out.println("#next level = " + s.size());
//
//        for (int n = 0; n < 8; ++n) {
//            zeitgeruest discrete = new zeitgeruest(n);
//            Set<zeitgeruest> classes = new HashSet<zeitgeruest>();
//            classes.add(discrete);
//            int edges = 0;
//
//            (new File("/tmp/zeitgerueste/" + n + "_elements/" + edges + "_edges")).mkdirs();
//            discrete.writeToDotFile("/tmp/zeitgerueste/" + n + "_elements/" + edges + "_edges/" + n + "v0e_1.dot");
//            while (!classes.isEmpty()) {
//                ++edges;
//                classes = zeitgeruest.getNextLevelOfIsoClassRepresentations(classes);
//                if (!classes.isEmpty()) {
//                    System.out.println(n + " vertices " + edges + " edges: " + classes.size());
//                    (new File("/tmp/zeitgerueste/" + n + "_elements/" + edges + "_edges")).mkdirs();
//                    int count = 0;
//                    Iterator<zeitgeruest> it = classes.iterator();
//                    while (it.hasNext()) {
//                        count++;
//                        it.next().writeToDotFile("/tmp/zeitgerueste/" + n + "_elements/"
//                                + edges + "_edges/" + n + "v" + edges + "e_" + count + ".dot");
//                    }
//
//                }
//            }
//        }
        Set<zeitgeruest> z = new HashSet<zeitgeruest>();
        Set<zeitgeruest> s = new HashSet<zeitgeruest>();
        Set<chronologischeAbbildung> maps = new TreeSet<chronologischeAbbildung>();
        s.add(new zeitgeruest(2));
        s.add(new zeitgeruest(3));
        s.add(new zeitgeruest(4));
        s.add(new zeitgeruest(5));

        z.addAll(s);
        while (!s.isEmpty()) {
            s = zeitgeruest.getNextLevelOfIsoClassRepresentations(s);
            z.addAll(s);
        }
        System.out.println("#zeitgeruests = " + z.size());

        Map<zeitgeruest,Set<chronologischeAbbildung>> automorphisms = new HashMap<zeitgeruest,Set<chronologischeAbbildung>>();

        Iterator<zeitgeruest> it = z.iterator();
        while (it.hasNext()) {
            zeitgeruest source = it.next();
            automorphisms.put(source, source.getAllMapsOnto(source));
            System.out.print("["+automorphisms.get(source).size()+"] ");
        }
        System.out.println("");

        it = z.iterator();
        while (it.hasNext()) {
            zeitgeruest source = it.next();
            Iterator<zeitgeruest> jt = z.iterator();
            while (jt.hasNext()) {
                zeitgeruest target = jt.next();
                if (target.T.size() < source.T.size()) {
                    maps.addAll(source.getAllMapsOnto(target));
                }
            }
        }
        System.out.println("#maps = " + maps.size());
        
        Set<chronologischeAbbildung> map_classes = new TreeSet<chronologischeAbbildung>();
        Iterator<chronologischeAbbildung> mt = maps.iterator();
        while (mt.hasNext()) {
            chronologischeAbbildung m = mt.next();
            Iterator<chronologischeAbbildung> ct = map_classes.iterator();
            boolean new_class = true;
            while (ct.hasNext()) {
                chronologischeAbbildung c = ct.next();
                if ((c.source == m.source)&&(c.target == m.target)) {
                    if (m.isEquivalentToByIsoSets(automorphisms.get(m.source), automorphisms.get(m.target), c)) {
                        new_class = false;
                        break;
                    }
                }
            }
            if (new_class) {
                map_classes.add(m);
            }
        }

        System.out.println("#classes = " + map_classes.size());

        (new File("/tmp/chronologischeAbbildungen")).mkdirs();
        mt = map_classes.iterator();
        Integer nbr = 0;
        while (mt.hasNext()) {
            chronologischeAbbildung m = mt.next();
            nbr++;
            String filename = "/tmp/chronologischeAbbildungen/map_class_"+String.format("%06d",nbr)+".dot";
            System.out.println(filename);
            FileWriter file = new FileWriter(filename);
            file.write(m.getDotCode());
            file.close();
        }
    }
}
