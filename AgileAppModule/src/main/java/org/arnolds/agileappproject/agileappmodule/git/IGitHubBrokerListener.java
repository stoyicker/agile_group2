package org.arnolds.agileappproject.agileappmodule.git;

import org.kohsuke.github.GHBranch;
import org.kohsuke.github.GHIssue;
import org.kohsuke.github.GHRepository;

import java.util.Collection;

public interface IGitHubBrokerListener {
    public void onConnected();

    public void onConnectionRefused(String reason);

    public void onDisconnected();

    public void onAllIssuesRetrieved(boolean success, Collection<GHIssue> issues);

    public void onAllBranchesRetrieved(boolean success, Collection<GHBranch> branches);

    public void onAllReposRetrieved(boolean success, Collection<GHRepository> repos);

    public void onRepoSelected(boolean result);
}
