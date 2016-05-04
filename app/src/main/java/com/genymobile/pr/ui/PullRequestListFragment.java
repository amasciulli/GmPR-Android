package com.genymobile.pr.ui;

import com.genymobile.pr.R;
import com.genymobile.pr.bus.BusProvider;
import com.genymobile.pr.bus.LoadingErrorEvent;
import com.genymobile.pr.bus.NetworkErrorEvent;
import com.genymobile.pr.bus.PullRequestsRetrievedEvent;
import com.genymobile.pr.bus.ReposRetrievedEvent;
import com.genymobile.pr.bus.SettingsUpdatedEvent;
import com.genymobile.pr.model.PullRequest;
import com.genymobile.pr.model.Repo;
import com.genymobile.pr.net.GitHubProvider;
import com.genymobile.pr.net.PullRequestsCallback;
import com.genymobile.pr.net.ReposCallback;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.squareup.otto.Subscribe;

import java.util.List;

public class PullRequestListFragment extends Fragment {
    private static final String TAG = PullRequestListFragment.class.getSimpleName();
    private static final String PULL_REQUEST_DETAILS_DIALOG_TAG = "pull_request_details_dialog_tag";

    private static final int STATE_LOADING = 0;
    private static final int STATE_LOADED = 1;
    private static final int STATE_ERROR = 2;

    private SharedPreferences preferences;

    private String organization;
    private GitHubProvider provider;
    private boolean loadingOrNetworkErrorEncoutered = false;

    private RepoListAdapter adapter;
    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private View errorView;

