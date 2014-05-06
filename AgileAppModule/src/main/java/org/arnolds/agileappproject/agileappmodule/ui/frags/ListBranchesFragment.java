package org.arnolds.agileappproject.agileappmodule.ui.frags;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.arnolds.agileappproject.agileappmodule.R;
import org.arnolds.agileappproject.agileappmodule.git.GitHubBroker;
import org.arnolds.agileappproject.agileappmodule.git.GitHubBrokerListener;
import org.arnolds.agileappproject.agileappmodule.git.IGitHubBrokerListener;
import org.arnolds.agileappproject.agileappmodule.ui.activities.DrawerLayoutFragmentActivity;
import org.kohsuke.github.GHBranch;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

public class ListBranchesFragment extends ArnoldSupportFragment {

    private static final long BRANCHES_POLL_INTERVAL_MILLIS = 2000;
    private final static int MENU_INDEX = 1;
    private BranchesListAdapter listAdapter;
    private IGitHubBrokerListener branchesListener = new BranchesListener();
    private ListView branchesListView;
    private Collection<GHBranch> branches1;

    public ListBranchesFragment() {
        super(MENU_INDEX);
    }

    @Override
    public void onNewRepositorySelected() {
        updateShownBranches();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
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

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

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

    private synchronized void onBranchesReceived(Collection<GHBranch> branches) {
        branches1 = branches;
        if (getActivity() == null || listAdapter == null) {
            //If the device is rotated this is going to trigger, so return to end the refresh cycle
            return;
        }
        listAdapter.getBranchCollection().clear();
        listAdapter.getBranchCollection().addAll(branches);


        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ListBranchesFragment.this.listAdapter.notifyDataSetChanged();
                branchesListView.invalidateViews();
            }
        });
        if (getView() != null) {
            ((DrawerLayoutFragmentActivity) getActivity()).onStopLoad();
        }
        updateShownBranches();
    }

    private final class BranchesListAdapter extends BaseAdapter {
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
                convertView = ((LayoutInflater) getActivity().getApplicationContext()
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE))
                        .inflate(R.layout.list_item_branch, null);
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

    private final class BranchesListener extends GitHubBrokerListener {
        @Override
        public void onAllBranchesRetrieved(boolean success, Collection<GHBranch> branches) {
            if (success) {
                onBranchesReceived(branches);
            }
            else {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getActivity().getApplicationContext(),
                                getResources().getString(R.string.error_retri_branches),
                                Toast.LENGTH_LONG).show();
                    }
                });
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View ret = inflater.inflate(R.layout.fragment_list_branches, container, Boolean.FALSE);

        branchesListView = (ListView) ret.findViewById(R.id.branch_list);
        listAdapter = new BranchesListAdapter();
        branchesListView.setAdapter(listAdapter);
        branchesListView.setOnItemClickListener(new OnItemClickListenerListViewItem());
        return ret;
    }

    public class OnItemClickListenerListViewItem implements AdapterView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            Context context = view.getContext();
            TextView t = (TextView) getActivity().findViewById(R.id.selected_branch);
            t.setText(" Working on " + listAdapter.getItem(position).getName().toString() +
                    " branch");
            for (int j = 0; j < parent.getChildCount(); j++)
                parent.getChildAt(j).setBackgroundColor(Color.TRANSPARENT);

            // change the background color of the selected element
            view.setBackgroundColor(Color.CYAN);
            Toast.makeText(context,
                    listAdapter.getItem(position).getName().toString() + " selected",
                    Toast.LENGTH_SHORT).show();

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
}
