package org.arnolds.agileappproject.agileappmodule.ui.frags;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import org.arnolds.agileappproject.agileappmodule.R;
import org.arnolds.agileappproject.agileappmodule.git.GitHubBroker;
import org.arnolds.agileappproject.agileappmodule.git.GitHubBrokerListener;
import org.arnolds.agileappproject.agileappmodule.ui.activities.DrawerLayoutFragmentActivity;
import org.arnolds.agileappproject.agileappmodule.utils.AgileAppModuleUtils;
import org.kohsuke.github.GHRepository;

import java.util.ArrayList;
import java.util.List;

public class NavigationDrawerFragment extends Fragment {

    private static final long REPOS_POLL_RATE_SECONDS = Long.MAX_VALUE;
    private static int LAST_SELECTED_ITEM_INDEX = 0;
    private NavigationDrawerCallbacks mCallbacks;

    private ActionBarDrawerToggle mDrawerToggle;

    private DrawerLayout mDrawerLayout;
    private ListView mDrawerListView;
    private View mFragmentContainerView;
    private Spinner mRepoSelectionSpinner;
    private String latestSelectedRepoName = "";
    private final SelectionListener selectionListener = new SelectionListener();

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // Indicate that this fragment would like to influence the set of actions in the action bar.
        setHasOptionsMenu(Boolean.TRUE);
    }

    public void invalidateListView() {
        mDrawerListView.invalidateViews();
    }

    private class SelectionListener extends GitHubBrokerListener {
        @Override
        public void onRepoSelected(boolean result) {
            Log.wtf("BLAH", "----- STOP LOAD ----");
            mCallbacks.onStopLoad();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View ret = inflater.inflate(
                R.layout.fragment_navigation_drawer_layout, container, false);
        mDrawerListView = (ListView) ret.findViewById(R.id.navigation_drawer_list_view);

        mRepoSelectionSpinner = (Spinner) ret.findViewById(R.id.repo_selector_view);

        mRepoSelectionSpinner.setBackgroundColor(getResources().getColor(R.color.theme_white));



        final List<String> allRepositories = new ArrayList<String>();
        for (GHRepository repository : GitHubBroker.getInstance().getCurrentRepositories().values())
            allRepositories.add(repository.getName());

        final ArrayAdapter<String> adapter =
                new ArrayAdapter<String>(
                        getActivity().getApplicationContext(),
                        R.layout.repo_selector_spinner_selected_item,
                        allRepositories);
        adapter.setDropDownViewResource(
                R.layout.repo_selector_dropdown_item);
        final String newSelectedRepoName =
                mRepoSelectionSpinner.getSelectedItem() == null ? "" :
                        mRepoSelectionSpinner.getSelectedItem().toString();

        mRepoSelectionSpinner.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        if (newSelectedRepoName.isEmpty()) {
            mRepoSelectionSpinner
                    .setSelection(LAST_SELECTED_ITEM_INDEX, false);
        }

        mRepoSelectionSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                LAST_SELECTED_ITEM_INDEX = position;
                String repoName = mRepoSelectionSpinner.getItemAtPosition(position).toString();
                if (!repoName.isEmpty() && !repoName.contentEquals(latestSelectedRepoName)) {
                    NavigationDrawerFragment.this.latestSelectedRepoName = repoName;
                    try {
                        GitHubBroker.getInstance().selectRepo(repoName, selectionListener);
                    }
                    catch (GitHubBroker.AlreadyNotConnectedException e) {
                        Log.wtf("debug", e.getClass().getName(), e);
                    }
                    catch (GitHubBroker.NullArgumentException e) {
                        Log.wtf("debug", e.getClass().getName(), e);
                    }
                    mCallbacks.onStartLoad();
                    Log.wtf("BLAH", "----- START LOAD ----");
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        mDrawerListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectItem(position);
            }
        });

        final NavigationDrawerArrayAdapter navigationAdapter = new NavigationDrawerArrayAdapter();
        mDrawerListView.setAdapter(navigationAdapter);


        return ret;
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
                                DrawerLayoutFragmentActivity.getLastSelectedFragmentIndex() + 1),
                        "Home"
                )
        );
    }

    private void selectItem(int position) {
        if (mDrawerListView != null) {
            mDrawerListView.setItemChecked(position, true);

        }
        if (mDrawerLayout != null && (!(position == 0 || position == 4 || position == 7))) {
            mDrawerLayout.closeDrawer(mFragmentContainerView);
        }
        if (mCallbacks != null) {
            if (!(position == 0 || position == 4 || position == 7)) {
                int aux;
                if (position < 4) {
                    aux = position - 1;
                }
                else if (position < 7) {
                    aux = position - 2;
                }
                else {
                    aux = position - 3;
                }
                mCallbacks.onNavigationDrawerItemSelected(aux);
            }
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

        void onStartLoad();

        void onStopLoad();
    }

    private class NavigationDrawerArrayAdapter extends ArrayAdapter<String> {

        private static final int mEntryResource = R.layout.list_item_navigation_drawer_list,
                mDividerResource = R.layout.list_item_navigation_drawer_list_divider;

        public NavigationDrawerArrayAdapter() {
            super(getActivity().getApplicationContext(), mEntryResource);
        }

        @Override
        public int getCount() {
            Context context = getActivity().getApplicationContext();
            for (int i = 1; ; i++) {
                if (AgileAppModuleUtils.getString(context, "title_section" + i, null) == null) {
                    return i - 1 + 3;//3 is the number of sections
                }
            }
        }

        @Override
        public String getItem(int i) {
            int temp = i + 1;
            if (i == 0 | i == 4 | i == 7) {
                return AgileAppModuleUtils
                        .getString(getActivity().getApplicationContext(), "title_header" + i, null);
            }
            return AgileAppModuleUtils
                    .getString(getActivity().getApplicationContext(), "title_section" + temp, "");
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater =
                    (LayoutInflater) getActivity().getApplicationContext().getSystemService(
                            Context.LAYOUT_INFLATER_SERVICE);
            Log.d("debug", "position on getView:" + position);
            if (position == 0 || position == 4 || position == 7) {
                String text = AgileAppModuleUtils
                        .getString(getActivity().getApplicationContext(), "title_header" + position,
                                null);
                convertView = inflater.inflate(mDividerResource, parent, false);
                ((TextView) convertView.findViewById(R.id.divider_title_view)).setText(text);
                return convertView;
            }
            convertView = inflater.inflate(mEntryResource, parent, false);

            int temp = position + 1;

            if (position < 4) {
                temp = temp - 1;
            }
            else if (position < 7) {
                temp = temp - 2;
            }
            else {
                temp = temp - 3;
            }

            TextView textView =
                    (TextView) convertView.findViewById(R.id.navigation_drawer_item_title);
            ImageView imageView =
                    (ImageView) convertView.findViewById(R.id.navigation_drawer_item_icon);

            if (temp == (DrawerLayoutFragmentActivity.getLastSelectedFragmentIndex() + 1)) {
                convertView.setBackgroundColor(getResources().getColor(R.color.theme_orange));
                textView.setTypeface(null, Typeface.BOLD);
            }

            textView.setText(AgileAppModuleUtils
                    .getString(getActivity().getApplicationContext(), "title_section" + +temp, ""));
            imageView.setImageResource(
                    AgileAppModuleUtils.getDrawableAsId("icon_section" + temp, -1));
            return convertView;
        }
    }
}
