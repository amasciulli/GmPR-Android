package com.genymobile.pr.ui;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;

import com.genymobile.pr.R;

public class MainActivity extends AppCompatActivity implements OnboardingFragment.Callbacks {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Fragment fragment;
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        if (preferences.contains(getString(R.string.pref_login)) &&
                preferences.contains(getString(R.string.pref_password))) {
            fragment = new PullRequestListFragment();
        } else {
            fragment = new OnboardingFragment();
        }

        replaceFragment(fragment);
    }

    @Override
    public void onboardingComplete() {
        replaceFragment(new PullRequestListFragment());
    }

    private void replaceFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, fragment)
                .commit();
    }
}
