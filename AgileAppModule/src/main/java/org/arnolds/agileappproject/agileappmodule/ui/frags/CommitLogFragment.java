package org.arnolds.agileappproject.agileappmodule.ui.frags;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import org.arnolds.agileappproject.agileappmodule.R;
import org.arnolds.agileappproject.agileappmodule.git.notifications.GitHubNotificationService;
import org.arnolds.agileappproject.agileappmodule.ui.activities.DrawerLayoutFragmentActivity;
import org.kohsuke.github.GHCommit;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;

public class CommitLogFragment extends ArnoldSupportFragment
        implements PropertyChangeListener, AdapterView.OnItemClickListener {
    private Context mContext;
    CommitAdapter commitAdapter;

    public final static int DRAWER_POSITION = 0;
    private FragmentActivity mActivity;

    public CommitLogFragment() {
        super(DRAWER_POSITION);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        commitAdapter.getItem(i);
        CommitAdapter.ViewHolder vh = (CommitAdapter.ViewHolder) view.getTag();
        vh.toggleExpanded();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_commit_log, container, false);

        mActivity = getActivity();

        mContext = getActivity().getApplicationContext();
        ListView listView = (ListView) view.findViewById(R.id.commit_list_view);
        commitAdapter = new CommitAdapter();
        listView.setAdapter(commitAdapter);

        GitHubNotificationService service = GitHubNotificationService.getInstance();
        populateList(service.getCurrentCommitList());
        service.addCommitListener(this);

        listView.setOnItemClickListener(this);

        return view;
    }

    private void populateList(List<GHCommit> commitList) {
        commitAdapter.getCommitCollection().clear();
        commitAdapter.getCommitCollection().addAll(commitList);
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                CommitLogFragment.this.commitAdapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public void onNewRepositorySelected() {
    }

    public final class CommitAdapter extends BaseAdapter {
        private final List<GHCommit> commitCollection = new LinkedList<GHCommit>();

        public List<GHCommit> getCommitCollection() {
            return commitCollection;
        }

        @Override
        public int getCount() {
            return commitCollection.size();
        }

        @Override
        public GHCommit getItem(int position) {
            return commitCollection.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final ViewHolder viewHolder;
            if (convertView == null) {
                convertView = ((LayoutInflater) mContext
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE))
                        .inflate(R.layout.list_item_commit, null);
                viewHolder = new ViewHolder();
                viewHolder.setCommentView((TextView) convertView.findViewById(R.id.commit_message));
                viewHolder.setCommitterView((TextView) convertView.findViewById(R.id.committer));
                viewHolder.setExpandIconImageView(
                        (ImageView) convertView.findViewById(R.id.commit_expander_icon));
                convertView.setTag(viewHolder);
                // viewHolder.setExpanded(false);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            if (position % 2 == 0) {
                convertView.findViewById(R.id.commit_fragment)
                        .setBackgroundColor(getResources().getColor(R.color.list_row_background1));
            } else {
                convertView.findViewById(R.id.commit_fragment)
                        .setBackgroundColor(getResources().getColor(R.color.list_row_background2));
            }

            final GHCommit commit = getItem(position);

            // Set commit and commenter texts
            viewHolder.getCommentView().setText(commit.getCommitShortInfo().getMessage());
            viewHolder.getCommitterView()
                    .setText(commit.getCommitShortInfo().getCommitter().getName());

            // All expand-buttons are hidden by default
            viewHolder.setExpandIconVisible(false);
            // Logic to show expand-button if there is something to expand
            viewHolder.getCommentView().post(new Runnable() {
                @Override
                public void run() {
                    // Guard makes sure we do not update GUI stuff unnecessarily
                    if (viewHolder.getCommentView().getLineCount() > 1) {
                        viewHolder.setExpandIconVisible(true);
                    }
                }
            });
            // Per default, all comments are collapsed
            viewHolder.setExpanded(false);
            return convertView;
        }

        private final class ViewHolder {
            private TextView commentView, committerView;
            private ImageView expandIconImageView;

            private boolean expanded = true;

            public void setExpandIconVisible(boolean visible) {
                expandIconImageView.setVisibility(visible ? ImageView.VISIBLE : ImageView.GONE);
            }

            public void toggleExpanded() {
                setExpanded(!expanded);
            }

            public void setExpanded(boolean value) {
                expanded = value;
                if (expanded) {
                    commentView.setMaxLines(Integer.MAX_VALUE);
                    expandIconImageView.setImageResource(R.drawable.expander_ic_maximized);
                } else {
                    commentView.setMaxLines(1);
                    expandIconImageView.setImageResource(R.drawable.expander_ic_minimized);
                }
            }

            public void setExpandIconImageView(ImageView expandIconImageView) {
                this.expandIconImageView = expandIconImageView;
            }

            public TextView getCommentView() {
                return commentView;
            }

            public void setCommentView(TextView commentView) {
                this.commentView = commentView;
            }

            public TextView getCommitterView() {
                return committerView;
            }

            public void setCommitterView(TextView committerView) {
                this.committerView = committerView;
            }
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

//        new Exception().printStackTrace(System.err);
    }

    @Override
    public void propertyChange(PropertyChangeEvent event) {

        LinkedHashMap<String, GHCommit> commits = (LinkedHashMap<String, GHCommit>) event.getNewValue();
        populateList(new ArrayList<GHCommit>(commits.values()));
        if (event.getNewValue() != event.getOldValue()) {
            try {
                ((DrawerLayoutFragmentActivity) mActivity).onStopLoad();
            } catch (IllegalStateException e) {

            }
        }
    }

}