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
            replaceFragment(new LoginFragment());
        } else if (!organizationSet) {
            replaceFragment(new ChooseOrganizationFragment());
        } else if (!reposChosen) {
            replaceFragment(new ChooseReposFragment());
        } else {
            replaceFragment(new PullRequestListFragment());
        }
    }

    @Override
    public void onLoginComplete() {
        replaceFragment(new ChooseOrganizationFragment());
    }

    @Override
    public void onOrganizationChosen() {
        replaceFragment(new ChooseReposFragment());
    }

    @Override
    public void onReposChosen() {
        replaceFragment(new PullRequestListFragment());
    }

    private void replaceFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, fragment)
                .commit();
    }
}
