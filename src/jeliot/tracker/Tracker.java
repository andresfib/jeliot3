/*
 * Created on 29.7.2004
 */
package jeliot.tracker;

import java.awt.Point;
import java.awt.Rectangle;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

import jeliot.gui.CodePane2;
import jeliot.theater.Theater;

//TODO: Change this class to use Singleton pattern

/**
 * @author Niko Myller
 */
public class Tracker {

	/**
	 * Comment for <code>out</code>
	 */
	private static BufferedWriter out;

	/**
	 * Comment for <code>theater</code>
	 */
	private static Theater theater;

	/**
	 * Comment for <code>track</code>
	 */
	private static boolean track = false;

	/**
	 * Comment for <code>codePane</code>
	 */
	private static CodePane2 codePane;

	/**
	 * @param t
	 */
	public static void setTrack(boolean t) {
		track = t;
	}

	/**
	 * @param cp
	 */
	public static void setCodePane2(CodePane2 cp) {
		codePane = cp;
	}

	/**
	 * @param t
	 */
	public static void setTheater(Theater t) {
		theater = t;
	}

	/**
	 * @param f
	 */
	public static void openFile(File f) {
		if (out == null && track) {
			try {
				File file = new File(f, "JeliotTracker"
						+ System.currentTimeMillis() + ".txt");
				file.createNewFile();
				out = new BufferedWriter(new FileWriter(file));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 
	 */
	public static void closeFile() {
		if (track) {
			try {
				out.flush();
				out.close();
				out = null;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * @param name
	 * @param x
	 * @param y
	 * @param w
	 * @param h
	 * @param millis
	 */
	public static void writeToFile(String name, int x, int y, int w, int h,
			long millis) {
		if (out != null && track && theater != null) {
			try {
				Point p = theater.getLocationOnScreen();
				Rectangle r = theater.getClipRect();
				if (r != null) {
					out.write(name + ":" + (p.x + x - r.x) + ":"
							+ (p.y + y - r.y) + ":" + w + ":" + h + ":"
							+ millis);
					out.newLine();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * @param name
	 * @param x
	 * @param y
	 * @param w
	 * @param h
	 * @param millis
	 */
	public static void writeToFileFromCodeView(String name, int x, int y,
			int w, int h, long millis) {
		if (out != null && track && codePane != null
				&& codePane.getTextArea().isShowing()) {
			try {
				Point p = codePane.getTextArea().getLocationOnScreen();
				Rectangle r = codePane.getTextArea().getPainter().getClipRect();
				if (r != null) {
					p.x = p.x + 35; //35 comes from the width of the line
					// numbers showing component that is for
					// some reason not regonized correctly.
					out.write(name
							+ ":"
							+ (p.x + x - r.x)
							+ ":"
							+ (p.y + y - r.y)
							+ ":"
							+ (((x + w - r.x) < (codePane.getWidth() - 35)) ? w
									: ((codePane.getWidth() - 35))) + ":" + h
							+ ":" + millis);
					out.newLine();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * @param name
	 * @param millis
	 */
	public static void writeToFile(String name, long millis) {
		if (out != null && track) {
			try {
				out.write(name + ":" + millis);
				out.newLine();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * @param name
	 * @param fileName
	 * @param millis
	 */
	public static void writeToFile(String name, String fileName, long millis) {
		if (out != null && track) {
			try {
				out.write(name + ":" + fileName + ":" + millis);
				out.newLine();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}