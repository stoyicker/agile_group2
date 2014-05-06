package org.arnolds.agileappproject.agileappmodule.ui.frags;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.arnolds.agileappproject.agileappmodule.R;
import org.arnolds.agileappproject.agileappmodule.git.GitHubBroker;
import org.arnolds.agileappproject.agileappmodule.git.GitHubBrokerListener;
import org.arnolds.agileappproject.agileappmodule.git.IGitHubBrokerListener;
import org.arnolds.agileappproject.agileappmodule.ui.activities.DrawerLayoutFragmentActivity;
import org.kohsuke.github.GHIssue;

import java.io.IOException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

public class ListIssuesFragment extends ArnoldSupportFragment implements AdapterView.OnItemClickListener {

    private static final long ISSUES_POLL_INTERVAL_MILLIS = 2000;
    private final static int MENU_INDEX = 2;
    private IssuesListAdapter listAdapter;
    private IGitHubBrokerListener issuesListener = new IssuesListener();
    private ListView issuesListView;

    public ListIssuesFragment() {
        super(MENU_INDEX);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        listAdapter.getItem(i);
        IssuesListAdapter.ViewHolder vh = (IssuesListAdapter.ViewHolder) view.getTag();
        vh.toggleExpanded();
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
            final ViewHolder viewHolder;
            if (convertView == null) {
                convertView = ((LayoutInflater) getActivity().getApplicationContext()
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE))
                        .inflate(R.layout.list_item_issue, null);
                viewHolder = new ViewHolder();
                viewHolder.setTitleView((TextView) convertView.findViewById(R.id.title_issue));
                viewHolder.setCreatedView((TextView) convertView.findViewById(R.id.issue_created));
                viewHolder.setCreatorView((TextView) convertView.findViewById(R.id.issue_creator));
                viewHolder.setBodyView((TextView) convertView.findViewById(R.id.issue_body));
                viewHolder.setExpandPanel((RelativeLayout) convertView.findViewById(R.id.issue_expand_panel));
                viewHolder.setExpandIcon((ImageView) convertView.findViewById(R.id.issue_expander_icon));
                viewHolder.setAssigneeView((TextView) convertView.findViewById(R.id.issue_assignee));
                convertView.setTag(viewHolder);

                viewHolder.setExpanded(false);
            }
            else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            if (position % 2 == 0) {
                convertView.findViewById(R.id.issue_item_fragment)
                        .setBackgroundColor(getResources().getColor(R.color.list_row_background1));
            }
            else {
                convertView.findViewById(R.id.issue_item_fragment)
                        .setBackgroundColor(getResources().getColor(R.color.list_row_background2));
            }

            final GHIssue issue = getItem(position);

            String title = "#" + issue.getNumber() + ": " + issue.getTitle();
            String creator = "Created by: " + issue.getUser().getLogin();
            String date = "ERROR";
            if(issue.getClosedAt() != null){
                date = "Closed at: " + issue.getClosedAt().toString();
            } else if(issue.getUpdatedAt() != null){
                date = "Updated at: " + issue.getUpdatedAt().toString();
            } else if(issue.getCreatedAt() != null) {
                date = "Created at: " + issue.getCreatedAt().toString();
            }

            String body = issue.getBody();
            if(body.length() == 0){
                body = "<No message>";
            }
            viewHolder.getBodyView().setText(body);

            if(issue.getAssignee() == null){
                viewHolder.getAssigneeView().setVisibility(TextView.GONE);
            } else {
                viewHolder.getAssigneeView().setVisibility(TextView.VISIBLE);
                viewHolder.getAssigneeView().setText(issue.getAssignee().getLogin());
            }

            viewHolder.getTitleView().setText(title);
            viewHolder.getCreatorView().setText(creator);
            viewHolder.getCreatedView().setText(date);

            return convertView;
        }

        private final class ViewHolder {
            private TextView titleView;
            private TextView createdView;
            private TextView creatorView;
            private TextView assigneeView;
            private TextView bodyView;
            private RelativeLayout expandPanel;
            private ImageView expandIcon;

            private boolean expanded = false;

            public void setExpanded(boolean expanded){
                if(this.expandPanel == null){
                    throw new IllegalStateException("Expand panel not set");
                }
                if(this.expandIcon == null){
                    throw new IllegalStateException("Expand icon not set");
                }
                this.expanded = expanded;
                this.expandPanel.setVisibility(this.expanded ? RelativeLayout.VISIBLE : RelativeLayout.GONE);
                this.expandIcon.setImageResource(this.expanded ? R.drawable.expander_ic_maximized
                        : R.drawable.expander_ic_minimized);
            }

            public void toggleExpanded(){
                this.setExpanded(!this.expanded);
            }

            public TextView getBodyView() {
                return this.bodyView;
            }

            public TextView getTitleView() {
                return this.titleView;
            }

            public TextView getCreatedView() {
                return this.createdView;
            }

            public TextView getCreatorView() {
                return this.creatorView;
            }

            public TextView getAssigneeView() { return this.assigneeView; }

            public void setExpandIcon(ImageView expandIcon) { this.expandIcon = expandIcon; }

            public void setExpandPanel(RelativeLayout panel){
                this.expandPanel = panel;
            }

            public void setBodyView(TextView bodyView) { this.bodyView = bodyView; }

            public void setTitleView(TextView titleView) {
                this.titleView = titleView;
            }

            public void setCreatedView(TextView createdView) {
                this.createdView = createdView;
            }

            public void setAssigneeView(TextView assigneeView) {
                this.assigneeView = assigneeView;
            }

            public void setCreatorView(TextView creatorView) {
                this.creatorView = creatorView;
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
        issuesListView.setOnItemClickListener(this);
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
