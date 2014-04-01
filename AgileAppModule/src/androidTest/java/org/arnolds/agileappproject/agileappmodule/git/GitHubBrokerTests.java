package org.arnolds.agileappproject.agileappmodule.git;

import android.test.InstrumentationTestCase;

import org.kohsuke.github.GHBranch;
import org.kohsuke.github.GHIssue;
import org.kohsuke.github.GHIssueState;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GHUser;
import org.kohsuke.github.GitHub;
import org.mockito.Mockito;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.only;
import static org.mockito.Mockito.timeout;

public class GitHubBrokerTests extends InstrumentationTestCase {

    private IGitHubBroker broker;
    private IGitHubBrokerListener listener;
    private static GHRepository repo;
    private static Map<String, GHBranch> branches = new HashMap<String, GHBranch>();
    private static List<GHIssue> openIssues = new LinkedList<GHIssue>(), closedIssues =
            new LinkedList<GHIssue>();
    private static Map<String, GHRepository> repositories = new HashMap<String, GHRepository>();
    private static boolean firstRun = true;

    public void init() {
        System.setProperty("dexmaker.dexcache",
                getInstrumentation().getTargetContext().getCacheDir().getAbsolutePath());
        repo = Mockito.mock(GHRepository.class);
        for (int i = 0; i < 10; i++) {
            branches.put("branch" + i, Mockito.mock(GHBranch.class));
            openIssues.add(Mockito.mock(GHIssue.class));
            closedIssues.add(Mockito.mock(GHIssue.class));
            repositories.put("repo" + i, Mockito.mock(GHRepository.class));
        }
        repositories.put("customRepo", repo);
    }

    public void setUp() throws IllegalAccessException, NoSuchFieldException, IOException {
        if (firstRun) {
            firstRun = false;
            init();
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
        Mockito.when(repo.getBranches()).thenReturn(branches);
        Mockito.when(repo.getIssues(GHIssueState.OPEN)).thenReturn(openIssues);
        Mockito.when(repo.getIssues(GHIssueState.CLOSED)).thenReturn(closedIssues);
        userField.set(broker, user);
        userField.setAccessible(false);

        listener = Mockito.mock(IGitHubBrokerListener.class);
        broker.addSubscriber(listener);
    }

    public void test_remove_subscriber_when_subscribed() {
        broker.removeSubscriber(listener);
        broker.disconnect();
        Mockito.verifyZeroInteractions(listener);
    }

    public void test_getRepositories_connected() {
        broker.getAllRepos();
        Mockito.verify(listener, timeout(1000))
                .onAllReposRetrieved(true, repositories.values());
    }

    public void test_getRepositories_not_connected() {
        broker.disconnect();
        try {
            broker.getAllRepos();
            fail();
        }
        catch (IllegalStateException e) {
        }
    }


    public void test_getIssues_not_connected() {
        broker.disconnect();
        try {
            broker.getAllIssues();
            fail();
        }
        catch (IllegalStateException e) {
        }
    }

    public void test_getIssues_connected_selected() {
        broker.selectRepo(repo);
        Mockito.verify(listener, timeout(1000).only()).onRepoSelected(true);
        broker.getAllIssues();
        Collection<GHIssue> expectedIssues = new LinkedList<GHIssue>();
        expectedIssues.addAll(openIssues);
        expectedIssues.addAll(closedIssues);
        Mockito.verify(listener, timeout(1000))
                .onAllIssuesRetrieved(true, expectedIssues);
    }

    public void test_getIssues_connected_not_selected() {
        try {
            broker.getAllIssues();
            fail();
        }
        catch (IllegalStateException e) {
        }
    }

    public void test_getBranches_not_connected() {
        broker.disconnect();
        try {
            broker.getAllBranches();
            fail();
        }
        catch (IllegalStateException e) {
        }
    }

    public void test_getBranches_connected_selected() {
        broker.selectRepo(repo);
        Mockito.verify(listener, timeout(1000).only()).onRepoSelected(true);
        broker.getAllBranches();
        Mockito.verify(listener, timeout(1000))
                .onAllBranchesRetrieved(true, branches.values());
    }

    public void test_getBranches_connected_not_selected() {
        try {
            broker.getAllBranches();
            fail();
        }
        catch (IllegalStateException e) {
        }
    }

    public void test_remove_subscriber_when_not_subscribed() {
        broker.removeSubscriber(listener);
        try {
            broker.removeSubscriber(listener);
            fail();
        }
        catch (IllegalArgumentException e) {
        }
    }

    public void test_select_repo_not_found() {
        broker.selectRepo(Mockito.mock(GHRepository.class));
        Mockito.verify(listener, Mockito.timeout(1000).only()).onRepoSelected(false);
    }

    public void test_select_repo_null() {
        try {
            broker.selectRepo(null);
            fail();
        }
        catch (IllegalArgumentException e) {
        }
    }

    public void test_select_repo_not_connected() {
        broker.disconnect();
        try {
            broker.selectRepo(Mockito.mock(GHRepository.class));
            fail();
        }
        catch (IllegalStateException e) {
        }
    }

    public void test_select_repo_valid() {
        broker.selectRepo(repo);
        Mockito.verify(listener, Mockito.timeout(1000).only()).onRepoSelected(true);
    }

    public void test_remove_subscriber_when_null() {
        try {
            broker.removeSubscriber(null);
            fail();
        }
        catch (IllegalArgumentException e) {
        }
    }

    public void test_disconnect_when_not_connected() {
        broker.disconnect();
        try {
            broker.disconnect();
            fail();
        }
        catch (IllegalStateException e) {
        }
    }

    public void test_disconnect_when_connected() {
        broker.disconnect();
        Mockito.verify(listener, only()).onDisconnected();
    }


}
