package org.arnolds.agileappproject.agileappmodule.ui.frags;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import org.arnolds.agileappproject.agileappmodule.R;
import org.arnolds.agileappproject.agileappmodule.ui.activities.DrawerLayoutFragmentActivity;
import org.arnolds.agileappproject.agileappmodule.utils.AgileAppModuleUtils;

public class NavigationDrawerListFragment extends Fragment {

    private NavigationDrawerCallbacks mCallbacks;

    private ActionBarDrawerToggle mDrawerToggle;

    private DrawerLayout mDrawerLayout;
    private ListView mDrawerListView;
    private View mFragmentContainerView;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // Indicate that this fragment would like to influence the set of actions in the action bar.
        setHasOptionsMenu(Boolean.TRUE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mDrawerListView = (ListView) inflater.inflate(R.layout.navigation_drawer_list, container,
                false);

        mDrawerListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectItem(position);
            }
        });
        mDrawerListView
                .setAdapter(new NavigationDrawerArrayAdapter());

        return mDrawerListView;
    }

    public boolean isDrawerOpen() {
        return mDrawerLayout != null && mDrawerLayout.isDrawerOpen(mFragmentContainerView);
    }

    /**
     * Users of this fragment must call this method to set up the navigation drawer interactions.
     *
     * @param fragmentId   The android:id of this fragment in its activity's layout.
     * @param drawerLayout The DrawerLayout containing this fragment's UI.
     */
    public void setUp(int fragmentId, DrawerLayout drawerLayout) {
        mFragmentContainerView = getActivity().findViewById(fragmentId);
        mDrawerLayout = drawerLayout;

        // set a custom shadow that overlays the main content when the drawer opens
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        // set up the drawer's list view with items and click listener

        ActionBar actionBar = getActionBar();
        actionBar.setHomeButtonEnabled(Boolean.TRUE);
        actionBar.setDisplayHomeAsUpEnabled(Boolean.TRUE);

        // ActionBarDrawerToggle ties together the the proper interactions
        // between the navigation drawer and the action bar app icon.
        mDrawerToggle = new ActionBarDrawerToggle(
                getActivity(),                    /* host Activity */
                mDrawerLayout,                    /* DrawerLayout object */
                R.drawable.ic_drawer,             /* nav drawer image to replace 'Up' caret */
                R.string.navigation_drawer_open,  /* "open drawer" description for accessibility */
                R.string.navigation_drawer_close  /* "close drawer" description for accessibility */
        ) {
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                if (!isAdded()) {
                    return;
                }

                restoreActionBar();
                getActivity().invalidateOptionsMenu();
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                showGlobalContextActionBar();
            }
        };

        // Defer code dependent on restoration of previous instance state.
        mDrawerLayout.post(new Runnable() {
            @Override
            public void run() {
                mDrawerToggle.syncState();
            }
        });

        mDrawerLayout.setDrawerListener(mDrawerToggle);
    }

    private void restoreActionBar() {
        ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(Boolean.TRUE);
        actionBar.setTitle(
                AgileAppModuleUtils.getString(getActivity(), "title_section" + (
                                DrawerLayoutFragmentActivity.getLastSelectedNavDavIndex() + 1),
                        "Home"
                )
        );
    }

    private void selectItem(int position) {
        if (mDrawerListView != null) {
            mDrawerListView.setItemChecked(position, true);
        }
        if (mDrawerLayout != null) {
            mDrawerLayout.closeDrawer(mFragmentContainerView);
        }
        if (mCallbacks != null) {
            mCallbacks.onNavigationDrawerItemSelected(position);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mCallbacks = (NavigationDrawerCallbacks) activity;
        }
        catch (ClassCastException e) {
            throw new ClassCastException("Activity must implement NavigationDrawerCallbacks.");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Forward the new configuration the drawer toggle component.
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.standard, menu);
        if (mDrawerLayout != null) {
            if (isDrawerOpen()) {
                showGlobalContextActionBar();
            }
        }
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return mDrawerToggle.onOptionsItemSelected(item) || super.onOptionsItemSelected(item);
    }

    /**
     * Per the navigation drawer design guidelines, updates the action bar to show the actionbar app
     * 'context', rather than just what's in the current screen.
     */
    private void showGlobalContextActionBar() {
        ActionBar actionBar = getActionBar();
        actionBar.setDisplayShowTitleEnabled(Boolean.TRUE);
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setTitle(R.string.app_name);
    }

    private ActionBar getActionBar() {
        return getActivity().getActionBar();
    }

    /**
     * Callbacks interface that all activities using this fragment must implement.
     */
    public static interface NavigationDrawerCallbacks {
        /**
         * Called when an item in the navigation drawer is selected.
         */
        void onNavigationDrawerItemSelected(int position);
    }

    private class NavigationDrawerArrayAdapter extends BaseAdapter {

        private final int mResource = R.layout.list_item_navigation_drawer;

        @Override
        public int getCount() {
            return getActivity().getApplicationContext().getResources()
                    .getStringArray(R.array.navigation_drawer_items).length;
        }

        @Override
        public Object getItem(int i) {
            int temp = i + 1;
            return AgileAppModuleUtils
                    .getString(getActivity().getApplicationContext(), "title_section" + temp, "");
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            if (convertView == null) {
                convertView =
                        ((LayoutInflater) getActivity().getApplicationContext().getSystemService(
                                Context.LAYOUT_INFLATER_SERVICE)).inflate(mResource, parent, false);
                viewHolder = new ViewHolder();
                viewHolder.setTextView((TextView) convertView.findViewById(R.id.drawer_item_title));
                viewHolder
                        .setImageView((ImageView) convertView.findViewById(R.id.drawer_item_icon));
            }
            else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            int temp = position + 1;

            viewHolder.getTextView().setText(
                    AgileAppModuleUtils.getString(getActivity().getApplicationContext(),
                            "title_section" + +temp, "")
            );
            viewHolder.getImageView().setImageResource(
                    AgileAppModuleUtils.getDrawableAsId("icon_section" + temp, -1));

            return convertView;
        }

        private class ViewHolder {
            private TextView textView;
            private ImageView imageView;

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
}
