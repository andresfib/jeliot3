/*
 * Created on 29.7.2004
 */
package jeliot.tracker;

import java.awt.Point;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import jeliot.theater.Theater;


/**
 * @author Niko Myller
 */
public class Tracker {

    private static BufferedWriter out;
    private static Theater theater;
    private static boolean track = false;
    
    public static void setTrack(boolean t) {
        track = t;
    }
    
    public static void setTheater(Theater t) {
        theater = t;
    }
    
    public static void openFile(File f) {
        if (out == null && track) {
            try {
                File file = new File(f, "JeliotTracker" + System.currentTimeMillis() + ".txt");
                file.createNewFile();
                out = new BufferedWriter(new FileWriter(file));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    
    public static void closeFile() {
        if (track) {
            try {
                out.flush();
                out.close();
                out = null;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    
    public static void writeToFile(String name, int x, int y, int w, int h, long millis) {
        if (out != null && track) {
            try {
                Point p = theater.getLocationOnScreen();
                out.write(name + ":" + (p.x + x) + ":" + (p.y + y) + ":" + w + ":" + h + ":" + millis);
                out.newLine();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    
    public static void writeToFile(String name, long millis) {
        if (out != null && track) {
            try {
                out.write(name + ":" + millis);
                out.newLine();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    
    public static void writeToFile(String name, String fileName, long millis) {
        if (out != null && track) {
            try {
                out.write(name + ":" + fileName + ":" + millis);
                out.newLine();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }    
}
