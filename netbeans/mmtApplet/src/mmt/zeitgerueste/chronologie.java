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

    public boolean isLess(int x,int y) {
        return relation.contains(new int[]{x,y});
    }


    public void closeRelation() {
        Set<int[]> closure = new HashSet<int[]>();
        Map<Integer, Set<Integer>> filters = new HashMap<Integer,Set<Integer>>();
        Map<Integer, Set<Integer>> ideals = new HashMap<Integer,Set<Integer>>();

        Iterator<int[]> it = relation.iterator();
        while (it.hasNext()){
            int[] pair = it.next();
            int s = pair[0];
            int t = pair[1];
            if (!filters.containsKey(t)) {
                filters.put(t, new TreeSet<Integer>());
                filters.get(t).add(t);
            }
            if (!ideals.containsKey(t)) {
                ideals.put(t, new TreeSet<Integer>());
                ideals.get(t).add(t);
            }

            if (!filters.containsKey(s)) {
                filters.put(s, new TreeSet<Integer>());
                filters.get(s).add(s);
            }
            if (!ideals.containsKey(s)) {
                ideals.put(s, new TreeSet<Integer>());
                ideals.get(s).add(s);
            }
            
            filters.get(s).addAll(filters.get(t));
            ideals.get(t).addAll(ideals.get(s));
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
    }
}
