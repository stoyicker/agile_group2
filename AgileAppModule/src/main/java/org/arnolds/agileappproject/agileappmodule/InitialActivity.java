package org.arnolds.agileappproject.agileappmodule;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;


/**
 * Created by andreas on 2014-04-04.
 */
public class InitialActivity extends Activity {
    public static final String LAUNCH_ACTIVITY = "launch_activity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = new Intent(this, LoginActivity.class);
        intent.putExtra(LAUNCH_ACTIVITY, LoginActivity.MAIN_ACTIVITY);
        startActivity(intent);
    }
}
