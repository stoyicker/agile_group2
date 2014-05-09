package org.arnolds.agileappproject.agileappmodule.ui.frags;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Color;
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
import android.widget.BaseAdapter;
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
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class NavigationDrawerFragment extends Fragment {

    private static final long REPOS_POLL_RATE_SECONDS = Long.MAX_VALUE;
    private static int LAST_SELECTED_ITEM_INDEX = 0;
    private NavigationDrawerCallbacks mCallbacks;

    private ActionBarDrawerToggle mDrawerToggle;

    private DrawerLayout mDrawerLayout;
    private ListView mDrawerListView;
    private View mFragmentContainerView;
    private int latestMenuItemSelected = -1;
    private Spinner mRepoSelectionSpinner;
    private String latestSelectedRepoName = "";
    private final SelectionListener selectionListener = new SelectionListener();

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // Indicate that this fragment would like to influence the set of actions in the action bar.
        setHasOptionsMenu(Boolean.TRUE);
    }

    private class SelectionListener extends GitHubBrokerListener {
        @Override
        public void onRepoSelected(boolean result) {
            try {
                mCallbacks.onNewRepoSelected(latestSelectedRepoName);
            }
            catch (NullPointerException ex) {
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View ret = inflater.inflate(
                R.layout.fragment_navigation_drawer_layout, container, false);
        mDrawerListView = (ListView) ret.findViewById(R.id.navigation_drawer_list_view);

        mRepoSelectionSpinner = (Spinner) ret.findViewById(R.id.repo_selector_view);

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
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        mRepoSelectionSpinner.setBackgroundColor(getResources().getColor(R.color.theme_white));

        mDrawerListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectItem(position);
                latestMenuItemSelected = position;
            }
        });
        mDrawerListView
                .setAdapter(new NavigationDrawerArrayAdapter());

        initializeAutoUpdaterRepoSelector(mRepoSelectionSpinner);

        return ret;
    }

    private final void initializeAutoUpdaterRepoSelector(final Spinner selectionSpinner) {
        final ScheduledExecutorService reposFetchService = Executors
                .newScheduledThreadPool(1);

        reposFetchService.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                if (isDrawerOpen()) {
                    return;
                }
                try {
                    GitHubBroker.getInstance().getAllRepos(new GitHubBrokerListener() {
                        @Override
                        public void onAllReposRetrieved(boolean success,
                                                        Collection<GHRepository> repositories) {
                            if (success) {
                                final List<String> allRepositories = new ArrayList<String>();
                                for (GHRepository repository : repositories)
                                    allRepositories.add(repository.getName());
                                try {
                                    final ArrayAdapter<String> adapter =
                                            new ArrayAdapter<String>(
                                                    getActivity().getApplicationContext(),
                                                    R.layout.repo_selector_spinner_selected_item,
                                                    allRepositories);
                                    adapter.setDropDownViewResource(
                                            R.layout.repo_selector_dropdown_item);
                                    final String newSelectedRepoName =
                                            selectionSpinner.getSelectedItem() == null ? "" :
                                                    selectionSpinner.getSelectedItem().toString();
                                    getActivity().runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            selectionSpinner.setAdapter(adapter);
                                            adapter.notifyDataSetChanged();
                                            if (newSelectedRepoName.isEmpty()) {
                                                selectionSpinner
                                                        .setSelection(LAST_SELECTED_ITEM_INDEX);
                                            }
                                        }
                                    });
                                }
                                catch (NullPointerException ex) {
                                }
                            }
                        }
                    });
                }
                catch (GitHubBroker.AlreadyNotConnectedException e) {
                    Log.wtf("debug", e.getClass().getName(), e);
                }
            }
        }, 0, REPOS_POLL_RATE_SECONDS, TimeUnit.SECONDS);
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

        void onNewRepoSelected(String repoName);

        void onStartLoad();
    }

    private class NavigationDrawerArrayAdapter extends BaseAdapter {

        private final int mResource = R.layout.list_item_navigation_drawer_list;

        @Override
        public int getCount() {
            Context context = getActivity().getApplicationContext();
            for (int i = 1; ; i++) {
                if (AgileAppModuleUtils.getString(context, "title_section" + i, null) == null) {
                    return i - 1;
                }
            }
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
            LayoutInflater inflater =
                    (LayoutInflater) getActivity().getApplicationContext().getSystemService(
                            Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(mResource, parent, false);


            int temp = position + 1;

            TextView textView =
                    (TextView) convertView.findViewById(R.id.navigation_drawer_item_title);
            ImageView imageView =
                    (ImageView) convertView.findViewById(R.id.navigation_drawer_item_icon);

            if (latestMenuItemSelected != -1)
                if (position==(DrawerLayoutFragmentActivity.getLastSelectedFragmentIndex())){
                    convertView.setBackgroundColor(Color.GRAY);
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
