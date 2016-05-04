package com.genymobile.pr.ui;

import com.genymobile.pr.R;
import com.genymobile.pr.bus.BusProvider;
import com.genymobile.pr.bus.SettingsUpdatedEvent;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.widget.Toast;

public class SettingsFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PreferenceManager.getDefaultSharedPreferences(getActivity()).registerOnSharedPreferenceChangeListener(this);
        addPreferencesFromResource(R.xml.preferences);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        BusProvider.getInstance().post(new SettingsUpdatedEvent());
        if (getActivity() == null) {
            return;
        }

        if (key.equals(getString(R.string.pref_repos))) {
            String repos = sharedPreferences.getString(key, null);
            if (repos == null) {
                return;
            }

            if (repos.equals(getString(R.string.pref_repos_all))) {
                Toast.makeText(getActivity(), R.string.toast_update_organization, Toast.LENGTH_LONG).show();
            }
        }
    }
}
