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

import org.arnolds.agileappproject.agileappmodule.R;
import org.arnolds.agileappproject.agileappmodule.git.GitHubBroker;
import org.arnolds.agileappproject.agileappmodule.git.notifications.GitHubNotificationService;
import org.arnolds.agileappproject.agileappmodule.git.wrappers.GitIssue;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.LinkedList;
import java.util.List;

public class ListIssuesFragment extends ArnoldSupportFragment implements AdapterView.OnItemClickListener, PropertyChangeListener {

    private final static int MENU_INDEX = 2;
    private IssuesListAdapter listAdapter;
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

    private void populateList() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                listAdapter.getIssueCollection().clear();
                listAdapter.getIssueCollection().addAll(GitHubBroker.getInstance().getCurrentIssues());
                listAdapter.notifyDataSetChanged();
                issuesListView.invalidateViews();
            }
        });
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void propertyChange(PropertyChangeEvent event) {
        populateList();
    }


    private final class IssuesListAdapter extends BaseAdapter {
        private final List<GitIssue> issueCollection = new LinkedList<GitIssue>();

        public List<GitIssue> getIssueCollection() {
            return issueCollection;
        }

        @Override
        public int getCount() {
            return issueCollection.size();
        }

        @Override
        public GitIssue getItem(int position) {
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

            final GitIssue issue = getItem(position);

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


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View ret = inflater.inflate(R.layout.fragment_list_issues, container, Boolean.FALSE);

        issuesListView = (ListView) ret.findViewById(R.id.issue_list);
        listAdapter = new IssuesListAdapter();
        issuesListView.setAdapter(listAdapter);
        issuesListView.setOnItemClickListener(this);

        populateList();

        GitHubNotificationService.getInstance().addIssueListener(this);
        return ret;
    }

}
