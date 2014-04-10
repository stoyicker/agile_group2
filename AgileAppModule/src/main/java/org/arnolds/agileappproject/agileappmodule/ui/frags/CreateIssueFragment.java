package org.arnolds.agileappproject.agileappmodule.ui.frags;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import org.arnolds.agileappproject.agileappmodule.R;
import org.arnolds.agileappproject.agileappmodule.git.GitHubBroker;
import org.arnolds.agileappproject.agileappmodule.git.IGitHubBroker;



public class CreateIssueFragment extends Fragment {

    public CreateIssueFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_create_issue, container, false);
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
        EditText editTextTitle = (EditText) getView().findViewById(R.id.create_issue_title);
        String title = editTextTitle.getText().toString();

        EditText editTextComment = (EditText) getView().findViewById(R.id.create_issue_comment);
        String comment = editTextComment.getText().toString();

        //Checks that the title is not all spaces or empty.
        if (!title.trim().isEmpty()){
            IGitHubBroker gitHubBroker = GitHubBroker.getInstance();
        }
        else{
            editTextTitle.setError(getString(R.string.error_no_title));
        }

    }


}
