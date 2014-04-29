package org.arnolds.agileappproject.agileappmodule.ui.frags;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import org.arnolds.agileappproject.agileappmodule.R;
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
            PairTimer.getInstance().forceTick();
        }
        catch (ParseException e) {
            Log.wtf("debug", e.getClass().getName(), e);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View ret = inflater.inflate(R.layout.fragment_timer, container,
                Boolean.FALSE);

        PairTimer.setTextView((android.widget.TextView) ret.findViewById(R.id.timer_text_view));

        final ImageButton toggleButton = (ImageButton) ret.findViewById(R.id.timer_toggle_button),
                stopButton =
                        (ImageButton) ret.findViewById(R.id.timer_stop_button);

        try {
            toggleButton.setBackgroundResource(
                    !PairTimer.getInstance().isRunning() ? R.drawable.icon_play :
                            R.drawable.icon_paused
            );
        }
        catch (ParseException e) {
            Log.wtf("debug", e.getClass().getName(), e);
        }

        toggleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    toggleButton.setBackgroundResource(
                            !PairTimer.getInstance().togglePlayPause() ? R.drawable.icon_play :
                                    R.drawable.icon_paused
                    );
                }
                catch (ParseException e) {
                    Log.wtf("debug", e.getClass().getName(), e);
                }
            }
        });

        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    PairTimer.getInstance().completeStop();
                    toggleButton.setBackgroundResource(R.drawable.icon_play);
                }
                catch (ParseException e) {
                    Log.wtf("debug", e.getClass().getName(), e);
                }
            }
        });

        return ret;
    }
}
