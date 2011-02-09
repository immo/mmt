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
public class intervalChronologie extends chronologie {

    public intervalChronologie(ArrayList a, ArrayList b) {
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
                            this.relation.add(new nTuple<Integer>(lint, ri.next()));
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
                                this.neighborhood_relation.add(new nTuple<Integer>(lint, ri.next()));
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

        this.longest_up_path = new TreeMap<nTuple<Integer>,Integer>();

        Iterator<nTuple<Integer>> pit = this.neighborhood_relation.iterator();
        while (pit.hasNext()) {
            nTuple<Integer> pair = pit.next();
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

        Set<nTuple<Integer>> got_better = new TreeSet<nTuple<Integer>>(this.neighborhood_relation);

        
        while (!got_better.isEmpty()) {
            Set<nTuple<Integer>> keyset = new TreeSet<nTuple<Integer>>
                        (got_better);
            got_better = new TreeSet<nTuple<Integer>>();
            
            pit = keyset.iterator();
            while (pit.hasNext()) {
                nTuple<Integer> pair = pit.next();
                Integer l = pair.get(0);
                Integer h = pair.get(1);
                Integer length = this.longest_up_path.get(pair);
                if (!this.upper_neighbors.containsKey(h)) continue;
                Iterator<Integer> nit = this.upper_neighbors.get(h).iterator();
                while (nit.hasNext()) {
                    Integer hh = nit.next();
                    nTuple<Integer> npair = new nTuple<Integer>(l,hh);
                    if (!this.longest_up_path.containsKey(npair)) {
                        this.longest_up_path.put(npair,length+1);
                        got_better.add(npair);
                    } else {
                        if (this.longest_up_path.get(npair)<=length) {
                            this.longest_up_path.put(npair,length+1);
                            got_better.add(npair);

                        }
                    }
                }                
            }
        }






//    private Map<Integer, Set<Integer>> upper_neighbors;
//    private Map<Integer, Set<Integer>> lower_neighbors;
//    private Map<nTuple<Integer>, Integer> longest_up_path;
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
        intervalChronologie iz = new intervalChronologie(a, b);
        System.out.println(iz.neighborhood_relation);
        System.out.println(iz.longest_up_path);
    }
}
