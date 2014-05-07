package org.arnolds.agileappproject.agileappmodule.git.notifications;

import android.test.InstrumentationTestCase;
import android.util.Log;

import org.kohsuke.github.GHBranch;
import org.kohsuke.github.GHCommit;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

public class NotificationsUtilsTests extends InstrumentationTestCase {

    private static final String NEW_COMMIT_SHA1 = "newCommitSHA1";
    private static final String ROOT_SHA1 = "rootSHA1";
    private static final String FIRST_BRANCH_COMMIT_SHA1 = "firstBranchCommitSHA1";
    private static final String SECOND_COMMIT_SHA1 = "secondCommitSHA1";
    private static final String UNRELATED_COMMIT_SHA1 = "unrelatedCommitSHA1";

    GHBranch branch;
    GHCommit newCommit;
    LinkedHashMap<String, GHCommit> oldCommits = new LinkedHashMap<String, GHCommit>();
    List<GHCommit.File> ghFiles = new ArrayList<GHCommit.File>();

    GitFile[] files = {new GitFile("file0", "/github/file0"), new GitFile("file1", "/github/file1"),
            new GitFile("file2", "/github/file2"), new GitFile("file3", "/github/file3")};


    GHCommit unRelatedCommit;
    GitFile unRelatedFile = new GitFile("unrelatedFile", "somewhere/else/unrelatedFile");

    public void setUp() throws IOException {
        System.setProperty("dexmaker.dexcache",
                getInstrumentation().getTargetContext().getCacheDir().getAbsolutePath());

        for (GitFile gitFile : files){
            GHCommit.File file = Mockito.mock(GHCommit.File.class);
            try {
                URL url = new URL("http://www.example.com"+gitFile.getUrl());
                Mockito.when(file.getBlobUrl()).thenReturn(url);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            Mockito.when(file.getFileName()).thenReturn(gitFile.getFileName());
            ghFiles.add(file);

        }

        GHCommit root = Mockito.mock(GHCommit.class);
        Mockito.when(root.getParents()).thenThrow(new IOException());

        Mockito.when(root.getSHA1()).thenReturn(ROOT_SHA1);
        Mockito.when(root.getFiles()).thenReturn(ghFiles.subList(0,2));
        List<GHCommit> rootList = new ArrayList<GHCommit>();
        rootList.add(root);


        GHCommit secondCommit = Mockito.mock(GHCommit.class);
        try {
            Mockito.when(root.getParents()).thenReturn(rootList);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Mockito.when(secondCommit.getSHA1()).thenReturn(SECOND_COMMIT_SHA1);
        Mockito.when(secondCommit.getFiles()).thenReturn(ghFiles.subList(2,3));

        GHCommit firstBranchCommit = Mockito.mock(GHCommit.class);

        Mockito.when(firstBranchCommit.getParents()).thenReturn(rootList);
        Mockito.when(firstBranchCommit.getSHA1()).thenReturn(FIRST_BRANCH_COMMIT_SHA1);
        Mockito.when(firstBranchCommit.getFiles()).thenReturn(ghFiles.subList(1,2));

        List<GHCommit> firstBranchCommitList = new ArrayList<GHCommit>();
        firstBranchCommitList.add(firstBranchCommit);

        newCommit = Mockito.mock(GHCommit.class);
        Mockito.when(newCommit.getFiles()).thenReturn(ghFiles.subList(0,1));
        Mockito.when(newCommit.getParents()).thenReturn(firstBranchCommitList);
        Mockito.when(newCommit.getSHA1()).thenReturn(NEW_COMMIT_SHA1);

        unRelatedCommit = Mockito.mock(GHCommit.class);
        Mockito.when(unRelatedCommit.getFiles()).thenReturn(ghFiles.subList(3,4));
        Mockito.when(unRelatedCommit.getParents()).thenReturn(rootList);
        Mockito.when(unRelatedCommit.getSHA1()).thenReturn(UNRELATED_COMMIT_SHA1);

        oldCommits.put(ROOT_SHA1, root);
        oldCommits.put(SECOND_COMMIT_SHA1, secondCommit);
        oldCommits.put(FIRST_BRANCH_COMMIT_SHA1, firstBranchCommit);

        branch = Mockito.mock(GHBranch.class);
        Mockito.when(branch.getSHA1()).thenReturn(FIRST_BRANCH_COMMIT_SHA1);

    }
    public void test_conflictingFiles_conflict(){

        Set<GitFile> conflictingFiles = new HashSet<GitFile>();
        conflictingFiles.add(files[0]);

        assertEquals(conflictingFiles, NotificationUtils.conflictingFiles(branch, newCommit, oldCommits));

    }

    public void test_conflictingFiles_no_conflict(){
        assertTrue(NotificationUtils.conflictingFiles(branch, unRelatedCommit, oldCommits).isEmpty());

    }

}


