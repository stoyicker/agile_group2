package org.arnolds.agileappproject.agileappmodule.ui.activities;

import android.app.ActionBar;
import android.app.FragmentManager;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;

import org.arnolds.agileappproject.agileappmodule.R;
import org.arnolds.agileappproject.agileappmodule.RetrieveBranchesActivity;
import org.arnolds.agileappproject.agileappmodule.RetrieveIssuesActivity;
import org.arnolds.agileappproject.agileappmodule.ui.frags.NavigationDrawerListFragment;
import org.arnolds.agileappproject.agileappmodule.utils.AgileAppModuleUtils;

import java.util.ArrayList;

public abstract class DrawerLayoutFragmentActivity extends FragmentActivity implements
        NavigationDrawerListFragment.NavigationDrawerCallbacks {

    private static final ArrayList<Integer> navigatedItemsStack =
            new ArrayList<Integer>();
    private DrawerLayout drawerLayout;
    private CharSequence mTitle;

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

        Class target = null;
        switch (position) {
            case 0:
                target = RetrieveBranchesActivity.class;
                break;
            case 5:
                target = RetrieveIssuesActivity.class;
                break;
            default:
                Log.wtf("debug", "Should never happen - Selected index - " + position);
        }
        startActivity(new Intent(getApplicationContext(), target));
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

        if (navigatedItemsStack.isEmpty()) {
            navigatedItemsStack.add(0);
        }

        FragmentManager fragmentManager = getFragmentManager();

        NavigationDrawerListFragment mNavigationDrawerListFragment = (NavigationDrawerListFragment)
                fragmentManager.findFragmentById(R.id.navigation_drawer);

        mNavigationDrawerListFragment.setHasOptionsMenu(Boolean.TRUE);

        mTitle = getTitle();

        // Set up the drawer.
        mNavigationDrawerListFragment.setUp(
                R.id.navigation_drawer,
                drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout));
    }

    public void onSectionAttached(int number) {
        int shiftedPos = number + 1;
        mTitle = AgileAppModuleUtils.getString(this, "title_section" + shiftedPos, "");
        if (mTitle.toString().isEmpty()) {
            mTitle = getString(R.string.title_section1);
        }
    }
}
