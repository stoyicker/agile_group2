package org.arnolds.agileappproject.agileappmodule.ui.frags;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import org.arnolds.agileappproject.agileappmodule.R;

/**
 * This file is part of LoLin1.
 * <p/>
 * LoLin1 is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * <p/>
 * LoLin1 is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * <p/>
 * You should have received a copy of the GNU General Public License
 * along with LoLin1. If not, see <http://www.gnu.org/licenses/>.
 * <p/>
 * Created by JorgeAntonio on 08/04/2014.
 */
public class LogInButtonFragment extends Fragment {

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
            Log.wtf("debug", e.getClass().getName(), e);
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
