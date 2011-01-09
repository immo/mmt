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

    public Set<chronologischeAbbildung> getAllMapsOnto(zeitgeruest target) {
        Set<chronologischeAbbildung> maps = new TreeSet<chronologischeAbbildung>();
        if (T.size() >= target.T.size()) {
            chronologischeAbbildung current_map = new chronologischeAbbildung(this, target);

        }
        return maps;
    }

    @Override
    public String toString() {
        String rep = "Traeger: \n";
        for (int i=0;i<T.size();++i)
        {
            rep += "   "+i+" "+T.get(i)+"\n";
        }
        rep += "Neighbors: \n";
        int count = 0;
        Iterator<int[]> it = X.getNeighbors().iterator();
        while (it.hasNext()){
            int[] pair = it.next();
            if (count > 0)
            {
                if (count % 6 == 0) {
                    rep += "\n";
                } else {
                    rep += "   ";
                }
            }
            count ++;
            rep += pair[0]+"->"+pair[1];
        }
        return rep;
    }
    public static void main(String args[])
	throws java.io.IOException, java.io.FileNotFoundException
    {
        zeitgeruest z = new zeitgeruest();
        z.T.add(new traeger());
        z.T.get(0).addAnotation("yellow");
        z.T.add(new traeger());
        z.T.get(1).addAnotation("blue");
        z.T.add(new traeger());
        z.T.get(2).addAnotation("green");
        z.X.addPair(0, 1);
        System.out.println(z);
    }

}
