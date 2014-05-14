package org.arnolds.agileappproject.agileappmodule.ui.frags;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;

import org.arnolds.agileappproject.agileappmodule.R;
import org.arnolds.agileappproject.agileappmodule.git.GitHubBroker;
import org.arnolds.agileappproject.agileappmodule.git.IGitHubBroker;
import org.arnolds.agileappproject.agileappmodule.git.notifications.GitFile;
import org.arnolds.agileappproject.agileappmodule.git.notifications.NotificationUtils;
import org.arnolds.agileappproject.agileappmodule.git.wrappers.GitBranch;
import org.arnolds.agileappproject.agileappmodule.git.wrappers.GitCommit;
import org.arnolds.agileappproject.agileappmodule.ui.activities.DrawerLayoutFragmentActivity;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MonitoredFileSelectorFragment extends ArnoldSupportFragment
        implements PropertyChangeListener {
    private Context mContext;
    private FileListAdapter fileListAdapter;

    private static final String DEFAULT_LOCATION = "/";
    private String currentLocation;

    public final static int MENU_INDEX = 3;
    private FragmentActivity mActivity;

    public MonitoredFileSelectorFragment() {
        super(MENU_INDEX);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_list_files, container, false);

        mActivity = getActivity();

        mContext = getActivity().getApplicationContext();
        ListView listView = (ListView) view.findViewById(R.id.listFiles);
        fileListAdapter = new FileListAdapter();
        listView.setAdapter(fileListAdapter);

        populateList();

        return view;
    }

    private void populateList() {
        IGitHubBroker broker = GitHubBroker.getInstance();
        GitBranch selectedBranch = broker.getSelectedBranch();
        Map<String, GitCommit> commits = broker.getCurrentCommitList();
        Set<GitFile> files = NotificationUtils.filesOnBranch(selectedBranch, commits);

        fileListAdapter.getFileList().clear();
        Pattern directoriesPattern = Pattern.compile(currentLocation+"(.+?)/");
        Pattern filesPattern = Pattern.compile(currentLocation+"(.+?)(?!/)");

        List<GitFile> currentFiles = new LinkedList<GitFile>();
        List<GitFile> currentFiles = new LinkedList<GitFile>();

        for(GitFile f : files){
            Matcher dirMatcher = directoriesPattern.matcher(f.getFileName());
            Matcher fileMatcher = filesPattern.matcher(f.getFileName());


            if(dirMatcher.matches()) {
                fileListAdapter.getFileList().add(f);
            } else if(fileMatcher.matches()) {

            }
        }

        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                MonitoredFileSelectorFragment.this.fileListAdapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public void onNewRepositorySelected() {
        populateList();
    }

    public final class FileListAdapter extends BaseAdapter {
        private final List<GitFile> fileList = new ArrayList<GitFile>();

        public List<GitFile> getFileList() {
            return fileList;
        }

        @Override
        public int getCount() {
            return fileList.size();
        }

        @Override
        public GitFile getItem(int position) {
            return fileList.get(position);
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