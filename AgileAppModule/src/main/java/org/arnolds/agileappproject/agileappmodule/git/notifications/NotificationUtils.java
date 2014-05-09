package org.arnolds.agileappproject.agileappmodule.git.notifications;


import org.kohsuke.github.GHBranch;
import org.kohsuke.github.GHCommit;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class NotificationUtils {

    public static Set<GitFile> conflictingFiles(final GHBranch branch, final GHCommit newCommit,
                                                final Map<String, GHCommit> oldCommits) {
        Set<GitFile> newFiles = new HashSet<GitFile>();
        Set<GitFile> branchFiles = filesOnBranch(branch, oldCommits);

        for (GHCommit.File file : newCommit.getFiles()) {
            newFiles.add(new GitFile(file.getFileName(), file.getBlobUrl().getPath()));
        }

        branchFiles.retainAll(newFiles);
        return branchFiles;
    }

    private static Set<GitFile> filesOnBranch(final GHBranch branch,
                                              final Map<String, GHCommit> oldCommits) {

        GHCommit commit = oldCommits.get(branch.getSHA1());

        Set<GitFile> files = new HashSet<GitFile>();
        getFiles(commit, files, oldCommits);

        return files;
    }

    private static Set<GitFile> getFiles(GHCommit commit, Set<GitFile> files,
                                         final Map<String, GHCommit> oldCommits) {
        if (commit != null) {
            for (GHCommit.File file : commit.getFiles()) {
                files.add(new GitFile(file.getFileName(), file.getBlobUrl().getPath()));
            }
            for (String commitParentSHA1 : commit.getParentSHA1s()) {
                getFiles(oldCommits.get(commitParentSHA1), files, oldCommits);
            }
        }

        return files;
    }
}
