package org.arnolds.agileappproject.agileappmodule.git;

import org.kohsuke.github.GHRepository;

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
     * @param callback {@link org.arnolds.agileappproject.agileappmodule.git.IGitHubBroker} Callback receiver, if null no callback will be made.
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
     * Asynchronously selects a repo to work with, giving response on the provided callback.
     *
     * @param repo     {@link org.kohsuke.github.GHRepository} The repository to select.
     * @param callback {@link org.arnolds.agileappproject.agileappmodule.git.IGitHubBroker} Callback receiver, if null no callback will be made.
     * @throws {@link org.arnolds.agileappproject.agileappmodule.git.GitHubBroker.NullArgumentException} If repo is null.
     * @throws {@link org.arnolds.agileappproject.agileappmodule.git.GitHubBroker.AlreadyNotConnectedException} If there is not a connected session.
     */
    public void selectRepo(GHRepository repo, IGitHubBrokerListener callback)
            throws GitHubBroker.AlreadyNotConnectedException, GitHubBroker.NullArgumentException;

    /**
     * Asynchronously return all branches of the working repository, giving response on the provided callback.
     *
     * @param callback {@link org.arnolds.agileappproject.agileappmodule.git.IGitHubBroker} Callback receiver, if null no callback will be made.
     * @throws {@link org.arnolds.agileappproject.agileappmodule.git.GitHubBroker.RepositoryNotSelectedException} If there is not a working repo selected.
     * @throws {@link org.arnolds.agileappproject.agileappmodule.git.GitHubBroker.AlreadyNotConnectedException} If there is not a connected session.
     */
    public void getAllBranches(IGitHubBrokerListener callback)
            throws GitHubBroker.RepositoryNotSelectedException,
            GitHubBroker.AlreadyNotConnectedException;

    /**
     * Asynchronously return all repos of the currently logged in user, giving response on the provided callback.
     *
     * @param callback {@link org.arnolds.agileappproject.agileappmodule.git.IGitHubBroker} Callback receiver, if null no callback will be made.
     * @throws {@link org.arnolds.agileappproject.agileappmodule.git.GitHubBroker.AlreadyNotConnectedException} If there is not a connected session.
     */
    public void getAllRepos(IGitHubBrokerListener callback)
            throws GitHubBroker.AlreadyNotConnectedException;

    /**
     * Asynchronously return all issues of the working repository, giving response on the provided callback.
     *
     * @param callback {@link org.arnolds.agileappproject.agileappmodule.git.IGitHubBroker} Callback receiver, if null no callback will be made.
     * @throws {@link org.arnolds.agileappproject.agileappmodule.git.GitHubBroker.RepositoryNotSelectedException} If there is not a working repo selected.
     * @throws {@link org.arnolds.agileappproject.agileappmodule.git.GitHubBroker.AlreadyNotConnectedException} If there is not a connected session.
     */
    public void getAllIssues(IGitHubBrokerListener callback)
            throws GitHubBroker.RepositoryNotSelectedException,
            GitHubBroker.AlreadyNotConnectedException;
}
