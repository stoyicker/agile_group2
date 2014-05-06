package org.arnolds.agileappproject.agileappmodule.ui.activities;

import android.app.ActionBar;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;

import org.arnolds.agileappproject.agileappmodule.R;
import org.arnolds.agileappproject.agileappmodule.ui.frags.ArnoldSupportFragment;
import org.arnolds.agileappproject.agileappmodule.ui.frags.CommitLogFragment;
import org.arnolds.agileappproject.agileappmodule.ui.frags.CreateIssueFragment;
import org.arnolds.agileappproject.agileappmodule.ui.frags.IndefiniteFancyProgressFragment;
import org.arnolds.agileappproject.agileappmodule.ui.frags.ListBranchesFragment;
import org.arnolds.agileappproject.agileappmodule.ui.frags.ListIssuesFragment;
import org.arnolds.agileappproject.agileappmodule.ui.frags.NavigationDrawerFragment;
import org.arnolds.agileappproject.agileappmodule.ui.frags.TimerFragment;
import org.arnolds.agileappproject.agileappmodule.utils.AgileAppModuleUtils;

import java.util.Stack;

public abstract class DrawerLayoutFragmentActivity extends FragmentActivity implements
        NavigationDrawerFragment.NavigationDrawerCallbacks,
        CreateIssueFragment.IssueCreationCallbacks {

    private static int MAIN_FRAGMENT_CONTAINER;
    private DrawerLayout drawerLayout;
    private CharSequence mTitle;
    private ArnoldSupportFragment[] fragments;

    public static int getLastSelectedFragmentIndex() {
        return lastSelectedFragmentIndex;
    }

    private static int lastSelectedFragmentIndex = 0;
    private final IndefiniteFancyProgressFragment progressFragment =
            new IndefiniteFancyProgressFragment();
    private static final Stack<Integer> selectedItemsQueue = new Stack<Integer>();
    private Boolean isLoading = Boolean.FALSE;

    public static int getLastSelectedNavDavIndex() {
        Integer ret;
        if (selectedItemsQueue.isEmpty()) {
            ret = 0;
        }
        else {
            ret = selectedItemsQueue.peek();
        }

        return ret.intValue();
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
                        if (!isLoading) {
                            getSupportFragmentManager().beginTransaction()
                                    .replace(R.id.main_fragment_container,
                                            new CreateIssueFragment())
                                    .addToBackStack("")
                                    .commit();
                            getSupportFragmentManager().executePendingTransactions();
                        }
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
        onNavigationDrawerItemSelected(lastSelectedFragmentIndex);
//        navigatedItemsStack.add(0, navigatedItemsStack.get(0));
////        recreate();
//        onNavigationDrawerItemSelected(lastSelectedFragmentIndex);
        //TODO
    }

    @Override
    public synchronized void onStartLoad() {
        isLoading = Boolean.TRUE;
        if (fragments[lastSelectedFragmentIndex] != null) {
            getSupportFragmentManager().beginTransaction()
                    .remove(fragments[lastSelectedFragmentIndex])
                    .commit();
        }
        getFragmentManager().beginTransaction()
                .replace(R.id.main_fragment_container, progressFragment)
                .commit();
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                getFragmentManager().executePendingTransactions();
                getSupportFragmentManager().executePendingTransactions();
            }
        });
    }

    public synchronized void onStopLoad() {
        try {
            getFragmentManager().beginTransaction().remove(progressFragment).commit();
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.main_fragment_container, fragments[lastSelectedFragmentIndex])
                    .commit();
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    getFragmentManager().executePendingTransactions();
                    getSupportFragmentManager().executePendingTransactions();
                }
            });
        }
        catch (IllegalStateException ex) {
        }
        isLoading = Boolean.FALSE;
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        if (position == lastSelectedFragmentIndex) {
            //We don't want to perform an unnecessary Activity reload
            return;
        }

        Log.wtf("debug", "Adding " + position, new Exception());
        selectedItemsQueue.add(lastSelectedFragmentIndex);

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
        if (fragmentManager != null && (!isLoading || position > 2)) {
            try {
                fragmentManager.beginTransaction().remove(fragments[lastSelectedFragmentIndex])
                        .commit();
                fragmentManager.beginTransaction()
                        .replace(MAIN_FRAGMENT_CONTAINER, target)
                        .addToBackStack("").commit();
                fragmentManager.executePendingTransactions();
            }
            catch (NullPointerException ex) {
//                Log.wtf("debug", ex.getClass().getName(),ex);
            }
        }
        else if (fragmentManager != null && isLoading && position != 3) {
            fragmentManager.beginTransaction()
                    .remove(fragments[lastSelectedFragmentIndex]).commit();
            getFragmentManager().beginTransaction().replace(MAIN_FRAGMENT_CONTAINER,
                    progressFragment).commit();
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    getFragmentManager().executePendingTransactions();
                    getSupportFragmentManager().executePendingTransactions();
                }
            });
        }

        lastSelectedFragmentIndex = position;

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                DrawerLayoutFragmentActivity.this.findViewById(R.id.activity_home).invalidate();
//                restoreActionBar();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        mTitle = AgileAppModuleUtils
                .getString(this, "title_section" + (lastSelectedFragmentIndex + 1), "");
        restoreActionBar();
    }

    @Override
    public void onBackPressed() {
        printQueue();
        Log.d("debug", "selectedItemsQueue size: " + selectedItemsQueue.size());
        if (selectedItemsQueue.isEmpty()) {
            Log.d("debug", "the queue is empty");
        }
        else {
            Log.d("debug", "selectedItemsQueue head: " + selectedItemsQueue.peek());
        }
        Log.d("debug", "lastSelectedFragmentIndex " + lastSelectedFragmentIndex);
        if (selectedItemsQueue.isEmpty()) {
            Intent i = new Intent(Intent.ACTION_MAIN);
            i.addCategory(Intent.CATEGORY_HOME);
            startActivity(i);
        }
        else {
            if (lastSelectedFragmentIndex == 2 &&
                    CreateIssueFragment.isShown) {//If the new issue fragment is being shown
                getSupportFragmentManager().popBackStackImmediate();
                CreateIssueFragment.isShown = Boolean.FALSE;
            }
            else {
                Integer x = selectedItemsQueue.pop();
                Log.d("debug", "Polled a " + x);
                onNavigationDrawerItemSelected(x == null ? 0 : x);
                Integer y = selectedItemsQueue.pop();
                Log.d("debug", "Removed the (should be duplicated) " + y);
            }

            restoreActionBar();
        }
        printQueue();
    }

    private void printQueue() {
        StringBuilder builder = new StringBuilder("");
        for (Integer x : selectedItemsQueue) {
            builder.append(x).append("\n");
        }
        Log.d("debug", "Queue: " + builder.toString());
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
        mTitle = AgileAppModuleUtils.getString(this, "title_section" + shiftedPos, "");
        if (mTitle.toString().isEmpty()) {
            mTitle = getString(R.string.title_section1);
        }
    }

    @Override
    public void onNewRepoSelected(String repoName) {
        for (ArnoldSupportFragment x : fragments) {
            try {
                x.onNewRepositorySelected();
            }
            catch (NullPointerException ex) {
//            Log.wtf("debug", ex.getClass().getName(), ex);
            }
            getSupportFragmentManager()
                    .popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
            selectedItemsQueue.clear();
//            lastSelectedFragmentIndex = 0;
            onNavigationDrawerItemSelected(lastSelectedFragmentIndex);
        }
    }


    public void notifyIssueCreated() {
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
