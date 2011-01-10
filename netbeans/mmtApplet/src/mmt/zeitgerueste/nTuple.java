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
public class nTuple<T> {

    public ArrayList<T> tuple;

    public nTuple(ArrayList<T> tuple) {
        this.tuple = tuple;
    }

    public nTuple(T[] tuple) {
        this.tuple = new ArrayList<T>(tuple.length);
        for (int i=0;i<tuple.length;++i) {
            this.tuple.add(tuple[i]);
        }
    }

    public nTuple(T x) {
        this.tuple = new ArrayList<T>();
        this.tuple.add(x);
    }

    public nTuple(T x, T y) {
        this.tuple = new ArrayList<T>();
        this.tuple.add(x);
        this.tuple.add(y);
    }

    public nTuple(T x, T y, T z) {
        this.tuple = new ArrayList<T>();
        this.tuple.add(x);
        this.tuple.add(y);
        this.tuple.add(z);
    }

    public nTuple(T x1, T x2, T x3, T x4) {
        this.tuple = new ArrayList<T>();
        this.tuple.add(x1);
        this.tuple.add(x2);
        this.tuple.add(x3);
        this.tuple.add(x4);

    }

    public T get(int i) {
        return this.tuple.get(i);
    }

    @Override
    public boolean equals(Object obj) {
        
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final nTuple<T> other = (nTuple<T>) obj;
        if (this.tuple.size() != other.tuple.size()) {
            return false;
        }
        Iterator<T> lit = this.tuple.iterator();
        Iterator<T> rit = other.tuple.iterator();
        while (lit.hasNext()) {
            if (!rit.hasNext()) {
                return false;
            }
            T l = lit.next();
            T r = rit.next();
            if (!r.equals(l)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public int hashCode() {
        String hasher = "";
        Iterator<T> lit = this.tuple.iterator();
        while (lit.hasNext()) {
            hasher += lit.next().hashCode() + ",";
        }
        return hasher.hashCode();
    }

    @Override
    public String toString() {
        if (tuple.size() > 0) {
            String stuffer = "";
            Iterator<T> it = tuple.iterator();
            while (it.hasNext()) {
                stuffer += ", " + it.next();
            }
            return "(" + stuffer.substring(2) + ")";
        } else {
            return "ε";
        }
    }

    public static void main(String args[])
            throws java.io.IOException, java.io.FileNotFoundException {
        nTuple<Integer> t = new nTuple<Integer>(1, 2);
        nTuple<Integer> v = new nTuple<Integer>(3, 4);
        nTuple<Integer> v2 = new nTuple<Integer>(1, 2);
        nTuple<Integer> x = new nTuple<Integer>(1);
        System.out.println(t);
        System.out.println(v);
        System.out.println(t==v);
        System.out.println(t==v2);
        System.out.println(t.equals(v2));
        System.out.println(v2.hashCode());
        System.out.println(x.hashCode());
        System.out.println(t.hashCode());
        Set<nTuple<Integer>> tupleSet = new HashSet<nTuple<Integer>>();
        tupleSet.add(t);
        System.out.println(tupleSet);
        tupleSet.add(x);
        System.out.println(tupleSet);
        tupleSet.add(v);
        System.out.println(tupleSet);
        tupleSet.add(v2);
        System.out.println(tupleSet);
        System.out.println(tupleSet.contains(t));
        System.out.println(tupleSet.contains(v2));
        
    }
}
