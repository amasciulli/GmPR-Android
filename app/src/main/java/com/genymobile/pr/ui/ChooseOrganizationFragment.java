package com.genymobile.pr.ui;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.genymobile.pr.R;

public class ChooseOrganizationFragment extends Fragment {
    private Callbacks fragmentCallbacks;
    private EditText organizationText;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.onCreate(savedInstanceState);
        if (!(getActivity() instanceof Callbacks)) {
            throw new IllegalStateException("Parent activity must implement " + Callbacks.class.getName());
        }
        fragmentCallbacks = (Callbacks) getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_choose_organization, container, false);

        organizationText = (EditText) root.findViewById(R.id.organization);

        Button chooseButton = (Button) root.findViewById(R.id.choose);
        chooseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveOrganization();
                showPullRequests();
            }
        });

        return root;
    }

    private void saveOrganization() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        preferences.edit()
                .putString(getString(R.string.pref_organization), organizationText.getText().toString())
                .commit();
    }

    private void showPullRequests() {
        fragmentCallbacks.onOrganizationChosen();
    }

    interface Callbacks {
        void onOrganizationChosen();
    }
}
