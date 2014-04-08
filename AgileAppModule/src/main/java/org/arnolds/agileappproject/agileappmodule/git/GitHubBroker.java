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
import java.util.Objects;

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
    private GitHub session;
    private GHUser user;
    private GHRepository repository;
    private static IGitHubBroker instance;

    private final Object asyncLock = new Object();

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
    public void connect(String username, String password, IGitHubBrokerListener callback)
            throws AlreadyConnectedException {
        if (isConnected()) {
            throw new AlreadyConnectedException();
        }
        new AsyncTask<Object, Void, Void>() {
            @Override
            protected Void doInBackground(Object... params) {
                IGitHubBrokerListener callback = ((IGitHubBrokerListener)params[3]);
                GitHub tempSession;

                try {
                    tempSession = GitHub.connectUsingPassword(params[0].toString(), params[1].toString());
                    if (tempSession.isCredentialValid()) {
                        session = tempSession;
                        user = session.getMyself();
                    }
                    else if (callback != null) {
                        callback.onConnectionRefused(INVALID_CREDENTIALS);
                    }
                }
                catch (IOException e) {
                    Log.wtf("debug", IO_EXCEPTION_LOG, e);
                    if (callback != null) {
                        callback.onConnectionRefused(e.getMessage());
                    }
                }
                synchronized (asyncLock) {
                    if (isConnected()) {
                        if (callback != null) {
                            callback.onConnected();
                        }
                    }
                }
                return null;
            }
        }.execute(username, password, callback);
    }

    @Override
    public void disconnect() throws AlreadyNotConnectedException {
        if (!isConnected()) {
            throw new AlreadyNotConnectedException();
        }
        session = null;
        user = null;
        repository = null;
    }

    @Override
    public void selectRepo(GHRepository repo, IGitHubBrokerListener callback)
            throws NullArgumentException, AlreadyNotConnectedException {
        if (!isConnected()) {
            throw new AlreadyNotConnectedException();
        }
        if (repo == null) {
            throw new NullArgumentException();
        }
        new AsyncTask<Object, Void, Void>() {
            @Override
            protected Void doInBackground(Object... params) {
                GHRepository repo = (GHRepository)params[0];
                IGitHubBrokerListener callback = (IGitHubBrokerListener)params[1];
                try {
                    Map<String, GHRepository> repositories = user.getRepositories();
                    boolean success = repositories.values().contains(repo);
                    if (success) {
                        Log.d("debug", "repository set to: " + repo.toString());
                        repository = repo;
                    }
                    synchronized (asyncLock) {
                        if(callback != null) {
                            callback.onRepoSelected(success);
                        }
                    }
                }
                catch (IOException e) {
                    Log.wtf("debug", IO_EXCEPTION_LOG, e);
                }
                return null;
            }
        }.execute(repo, callback);
    }

    @Override
    public void getAllBranches(IGitHubBrokerListener callback) throws RepositoryNotSelectedException,
            AlreadyNotConnectedException {
        if (!isConnected()) {
            throw new AlreadyNotConnectedException();
        }
        if (repository == null) {
            throw new RepositoryNotSelectedException();
        }
        new AsyncTask<IGitHubBrokerListener, Void, Void>() {
            @Override
            protected Void doInBackground(IGitHubBrokerListener... params) {
                Map<String, GHBranch> branches = null;
                try {
                    branches = repository.getBranches();
                }
                catch (IOException e) {
                    Log.wtf("debug", IO_EXCEPTION_LOG, e);
                }
                boolean success = branches != null;
                synchronized (asyncLock) {
                    if(params[0] != null) {
                        params[0].onAllBranchesRetrieved(success, success ? branches.values() : null);
                    }
                }
                return null;
            }
        }.execute(callback);
    }

    @Override
    public void getAllRepos(IGitHubBrokerListener callback) throws AlreadyNotConnectedException {
        if (!isConnected()) {
            throw new AlreadyNotConnectedException();
        }
        new AsyncTask<IGitHubBrokerListener, Void, Void>() {
            @Override
            protected Void doInBackground(IGitHubBrokerListener... params) {
                Map<String, GHRepository> repos = null;
                try {
                    repos = user.getRepositories();
                }
                catch (IOException e) {
                    Log.wtf("debug", IO_EXCEPTION_LOG, e);
                }
                boolean success = repos != null;
                synchronized (asyncLock) {
                    if(params[0] != null){
                        params[0].onAllReposRetrieved(success, success ? repos.values() : null);
                    }
                    return null;
                }
            }
        }.execute(callback);
    }

    @Override
    public void getAllIssues(IGitHubBrokerListener callback) throws RepositoryNotSelectedException, AlreadyNotConnectedException {
        if (!isConnected()) {
            throw new AlreadyNotConnectedException();
        }
        if (repository == null) {
            throw new RepositoryNotSelectedException();
        }
        new AsyncTask<IGitHubBrokerListener, Void, Void>() {
            @Override
            protected Void doInBackground(IGitHubBrokerListener... params) {
                Collection<GHIssue> issues = null;
                boolean success = true;
                try {
                    issues = repository.getIssues(GHIssueState.OPEN);
                    issues.addAll(repository.getIssues(GHIssueState.CLOSED));
                }
                catch (IOException e) {
                    success = false;
                    Log.wtf("debug", IO_EXCEPTION_LOG, e);
                }

                synchronized (asyncLock) {
                    if(params[0] != null){
                        params[0].onAllIssuesRetrieved(success, success ? issues : null);
                    }
                    return null;
                }
            }
        }.execute(callback);
    }
}
