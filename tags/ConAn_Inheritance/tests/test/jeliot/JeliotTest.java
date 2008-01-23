/*
 * Created on Oct 19, 2004
 */
package test.jeliot;

import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FilenameFilter;

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
 * This test should be run against the files in the testFiles directory.
 * So the context for these tests is the testFiles directory and it is assumed.
 * 
 * You can control the test with the INPUT_GIVEN variable that is set <code>true</code> when
 * there is someone giving inputs to the programs when needed and <code>false</code> when
 * the tests are run stand-alone without input.
 * 
 * When adding new files to the testFiles directory, there are few thing to remember.
 * This test assumes that the file name is also the class name that contains the main method.
 * If this is not the case then the class name should be indicated with <code>//Class:ClassNameHere</code>
 * in the beginning of the file. Secondly, the method main is assumed to contain no parameters so it is
 * of form <code>main()</code> if the method is something else it needs to be indicated with
 * <code>//Call-Method:HereComesTheMethodCall</code> (e.g. <code>//Call-Method:main(new String[0])</code>
 * or <code>//Call-Method:SomeMethod()</code>). Thirdly, if the program needs any input then it should be
 * indicated with <code>//Needs_Input</code> code in the beginning of the file. Fourthly, if the method call
 * is non-static for example a constructor call such as <code>//Call-Method:new ClockDisplay()</code> then
 * it should be indicated with the code <code>//NonStaticCall</code>.
 * 
 * @author nmyller
 */
public class JeliotTest extends TestCase {

    private static final boolean INPUT_GIVEN = false;

    private static final String MAIN_METHOD_CALL = "main()";

    //private static final String mainMethodCallWithArgs = "main(new String[0]);";

    private static final String CLASS = "//Class:";

    private static final String CALL_METHOD = "//Call-Method:";

    private static final String NEEDS_INPUT = "//Needs_Input";

    private static final String NON_STATIC_CALL = "//NonStaticCall";

    public void testJeliot() {
        File file = new File(System.getProperties().getProperty("user.dir"));

        File[] files = file.listFiles(new FilenameFilter() {
            public boolean accept(File dir, String name) {
                return (name.indexOf(".java") >= 0);
            }
        });

        int n = files.length;
        for (int i = 0; i < n; i++) {
            String fileName = files[i].getName();
            String className = fileName.substring(0, fileName.length() - 5);
            String calledMethod = MAIN_METHOD_CALL;
            boolean needsInput = false;
            boolean staticCall = true;

            try {
                BufferedReader br = new BufferedReader(new FileReader(files[i]));
                String str = null;

                while ((str = br.readLine()) != null) {
                    int index;
                    if ((index = str.indexOf(CLASS)) >= 0) {
                        className = str.substring(index + CLASS.length());
                    }

                    if ((index = str.indexOf(CALL_METHOD)) >= 0) {
                        calledMethod = str.substring(index
                                + CALL_METHOD.length());
                    }

                    if ((index = str.indexOf(NEEDS_INPUT)) >= 0) {
                        needsInput = true;
                    }

                    if ((index = str.indexOf(NON_STATIC_CALL)) >= 0) {
                        staticCall = false;
                    }

                }

            } catch (Exception e) {
                e.printStackTrace();
            }

            if (INPUT_GIVEN || needsInput == INPUT_GIVEN) {
                runJeliotWithClassAndMethod(fileName, className, calledMethod,
                        staticCall);
            }
        }

    }

    public void runJeliotWithClassAndMethod(String fileName, String className,
            String calledMethod, boolean staticCall) {

        System.out.println(fileName + ":" + className + ":" + calledMethod
                + ":" + staticCall);

        Jeliot j = Jeliot.start(new String[] { "", "true", fileName });
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        if (staticCall) {
            j.compile(className + "." + calledMethod + ";");
        } else {
            j.compile(calledMethod + ";");
        }

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
        assertTrue("Error Message is shown instead of Theater view in file "
                + fileName + ".", jw.getTheaterPane() instanceof JTabbedPane);
        //edit.doClick();
        JFrame frame = jw.getFrame();
        WindowListener[] wl = frame.getWindowListeners();
        int n = wl.length;
        for (int i = 0; i < n; i++) {
            wl[i].windowClosing(new WindowEvent(frame,
                    WindowEvent.WINDOW_CLOSING));
        }
        frame.dispose();
        frame.setVisible(false);
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