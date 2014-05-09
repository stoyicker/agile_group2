package org.arnolds.agileappproject.agileappmodule.ui.frags;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import org.arnolds.agileappproject.agileappmodule.R;
import org.arnolds.agileappproject.agileappmodule.pair.PairTimer;

import java.text.ParseException;

public class TimerFragment extends ArnoldSupportFragment {

    private static final int DRAWER_POSITION = 3;
    private TextView timerTextView;
    private static EditText secondsEditTimeView, minutesEditTimeView, hoursEditTimeView;
    private static ImageButton newTimeAcceptButton;
    private static ImageButton toggleButton;

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

        newTimeAcceptButton =
                (ImageButton) ret.findViewById(R.id.button_confirm_new_time_amount);

        newTimeAcceptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imm =
                        (InputMethodManager) getActivity().getApplicationContext().getSystemService(
                                Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(secondsEditTimeView.getWindowToken(),
                        0);
                imm.hideSoftInputFromWindow(minutesEditTimeView.getWindowToken(),
                        0);
                imm.hideSoftInputFromWindow(hoursEditTimeView.getWindowToken(),
                        0);
                TimerFragment.this.fireNewTimeSet();
            }
        });

        secondsEditTimeView =
                (EditText) ret.findViewById(R.id.seconds_edit_time_view);
        minutesEditTimeView =
                (EditText) ret.findViewById(R.id.minutes_edit_time_view);
        hoursEditTimeView =
                (EditText) ret.findViewById(R.id.hours_edit_time_view);

        hoursEditTimeView.addTextChangedListener(new TextWatcher() {

            String originalText;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                originalText = hoursEditTimeView.getText().toString();
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                int value;
                try {
                    value = Integer.parseInt(s.toString());
                    if (value < 0 || value > 23) {
                        Toast.makeText(getActivity().getApplicationContext(),
                                R.string.wrong_time_format,
                                Toast.LENGTH_SHORT).show();
                        hoursEditTimeView.setText(originalText);
                    }
                }
                catch (NumberFormatException ex) {
                    try {
                        Toast.makeText(getActivity().getApplicationContext(),
                                R.string.wrong_time_format,
                                Toast.LENGTH_SHORT).show();
                    }
                    catch (InflateException e) {
                    }
                    hoursEditTimeView.setText(originalText);
                }
            }
        });

        minutesEditTimeView.addTextChangedListener(new TextWatcher() {

            String originalText;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                originalText = minutesEditTimeView.getText().toString();
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                int value;
                try {
                    value = Integer.parseInt(s.toString());
                    if (value < 0 || value > 59 || s.length() > 2) {
                        Toast.makeText(getActivity().getApplicationContext(),
                                R.string.wrong_time_format,
                                Toast.LENGTH_SHORT).show();
                        minutesEditTimeView.setText(originalText);
                    }
                }
                catch (NumberFormatException ex) {
                    try {
                        Toast.makeText(getActivity().getApplicationContext(),
                                R.string.wrong_time_format,
                                Toast.LENGTH_SHORT).show();
                    }
                    catch (InflateException e) {
                    }
                    minutesEditTimeView.setText(originalText);
                }
            }
        });

        secondsEditTimeView.addTextChangedListener(new TextWatcher() {

            String originalText;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                originalText = secondsEditTimeView.getText().toString();
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                int value;
                try {
                    value = Integer.parseInt(s.toString());
                    if (value < 0 || value > 59 || s.length() > 2) {
                        secondsEditTimeView.setText(originalText);
                        Toast.makeText(getActivity().getApplicationContext(),
                                R.string.wrong_time_format,
                                Toast.LENGTH_SHORT).show();
                    }
                }
                catch (NumberFormatException ex) {
                    secondsEditTimeView.setText(originalText);
                    try {
                        Toast.makeText(getActivity().getApplicationContext(),
                                R.string.wrong_time_format,
                                Toast.LENGTH_SHORT).show();
                    }
                    catch (InflateException e) {
                    }
                }
            }
        });

        timerTextView = (android.widget.TextView) ret.findViewById(R.id.timer_text_view);

        PairTimer.setTextView(timerTextView);

        toggleButton = (ImageButton) ret.findViewById(R.id.timer_toggle_button);
        ImageButton
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
                boolean b = false;
                try {
                    b = !PairTimer.getInstance().togglePlayPause();
                }
                catch (ParseException e) {
                    Log.wtf("debug", e.getClass().getName(), e);
                }
                toggleButton.setBackgroundResource(
                        b ? R.drawable.icon_play :
                                R.drawable.icon_paused
                );
                if (b) {
                    setEditTimeThingiesEnabled(true);
                }
                else {
                    setEditTimeThingiesEnabled(false);
                }
            }
        });

        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    PairTimer.getInstance().completeStop();
                    toggleButton.setBackgroundResource(R.drawable.icon_play);
                    setEditTimeThingiesEnabled(true);
                }
                catch (ParseException e) {
                    Log.wtf("debug", e.getClass().getName(), e);
                }
            }
        });

        return ret;
    }

    private static void setEditTimeThingiesEnabled(boolean b) {
        secondsEditTimeView.setEnabled(b);
        hoursEditTimeView.setEnabled(b);
        minutesEditTimeView.setEnabled(b);
        newTimeAcceptButton.setEnabled(b);
    }

    private void fireNewTimeSet() {
        try {
            String hours, minutes, seconds;
            hours = hoursEditTimeView.getText().toString();
            minutes = minutesEditTimeView.getText().toString();
            seconds = secondsEditTimeView.getText().toString();
            if (TextUtils.isEmpty(hours) || TextUtils.isEmpty(minutes) ||
                    TextUtils.isEmpty(seconds)) {
                Toast.makeText(getActivity().getApplicationContext(), R.string.wrong_time_format,
                        Toast.LENGTH_SHORT).show();
                return;
            }
            if (!PairTimer.getInstance().isRunning()) {
                String n = hours + ":" + minutes + ":" + seconds;
                PairTimer.setTimePreference(n);
                timerTextView.setText(n);
            }
            else {
                Toast.makeText(getActivity().getApplicationContext(), R.string.pause_the_timer,
                        Toast.LENGTH_SHORT).show();
            }
        }
        catch (ParseException e) {
            Log.wtf("debug", e.getClass().getName(), e);
        }
    }

    public static void setPaused() {
        toggleButton.setBackgroundResource(R.drawable.icon_play);
        setEditTimeThingiesEnabled(true);
    }
}
