package org.arnolds.agileappproject.agileappmodule.git;


import android.test.InstrumentationTestCase;

import org.arnolds.agileappproject.agileappmodule.git.notifications.GitHubNotificationService;
import org.kohsuke.github.GHCommit;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.PagedIterable;
import org.kohsuke.github.PagedIterator;
import org.mockito.Mockito;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by thrawn on 08/04/14.
 */
public class GitHubNotificationServiceTests extends InstrumentationTestCase {

    private GitHubNotificationService service;
    private GHRepository repo = Mockito.mock(GHRepository.class);
    private GitHubBroker broker = Mockito.mock(GitHubBroker.class);
    private List<GHCommit> dummyCommits;

    public void init() {
        System.setProperty("dexmaker.dexcache",
                getInstrumentation().getTargetContext().getCacheDir().getAbsolutePath());
        Mockito.when(GitHubBroker.getInstance()).thenReturn(broker);

        GHCommit commit1 = new GHCommit();
        GHCommit commit2 = new GHCommit();

        try {
            commit2.createComment("hej hej");
            commit1.createComment("LALALA");
        } catch (IOException e) {
            e.printStackTrace();
        }

        dummyCommits = new ArrayList<GHCommit>();
        dummyCommits.add(commit1);
        dummyCommits.add(commit2);


    }

    public void setUp() {
        init();


    }

    public void test_commits_changed() throws NoSuchFieldException, IllegalAccessException {
        Mockito.when(repo.listCommits().asList()).thenReturn(null);
        service = GitHubNotificationService.getInstance();

        myListener listener = new myListener();
        service.addPropertyChangeListener(listener);

        Field serviceField = GitHubNotificationService.class.getField("repo");
        serviceField.setAccessible(true);
        serviceField.set(service, repo);
        serviceField.setAccessible(false);

        Mockito.when(repo.listCommits().asList()).thenReturn(dummyCommits);



        while (!listener.receivedEvent) {

        }

    }


    private class myListener implements PropertyChangeListener {

        private List<GHCommit> commits;
        public boolean receivedEvent = false;

        @Override
        public void propertyChange(PropertyChangeEvent event) {
            commits = (List<GHCommit>) event.getNewValue();
            receivedEvent = true;


        }

        public List<GHCommit> getCommits() {
            return commits;
        }



    }









}
