package com.genymobile.pr.ui;

import com.genymobile.pr.R;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity implements LoginFragment.Callbacks,
        ChooseOrganizationFragment.Callbacks, ChooseReposFragment.Callbacks {

    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        preferences = PreferenceManager.getDefaultSharedPreferences(this);

        boolean credentialsSet = preferences.contains(getString(R.string.pref_login)) &&
                preferences.contains(getString(R.string.pref_password));
        boolean organizationSet = preferences.contains(getString(R.string.pref_organization));
        boolean reposChosen = preferences.contains(getString(R.string.pref_repos));

        if (!credentialsSet) {
            login();
        } else if (!reposChosen) {
            chooseRepos();
        } else if (!organizationSet) {
            chooseOrganizationOrShowPullRequests();
        } else {
            showPullRequests();
        }
    }

    @Override
    public void onLoginComplete() {
        chooseRepos();
    }

    @Override
    public void onReposChosen() {
        chooseOrganizationOrShowPullRequests();
    }

    @Override
    public void onOrganizationChosen() {
        showPullRequests();
    }

    private void login() {
        replaceFragment(new LoginFragment());
    }

    private void chooseRepos() {
        replaceFragment(new ChooseReposFragment());
    }

    private void chooseOrganizationOrShowPullRequests() {
        if (shouldChooseOrganization()) {
            chooseOrganization();
        } else {
            showPullRequests();
        }
    }

    private void chooseOrganization() {
        replaceFragment(new ChooseOrganizationFragment());
    }

    private boolean shouldChooseOrganization() {
        String allRepos = getString(R.string.pref_repos_all);
        String reposToDisplay = preferences.getString(getString(R.string.pref_repos), null);
        return reposToDisplay != null && reposToDisplay.equals(allRepos);
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
