package org.arnolds.agileappproject.agileappmodule;

import android.accounts.Account;
import android.accounts.AccountAuthenticatorActivity;
import android.accounts.AccountManager;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.arnolds.agileappproject.agileappmodule.git.GitHubBroker;
import org.arnolds.agileappproject.agileappmodule.git.IGitHubBroker;
import org.arnolds.agileappproject.agileappmodule.git.IGitHubBrokerListener;
import org.arnolds.agileappproject.agileappmodule.git.auth.GitHubAuthenticator;
import org.arnolds.agileappproject.agileappmodule.ui.activities.HomeActivity;
import org.kohsuke.github.GHBranch;
import org.kohsuke.github.GHIssue;
import org.kohsuke.github.GHRepository;

import java.util.Collection;


/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AccountAuthenticatorActivity {

    public final static String ARG_ACCOUNT_TYPE = "ACCOUNT_TYPE";
    public final static String ARG_AUTH_TYPE = "AUTH_TYPE";
    public final static String ARG_IS_ADDING_NEW_ACCOUNT = "IS_ADDING_ACCOUNT";
    public static final String ACCOUNT_TYPE = "org.arnolds.agileappproject.agileappmodule.account";
    public static final int MAIN_ACTIVITY = 0;

    // UI references.
    private EditText mUsernameView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;
    private AccountManager mAccountManager;
    private String mUsername;
    private String mPassword;
    private Context mContext;
    private Bundle credentials;
    private int mNextActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        GitHubAuthenticator mGitHubAuthenticator = new GitHubAuthenticator(this);
        credentials = mGitHubAuthenticator.getCredentials();


        setContentView(R.layout.activity_login);

        mContext = getBaseContext();

        mAccountManager = AccountManager.get(getBaseContext());

        mNextActivity = getIntent().getIntExtra(InitialActivity.LAUNCH_ACTIVITY, -1);

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

        Button mSignInButton = (Button) findViewById(R.id.sign_in_button);
        mSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);
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
            showProgress(true);
            submit();
        }
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 4;
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    public void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        }
        else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }


    public void submit() {

        final IGitHubBroker mGitHubBroker = GitHubBroker.getInstance();
        try {
            mGitHubBroker.addSubscriber(new IGitHubBrokerListener() {

                @Override
                public void onConnected() {
                    Log.d("DEBUG", "onConnected");
                    runOnUiThread(new Runnable() {
                        public void run() {
                            showProgress(false);
                            Toast.makeText(mContext, "Connected",
                                    Toast.LENGTH_SHORT).show();

                            switch (mNextActivity) {
                                //TODO remove magic strings
                                case MAIN_ACTIVITY:
                                    startActivity(
                                            new Intent(LoginActivity.this, HomeActivity.class));
                                    break;
                                default:
                            }


                            //if an account already exists skip finishLogin()
                            if (credentials != null) {
                                finish();
                            }
                            finishLogin();
                        }
                    });
                }

                @Override
                public void onConnectionRefused(String reason) {

                    runOnUiThread(new Runnable() {
                        public void run() {
                            showProgress(false);
                            Toast.makeText(mContext, "Connection refused",
                                    Toast.LENGTH_SHORT).show();
                            mPasswordView.setError(getString(R.string.error_incorrect_password));
                            mPasswordView.requestFocus();
                        }
                    });

                }

                @Override
                public void onDisconnected() {

                }

                @Override
                public void onAllIssuesRetrieved(boolean success, Collection<GHIssue> issues) {

                }

                @Override
                public void onAllBranchesRetrieved(boolean success, Collection<GHBranch> branches) {

                }

                @Override
                public void onAllReposRetrieved(boolean success, Collection<GHRepository> repos) {

                }

                @Override
                public void onRepoSelected(boolean result) {

                }
            });
        }
        catch (GitHubBroker.NullArgumentException e) {
            e.printStackTrace();
        }
        catch (GitHubBroker.ListenerAlreadyRegisteredException e) {
            e.printStackTrace();
        }

        try {
            mGitHubBroker.connect(mUsername, mPassword, this);
        }
        catch (GitHubBroker.AlreadyConnectedException e) {
            e.printStackTrace();
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
        finish();
    }
}



