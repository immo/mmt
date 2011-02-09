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
public class traeger {
    ArrayList<Object> annotations;

    public traeger() {
        annotations = new ArrayList<Object>();
    }

    public Set<Object> getAnnotationSet() {
        return new HashSet<Object>(this.annotations);
    }

    public void addAnnotation(Object o) {
        annotations.add(o);
    }

    @Override
    public String toString() {
        String anreps = "";
        Iterator it = annotations.iterator();
        while (it.hasNext()) {
            anreps += " " + it.next();
        }
        
        return anreps.trim();
    }



}
