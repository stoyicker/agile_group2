package org.arnolds.agileappproject.agileappmodule.git;

import org.arnolds.agileappproject.agileappmodule.git.wrappers.GitBranch;
import org.arnolds.agileappproject.agileappmodule.git.wrappers.GitCommit;
import org.kohsuke.github.GHBranch;
import org.kohsuke.github.GHCommit;

import java.util.List;
import java.util.Map;


import org.kohsuke.github.GHCommit;
import org.kohsuke.github.GHRepository;

import java.io.IOException;


public interface IGitHubBroker {

    /**
     * Returns the connection state.
     *
     * @return true if there is a session connected; otherwise false.
     */
    public boolean isConnected();

    /**
     * Asynchronously connects to GitHub, giving response on the provided callback.
     *
     * @param username {@link String} GitHub username.
     * @param password {@link String} GitHub password.
     * @param callback {@link org.arnolds.agileappproject.agileappmodule.git.IGitHubBrokerListener} Callback receiver, if null no callback will be made.
     * @throws {@link org.arnolds.agileappproject.agileappmodule.git.GitHubBroker.AlreadyConnectedException} If already connected.
     */
    public void connect(String username, String password, IGitHubBrokerListener callback)
            throws GitHubBroker.AlreadyConnectedException;

    /**
     * Disconnects the current session synchronously and immediately.
     *
     * @throws {@link org.arnolds.agileappproject.agileappmodule.git.GitHubBroker.AlreadyNotConnectedException} If there is not a connected session.
     */
    public void disconnect() throws GitHubBroker.AlreadyNotConnectedException;

    /**
     * Returns the name of the currently selected repo. Returns null if no repo has been selected.
     * @return String representation of the currently selected repo.
     */
    public String getSelectedRepoName();

    /**
     * Creates an issue in the currently selected repository.
     *
     * @param title    {@link String} Title of the issue.
     * @param body     {@link String} The body of the issue. If null, it is ignored.
     * @param assignee {@link String} The assignee of the issue. If null, it is ignored.
     * @param callback {@link org.arnolds.agileappproject.agileappmodule.git.IGitHubBrokerListener} Callback receiver, if null no callback will be made.
     * @throws org.arnolds.agileappproject.agileappmodule.git.GitHubBroker.AlreadyNotConnectedException   Thrown when not connected to github.
     * @throws org.arnolds.agileappproject.agileappmodule.git.GitHubBroker.RepositoryNotSelectedException Thrown when no repository connected.
     * @throws {@link                                                                                     org.arnolds.agileappproject.agileappmodule.git.GitHubBroker.NullArgumentException} Thrown if title is null.
     * @throws {@link                                                                                     IllegalArgumentException} Thrown if title.length() == 0.
     */
    public void createIssue(String title, String body, String assignee,
                            IGitHubBrokerListener callback) throws
            GitHubBroker.AlreadyNotConnectedException, GitHubBroker.RepositoryNotSelectedException,
            GitHubBroker.NullArgumentException, IllegalArgumentException;

    /**
     * Asynchronously selects a repo to work with, giving response on the provided callback.
     *
     * @param repoName {@link String} The name of the repository to select.
     * @param callback {@link org.arnolds.agileappproject.agileappmodule.git.IGitHubBrokerListener} Callback receiver, if null no callback will be made.
     * @throws {@link org.arnolds.agileappproject.agileappmodule.git.GitHubBroker.NullArgumentException} If repo is null.
     * @throws {@link org.arnolds.agileappproject.agileappmodule.git.GitHubBroker.AlreadyNotConnectedException} If there is not a connected session.
     */
    public void selectRepo(String repoName, IGitHubBrokerListener callback)
            throws GitHubBroker.AlreadyNotConnectedException, GitHubBroker.NullArgumentException;

    /**
     * Returns all branches in the select repo.
     */
    public Map<String, GitBranch> getAllBranches();


    /**
     * Asynchronously return all repos of the currently logged in user, giving response on the provided callback.
     *
     * @param callback {@link org.arnolds.agileappproject.agileappmodule.git.IGitHubBrokerListener} Callback receiver, if null no callback will be made.
     * @throws {@link org.arnolds.agileappproject.agileappmodule.git.GitHubBroker.AlreadyNotConnectedException} If there is not a connected session.
     */
    public void getAllRepos(IGitHubBrokerListener callback)
            throws GitHubBroker.AlreadyNotConnectedException;

    /**
     * Asynchronously returns all issues of the working repository, giving response on the provided callback.
     *
     * @param callback {@link org.arnolds.agileappproject.agileappmodule.git.IGitHubBrokerListener} Callback receiver, if null no callback will be made.
     * @throws {@link org.arnolds.agileappproject.agileappmodule.git.GitHubBroker.RepositoryNotSelectedException} If there is not a working repo selected.
     * @throws {@link org.arnolds.agileappproject.agileappmodule.git.GitHubBroker.AlreadyNotConnectedException} If there is not a connected session.
     */
    public void getAllIssues(IGitHubBrokerListener callback)
            throws GitHubBroker.RepositoryNotSelectedException,
            GitHubBroker.AlreadyNotConnectedException;


    /**
     * Asynchronously returns all commits in the current branch, giving response on the provided callback.
     *
     * @param callback {@link org.arnolds.agileappproject.agileappmodule.git.IGitHubBrokerListener} Callback receiver, if null no callback will be made.
     * @throws {@link org.arnolds.agileappproject.agileappmodule.git.GitHubBroker.RepositoryNotSelectedException} If there is not a working repo selected.
     * @throws {@link org.arnolds.agileappproject.agileappmodule.git.GitHubBroker.AlreadyNotConnectedException} If not connected.
     */
    public void getAllCommits(IGitHubBrokerListener callback) throws GitHubBroker.RepositoryNotSelectedException,
            GitHubBroker.AlreadyNotConnectedException;


    public GHCommit getCommit(String hash) throws IOException;

    /**
     * Gets the selected branch.
     * @return the selected branch or null if no branch is selected.
     */
    public GitBranch getSelectedBranch();

    /**
     * Sets the selected branch.
     * @param branchName the name of the branch that should be selected.
     */
    public void setSelectedBranch(String branchName);



    public void fetchNewCommits(IGitHubBrokerListener callback) throws GitHubBroker.RepositoryNotSelectedException,
            GitHubBroker.AlreadyNotConnectedException;

    /**
     *
     * @return Map<String,GHCommit>, Contains all commits in selected repo. Returns empty if there are no commits.
     */
    public Map<String,GitCommit> getCurrentCommitList();

    /**
     * Get all commits from the currently selected branch.
     *
     * @return List<GitCommit> the commits
     */
    public List<GitCommit> getCommitsFromSelectedBranch();

    /**
     *
     * @return Map<String,GHRepository>, Contains all the user's repositories.
     */
    public Map<String,GHRepository> getCurrentRepositories();

}
