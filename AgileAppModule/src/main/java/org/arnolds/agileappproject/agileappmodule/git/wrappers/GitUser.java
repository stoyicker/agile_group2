package org.arnolds.agileappproject.agileappmodule.git.wrappers;


import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GHUser;

import java.io.IOException;
import java.util.Map;

/**
 * Immutable value object for the Git User.
 */
public class GitUser {
    private int userId;
    private String name;
    private String email;
    private String avatarUrl;
    private String login;

    public GitUser() {}

    public GitUser(GHUser apiUser) {
        if (apiUser != null ) {

            this.userId = apiUser.getId();
            this.login = apiUser.getLogin();
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
        else {
            this.userId = 0;
            this.login = "";
            this.avatarUrl = "";
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
        return name.hashCode()*email.hashCode()*login.hashCode()*39;
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

    public String getLogin() {
        return login;
    }

}
