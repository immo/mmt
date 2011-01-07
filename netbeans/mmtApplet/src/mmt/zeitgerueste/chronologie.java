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
public class chronologie {

    private Set<int[]> relation;
    private Set<int[]> neighborhood_relation;

    public chronologie() {
        relation = new HashSet<int[]>();
        neighborhood_relation = new HashSet<int[]>();
    }

    public boolean isLess(int x, int y) {
        return relation.contains(new int[]{x, y});
    }

    public boolean addPair(int x, int y) {
        if (isLess(y, x) || (x == y)) {
            return false;
        }
        if (!isLess(x, y)) {
            relation = neighborhood_relation;
            relation.add(new int[]{x, y});
            closeRelation();
        }
        return true;
    }

    public boolean removePair(int x, int y) {
        if (!isLess(x, y)) {
            return false;
        }

        Iterator<int[]> it = neighborhood_relation.iterator();
        while (it.hasNext()) {
            int[] pair = it.next();
            int s = pair[0];
            int t = pair[1];

            if ((t == y) && isLess(x, s))  {
                it.remove();
            }
        }
        relation = neighborhood_relation;
        closeRelation();
        return true;
    }

    public void closeRelation() {
        Set<int[]> closure = new HashSet<int[]>();
        Set<int[]> neighborhood = new HashSet<int[]>();
        Map<Integer, Set<Integer>> ideals = new HashMap<Integer, Set<Integer>>();
        Map<Integer, Set<Integer>> filters = new HashMap<Integer, Set<Integer>>();
        Map<Integer, Set<Integer>> non_neighbors = new HashMap<Integer, Set<Integer>>();

        Iterator<int[]> it = relation.iterator();
        while (it.hasNext()) {
            int[] pair = it.next();
            int s = pair[0];
            int t = pair[1];
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
            Iterator<Integer> git = filters.get(s).iterator();
            while (git.hasNext()) {
                Integer t = git.next();
                closure.add(new int[]{s, t});                
            }
        }

        it = closure.iterator();
        while (it.hasNext()) {
            int[] pair = it.next();
            if (pair[0]==pair[1]) {
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

        fit = filters.keySet().iterator();
        while (fit.hasNext()) {
            Integer s = fit.next();
            Iterator<Integer> git = filters.get(s).iterator();
            while (git.hasNext()) {
                Integer t = git.next();
                neighborhood.add(new int[]{s, t});
            }
        }



        this.neighborhood_relation = neighborhood;
        this.relation = closure;
    }

    public static void main(String args[])
	throws java.io.IOException, java.io.FileNotFoundException
    {
        System.out.println("Testing chronology class...");

        chronologie c = new chronologie();
        c.addPair(0,1);
        c.addPair(1,2);
        c.addPair(0,3);
        c.addPair(0,4);
        c.addPair(-2,-1);
        c.addPair(-4, -1);
        c.addPair(-1,1);

        System.out.println("Relation pairs:");
        Iterator<int[]> it = c.relation.iterator();
        while (it.hasNext()) {
            int[] pair = it.next();
            System.out.println("("+pair[0]+", "+pair[1]+")");
        }

        System.out.println("Neighbor pairs:");
        it = c.neighborhood_relation.iterator();
        while (it.hasNext()) {
            int[] pair = it.next();
            System.out.println("("+pair[0]+", "+pair[1]+")");
        }

        System.out.println("Adding (-4,-2)...");
        c.addPair(-4,-2);

        System.out.println("Neighbor pairs:");
        it = c.neighborhood_relation.iterator();
        while (it.hasNext()) {
            int[] pair = it.next();
            System.out.println("("+pair[0]+", "+pair[1]+")");
        }
        

        
    }

}
