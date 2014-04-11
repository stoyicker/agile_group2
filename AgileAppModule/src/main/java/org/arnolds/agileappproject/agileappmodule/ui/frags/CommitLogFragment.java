package org.arnolds.agileappproject.agileappmodule.ui.frags;

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
import android.widget.TextView;

import org.arnolds.agileappproject.agileappmodule.R;
import org.arnolds.agileappproject.agileappmodule.git.notifications.GitHubNotificationService;
import org.kohsuke.github.GHCommit;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.LinkedList;
import java.util.List;

public class CommitLogFragment extends ArnoldSupportFragment
        implements PropertyChangeListener, AdapterView.OnItemClickListener {
    private Context mContext;
    CommitAdapter commitAdapter;

    public final static int DRAWER_POSITION = 0;

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

        if (!GitHubNotificationService.getInstance().isEmpty()) {
            view.findViewById(R.id.commit_list_empty).setVisibility(View.INVISIBLE);
        }

        mContext = getActivity().getApplicationContext();
        ListView listView = (ListView) view.findViewById(R.id.commit_list_view);
        commitAdapter = new CommitAdapter();
        listView.setAdapter(commitAdapter);

        GitHubNotificationService service = GitHubNotificationService.getInstance();
        populateList(service.getCurrentCommitList());
        service.addCommitListener(this);

        Log.d("Commit fragment", "on create");

        listView.setOnItemClickListener(this);

        return view;
    }

    private void populateList(List<GHCommit> commitList) {
        commitAdapter.getCommitCollection().clear();
        commitAdapter.getCommitCollection().addAll(commitList);
        getActivity().runOnUiThread(new Runnable() {
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
            ViewHolder viewHolder;
            if (convertView == null) {
                convertView = ((LayoutInflater) mContext
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE))
                        .inflate(R.layout.list_item_commit, null);
                viewHolder = new ViewHolder();
                viewHolder.setExpandIconImageView(
                        (ImageView) convertView.findViewById(R.id.commit_expander_icon));
                viewHolder.setCommentView((TextView) convertView.findViewById(R.id.commit_message));
                viewHolder.setCommitterView((TextView) convertView.findViewById(R.id.committer));
                convertView.setTag(viewHolder);
                viewHolder.setExpanded(false);
            }
            else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            if (position % 2 == 0) {
                convertView.findViewById(R.id.commit_fragment)
                        .setBackgroundColor(getResources().getColor(R.color.list_row_background1));
            }
            else {
                convertView.findViewById(R.id.commit_fragment)
                        .setBackgroundColor(getResources().getColor(R.color.list_row_background2));
            }

            GHCommit commit = getItem(position);
            viewHolder.getCommentView().setText(commit.getCommitShortInfo().getMessage());
            viewHolder.getCommitterView()
                    .setText(commit.getCommitShortInfo().getCommitter().getName());

            return convertView;
        }

        private final class ViewHolder {
            private TextView commentView, committerView;
            private ImageView expandIconImageView;

            private boolean expanded = true;

            public void toggleExpanded() {
                setExpanded(!expanded);
            }

            public void setExpanded(boolean value) {
                expanded = value;
                if (expanded) {
                    commentView.setMaxLines(Integer.MAX_VALUE);
                    expandIconImageView.setImageResource(R.drawable.expander_ic_maximized);
                }
                else {
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
    public void propertyChange(PropertyChangeEvent event) {
        if (getView() != null) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    getView().findViewById(R.id.commit_list_empty).setVisibility(View.INVISIBLE);
                }
            });
        }
        populateList((List<GHCommit>) event.getNewValue());
    }

}