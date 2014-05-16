package org.arnolds.agileappproject.agileappmodule.data;


import android.content.res.Resources;

import org.arnolds.agileappproject.agileappmodule.R;
import org.arnolds.agileappproject.agileappmodule.git.notifications.GitFile;
import org.arnolds.agileappproject.agileappmodule.git.notifications.GitFileTree;
import org.arnolds.agileappproject.agileappmodule.git.wrappers.GitCommit;
import org.arnolds.agileappproject.agileappmodule.git.wrappers.GitIssue;
import org.kohsuke.github.GHCommit;
import org.kohsuke.github.GHIssue;

import java.util.List;

public class GitEvent {

    public enum EventType {
        COMMIT,
        ISSUE,
        FILE_CONFLICT,
        TIMER_EVENT,
        MONITORED_FILE_CONFLICT
    }

    private EventType type;
    private GitCommit commit;
    private GitIssue issue;
    private List<GitFile> fileConflicts;
    private String time;
    private GitFile monitoredFile;

    public GitEvent(GitFile commitFile) {
        monitoredFile=commitFile;
        this.type = EventType.MONITORED_FILE_CONFLICT;
    }

    public GitEvent(GitCommit commit) {
        this.type = EventType.COMMIT;
        this.commit = commit;
    }

    public GitEvent(GitIssue issue) {
        this.type = EventType.ISSUE;
        this.issue = issue;
    }

    public GitEvent(String time) {
        this.type = EventType.TIMER_EVENT;
        this.time = time;
    }

    public GitEvent(GitCommit commit, List<GitFile> fileConflicts) {
        this.type = EventType.FILE_CONFLICT;
        this.commit = commit;
        this.fileConflicts = fileConflicts;
    }

    public String getEventText() {
        String eventText = null;
        switch (type) {
            case COMMIT:
                eventText = commit.getMessage();
                break;
            case ISSUE:
                eventText = issue.getTitle();
                break;
            case FILE_CONFLICT:
                eventText = commit.getMessage();
                break;
            case TIMER_EVENT:
                //TODO Fix hard coded string
                eventText = "Timer \""+time+"\" has finished!";
                break;
            case MONITORED_FILE_CONFLICT:
                eventText = "File conflict on: "+monitoredFile.getFileName();
                break;
        }

        return eventText;
    }

    public EventType getType() {
        return type;
    }
}
