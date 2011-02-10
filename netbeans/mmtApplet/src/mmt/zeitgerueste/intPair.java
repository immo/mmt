/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package mmt.zeitgerueste;

/**
 *
 * @author immanuel
 */
public class intPair implements Comparable {
    int p,q;

    public intPair(int p, int q) {
        this.p = p;
        this.q = q;
    }

    int get(int c) {
        if (c==0) {
            return q;
        }
        return p;
    }

    boolean inDelta() {
        return this.p == this.q;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final intPair other = (intPair) obj;
        if (this.p != other.p) {
            return false;
        }
        if (this.q != other.q) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        return 53*this.p + this.q;
    }

    public int compareTo(Object t) {
        intPair o = (intPair) t;
        if (this.p == o.p) {
            return this.q - o.q;
        }
        return this.p - o.p;
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        return new intPair(p, q);
    }

    @Override
    public String toString() {
        return "(" + this.p + ", " + this.q +")";
    }



}
