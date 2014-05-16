package org.arnolds.agileappproject.agileappmodule.data;


import org.arnolds.agileappproject.agileappmodule.git.notifications.GitFile;
import org.arnolds.agileappproject.agileappmodule.git.wrappers.GitCommit;
import org.arnolds.agileappproject.agileappmodule.git.wrappers.GitIssue;
import org.kohsuke.github.GHCommit;
import org.kohsuke.github.GHIssue;

import java.beans.PropertyChangeListener;
import java.util.List;

public interface IDataModel {

    /**
     * Stores a commit that has been polled after the initial population.
     * @param commit the commit that should be stored.
     */
    public void addLateCommit(final GitCommit commit);

    /**
     * Stores commits that has been polled after the initial population.
     * @param commits the commits that should be stored.
     */
    public void addLateCommits(final List<GHCommit> commits);

    /**
     * Creates a new timer event after the initial population.
     * @param time the initial countdown.
     */
    public void addLateTimerEvent(String time);

    /**
     * Adds a commit with associated files that conflicts with the selected branch.
     * @param commit - The commit that is associated with the file conflicts.
     * @param affectedFiles - The files that are in conflict.
     */
    public void addFileConflict(final GitCommit commit, final List<GitFile> affectedFiles);

    /**
     * Adds a issue that has been polled after the initial population.
     * @param issue - The issue that should be stored.
     */
    public void addLateIssue(final GitIssue issue);

    /**
     * Removes an event from the stored events.
     * @param event the event that should be removed.
     */
    public void removeEvent(final GitEvent event);

    /**
     * Adds a listener that should be added.
     * Null is ignored and if a listener already exists it will now receive one additional event.
     * @param listener the listener that should be added.
     */
    public void addPropertyChangeListener(final PropertyChangeListener listener);

    /**
     * Removes the specified listener.
     * Null is ignored and if a listener exists multiple times it will now receive one event less.
     * @param listener the lister that should be removed.
     */
    public void removePropertChangeListener(final PropertyChangeListener listener);

    /**
     * Returns the EventList.
     * @return the stored eventList.
     */
    public List<GitEvent> getEventList();

    public void addMonitoredFile(GitFile file);

    public void removeMonitoredFile(GitFile file);

    public List<GitFile> getAllMonitoredFiles();

    public void addMonitoredFileConflict(GitFile commitFile);
}
