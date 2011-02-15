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
public class intervallChronologie extends chronologie {

    ArrayList start_points, end_points;

    public intervallChronologie(ArrayList a, ArrayList b, boolean keepListRefs) {
        this.initChronologie(a, b, keepListRefs);
    }

    public intervallChronologie(ArrayList a, ArrayList b) {
        this.initChronologie(a, b, false);
    }

    private void initChronologie(ArrayList a, ArrayList b, boolean keepListRefs) {
        if (keepListRefs) {
            this.start_points = a;
            this.end_points = b;
        } else {
            this.start_points = null;
            this.end_points = null;
        }
        /* i < j iff b(i).compareTo(a(j)) <= 0 */
        TreeMap<nTuple<Comparable>, Set<Integer>> map = new TreeMap<nTuple<Comparable>, Set<Integer>>();

        for (int i = 0; i < a.size(); ++i) {
            nTuple<Comparable> tuple = new nTuple(a.get(i), b.get(i));
            if (!map.containsKey(tuple)) {
                map.put(tuple, new TreeSet<Integer>());
            }
            map.get(tuple).add(i);
        }

        Iterator<nTuple<Comparable>> it = map.keySet().iterator();

        while (it.hasNext()) {
            nTuple<Comparable> left = it.next();
            Set<Integer> l = map.get(left);
            Set<Integer> left_filter = new TreeSet<Integer>();


            Iterator<nTuple<Comparable>> jt = map.keySet().iterator();
            while (jt.hasNext()) {
                nTuple<Comparable> right = jt.next();
                if (left.get(1).compareTo(right.get(0)) <= 0) {
                    Set<Integer> r = map.get(right);
                    Iterator<Integer> li = l.iterator();
                    while (li.hasNext()) {
                        Iterator<Integer> ri = r.iterator();
                        Integer lint = li.next();
                        while (ri.hasNext()) {
                            this.relation.add(new intPair(lint, ri.next()));
                        }
                    }

                    left_filter.addAll(r);
                    Iterator<Integer> ri = r.iterator();
                    while (ri.hasNext()) {
                        Integer rint = ri.next();
                        if (!this.ideals.containsKey(rint)) {
                            this.ideals.put(rint, new TreeSet<Integer>());
                        }
                        this.ideals.get(rint).addAll(l);
                    }
                    /* check neighbors */
                    Iterator<nTuple<Comparable>> kt = map.keySet().iterator();
                    boolean neighbor = true;
                    while (kt.hasNext()) {
                        nTuple<Comparable> mid = kt.next();
                        if (left.get(1).compareTo(mid.get(0)) <= 0) {
                            if (mid.get(1).compareTo(right.get(0)) <= 0) {
                                neighbor = false;
                                break;
                            } else { //we are already right of right
                                break;
                            }
                        }

                    }
                    if (neighbor) {

                        li = l.iterator();
                        while (li.hasNext()) {
                            ri = r.iterator();
                            Integer lint = li.next();
                            while (ri.hasNext()) {
                                this.neighborhood_relation.add(new intPair(lint, ri.next()));
                            }
                        }
                    }


                }
            }
            Iterator<Integer> li = l.iterator();
            while (li.hasNext()) {
                Integer lint = li.next();
                this.filters.put(lint, left_filter);
            }

        }

        this.longest_up_path = new TreeMap<intPair, Integer>();

        if (this.neighborhood_relation.size() > 1000) {
            this.longest_paths_available = false;

        } else {
            this.longest_paths_available = true;


            Iterator<intPair> pit = this.neighborhood_relation.iterator();
            while (pit.hasNext()) {
                intPair pair = pit.next();
                Integer l = pair.get(0);
                Integer h = pair.get(1);
                if (!this.upper_neighbors.containsKey(l)) {
                    this.upper_neighbors.put(l, new TreeSet<Integer>());
                }
                if (!this.lower_neighbors.containsKey(h)) {
                    this.lower_neighbors.put(h, new TreeSet<Integer>());
                }
                this.upper_neighbors.get(l).add(h);
                this.lower_neighbors.get(h).add(l);
                this.longest_up_path.put(pair, 1);
            }

            Set<intPair> got_better = new TreeSet<intPair>(this.neighborhood_relation);


            while (!got_better.isEmpty()) {
                Set<intPair> keyset = new TreeSet<intPair>(got_better);
                got_better = new TreeSet<intPair>();

                pit = keyset.iterator();
                while (pit.hasNext()) {
                    intPair pair = pit.next();
                    Integer l = pair.get(0);
                    Integer h = pair.get(1);
                    Integer length = this.longest_up_path.get(pair);
                    if (!this.upper_neighbors.containsKey(h)) {
                        continue;
                    }
                    Iterator<Integer> nit = this.upper_neighbors.get(h).iterator();
                    while (nit.hasNext()) {
                        Integer hh = nit.next();
                        intPair npair = new intPair(l, hh);
                        if (!this.longest_up_path.containsKey(npair)) {
                            this.longest_up_path.put(npair, length + 1);
                            got_better.add(npair);
                        } else {
                            if (this.longest_up_path.get(npair) <= length) {
                                this.longest_up_path.put(npair, length + 1);
                                got_better.add(npair);

                            }
                        }
                    }
                }
            }
        }

    }

    public void writeEndpointFile(String filename) throws IOException {
        if (start_points == null) {
            return;
        }

        FileWriter file = new FileWriter(filename);
        file.write("{ ");
        int count = start_points.size();
        for (int i=0;i<count;++i) {
            file.write("'v"+i+"': ("+start_points.get(i)+", "+end_points.get(i)+"),\n");
        }
        file.write("}\n");

        file.close();
    }

    public static void main(String args[])
            throws java.io.IOException, java.io.FileNotFoundException {
        System.out.println((new Integer(3)).compareTo(new Integer(3)));
        ArrayList<Integer> a = new ArrayList<Integer>();
        ArrayList<Integer> b = new ArrayList<Integer>();
        a.add(0);
        b.add(1);
        a.add(1);
        b.add(2);
        a.add(1);
        b.add(3);
        a.add(4);
        b.add(5);
        intervallChronologie iz = new intervallChronologie(a, b);
        System.out.println(iz.neighborhood_relation);
        System.out.println(iz.longest_up_path);
    }
}
