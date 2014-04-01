package org.arnolds.agileappproject.agileappmodule.auth;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import org.arnolds.agileappproject.agileappmodule.auth.GitHubAuthenticator;

public class GitHubAuthenticatorService extends Service {

    @Override
    public IBinder onBind(Intent intent) {

        GitHubAuthenticator mGitHubAuthenticator = new GitHubAuthenticator(this);
        return  mGitHubAuthenticator.getIBinder();
    }
}
