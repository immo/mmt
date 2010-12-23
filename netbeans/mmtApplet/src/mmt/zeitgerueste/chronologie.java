/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package mmt.zeitgerueste;

import java.util.*;
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


    public boolean isLess(int x,int y) {
        return relation.contains(new int[]{x,y});
    }


    public void closeRelation() {
        Set<int[]> closure = new HashSet<int[]>();
        Set<int[]> neighborhood = new HashSet<int[]>();
        Map<Integer, Set<Integer>> filters = new HashMap<Integer,Set<Integer>>();
        Map<Integer, Set<Integer>> non_neighbors = new HashMap<Integer,Set<Integer>>();

        Iterator<int[]> it = relation.iterator();
        while (it.hasNext()){
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
            
            if (!filters.get(s).contains(t)) {                
                filters.get(s).addAll(filters.get(t));
            }
        }




        Iterator<Integer> fit = filters.keySet().iterator();
        while (fit.hasNext()) {
            Integer s = fit.next();
            Iterator<Integer> git = filters.get(s).iterator();
            while (git.hasNext()) {
                Integer t = git.next();
                closure.add(new int[]{s,t});
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
                neighborhood.add(new int[]{s,t});
            }
        }



        this.neighborhood_relation = neighborhood;
        this.relation = closure;
    }
}
