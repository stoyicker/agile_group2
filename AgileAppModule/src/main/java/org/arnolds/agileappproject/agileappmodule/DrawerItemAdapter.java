package org.arnolds.agileappproject.agileappmodule;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class DrawerItemAdapter extends BaseAdapter {
    private List<DrawerItem> mDrawerItems;
    private Context mContext;

    public DrawerItemAdapter(Context context, ArrayList<DrawerItem> drawerItems) {
        mDrawerItems = drawerItems;
        mContext = context;
    }

    @Override
    public int getCount() {
        return mDrawerItems.size();
    }

    @Override
    public Object getItem(int position) {
        return mDrawerItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;

        //Check if there is view to convert, otherwise inflate from XML
        if(view == null) {
            LayoutInflater mLayoutInflater = (LayoutInflater) mContext.getSystemService
                    (Context.LAYOUT_INFLATER_SERVICE);
            view = mLayoutInflater.inflate(R.layout.drawer_item, parent, false);
        }

        //Get View-elements to update.
        ImageView imageView = (ImageView) view.findViewById(R.id.drawer_item_icon);
        TextView textView = (TextView) view.findViewById(R.id.drawer_item_title);

        //Set
        imageView.setImageResource(mDrawerItems.get(position).getIcon());
        textView.setText(mDrawerItems.get(position).getTitle());

        return view;
    }
}
