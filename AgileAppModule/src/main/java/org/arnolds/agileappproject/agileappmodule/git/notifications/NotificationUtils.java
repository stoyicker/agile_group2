package org.arnolds.agileappproject.agileappmodule.git.notifications;

import org.arnolds.agileappproject.agileappmodule.git.wrappers.GitBranch;
import org.arnolds.agileappproject.agileappmodule.git.wrappers.GitCommit;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class NotificationUtils {

    public static Set<GitFile> conflictingFiles(final GitBranch branch, final GitCommit newCommit, final Map<String, GitCommit> commits) {

        Set<GitFile> newFiles = new HashSet<GitFile>();
        Set<GitFile> branchFiles = filesOnBranch(branch, commits);

        for (GitFile file : newCommit.getFiles()){
            newFiles.add(file);
        }

        branchFiles.retainAll(newFiles);
        return branchFiles;
    }

    public static Set<GitFile> filesOnBranch(final GitBranch branch, final Map<String, GitCommit> commits) {
        GitCommit commit = branch.getCommit();

        Set<GitFile> files = new HashSet<GitFile>();
        getFiles(commit, files, commits);

        return files;
    }


    private static Set<GitFile> getFiles(GitCommit commit, Set<GitFile> files, final Map<String, GitCommit> commits) {
        for (GitFile file : commit.getFiles()) {
            files.add(file);
        }
        for (String commitParentSHA1 : commit.getParentsSHA1()) {
            getFiles(commits.get(commitParentSHA1), files, commits);
        }

        return files;
    }
}
