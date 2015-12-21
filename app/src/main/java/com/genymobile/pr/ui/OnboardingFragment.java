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

public class OnboardingFragment extends Fragment {
    private EditText loginText;
    private EditText passwordText;
    private Callbacks fragmentCallbacks;

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
        View root = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_onboarding, container, false);

        loginText = (EditText) root.findViewById(R.id.login);
        passwordText = (EditText) root.findViewById(R.id.password);

        Button saveButton = (Button) root.findViewById(R.id.save);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveCredentials();
                showPullRequests();
            }
        });

        return root;
    }

    private void saveCredentials() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        preferences.edit()
                .putString(getString(R.string.pref_login), loginText.getText().toString())
                .putString(getString(R.string.pref_password), passwordText.getText().toString())
                .commit();
    }

    private void showPullRequests() {
        fragmentCallbacks.onboardingComplete();
    }

    interface Callbacks {
        void onboardingComplete();
    }
}
