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
import org.arnolds.agileappproject.agileappmodule.git.wrappers.GitBranch;
import org.arnolds.agileappproject.agileappmodule.git.wrappers.GitCommit;
import org.arnolds.agileappproject.agileappmodule.git.wrappers.GitIssue;
import org.kohsuke.github.GHCommit;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;


public class GitHubNotificationService implements IGitHubNotificationService {
    public static final int POLL_TIMEOUT_SECONDS = 5;

    private static GitHubNotificationService instance;
    private PropertyChangeSupport commitChangeSupport;
    private PropertyChangeSupport issueChangeSupport;
    private LinkedHashMap<String, GHCommit> commits;
    private IGitHubBroker broker;
    private IGitHubBrokerListener brokerListener;
    private Thread commitPollerThread;
    private IDataModel dataModel;

    private volatile boolean commitPollerRunning;

    private Context context;
    private boolean firstRecieve = true;
    private boolean firstRecieveIssue = true;

    private GitHubNotificationService() {
        commitChangeSupport = new PropertyChangeSupport(this);
        issueChangeSupport = new PropertyChangeSupport(this);

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
    public synchronized void addIssueListener(PropertyChangeListener issueListener) {

        if (issueListener == null) {
            return;
        }
        issueChangeSupport.addPropertyChangeListener(issueListener);
    }

    @Override
    public synchronized void removeIssueListener(PropertyChangeListener issueListener) {
        issueChangeSupport.removePropertyChangeListener(issueListener);

        //If no more listeners, destroy polling thread
        if (issueChangeSupport.getPropertyChangeListeners().length <= 0) {
            terminateCommitPoller();
        }
    }

    @Override
    public void killService() {
        terminateCommitPoller();
        instance = null;
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
                            broker.fetchNewCommits(brokerListener);
                            broker.fetchNewIssues(brokerListener);
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
        public void onNewCommitsReceived(boolean result, Map<String, GitCommit> newCommits,
                                         Map<String, GitCommit> commits) {

            if (!firstRecieve && !newCommits.isEmpty()) {

                GitBranch selectedBranch = broker.getSelectedBranch();

                for (GitCommit commit : newCommits.values()) {
                    List<GitFile> monitoredFiles = DataModel.getInstance().getAllMonitoredFiles();
                    if (monitoredFiles.size() > 0){
                        for (GitFile file : monitoredFiles){
                            for (GitFile commitFile : commit.getFiles())
                            if (file.equals(commitFile)){
                                dataModel.addMonitoredFileConflict(commitFile);
                                makeToast(context.getString(R.string.file_conflict_on_monitored)+" "+commitFile.getName());
                            }
                        }
                    }
                    else {
                        Set<GitFile> conflictingFiles = null;
                        if (selectedBranch != null) {
                            conflictingFiles =
                                    NotificationUtils.conflictingFiles(selectedBranch, commit, commits);
                        }

                        if (conflictingFiles != null && conflictingFiles.size() > 0) {
                            makeToast(context.getString(R.string.file_conflict));
                            dataModel.addFileConflict(commit, new ArrayList<GitFile>(conflictingFiles));
                        } else {
                            makeToast(context.getString(R.string.notification_new_commits) + " " +
                                    commit.getMessage());
                            dataModel.addLateCommit(commit);
                        }
                    }
                }
                commitChangeSupport.firePropertyChange("new commits", null, newCommits);
            }
            firstRecieve = false;
        }

        @Override
        public void onNewIssuesReceived(boolean b, List<GitIssue> oldIssues,
                                        List<GitIssue> issues) {
            if (!firstRecieveIssue && oldIssues.size() < issues.size()) { //If there are new issues
                for (int i = 0; i < issues.size() - oldIssues.size(); i++) {
                    dataModel.addLateIssue(issues.get(i));
                }
                issueChangeSupport.firePropertyChange("new issues", null, issues);
            }
            firstRecieveIssue = false;
        }

        @Override
        public void onRepoSelected(boolean result) {


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

}
