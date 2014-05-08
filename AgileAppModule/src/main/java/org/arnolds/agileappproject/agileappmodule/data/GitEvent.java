package org.arnolds.agileappproject.agileappmodule.data;


import org.arnolds.agileappproject.agileappmodule.git.notifications.GitFile;
import org.kohsuke.github.GHCommit;
import org.kohsuke.github.GHIssue;

import java.util.List;

public class GitEvent {
    public enum EventType {
        COMMIT,
        ISSUE,
        FILE_CONFLICT
    }

    private EventType type;
    private GHCommit commit;
    private GHIssue issue;
    private List<GitFile> fileConflicts;

    public GitEvent(GHCommit commit) {
        this.type = EventType.COMMIT;
        this.commit = commit;
    }

    public GitEvent(GHIssue issue) {
        this.type = EventType.ISSUE;
        this.issue = issue;
    }

    public GitEvent(GHCommit commit, List<GitFile> fileConflicts) {
        this.type = EventType.FILE_CONFLICT;
        this.commit = commit;
        this.fileConflicts = fileConflicts;
    }

    public String getEventText() {
        String eventText = null;
        switch (type) {
            case COMMIT:
                eventText = commit.getCommitShortInfo().getMessage();
                break;
            case ISSUE:
                eventText = issue.getTitle();
                break;
            case FILE_CONFLICT:
                eventText = commit.getCommitShortInfo().getMessage();
                break;
        }

        return eventText;
    }

    public EventType getType() {
        return type;
    }
}
