package org.arnolds.agileappproject.agileappmodule.pair;

import android.app.Activity;
import android.os.CountDownTimer;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import org.arnolds.agileappproject.agileappmodule.R;
import org.arnolds.agileappproject.agileappmodule.ui.frags.TimerFragment;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class PairTimer {

    private static Boolean isChronometerRunning = Boolean.FALSE;

    /**
     * @param newChronometerTime {@link String} Should be in format HH:mm:ss
     * @throws ParseException
     */
    public static void setTimePreference(String newChronometerTime) throws ParseException {
        initialValueAsDate = SDF.parse(newChronometerTime);
        Log.d("debug", "nCT is " + newChronometerTime);
        String[] values = newChronometerTime.split("\\:");
        values[0] = ((Integer.parseInt(values[0]))) + "";
        StringBuilder x = new StringBuilder();
        for (int i = 0; i < values.length; i++) {
            x.append(values[i]);
            x.append(i < values.length - 1 ? ":" : "");
        }
        initialValue =
                new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").parse("01-01-1970 " + x.toString())
                        .getTime();

        initialValue += 3600000;

        chronometer = new InnerCountDownTimer(initialValue, 1000);
    }

    public static void setActivity(Activity activity) {
        PairTimer.activity = activity;
    }

    public static void setInitialValue(long initialValue) throws ParseException {
        PairTimer.initialValue = initialValue;
        setTimePreference(initialValue + "");
    }

    private static Activity activity;
    private static Date initialValueAsDate;
    private static long initialValue;
    private static long lastTimeTracked;
    private static final SimpleDateFormat SDF = new SimpleDateFormat("HH:mm:ss", Locale.ENGLISH);
    private static InnerCountDownTimer chronometer;

    public static void setTextView(TextView chronometerTextView) {
        PairTimer.chronometerTextView = chronometerTextView;
    }

    private static TextView chronometerTextView;
    private static PairTimer instance;
    private final static String defaultTime = "01:00:00";

    /**
     * Use {@link org.arnolds.agileappproject.agileappmodule.pair.PairTimer#setInitialValue(long)}, {@link org.arnolds.agileappproject.agileappmodule.pair.PairTimer#setActivity(android.app.Activity)} and {@link org.arnolds.agileappproject.agileappmodule.pair.PairTimer#setTextView(android.widget.TextView)} to configure the chronometer before calling this.
     *
     * @return
     * @throws ParseException
     */
    public static PairTimer getInstance()
            throws ParseException {
        if (instance == null) {
            instance = new PairTimer(activity, defaultTime, chronometerTextView);
        }

        return instance;
    }

    /**
     * @param _activity
     * @param chronometerTime {@link String} Should be in format HH:mm:ss
     * @throws ParseException
     */
    private PairTimer(Activity _activity, String chronometerTime, TextView textView)
            throws ParseException {
        activity = _activity;

        String[] values = chronometerTime.split("\\:");
        values[0] = ((Integer.parseInt(values[0]))) + "";
        StringBuilder x = new StringBuilder();
        for (int i = 0; i < values.length; i++) {
            x.append(values[i]);
            x.append(i < (values.length - 1) ? ":" : "");
        }
        initialValue =
                new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").parse("01-01-1970 " + x.toString())
                        .getTime();

        initialValue += 3600000;

        initialValueAsDate = SDF.parse(chronometerTime);

        chronometerTextView = textView;

        chronometer = new InnerCountDownTimer(initialValue, 1000);
    }

    public boolean togglePlayPause() {
        if (!isChronometerRunning) {
            chronometer.start();
            isChronometerRunning = Boolean.TRUE;
        }
        else {
            chronometer.cancel();
            chronometer = new InnerCountDownTimer(lastTimeTracked, 1000);
            isChronometerRunning = Boolean.FALSE;
        }
        return isChronometerRunning;
    }

    public void completeStop() {
        chronometer.cancel();
        chronometer = new InnerCountDownTimer(initialValue, 1000);
        lastTimeTracked = 0;
        isChronometerRunning = Boolean.FALSE;
        chronometerTextView.setText(SDF.format(initialValueAsDate));
    }

    public boolean isRunning() {
        return isChronometerRunning;
    }

    public void forceTick() {
        if (chronometerTextView != null) {
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    chronometerTextView.setText(SDF
                            .format(new Date(lastTimeTracked - 3600000).getTime()));
                }
            });
        }
    }

    private static class InnerCountDownTimer extends CountDownTimer {

        private InnerCountDownTimer(long initialValue, long tickInterval) {
            super(initialValue, tickInterval);
        }

        @Override
        public void onTick(final long millisUntilFinished) {
            lastTimeTracked = millisUntilFinished;
            if (chronometerTextView != null) {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        chronometerTextView.setText(SDF
                                .format(new Date(millisUntilFinished - 3600000).getTime()));
                    }
                });
            }
        }

        @Override
        public void onFinish() {
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (chronometerTextView != null) {
                        chronometerTextView.setText(SDF.format(initialValueAsDate));
                        TimerFragment.setPaused();
                    }
                    Toast.makeText(activity, R.string.timer_done,Toast.LENGTH_LONG).show();
                }
            });
            isChronometerRunning = Boolean.FALSE;
        }
    }
}
