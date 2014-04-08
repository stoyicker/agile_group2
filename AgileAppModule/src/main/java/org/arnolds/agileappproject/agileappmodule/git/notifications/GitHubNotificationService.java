package org.arnolds.agileappproject.agileappmodule.git.notifications;


import org.arnolds.agileappproject.agileappmodule.git.GitHubBrokerListener;
import org.arnolds.agileappproject.agileappmodule.git.IGitHubBroker;
import org.kohsuke.github.GHRepository;

import java.util.Collection;

/**
 * Created by thrawn on 08/04/14.
 */
class GitHubNotificationService extends GitHubBrokerListener {

    private static GitHubNotificationService instance;

    private GHRepository repo;


    private GitHubNotificationService() {


    }

    public static GitHubNotificationService getInstance() {
        if (instance == null) {
            instance = new GitHubNotificationService();
        }
        return instance;
    }


    @Override
    public void onAllReposRetrieved(boolean success, Collection<GHRepository> repos) {
        repo = repos.
    }
}
