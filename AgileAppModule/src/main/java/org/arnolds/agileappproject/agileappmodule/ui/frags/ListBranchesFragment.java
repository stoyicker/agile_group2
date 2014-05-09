package org.arnolds.agileappproject.agileappmodule.ui.frags;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
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
import org.arnolds.agileappproject.agileappmodule.git.IGitHubBroker;
import org.arnolds.agileappproject.agileappmodule.git.IGitHubBrokerListener;
import org.arnolds.agileappproject.agileappmodule.ui.activities.DrawerLayoutFragmentActivity;
import org.kohsuke.github.GHBranch;

import java.io.IOException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

public class ListBranchesFragment extends ArnoldSupportFragment {

    private static final long BRANCHES_POLL_INTERVAL_MILLIS = 5000;
    private final static int MENU_INDEX = 1;
    private BranchesListAdapter listAdapter;
    private IGitHubBrokerListener branchesListener = new BranchesListener();
    private ListView branchesListView;
    private Integer posPicked = 0;

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
            final ViewHolder viewHolder;
            if (convertView == null) {
                convertView = ((LayoutInflater) getActivity().getApplicationContext()
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE))
                        .inflate(R.layout.list_item_branch, null);
                viewHolder = new ViewHolder();
                viewHolder.setNameView((TextView) convertView.findViewById(R.id.name_branch));
                viewHolder.setShaView((TextView) convertView.findViewById(R.id.sha_branch));
                viewHolder.setMessageView((TextView) convertView.findViewById(R.id.message_branch));
                convertView.setTag(viewHolder);
            }
            else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            final GHBranch branch = getItem(position);
            if (posPicked != position) {
                if (position % 2 == 0) {
                    convertView.findViewById(R.id.branch_fragment)
                            .setBackgroundColor(
                                    getResources().getColor(R.color.list_row_background1));
                }
                else {
                    convertView.findViewById(R.id.branch_fragment)
                            .setBackgroundColor(
                                    getResources().getColor(R.color.list_row_background2));
                }
            }
            else {
                TextView t = (TextView) getActivity().findViewById(R.id.selected_branch);
                t.setText("Working on origin/" + branch.getName().toString() + " branch");
                convertView.findViewById(R.id.branch_fragment)
                        .setBackgroundColor(getResources().getColor(R.color.orange));
            }


            viewHolder.getNameView().setText(branch.getName());
            viewHolder.getShaView().setText("Latest commit: " + branch.getSHA1());

            new AsyncTask<Void, Void, Void>() {
                @Override
                protected Void doInBackground(Void... params) {
                    try {
                        final String commitMessage =
                                branch.getOwner().getCommit(branch.getSHA1()).getCommitShortInfo()
                                        .getMessage();
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                viewHolder.getMessageView()
                                        .setText("Commit message: " + commitMessage);
                            }
                        });
                    }
                    catch (IOException e) {
                        e.printStackTrace();
                    }
                    return null;
                }
            }.execute();

            return convertView;
        }

        private final class ViewHolder {
            private TextView nameView, shaView, messageView;

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

            public TextView getMessageView() {
                return messageView;
            }

            public void setMessageView(TextView messageView) {
                this.messageView = messageView;
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
            IGitHubBroker broker = GitHubBroker.getInstance();
            GHBranch selectedBranch = listAdapter.getItem(position);
            broker.setSelectedBranch(selectedBranch);

            Context context = view.getContext();
            TextView t = (TextView) getActivity().findViewById(R.id.selected_branch);

            for (int chosen = 0; chosen < parent.getChildCount(); chosen++) {
                parent.getChildAt(chosen).setBackgroundColor(Color.TRANSPARENT);

            }
            view.setBackgroundColor(getResources().getColor(R.color.orange));
            posPicked = position;
            Toast.makeText(context, "Branch " +
                            listAdapter.getItem(position).getName().toString() + " selected",
                    Toast.LENGTH_SHORT
            ).show();


            t.setText(" Working on origin/" + selectedBranch.getName().toString() + " branch");
            Toast.makeText(context, selectedBranch.getName().toString() + " selected",
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
