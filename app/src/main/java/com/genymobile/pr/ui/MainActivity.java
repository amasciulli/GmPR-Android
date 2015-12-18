package com.genymobile.pr.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import com.genymobile.pr.bus.ReposRetrievedEvent;
import com.genymobile.pr.model.PullRequest;
import com.genymobile.pr.net.ReposCallback;
import com.genymobile.pr.net.GitHubProvider;
import com.genymobile.pr.net.PullRequestsCallback;
import com.genymobile.pr.R;
import com.genymobile.pr.model.Repo;
import com.genymobile.pr.bus.BusProvider;
import com.genymobile.pr.bus.PullRequestsRetrievedEvent;
import com.squareup.otto.Subscribe;

import java.util.List;

public class MainActivity extends AppCompatActivity implements ItemClickListener<PullRequest> {
    private static final String TAG = MainActivity.class.getSimpleName();
    private static final String GENYMOBILE = "Genymobile";

    private RepoListAdapter adapter;

    private GitHubProvider provider = new GitHubProvider();
    private List<Repo> repos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RecyclerView recycler = (RecyclerView) findViewById(R.id.recycler);
        recycler.setLayoutManager(new LinearLayoutManager(this));

        adapter = new RepoListAdapter();
        adapter.setItemClickListener(this);
        recycler.setAdapter(adapter);

        provider.getRepos(GENYMOBILE).enqueue(new ReposCallback());
    }

    @Override
    protected void onResume() {
        super.onResume();
        BusProvider.getInstance().register(this);
    }

    @Subscribe
    @SuppressWarnings("unused")
    public void onReposRetrieved(ReposRetrievedEvent event) {
        PullRequestsCallback callback = new PullRequestsCallback();
        repos = event.getRepos();
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

    @Override
    public void onClick(PullRequest pullRequest, int position) {
        Toast.makeText(this, pullRequest.getTitle() + " clicked", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onPause() {
        BusProvider.getInstance().unregister(this);
        super.onPause();
    }
}
