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
import org.arnolds.agileappproject.agileappmodule.git.IGitHubBroker;
import org.arnolds.agileappproject.agileappmodule.git.notifications.GitHubNotificationService;
import org.arnolds.agileappproject.agileappmodule.git.wrappers.GitBranch;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ListBranchesFragment extends ArnoldSupportFragment implements PropertyChangeListener {

    private final static int MENU_INDEX = 1;
    private BranchesListAdapter listAdapter;

    private ListView branchesListView;

    private Integer posPicked = 0;

    private Map<String, GitBranch> branches;


    public ListBranchesFragment() {
        super(MENU_INDEX);
    }

    @Override
    public void onNewRepositorySelected() {
        populateList();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        branches = GitHubBroker.getInstance().getAllBranches();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }


    @Override
    public void propertyChange(PropertyChangeEvent event) {
        populateList();
    }


    private void populateList() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                listAdapter.getBranchCollection().clear();
                listAdapter.getBranchCollection().addAll(GitHubBroker.getInstance().getAllBranches().values());
                listAdapter.notifyDataSetChanged();

                Log.wtf("BLAH",GitHubBroker.getInstance().getAllBranches().values().size()+"");
            }
        });
    }


    private final class BranchesListAdapter extends BaseAdapter {
        private final List<GitBranch> branchCollection = new ArrayList<GitBranch>();

        public List<GitBranch> getBranchCollection() {
            return branchCollection;
        }


        @Override
        public int getCount() {
            return branchCollection.size();
        }

        @Override
        public GitBranch getItem(int position) {
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

            final GitBranch branch = getItem(position);
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
            viewHolder.getMessageView().setText("Commit message: " + branch.getCommit().getMessage());

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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View ret = inflater.inflate(R.layout.fragment_list_branches, container, Boolean.FALSE);

        GitHubNotificationService.getInstance().addCommitListener(this);

        branchesListView = (ListView) ret.findViewById(R.id.branch_list);
        listAdapter = new BranchesListAdapter();
        branchesListView.setAdapter(listAdapter);
        branchesListView.setOnItemClickListener(new OnItemClickListenerListViewItem());

        populateList();

        return ret;
    }

    public class OnItemClickListenerListViewItem implements AdapterView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            IGitHubBroker broker = GitHubBroker.getInstance();
            GitBranch selectedBranch = listAdapter.getItem(position);
            broker.setSelectedBranch(selectedBranch.getName());

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
}
