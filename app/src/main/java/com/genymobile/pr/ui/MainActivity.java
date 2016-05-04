package com.genymobile.pr.ui;

import com.genymobile.pr.R;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity implements LoginFragment.Callbacks,
        ChooseOrganizationFragment.Callbacks, ChooseReposFragment.Callbacks {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);

        boolean credentialsSet = preferences.contains(getString(R.string.pref_login)) &&
                preferences.contains(getString(R.string.pref_password));
        boolean organizationSet = preferences.contains(getString(R.string.pref_organization));
        boolean reposChosen = preferences.contains(getString(R.string.pref_repos));

        if (!credentialsSet) {
            login();
        } else if (!organizationSet) {
            chooseOrganization();
        } else if (!reposChosen) {
            chooseRepos();
        } else {
            showPullRequests();
        }
    }

    @Override
    public void onLoginComplete() {
        chooseOrganization();
    }

    @Override
    public void onOrganizationChosen() {
        chooseRepos();
    }

    @Override
    public void onReposChosen() {
        showPullRequests();
    }

    private void login() {
        replaceFragment(new LoginFragment());
    }

    private void chooseOrganization() {
        replaceFragment(new ChooseOrganizationFragment());
    }

    private void chooseRepos() {
        replaceFragment(new ChooseReposFragment());
    }

    private void showPullRequests() {
        replaceFragment(new PullRequestListFragment());
    }

    private void replaceFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, fragment)
                .commit();
    }
}
