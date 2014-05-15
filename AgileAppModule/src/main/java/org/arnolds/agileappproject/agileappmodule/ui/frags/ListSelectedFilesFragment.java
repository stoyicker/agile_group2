package org.arnolds.agileappproject.agileappmodule.ui.frags;

import android.app.Activity;
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
import org.arnolds.agileappproject.agileappmodule.git.GitHubBroker;
import org.arnolds.agileappproject.agileappmodule.git.notifications.GitHubNotificationService;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class ListSelectedFilesFragment extends ArnoldSupportFragment implements PropertyChangeListener {

    private final static int MENU_INDEX = 4;
    private IssuesListAdapter listAdapter;
    private ListView fileListView;

    public ListSelectedFilesFragment() {
        super(MENU_INDEX);
    }

    private void populateList() {
        final List<String> list = new ArrayList<String>();
        list.add("/arnold/penis.txt");
        list.add("/arnold/kanban/lolface.extension");

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                listAdapter.getFileCollection().clear();
                listAdapter.getFileCollection().addAll(list);
                listAdapter.notifyDataSetChanged();
                fileListView.invalidateViews();
            }
        });
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void propertyChange(PropertyChangeEvent event) {
        populateList();
    }


    private final class IssuesListAdapter extends BaseAdapter {
        private final List<String> fileCollection = new LinkedList<String>();

        public List<String> getFileCollection() {
            return fileCollection;
        }

        @Override
        public int getCount() {
            return fileCollection.size();
        }

        @Override
        public String getItem(int position) {
            return fileCollection.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            final ViewHolder viewHolder;
            if (convertView == null) {
                convertView = ((LayoutInflater) getActivity().getApplicationContext()
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE))
                        .inflate(R.layout.list_item_selected_file, null);
                viewHolder = new ViewHolder();

                viewHolder.setNameView((TextView) convertView.findViewById(R.id.selected_file_name));
                viewHolder.setRemoveView((ImageView) convertView.findViewById(R.id.remove_watched_file));

                viewHolder.getRemoveView().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // TODO: Remove file from watch list and ui
                        Log.wtf("debug", "remove thingie" + position);
                        // TODO: Remove data set changed?
                        notifyDataSetChanged();
                    }
                });

                convertView.setTag(viewHolder);

                if (position % 2 == 0) {
                    convertView.findViewById(R.id.selected_file_item_fragment)
                            .setBackgroundColor(getResources().getColor(R.color.list_row_background1));
                }
                else {
                    convertView.findViewById(R.id.selected_file_item_fragment)
                            .setBackgroundColor(getResources().getColor(R.color.list_row_background2));
                }

                final String name = getItem(position);
                viewHolder.getNameView().setText(name);
            }
            else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            return convertView;
        }

        private final class ViewHolder {
            private TextView nameView;

            private ImageView removeView;

            public ImageView getRemoveView() {return this.removeView; }

            public TextView getNameView() { return this.nameView; }

            public void setRemoveView(ImageView removeView){
                this.removeView = removeView;
            }

            public void setNameView(TextView nameView) {
                this.nameView = nameView;
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View ret = inflater.inflate(R.layout.fragment_list_selected_files, container, Boolean.FALSE);
        fileListView = (ListView) ret.findViewById(R.id.selected_file_list);
        listAdapter = new IssuesListAdapter();
        fileListView.setAdapter(listAdapter);

        populateList();

        return ret;
    }

}
