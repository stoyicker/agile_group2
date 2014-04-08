package org.arnolds.agileappproject.agileappmodule;

import android.accounts.Account;
import android.accounts.AccountAuthenticatorActivity;
import android.accounts.AccountManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.arnolds.agileappproject.agileappmodule.git.GitHubBroker;
import org.arnolds.agileappproject.agileappmodule.git.GitHubBrokerListener;
import org.arnolds.agileappproject.agileappmodule.git.IGitHubBroker;
import org.arnolds.agileappproject.agileappmodule.ui.activities.HomeActivity;
import org.arnolds.agileappproject.agileappmodule.ui.frags.IndefiniteFancyProgressFragment;
import org.arnolds.agileappproject.agileappmodule.ui.frags.LogInButtonFragment;
import org.arnolds.agileappproject.agileappmodule.utils.AgileAppModuleUtils;


/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AccountAuthenticatorActivity implements
        LogInButtonFragment.LogInButtonFragmentCallbacks {

    public final static String ARG_ACCOUNT_TYPE = "ACCOUNT_TYPE";
    public final static String ARG_AUTH_TYPE = "AUTH_TYPE";
    //    public final static String ARG_IS_ADDING_NEW_ACCOUNT = "IS_ADDING_ACCOUNT";
    public static final String ACCOUNT_TYPE = "org.arnolds.agileappproject.agileappmodule.account";
    public static final String LAUNCH_HOME_ACTIVTY = "launch_home_activity";
    public Boolean launchHome = false;

    // UI references.
    private EditText mUsernameView;
    private EditText mPasswordView;
    private AccountManager mAccountManager;
    private String mUsername;
    private String mPassword;
    private Context mContext;
    private InnerListener listener;

    @Override
    public void onClick() {
        attemptLogin();
    }

    private class InnerListener extends GitHubBrokerListener {

        @Override
        public void onConnected() {
            Log.d("debug", "onConnected");
            runOnUiThread(new Runnable() {
                public void run() {
                    Toast.makeText(mContext, AgileAppModuleUtils
                                    .getString(getApplicationContext(),
                                            "connection_success", null),
                            Toast.LENGTH_SHORT
                    ).show();
                    finishLogin();
                }
            });
        }

        @Override
        public void onConnectionRefused(String reason) {
            getFragmentManager().popBackStack();
            runOnUiThread(new Runnable() {
                public void run() {
                    Toast.makeText(getApplicationContext(), AgileAppModuleUtils
                                    .getString(getApplicationContext(), "connection_error",
                                            null),
                            Toast.LENGTH_SHORT
                    )
                            .show();
                    mPasswordView.setError(getString(R.string.error_incorrect_password));
                    mPasswordView.requestFocus();
                }
            });

        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        launchHome = getIntent().getBooleanExtra(LAUNCH_HOME_ACTIVTY, false);

        listener = new InnerListener();

        launchHome = getIntent().getBooleanExtra(LAUNCH_HOME_ACTIVTY, false);

        setContentView(R.layout.activity_login);

        mContext = getBaseContext();

        mAccountManager = AccountManager.get(getBaseContext());

        // Set up the login form.
        mUsernameView = (EditText) findViewById(R.id.username);


        mPasswordView = (EditText) findViewById(R.id.password);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

//        mProgressView = findViewById(R.id.login_progress);
    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    public void attemptLogin() {


        // Reset errors.
        mUsernameView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        mUsername = mUsernameView.getText().toString();
        mPassword = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;


        // Check for a valid password
        if (TextUtils.isEmpty(mPassword) || !isPasswordValid(mPassword)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }


        if (TextUtils.isEmpty(mUsername)) {
            mUsernameView.setError(getString(R.string.error_field_required));
            focusView = mUsernameView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        }
        else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            submit();
        }
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 4;
    }

//    /**
//     * Shows the progress UI and hides the login form.
//     */
//    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
//    public void showProgress(final boolean show) {
//        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
//        // for very easy animations. If available, use these APIs to fade-in
//        // the progress spinner.
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
//            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);
//
//            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
//            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
//                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
//                @Override
//                public void onAnimationEnd(Animator animation) {
//                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
//                }
//            });
//
//            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
//            mProgressView.animate().setDuration(shortAnimTime).alpha(
//                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
//                @Override
//                public void onAnimationEnd(Animator animation) {
//                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
//                }
//            });
//        }
//        else {
//            // The ViewPropertyAnimator APIs are not available, so simply show
//            // and hide the relevant UI components.
//            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
//            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
//        }
//    }


    public void submit() {
        getFragmentManager().beginTransaction()
                .replace(R.id.log_in_button_fragment, new IndefiniteFancyProgressFragment())
                .addToBackStack("").commit();
        final IGitHubBroker mGitHubBroker = GitHubBroker.getInstance();
        try {
            mGitHubBroker.connect(mUsername, mPassword, listener);
        }
        catch (GitHubBroker.AlreadyConnectedException e) {
            Log.wtf("debug", e.getClass().getName(), e);
        }
    }

    private void finishLogin() {

        final Account account = new Account(mUsername, ACCOUNT_TYPE);

        Bundle bundle = new Bundle();
        Intent intent = new Intent();
        intent.putExtras(bundle);


        // Creating the account on the device and setting the auth token we got
        // (Not setting the auth token will cause another call to the server to authenticate the user)
        mAccountManager.addAccountExplicitly(account, mPassword, null);
        //mAccountManager.setAuthToken(account, TOKEN_TYPE, token);

        setAccountAuthenticatorResult(intent.getExtras());
        setResult(RESULT_OK, intent);

        Log.d("LoginActivity", "finished login");
        //Log.d("LoginActivity", "token for: "+mUsername+"="+token);
        if (launchHome) {
            Log.d("debug", "I'M IN");
            startActivity(new Intent(getApplicationContext(), HomeActivity.class));
        }
        finish();
    }
}



