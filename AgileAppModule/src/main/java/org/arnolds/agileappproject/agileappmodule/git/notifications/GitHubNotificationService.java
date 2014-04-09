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

/**
 * Created by thrawn on 08/04/14.
 */
public class GitHubNotificationService implements IGitHubNotificationService {
    public static final int POLL_TIMEOUT_SECONDS = 10;
    private GHRepository repo;

    private static GitHubNotificationService instance;
    private PropertyChangeSupport commitChangeSupport;
    private List<GHCommit> commitList;
    private IGitHubBroker broker;
    private IGitHubBrokerListener brokerListener;

    private GitHubNotificationService() {
        commitChangeSupport = new PropertyChangeSupport(this);

        commitList = new ArrayList<GHCommit>();
        brokerListener = new MyGitHubBrokerListener();
        broker = GitHubBroker.getInstance();
        try {
            broker.addSubscriber(brokerListener);
        } catch (GitHubBroker.NullArgumentException e) {
            e.printStackTrace();
        } catch (GitHubBroker.ListenerAlreadyRegisteredException e) {
            e.printStackTrace();
        }

        //Start the poller
        Thread thread = new Thread(new PollerThread());
        thread.start();

    }

    public static GitHubNotificationService getInstance() {
        if (instance == null) {
            instance = new GitHubNotificationService();
        }
        return instance;
    }


    @Override
    public void addCommitListener(PropertyChangeListener commitListener) {
        commitChangeSupport.addPropertyChangeListener(commitListener);
    }

    @Override
    public void removeCommitListener(PropertyChangeListener commitListener) {
        commitChangeSupport.removePropertyChangeListener(commitListener);
    }

    private class PollerThread implements Runnable {
        @Override
        public void run() {
            while(true) {
                try {
                    broker.getAllRepos();
                } catch (GitHubBroker.AlreadyNotConnectedException e) {
                    e.printStackTrace();
                }

                try {
                    TimeUnit.SECONDS.sleep(POLL_TIMEOUT_SECONDS);
                } catch (InterruptedException e) {
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
            if(remoteCommitList.size() != commitList.size()) {
                commitList = remoteCommitList;
                commitChangeSupport.firePropertyChange("New ", null, commitList); //TODO: don't send pointer.
            }
        }
    }
}
