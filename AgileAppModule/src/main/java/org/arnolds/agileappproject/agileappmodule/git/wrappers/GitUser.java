package org.arnolds.agileappproject.agileappmodule.git.wrappers;


import org.kohsuke.github.GHUser;

import java.io.IOException;

/**
 * Immutable value object for the Git User.
 */
public class GitUser {
    private int userId;
    private String name;
    private String email;
    private String avatarUrl;

    public GitUser() {}

    public GitUser(GHUser apiUser) {
        this.userId = apiUser.getId();

        this.avatarUrl = apiUser.getAvatarUrl();

        try {
            this.name = apiUser.getName();
            this.email = apiUser.getEmail();
        } catch (IOException e) {
            e.printStackTrace();
            this.name = "Unable to retrieve username";
            this.email = "Unable to retrieve email";
        }
    }

    @Override
    public boolean equals(Object o) {
        if (o != null && o instanceof GitUser) {
            GitUser otherUser = (GitUser) o;
            return userId == otherUser.getId();
        }

        return false;
    }

    @Override
    public int hashCode() {
        int multiplier = (userId != 0) ? userId : 13;
        return name.hashCode()*email.hashCode()*multiplier;
    }

    @Override
    public String toString() {
        return "User ["+userId+"]: "+ name + " Email: " +email;
    }

    public int getId() {
        return userId;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }
}