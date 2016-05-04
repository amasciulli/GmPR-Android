package com.genymobile.pr.ui;

import com.genymobile.pr.R;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;

public class ChooseReposFragment extends Fragment {
    private Callbacks fragmentCallbacks;
    private RadioGroup reposGroup;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!(getActivity() instanceof Callbacks)) {
            throw new IllegalStateException("Parent activity must implement " + Callbacks.class.getName());
        }
        fragmentCallbacks = (Callbacks) getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_choose_repos, container, false);

        Button goButton = (Button) root.findViewById(R.id.go);
        reposGroup = (RadioGroup) root.findViewById(R.id.repos);
        RadioButton allReposButton = (RadioButton) root.findViewById(R.id.all_repos);

        allReposButton.setText(getString(R.string.all_repos));

        goButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveChosenRepos();
                goToNextStep();
            }
        });

        return root;
    }

    private void saveChosenRepos() {
        int reposResource = reposGroup.getCheckedRadioButtonId() == R.id.watched ? R.string.pref_repos_watched : R.string.pref_repos_all;
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        preferences.edit()
                .putString(getString(R.string.pref_repos), getString(reposResource))
                .apply();
    }

    private void goToNextStep() {
        fragmentCallbacks.onReposChosen();
    }

    interface Callbacks {
        void onReposChosen();
    }
}
