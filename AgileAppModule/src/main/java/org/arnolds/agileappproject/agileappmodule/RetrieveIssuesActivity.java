package org.arnolds.agileappproject.agileappmodule;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import org.arnolds.agileappproject.agileappmodule.git.GitHubBroker;
import org.arnolds.agileappproject.agileappmodule.git.GitHubBrokerListener;
import org.arnolds.agileappproject.agileappmodule.git.IGitHubBrokerListener;
import org.kohsuke.github.GHIssue;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

public class RetrieveIssuesActivity extends Activity {

    private static final long ISSUES_POLL_INTERVAL_MILLIS = 30000;
    private IssuesListAdapter listAdapter;
    private final IGitHubBrokerListener issuesListener = new IssuesListener();

    @Override
    public View onCreateView(View parent, String name, Context context, AttributeSet attrs) {
        View ret = super.onCreateView(parent, name, context, attrs);


        return ret;
    }

    private void onIssuesReceived(Collection<GHIssue> issues){
        listAdapter.getIssueCollection().clear();
        listAdapter.getIssueCollection().addAll(issues);
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                RetrieveIssuesActivity.this.listAdapter.notifyDataSetChanged();
            }
        });
        updateShownIssues();
    }

    private final class IssuesListener extends GitHubBrokerListener{
        @Override
        public void onAllIssuesRetrieved(boolean success, Collection<GHIssue> issues) {
            if (success){
                onIssuesReceived(issues);
            } else {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(), getResources().getString(R.string.error_retri_branches), Toast.LENGTH_LONG);
                    }
                });
            }
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_retrieve_issues);
    }

    private void updateShownIssues(){
        try {
            Thread.sleep(ISSUES_POLL_INTERVAL_MILLIS);
        } catch (InterruptedException e) {
            Log.wtf("debug", e.getClass().getName(), e);
        }
        try {
            GitHubBroker.getInstance().getAllIssues();
        } catch (GitHubBroker.RepositoryNotSelectedException e) {
            Log.wtf("debug", e.getClass().getName(), e);
        } catch (GitHubBroker.AlreadyNotConnectedException e) {
            if(GitHubBroker.getInstance().isConnected()){
                updateShownIssues();
            }
        }
    }


    public final class IssuesListAdapter extends BaseAdapter {

        private final List<GHIssue> issueCollection = new LinkedList<GHIssue>();

        public List<GHIssue> getIssueCollection() {
            return issueCollection;
        }

        @Override
        public int getCount() {
            return issueCollection.size();
        }

        @Override
        public GHIssue getItem(int position) {
            return issueCollection.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            if (convertView == null){
                convertView = ((LayoutInflater)getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.list_item_branch,null);
                viewHolder = new ViewHolder();
                viewHolder.setNameView((TextView) convertView.findViewById(R.id.title_issue));
                convertView.setTag(viewHolder);
            }
            else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            GHIssue issue = getItem(position);
            viewHolder.getNameView().setText(issue.getTitle());
            return convertView;
        }

        private final class ViewHolder {
            private TextView nameView;

            public TextView getNameView() {
                return nameView;
            }

            public void setNameView(TextView nameView) {
                this.nameView = nameView;
            }

        }
    }

}
