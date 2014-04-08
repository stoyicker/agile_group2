package org.arnolds.agileappproject.agileappmodule.git.notifications;
import org.arnolds.agileappproject.agileappmodule.git.GitHubBroker;
import org.arnolds.agileappproject.agileappmodule.git.GitHubBrokerListener;
import org.arnolds.agileappproject.agileappmodule.git.IGitHubBrokerListener;
import org.kohsuke.github.GHRepository;
import org.mockito.Mockito;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Collection;

/**
 * Created by thrawn on 08/04/14.
 */
public class GitHubNotificationService {

    private GHRepository repo;
    private GitHubBroker broker;

    private static GitHubNotificationService instance;
    private PropertyChangeSupport propertyChangeSupport;



    private GitHubNotificationService() {
        propertyChangeSupport = new PropertyChangeSupport(this);


    }

    public static GitHubNotificationService getInstance() {
        if (instance == null) {
            instance = new GitHubNotificationService();
        }
        return instance;
    }



    public void addPropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.addPropertyChangeListener(listener);
    }


    private class myGitHubBrokerListener extends GitHubBrokerListener {

    }



}
