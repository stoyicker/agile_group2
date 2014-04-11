package org.arnolds.agileappproject.agileappmodule.git.notifications;

import org.arnolds.agileappproject.agileappmodule.git.GitHubBroker;
import org.arnolds.agileappproject.agileappmodule.git.GitHubBrokerListener;
import org.arnolds.agileappproject.agileappmodule.git.IGitHubBroker;
import org.arnolds.agileappproject.agileappmodule.git.IGitHubBrokerListener;
import org.kohsuke.github.GHCommit;
import org.kohsuke.github.GHRepository;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.TimeUnit;


public class GitHubNotificationService implements IGitHubNotificationService {
    public static final int POLL_TIMEOUT_SECONDS = 10;
    private GHRepository repo;

    private static GitHubNotificationService instance;
    private PropertyChangeSupport commitChangeSupport;
    private List<GHCommit> commitList;
    private IGitHubBroker broker;
    private IGitHubBrokerListener brokerListener;
    private Thread commitPollerThread;

    private volatile boolean commitPollerRunning;

    private GitHubNotificationService() {
        commitChangeSupport = new PropertyChangeSupport(this);

        commitList = new ArrayList<GHCommit>();
        brokerListener = new MyGitHubBrokerListener();
        broker = GitHubBroker.getInstance();

        //Start the poller
        commitPollerThread = new Thread(new CommitPollerThread());
        commitPollerRunning = true;
        commitPollerThread.start();
    }

    public static GitHubNotificationService getInstance() {
        if (instance == null) {
            instance = new GitHubNotificationService();
        }
        return instance;
    }

    private void terminateCommitPoller() {
        commitPollerRunning = false;
    }

    @Override
    public synchronized void addCommitListener(PropertyChangeListener commitListener) {
        //Prepare for running and abort termination if possible
        if(!commitPollerRunning) {
            commitPollerRunning = true;
        }

        //If thread is dead, resurrect it
        if(!commitPollerThread.isAlive()) {
            commitPollerThread.start();
        }

        commitChangeSupport.addPropertyChangeListener(commitListener);
    }

    @Override
    public synchronized void removeCommitListener(PropertyChangeListener commitListener) {
        commitChangeSupport.removePropertyChangeListener(commitListener);

        //If no more listeners, destroy polling thread
        if(commitChangeSupport.getPropertyChangeListeners().length <= 0) {
            terminateCommitPoller();
        }
    }

    private class CommitPollerThread implements Runnable {
        @Override
        public void run() {
            while(commitPollerRunning) {
                try {
                    broker.getAllRepos(brokerListener);
                }
                catch (GitHubBroker.AlreadyNotConnectedException e) {
                    e.printStackTrace();
                }

                try {
                    TimeUnit.SECONDS.sleep(POLL_TIMEOUT_SECONDS);
                }
                catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private class MyGitHubBrokerListener extends GitHubBrokerListener {
        @Override
        public void onAllReposRetrieved(boolean success, Collection<GHRepository> repos) {
            Object[] ghRepos = repos.toArray();
            repo = (GHRepository) ghRepos[0];
            List<GHCommit> remoteCommitList = repo.listCommits().asList();

            //If change
            if (remoteCommitList.size() != commitList.size()) {
                commitList = remoteCommitList;
                commitChangeSupport
                        .firePropertyChange("New ", null, commitList); //TODO: don't send pointer.
            }
        }
    }

    public List<GHCommit> getCurrentCommitList() {
        return commitList;
    }
}
