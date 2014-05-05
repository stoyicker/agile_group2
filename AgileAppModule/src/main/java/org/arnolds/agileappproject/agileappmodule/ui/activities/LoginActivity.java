package org.arnolds.agileappproject.agileappmodule.ui.activities;

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
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.arnolds.agileappproject.agileappmodule.R;
import org.arnolds.agileappproject.agileappmodule.git.GitHubBroker;
import org.arnolds.agileappproject.agileappmodule.git.GitHubBrokerListener;
import org.arnolds.agileappproject.agileappmodule.git.IGitHubBroker;
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
    public static final String ACCOUNT_TYPE = "org.arnolds.agileappproject.agileappmodule.account";
    public static final String LAUNCH_HOME_ACTIVITY = "LAUNCH_HOME";
    private static final int MINIMUM_PASSWORD_LENGTH = 7;
    private Boolean launchHome = false;

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
        InputMethodManager inputManager = (InputMethodManager)
                getSystemService(Context.INPUT_METHOD_SERVICE);

        inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                InputMethodManager.HIDE_NOT_ALWAYS);
        attemptLogin();
    }

    private class InnerListener extends GitHubBrokerListener {

        @Override
        public void onConnected() {
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
                    getFragmentManager().executePendingTransactions();
                    Toast.makeText(LoginActivity.this.getApplicationContext(), AgileAppModuleUtils
                                    .getString(getApplicationContext(), "connection_error",
                                            null),
                            Toast.LENGTH_SHORT
                    )
                            .show();
                    mUsernameView.setEnabled(Boolean.TRUE);
                    mPasswordView.setEnabled(Boolean.TRUE);
                    mUsernameView.clearFocus();
                    mPasswordView.clearFocus();
                    mPasswordView.setError(getString(R.string.error_incorrect_password));
                }
            });
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        listener = new InnerListener();

        getFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new LogInButtonFragment()).commit();
        getFragmentManager().executePendingTransactions();

        launchHome = getIntent().getBooleanExtra(LAUNCH_HOME_ACTIVITY, false);

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

        if (TextUtils.isEmpty(mPassword) || !isGitHubPasswordValid(mPassword)) {
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

    private boolean isGitHubPasswordValid(String password) {
        //Implemented real GitHub credentials validation
        return password.length() >= MINIMUM_PASSWORD_LENGTH;
            /* && password.matches(".*\\d.*") &&
           password.matches(".*[a-z].*"); */
    }

    public void submit() {
        mUsernameView.setEnabled(Boolean.FALSE);
        mPasswordView.setEnabled(Boolean.FALSE);
        getFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new IndefiniteFancyProgressFragment())
                .addToBackStack("").commit();
        getFragmentManager().executePendingTransactions();
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

        if (launchHome) {
            startActivity(new Intent(getApplicationContext(), HomeActivity.class));
        }
        finish();
    }
}



