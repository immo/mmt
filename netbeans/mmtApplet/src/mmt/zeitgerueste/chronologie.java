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
public class chronologie implements Comparable {

    protected Set<intPair> relation;
    protected Set<intPair> neighborhood_relation;
    protected Map<Integer, Set<Integer>> ideals;
    protected Map<Integer, Set<Integer>> filters;
    protected Map<Integer, Set<Integer>> upper_neighbors;
    protected Map<Integer, Set<Integer>> lower_neighbors;
    protected Map<intPair, Integer> longest_up_path;
    protected boolean longest_paths_available;

    public chronologie() {
        relation = new TreeSet<intPair>();

        closeRelation();
    }

    public int compareTo(Object o) {
        chronologie other = (chronologie) o;
        Set<intPair> meetSet = new TreeSet<intPair>();
        Set<intPair> deltaSet = new TreeSet<intPair>();
        deltaSet.addAll(neighborhood_relation);
        deltaSet.addAll(other.neighborhood_relation);
        meetSet.addAll(neighborhood_relation);
        meetSet.retainAll(other.neighborhood_relation);
        deltaSet.removeAll(meetSet);
        if (deltaSet.isEmpty()) {
            return 0;
        } else {
            intPair first_difference = deltaSet.iterator().next();
            if (neighborhood_relation.contains(first_difference)) {
                return 1;
            }
            return -1;
        }
    }

