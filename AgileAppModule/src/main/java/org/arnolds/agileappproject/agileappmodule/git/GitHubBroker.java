package org.arnolds.agileappproject.agileappmodule.git;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.kohsuke.github.GHBranch;
import org.kohsuke.github.GHIssue;
import org.kohsuke.github.GHIssueState;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GHUser;
import org.kohsuke.github.GitHub;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;

public class GitHubBroker implements IGitHubBroker {

    private abstract class GitHubBrokerException extends Exception {
        protected String reason;

        public GitHubBrokerException(String _reason) {
            reason = _reason;
        }

        public final String getReason() {
            StringWriter stringWriter = new StringWriter();
            printStackTrace(new PrintWriter(stringWriter));
            return reason + "\n" + stringWriter.toString();
        }
    }

    public class ListenerAlreadyRegisteredException extends GitHubBrokerException {
        public ListenerAlreadyRegisteredException() {
            super("Listener already registered.");
        }
    }

    public class ListenerNotRegisteredException extends GitHubBrokerException {
        public ListenerNotRegisteredException() {
            super("Listener is not registered.");
        }
    }

    public class AlreadyConnectedException extends GitHubBrokerException {
        public AlreadyConnectedException() {
            super("There is already a connected session.");
        }
    }

    public class AlreadyNotConnectedException extends GitHubBrokerException {
        public AlreadyNotConnectedException() {
            super("There is not a connected session.");
        }
    }

    public class RepositoryNotSelectedException extends GitHubBrokerException {
        public RepositoryNotSelectedException() {
            super("There is not a repository selected to work with.");
        }
    }

    public class NullArgumentException extends GitHubBrokerException {
        public NullArgumentException() {
            super("The argument is null.");
        }
    }

    private final static String IO_EXCEPTION_LOG = "IOException.", INVALID_CREDENTIALS =
            "Wrong username or password.";
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
    public void connect(String username, String password, final Context context)
            throws AlreadyConnectedException {
        if (isConnected()) {
            throw new AlreadyConnectedException();
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
                    Log.wtf("debug", IO_EXCEPTION_LOG, e);
                    for (IGitHubBrokerListener listener : listeners)
                        listener.onConnectionRefused(e.getMessage());
                }
                synchronized (GitHubBroker.this) {
                    if (isConnected()) {
                        for (IGitHubBrokerListener listener : listeners)
                            listener.onConnected();
                    }
                }
                return null;
            }
        }.execute(username, password);
    }

    @Override
    public void disconnect() throws AlreadyNotConnectedException {
        if (!isConnected()) {
            throw new AlreadyNotConnectedException();
        }
        session = null;
        user = null;
        repository = null;
        synchronized (GitHubBroker.this) {
            for (IGitHubBrokerListener listener : listeners)
                listener.onDisconnected();
        }
    }

    @Override
    public void addSubscriber(IGitHubBrokerListener listener)
            throws NullArgumentException, ListenerAlreadyRegisteredException {
        if (listener == null) {
            throw new NullArgumentException();
        }
        synchronized (GitHubBroker.this) {
            if (listeners.contains(listener)) {
                throw new ListenerAlreadyRegisteredException();
            }
            listeners.add(listener);
        }
    }


    @Override
    public void removeSubscriber(IGitHubBrokerListener listener)
            throws ListenerNotRegisteredException, NullArgumentException {
        if (listener == null) {
            throw new NullArgumentException();
        }
        synchronized (GitHubBroker.this) {
            if (!listeners.contains(listener)) {
                throw new ListenerNotRegisteredException();
            }
            listeners.remove(listener);
        }
    }

    @Override
    public void selectRepo(GHRepository repo)
            throws NullArgumentException, AlreadyNotConnectedException {
        if (!isConnected()) {
            throw new AlreadyNotConnectedException();
        }
        if (repo == null) {
            throw new NullArgumentException();
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
                    synchronized (GitHubBroker.this) {
                        for (IGitHubBrokerListener listener : listeners)
                            listener.onRepoSelected(success);
                    }
                }
                catch (IOException e) {
                    Log.wtf("debug", IO_EXCEPTION_LOG, e);
                }
                return null;
            }
        }.execute(repo);
    }

    @Override
    public void getAllBranches() throws RepositoryNotSelectedException,
            AlreadyNotConnectedException {
        if (!isConnected()) {
            throw new AlreadyNotConnectedException();
        }
        if (repository == null) {
            throw new RepositoryNotSelectedException();
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
                synchronized (GitHubBroker.this) {
                    for (IGitHubBrokerListener listener : listeners)
                        listener.onAllBranchesRetrieved(success,
                                success ? branches.values() : null);
                }
                return null;
            }
        }.execute();
    }

    @Override
    public void getAllRepos() throws AlreadyNotConnectedException {
        if (!isConnected()) {
            throw new AlreadyNotConnectedException();
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
                synchronized (GitHubBroker.this) {
                    for (IGitHubBrokerListener listener : listeners)
                        listener.onAllReposRetrieved(success,
                                success ? repos.values() : null);
                    return null;
                }
            }
        }.execute();
    }

    @Override
    public void getAllIssues() throws RepositoryNotSelectedException, AlreadyNotConnectedException {
        if (!isConnected()) {
            throw new AlreadyNotConnectedException();
        }
        if (repository == null) {
            throw new RepositoryNotSelectedException();
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
                synchronized (GitHubBroker.this) {
                    for (IGitHubBrokerListener listener : listeners)
                        listener.onAllIssuesRetrieved(success,
                                success ? openIssues : null);
                    return null;
                }
            }
        }.execute();
    }
}
