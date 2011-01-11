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

    public zeitgeruest(int sizeOfT) {
        this.T = new ArrayList<traeger>();
        for (int i =0;i<sizeOfT; ++i) {
            this.T.add(new traeger());
        }
        X = new chronologie();
    }

    public zeitgeruest(Iterable annotations) {
        this.T = new ArrayList<traeger>();
        for (Iterator it = annotations.iterator();it.hasNext();) {
            traeger t = new traeger();
            t.addAnotation(it.next());
            this.T.add(t);
        }
        X = new chronologie();
    }

    public zeitgeruest(Object[] annotations) {
        this.T = new ArrayList<traeger>();
        for (int i = 0;i<annotations.length;++i) {
            traeger t = new traeger();
            t.addAnotation(annotations[i]);
            this.T.add(t);
        }
        X = new chronologie();
    }

    public boolean addChain(int[] chain) {
        ArrayList<Integer> list = new ArrayList<Integer>(chain.length);

        for (int i =0; i <chain.length; ++i)
        {
            list.add(chain[i]);
        }

        return this.X.addChain(list);
    }


    public Set<chronologischeAbbildung> getAllMapsOnto(zeitgeruest target) {
        Set<chronologischeAbbildung> maps = new TreeSet<chronologischeAbbildung>();
        if (T.size() >= target.T.size()) {
            chronologischeAbbildung current_map = new chronologischeAbbildung(this, target);
            
            /* Idea: take a partial but surjective map and fill out the free spots */

            ArrayList<Set<Integer>> checked_possibilities = new ArrayList<Set<Integer>>();
            for (int i=0;i<target.T.size();++i) {
                checked_possibilities.add(new TreeSet<Integer>());
            }

            
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

        zeitgeruest source = new zeitgeruest(new Object[]{"1","2","3","4"});
        source.addChain(new int[]{0,1,2});

        zeitgeruest t1 = new zeitgeruest(new Object[]{"1/2","3","4"});
        t1.addChain(new int[]{0,1});
        zeitgeruest t2 = new zeitgeruest(new Object[]{"1","2/3","4"});
        t2.addChain(new int[]{0,1});
        zeitgeruest t3 = new zeitgeruest(new Object[]{"1/2/3/4"});

        zeitgeruest t4 = new zeitgeruest(new Object[]{"2","1","3","4"});
        t4.addChain(new int[]{0,1,2});
        zeitgeruest t5 = new zeitgeruest(new Object[]{"1","2","3","4"});
        zeitgeruest t6 = new zeitgeruest(new Object[]{"2","3","1/4"});
        t6.addChain(new int[]{0,1});
        zeitgeruest t7 = new zeitgeruest(new Object[]{"1","2","3","4","_"});
        t7.addChain(new int[]{0,1,2});

        ArrayList<chronologischeAbbildung> maps = new ArrayList<chronologischeAbbildung>();
        maps.add(new chronologischeAbbildung(source, t1));
        maps.add(new chronologischeAbbildung(source, t2));
        maps.add(new chronologischeAbbildung(source, t3));
        maps.add(new chronologischeAbbildung(source, t4));
        maps.add(new chronologischeAbbildung(source, t5));
        maps.add(new chronologischeAbbildung(source, t6));
        maps.add(new chronologischeAbbildung(source, t7));

        maps.get(0).addMappingPairs(new int[]{0,0, 1,0, 2,1, 3,2});
        maps.get(1).addMappingPairs(new int[]{0,0, 1,1, 2,1, 3,2});
        maps.get(2).addMappingPairs(new int[]{0,0, 1,0, 2,0, 3,0});
        maps.get(3).addMappingPairs(new int[]{0,1, 1,0, 2,2, 3,3});
        maps.get(4).addMappingPairs(new int[]{0,0, 1,1, 2,2, 3,3});
        maps.get(5).addMappingPairs(new int[]{0,2, 1,0, 2,1, 3,2});
        maps.get(6).addMappingPairs(new int[]{0,0, 1,1, 2,2, 3,3});


        System.out.println(maps);

        System.out.println("\n\n");
        for (int i =0;i<7;++i) {
            System.out.println("Map #"+i);
            System.out.println("(c)? " + maps.get(i).isComplete()
                    + " (s)? " + maps.get(i).isSurjective()
                    + " (m)? " + maps.get(i).isPartialWeaklyMonotone()
                    + " (o)? " + maps.get(i).isPartialTargetOrderDefining());
        }

        System.out.println("Maps between source and t1:");
        System.out.println(source.getAllMapsOnto(t1));
        
    }

}
