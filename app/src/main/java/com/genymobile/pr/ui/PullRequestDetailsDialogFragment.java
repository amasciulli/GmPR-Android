package com.genymobile.pr.ui;

import com.genymobile.pr.R;
import com.genymobile.pr.bus.BusProvider;
import com.genymobile.pr.bus.IssueRetrievedEvent;
import com.genymobile.pr.model.Issue;
import com.genymobile.pr.model.Label;
import com.genymobile.pr.net.GitHubProvider;
import com.genymobile.pr.net.IssueCallback;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;
import com.squareup.otto.Subscribe;

// TODO add a loader when retrieving data.
// TODO handle retrieving data errors
public class PullRequestDetailsDialogFragment extends DialogFragment {
    private static final String ARG_TITLE = "title";
    private static final String ARG_NUMBER = "number";
    private static final String ARG_REPO = "repo";
    private static final int DEFAULT_SPACING_PX = 5;
    private int number;
    private String repo;
    private String organization;
    private String title;

    public static PullRequestDetailsDialogFragment newInstance(int pullRequestNumber, String repo, String title) {
        PullRequestDetailsDialogFragment pullRequestDetailsDialogFragment = new PullRequestDetailsDialogFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_NUMBER, pullRequestNumber);
        args.putString(ARG_REPO, repo);
        args.putString(ARG_TITLE, title);
        pullRequestDetailsDialogFragment.setArguments(args);

        return pullRequestDetailsDialogFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        BusProvider.getInstance().register(this);
        number = getArguments().getInt(ARG_NUMBER);
        repo = getArguments().getString(ARG_REPO);
        title = getArguments().getString(ARG_TITLE);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        organization = preferences.getString(getString(R.string.pref_organization), null);

        getIssue();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Custom Layouts are created with a null parent as per https://developer.android.com/guide/topics/ui/dialogs.html section Creating a Custom Layout
        @SuppressLint("InflateParams")
        View root = getActivity().getLayoutInflater().inflate(R.layout.pull_request_details_fragment_dialog, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        return builder.setTitle(title).setView(root).create();
    }

    private void getIssue() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String login = preferences.getString(getString(R.string.pref_login), null);
        String password = preferences.getString(getString(R.string.pref_password), null);
        GitHubProvider provider = new GitHubProvider(login, password);
        provider.getIssue(organization, repo, number).enqueue(new IssueCallback());
    }

    @Subscribe
    @SuppressWarnings("unused")
    public void onIssueRetrieved(IssueRetrievedEvent event) {
        int defaultSpacing = dpToPx(DEFAULT_SPACING_PX);
        LinearLayout labelsView = (LinearLayout) PullRequestDetailsDialogFragment.this.getDialog().findViewById(R.id.labels);
        LayoutParams layoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(defaultSpacing, defaultSpacing, defaultSpacing, defaultSpacing);
        Issue issue = event.getIssue();
        for (Label label : issue.getLabels()) {
            addLabel(labelsView, label, layoutParams);
        }
    }

    public int dpToPx(int dp) {
        DisplayMetrics displayMetrics = getContext().getResources().getDisplayMetrics();
        return Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
    }

    private void addLabel(LinearLayout labelsView, Label label, LayoutParams layoutParams) {
        TextView textView = new TextView(getContext());
        textView.setLayoutParams(layoutParams);
        textView.setText(label.getName());
        textView.setPadding(DEFAULT_SPACING_PX, DEFAULT_SPACING_PX, DEFAULT_SPACING_PX, DEFAULT_SPACING_PX);
        textView.setBackgroundColor(Color.parseColor("#" + label.getColor()));
        labelsView.addView(textView);
    }

    @Override
    public void onDestroy() {
        BusProvider.getInstance().unregister(this);
        super.onDestroy();
    }
}

