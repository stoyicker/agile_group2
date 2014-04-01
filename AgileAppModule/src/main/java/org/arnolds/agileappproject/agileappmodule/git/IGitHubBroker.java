package org.arnolds.agileappproject.agileappmodule.git;

import org.kohsuke.github.GHBranch;
import org.kohsuke.github.GHIssue;
import org.kohsuke.github.GHRepository;

import java.util.Collection;

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
     * @param listener {@link org.arnolds.agileappproject.agileappmodule.git.IGitHubBrokerListener} The object that wants to subscribe.
     * @throws IllegalArgumentException If listener is null or is already susbscribed.
     */
    public void subscribe(IGitHubBrokerListener listener) throws IllegalArgumentException;

    /**
     * Removes the suscription to the broker.
     *
     * @param listener {@link org.arnolds.agileappproject.agileappmodule.git.IGitHubBrokerListener} The object that wants to unsubscribe.
     * @throws IllegalArgumentException If listener is null or is not subscribed.
     */
    public void unsubscribe(IGitHubBrokerListener listener) throws IllegalArgumentException;


    /**
     * Asynchronously selects a repo to work with.
     *
     * @param repo {@link org.kohsuke.github.GHRepository} The repository to select.
     * @throws IllegalArgumentException If the repo is not found or is null.
     * @throws IllegalStateException    If there is not a connected session.
     */
    public void selectRepo(GHRepository repo)
            throws IllegalArgumentException, IllegalStateException;

    /**
     * Asynchronously return all branches of the working repository.
     *
     * @return {@link java.util.Collection-{@link org.kohsuke.github.GHBranch}-} All branches in the working repository.
     * @throws IllegalStateException If there is not a connected session or if there is not a working repo selected.
     */
    public Collection<GHBranch> getAllBranches() throws IllegalStateException;

    /**
     * Asynchronously return all repos of the currently logged in user.
     *
     * @return {@link java.util.Collection-{@link org.kohsuke.github.GHRepository}-} All repositories of the logged in user.
     * @throws IllegalStateException If there is not a connected session.
     */
    public Collection<GHRepository> getAllRepos() throws IllegalStateException;

    /**
     * Asynchronously return all issues of the working repository.
     *
     * @return {@link java.util.Collection-{@link org.kohsuke.github.GHIssue}-} All issues in the working repository.
     * @throws IllegalStateException If there is not a connected session or if there is not a working repo selected.
     */
    public Collection<GHIssue> getAllIssues() throws IllegalStateException;

}
