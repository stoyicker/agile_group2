package org.arnolds.agileappproject.agileappmodule.git.notifications;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import org.arnolds.agileappproject.agileappmodule.R;
import org.arnolds.agileappproject.agileappmodule.data.DataModel;
import org.arnolds.agileappproject.agileappmodule.data.IDataModel;
import org.arnolds.agileappproject.agileappmodule.git.GitHubBroker;
import org.arnolds.agileappproject.agileappmodule.git.GitHubBrokerListener;
import org.arnolds.agileappproject.agileappmodule.git.IGitHubBroker;
import org.arnolds.agileappproject.agileappmodule.git.IGitHubBrokerListener;
import org.kohsuke.github.GHBranch;
import org.kohsuke.github.GHCommit;
import org.kohsuke.github.GHRepository;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.List;
import java.util.HashSet;
import java.util.LinkedHashMap;

import java.util.Set;
import java.util.concurrent.TimeUnit;


public class GitHubNotificationService implements IGitHubNotificationService {
    public static final int POLL_TIMEOUT_SECONDS = 5;
    private GHRepository repo;

    private static GitHubNotificationService instance;
    private PropertyChangeSupport commitChangeSupport;
    private LinkedHashMap<String, GHCommit> commits;
    private IGitHubBroker broker;
    private IGitHubBrokerListener brokerListener;
    private Thread commitPollerThread;
    private String repoName = "";
    private String branchName = "";
    private IDataModel dataModel;

    private volatile boolean commitPollerRunning;

    private Context context;

    private GitHubNotificationService() {
        commitChangeSupport = new PropertyChangeSupport(this);

        commits = new LinkedHashMap<String, GHCommit>();
        brokerListener = new MyGitHubBrokerListener();
        broker = GitHubBroker.getInstance();
        dataModel = DataModel.getInstance();

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
                            broker.getAllCommitsOld(brokerListener);
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
        public void onAllCommitsRetrieved(boolean result, final LinkedHashMap<String, GHCommit> remoteCommits) {
            String currentRepo = broker.getSelectedRepoName();
            String currentBranch = broker.getSelectedBranch().getName();
            //If no previous list or repo change.

            if (commits == null || !branchName.equals(currentBranch) || !repoName.equals(currentRepo)) {
                commits = remoteCommits;
                try {   //TODO Why is this necessary?
                    commitChangeSupport
                            .firePropertyChange("New ", null, commits); //TODO: don't send pointer.
                }catch (NullPointerException np){

                }
                repoName = currentRepo;
                branchName = currentBranch;//TODO: don't store repoName locally.
            }
            else if (remoteCommits.size() > commits.size()) {

                //Filter out new commits
                //Removes old commits from remoteCommits
                for (GHCommit commit : commits.values()) {
                    remoteCommits.remove(commit.getSHA1());
                }

                GHBranch selectedBranch = broker.getSelectedBranch();

                for (GHCommit commit : remoteCommits.values()) {
                    Set<GitFile> conflictingFiles = null;
                    if (selectedBranch != null) {
                        conflictingFiles = NotificationUtils.conflictingFiles(selectedBranch, commit, commits);
                    }

                    if (conflictingFiles !=  null && conflictingFiles.size() > 0) {
                        makeToast(context.getString(R.id.file_conflict));
                        dataModel.addFileConflict(commit, new ArrayList<GitFile>(conflictingFiles));
                    } else {
                        makeToast(context.getString(R.id.notification_new_commits) +" " + commit.getCommitShortInfo().getMessage());
                        dataModel.addLateCommit(commit);
                    }
                }

                commits.putAll(remoteCommits);
                commitChangeSupport.firePropertyChange("New ", null, commits); //TODO: don't send pointer.
            }
        }

        @Override
        public void onRepoSelected(boolean result) {
            commits = null;
        }

        private void makeToast(final String toastString) {
            ((Activity) context).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(context,
                            toastString,
                            Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    public boolean isEmpty() {
        return commits.isEmpty();
    }

    public List<GHCommit> getCurrentCommitList() {
        return new ArrayList<GHCommit>(commits.values());
    }
}
