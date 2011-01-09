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
public class chronologischeAbbildung {

    public zeitgeruest source;
    public zeitgeruest target;
    public Map<Integer, Integer> map;

    public boolean isNeighborCompatible() {
        Iterator<int[]> it = source.X.getNeighbors().iterator();
        while (it.hasNext()) {
            int[] pair = it.next();
            int s = pair[0];
            int t = pair[1];

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

    public static void main(String args[])
            throws java.io.IOException, java.io.FileNotFoundException {
    }
}
