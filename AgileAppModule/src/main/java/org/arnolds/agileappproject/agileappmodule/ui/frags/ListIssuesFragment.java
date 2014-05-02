package org.arnolds.agileappproject.agileappmodule.ui.frags;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.arnolds.agileappproject.agileappmodule.R;
import org.arnolds.agileappproject.agileappmodule.git.GitHubBroker;
import org.arnolds.agileappproject.agileappmodule.git.GitHubBrokerListener;
import org.arnolds.agileappproject.agileappmodule.git.IGitHubBrokerListener;
import org.arnolds.agileappproject.agileappmodule.ui.activities.DrawerLayoutFragmentActivity;
import org.kohsuke.github.GHIssue;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

public class ListIssuesFragment extends ArnoldSupportFragment {

    private static final long ISSUES_POLL_INTERVAL_MILLIS = 2000;
    private final static int MENU_INDEX = 2;
    private IssuesListAdapter listAdapter;
    private IGitHubBrokerListener issuesListener = new IssuesListener();
    private ListView issuesListView;

    public ListIssuesFragment() {
        super(MENU_INDEX);
    }

    @Override
    public void onNewRepositorySelected() {
        updateShownIssues();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            GitHubBroker.getInstance().getAllIssues(issuesListener);
        }
        catch (GitHubBroker.RepositoryNotSelectedException e) {
            Log.wtf("debug", e.getClass().getName(), e);
        }
        catch (GitHubBroker.AlreadyNotConnectedException e) {
            Log.wtf("debug", e.getClass().getName(), e);
        }
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        try {
            GitHubBroker.getInstance().getAllIssues(issuesListener);
        }
        catch (GitHubBroker.RepositoryNotSelectedException e) {
            Log.wtf("debug", e.getClass().getName(), e);
        }
        catch (GitHubBroker.AlreadyNotConnectedException e) {
            if (GitHubBroker.getInstance().isConnected()) {
                updateShownIssues();
            }
        }
    }

    private synchronized void onIssuesReceived(Collection<GHIssue> issues) {
        if (getActivity() == null || listAdapter == null) {
            //If the device is rotated this is going to trigger, so return to end the refresh cycle
            return;
        }
        listAdapter.getIssueCollection().clear();
        listAdapter.getIssueCollection().addAll(issues);
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ListIssuesFragment.this.listAdapter.notifyDataSetChanged();
                issuesListView.invalidateViews();
            }
        });
        if (getView() != null) {
            ((DrawerLayoutFragmentActivity) getActivity()).onStopLoad();
        }
        updateShownIssues();
    }

    private final class IssuesListAdapter extends BaseAdapter {
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
            if (convertView == null) {
                convertView = ((LayoutInflater) getActivity().getApplicationContext()
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE))
                        .inflate(R.layout.list_item_issue, null);
                viewHolder = new ViewHolder();
                viewHolder.setTitleView((TextView) convertView.findViewById(R.id.title_issue));
                convertView.setTag(viewHolder);
            }
            else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            GHIssue issue = getItem(position);
            viewHolder.getTitleView().setText(issue.getTitle());
            return convertView;
        }

        private final class ViewHolder {
            private TextView titleView;

            public TextView getTitleView() {
                return titleView;
            }

            public void setTitleView(TextView titleView) {
                this.titleView = titleView;
            }
        }
    }

    private final class IssuesListener extends GitHubBrokerListener {
        @Override
        public void onAllIssuesRetrieved(boolean success, Collection<GHIssue> issues) {
            if (success) {
                onIssuesReceived(issues);
            }
            else {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getActivity().getApplicationContext(),
                                getResources().getString(R.string.error_retri_issues),
                                Toast.LENGTH_LONG).show();
                    }
                });
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View ret = inflater.inflate(R.layout.fragment_list_issues, container, Boolean.FALSE);

        issuesListView = (ListView) ret.findViewById(R.id.issue_list);
        listAdapter = new IssuesListAdapter();
        issuesListView.setAdapter(listAdapter);
        return ret;
    }

    private void updateShownIssues() {
        try {
            Thread.sleep(ISSUES_POLL_INTERVAL_MILLIS);
        }
        catch (InterruptedException e) {
            Log.wtf("debug", e.getClass().getName(), e);
        }
        try {
            GitHubBroker.getInstance().getAllIssues(issuesListener);
        }
        catch (GitHubBroker.RepositoryNotSelectedException e) {
            Log.wtf("debug", e.getClass().getName(), e);
        }
        catch (GitHubBroker.AlreadyNotConnectedException e) {
            if (GitHubBroker.getInstance().isConnected()) {
                updateShownIssues();
            }
        }
    }
}
