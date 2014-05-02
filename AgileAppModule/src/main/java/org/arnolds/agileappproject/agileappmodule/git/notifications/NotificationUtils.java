package org.arnolds.agileappproject.agileappmodule.git.notifications;


import org.kohsuke.github.GHBranch;
import org.kohsuke.github.GHCommit;

import java.io.IOException;
import java.net.URL;
import java.util.HashSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;

public class NotificationUtils {

    public static Set<GitFile> conflictingFiles(final GHBranch branch, final GHCommit newCommit, final List<GHCommit> oldCommits) {
        Set<GitFile> newFiles = new HashSet<GitFile>();
        Set<GitFile> branchFiles = filesOnBranch(branch, oldCommits);

        for (GHCommit.File file : newCommit.getFiles()){
            newFiles.add(new GitFile(file.getFileName(), file.getBlobUrl().getPath()));
        }

        branchFiles.retainAll(newFiles);
        return branchFiles;
    }

    private static Set<GitFile> filesOnBranch(final GHBranch branch, final List<GHCommit> oldCommits) {
        String branchHeadId = branch.getSHA1();
        ListIterator<GHCommit> iterator = oldCommits.listIterator();

        GHCommit commit = null;
        while (iterator.hasNext()) {
            commit = iterator.next();
            if (commit.getSHA1().equals(branchHeadId)) {
                break;
            }
        }

        Set<GitFile> files = new HashSet<GitFile>();
        getFiles(commit, files);

        return files;
    }

    private static Set<GitFile> getFiles(GHCommit commit, Set<GitFile> files) {
        for (GHCommit.File file : commit.getFiles()) {
            files.add(new GitFile(file.getFileName(), file.getBlobUrl().getPath()));
        }

        try {
            for (GHCommit commitParent : commit.getParents()) {
                getFiles(commitParent, files);
            }
        } catch (IOException e) {}

        return files;
    }
}
