package org.arnolds.agileappproject.agileappmodule.git;


import android.test.InstrumentationTestCase;

import org.arnolds.agileappproject.agileappmodule.git.notifications.GitHubNotificationService;
import org.kohsuke.github.GHCommit;
import org.kohsuke.github.GHRepository;
import org.mockito.Mockito;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class GitHubNotificationServiceTests extends InstrumentationTestCase {
    public static final long SECOND = 1000;
    public static final long SLEEP_TIME = 4;
    private GitHubNotificationService service;
    private GHRepository repo;
    private List<GHCommit> dummyCommits;

    public void init() {
        System.setProperty("dexmaker.dexcache",
                getInstrumentation().getTargetContext().getCacheDir().getAbsolutePath());

        repo = Mockito.mock(GHRepository.class);

        GHCommit commit1 = new GHCommit();
        GHCommit commit2 = new GHCommit();

        dummyCommits = new ArrayList<GHCommit>();
        dummyCommits.add(commit1);
        dummyCommits.add(commit2);


    }

    public void setUp() {
        init();


    }

    public void test_commits_changed() throws NoSuchFieldException, IllegalAccessException {
        //Mockito.when(repo.listCommits().asList()).thenReturn(null);
        service = GitHubNotificationService.getInstance();

        myListener listener = new myListener();
        service.addCommitListener(listener);

        Field serviceField = GitHubNotificationService.class.getDeclaredField("repo");
        serviceField.setAccessible(true);
        serviceField.set(service, repo);
        serviceField.setAccessible(false);

        assertNotNull(dummyCommits);

        Mockito.when(repo.listCommits().asList()).thenReturn(dummyCommits);

        //Wait for event in SLEEP_TIME seconds
        for (int i = 0; !listener.isEventReceived() && i < SLEEP_TIME; i++) {
            try {
                Thread.sleep(SECOND);
            }
            catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        assertTrue(listener.isEventReceived());

        List<GHCommit> commits = listener.getCommits();
        assertTrue(commits.containsAll(dummyCommits));
    }


    private class myListener implements PropertyChangeListener {

        private List<GHCommit> commits;
        private boolean eventReceived = false;

        @Override
        public void propertyChange(PropertyChangeEvent event) {
            commits = (List<GHCommit>) event.getNewValue();
            eventReceived = true;


        }

        public boolean isEventReceived() {
            return eventReceived;
        }

        public List<GHCommit> getCommits() {
            return commits;
        }


    }


}
