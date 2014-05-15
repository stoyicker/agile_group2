package org.arnolds.agileappproject.agileappmodule.git.notifications;

import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class GitFileTree {
    public enum Type {
        DIR,
        FILE
    }

    private class Node {
        private String dirName;
        private HashMap<String, Node> directories = new HashMap<String, Node>();
        private List<GitFile> files = new ArrayList<GitFile>();

        public Node(final String dirName) {
            this.dirName = dirName;
        }

        public List<GitFile> getFiles() {
            return files;
        }

        public void addFile(final GitFile gitFile) {
            files.add(gitFile);
        }

        public Node addDirectory(final Node dir) {
            Node existingNode = directories.get(dir.getDirName());
            if (existingNode == null) {
                directories.put(dir.getDirName(), dir);
            } else {
                return existingNode;
            }

            return dir;
        }

        public String getDirName() {
            return dirName;
        }

        public Node getDirectory(String dirName) {
            return directories.get(dirName);
        }

        @Override
        public int hashCode() {
            return dirName.hashCode();
        }

        @Override
        public boolean equals(Object o) {
            if(o != null && o instanceof Node) {
                Node otherNode = (Node) o;
                return dirName.equals(otherNode.getDirName());
            }

            return false;
        }

        public List<String> getDirectories() {
            return new ArrayList<String>(directories.keySet());
        }
    }

    private Node root;

    public GitFileTree(List<GitFile> gitFiles) {
        root = new Node("root");

        for(GitFile file : gitFiles) {
            String path = normalisePath(file.getFileName());
            String[] stringParts = path.split("/");
            Node previousNode = root;
            for (int i = 0; i < stringParts.length; i++) {
                if (i != stringParts.length-1) {
                    // Directory
                    Node newNode = new Node(stringParts[i]);
                    previousNode = previousNode.addDirectory(newNode);
                } else {
                    // File
                    previousNode.addFile(file);
                }
            }
        }

    }

    public List<GitFile> getFiles(final String path) {
        String cleanedPath = normalisePath(path);

        String[] pathParts = cleanedPath.split("/");
        Node currentNode = root;

        if (cleanedPath.length() != 0) {
            for (int i = 0; i < pathParts.length; i++) {
                currentNode = currentNode.getDirectory(pathParts[i]);
                if (currentNode == null) {
                    throw new IllegalArgumentException(path + " is not a valid path.");
                }
            }
        }

        return currentNode.getFiles();
    }

    public List<String> getDirectories(final String path) {
        String cleanedPath = normalisePath(path);

        String[] pathParts = cleanedPath.split("/");
        Node currentNode = root;

        if (cleanedPath.length() != 0) {
            for (int i = 0; i < pathParts.length; i++) {
                currentNode = currentNode.getDirectory(pathParts[i]);
                if (currentNode == null) {
                    throw new IllegalArgumentException(path + " is not a valid path.");
                }
            }
        }

        return currentNode.getDirectories();
    }

    private String normalisePath(final String path) {
        String returnPath = path;

        if (returnPath.equals("/")) {
            returnPath = "";
        } else if (returnPath.startsWith("/")) {
            returnPath = returnPath.substring(1);
        }

        return returnPath;
    }
}