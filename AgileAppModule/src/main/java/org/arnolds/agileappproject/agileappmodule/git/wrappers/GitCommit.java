package org.arnolds.agileappproject.agileappmodule.git.wrappers;

import org.arnolds.agileappproject.agileappmodule.git.notifications.GitFile;
import org.kohsuke.github.GHCommit;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Value object for Git Commits.
 * This object is mutable through the GHCommit reference that is passed to the constructor.
 */
public class GitCommit {
    private GHCommit apiCommit;
    private GitUser author;
    private GitUser committer;

    public GitCommit() {}

    public GitCommit(GHCommit apiCommit) {
        if (apiCommit == null) {
            throw new IllegalArgumentException("apiCommit can not be null");
        }

        try {
            this.author = new GitUser(apiCommit.getAuthor());
            this.committer = new GitUser(apiCommit.getCommitter());
        } catch (IOException e) {
            e.printStackTrace();
        }

        this.apiCommit = apiCommit;
    }

    @Override
    public boolean equals(Object o) {
        if(o != null && o instanceof GitCommit) {
            GitCommit otherCommit = (GitCommit) o;
            return (apiCommit.getSHA1().equals(otherCommit.getSHA1()));
        }

        return false;
    }

    @Override
    public int hashCode() {
        return getSHA1().hashCode();
    }

    @Override
    public String toString() {
        return "Commit: "+getSHA1();
    }

    public String getSHA1() {
        return apiCommit.getSHA1();
    }

    public GitUser getAuthor() {
        return author;
    }

    public GitUser getCommitter() {
        return committer;
    }

    public String getMessage() {
        return apiCommit.getCommitShortInfo().getMessage();
    }

    public List<GitFile> getFiles() {
        List<GHCommit.File> apiFiles = apiCommit.getFiles();
        List<GitFile> files = new ArrayList<GitFile>();

        for (GHCommit.File apiFile : apiFiles) {
            files.add(new GitFile(apiFile.getFileName(), apiFile.getBlobUrl().getPath()));
        }

        return files;
    }

    public List<String> getParentsSHA1() {
        return apiCommit.getParentSHA1s();
    }
}
