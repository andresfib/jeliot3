/*
 * Created on 29.7.2004
 */
package jeliot.tracker;

import java.awt.Point;
import java.awt.Rectangle;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import jeliot.gui.CodePane2;
import jeliot.theater.Theater;
import jeliot.util.DebugUtil;

/**
 * Most of the times the format for different tracker lines is
 * TIME:ID:NAME:X:Y:W:H but there are other possibilities as well.
 * The time is counted in milliseconds from the starting time.
 * 
 * @author Niko Myller
 */
public class Tracker {

    
    /**
     * Comment for <code>DATE_FORMAT</code>
     */
    private static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy.MM.dd't'HH.mm.ss.SSSS"); 

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
     * Comment for <code>nextId</code>
     */
    private static int nextId = 1;

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
                File file = null;
                do {
				file = new File(f, "JeliotTracker"
						+ System.currentTimeMillis() + ".txt");
                } while (file.exists());
				file.createNewFile();
				out = new BufferedWriter(new FileWriter(file));
                out.write("StartTime:"+DATE_FORMAT.format(TrackerClock.getInstance().getStartTime()));
                out.newLine();
			} catch (Exception e) {
                if (DebugUtil.DEBUGGING) {
                    e.printStackTrace();
                }
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
                if (DebugUtil.DEBUGGING) {
                    e.printStackTrace();
                }
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
	public static int writeToFile(String name, int x, int y, int w, int h,
			long millis, int id) {
        if (id < 0) {
            id = Tracker.nextId++;
        }
		if (out != null && track && theater != null) {
			try {
				Point p = theater.getLocationOnScreen();
				Rectangle r = theater.getClipRect();
				if (r != null) {
					out.write(millis + ":" + id + ":" +name + ":" + (p.x + x - r.x) + ":"
							+ (p.y + y - r.y) + ":" + w + ":" + h);
					out.newLine();
				}
			} catch (Exception e) {
                if (DebugUtil.DEBUGGING) {
                    e.printStackTrace();
                }
			}
		}
        return id;
	}

    /**
     * @param name
     * @param x
     * @param y
     * @param w
     * @param h
     * @param millis
     */
    public static int writeIndexToFile(String name, int x, int y, int x2, int y2,
            long millis, int id) {
        if (id < 0) {
            id = Tracker.nextId++;
        }
        if (out != null && track && theater != null) {
            try {
                Point p = theater.getLocationOnScreen();
                Rectangle r = theater.getClipRect();
                if (r != null) {
                    out.write(millis + ":" + id + ":" + name + ":" + (p.x + x - r.x) + ":"
                            + (p.y + y - r.y) + ":" + (p.x + x2 - r.x) + ":" + (p.y + y2 - r.y));
                    out.newLine();
                }
            } catch (Exception e) {
                if (DebugUtil.DEBUGGING) {
                    e.printStackTrace();
                }
            }
        }
        return id;
    }
    
    
	/**
	 * @param name
	 * @param x
	 * @param y
	 * @param w
	 * @param h
	 * @param millis
	 */
	public static int writeToFileFromCodeView(String name, int x, int y,
			int w, int h, long millis, int id) {
        if (id < 0) {
            id = Tracker.nextId++;
        }

		if (out != null && track && codePane != null
				&& codePane.getTextArea().isShowing()) {
			try {
				Point p = codePane.getTextArea().getLocationOnScreen();
				Rectangle r = codePane.getTextArea().getPainter().getClipRect();
				if (r != null) {
					p.x = p.x + 35; //35 comes from the width of the line
					// numbers showing component that is for
					// some reason not regonized correctly.
					out.write(millis
                            + ":"
                            + id
                            + ":" 
                            + name
							+ ":"
							+ (p.x + x - r.x)
							+ ":"
							+ (p.y + y - r.y)
							+ ":"
							+ (((x + w - r.x) < (codePane.getWidth() - 35)) ? w
									: ((codePane.getWidth() - 35)))
                            + ":"
                            + h
							);
					out.newLine();
				}
			} catch (Exception e) {
                if (DebugUtil.DEBUGGING) {
                    e.printStackTrace();
                }
			}
		}
        return id;
	}

    /**
     * @param name
     * @param l
     * @param r
     * @param millis
     */
    public static int writeToFileFromCodeView(String name, int l, int r,
            long millis, int id) {
        if (id < 0) {
            id = Tracker.nextId++;
        }

        if (out != null && track && codePane != null
                && codePane.getTextArea().isShowing()) {
            try {
                    out.write(millis
                            + ":"
                            + id
                            + ":"
                            + name
                            + ":"
                            + l
                            + ":"
                            + r
                            );
                    out.newLine();
            } catch (Exception e) {
                if (DebugUtil.DEBUGGING) {
                    e.printStackTrace();
                }
            }
        }
        return id;
    }
    
	/**
	 * @param name
	 * @param millis
	 */
	public static int writeToFile(String name, long millis, int id) {
        if (id < 0) {
            id = Tracker.nextId++;
        }

		if (out != null && track) {
			try {
				out.write(millis + ":" + id + ":" + name);
				out.newLine();
			} catch (Exception e) {
                if (DebugUtil.DEBUGGING) {
                    e.printStackTrace();
                }
			}
		}
        
        return id;
	}

	/**
	 * @param name
	 * @param fileName
	 * @param millis
	 */
	public static int writeToFile(String name, String fileName, long millis, int id) {
        if (id < 0) {
            id = Tracker.nextId++;
        }

		if (out != null && track) {
			try {
				out.write(millis + ":" + id + ":" + name + ":" + fileName);
				out.newLine();
			} catch (Exception e) {
                if (DebugUtil.DEBUGGING) {
                    e.printStackTrace();
                }
			}
		}
        return id;
	}
}