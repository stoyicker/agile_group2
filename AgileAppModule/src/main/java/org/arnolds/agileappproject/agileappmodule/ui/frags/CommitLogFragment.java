package org.arnolds.agileappproject.agileappmodule.ui.frags;



import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.arnolds.agileappproject.agileappmodule.R;
import org.kohsuke.github.GHCommit;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;

public class CommitLogFragment extends Fragment implements PropertyChangeListener {
    private List<GHCommit> mCommitList;

    public CommitLogFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_commit_log, container, false);
    }


    @Override
    public void propertyChange(PropertyChangeEvent event) {
        mCommitList = (List<GHCommit>) event.getNewValue();
    }
}
