package org.arnolds.agileappproject.agileappmodule.git;

import android.content.Context;

import org.kohsuke.github.GHRepository;

public interface IGitHubBroker {

    /**
     * Returns the connection state.
     *
     * @return true if there is a session connected; false otherwise.
     */
    public boolean isConnected();

    /**
     * Asynchronously connects to GitHub.
     *
     * @param username {@link String} GitHub username.
     * @param password {@link String} GitHub password.
     * @param context  {@link android.content.Context} Context of the application. Use getApplicationContext() {@link android.app.Activity} object.
     * @throws {@link org.arnolds.agileappproject.agileappmodule.git.GitHubBroker.AlreadyConnectedException} If already connected.
     */
    public void connect(String username, String password, Context context)
            throws GitHubBroker.AlreadyConnectedException;

    /**
     * Disconnects the current session.
     *
     * @throws {@link org.arnolds.agileappproject.agileappmodule.git.GitHubBroker.AlreadyNotConnectedException} If there is not a connected session.
     */
    public void disconnect() throws GitHubBroker.AlreadyNotConnectedException;

    /**
     * Subscribes an object to the broker.
     *
     * @param listener {@link IGitHubBrokerListener} The object that wants to addSubscriber.
     * @throws {@link org.arnolds.agileappproject.agileappmodule.git.GitHubBroker.NullArgumentException} If listener is null.
     * @throws {@link org.arnolds.agileappproject.agileappmodule.git.GitHubBroker.ListenerAlreadyRegisteredException} If the listener is already susbscribed.
     */
    public void addSubscriber(IGitHubBrokerListener listener)
            throws GitHubBroker.NullArgumentException,
            GitHubBroker.ListenerAlreadyRegisteredException;

    /**
     * Removes the suscription to the broker.
     *
     * @param listener {@link IGitHubBrokerListener} The object that wants to removeSubscriber.
     * @throws {@link org.arnolds.agileappproject.agileappmodule.git.GitHubBroker.NullArgumentException} If listener is null.
     * @throws {@link org.arnolds.agileappproject.agileappmodule.git.GitHubBroker.ListenerNotRegisteredException} If listener is not subscribed.
     */
    public void removeSubscriber(IGitHubBrokerListener listener)
            throws GitHubBroker.ListenerNotRegisteredException, GitHubBroker.NullArgumentException;


    /**
     * Asynchronously selects a repo to work with.
     *
     * @param repo {@link org.kohsuke.github.GHRepository} The repository to select.
     * @throws {@link org.arnolds.agileappproject.agileappmodule.git.GitHubBroker.NullArgumentException} If repo is null.
     * @throws {@link org.arnolds.agileappproject.agileappmodule.git.GitHubBroker.AlreadyNotConnectedException} If there is not a connected session.
     */
    public void selectRepo(GHRepository repo)
            throws GitHubBroker.AlreadyNotConnectedException, GitHubBroker.NullArgumentException;

    /**
     * Asynchronously return all branches of the working repository.
     *
     * @throws {@link org.arnolds.agileappproject.agileappmodule.git.GitHubBroker.RepositoryNotSelectedException} If there is not a working repo selected.
     * @throws {@link org.arnolds.agileappproject.agileappmodule.git.GitHubBroker.AlreadyNotConnectedException} If there is not a connected session.
     */
    public void getAllBranches() throws GitHubBroker.RepositoryNotSelectedException,
            GitHubBroker.AlreadyNotConnectedException;

    /**
     * Asynchronously return all repos of the currently logged in user.
     *
     * @throws {@link org.arnolds.agileappproject.agileappmodule.git.GitHubBroker.AlreadyNotConnectedException} If there is not a connected session.
     */
    public void getAllRepos() throws GitHubBroker.AlreadyNotConnectedException;

    /**
     * Asynchronously return all issues of the working repository.
     *
     * @throws {@link org.arnolds.agileappproject.agileappmodule.git.GitHubBroker.RepositoryNotSelectedException} If there is not a working repo selected.
     * @throws {@link org.arnolds.agileappproject.agileappmodule.git.GitHubBroker.AlreadyNotConnectedException} If there is not a connected session.
     */
    public void getAllIssues() throws GitHubBroker.RepositoryNotSelectedException,
            GitHubBroker.AlreadyNotConnectedException;
}
