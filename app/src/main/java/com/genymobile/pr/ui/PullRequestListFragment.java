package com.genymobile.pr.ui;

import com.genymobile.pr.R;
import com.genymobile.pr.bus.BusProvider;
import com.genymobile.pr.bus.LoadingErrorEvent;
import com.genymobile.pr.bus.PullRequestsRetrievedEvent;
import com.genymobile.pr.bus.ReposRetrievedEvent;
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
import android.widget.Toast;

import com.squareup.otto.Subscribe;

import java.util.List;

public class PullRequestListFragment extends Fragment {
    private static final String TAG = PullRequestListFragment.class.getSimpleName();
    private static final String PULL_REQUEST_DETAILS_DIALOG_TAG = "pull_request_details_dialog_tag";

    private GitHubProvider provider;
    private RepoListAdapter adapter;

    private String login;
    private String password;
    private String organization;
    private boolean loadingErrorEncoutered = false;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        login = preferences.getString(getString(R.string.pref_login), null);
        password = preferences.getString(getString(R.string.pref_password), null);
        organization = preferences.getString(getString(R.string.pref_organization), null);

        getActivity().setTitle(organization);

        provider = new GitHubProvider(login, password);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_pr_list, container, false);
        RecyclerView recycler = (RecyclerView) root.findViewById(R.id.recycler);
        recycler.setLayoutManager(new LinearLayoutManager(getActivity()));

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
        recycler.setAdapter(adapter);

        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        provider.getRepos(organization).enqueue(new ReposCallback());
    }

    @Override
    public void onResume() {
        super.onResume();
        BusProvider.getInstance().register(this);
    }

    @Subscribe
    @SuppressWarnings("unused")
    public void onReposRetrieved(ReposRetrievedEvent event) {
        PullRequestsCallback callback = new PullRequestsCallback();
        List<Repo> repos = event.getRepos();
        for (Repo repo : repos) {
            provider.getPullRequests(organization, repo.getName()).enqueue(callback);
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
    }

    @Subscribe
    @SuppressWarnings("unused")
    public void onNetworkError(LoadingErrorEvent event) {
        if (loadingErrorEncoutered) {
            return;
        }

        loadingErrorEncoutered = true;
        String message;
        if (event.isNetworkError()) {
            message = getString(R.string.error_network);
        } else {
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
        }
        // TODO use error/empty states (https://www.google.fr/design/spec/patterns/errors.html#errors-app-errors)
        Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
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
            int pullRequestNumber = pullRequest.getNumber();

            DialogFragment pullRequestDetailsDialogFragment =
                    PullRequestDetailsDialogFragment.newInstance(pullRequestNumber, repoName);
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
    public void onPause() {
        BusProvider.getInstance().unregister(this);
        super.onPause();
    }
}
