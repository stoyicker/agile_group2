package org.arnolds.agileappproject.agileappmodule;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import org.arnolds.agileappproject.agileappmodule.git.GitHubBroker;
import org.arnolds.agileappproject.agileappmodule.git.GitHubBrokerListener;
import org.arnolds.agileappproject.agileappmodule.git.IGitHubBrokerListener;

/**
 * The MIT License (MIT)
 * <p/>
 * <p/>
 * <p/>
 * Copyright (c) 2014 agile_arnolds
 * <p/>
 * <p/>
 * <p/>
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * <p/>
 * of this software and associated documentation files (the "Software"), to deal
 * <p/>
 * in the Software without restriction, including without limitation the rights
 * <p/>
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * <p/>
 * copies of the Software, and to permit persons to whom the Software is
 * <p/>
 * furnished to do so, subject to the following conditions:
 * <p/>
 * <p/>
 * <p/>
 * The above copyright notice and this permission notice shall be included in
 * <p/>
 * all copies or substantial portions of the Software.
 * <p/>
 * <p/>
 * <p/>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * <p/>
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * <p/>
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * <p/>
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * <p/>
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * <p/>
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * <p/>
 * THE SOFTWARE.
 */
public class MainRepoActivity extends Activity {

    private final IGitHubBrokerListener connectListener = new ConnectionListener();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            GitHubBroker.getInstance().connect("agilearnold", "beback2", getApplicationContext());
        } catch (GitHubBroker.AlreadyConnectedException e) {
            Log.wtf("debug", e.getClass().getName(), e);
        }

        try {
            GitHubBroker.getInstance().addSubscriber(connectListener);
        } catch (GitHubBroker.NullArgumentException e) {
            Log.wtf("debug", e.getClass().getName(), e);
        } catch (GitHubBroker.ListenerAlreadyRegisteredException e) {
            Log.wtf("debug", e.getClass().getName(), e);
        }

    }


    private final class ConnectionListener extends GitHubBrokerListener {

        @Override
        public void onConnected() {
            System.out.println("connected");
            startActivity(new Intent(getApplicationContext(), SelectRepositoryFragment.class));
            System.out.println("proceed");
            finish();
        }

        @Override
        public void onConnectionRefused(String reason) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.connection_error), Toast.LENGTH_LONG);
                }
            });
        }
    }
}
