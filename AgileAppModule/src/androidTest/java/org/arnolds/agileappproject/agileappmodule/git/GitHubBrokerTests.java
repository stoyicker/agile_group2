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

    private static final int TIMEOUT_MILLIS = 1000;
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

    public void setUp() throws IllegalAccessException, NoSuchFieldException,
            GitHubBroker.ListenerAlreadyRegisteredException, GitHubBroker.NullArgumentException,
            IOException {
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
        try {
            broker.disconnect();
            broker.removeSubscriber(listener);
        }
        catch (Exception e) {
            fail();
        }
        Mockito.verify(listener, only()).onDisconnected();
    }

    public void test_getRepositories_connected() {
        try {
            broker.getAllRepos();
        }
        catch (Exception e) {
            fail();
        }
        Mockito.verify(listener, timeout(TIMEOUT_MILLIS)).onAllReposRetrieved(true,
                repositories.values());
    }

    public void test_getRepositories_not_connected() {
        try {
            broker.disconnect();
        }
        catch (Exception e) {
            fail();
        }
        try {
            broker.getAllRepos();
            fail();
        }
        catch (GitHubBroker.AlreadyNotConnectedException e) {
        }
    }


    public void test_getIssues_not_connected() {
        try {
            broker.disconnect();
        }
        catch (Exception e) {
            fail();
        }
        try {
            broker.getAllIssues();
            fail();
        }
        catch (GitHubBroker.AlreadyNotConnectedException e) {
        }
        catch (GitHubBroker.RepositoryNotSelectedException e) {
            fail();
        }
    }

    public void test_getIssues_connected_selected() {
        try {
            broker.selectRepo(repo);
        }
        catch (Exception e) {
            fail();
        }
        Mockito.verify(listener, timeout(TIMEOUT_MILLIS).only()).onRepoSelected(true);
        try {
            broker.getAllIssues();
        }
        catch (Exception e) {
            fail();
        }
        Collection<GHIssue> expectedIssues = new LinkedList<GHIssue>();
        expectedIssues.addAll(openIssues);
        expectedIssues.addAll(closedIssues);
        Mockito.verify(listener, timeout(GitHubBrokerTests.TIMEOUT_MILLIS))
                .onAllIssuesRetrieved(true, expectedIssues);
    }

    public void test_getIssues_connected_not_selected() {
        try {
            broker.getAllIssues();
            fail();
        }
        catch (GitHubBroker.RepositoryNotSelectedException e) {
        }
        catch (GitHubBroker.AlreadyNotConnectedException e) {
            fail();
        }
    }

    public void test_getBranches_not_connected() {
        try {
            broker.disconnect();
        }
        catch (GitHubBroker.AlreadyNotConnectedException e) {
            fail();
        }
        try {
            broker.getAllBranches();
            fail();
        }
        catch (GitHubBroker.RepositoryNotSelectedException e) {
            fail();
        }
        catch (GitHubBroker.AlreadyNotConnectedException e) {
        }
    }

    public void test_getBranches_connected_selected() {
        try {
            broker.selectRepo(repo);
        }
        catch (Exception e) {
            fail();
        }
        Mockito.verify(listener, timeout(TIMEOUT_MILLIS).only()).onRepoSelected(true);
        try {
            broker.getAllBranches();
        }
        catch (Exception e) {
            fail();
        }
        Mockito.verify(listener, timeout(TIMEOUT_MILLIS))
                .onAllBranchesRetrieved(true, branches.values());
    }

    public void test_getBranches_connected_not_selected() {
        try {
            broker.getAllBranches();
            fail();
        }
        catch (GitHubBroker.RepositoryNotSelectedException e) {
        }
        catch (GitHubBroker.AlreadyNotConnectedException e) {
            fail();
        }
    }

    public void test_remove_subscriber_when_not_subscribed() {
        try {
            broker.removeSubscriber(listener);
        }
        catch (Exception e) {
            fail();
        }
        try {
            broker.removeSubscriber(listener);
            fail();
        }
        catch (GitHubBroker.ListenerNotRegisteredException e) {
        }
        catch (GitHubBroker.NullArgumentException e) {
            fail();
        }
    }

    public void test_select_repo_not_found() {
        try {
            broker.selectRepo(Mockito.mock(GHRepository.class));
        }
        catch (Exception e) {
            fail();
        }
        Mockito.verify(listener, Mockito.timeout(TIMEOUT_MILLIS).only()).onRepoSelected(false);
    }

    public void test_select_repo_null() {
        try {
            broker.selectRepo(null);
            fail();
        }
        catch (GitHubBroker.NullArgumentException e) {
        }
        catch (GitHubBroker.AlreadyNotConnectedException e) {
            fail();
        }
    }

    public void test_select_repo_not_connected() {
        try {
            broker.disconnect();
        }
        catch (GitHubBroker.AlreadyNotConnectedException e) {
            fail();
        }
        try {
            broker.selectRepo(Mockito.mock(GHRepository.class));
            fail();
        }
        catch (GitHubBroker.NullArgumentException e) {
            fail();
        }
        catch (GitHubBroker.AlreadyNotConnectedException e) {
        }
    }

    public void test_select_repo_valid() {
        try {
            broker.selectRepo(repo);
        }
        catch (Exception e) {
            fail();
        }
        Mockito.verify(listener, Mockito.timeout(TIMEOUT_MILLIS).only()).onRepoSelected(true);
    }

    public void test_remove_subscriber_when_null() {
        try {
            broker.removeSubscriber(null);
            fail();
        }
        catch (GitHubBroker.ListenerNotRegisteredException e) {
            fail();
        }
        catch (GitHubBroker.NullArgumentException e) {
        }
    }

    public void test_disconnect_when_not_connected() {
        try {
            broker.disconnect();
        }
        catch (GitHubBroker.AlreadyNotConnectedException e) {
            fail();
        }
        try {
            broker.disconnect();
            fail();
        }
        catch (GitHubBroker.AlreadyNotConnectedException e) {
        }
    }

    public void test_disconnect_when_connected() {
        try {
            broker.disconnect();
        }
        catch (GitHubBroker.AlreadyNotConnectedException e) {
            fail();
        }
        Mockito.verify(listener, only()).onDisconnected();
    }


}
