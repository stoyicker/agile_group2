package org.arnolds.agileappproject.agileappmodule.ui.activities;

import android.content.res.Configuration;
import android.os.Bundle;

import org.arnolds.agileappproject.agileappmodule.R;

public class HomeActivity extends DrawerLayoutFragmentActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (savedInstanceState == null) {
            savedInstanceState = new Bundle();
        }
        savedInstanceState.putInt("layout", R.layout.activity_home);
        savedInstanceState.putInt("main_fragment_container", R.id.main_fragment_container);
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public void onIssueCreated() {
        super.notifyIssueCreated();
    }
}
