package org.arnolds.agileappproject.agileappmodule.data;


import org.apache.commons.lang.NotImplementedException;
import org.arnolds.agileappproject.agileappmodule.git.notifications.GitFile;
import org.kohsuke.github.GHCommit;
import org.kohsuke.github.GHIssue;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.List;

public class DataModel implements IDataModel {
    private PropertyChangeSupport pcs;
    private List<GitEvent> eventList;
    private static IDataModel model = null;

    private DataModel() {
        pcs = new PropertyChangeSupport(this);
        eventList = new ArrayList<GitEvent>();
    }

    public synchronized static IDataModel getInstance() {
        if (model == null) {
            model = new DataModel();
        }

        return model;
    }

    @Override
    public void addLateCommit(GHCommit commit) {
        eventList.add(new GitEvent(commit));
        firePropertyEvent();
    }

    @Override
    public void addLateCommits(List<GHCommit> commits) {
        throw new NotImplementedException();
    }

    @Override
    public void addLateIssue(GHIssue issue) {
        eventList.add(new GitEvent(issue));
        firePropertyEvent();
    }

    @Override
    public void addFileConflict(GHCommit commit, List<GitFile> affectedFiles) {
        eventList.add(new GitEvent(commit, affectedFiles));
        firePropertyEvent();
    }

    @Override
    public void removeEvent(GitEvent event) {
        eventList.remove(event);
        firePropertyEvent();
    }

    @Override
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        pcs.addPropertyChangeListener(listener);
    }

    @Override
    public void removePropertChangeListener(PropertyChangeListener listener) {
        pcs.removePropertyChangeListener(listener);
    }

    @Override
    public List<GitEvent> getEventList() {
        return eventList;
    }

    private void firePropertyEvent() {
        pcs.firePropertyChange("New Event List", null, eventList);
    }
}