    public boolean hasIntervalProperty() {
        Iterator<intPair> it = neighborhood_relation.iterator();
        while (it.hasNext()) {
            intPair a = it.next();
            Iterator<intPair> it2=neighborhood_relation.iterator();
            while (it2.hasNext()) {
                intPair b = it2.next();
                if (a.compareTo(b)<=0) {
                    int s = a.get(0);
                    int t = a.get(1);
                    int p = b.get(0);
                    int q = b.get(1);
                    if (!(isLess(s,q)||isLess(p,t))) {
                        return false; //counterexample!!
                    }
                }
            }
        }

        return true;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final chronologie other = (chronologie) obj;
        if (this.neighborhood_relation != other.neighborhood_relation && (this.neighborhood_relation == null)) {
            return false;
        }

        if (!this.neighborhood_relation.equals(other.neighborhood_relation)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 97 * hash + (this.neighborhood_relation != null ? this.neighborhood_relation.hashCode() : 0);
        return hash;
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        chronologie c = new chronologie();
        /// DOESNT WORK LIKE THAT: c.relation = (Set<intPair>) this.relation.clone();

        Iterator<intPair> it = relation.iterator();
        while (it.hasNext()) {
            c.relation.add((intPair) it.next().clone());
        }

        it = neighborhood_relation.iterator();
        while (it.hasNext()) {
            c.neighborhood_relation.add((intPair) it.next().clone());
        }

        Iterator<Integer> i = ideals.keySet().iterator();
        while (i.hasNext()) {
            Integer s = i.next();
            c.ideals.put(s, new TreeSet<Integer>(ideals.get(s)));
        }

        i = filters.keySet().iterator();
        while (i.hasNext()) {
            Integer s = i.next();
            c.filters.put(s, new TreeSet<Integer>(filters.get(s)));
        }

        i = upper_neighbors.keySet().iterator();
        while (i.hasNext()) {
            Integer s = i.next();
            c.upper_neighbors.put(s, new TreeSet<Integer>(upper_neighbors.get(s)));
        }

        i = lower_neighbors.keySet().iterator();
        while (i.hasNext()) {
            Integer s = i.next();
            c.lower_neighbors.put(s, new TreeSet<Integer>(lower_neighbors.get(s)));
        }

        it = longest_up_path.keySet().iterator();
        while (it.hasNext()) {
            intPair key = it.next();
            c.longest_up_path.put((intPair) key.clone(), longest_up_path.get(key));
        }

        return c;
    }

    public Integer getNeighborEdgeCount() {
        return neighborhood_relation.size();
    }

    public Integer getComparableCount(int x) {
        return this.getFilter(x).size() + this.getIdeal(x).size();
    }

    public Integer getLongestUpPathLength(int x, int y) {
        if (!this.longest_paths_available) {
            throw new UnsupportedOperationException();
        } else {
            intPair t = new intPair(x, y);
            if (longest_up_path.containsKey(t)) {
                return longest_up_path.get(t);
            }
            return 0;
        }
    }

    public boolean isLess(int x, int y) {
        return relation.contains(new intPair(x, y));
    }

    public boolean isLess(intPair pair) {
        return relation.contains(pair);
    }

    public boolean isLowerNeighbor(int x, int y) {
        return neighborhood_relation.contains(new intPair(x, y));
    }

    public boolean isLowerNeighbor(intPair pair) {
        return neighborhood_relation.contains(pair);
    }

    public boolean isParallel(int x, int y) {
        if (x == y) {
            return false;
        }
        return !(isLess(x, y) || isLess(y, x));
    }

    public Set<Integer> getFilter(int x) {
        if (!this.filters.containsKey(x)) {
            this.filters.put(x, new HashSet<Integer>());
            this.filters.get(x).add(x);
        }
        return this.filters.get(x);
    }

    public Set<Integer> getIdeal(int x) {
        if (!this.ideals.containsKey(x)) {
            this.ideals.put(x, new HashSet<Integer>());
            this.ideals.get(x).add(x);
        }
        return this.ideals.get(x);
    }

    public Set<intPair> getNeighbors() {
        return this.neighborhood_relation;
    }

    public Set<Integer> getUpperNeighbors(int x) {
        if (this.upper_neighbors.containsKey(x)) {
            return this.upper_neighbors.get(x);
        } else {
            return new TreeSet<Integer>();
        }
    }

    public Set<Integer> getLowerNeighbors(int x) {
        if (this.lower_neighbors.containsKey(x)) {
            return this.lower_neighbors.get(x);
        } else {
            return new TreeSet<Integer>();
        }
    }

    public boolean addPair(int x, int y) {
        if (isLess(y, x) || (x == y)) {
            return false;
        }
        if (!isLess(x, y)) {
            relation = neighborhood_relation;
            relation.add(new intPair(x, y));
            closeRelation();
        }
        return true;
    }

    public boolean addChain(Iterable<Integer> chain) {
        boolean calculateClosure = false;
        Iterator<Integer> it = chain.iterator();
        if (it.hasNext()) {
            int x = it.next();
            while (it.hasNext()) {
                int y = it.next();
                if ((x != y) && (!isLess(x, y))) {
                    calculateClosure = true;
                    neighborhood_relation.add(new intPair(x, y));
                }
                x = y;
            }
        }
        if (calculateClosure) {
            relation = neighborhood_relation;
            closeRelation();
        }
        return calculateClosure;
    }

    public boolean addPairs(Set<intPair> pairs) {
        boolean calculateClosure = false;
        Iterator<intPair> it = pairs.iterator();
        while (it.hasNext()) {
            intPair pair = it.next();
            int x = pair.get(0);
            int y = pair.get(1);
            if ((x != y) && (!isLess(x, y))) {
                calculateClosure = true;
                neighborhood_relation.add(pair);
            }
        }
        if (calculateClosure) {
            relation = neighborhood_relation;
            closeRelation();
        }

        return calculateClosure;
    }

    public boolean removePair(int x, int y) {
        if (!isLess(x, y)) {
            return false;
        }

        Iterator<intPair> it = neighborhood_relation.iterator();
        while (it.hasNext()) {
            intPair pair = it.next();
            int s = pair.get(0);
            int t = pair.get(1);

            if ((t == y) && isLess(x, s)) {
                it.remove();
            }
        }
        relation = neighborhood_relation;
        closeRelation();
        return true;
    }

    public void closeRelation() {
        Set<intPair> closure = new TreeSet<intPair>();
        Set<intPair> neighborhood = new TreeSet<intPair>();
        Map<Integer, Set<Integer>> ideals = new HashMap<Integer, Set<Integer>>();
        Map<Integer, Set<Integer>> filters = new HashMap<Integer, Set<Integer>>();
        Map<Integer, Set<Integer>> non_neighbors = new HashMap<Integer, Set<Integer>>();

        this.ideals = new HashMap<Integer, Set<Integer>>();
        this.filters = new HashMap<Integer, Set<Integer>>();


        Iterator<intPair> it = relation.iterator();
        while (it.hasNext()) {
            intPair pair = it.next();
            int s = pair.get(0);
            int t = pair.get(1);
            if (!filters.containsKey(t)) {
                filters.put(t, new TreeSet<Integer>());
                filters.get(t).add(t);
            }
            if (!filters.containsKey(s)) {
                filters.put(s, new TreeSet<Integer>());
                filters.get(s).add(s);
            }
            if (!ideals.containsKey(t)) {
                ideals.put(t, new TreeSet<Integer>());
                ideals.get(t).add(t);
            }
            if (!ideals.containsKey(s)) {
                ideals.put(s, new TreeSet<Integer>());
                ideals.get(s).add(s);
            }

            if (!filters.get(s).contains(t)) {
                Iterator<Integer> sit = ideals.get(s).iterator();
                while (sit.hasNext()) {
                    Integer below_s = sit.next();
                    filters.get(below_s).addAll(filters.get(t));
                }
                Iterator<Integer> tit = filters.get(t).iterator();
                while (tit.hasNext()) {
                    Integer above_t = tit.next();
                    ideals.get(above_t).addAll(ideals.get(s));
                }
            }
        }

        Iterator<Integer> fit = filters.keySet().iterator();
        while (fit.hasNext()) {
            Integer s = fit.next();
            this.filters.put(s, new TreeSet<Integer>());
            Iterator<Integer> git = filters.get(s).iterator();
            while (git.hasNext()) {
                Integer t = git.next();
                this.filters.get(s).add(t);
            }
        }

        Iterator<Integer> iit = ideals.keySet().iterator();
        while (iit.hasNext()) {
            Integer s = iit.next();
            this.ideals.put(s, new TreeSet<Integer>());
            Iterator<Integer> git = ideals.get(s).iterator();
            while (git.hasNext()) {
                Integer t = git.next();
                this.ideals.get(s).add(t);
            }
        }

        fit = filters.keySet().iterator();
        while (fit.hasNext()) {
            Integer s = fit.next();
            Iterator<Integer> git = filters.get(s).iterator();
            while (git.hasNext()) {
                Integer t = git.next();
                closure.add(new intPair(s, t));
            }
        }

        it = closure.iterator();
        while (it.hasNext()) {
            intPair pair = it.next();
            if (pair.inDelta()) {
                it.remove();
            }
        }

        fit = filters.keySet().iterator();
        while (fit.hasNext()) {
            Integer s = fit.next();
            filters.get(s).remove(s);
            non_neighbors.put(s, new HashSet());
        }

        fit = filters.keySet().iterator();
        while (fit.hasNext()) {
            Integer s = fit.next();
            Iterator<Integer> ups = filters.get(s).iterator();
            while (ups.hasNext()) {
                Integer t = ups.next();
                non_neighbors.get(s).addAll(filters.get(t));
            }
        }

        fit = filters.keySet().iterator();
        while (fit.hasNext()) {
            Integer s = fit.next();
            filters.get(s).removeAll(non_neighbors.get(s));
        }

        this.lower_neighbors = new HashMap<Integer, Set<Integer>>();

        fit = filters.keySet().iterator();
        while (fit.hasNext()) {
            Integer s = fit.next();
            this.lower_neighbors.put(s, new TreeSet<Integer>());
        }

        fit = filters.keySet().iterator();
        while (fit.hasNext()) {
            Integer s = fit.next();

            Iterator<Integer> git = filters.get(s).iterator();
            while (git.hasNext()) {
                Integer t = git.next();
                neighborhood.add(new intPair(s, t));
                this.lower_neighbors.get(t).add(s);
            }
        }

        this.upper_neighbors = filters;

        this.neighborhood_relation = neighborhood;
        this.relation = closure;



        this.longest_up_path = new TreeMap<intPair, Integer>();

        /* better than below?? */

        Iterator<intPair> pit = this.neighborhood_relation.iterator();
        while (pit.hasNext()) {
            intPair pair = pit.next();

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

        this.longest_paths_available = true;

        /* this may not be the smartes way to calculate the longest-up-paths */

//
//        Iterator<Integer> sit = this.filters.keySet().iterator();
//        while (sit.hasNext()) {
//            Integer s = sit.next();
//            Iterator<Integer> tit = this.filters.get(s).iterator();
//            while (tit.hasNext()) {
//                Integer t = tit.next();
//                if (!s.equals(t)) {
//                    this.longest_up_path.put(new intPair(s, t), 1);
//                }
//            }
//        }
//
//        boolean checkAgain = true;
//
//        while (checkAgain) {
//            checkAgain = false;
//            sit = this.filters.keySet().iterator();
//            while (sit.hasNext()) {
//                Integer s = sit.next();
//                Set<Integer> sf = this.filters.get(s);
//                Iterator<intPair> current = this.longest_up_path.keySet().iterator();
//                while (current.hasNext()) {
//                    intPair pair = current.next();
//                    if ((pair.get(0) != s) && sf.contains(pair.get(0))) {
//                        Integer lengthVia = 1 + this.longest_up_path.get(pair);
//                        intPair via = new intPair(s, pair.get(1));
//                        if (lengthVia > this.longest_up_path.get(via)) {
//                            this.longest_up_path.put(via, lengthVia);
//                            checkAgain = true;
//
//                        }
//                    }
//                }
//            }
//        }

    }

    public static void main(String args[])
            throws java.io.IOException, java.io.FileNotFoundException {
        System.out.println("Testing chronology class...");

        chronologie c = new chronologie();
        c.addPair(0, 1);
        c.addPair(1, 2);
        c.addPair(0, 3);
        c.addPair(0, 4);
        c.addPair(-2, -1);
        c.addPair(-4, -1);
        c.addPair(-1, 1);

        System.out.println("Relation pairs:");
        Iterator<intPair> it = c.relation.iterator();
        while (it.hasNext()) {
            intPair pair = it.next();
            System.out.println(pair);
        }

        System.out.println("Neighbor pairs:");
        it = c.neighborhood_relation.iterator();
        while (it.hasNext()) {
            intPair pair = it.next();
            System.out.println(pair);
        }

        System.out.println("Adding (-4,-2)...");
        c.addPair(-4, -2);

        System.out.println("Neighbor pairs:");
        it = c.neighborhood_relation.iterator();
        while (it.hasNext()) {
            intPair pair = it.next();
            System.out.println(pair);
        }


        System.out.println("below 0: " + c.ideals.get(0));
        System.out.println("above 0: " + c.filters.get(0));

        System.out.println("below 1: " + c.ideals.get(1));
        System.out.println("above 1: " + c.filters.get(1));

        System.out.println("Up-path lengths: " + c.longest_up_path);
        System.out.println("lower: " + c.lower_neighbors);
        System.out.println("upper: " + c.upper_neighbors);
        System.out.println("upper-neighbors of 0: " + c.getUpperNeighbors(0));
        System.out.println("lower-neighbors of 3: " + c.getLowerNeighbors(3));

        chronologie c2 = null;
        try {
            c2 = (chronologie) c.clone();
        } catch (CloneNotSupportedException ex) {
            Logger.getLogger(chronologie.class.getName()).log(Level.SEVERE, null, ex);
        }

        System.out.println("cloning: " + (c2.equals(c)));

        System.out.println(c.neighborhood_relation);
        System.out.println(c2.neighborhood_relation);

    }
}
