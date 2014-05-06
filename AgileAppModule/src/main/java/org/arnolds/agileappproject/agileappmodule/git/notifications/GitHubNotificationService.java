package org.arnolds.agileappproject.agileappmodule.git.notifications;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import org.arnolds.agileappproject.agileappmodule.R;
import org.arnolds.agileappproject.agileappmodule.git.GitHubBroker;
import org.arnolds.agileappproject.agileappmodule.git.GitHubBrokerListener;
import org.arnolds.agileappproject.agileappmodule.git.IGitHubBroker;
import org.arnolds.agileappproject.agileappmodule.git.IGitHubBrokerListener;
import org.kohsuke.github.GHCommit;
import org.kohsuke.github.GHRepository;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;


public class GitHubNotificationService implements IGitHubNotificationService {
    public static final int POLL_TIMEOUT_SECONDS = 5;
    private GHRepository repo;

    private static GitHubNotificationService instance;
    private PropertyChangeSupport commitChangeSupport;
    private List<GHCommit> commitList;
    private IGitHubBroker broker;
    private IGitHubBrokerListener brokerListener;
    private Thread commitPollerThread;
    private String repoName = "";

    private volatile boolean commitPollerRunning;

    private Context context;

    private GitHubNotificationService() {
        commitChangeSupport = new PropertyChangeSupport(this);

        commitList = new ArrayList<GHCommit>();
        brokerListener = new MyGitHubBrokerListener();
        broker = GitHubBroker.getInstance();

        commitPollerThread = new Thread(new CommitPollerThread());

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

    public void setContext(Context context) {
        this.context = context;
    }

    @Override
    public synchronized void addCommitListener(PropertyChangeListener commitListener) {

        if (commitListener == null) {
            return;
        }

        //Prepare for running and abort termination if possible
        if (!commitPollerRunning && !commitPollerThread.isAlive()) {
            //Start the poller
            commitPollerRunning = true;
            try {
                commitPollerThread.start();
                if (!commitPollerRunning) {
                    commitPollerRunning = true;
                }

                //If thread is dead, resurrect it
                if (!commitPollerThread.isAlive()) {
                    commitPollerThread.start();
                }

                commitChangeSupport.addPropertyChangeListener(commitListener);
            }
            catch (IllegalThreadStateException ex) {
            }
        }
    }

    @Override
    public synchronized void removeCommitListener(PropertyChangeListener commitListener) {
        commitChangeSupport.removePropertyChangeListener(commitListener);

        //If no more listeners, destroy polling thread
        if (commitChangeSupport.getPropertyChangeListeners().length <= 0) {
            terminateCommitPoller();
        }
    }

    private class CommitPollerThread implements Runnable {
        @Override
        public void run() {
            while (commitPollerRunning) {
                try {
                    try {
                        if (!TextUtils.isEmpty(GitHubBroker.getInstance().getSelectedRepoName())) {
                            broker.getAllCommits(brokerListener);
                        }
                    }
                    catch (GitHubBroker.RepositoryNotSelectedException e) {
                        Log.wtf("debug", e.getClass().getName(), e);
                    }
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
        public void onAllCommitsRetrieved(boolean result, final List<GHCommit> remoteCommitList) {
            String currentRepo = broker.getSelectedRepoName();
            List<GHCommit> oldValues = commitList;
            //If no previous list or repo change.
            if ((!repoName.equals(currentRepo)) ||
                    commitList == null) {
                commitList = remoteCommitList;
                try {
                    commitChangeSupport
                            .firePropertyChange("New ", oldValues,
                                    commitList); //TODO: don't send pointer.
                    repoName = currentRepo;
                }
                catch (NullPointerException ex) {

                }
            }
            else if (remoteCommitList.size() != commitList.size()) {
                Log.wtf("GH NOTIF", "new Commits.");

                if (!commitList.isEmpty()) {
                    ((Activity) context).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(context,
                                    context.getString(R.id.notification_new_commits),
                                    Toast.LENGTH_LONG).show();
                        }
                    });
                }
                commitList = remoteCommitList;
                commitChangeSupport
                        .firePropertyChange("New ", oldValues,
                                commitList); //TODO: don't send pointer.
            }
        }

        @Override
        public void onRepoSelected(boolean result) {
            commitList = null;
        }
    }

    public boolean isEmpty() {
        return commitList.isEmpty();
    }

    public List<GHCommit> getCurrentCommitList() {
        return commitList;
    }
}
