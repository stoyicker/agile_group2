package org.arnolds.agileappproject.agileappmodule.ui.frags;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import org.arnolds.agileappproject.agileappmodule.R;

public class LogInButtonFragment extends android.app.Fragment {

    private Button mButton;
    private LogInButtonFragmentCallbacks mCallback;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            mCallback = (LogInButtonFragmentCallbacks) activity;
        }
        catch (ClassCastException e) {
            Log.wtf("debug", "You have to implement LogInButtonFragmentCallbacks", e);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View ret = inflater.inflate(R.layout.fragment_log_in_button, container,
                Boolean.FALSE);

        mButton = (Button) ret.findViewById(R.id.log_in_button);

        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LogInButtonFragment.this.mCallback.onClick();
            }
        });

        return ret;
    }

    public interface LogInButtonFragmentCallbacks {
        public void onClick();
    }
}
