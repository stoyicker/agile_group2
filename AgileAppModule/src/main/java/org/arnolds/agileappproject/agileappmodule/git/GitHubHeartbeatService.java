package org.arnolds.agileappproject.agileappmodule.git;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import org.apache.commons.io.filefilter.FalseFileFilter;
import org.kohsuke.github.GHRateLimit;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GHUser;
import org.kohsuke.github.GitHub;

import java.io.IOException;
import java.util.Collection;
import java.util.Objects;

/**
 * Created by Robert on 2014-04-08.
 * Service that monitors connection to GitHub.
 */
public class GitHubHeartbeatService extends Service {

    private class HeartBeatThread extends Thread {

        private class GHBL extends GitHubBrokerListener {
            @Override
            public void onAllReposRetrieved(boolean success, Collection<GHRepository> repos){
                connection = success;
                Log.wtf("debug", "Repos retrieved: " + success + " | repos: " + repos);
            }
        };

        private GHBL listener = new GHBL();
        private boolean connection = true;

        @Override
        public void run(){
            Log.wtf("debug", "ONRUN");
            final IGitHubBroker broker = GitHubBroker.getInstance();
            while(serviceActive){
                // Try connection
                try {
                    GitHubBroker.getInstance().getAllRepos(listener);
                } catch (GitHubBroker.AlreadyNotConnectedException e) {
                    e.printStackTrace();
                    connection = false;
                }

                // Wait until next check
                try {
                    synchronized (threadBlock) {
                        threadBlock.wait(1000);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                Log.wtf("debug", "Connection: " + connection);
            }
        }
    }

    private boolean serviceActive;
    private HeartBeatThread thread;
    private Object threadBlock = new Object();

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        Log.wtf("debug", "ONSTART");
        this.serviceActive = true;
        this.thread = new HeartBeatThread();
        this.thread.start();
        return Service.START_STICKY;
    }

    @Override
    public void onDestroy(){
        this.serviceActive = false;
        synchronized (threadBlock) {
            this.threadBlock.notify();
        }
    }
}