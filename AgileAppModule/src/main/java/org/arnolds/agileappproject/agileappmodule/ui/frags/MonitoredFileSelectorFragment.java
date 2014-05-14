package org.arnolds.agileappproject.agileappmodule.ui.frags;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

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

        Pattern directoriesPattern = Pattern.compile(currentLocation+"(.+?)/");
        Pattern filesPattern = Pattern.compile(currentLocation+"(.+?)(?!/)");

        List<ListItem> currentDirectories = new ArrayList<ListItem>();
        List<ListItem> currentFiles = new ArrayList<ListItem>();

        for(GitFile file : files) {
            Matcher dirMatcher = directoriesPattern.matcher(file.getFileName());
            Matcher fileMatcher = filesPattern.matcher(file.getFileName());

            if(dirMatcher.matches()) {
                currentDirectories.add(new ListItem(Type.DIR, dirMatcher.group(1), file));
            } else if(fileMatcher.matches()) {
                currentFiles.add(new ListItem(Type.FILE, fileMatcher.group(1), file));
            }
        }

        // Add files at the bottom of the directories list and send everything to the adapter.
        currentDirectories.addAll(currentFiles);
        fileListAdapter.setList(currentDirectories);

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
        private List<ListItem> list;

        public void clear() {
            list.clear();
        }

        public void setList(List<ListItem> list) {
            list = new ArrayList<ListItem>(list);

        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public ListItem getItem(int position) {
            return list.get(position);
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
                viewHolder.setImageView((ImageView) convertView.findViewById(R.id.file_icon));
                viewHolder.setTextView((TextView) convertView.findViewById(R.id.file_name));
                convertView.setTag(viewHolder);
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

            viewHolder.getImageView().setImageResource(R.drawable.ic_arnold);
            viewHolder.getTextView().setText(list.get(position).getName());


            return convertView;
        }

        private final class ViewHolder {
            private ImageView imageView;
            private TextView textView;

            public TextView getTextView() {
                return textView;
            }

            public void setTextView(TextView textView) {
                this.textView = textView;
            }

            public ImageView getImageView() {
                return imageView;
            }

            public void setImageView(ImageView imageView) {
                this.imageView = imageView;
            }
        }
    }

    @Override
    public void propertyChange(PropertyChangeEvent event) {
        populateList();
        ((DrawerLayoutFragmentActivity) mActivity).onStopLoad();
    }

    private class ListItem {
        private Type type;
        private String name;
        private GitFile file;

        public ListItem(final Type type, final String name, final GitFile file) {
            this.type = type;
            this.name = name;
            this.file = file;
        }

        public Type getType() {
            return type;
        }

        public String getName() {
            return name;
        }

        public GitFile getFile() {
            return file;
        }
    }

    private static enum Type {
        DIR,
        FILE
    }
}