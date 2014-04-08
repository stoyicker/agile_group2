package org.arnolds.agileappproject.agileappmodule.git;

import org.kohsuke.github.GHBranch;
import org.kohsuke.github.GHIssue;
import org.kohsuke.github.GHRepository;

import java.util.Collection;

public interface IGitHubBrokerListener {
    /**
     * Positive callback for connect.
     */
    public void onConnected();

    /**
     * Negative callback for connect.
     *
     * @param reason {@link String} Reason of the failure.
     */
    public void onConnectionRefused(String reason);

    /**
     * Callback for getAllIssues.
     *
     * @param success {@link String} The success of the operation.
     * @param issues  {@link java.util.Collection.<org.kohsuke.github.GHIssue></org.kohsuke.github.GHIssue>} The collection of issues
     */
    public void onAllIssuesRetrieved(boolean success, Collection<GHIssue> issues);

    /**
     * Callback for getAllBranches.
     *
     * @param success  {@link String} The success of the operation.
     * @param branches {@link java.util.Collection.<org.kohsuke.github.GHBranch></org.kohsuke.github.GHBranch>} The collection of branches.
     */
    public void onAllBranchesRetrieved(boolean success, Collection<GHBranch> branches);

    /**
     * Callback for getAllRepos.
     *
     * @param success {@link String} The success of the operation.
     * @param repos   {@link java.util.Collection.<org.kohsuke.github.GHRepository></org.kohsuke.github.GHRepository>} The collection of repositories.
     */
    public void onAllReposRetrieved(boolean success, Collection<GHRepository> repos);

    /**
     * Callback for selectRepo.
     *
     * @param result {@link java.lang.Boolean} The success of the operation.
     */
    public void onRepoSelected(boolean result);
}
