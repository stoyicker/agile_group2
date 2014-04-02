package org.arnolds.agileappproject.agileappmodule.git;

import org.kohsuke.github.GHRepository;

import java.io.IOException;

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
     * @throws IllegalStateException If already connected.
     */
    public void connect(String username, String password) throws IllegalStateException;

    /**
     * Disconnects the current session.
     *
     * @throws IllegalStateException If there is not a connected session.
     */
    public void disconnect() throws IllegalStateException;

    /**
     * Subscribes to the broker.
     *
     * @param listener {@link org.arnolds.agileappproject.agileappmodule.git.IGitHubBrokerListener} The object that wants to addSubscriber.
     * @throws IllegalArgumentException If listener is null or is already susbscribed.
     */
    public void addSubscriber(IGitHubBrokerListener listener) throws IllegalArgumentException;

    /**
     * Removes the suscription to the broker.
     *
     * @param listener {@link org.arnolds.agileappproject.agileappmodule.git.IGitHubBrokerListener} The object that wants to removeSubscriber.
     * @throws IllegalArgumentException If listener is null or is not subscribed.
     */
    public void removeSubscriber(IGitHubBrokerListener listener) throws IllegalArgumentException;


    /**
     * Asynchronously selects a repo to work with.
     *
     * @param repo {@link org.kohsuke.github.GHRepository} The repository to select.
     * @throws IllegalArgumentException If the repo is null.
     * @throws IllegalStateException    If there is not a connected session.
     */
    public void selectRepo(GHRepository repo)
            throws IllegalArgumentException, IllegalStateException;

    /**
     * Asynchronously return all branches of the working repository.
     *
     * @throws IllegalStateException If there is not a connected session or if there is not a working repo selected.
     */
    public void getAllBranches() throws IllegalStateException;

    /**
     * Asynchronously return all repos of the currently logged in user.
     *
     * @throws IllegalStateException If there is not a connected session.
     */
    public void getAllRepos() throws IllegalStateException;

    /**
     * Asynchronously return all issues of the working repository.
     *
     * @throws IllegalStateException If there is not a connected session or if there is not a working repo selected.
     */
    public void getAllIssues() throws IllegalStateException;


    public void createToken();


}
