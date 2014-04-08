package org.arnolds.agileappproject.agileappmodule.git;

import org.kohsuke.github.GHBranch;
import org.kohsuke.github.GHIssue;
import org.kohsuke.github.GHRepository;

import java.util.Collection;

/**
 * Basic implementation of the {@link org.arnolds.agileappproject.agileappmodule.git.IGitHubBrokerListener} interface.
 */
public abstract class GitHubBrokerListener implements IGitHubBrokerListener {
    /**
     * Positive callback for connect.
     */
    @Override
    public void onConnected() {

    }

    /**
     * Negative callback for connect.
     *
     * @param reason {@link String} Reason of the failure.
     */
    @Override
    public void onConnectionRefused(String reason) {
    }

    /**
     * Callback for getAllIssues.
     *
     * @param success {@link String} The success of the operation.
     * @param issues  {@link java.util.Collection.<org.kohsuke.github.GHIssue></org.kohsuke.github.GHIssue>} The collection of issues
     */
    @Override
    public void onAllIssuesRetrieved(boolean success, Collection<GHIssue> issues) {

    }

    /**
     * Callback for getAllBranches.
     *
     * @param success  {@link String} The success of the operation.
     * @param branches {@link java.util.Collection.<org.kohsuke.github.GHBranch></org.kohsuke.github.GHBranch>} The collection of branches.
     */
    @Override
    public void onAllBranchesRetrieved(boolean success, Collection<GHBranch> branches) {

    }

    /**
     * Callback for getAllRepos.
     *
     * @param success {@link String} The success of the operation.
     * @param repos   {@link java.util.Collection.<org.kohsuke.github.GHRepository></org.kohsuke.github.GHRepository>} The collection of repositories.
     */
    @Override
    public void onAllReposRetrieved(boolean success, Collection<GHRepository> repos) {

    }

    /**
     * Callback for selectRepo.
     *
     * @param result {@link Boolean} The success of the operation.
     */
    @Override
    public void onRepoSelected(boolean result) {

    }
}
