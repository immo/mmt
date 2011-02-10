/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mmt.zeitgerueste;

import java.util.*;
import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.lang.reflect.*;


/**
 *
 * @author immanuel
 */
public class nTuple<T> implements Comparable {

    public ArrayList<T> tuple;

    public nTuple(ArrayList<T> tuple) {
        this.tuple = tuple;
    }

    public nTuple(Iterator<T> data) {
        this.tuple = new ArrayList<T>();
        while (data.hasNext()) {
            this.tuple.add(data.next());
        }
    }

    public nTuple(T[] tuple) {
        this.tuple = new ArrayList<T>(tuple.length);
        for (int i = 0; i < tuple.length; ++i) {
            this.tuple.add(tuple[i]);
        }
    }

    public int compareTo(Object o) {
        nTuple<T> other = (nTuple<T>) o;
        int os = other.tuple.size();
        int ts = this.tuple.size();
        if (os != ts) {
            return ts - os;
        }
        for (int i = 0; i < os; ++i) {
            Comparable<T> p = (Comparable<T>) this.tuple.get(i);
            int cmp = p.compareTo(other.tuple.get(i));
            if (cmp != 0) {
                return cmp;
            }
        }
        return 0;

    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        nTuple<T> obj = null;
        try {
            Constructor c = this.getClass().getConstructor(new Class[]{});
            try {
                obj = (nTuple<T>) c.newInstance();
            } catch (InstantiationException ex) {
                Logger.getLogger(nTuple.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IllegalAccessException ex) {
                Logger.getLogger(nTuple.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IllegalArgumentException ex) {
                Logger.getLogger(nTuple.class.getName()).log(Level.SEVERE, null, ex);
            } catch (InvocationTargetException ex) {
                Logger.getLogger(nTuple.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (NoSuchMethodException ex) {
            Logger.getLogger(nTuple.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SecurityException ex) {
            Logger.getLogger(nTuple.class.getName()).log(Level.SEVERE, null, ex);
        }
        obj.tuple = (ArrayList<T>) this.tuple.clone();
        return obj;
    }

    public nTuple() {
        this.tuple = new ArrayList<T>();

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
        System.out.println(t == v);
        System.out.println(t == v2);
        System.out.println(t.equals(v2));
        System.out.println(v2.hashCode());
        System.out.println(x.hashCode());
        System.out.println(t.hashCode());
        Set<nTuple<Integer>> tupleSet = new TreeSet<nTuple<Integer>>();
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
        try {
            System.out.println(t.clone());
            nTuple<nTuple<Integer>> degree = new nTuple<nTuple<Integer>>(t, v);
            nTuple<nTuple<Integer>> degree2 = (nTuple<nTuple<Integer>>) degree.clone();
            System.out.println("deep cloning? " + (degree2.get(0) == degree.get(0)));
            System.out.println(degree + " = " + degree2);
        } catch (CloneNotSupportedException ex) {
            Logger.getLogger(nTuple.class.getName()).log(Level.SEVERE, null, ex);
        }
        

    }
}
