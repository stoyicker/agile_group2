package org.arnolds.agileappproject.agileappmodule.ui.frags;

import android.app.Activity;
import android.util.Log;

import org.arnolds.agileappproject.agileappmodule.pair.PairTimer;

import java.text.ParseException;

public class TimerFragment extends ArnoldSupportFragment {

    private static final int DRAWER_POSITION = 3;

    public TimerFragment() {
        super(DRAWER_POSITION);
    }

    @Override
    public void onNewRepositorySelected() {
        //Do nothing; it's not repository-dependent
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        PairTimer.setActivity(activity);

        try {
            PairTimer.getInstance().togglePlayPause();
        }
        catch (ParseException e) {
            Log.wtf("debug", e.getClass().getName(), e);
        }
    }
}
