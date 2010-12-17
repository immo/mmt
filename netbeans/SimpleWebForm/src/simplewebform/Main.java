/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package simplewebform;

/**
 *
 * @author immanuel
 */
import simplewebform.interfaces.JySwingType;
import org.plyjy.factory.JythonObjectFactory;

public class Main {
    JythonObjectFactory factory;

    public static void invokeJython(){
        JySwingType jySwing = (JySwingType) JythonObjectFactory
        .createObject(JySwingType.class, "JythonMain");
        jySwing.start();
    }

    public static void main(String[] args) {
        invokeJython();
    }
}