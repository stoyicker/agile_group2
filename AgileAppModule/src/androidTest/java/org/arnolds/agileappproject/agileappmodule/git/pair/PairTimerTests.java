package org.arnolds.agileappproject.agileappmodule.git.pair;

import android.app.Activity;
import android.test.InstrumentationTestCase;
import android.util.Log;

import org.arnolds.agileappproject.agileappmodule.pair.PairTimer;
import org.mockito.Mockito;

import java.text.ParseException;

public class PairTimerTests extends InstrumentationTestCase {

    private static boolean firstRun = true;

    public void init() throws ParseException {
        System.setProperty("dexmaker.dexcache",
                getInstrumentation().getTargetContext().getCacheDir().getAbsolutePath());
        PairTimer.setActivity(Mockito.mock(Activity.class));
        PairTimer.getInstance().togglePlayPause();//Start the countdown
    }

    public void setUp() {
        if (firstRun) {
            firstRun = false;
            try {
                init();
            }
            catch (ParseException e) {
                Log.wtf("debug", e.getClass().getName(), e);
            }
        }
    }

    public void test_pause() {
        try {
            PairTimer.getInstance().togglePlayPause();
        }
        catch (ParseException e) {
            fail();
        }
    }

    public void test_resume() {
        try {
            PairTimer.getInstance().togglePlayPause();
            Thread.sleep(10000);
            PairTimer.getInstance().togglePlayPause();
        }
        catch (ParseException e) {
            fail();
        }
        catch (InterruptedException e) {
            Log.wtf("debug", e.getClass().getName(), e);
        }
    }

    public void test_stop() {
        try {
            PairTimer.getInstance().completeStop();
            PairTimer.getInstance().togglePlayPause();
        }
        catch (ParseException e) {
            Log.wtf("debug", e.getClass().getName(), e);
        }
    }

}
