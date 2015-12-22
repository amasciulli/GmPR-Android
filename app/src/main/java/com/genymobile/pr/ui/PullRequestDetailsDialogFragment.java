package com.genymobile.pr.ui;

import com.genymobile.pr.R;
import com.genymobile.pr.bus.BusProvider;
import com.genymobile.pr.bus.IssueRetrievedEvent;
import com.genymobile.pr.model.Issue;
import com.genymobile.pr.model.Label;
import com.genymobile.pr.net.GitHubProvider;
import com.genymobile.pr.net.IssueCallback;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;
import com.squareup.otto.Subscribe;

// TODO add a loader when retrieving data.
// TODO handle retrieving data errors
public class PullRequestDetailsDialogFragment extends DialogFragment {
    private static final String NUMBER = "number";
    private static final String REPO = "repo";
    private static final int DEFAULT_SPACING = 10;
    int number;
    String repo;

    static PullRequestDetailsDialogFragment newInstance(int pullRequestNumber, String repo) {
        PullRequestDetailsDialogFragment pullRequestDetailsDialogFragment = new PullRequestDetailsDialogFragment();
        Bundle args = new Bundle();
        args.putInt(NUMBER, pullRequestNumber);
        args.putString(REPO, repo);
        pullRequestDetailsDialogFragment.setArguments(args);

        return pullRequestDetailsDialogFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        BusProvider.getInstance().register(this);
        number = getArguments().getInt(NUMBER);
        repo = getArguments().getString(REPO);

        getIssue();
    }

    private void getIssue() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String login = preferences.getString(getString(R.string.pref_login), null);
        String password = preferences.getString(getString(R.string.pref_password), null);
        GitHubProvider provider = new GitHubProvider(login, password);
        provider.getIssue("Genymobile", repo, number).enqueue(new IssueCallback());  //TODO owner will be in pref in a future PR
    }

    @Subscribe
    @SuppressWarnings("unused")
    public void onIssueRetrieved(IssueRetrievedEvent event) {
        LinearLayout labelsView = (LinearLayout) PullRequestDetailsDialogFragment.this.getDialog().findViewById(R.id.labels);
        LayoutParams layoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(DEFAULT_SPACING, DEFAULT_SPACING, DEFAULT_SPACING, DEFAULT_SPACING);
        Issue issue = event.getIssue();
        for (Label label : issue.getLabels()) {
            addLabel(labelsView, label, layoutParams);
        }
    }

    private void addLabel(LinearLayout labelsView, Label label, LayoutParams layoutParams) {
        TextView textView = new TextView(getContext());
        textView.setLayoutParams(layoutParams);
        textView.setText(label.getName());
        textView.setPadding(DEFAULT_SPACING, DEFAULT_SPACING, DEFAULT_SPACING, DEFAULT_SPACING);
        textView.setBackgroundColor(Color.parseColor("#" + label.getColor()));
        labelsView.addView(textView);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.pull_request_details_fragment_dialog, container, false);
    }

    @Override
    public void onDestroy() {
        BusProvider.getInstance().unregister(this);
        super.onDestroy();
    }
}

