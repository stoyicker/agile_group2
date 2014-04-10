package org.arnolds.agileappproject.agileappmodule.git.auth;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.os.Bundle;
import android.test.InstrumentationTestCase;

import org.arnolds.agileappproject.agileappmodule.ui.activities.LoginActivity;
import org.mockito.Mockito;

/**
 * Created by thrawn on 03/04/14.
 */
public class GitHubAuthenticatorTests extends InstrumentationTestCase {

    private AccountManager am;
    private GitHubAuthenticator gauth;

    public void setUp() {
        System.setProperty("dexmaker.dexcache",
                getInstrumentation().getTargetContext().getCacheDir().getAbsolutePath());

        Context c = Mockito.mock(Context.class);

        am = Mockito.mock(AccountManager.class);
        Mockito.when(AccountManager.get(c)).thenReturn(am);
        gauth = new GitHubAuthenticator(c);


    }

    public void test_getcredentials_with_no_account() {

        Account[] accs = new Account[0];
        Mockito.when(am.getAccountsByType(LoginActivity.ACCOUNT_TYPE)).thenReturn(accs);
        Bundle b = gauth.getCredentials();
        assertTrue(b == null);

    }

    public void test_getcredentials_with_account() {
        Account[] accs = new Account[1];
        String name = "Arnold";
        Account acc = new Account(name, LoginActivity.ACCOUNT_TYPE);
        accs[0] = acc;

        Mockito.when(am.getAccountsByType(LoginActivity.ACCOUNT_TYPE)).thenReturn(accs);

        Bundle b = gauth.getCredentials();
        assertTrue(b.getString(GitHubAuthenticator.USERNAME).equals(name));

    }
}
