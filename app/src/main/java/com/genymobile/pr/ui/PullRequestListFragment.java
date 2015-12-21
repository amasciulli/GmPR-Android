package com.genymobile.pr.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.genymobile.pr.R;
import com.genymobile.pr.bus.BusProvider;
import com.genymobile.pr.bus.PullRequestsRetrievedEvent;
import com.genymobile.pr.bus.ReposRetrievedEvent;
import com.genymobile.pr.model.PullRequest;
import com.genymobile.pr.model.Repo;
import com.genymobile.pr.net.GitHubProvider;
import com.genymobile.pr.net.PullRequestsCallback;
import com.genymobile.pr.net.ReposCallback;
import com.squareup.otto.Subscribe;

import java.util.List;

public class PullRequestListFragment extends Fragment {
    private static final String TAG = PullRequestListFragment.class.getSimpleName();
    private static final String GENYMOBILE = "Genymobile";

    private GitHubProvider provider;
    private RepoListAdapter adapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String login = preferences.getString(getString(R.string.pref_login), null);
        String password = preferences.getString(getString(R.string.pref_password), null);
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
        });
        adapter.setRepoClickListener(new ItemClickListener<Repo>() {
            @Override
            public void onClick(Repo repo) {
                openRepo(repo);
            }
        });
        recycler.setAdapter(adapter);

        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        provider.getRepos(GENYMOBILE).enqueue(new ReposCallback());
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
            provider.getPullRequests(GENYMOBILE, repo.getName()).enqueue(callback);
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

    @Override
    public void onPause() {
        BusProvider.getInstance().unregister(this);
        super.onPause();
    }
}
