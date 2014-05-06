package org.arnolds.agileappproject.agileappmodule.git.notifications;


public final class GitFile {
    private String fileName;
    private String url;

    public GitFile(final String fileName, final String url) {
        this.fileName = fileName;
        this.url = url;
    }

    @Override
    public boolean equals(final Object o) {
        if (o != null && o instanceof GitFile) {
            GitFile otherFile = (GitFile) o;
            return (otherFile.getFileName().equals(this.fileName));
        }
        return false;
    }

    @Override
    public int hashCode() {
        return fileName.hashCode();
    }

    public String getFileName() {
        return fileName;
    }

    public String getUrl() {
        return url;
    }
}
