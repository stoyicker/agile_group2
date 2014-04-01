package org.arnolds.agileappproject.agileappmodule.git;

import android.os.AsyncTask;
import android.util.Log;

import org.kohsuke.github.GHBranch;
import org.kohsuke.github.GHIssue;
import org.kohsuke.github.GHIssueState;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GHUser;
import org.kohsuke.github.GitHub;

import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;

public class GitHubBroker implements IGitHubBroker {

    private static final String LISTENER_NULL_MESSAGE = "listener is null",
            LISTENER_ALREADY_REGISTERED_MESSAGE = "listener is already registered",
            LISTENER_NOT_REGISTERED_MESSAGE = "listener is not registered", IO_EXCEPTION_LOG =
            "io exception", ALREADY_CONNECTED = "there is already a connected session",
            ALREADY_DISCONNECTED = "there is not a connected session", NOT_CONNECTED =
            "not connected", REPO_NULL = "repo is null",
            REPO_NOT_SELECTED = "there is not a working repo", INVALID_CREDENTIALS =
            "incorrect username/password";
    private final Collection<IGitHubBrokerListener> listeners =
            new HashSet<IGitHubBrokerListener>();
    private GitHub session;
    private GHUser user;
    private GHRepository repository;
    private static IGitHubBroker instance;

    private GitHubBroker() {
    }

    public static IGitHubBroker getInstance() {
        if (instance == null) {
            instance = new GitHubBroker();
        }
        return instance;
    }

    @Override
    public boolean isConnected() {
        return session != null;
    }

    @Override
    public void connect(String username, String password) throws IllegalStateException {
        if (isConnected()) {
            throw new IllegalStateException(ALREADY_CONNECTED);
        }
        new AsyncTask<String, Void, Void>() {

            @Override
            protected Void doInBackground(String... params) {
                GitHub tempSession;
                try {
                    if ((tempSession = GitHub.connectUsingPassword(params[0], params[1]))
                            .isCredentialValid()) {
                        session = tempSession;
                        user = session.getMyself();
                    }
                    else {
                        for (IGitHubBrokerListener listener : listeners)
                            listener.onConnectionRefused(INVALID_CREDENTIALS);
                    }
                }
                catch (IOException e) {
                    Log.wtf(IO_EXCEPTION_LOG, e);
                    for (IGitHubBrokerListener listener : listeners)
                        listener.onConnectionRefused(e.getMessage());
                }
                if (isConnected()) {
                    for (IGitHubBrokerListener listener : listeners)
                        listener.onConnected();
                }
                return null;
            }
        }.execute(username, password);
    }

    @Override
    public void disconnect() throws IllegalStateException {
        if (!isConnected()) {
            throw new IllegalStateException(ALREADY_DISCONNECTED);
        }
        session = null;
        user = null;
        repository = null;
        for (IGitHubBrokerListener listener : listeners)
            listener.onDisconnected();
    }

    @Override
    public void addSubscriber(IGitHubBrokerListener listener) throws IllegalArgumentException {
        if (listener == null) {
            throw new IllegalArgumentException(LISTENER_NULL_MESSAGE);
        }
        if (listeners.contains(listener)) {
            throw new IllegalArgumentException(LISTENER_ALREADY_REGISTERED_MESSAGE);
        }
        listeners.add(listener);
    }


    @Override
    public void removeSubscriber(IGitHubBrokerListener listener) throws IllegalArgumentException {
        if (listener == null) {
            throw new IllegalArgumentException(LISTENER_NULL_MESSAGE);
        }
        if (!listeners.contains(listener)) {
            throw new IllegalArgumentException(LISTENER_NOT_REGISTERED_MESSAGE);
        }
        listeners.remove(listener);
    }

    @Override
    public void selectRepo(GHRepository repo)
            throws IllegalArgumentException, IllegalStateException {
        if (!isConnected()) {
            throw new IllegalStateException(NOT_CONNECTED);
        }
        if (repo == null) {
            throw new IllegalArgumentException(REPO_NULL);
        }
        new AsyncTask<GHRepository, Void, Void>() {
            @Override
            protected Void doInBackground(GHRepository... params) {
                try {
                    Map<String, GHRepository> repositories = user.getRepositories();
                    boolean success = repositories.values().contains(params[0]);
                    if (success) {
                        Log.d("debug", "repository set to: " + params[0].toString());
                        repository = params[0];
                    }
                    for (IGitHubBrokerListener listener : listeners)
                        listener.onRepoSelected(success);
                }
                catch (IOException e) {
                    Log.wtf("debug", IO_EXCEPTION_LOG, e);
                }
                return null;
            }
        }.execute(repo);
    }

    @Override
    public void getAllBranches() throws IllegalStateException {
        if (!isConnected()) {
            throw new IllegalStateException(NOT_CONNECTED);
        }
        if (repository == null) {
            throw new IllegalStateException(REPO_NOT_SELECTED);
        }
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                Map<String, GHBranch> branches = null;
                try {
                    branches = repository.getBranches();
                }
                catch (IOException e) {
                    Log.wtf("debug", IO_EXCEPTION_LOG, e);
                }
                boolean success = branches != null;
                for (IGitHubBrokerListener listener : listeners)
                    listener.onAllBranchesRetrieved(success,
                            success ? branches.values() : null);
                return null;
            }
        }.execute();
    }

    @Override
    public void getAllRepos() throws IllegalStateException {
        if (!isConnected()) {
            throw new IllegalStateException(NOT_CONNECTED);
        }
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                Map<String, GHRepository> repos = null;
                try {
                    repos = user.getRepositories();
                }
                catch (IOException e) {
                    Log.wtf("debug", IO_EXCEPTION_LOG, e);
                }
                boolean success = repos != null;
                for (IGitHubBrokerListener listener : listeners)
                    listener.onAllReposRetrieved(success,
                            success ? repos.values() : null);
                return null;
            }
        }.execute();
    }

    @Override
    public void getAllIssues() throws IllegalStateException {
        if (!isConnected()) {
            throw new IllegalStateException(NOT_CONNECTED);
        }
        if (repository == null) {
            throw new IllegalStateException(REPO_NOT_SELECTED);
        }
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                Collection<GHIssue> openIssues = null;
                try {
                    openIssues = repository.getIssues(GHIssueState.OPEN);
                    openIssues.addAll(repository.getIssues(GHIssueState.CLOSED));
                }
                catch (IOException e) {
                    Log.wtf("debug", IO_EXCEPTION_LOG, e);
                }
                boolean success = openIssues != null;
                for (IGitHubBrokerListener listener : listeners)
                    listener.onAllIssuesRetrieved(success,
                            success ? openIssues : null);
                return null;
            }
        }.execute();
    }
}
