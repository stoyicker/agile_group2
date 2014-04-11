package org.arnolds.agileappproject.agileappmodule.ui.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import org.arnolds.agileappproject.agileappmodule.R;
import org.arnolds.agileappproject.agileappmodule.git.GitHubBroker;
import org.arnolds.agileappproject.agileappmodule.git.GitHubBrokerListener;
import org.arnolds.agileappproject.agileappmodule.git.auth.GitHubAuthenticator;
import org.arnolds.agileappproject.agileappmodule.git.notifications.GitHubNotificationService;
import org.arnolds.agileappproject.agileappmodule.ui.frags.IndefiniteFancyProgressFragment;
import org.arnolds.agileappproject.agileappmodule.utils.AgileAppModuleUtils;

public class InitialActivity extends Activity {
    private final GitHubBrokerListener listener = new InitialActivityListener();

    private class InitialActivityListener extends GitHubBrokerListener {

        @Override
        public void onConnected() {
            Intent home = new Intent(getApplicationContext(), HomeActivity.class);
            startActivity(home);
            InitialActivity.this.finish();
        }

        @Override
        public void onConnectionRefused(String reason) {
            //TODO React to the reason, maybe implement a better way to report it on the wrapper
            InitialActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(InitialActivity.this.getApplicationContext(), AgileAppModuleUtils
                                    .getString(getApplicationContext(), "connection_error", null),
                            Toast.LENGTH_SHORT
                    )
                            .show();
                }
            });
            InitialActivity.this.finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        GitHubNotificationService.getInstance().setContext(this);

        GitHubAuthenticator mGitHubAuthenticator = new GitHubAuthenticator(this);
        Bundle credentials = mGitHubAuthenticator.getCredentials();

        if (GitHubBroker.getInstance().isConnected()) {
            Intent home = new Intent(getApplicationContext(), HomeActivity.class);
            startActivity(home);
            finish();
            return;
        }

        String mUsername, mPassword;

        // != null => there is a stored account.
        if (credentials != null) {
            setContentView(R.layout.activity_initial);
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
            login.putExtra(LoginActivity.LAUNCH_HOME_ACTIVITY, true);
            startActivity(login);
            finish();
        }
    }
}
