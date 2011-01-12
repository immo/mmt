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
public class zeitgeruest {

    ArrayList<traeger> T;
    chronologie X;

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

    public boolean addChain(int[] chain) {
        ArrayList<Integer> list = new ArrayList<Integer>(chain.length);

        for (int i = 0; i < chain.length; ++i) {
            list.add(chain[i]);
        }

        return this.X.addChain(list);
    }

    public Set<chronologischeAbbildung> getAllMapsOnto(zeitgeruest target) {
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
            System.out.println(current_placement);
            boolean step_back = false;


        }

        return maps;
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

    public static void main(String args[])
            throws java.io.IOException, java.io.FileNotFoundException {
        System.out.println("test? " + ((new int[]{1, 2}) == (new int[]{1, 2}))); //DAMN YOUR EYES, JAVA!
        System.out.println("equals? " + (new int[]{1, 2}).equals(new int[]{1, 2})); //EVEN MORE

        zeitgeruest source = new zeitgeruest(new Object[]{"1", "2", "3", "4"});
        source.addChain(new int[]{0, 1, 2});

        zeitgeruest t1 = new zeitgeruest(new Object[]{"1/2", "3", "4"});
        t1.addChain(new int[]{0, 1});
        zeitgeruest t2 = new zeitgeruest(new Object[]{"1", "2/3", "4"});
        t2.addChain(new int[]{0, 1});
        zeitgeruest t3 = new zeitgeruest(new Object[]{"1/2/3/4"});

        zeitgeruest t4 = new zeitgeruest(new Object[]{"2", "1", "3", "4"});
        t4.addChain(new int[]{0, 1, 2});
        zeitgeruest t5 = new zeitgeruest(new Object[]{"1", "2", "3", "4"});
        zeitgeruest t6 = new zeitgeruest(new Object[]{"2", "3", "1/4"});
        t6.addChain(new int[]{0, 1});
        zeitgeruest t7 = new zeitgeruest(new Object[]{"1", "2", "3", "4", "_"});
        t7.addChain(new int[]{0, 1, 2});

        ArrayList<chronologischeAbbildung> maps = new ArrayList<chronologischeAbbildung>();
        maps.add(new chronologischeAbbildung(source, t1));
        maps.add(new chronologischeAbbildung(source, t2));
        maps.add(new chronologischeAbbildung(source, t3));
        maps.add(new chronologischeAbbildung(source, t4));
        maps.add(new chronologischeAbbildung(source, t5));
        maps.add(new chronologischeAbbildung(source, t6));
        maps.add(new chronologischeAbbildung(source, t7));

        maps.get(0).addMappingPairs(new int[]{0, 0, 1, 0, 2, 1, 3, 2});
        maps.get(1).addMappingPairs(new int[]{0, 0, 1, 1, 2, 1, 3, 2});
        maps.get(2).addMappingPairs(new int[]{0, 0, 1, 0, 2, 0, 3, 0});
        maps.get(3).addMappingPairs(new int[]{0, 1, 1, 0, 2, 2, 3, 3});
        maps.get(4).addMappingPairs(new int[]{0, 0, 1, 1, 2, 2, 3, 3});
        maps.get(5).addMappingPairs(new int[]{0, 2, 1, 0, 2, 1, 3, 2});
        maps.get(6).addMappingPairs(new int[]{0, 0, 1, 1, 2, 2, 3, 3});


        System.out.println(maps);

        System.out.println("\n\n");
        for (int i = 0; i < 7; ++i) {
            System.out.println("Map #" + i);
            System.out.println("(c)? " + maps.get(i).isComplete()
                    + " (s)? " + maps.get(i).isSurjective()
                    + " (m)? " + maps.get(i).isPartialWeaklyMonotone()
                    + " (o)? " + maps.get(i).isPartialTargetOrderDefining());
        }

        System.out.println("Maps between source and t1:");
        Set<chronologischeAbbildung> allMaps = source.getAllMapsOnto(t1);
        System.out.println(allMaps.size());
        Iterator<chronologischeAbbildung> mit = allMaps.iterator();
        while (mit.hasNext()) {
            System.out.println(mit.next().isPartialWeaklyMonotone());
        }

        System.out.println("Maps between source and source:");
        System.out.println(t5.getAllMapsOnto(t5));

        System.out.println(source.X.getLongestUpPathLength(0, 1));


    }
}
