package org.arnolds.agileappproject.agileappmodule.git.notifications;

import android.test.AndroidTestCase;

import java.util.ArrayList;
import java.util.List;

public class GitFileTreeTests extends AndroidTestCase {
    private static final String dir1 = "body_building";
    private static final String dir1sub1 = "secret";

    private static final String dir2 = "pumping-iron";
    private static final String dir2sub1 = "arnold";

    private static final GitFile[] files = {
        new GitFile(dir1 + "/" + dir1sub1 + "/" + "steroids.txt", null),
        new GitFile(dir2 + "/" + dir2sub1 + "/" + "chopper", null),
        new GitFile(dir2 + "/" + "chopper.txt", null),
        new GitFile(dir2 + "/" + "predator.ical", null),
        new GitFile("readme.md", null)
    };

    private static final String path1 = "/";
    private static final String path2 = "/" + dir1;
    private static final String path3 = dir2 + "/";
    private static final String path4 = "/" + dir1 + "/" + dir1sub1 + "/";
    private static final String path5 = dir2 + "/" + dir2sub1;
    private static final String path6 = "unrelated/path/asdf.txt";
    private GitFileTree tree;

    @Override
    protected void setUp() throws Exception {
        List<GitFile> fileList = new ArrayList<GitFile>();

        for (GitFile gitFile : files) {
            fileList.add(gitFile);
        }

        tree = new GitFileTree(fileList);
    }

    public void test_files_path1() {
        List<GitFile> retreievedFiles = tree.getFiles(path1);
        assertEquals(1, retreievedFiles.size());
        assertTrue(retreievedFiles.contains(files[4]));
    }

    public void test_dirs_path1() {
        List<String> retreivedDirs = tree.getDirectories(path1);
        assertEquals(2, retreivedDirs.size());
        assertTrue(retreivedDirs.contains(dir1));
        assertTrue(retreivedDirs.contains(dir2));
    }

    public void test_files_path2() {
        List<GitFile> retreievedFiles = tree.getFiles(path2);
        assertEquals(0, retreievedFiles.size());
    }

    public void test_dirs_path2() {
        List<String> retreivedDirs = tree.getDirectories(path2);
        assertEquals(1, retreivedDirs.size());
        assertTrue(retreivedDirs.contains(dir1sub1));
    }

    public void test_files_path3() {
        List<GitFile> retreievedFiles = tree.getFiles(path3);
        assertEquals(2, retreievedFiles.size());
        assertTrue(retreievedFiles.contains(files[2]));
        assertTrue(retreievedFiles.contains(files[3]));
    }

    public void test_dirs_path3() {
        List<String> retreivedDirs = tree.getDirectories(path3);
        assertEquals(1, retreivedDirs.size());
        assertTrue(retreivedDirs.contains(dir2sub1));
    }

    public void test_files_path4() {
        List<GitFile> retreievedFiles = tree.getFiles(path4);
        assertEquals(1, retreievedFiles.size());
        assertTrue(retreievedFiles.contains(files[0]));
    }

    public void test_dirs_path4() {
        List<String> retreivedDirs = tree.getDirectories(path4);
        assertEquals(0, retreivedDirs.size());
    }

    public void test_files_path5() {
        List<GitFile> retreievedFiles = tree.getFiles(path5);
        assertEquals(1, retreievedFiles.size());
        assertTrue(retreievedFiles.contains(files[1]));
    }

    public void test_dirs_path5() {
        List<String> retreivedDirs = tree.getDirectories(path5);
        assertEquals(0, retreivedDirs.size());
    }

    public void test_files_path6() {
        boolean caughtException = false;
        try {
            List<GitFile> retreievedFiles = tree.getFiles(path6);
        } catch (IllegalArgumentException e) {
            caughtException = true;
        }

        assertTrue(caughtException);
    }

    public void test_dirs_path6() {
        boolean caughtException = false;
        try {
            List<String> retreivedDirs = tree.getDirectories(path6);
        } catch (IllegalArgumentException e) {
            caughtException = true;
        }

        assertTrue(caughtException);
    }
}
