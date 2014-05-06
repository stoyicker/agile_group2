package org.arnolds.agileappproject.agileappmodule.ui.frags;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.arnolds.agileappproject.agileappmodule.R;
import org.arnolds.agileappproject.agileappmodule.git.GitHubBroker;
import org.arnolds.agileappproject.agileappmodule.git.GitHubBrokerListener;
import org.arnolds.agileappproject.agileappmodule.git.IGitHubBroker;
import org.kohsuke.github.GHIssue;

import fr.castorflex.android.smoothprogressbar.SmoothProgressBar;


public class CreateIssueFragment extends Fragment {

    private static final long ISSUE_CREATED_SHOW_TIME_MILLIS = 1000;
    public static boolean isShown = Boolean.FALSE;
    private View view;
    private EditText editTextTitle, editTextComment;
    private IssueCreationCallbacks mCallback;

    public interface IssueCreationCallbacks {
        public void onIssueCreated();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            mCallback = (IssueCreationCallbacks) activity;
        }
        catch (ClassCastException e) {
            Log.wtf("debug", "You have to implement IssueCreationCallbacks");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setHasOptionsMenu(Boolean.TRUE);
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        MenuItem newIssueItem = menu.findItem(R.id.action_create);

        newIssueItem.setVisible(Boolean.FALSE);
        newIssueItem.setEnabled(Boolean.FALSE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        isShown = Boolean.TRUE;
        view = inflater.inflate(R.layout.fragment_create_issue, container, false);
        Button createIssueButton = (Button) view.findViewById(R.id.create_issue_button);
        createIssueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submit();
            }
        });

        return view;
    }

    private void submit() {

        show_progress(true);

        final Button createIssueButton = (Button) view.findViewById(R.id.create_issue_button);
        createIssueButton.setVisibility(View.INVISIBLE);

        editTextTitle = (EditText) getView().findViewById(R.id.create_issue_title);
        final String title = editTextTitle.getText().toString();

        editTextComment = (EditText) getView().findViewById(R.id.create_issue_comment);
        final String comment = editTextComment.getText().toString();


        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(
                getActivity().INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(editTextTitle.getWindowToken(), 0);

        //Checks that the title is not all spaces or empty.
        if (!title.trim().isEmpty()) {

            final GitHubBrokerListener listener = new GitHubBrokerListener() {
                @Override
                public void onIssueCreation(boolean result, GHIssue issue) {
                    super.onIssueCreation(result, issue);
                    isShown = Boolean.FALSE;
                    getActivity().runOnUiThread(new Runnable() {
                        public void run() {
                            show_progress(false);
                            show_result();
                        }
                    });

                }
            };

            IGitHubBroker gitHubBroker = GitHubBroker.getInstance();
            try {
                gitHubBroker.createIssue(title, comment, null, listener);
            }
            catch (GitHubBroker.AlreadyNotConnectedException e) {
                e.printStackTrace();
            }
            catch (GitHubBroker.RepositoryNotSelectedException e) {
                e.printStackTrace();
            }
            catch (GitHubBroker.NullArgumentException e) {
                e.printStackTrace();
            }


        }
        else {
            editTextTitle.setError(getString(R.string.error_no_title));
        }

    }

    private void show_progress(boolean show) {
        SmoothProgressBar progressBar =
                (SmoothProgressBar) view.findViewById(R.id.create_issue_progess_bar);
        TextView creatingIssueText =
                (TextView) view.findViewById(R.id.create_issue_creating_issue_text);
        Button createIssueButton = (Button) view.findViewById(R.id.create_issue_button);

        if (show) {
            progressBar.setVisibility(View.VISIBLE);
            creatingIssueText.setVisibility(View.VISIBLE);
            createIssueButton.setVisibility(View.INVISIBLE);
        }
        else if (!show) {
            progressBar.setVisibility(View.INVISIBLE);
            creatingIssueText.setVisibility(View.INVISIBLE);
            createIssueButton.setVisibility(View.VISIBLE);
        }
    }

    private void show_result() {
        //TODO use tags instead
        Button createIssueButton = (Button) view.findViewById(R.id.create_issue_button);
        TextView createdIssueTitle = (TextView) view.findViewById(R.id.issue_title_text);
        TextView createdIssueText = (TextView) view.findViewById(R.id.created_issue_text);

        createdIssueText.setVisibility(View.VISIBLE);
        createdIssueTitle.setVisibility(View.VISIBLE);
        createdIssueTitle.setText(editTextTitle.getText().toString());

        editTextComment.setVisibility(View.INVISIBLE);
        editTextTitle.setVisibility(View.INVISIBLE);
        createIssueButton.setVisibility(View.INVISIBLE);

        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                try {
                    Thread.sleep(ISSUE_CREATED_SHOW_TIME_MILLIS);
                }
                catch (InterruptedException e) {
                    Log.wtf("debug", e.getClass().getName(), e);
                }
                mCallback.onIssueCreated();
                return null;
            }
        }.execute();
    }
}
