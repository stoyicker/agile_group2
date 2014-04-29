package org.arnolds.agileappproject.agileappmodule.ui.activities;

import android.app.ActionBar;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import org.arnolds.agileappproject.agileappmodule.R;
import org.arnolds.agileappproject.agileappmodule.ui.frags.ArnoldSupportFragment;
import org.arnolds.agileappproject.agileappmodule.ui.frags.CommitLogFragment;
import org.arnolds.agileappproject.agileappmodule.ui.frags.CreateIssueFragment;
import org.arnolds.agileappproject.agileappmodule.ui.frags.ListBranchesFragment;
import org.arnolds.agileappproject.agileappmodule.ui.frags.ListIssuesFragment;
import org.arnolds.agileappproject.agileappmodule.ui.frags.NavigationDrawerFragment;
import org.arnolds.agileappproject.agileappmodule.ui.frags.TimerFragment;
import org.arnolds.agileappproject.agileappmodule.utils.AgileAppModuleUtils;

import java.util.ArrayList;

public abstract class DrawerLayoutFragmentActivity extends FragmentActivity implements
        NavigationDrawerFragment.NavigationDrawerCallbacks,
        CreateIssueFragment.IssueCreationCallbacks {

    private static final ArrayList<Integer> navigatedItemsStack =
            new ArrayList<Integer>();
    private static int MAIN_FRAGMENT_CONTAINER;
    private DrawerLayout drawerLayout;
    private CharSequence mTitle;
    private ArnoldSupportFragment[] fragments;
    private static int lastSelectedFragmentIndex = 0;

    public static int getLastSelectedNavDavIndex() {
        return navigatedItemsStack.get(0);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        MenuItem newIssueItem = menu.findItem(R.id.action_create);

        switch (lastSelectedFragmentIndex) {
            case 2:
                if (newIssueItem != null) {
                    newIssueItem.setVisible(Boolean.TRUE);
                }
                break;
            default:
                if (newIssueItem != null) {
                    newIssueItem.setVisible(Boolean.FALSE);
                }
        }

        return super.onCreateOptionsMenu(menu);
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
            case R.id.action_create:
                switch (lastSelectedFragmentIndex) {
                    case 2:
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.main_fragment_container, new CreateIssueFragment())
                                .addToBackStack("")
                                .commit();
                        getSupportFragmentManager().executePendingTransactions();
                        break;
                    default:
                        Log.wtf("debug",
                                "Should never happen - Index: " + lastSelectedFragmentIndex);
                        break;
                }
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
        mTitle = AgileAppModuleUtils.getString(getApplicationContext(),
                "title_section" + (lastSelectedFragmentIndex + 1), null);
        actionBar.setTitle(mTitle);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        navigatedItemsStack.add(0, navigatedItemsStack.get(0));
        recreate();
        onNavigationDrawerItemSelected(lastSelectedFragmentIndex);
        View stub;
        if ((stub = findViewById(R.id.commit_list_empty)) != null) {
            stub.setVisibility(View.INVISIBLE);
        }
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
        ArnoldSupportFragment target = fragments[position];

        if (target == null) {
            switch (position) {
                case 0:
                    target = new CommitLogFragment();
                    break;
                case 1:
                    target = new ListBranchesFragment();
                    break;
                case 2:
                    target = new ListIssuesFragment();
                    break;
                case 3:
                    target = new TimerFragment();
                    break;
                default:
                    Log.wtf("debug", "Should never happen - position is " + position);
                    break;
            }
            fragments[position] = target;
        }

        FragmentManager fragmentManager = getSupportFragmentManager();
        if (fragmentManager != null) {
            try {
                getSupportFragmentManager().beginTransaction()
                        .replace(MAIN_FRAGMENT_CONTAINER, target)
                        .addToBackStack("").commit();
                getSupportFragmentManager().executePendingTransactions();
            }
            catch (NullPointerException ex) {
//                Log.wtf("debug", ex.getClass().getName(),ex);
            }
        }

        if (position == 0) {
            findViewById(R.id.commit_list_empty).setVisibility(View.INVISIBLE);
        }

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
        if (navigatedItemsStack.size() < 2) {
            finish();
        }
        else if (getSupportFragmentManager().popBackStackImmediate()) {
            navigatedItemsStack.remove(0);
            lastSelectedFragmentIndex = navigatedItemsStack.remove(0);
            restoreActionBar();
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

        //Setup default home fragment
        fragments[CommitLogFragment.DRAWER_POSITION] = new CommitLogFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.main_fragment_container,
                fragments[CommitLogFragment.DRAWER_POSITION])
                .commit();
        getSupportFragmentManager().executePendingTransactions();
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


    public void notifyIssueCreated() {
        Log.d("debug", "ey");
        final FragmentManager supportFragmentManager = getSupportFragmentManager();
        supportFragmentManager.beginTransaction()
                .replace(R.id.main_fragment_container, fragments[2]).commit();
        if (supportFragmentManager != null) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    supportFragmentManager.executePendingTransactions();
                }
            });
        }
    }
}
