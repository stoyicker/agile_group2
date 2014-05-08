package org.arnolds.agileappproject.agileappmodule.git.wrappers;


import org.kohsuke.github.GHIssue;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;

/**
 * This class wraps the API Issue class.
 * It is immutable.
 */
public class GitIssue {
    private int number;
    private String title;
    private String body;
    private Date createdAt;
    private Date closedAt;
    private Date updatedAt;
    private URL url;
    private GitUser user;

    public GitIssue() {}

    public GitIssue(GHIssue apiIssue) {
        this.number = apiIssue.getNumber();
        this.title = apiIssue.getTitle();
        this.body = apiIssue.getBody();
        this.createdAt = apiIssue.getCreatedAt();
        this.closedAt = apiIssue.getClosedAt();
        this.updatedAt = apiIssue.getUpdatedAt();
        this.url = apiIssue.getUrl();
        this.user = new GitUser(apiIssue.getUser());
    }

    public int getNumber() {
        return number;
    }

    public String getTitle() {
        return title;
    }

    public String getBody() {
        return body;
    }

    public Date getCreatedAt() {
        return new Date(createdAt.getTime());
    }

    public Date getClosedAt() {
        return new Date(closedAt.getTime());
    }

    public Date getUpdatedAt() {
        return new Date(updatedAt.getTime());
    }

    public GitUser getUser() {
        return user;
    }

    public URL getUrl() {
        try {
            return new URL(url.getProtocol(), url.getHost(), url.getFile());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public boolean equals(Object o) {
        if (o != null && o instanceof GitIssue) {
            GitIssue otherIssue = (GitIssue) o;
            return getNumber() == otherIssue.getNumber();
        }

        return false;
    }

    @Override
    public int hashCode() {
        int multiplier = (getNumber() != 0) ? getNumber() : 39;
        return title.hashCode()*body.hashCode()*user.hashCode()*multiplier;
    }

    @Override
    public String toString() {
        return "Issue ["+ getNumber() +"]: "+ getTitle();
    }
}
