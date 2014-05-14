package org.arnolds.agileappproject.agileappmodule.git.notifications;

import java.util.ArrayList;
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

        public void addDirectory(Node dir) {
            Node existingNode = directories.get(dir.getDirName());
            if (existingNode == null) {
                directories.put(dir.getDirName(), dir);
            }
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
        root = new Node("/");

        for(GitFile file : gitFiles) {
            String path = file.getFileName();
            String[] stringParts = path.split("/");
            Node previousNode = root;
            for (int i = 0; i < stringParts.length; i++) {
                if (i != stringParts.length) {
                    // Directory
                    Node newNode = new Node(stringParts[i]);
                    previousNode.addDirectory(newNode);
                    previousNode = newNode;
                } else {
                    // File
                    previousNode.addFile(file);
                }
            }
        }

    }

    public List<GitFile> getFiles(final String path) {
        String[] pathParts = path.split("/");
        Node currentNode = root;
        for (int i = 0; i < pathParts.length; i++) {
            currentNode = currentNode.getDirectory(pathParts[i]);
            if (currentNode == null) {
                throw new IllegalArgumentException(path + " is not a valid path.");
            }
        }

        return currentNode.getFiles();
    }

    public List<String> getDirectories(final String path) {
        String[] pathParts = path.split("/");
        Node currentNode = root;
        for (int i = 0; i < pathParts.length - 1; i++) {
            currentNode = currentNode.getDirectory(pathParts[i]);
            if (currentNode == null) {
                throw new IllegalArgumentException(path + " is not a valid path.");
            }
        }

        return currentNode.getDirectories();
    }
}
