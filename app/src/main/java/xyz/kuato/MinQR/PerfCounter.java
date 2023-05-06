package xyz.kuato.MinQR;

import android.util.Log;

import java.util.Timer;
import java.util.TimerTask;


/**
 * Counts events (defined by caller) and dumps them to the log at some interval
 */
public class PerfCounter {

    /**
     * Initialize timer to dump every 1s
     */
    public PerfCounter(String name) {
        mName = name;
        mCount = 0;
        mTimer = new Timer(mName);
        mTimerTask = new TimerTask() {
            @Override
            public void run() {
                dumpAndReset();
            }
        };

        mTimer.scheduleAtFixedRate(mTimerTask, 1000, 1000);
    }

    /**
     * Increment the event counter
     */
    public void inc() {
        ++mCount;
    }

    /**
     * Dump the event count to the log and reset the counter
     * @return The number of events
     */
    public void dumpAndReset() {
        Log.d(mName, "" + mCount);
        mCount = 0;
    }

    private int mCount;             /** Internal event counter */
    private Timer mTimer;           /** Internal timer */
    private TimerTask mTimerTask;   /** Task used by timer to dump and reset counter */
    private String mName;           /** Counter name for log file */
}
