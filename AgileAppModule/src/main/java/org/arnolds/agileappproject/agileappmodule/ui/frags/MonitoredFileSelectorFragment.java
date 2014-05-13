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
import org.arnolds.agileappproject.agileappmodule.git.GitHubBroker;
import org.arnolds.agileappproject.agileappmodule.git.IGitHubBroker;
import org.arnolds.agileappproject.agileappmodule.git.notifications.GitHubNotificationService;
import org.arnolds.agileappproject.agileappmodule.git.wrappers.GitCommit;
import org.arnolds.agileappproject.agileappmodule.ui.activities.DrawerLayoutFragmentActivity;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;

public class MonitoredFileSelectorFragment extends ArnoldSupportFragment
        implements PropertyChangeListener {
    private Context mContext;
    private CommitAdapter commitAdapter;

    public final static int MENU_INDEX = 3;
    private FragmentActivity mActivity;

    public MonitoredFileSelectorFragment() {
        super(MENU_INDEX);
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

        populateList();
        service.addCommitListener(this);

        return view;
    }

    private void populateList() {
        IGitHubBroker broker = GitHubBroker.getInstance();

        commitAdapter.getCommitCollection().clear();
        commitAdapter.getCommitCollection().addAll(broker.getCommitsFromSelectedBranch());
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                MonitoredFileSelectorFragment.this.commitAdapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public void onNewRepositorySelected() {
        populateList();
    }

    public final class CommitAdapter extends BaseAdapter {
        private final List<GitCommit> commitCollection = new ArrayList<GitCommit>();

        public List<GitCommit> getCommitCollection() {
            return commitCollection;
        }

        @Override
        public int getCount() {
            return commitCollection.size();
        }

        @Override
        public GitCommit getItem(int position) {
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

            final GitCommit commit = getItem(position);
            return convertView;
        }

        private final class ViewHolder {

        }
    }

    @Override
    public void propertyChange(PropertyChangeEvent event) {
        populateList();
        ((DrawerLayoutFragmentActivity) mActivity).onStopLoad();
    }
}