package com.genymobile.pr;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import java.util.List;

import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

public class MainActivity extends AppCompatActivity implements Callback<List<Repo>> {
    private static final String TAG = MainActivity.class.getSimpleName();

    private RepoListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RecyclerView recycler = (RecyclerView) findViewById(R.id.recycler);
        recycler.setLayoutManager(new LinearLayoutManager(this));

        adapter = new RepoListAdapter();
        recycler.setAdapter(adapter);

        new GitHubProvider().getRepos("Genymobile").enqueue(this);
    }

    @Override
    public void onResponse(Response<List<Repo>> response, Retrofit retrofit) {
        if (!response.isSuccess()) {
            //TODO handle
            Log.e(TAG, "Couldn't load repos");
            return;
        }
        adapter.setRepos(response.body());
    }

    @Override
    public void onFailure(Throwable t) {
        Log.e(TAG, "Couldn't load repos", t);
        //TODO handle
    }
}
