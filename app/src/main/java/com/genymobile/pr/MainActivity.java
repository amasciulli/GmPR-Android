package com.genymobile.pr;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import java.util.List;

import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

public class MainActivity extends AppCompatActivity implements Callback<List<Repo>> {

    private static final String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        new GitHubProvider().getRepos("Genymobile").enqueue(this);
    }

    @Override
    public void onResponse(Response<List<Repo>> response, Retrofit retrofit) {
        if (!response.isSuccess()) {
            //TODO handle
            return;
        }
        for (Repo repo : response.body()) {
            Log.d(TAG , repo.getName());
        }
    }

    @Override
    public void onFailure(Throwable t) {
        //TODO handle
    }
}
