package org.arnolds.agileappproject.agileappmodule.git;

import org.kohsuke.github.GHBranch;
import org.kohsuke.github.GHCommit;
import org.kohsuke.github.GHIssue;
import org.kohsuke.github.GHRepository;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Basic implementation of the {@link org.arnolds.agileappproject.agileappmodule.git.IGitHubBrokerListener} interface.
 */
public abstract class GitHubBrokerListener implements IGitHubBrokerListener {
    @Override
    public void onConnected() {

    }

    @Override
    public void onConnectionRefused(String reason) {

    }

    @Override
    public void onAllIssuesRetrieved(boolean success, Collection<GHIssue> issues) {

    }

    @Override
    public void onAllBranchesRetrieved(boolean success, Collection<GHBranch> branches) {

    }

    @Override
    public void onAllCommitsRetrieved(boolean result, LinkedHashMap<String, GHCommit> commits) {

    }

    @Override
    public void onAllReposRetrieved(boolean success, Collection<GHRepository> repos) {

    }

    @Override
    public void onRepoSelected(boolean result) {

    }

    @Override
    public void onIssueCreation(boolean result, GHIssue issue) {

    }
}
