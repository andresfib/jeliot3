package jeliot.tracker;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import jeliot.util.DebugUtil;

/**
 * 
 * @author Niko Myller
 */
public class TrackerClock {

    public native long getTrackerTime();

    public native String getTrackerTimeStamp();

    protected static TrackerClock trackerClock;

    private static boolean nativeTracking;

    private Date startTime;

    private static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy.MM.dd't'HH.mm.ss.SSSS");

    public Date getCurrentTime() {
        if (TrackerClock.nativeTracking) {
            System.loadLibrary("JeliotJNI");
            String trackerTimeStamp = getTrackerTimeStamp();
            //DebugUtil.printDebugInfo(trackerTimeStamp);
            try {
                return DATE_FORMAT.parse(trackerTimeStamp);
            } catch (ParseException e) {
                DebugUtil.handleThrowable(e);
            }
        } else {
            return Calendar.getInstance().getTime();
        }
        return null;
    }

    protected static TrackerClock getInstance() {
        if (trackerClock == null) {
            trackerClock = new TrackerClock();
            trackerClock.startTime = trackerClock.getCurrentTime();
        }
        return trackerClock;
    }

    protected TrackerClock() {}

    public static long currentTimeMillis() {
        TrackerClock clock = getInstance();
        return clock.getCurrentTime().getTime() - clock.startTime.getTime();
    }

    public static void setNativeTracking(boolean b) {
        nativeTracking = b;
    }

    public Date getStartTime() {
        return this.startTime;
    }
}