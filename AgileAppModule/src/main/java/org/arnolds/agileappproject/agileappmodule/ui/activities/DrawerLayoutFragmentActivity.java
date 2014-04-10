package org.arnolds.agileappproject.agileappmodule.ui.activities;

import android.app.ActionBar;

import android.support.v4.app.Fragment;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;

import org.arnolds.agileappproject.agileappmodule.R;
import org.arnolds.agileappproject.agileappmodule.ui.frags.ArnoldSupportFragment;
import org.arnolds.agileappproject.agileappmodule.ui.frags.ListBranchesFragment;
import org.arnolds.agileappproject.agileappmodule.ui.frags.ListIssuesFragment;
import org.arnolds.agileappproject.agileappmodule.ui.frags.NavigationDrawerFragment;
import org.arnolds.agileappproject.agileappmodule.RetrieveBranchesActivity;
import org.arnolds.agileappproject.agileappmodule.RetrieveIssuesActivity;
import org.arnolds.agileappproject.agileappmodule.ui.frags.CreateIssueFragment;
import org.arnolds.agileappproject.agileappmodule.utils.AgileAppModuleUtils;

import java.util.ArrayList;

public abstract class DrawerLayoutFragmentActivity extends FragmentActivity implements
        NavigationDrawerFragment.NavigationDrawerCallbacks {

    private static final ArrayList<Integer> navigatedItemsStack =
            new ArrayList<Integer>();
    private static int MAIN_FRAGMENT_CONTAINER;
    private DrawerLayout drawerLayout;
    private CharSequence mTitle;
    private ArnoldSupportFragment[] fragments;
    private int lastSelectedFragmentIndex = 0;

    public static int getLastSelectedNavDavIndex() {
        return navigatedItemsStack.get(0);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        drawerLayout.closeDrawer(Gravity.LEFT);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Boolean ret = Boolean.TRUE;
        switch (item.getItemId()) {
            case R.id.action_settings:
//  TODO make settings startActivity(new Intent(getApplicationContext(), SettingsPreferenceActivity.class));
                break;
            default: //Up button
                ret = super.onOptionsItemSelected(item);
        }
        restoreActionBar();
        return ret;
    }

    private void restoreActionBar() {
        ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(Boolean.TRUE);
        actionBar.setTitle(mTitle);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        navigatedItemsStack.add(0, navigatedItemsStack.get(0));
        recreate();
        onNavigationDrawerItemSelected(lastSelectedFragmentIndex);
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        if (position == getLastSelectedNavDavIndex()) {
            //We don't want to perform an unnecessary Activity reload
            return;
        }
        else {
            navigatedItemsStack.add(0, position);
        }

        lastSelectedFragmentIndex = position;
        Fragment target = fragments[position];

        if (target == null) {
            switch (position) {
                case 0:
                    //NOTIFICATIONCOMMITFRAGMENT
                    break;
                case 1:
                    target = new ListBranchesFragment();
                    break;
                case 2:
                    target = new ListIssuesFragment();
                    break;
                default:
                    Log.wtf("debug", "Should never happen - position is " + position);
                    break;
            }
        }

        getSupportFragmentManager().beginTransaction().replace(MAIN_FRAGMENT_CONTAINER, target)
                .addToBackStack("").commit();
        getSupportFragmentManager().executePendingTransactions();
        findViewById(R.id.activity_home).invalidate();
    }


    @Override
    protected void onResume() {
        super.onResume();
        mTitle = AgileAppModuleUtils
                .getString(this, "title_section" + (getLastSelectedNavDavIndex() + 1), "");
        restoreActionBar();
    }

    @Override
    public void onBackPressed() {
        if (navigatedItemsStack.size() > 1) {
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        navigatedItemsStack.remove(0);
        super.onDestroy();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(savedInstanceState.getInt("layout"));

        MAIN_FRAGMENT_CONTAINER = savedInstanceState.getInt("main_fragment_container");

        int amountOfSections;

        for (int i = 1; ; i++) {
            String thisTitle = AgileAppModuleUtils
                    .getString(getApplicationContext(), "title_section" + i, null);
            if (thisTitle == null) {
                amountOfSections = i - 1;
                break;
            }
        }

        fragments = new ArnoldSupportFragment[amountOfSections];

        if (navigatedItemsStack.isEmpty()) {
            navigatedItemsStack.add(0);
        }

        FragmentManager fragmentManager = getSupportFragmentManager();

        NavigationDrawerFragment mNavigationDrawerFragment = (NavigationDrawerFragment)
                fragmentManager.findFragmentById(R.id.navigation_drawer_fragment);

        mNavigationDrawerFragment.setHasOptionsMenu(Boolean.TRUE);

        mTitle = getTitle();

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer_fragment,
                drawerLayout = (DrawerLayout) findViewById(R.id.activity_home));
    }

    public void onSectionAttached(int number) {
        int shiftedPos = number + 1;
        Log.d("tagThis", "shiftedPost" + shiftedPos);
        mTitle = AgileAppModuleUtils.getString(this, "title_section" + shiftedPos, "");
        if (mTitle.toString().isEmpty()) {
            mTitle = getString(R.string.title_section1);
        }
    }

    @Override
    public void onNewRepoSelected(String repoName) {
        try {
            for (ArnoldSupportFragment x : fragments)
                x.onNewRepositorySelected();
        }
        catch (NullPointerException ex) {
//            Log.wtf("debug", ex.getClass().getName(), ex);
        }
    }
}
