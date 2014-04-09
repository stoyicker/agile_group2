package org.arnolds.agileappproject.agileappmodule;


import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.arnolds.agileappproject.agileappmodule.git.GitHubBroker;
import org.arnolds.agileappproject.agileappmodule.git.GitHubBrokerListener;
import org.arnolds.agileappproject.agileappmodule.git.IGitHubBrokerListener;
import org.kohsuke.github.GHBranch;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

public class RetrieveBranchesActivity extends Activity {

    private static final long BRANCHES_POLL_INTERVAL_MILLIS = 30000;
    private BranchesListAdapter listAdapter;
    private final IGitHubBrokerListener branchesListener = new BranchesListener();

    @Override
    public View onCreateView(View parent, String name, Context context, AttributeSet attrs) {
        View ret = super.onCreateView(parent, name, context, attrs);


        return ret;
    }

    private void onBranchesReceived(Collection<GHBranch> branches) {

        listAdapter.getBranchCollection().clear();
        listAdapter.getBranchCollection().addAll(branches);
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                RetrieveBranchesActivity.this.listAdapter.notifyDataSetChanged();
            }
        });
        updateShownBranches();
    }

    private final class BranchesListener extends GitHubBrokerListener {
        @Override
        public void onAllBranchesRetrieved(boolean success, Collection<GHBranch> branches) {
            if (success) {
                onBranchesReceived(branches);
            }
            else {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(),
                                getResources().getString(R.string.error_retri_branches),
                                Toast.LENGTH_LONG);
                    }
                });
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_retrieve_branches);

        ListView listView = (ListView) findViewById(R.id.branch_list);
        listAdapter = new BranchesListAdapter();
        listView.setAdapter(listAdapter);

        try {
            GitHubBroker.getInstance().getAllBranches(branchesListener);
        }
        catch (GitHubBroker.RepositoryNotSelectedException e) {
            Log.wtf("debug", e.getClass().getName(), e);
        }
        catch (GitHubBroker.AlreadyNotConnectedException e) {
            Log.wtf("debug", e.getClass().getName(), e);
        }

    }

    private void updateShownBranches() {
        try {
            Thread.sleep(BRANCHES_POLL_INTERVAL_MILLIS);
        }
        catch (InterruptedException e) {
            Log.wtf("debug", e.getClass().getName(), e);
        }
        try {
            GitHubBroker.getInstance().getAllBranches(branchesListener);
        }
        catch (GitHubBroker.RepositoryNotSelectedException e) {
            Log.wtf("debug", e.getClass().getName(), e);
        }
        catch (GitHubBroker.AlreadyNotConnectedException e) {
            if (GitHubBroker.getInstance().isConnected()) {
                updateShownBranches();
            }
        }
    }
    
    public final class BranchesListAdapter extends BaseAdapter {
        private final List<GHBranch> branchCollection = new LinkedList<GHBranch>();

        public List<GHBranch> getBranchCollection() {
            return branchCollection;
        }

        @Override
        public int getCount() {
            return branchCollection.size();
        }

        @Override
        public GHBranch getItem(int position) {
            return branchCollection.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            if (convertView == null) {
                convertView = ((LayoutInflater) getApplicationContext()
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE))
                        .inflate(R.layout.list_item_navigation_drawer_list, null);
                viewHolder = new ViewHolder();
                viewHolder.setNameView((TextView) convertView.findViewById(R.id.name_branch));
                viewHolder.setShaView((TextView) convertView.findViewById(R.id.sha_branch));
                convertView.setTag(viewHolder);
            }
            else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            GHBranch branch = getItem(position);
            viewHolder.getNameView().setText(branch.getName());
            viewHolder.getShaView().setText(branch.getSHA1());
            return convertView;
        }

        private final class ViewHolder {
            private TextView nameView, shaView;

            public TextView getNameView() {
                return nameView;
            }

            public void setNameView(TextView nameView) {
                this.nameView = nameView;
            }

            public TextView getShaView() {
                return shaView;
            }

            public void setShaView(TextView shaView) {
                this.shaView = shaView;
            }
        }
    }
}