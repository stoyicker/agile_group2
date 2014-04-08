package org.arnolds.agileappproject.agileappmodule;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import org.arnolds.agileappproject.agileappmodule.git.auth.GitHubAuthenticator;
import org.arnolds.agileappproject.agileappmodule.ui.activities.HomeActivity;


public class InitialActivity extends Activity {
    public static final String LAUNCH_ACTIVITY = "launch_activity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        Intent intent = new Intent(this, LoginActivity.class);
//        intent.putExtra(LAUNCH_ACTIVITY, LoginActivity.MAIN_ACTIVITY);
//        startActivity(intent);

        startActivity(new Intent(this, HomeActivity.class));

//        GitHubAuthenticator mGitHubAuthenticator = new GitHubAuthenticator(this);
//        Bundle credentials = mGitHubAuthenticator.getCredentials();
//
//        String mUsername, mPassword;
//
//        // != null => there is a stored account.
//        if (credentials != null) {
//            mUsername = credentials.getString(GitHubAuthenticator.USERNAME);
//            mPassword = credentials.getString(GitHubAuthenticator.PASSWORD);
//            showProgress(true);
//            submit();
//
//        }

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
