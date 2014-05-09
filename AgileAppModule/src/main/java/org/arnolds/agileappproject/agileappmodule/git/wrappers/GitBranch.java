package org.arnolds.agileappproject.agileappmodule.git.wrappers;

public class GitBranch {
    private String name;
    private GitCommit commit;

    public GitBranch() {}

    public GitBranch(String name, GitCommit commit) {
        this.name = name;
        this.commit = commit;
    }

    public String getSHA1() {
        return commit.getSHA1();
    }

    public String getName() {
        return name;
    }

    public GitCommit getCommit() {
        return commit;
    }

    @Override
    public boolean equals(Object o) {
        if(o != null && o instanceof GitBranch) {
            GitBranch otherBranch = (GitBranch) o;
            return (getSHA1().equals(otherBranch.getSHA1())
                && getName().equals(otherBranch.getName()));
        }

        return false;
    }

    @Override
    public int hashCode() {
        return getSHA1().hashCode()*getName().hashCode();
    }

    @Override
    public String toString() {
        return "Branch ["+ getSHA1() +"]: " + getName();
    }
}
