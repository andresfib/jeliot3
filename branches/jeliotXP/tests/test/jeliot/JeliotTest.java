/*
 * Created on Oct 19, 2004
 */
package test.jeliot;

import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JSlider;
import javax.swing.JTabbedPane;

import jeliot.Jeliot;
import jeliot.gui.JeliotWindow;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * @author nmyller
 */
public class JeliotTest extends TestCase {

    private static final String mainMethodCall = "main();";
    private static final String mainMethodCallWithArgs = "main(new String[0]);";
    
    public void testJeliot() {
        runJeliotWithClassAndMethod("If");
    }
    
    public void runJeliotWithClassAndMethod(String className) {
        Jeliot j = Jeliot.start(new String[] {"", "true", className + ".java"});
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        j.compile(className + "." + mainMethodCall);
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        JeliotWindow jw = j.getGUI();
        JButton play = jw.getPlayButton();
        JButton edit = jw.getEditButton();
        while (!play.isEnabled()) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }            
        }
        JSlider slider = jw.getSpeedSlider();
        slider.setValue(slider.getMaximum());
        play.doClick();
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        while (!edit.isEnabled()) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }                        
        }
        assertTrue("Error Message is shown instead of Theater view.", jw.getTheaterPane() instanceof JTabbedPane);
        //edit.doClick();
        JFrame frame = jw.getFrame();
        WindowListener[] wl = frame.getWindowListeners();
        int n = wl.length;
        for (int i = 0; i < n; i++) {
            wl[i].windowClosing(new WindowEvent(frame, WindowEvent.WINDOW_CLOSING));
        }
        j = null;
        jw = null;
        frame = null;
        play = null;
        edit = null;
    }
    
    public static Test suite() {
        return new TestSuite(JeliotTest.class);
    }
    
    public static void main(String[] args) {
        junit.textui.TestRunner.run(suite());
        //System.exit(0);
    }
}