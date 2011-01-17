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
public class chronologie {

    private Set<nTuple<Integer>> relation;
    private Set<nTuple<Integer>> neighborhood_relation;
    private Map<Integer, Set<Integer>> ideals;
    private Map<Integer, Set<Integer>> filters;
    private Map<Integer, Set<Integer>> upper_neighbors;
    private Map<Integer, Set<Integer>> lower_neighbors;
    private Map<nTuple<Integer>, Integer> longest_up_path;

    public chronologie() {
        relation = new HashSet<nTuple<Integer>>();

        closeRelation();
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
        
        if (! this.neighborhood_relation.equals(other.neighborhood_relation)){
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
        /// DOESNT WORK LIKE THAT: c.relation = (Set<nTuple<Integer>>) this.relation.clone();

        Iterator<nTuple<Integer>> it = relation.iterator();
        while (it.hasNext()) {
            c.relation.add((nTuple<Integer>) it.next().clone());
        }

        it = neighborhood_relation.iterator();
        while (it.hasNext()) {
            c.neighborhood_relation.add((nTuple<Integer>) it.next().clone());
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
            nTuple<Integer> key = it.next();
            c.longest_up_path.put((nTuple<Integer>)key.clone(), longest_up_path.get(key));
        }

        return c;
    }

    

    public Integer getLongestUpPathLength(int x, int y) {
        nTuple<Integer> t = new nTuple<Integer>(x, y);
        if (longest_up_path.containsKey(t)) {
            return longest_up_path.get(t);
        }
        return 0;
    }

    public boolean isLess(int x, int y) {
        return relation.contains(new nTuple<Integer>(x, y));
    }

    public boolean isLess(nTuple<Integer> pair) {
        return relation.contains(pair);
    }

    public boolean isLowerNeighbor(int x, int y) {
        return neighborhood_relation.contains(new nTuple<Integer>(x, y));
    }

    public boolean isLowerNeighbor(nTuple<Integer> pair) {
        return neighborhood_relation.contains(pair);
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

    public Set<nTuple<Integer>> getNeighbors() {
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
            relation.add(new nTuple<Integer>(x, y));
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
                    neighborhood_relation.add(new nTuple<Integer>(x, y));
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

    public boolean addPairs(Set<nTuple<Integer>> pairs) {
        boolean calculateClosure = false;
        Iterator<nTuple<Integer>> it = pairs.iterator();
        while (it.hasNext()) {
            nTuple<Integer> pair = it.next();
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

        Iterator<nTuple<Integer>> it = neighborhood_relation.iterator();
        while (it.hasNext()) {
            nTuple<Integer> pair = it.next();
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
        Set<nTuple<Integer>> closure = new HashSet<nTuple<Integer>>();
        Set<nTuple<Integer>> neighborhood = new HashSet<nTuple<Integer>>();
        Map<Integer, Set<Integer>> ideals = new HashMap<Integer, Set<Integer>>();
        Map<Integer, Set<Integer>> filters = new HashMap<Integer, Set<Integer>>();
        Map<Integer, Set<Integer>> non_neighbors = new HashMap<Integer, Set<Integer>>();

        this.ideals = new HashMap<Integer, Set<Integer>>();
        this.filters = new HashMap<Integer, Set<Integer>>();


        Iterator<nTuple<Integer>> it = relation.iterator();
        while (it.hasNext()) {
            nTuple<Integer> pair = it.next();
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
                closure.add(new nTuple<Integer>(s, t));
            }
        }

        it = closure.iterator();
        while (it.hasNext()) {
            nTuple<Integer> pair = it.next();
            if (pair.get(0).equals(pair.get(1))) {
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
                neighborhood.add(new nTuple<Integer>(s, t));
                this.lower_neighbors.get(t).add(s);
            }
        }

        this.upper_neighbors = filters;

        this.neighborhood_relation = neighborhood;
        this.relation = closure;

        this.longest_up_path = new HashMap<nTuple<Integer>, Integer>();

        Iterator<Integer> sit = this.filters.keySet().iterator();
        while (sit.hasNext()) {
            Integer s = sit.next();
            Iterator<Integer> tit = this.filters.get(s).iterator();
            while (tit.hasNext()) {
                Integer t = tit.next();
                if (!s.equals(t)) {
                    this.longest_up_path.put(new nTuple<Integer>(s, t), 1);
                }
            }
        }

        boolean checkAgain = true;

        while (checkAgain) {
            checkAgain = false;
            sit = this.filters.keySet().iterator();
            while (sit.hasNext()) {
                Integer s = sit.next();
                Set<Integer> sf = this.filters.get(s);
                Iterator<nTuple<Integer>> current = this.longest_up_path.keySet().iterator();
                while (current.hasNext()) {
                    nTuple<Integer> pair = current.next();
                    if ((pair.get(0) != s) && sf.contains(pair.get(0))) {
                        Integer lengthVia = 1 + this.longest_up_path.get(pair);
                        nTuple<Integer> via = new nTuple<Integer>(s, pair.get(1));
                        if (lengthVia > this.longest_up_path.get(via)) {
                            this.longest_up_path.put(via, lengthVia);
                            checkAgain = true;

                        }
                    }
                }
            }
        }

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
        Iterator<nTuple<Integer>> it = c.relation.iterator();
        while (it.hasNext()) {
            nTuple<Integer> pair = it.next();
            System.out.println(pair);
        }

        System.out.println("Neighbor pairs:");
        it = c.neighborhood_relation.iterator();
        while (it.hasNext()) {
            nTuple<Integer> pair = it.next();
            System.out.println(pair);
        }

        System.out.println("Adding (-4,-2)...");
        c.addPair(-4, -2);

        System.out.println("Neighbor pairs:");
        it = c.neighborhood_relation.iterator();
        while (it.hasNext()) {
            nTuple<Integer> pair = it.next();
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

        chronologie c2=null;
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
