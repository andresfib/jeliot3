package jeliot.tracker;

import java.awt.Toolkit;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Simple demo that uses java.util.Timer to schedule a task 
 * to execute once 5 seconds have passed.
 */

public class Reminder {
    Timer timer;
    Timer  timer2;
    
    public Reminder(int seconds) {
        timer = new Timer();
        timer2 = new Timer();
        
        timer.schedule(new RemindTask(), seconds*1000);
        timer.schedule(new ExitTask(), (120*1000)*(seconds*1000));
}

    class RemindTask extends TimerTask {
        public void run() {
        	Toolkit.getDefaultToolkit().beep();
        }
    }
    class ExitTask extends TimerTask{
    	public void run(){
    		System.exit(1);
    	}
    }

 }