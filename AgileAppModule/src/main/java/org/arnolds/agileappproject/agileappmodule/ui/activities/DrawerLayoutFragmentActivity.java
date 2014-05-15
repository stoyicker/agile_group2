package org.arnolds.agileappproject.agileappmodule.ui.activities;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import org.arnolds.agileappproject.agileappmodule.R;
import org.arnolds.agileappproject.agileappmodule.data.DataModel;
import org.arnolds.agileappproject.agileappmodule.data.GitEvent;
import org.arnolds.agileappproject.agileappmodule.data.IDataModel;
import org.arnolds.agileappproject.agileappmodule.git.GitHubBroker;
import org.arnolds.agileappproject.agileappmodule.git.notifications.GitHubNotificationService;
import org.arnolds.agileappproject.agileappmodule.ui.frags.ArnoldSupportFragment;
import org.arnolds.agileappproject.agileappmodule.ui.frags.CommitLogFragment;
import org.arnolds.agileappproject.agileappmodule.ui.frags.CreateIssueFragment;
import org.arnolds.agileappproject.agileappmodule.ui.frags.IndefiniteFancyProgressFragment;
import org.arnolds.agileappproject.agileappmodule.ui.frags.ListBranchesFragment;
import org.arnolds.agileappproject.agileappmodule.ui.frags.ListIssuesFragment;
import org.arnolds.agileappproject.agileappmodule.ui.frags.NavigationDrawerFragment;
import org.arnolds.agileappproject.agileappmodule.ui.frags.PokerGameFragment;
import org.arnolds.agileappproject.agileappmodule.ui.frags.TimerFragment;
import org.arnolds.agileappproject.agileappmodule.utils.AgileAppModuleUtils;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.net.Authenticator;
import java.util.List;
import java.util.Stack;


