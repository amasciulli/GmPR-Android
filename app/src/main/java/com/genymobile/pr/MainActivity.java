package com.genymobile.pr;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Toast;
import com.squareup.otto.Subscribe;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

import java.util.List;

public class MainActivity extends AppCompatActivity implements Callback<List<Repo>>, ItemClickListener<Repo> {
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

        provider.getRepos(GENYMOBILE).enqueue(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        BusProvider.getInstance().register(this);
    }

    @Override
    public void onResponse(Response<List<Repo>> response, Retrofit retrofit) {
        if (!response.isSuccess()) {
            //TODO handle
            Log.e(TAG, "Couldn't load repos");
            return;
        }
        repos = response.body();
        for (Repo repo : repos) {
            provider.getPullrequests(GENYMOBILE, repo.getName()).enqueue(new PullRequestsCallback());
        }
    }

    @Subscribe
    @SuppressWarnings("unused")
    public void onPullRequestsRetrieved(PullRequestsRetrievedEvent event) {
        Log.d(TAG, "onPullRequestsRetrieved(): " + "event = [" + event + "]");
    }

    @Override
    public void onFailure(Throwable t) {
        Log.e(TAG, "Couldn't load repos", t);
        //TODO handle
    }

    @Override
    public void onClick(Repo item, int position) {
        Toast.makeText(this, item.getName() + " clicked", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onPause() {
        BusProvider.getInstance().unregister(this);
        super.onPause();
    }
}
