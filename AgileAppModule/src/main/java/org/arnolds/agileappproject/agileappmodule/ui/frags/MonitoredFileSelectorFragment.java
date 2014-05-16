package org.arnolds.agileappproject.agileappmodule.ui.frags;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import org.arnolds.agileappproject.agileappmodule.R;
import org.arnolds.agileappproject.agileappmodule.data.DataModel;
import org.arnolds.agileappproject.agileappmodule.git.GitHubBroker;
import org.arnolds.agileappproject.agileappmodule.git.IGitHubBroker;
import org.arnolds.agileappproject.agileappmodule.git.notifications.GitFile;
import org.arnolds.agileappproject.agileappmodule.git.notifications.GitFileTree;
import org.arnolds.agileappproject.agileappmodule.git.notifications.NotificationUtils;
import org.arnolds.agileappproject.agileappmodule.git.wrappers.GitBranch;
import org.arnolds.agileappproject.agileappmodule.git.wrappers.GitCommit;
import org.arnolds.agileappproject.agileappmodule.ui.activities.DrawerLayoutFragmentActivity;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class MonitoredFileSelectorFragment extends ArnoldSupportFragment
        implements PropertyChangeListener {
    private Context mContext;
    private FileListAdapter fileListAdapter;

    private static final String DEFAULT_LOCATION = "";
    private String currentLocation = DEFAULT_LOCATION;

    public final static int MENU_INDEX = 3;
    private FragmentActivity mActivity;
    private TextView pathTextView;

    private String repoName = "";

    private GitFileTree tree;

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
        pathTextView = (TextView) view.findViewById(R.id.filebrowser_path_text);
        pathTextView.setText(DEFAULT_LOCATION);
        ListView listView = (ListView) view.findViewById(R.id.listFiles);
        fileListAdapter = new FileListAdapter();
        listView.setAdapter(fileListAdapter);

        populateList();

        return view;
    }

    private void updatePathText() {
        if (currentLocation.equals("")) {
            pathTextView.setText("/");
        } else {
            pathTextView.setText(currentLocation);
        }
    }

    private void populateList() {

        IGitHubBroker broker = GitHubBroker.getInstance();
        String newRepoName = broker.getSelectedRepoName();

        // If repo has changed, set default path.
        if (repoName.equals("") || !repoName.equals(newRepoName)) {
            currentLocation = DEFAULT_LOCATION;
            repoName = newRepoName;

            GitBranch selectedBranch = broker.getSelectedBranch();
            Map<String, GitCommit> commits = broker.getCurrentCommitList();

            // TODO: Return List from getter method.
            List<GitFile> files = new ArrayList<GitFile>(NotificationUtils.filesOnBranch(selectedBranch, commits));

            tree = new GitFileTree(files);
        }


        // Update Path Text
        updatePathText();

        ArrayList<ListItem> list = new ArrayList<ListItem>();

        // Add up
        if(!currentLocation.equals("")){
            list.add(new ListItem(Type.UP, "..", null));
        }
        // Add directories
        for (String dirName : tree.getDirectories(currentLocation)) {
            list.add(new ListItem(Type.DIR, dirName, null));
        }
        // Add files
        for (GitFile file : tree.getFiles(currentLocation)) {
            list.add(new ListItem(Type.FILE, file.getName(), file));
        }

        fileListAdapter.setList(list);

        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                MonitoredFileSelectorFragment.this.fileListAdapter.notifyDataSetChanged();
            }
        });
    }

    public final class FileListAdapter extends BaseAdapter {
        private final List<ListItem> list = new ArrayList<ListItem>();

        public void clear() {
            list.clear();
        }

        public void setList(List<ListItem> list) {
            this.list.clear();
            this.list.addAll(list);
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
        public View getView(final int position, View convertView, ViewGroup parent) {
            final ViewHolder viewHolder;

            convertView = ((LayoutInflater) mContext
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE))
                    .inflate(R.layout.list_item_file, null);
            viewHolder = new ViewHolder();
            viewHolder.setImageView((ImageView) convertView.findViewById(R.id.file_icon));
            viewHolder.setTextView((TextView) convertView.findViewById(R.id.file_name));
            viewHolder.setCheckBox((CheckBox) convertView.findViewById(R.id.file_checkbox));

            final ListItem currentListItem = list.get(position);

            if (currentListItem.getType() == Type.DIR){
                viewHolder.getCheckBox().setVisibility(View.GONE);
                viewHolder.getImageView().setImageResource(R.drawable.folder_128);

                convertView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        currentLocation +="/"+currentListItem.getName();
                        populateList();
                    }
                });
            }
            else if(currentListItem.getType() == Type.UP){
                viewHolder.getCheckBox().setVisibility(View.GONE);
                viewHolder.getImageView().setImageResource(R.drawable.arrow_up_128);

                convertView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        currentLocation = currentLocation.substring(0, currentLocation.lastIndexOf('/'));
                        populateList();
                    }
                });
            }
            else {
                viewHolder.getImageView().setImageResource(R.drawable.blank_file_128);

                if(DataModel.getInstance().getAllMonitoredFiles().contains(currentListItem.getFile())){
                    viewHolder.getCheckBox().setChecked(true);
                }

                viewHolder.getCheckBox().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!viewHolder.getCheckBox().isChecked()) {
                            DataModel.getInstance().removeMonitoredFile(list.get(position).getFile());
                        } else {
                            DataModel.getInstance().addMonitoredFile(list.get(position).getFile());
                        }
                    }
                });
            }
            convertView.setTag(viewHolder);

            if (position % 2 == 0) {
                convertView.findViewById(R.id.list_item_file)
                        .setBackgroundColor(getResources().getColor(R.color.list_row_background1));
            } else {
                convertView.findViewById(R.id.list_item_file)
                        .setBackgroundColor(getResources().getColor(R.color.list_row_background2));
            }

            viewHolder.getTextView().setText(list.get(position).getName());

            return convertView;
        }

        private final class ViewHolder {
            private ImageView imageView;
            private TextView textView;
            private CheckBox checkBox;

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

            public CheckBox getCheckBox() {
                return checkBox;
            }

            public void setCheckBox(CheckBox checkBox) {
                this.checkBox = checkBox;
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
        UP, FILE
    }
}