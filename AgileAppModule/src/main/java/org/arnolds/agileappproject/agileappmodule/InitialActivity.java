package org.arnolds.agileappproject.agileappmodule;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import org.arnolds.agileappproject.agileappmodule.git.GitHubBroker;
import org.arnolds.agileappproject.agileappmodule.git.IGitHubBrokerListener;
import org.kohsuke.github.GHBranch;
import org.kohsuke.github.GHIssue;
import org.kohsuke.github.GHRepository;

import java.util.Collection;


public class InitialActivity extends Activity implements IGitHubBrokerListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onConnected() {
        Log.d("debug", "connected");
    }

    @Override
    public void onConnectionRefused(String reason) {
        Log.d("debug", "connection refused: " + reason);
    }

    @Override
    public void onDisconnected() {
        Log.d("debug", "disconnected");
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
}
