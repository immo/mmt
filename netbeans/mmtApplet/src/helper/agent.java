/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package helper;
import java.lang.instrument.*;
import mmt.zeitgerueste.*;
/**
 *
 * @author immanuel
 */

public class agent {
    
    public static void premain(String args, Instrumentation inst) {
    nTuple<Integer> obj = new nTuple<Integer>(1,2);
    long size = inst.getObjectSize(obj);
    System.out.println("Bytes used by object: " + size);
    long array = inst.getObjectSize(obj.tuple);

    System.out.println("Bytes used by array: " + array);
    long occ = inst.getObjectSize(obj.tuple.get(0))+
               inst.getObjectSize(obj.tuple.get(1));
    System.out.println("Bytes occupied by array: " + occ);
    test t = new test(1,2);
    System.out.println("Bytes occupied by test: " + inst.getObjectSize(t));
    
    intPair p = new intPair(10,11);
    System.out.println("Bytes occupied by intPair: " + inst.getObjectSize(p));

  }
   public static void main(String args[])
            throws java.io.IOException, java.io.FileNotFoundException {
 
      }
}
