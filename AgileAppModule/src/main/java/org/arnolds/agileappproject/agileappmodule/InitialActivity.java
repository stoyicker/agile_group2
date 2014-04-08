package org.arnolds.agileappproject.agileappmodule;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import org.arnolds.agileappproject.agileappmodule.git.GitHubBroker;
import org.arnolds.agileappproject.agileappmodule.git.GitHubBrokerListener;
import org.arnolds.agileappproject.agileappmodule.git.auth.GitHubAuthenticator;
import org.arnolds.agileappproject.agileappmodule.ui.activities.HomeActivity;
import org.arnolds.agileappproject.agileappmodule.ui.frags.IndefiniteFancyProgressFragment;
import org.arnolds.agileappproject.agileappmodule.utils.AgileAppModuleUtils;


public class InitialActivity extends Activity {
    private final GitHubBrokerListener listener = new InitialActivityListener();

    private class InitialActivityListener extends GitHubBrokerListener {

        @Override
        public void onConnected() {
            Intent home = new Intent(getApplicationContext(), HomeActivity.class);
            startActivity(home);
            finish();
        }

        @Override
        public void onConnectionRefused(String reason) {
            //TODO React to the reason, maybe implement a better way to report it on the wrapper
            InitialActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getApplicationContext(), AgileAppModuleUtils
                                    .getString(getApplicationContext(), "connection_error", null),
                            Toast.LENGTH_SHORT
                    )
                            .show();
                }
            });
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_initial);
//        Intent intent = new Intent(this, LoginActivity.class);
//        intent.putExtra(LAUNCH_ACTIVITY, LoginActivity.MAIN_ACTIVITY);
//        startActivity(intent);

//        startActivity(new Intent(this, HomeActivity.class));

        GitHubAuthenticator mGitHubAuthenticator = new GitHubAuthenticator(this);
        Bundle credentials = mGitHubAuthenticator.getCredentials();

        String mUsername, mPassword;

        // != null => there is a stored account.
        if (credentials != null) {
            getFragmentManager().beginTransaction()
                    .add(new IndefiniteFancyProgressFragment(), "progress").commit();

            mUsername = credentials.getString(GitHubAuthenticator.USERNAME);
            mPassword = credentials.getString(GitHubAuthenticator.PASSWORD);
            try {
                GitHubBroker.getInstance().connect(mUsername, mPassword, listener);
            }
            catch (GitHubBroker.AlreadyConnectedException e) {
                Log.wtf("debug", e.getClass().getName(), e);
            }
        }
        else {
            Intent login = new Intent(getApplicationContext(), LoginActivity.class);
            login.putExtra(LoginActivity.LAUNCH_HOME_ACTIVTY, true);
            startActivity(login);
            finish();
        }

        /**
         *TODO
         *  Intent firstActivity;
         *  if(!authenticated){
         *  firstActivity = new Intent(this,LoginActivity.class);
         * } else{
         *  connect();
         *  ...onConnnected(){
         *  firstActivity = new Intent(this,LoadingActivity.class);
         *  }
         *  }
         *  startActivity(firstActivity);
         */
    }
}
