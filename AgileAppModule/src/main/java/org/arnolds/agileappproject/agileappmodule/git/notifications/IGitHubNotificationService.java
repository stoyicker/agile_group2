package org.arnolds.agileappproject.agileappmodule.git.notifications;

import java.beans.PropertyChangeListener;

/**
 * Created by andreas on 2014-04-09.
 */
public interface IGitHubNotificationService {
    /**
     *
     * @param commitListener The PropertyChangeListener that will receive the commit updates.
     */
    public void addCommitListener(PropertyChangeListener commitListener);

    /**
     *
     * @param commitListener
     */
    public void removeCommitListener(PropertyChangeListener commitListener);

    /**
     *
     * @param issueListener The PropertyChangeListener that will receive the issue updates.
     */
    public void addIssueListener(PropertyChangeListener issueListener);

    /**
     *
     * @param issueListener
     */
    public void removeIssueListener(PropertyChangeListener issueListener);


}
