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
        Iterator<nTuple<Integer>> it = X.getNeighbors().iterator();
        while (it.hasNext()){
            nTuple<Integer> pair = it.next();
            if (count > 0)
            {
                if (count % 6 == 0) {
                    rep += "\n";
                } else {
                    rep += "   ";
                }
            }
            count ++;
            rep += pair.get(0)+"->"+pair.get(1);
        }
        return rep;
    }
    public static void main(String args[])
	throws java.io.IOException, java.io.FileNotFoundException
    {
        System.out.println("test? "+ ((new int[]{1,2}) == (new int[]{1,2}))); //DAMN YOUR EYES, JAVA!
        System.out.println("equals? " + (new int[]{1,2}).equals(new int[]{1,2})); //EVEN MORE

        zeitgeruest z = new zeitgeruest();
        z.T.add(new traeger());
        z.T.get(0).addAnotation("yellow");
        z.T.add(new traeger());
        z.T.get(1).addAnotation("blue");
        z.T.add(new traeger());
        z.T.get(2).addAnotation("green");
        z.X.addPair(0, 1);
        z.X.addPair(0, 2);
        System.out.println(z);
        chronologischeAbbildung f = new chronologischeAbbildung(z, z);
        f.map.put(0,0);
        f.map.put(1,2);
        f.map.put(2,1);
        System.out.println(f);
        System.out.println("(s)? " + f.isSurjective() +
                    " (m)? " + f.isPartialWeaklyMonotone() +
                    " (o)? " + f.isPartialTargetOrderDefining() +
                    " (complete)? " + f.isComplete());
        System.out.println("f.target.X.isLess(0,1) = " + f.target.X.isLess(0,1));
        System.out.println("f.source.X.isLess(0,1) = " + f.source.X.isLess(0,1));
        System.out.println("z.X.isLess(0,1) = " + z.X.isLess(0,1));
        System.out.println("z.X.isLowerNeighbor(0,1) = " + z.X.isLowerNeighbor(0,1));


        
        
    }

}