public abstract class DrawerLayoutFragmentActivity extends FragmentActivity implements
        NavigationDrawerFragment.NavigationDrawerCallbacks,
        CreateIssueFragment.IssueCreationCallbacks {

    private static int MAIN_FRAGMENT_CONTAINER;
    private DrawerLayout drawerLayout;
    private CharSequence mTitle;
    private ArnoldSupportFragment[] fragments;
    private Button eventCount;
    private IDataModel dataModel = DataModel.getInstance();
    private NavigationDrawerFragment mNavigationDrawerFragment;

    public static int getLastSelectedFragmentIndex() {
        return lastSelectedFragmentIndex;
    }

    private static int lastSelectedFragmentIndex = 0;
    private final IndefiniteFancyProgressFragment progressFragment =
            new IndefiniteFancyProgressFragment();
    private static final Stack<Integer> selectedItemsQueue = new Stack<Integer>();
    private Boolean isLoading = Boolean.FALSE;
    private EventLogAdapter adapter;

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        MenuItem newIssueItem = menu.findItem(R.id.action_create);

        final MenuItem eventMenuItem = menu.findItem(R.id.action_event_log);
        View count = eventMenuItem.getActionView();
        eventCount = (Button) count.findViewById(R.id.feed_event_count);
        eventCount.setText(adapter.getCount() + "");

        dataModel.addPropertyChangeListener(new EventLogListener(eventCount));

        eventCount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                menu.performIdentifierAction(eventMenuItem.getItemId(), 0);
            }
        });

        switch (lastSelectedFragmentIndex) {
            case 2:
                if (newIssueItem != null && !GitHubBroker.getInstance().isFork()) {
                    newIssueItem.setVisible(Boolean.TRUE);
                }
                else if (GitHubBroker.getInstance().isFork()) {
                    newIssueItem.setVisible(Boolean.FALSE);
                }
                break;
            default:
                if (newIssueItem != null) {
                    newIssueItem.setVisible(Boolean.FALSE);
                }
        }
        return super.onCreateOptionsMenu(menu);
    }

    private void signOut() {
        Toast.makeText(this, R.id.siging_out, 2).show();
        final AccountManager accountManager = AccountManager.get(this);
        Account account = accountManager.getAccountsByType(LoginActivity.ACCOUNT_TYPE)[0];
        accountManager.removeAccount(account, null, null);
        Intent login = new Intent(this, LoginActivity.class);
        login.putExtra(LoginActivity.LAUNCH_HOME_ACTIVITY, true);
        GitHubNotificationService.getInstance().killService();
        try {
            GitHubBroker.getInstance().disconnect();
        } catch (GitHubBroker.AlreadyNotConnectedException e) {
            e.printStackTrace();
        }
        startActivity(login);
        System.exit(0);
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
            case R.id.action_event_log:
                eventLogPressed();
                break;
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

    private void eventLogPressed() {
        Log.wtf("event", "event pressed");

        LayoutInflater layoutInflater
                = (LayoutInflater) getBaseContext()
                .getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = layoutInflater.inflate(R.layout.event_log, null);
        Button clearButton = (Button) popupView.findViewById(R.id.dismiss_all_events);
        final ListView listView = (ListView) popupView.findViewById(R.id.event_log_list);
        clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                eventCount.setText("0");
                adapter.clear();
            }
        });


        listView.setAdapter(adapter);

        final PopupWindow popupWindow = new PopupWindow(
                popupView, (int) getResources().getDimension(R.dimen.event_log_width),
                (int) getResources().getDimension(R.dimen.event_log_height));

        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                popupWindow.dismiss();
            }
        });

        popupWindow.setBackgroundDrawable(new ColorDrawable(android.R.color.transparent));
        popupWindow.setFocusable(true);
        popupWindow.setOutsideTouchable(true);

        popupWindow.showAsDropDown(eventCount);


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

    @Override
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
    public void onNavigationDrawerItemSelected(final int position) {
        if (position == lastSelectedFragmentIndex) {
            //We don't want to perform an unnecessary Activity reload
            return;
        }

        selectedItemsQueue.add(lastSelectedFragmentIndex);

        ArnoldSupportFragment target = fragments[position];

        if (target == null || position == 4) {
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
                case 4:
                    target = new PokerGameFragment();
                    break;
                case 5:
                    signOut();
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
                onNavigationDrawerItemSelected(x == null ? 0 : x);
                Log.d("debug", "Am I in the else?");
                lastSelectedFragmentIndex = (x == null) ? 0 : x;
                selectedItemsQueue.pop();
            }

            mNavigationDrawerFragment.invalidateListView();
            restoreActionBar();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        adapter = new EventLogAdapter(this, dataModel.getEventList());

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

        mNavigationDrawerFragment = (NavigationDrawerFragment)
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

    private class EventLogAdapter extends BaseAdapter {

        private List<GitEvent> mEvents;
        private LayoutInflater mInflater;

        public EventLogAdapter(Context context, List<GitEvent> events) {
            mEvents = events;
            mInflater = LayoutInflater.from(context);
        }

        public void clear() {
            mEvents.clear();
            eventCount.setText("0");
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return mEvents.size();
        }

        @Override
        public Object getItem(int position) {
            return mEvents.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, final ViewGroup parent) {
            final GitEvent event = mEvents.get(position);
            final View view = mInflater.inflate(R.layout.event_log_row, null);

            ImageView imageView = (ImageView) view.findViewById(R.id.event_icon);
            switch (event.getType()) {
                case COMMIT:
                    imageView.setImageResource(R.drawable.icon_section1);
                    break;
                case FILE_CONFLICT:
                    imageView.setImageResource(R.drawable.warning);
                    break;
                case ISSUE:
                    imageView.setImageResource(R.drawable.issueyellow);
                    break;
                case TIMER_EVENT:
                    imageView.setImageResource(R.drawable.alarm);
            }

            TextView textView = (TextView) view.findViewById(R.id.event_name);
            textView.setText(event.getEventText());

            ImageButton dismissButton = (ImageButton) view.findViewById(R.id.dismiss_event);
            dismissButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dataModel.removeEvent(event);
                    mEvents.remove(event);
                    notifyDataSetChanged();
                }
            });

            return view;
        }
    }

    private class EventLogListener implements PropertyChangeListener {
        private Button button;

        public EventLogListener(Button button) {
            this.button = button;
        }

        @Override
        public void propertyChange(PropertyChangeEvent event) {
            if (event.getNewValue() != null && event.getNewValue() instanceof List) {
                final List<GitEvent> eventList = (List<GitEvent>) event.getNewValue();
                DrawerLayoutFragmentActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        button.setText(eventList.size() + "");
                    }
                });

            }
        }
    }
}
