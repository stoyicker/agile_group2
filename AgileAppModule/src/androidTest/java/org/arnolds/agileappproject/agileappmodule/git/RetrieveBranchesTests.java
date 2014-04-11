package org.arnolds.agileappproject.agileappmodule.git;

import android.test.InstrumentationTestCase;

import org.kohsuke.github.GHBranch;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GHUser;
import org.kohsuke.github.GitHub;
import org.mockito.Mockito;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class RetrieveBranchesTests extends InstrumentationTestCase {

    private static final int TIMEOUT_MILLIS = 1000;
    private IGitHubBroker broker;
    private static Collection<GHBranch> branches;
    private boolean firstRun = Boolean.TRUE;
    private static Map<String, GHRepository> repositories = new HashMap<String, GHRepository>();
    private Map<String, GHBranch> innerBranches;
    private IGitHubBrokerListener listener;
    private static GHRepository repo;

    private void init() {
        System.setProperty("dexmaker.dexcache",
                getInstrumentation().getTargetContext().getCacheDir().getAbsolutePath());
        repo = Mockito.mock(GHRepository.class);
        repositories.put("customRepo", repo);
    }

    public void setUp() throws IllegalAccessException, NoSuchFieldException, IOException,
            GitHubBroker.ListenerAlreadyRegisteredException, GitHubBroker.NullArgumentException {
        if (firstRun) {
            init();
            firstRun = Boolean.FALSE;
        }

        Field brokerField = GitHubBroker.class.getDeclaredField("instance");
        brokerField.setAccessible(true);
        brokerField.set(null, null);
        brokerField.setAccessible(false);

        broker = GitHubBroker.getInstance();
        Field field = GitHubBroker.class.getDeclaredField("session");
        field.setAccessible(true);

        GitHub gitHub = Mockito.mock(GitHub.class);

        field.set(broker, gitHub);
        field.setAccessible(false);

        Field userField = GitHubBroker.class.getDeclaredField("user");
        userField.setAccessible(true);
        GHUser user = Mockito.mock(GHUser.class);
        Mockito.when(user.getRepositories()).thenReturn(repositories);
        innerBranches = new HashMap<String, GHBranch>();
        userField.set(broker, user);
        userField.setAccessible(false);

        listener = Mockito.mock(IGitHubBrokerListener.class);
        branches = new HashSet<GHBranch>();
    }

    public void test_branches_polling_when_removed() {
        try {
            broker.selectRepo(repo.getName(), listener);
        }
        catch (GitHubBroker.AlreadyNotConnectedException e) {
            fail();
        }
        catch (GitHubBroker.NullArgumentException e) {
            fail();
        }
        Mockito.verify(listener, Mockito.timeout(TIMEOUT_MILLIS).only()).onRepoSelected(true);
        branches = new HashSet<GHBranch>();
        branches.add(Mockito.mock(GHBranch.class));
        branches.add(Mockito.mock(GHBranch.class));
        for (GHBranch branch : branches)
            innerBranches.put(branch.getName(), branch);
        try {
            Mockito.when(repo.getBranches()).thenReturn(innerBranches);
        }
        catch (IOException e) {
            fail();
        }
        try {
            broker.getAllBranches(listener);
        }
        catch (GitHubBroker.RepositoryNotSelectedException e) {
            fail();
        }
        catch (GitHubBroker.AlreadyNotConnectedException e) {
            fail();
        }
        Mockito.verify(listener, Mockito.timeout(TIMEOUT_MILLIS))
                .onAllBranchesRetrieved(true, branches);
        branches.remove("branch2");
        try {
            broker.getAllBranches(listener);
        }
        catch (GitHubBroker.RepositoryNotSelectedException e) {
            fail();
        }
        catch (GitHubBroker.AlreadyNotConnectedException e) {
            fail();
        }
        Mockito.verify(listener, Mockito.timeout(TIMEOUT_MILLIS))
                .onAllBranchesRetrieved(true, branches);
    }
}
