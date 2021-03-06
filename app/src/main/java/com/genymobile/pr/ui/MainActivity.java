package com.genymobile.pr.ui;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;

import com.genymobile.pr.R;

public class MainActivity extends AppCompatActivity implements LoginFragment.Callbacks, ChooseOrganizationFragment.Callbacks {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Fragment fragment;
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);

        boolean credentialsSet = preferences.contains(getString(R.string.pref_login)) &&
                preferences.contains(getString(R.string.pref_password));
        boolean organizationSet = preferences.contains(getString(R.string.pref_organization));

        if (credentialsSet) {
            if (organizationSet) {
                fragment = new PullRequestListFragment();
            } else {
                fragment = new ChooseOrganizationFragment();
            }
        } else {
            fragment = new LoginFragment();
        }
        replaceFragment(fragment);
    }

    @Override
    public void onLoginComplete() {
        replaceFragment(new ChooseOrganizationFragment());
    }

    @Override
    public void onOrganizationChosen() {
        replaceFragment(new PullRequestListFragment());
    }

    private void replaceFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, fragment)
                .commit();
    }
}
