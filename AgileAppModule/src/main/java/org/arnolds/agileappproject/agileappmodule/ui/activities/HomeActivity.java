package org.arnolds.agileappproject.agileappmodule.ui.activities;

import android.os.Bundle;
import android.util.Log;

import org.arnolds.agileappproject.agileappmodule.R;

public class HomeActivity extends DrawerLayoutFragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (savedInstanceState == null) {
            savedInstanceState = new Bundle();
        }
        savedInstanceState.putInt("layout", R.layout.activity_home);
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onNewRepoSelected(String repoName) {
        //TODO Show the commit history of the given repo
        Log.d("debug", "Selected repo " + repoName);
    }
}