    private TextView errorTextView;
    private int loadingState = -1;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());

        setHasOptionsMenu(true);

        BusProvider.getInstance().register(this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_pr_list, container, false);

        recyclerView = (RecyclerView) root.findViewById(R.id.recycler);
        progressBar = (ProgressBar) root.findViewById(R.id.progressbar);
        errorView = root.findViewById(R.id.error);
        errorTextView = (TextView) root.findViewById(R.id.error_text);

        Button retryButton = (Button) root.findViewById(R.id.retry);

        retryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadPullRequests();
            }
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        adapter = new RepoListAdapter();
        adapter.setPullRequestClickListener(new ItemClickListener<PullRequest>() {
            @Override
            public void onClick(PullRequest pullRequest) {
                openPullRequest(pullRequest);
            }

            @Override
            public void onLongClick(PullRequest pullRequest) {
                showPullRequestDetailsDialog(pullRequest);
            }
        });
        adapter.setRepoClickListener(new ItemClickListener<Repo>() {
            @Override
            public void onClick(Repo repo) {
                openRepo(repo);
            }

            @Override
            public void onLongClick(Repo item) {
                // no-op
            }
        });
        recyclerView.setAdapter(adapter);

        return root;
    }

    private void setLoadingState(int newState) {
        if (loadingState == newState) {
            return;
        }

        switch (newState) {
            case STATE_LOADING:
                recyclerView.setVisibility(View.GONE);
                progressBar.setVisibility(View.VISIBLE);
                errorView.setVisibility(View.GONE);
                break;
            case STATE_LOADED:
                recyclerView.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
                errorView.setVisibility(View.GONE);
                break;
            case STATE_ERROR:
                recyclerView.setVisibility(View.GONE);
                progressBar.setVisibility(View.GONE);
                errorView.setVisibility(View.VISIBLE);
                break;
            default:
                throw new IllegalArgumentException("Unknown state");
        }
        loadingState = newState;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        loadPullRequests();
    }

    private void loadPullRequests() {
        adapter.clear();

        String repos = preferences.getString(getString(R.string.pref_repos), null);

        String login = preferences.getString(getString(R.string.pref_login), null);
        String password = preferences.getString(getString(R.string.pref_password), null);
        organization = preferences.getString(getString(R.string.pref_organization), null);

        String watchedRepos = getString(R.string.pref_repos_watched);
        boolean displayWatched = repos != null && repos.equals(watchedRepos);

        String title = displayWatched ? getString(R.string.title_watched, login) : organization;
        getActivity().setTitle(title);

        provider = new GitHubProvider(login, password);
        loadingOrNetworkErrorEncoutered = false;
        setLoadingState(STATE_LOADING);

        if (displayWatched) {
            provider.getWatchedRepos().enqueue(new ReposCallback());
        } else {
            provider.getRepos(organization).enqueue(new ReposCallback());
        }
    }

    @Subscribe
    @SuppressWarnings("unused")
    public void onReposRetrieved(ReposRetrievedEvent event) {
        PullRequestsCallback callback = new PullRequestsCallback();
        List<Repo> repos = event.getRepos();
        for (Repo repo : repos) {
            provider.getPullRequests(repo.getOwner().getLogin(), repo.getName()).enqueue(callback);
        }
    }

    @Subscribe
    @SuppressWarnings("unused")
    public void onPullRequestsRetrieved(PullRequestsRetrievedEvent event) {
        List<PullRequest> pullRequests = event.getPullRequests();
        if (pullRequests.size() > 0) {
            Repo repo = pullRequests.get(0).getHead().getRepo();
            adapter.addRepo(repo, pullRequests);
        }
        setLoadingState(STATE_LOADED);
    }

    @Subscribe
    @SuppressWarnings("unused")
    public void onLoadingError(LoadingErrorEvent event) {
        if (loadingOrNetworkErrorEncoutered) {
            return;
        }
        loadingOrNetworkErrorEncoutered = true;

        String message;
        switch (event.getResponse().code()) {
            case 401:
                message = getString(R.string.error_unauthorized);
                break;
            case 404:
                message = getString(R.string.error_not_found);
                break;
            default:
                message = getString(R.string.error_unknown);
                break;

        }
        // TODO use error/empty states (https://www.google.fr/design/spec/patterns/errors.html#errors-app-errors)
        errorTextView.setText(message);
        setLoadingState(STATE_ERROR);
    }

    @Subscribe
    @SuppressWarnings("unused")
    public void onNetworkError(NetworkErrorEvent event) {
        if (loadingOrNetworkErrorEncoutered) {
            return;
        }
        loadingOrNetworkErrorEncoutered = true;

        // TODO use error/empty states (https://www.google.fr/design/spec/patterns/errors.html#errors-app-errors)
        errorTextView.setText(R.string.error_network);
        setLoadingState(STATE_ERROR);
    }

    @Subscribe
    @SuppressWarnings("unused")
    public void onSettingsUpdated(SettingsUpdatedEvent event) {
        loadPullRequests();
    }

    public void openPullRequest(PullRequest pullRequest) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(pullRequest.getHtmlUrl()));
        startActivity(intent);
    }

    public void openRepo(Repo repo) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(repo.getHtmlUrl()));
        startActivity(intent);
    }

    private void showPullRequestDetailsDialog(PullRequest pullRequest) {
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        Fragment previousFragment = getFragmentManager().findFragmentByTag(PULL_REQUEST_DETAILS_DIALOG_TAG);
        if (previousFragment != null) {
            fragmentTransaction.remove(previousFragment);
        }
        fragmentTransaction.addToBackStack(null);

        if (pullRequest.getHead() != null
                && pullRequest.getHead().getRepo() != null) { //TODO see why a repo can be null

            String repoName = pullRequest.getHead().getRepo().getName();
            String pullRequestTitle = pullRequest.getTitle();
            int pullRequestNumber = pullRequest.getNumber();

            DialogFragment pullRequestDetailsDialogFragment =
                    PullRequestDetailsDialogFragment.newInstance(pullRequestNumber, repoName, pullRequestTitle);
            pullRequestDetailsDialogFragment.show(fragmentTransaction, PULL_REQUEST_DETAILS_DIALOG_TAG);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_pr_list, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_settings) {
            openSettings();
        }
        return super.onOptionsItemSelected(item);
    }

    private void openSettings() {
        startActivity(new Intent(getActivity(), SettingsActivity.class));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        BusProvider.getInstance().unregister(this);
    }
}
