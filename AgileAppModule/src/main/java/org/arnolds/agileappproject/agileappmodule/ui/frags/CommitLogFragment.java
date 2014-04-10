package org.arnolds.agileappproject.agileappmodule.ui.frags;



import android.support.v4.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import org.arnolds.agileappproject.agileappmodule.R;
import org.arnolds.agileappproject.agileappmodule.git.notifications.GitHubNotificationService;
import org.kohsuke.github.GHCommit;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.LinkedList;
import java.util.List;

public class CommitLogFragment extends Fragment implements PropertyChangeListener {
    private List<GHCommit> mCommitList;
    private Context mContext;
    CommitAdapter commitAdapter;

    public CommitLogFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_commit_log, container, false);

        mContext = getActivity().getApplicationContext();
        ListView listView = (ListView) view.findViewById(R.id.commit_list_view);
        commitAdapter = new CommitAdapter();
        listView.setAdapter(commitAdapter);

        GitHubNotificationService service = GitHubNotificationService.getInstance();
        service.addCommitListener(this);

        Log.d("Commit fragment", "on create");

        return view;
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
                viewHolder.setCommentView((TextView) convertView.findViewById(R.id.commit_message));
                viewHolder.setCommitterView((TextView) convertView.findViewById(R.id.committer));
                convertView.setTag(viewHolder);
            }
            else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            GHCommit commit = getItem(position);
            viewHolder.getCommentView().setText(commit.getCommitShortInfo().getMessage());
            viewHolder.getCommitterView().setText(commit.getCommitShortInfo().getCommitter().getName());
            return convertView;
        }

        private final class ViewHolder {
            private TextView commentView, committerView;

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
        mCommitList = (List<GHCommit>) event.getNewValue();
        commitAdapter.getCommitCollection().clear();
        commitAdapter.getCommitCollection().addAll(mCommitList);
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                CommitLogFragment.this.commitAdapter.notifyDataSetChanged();
            }
        });


    }
}