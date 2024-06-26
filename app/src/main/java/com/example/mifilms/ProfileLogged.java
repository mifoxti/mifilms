package com.example.mifilms;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ProfileLogged extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String PREFS_NAME = "MyPrefs";
    private static final String KIDS_MODE_KEY = "kids_mode";

    private String mParam1;
    private String mParam2;

    private FirebaseAuth mAuth;
    private FirebaseUser mUser;

    public ProfileLogged() {}

    public static ProfileLogged newInstance(String param1, String param2) {
        ProfileLogged fragment = new ProfileLogged();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile_logged, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Button logoutBtn = view.findViewById(R.id.logoutBtn);
        CheckBox kidsModeCheckBox = view.findViewById(R.id.kidsmode);
        Button showFavoritesBtn = view.findViewById(R.id.showFavoritesBtn);
        TextView helloTxt = view.findViewById(R.id.helloTxt);

        // Set welcome text
        if (mUser != null) {
            helloTxt.setText("Здравствуйте, " + mUser.getEmail());
        }

        // Load saved state
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        boolean isKidsModeEnabled = sharedPreferences.getBoolean(KIDS_MODE_KEY, false);
        kidsModeCheckBox.setChecked(isKidsModeEnabled);

        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.signOut();
                Fragment fragment = new Profile();
                requireActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, fragment)
                        .commit();
            }
        });

        kidsModeCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            // Save state
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean(KIDS_MODE_KEY, isChecked);
            editor.apply();

            Fragment mainScreenFragment = getParentFragmentManager().findFragmentByTag("mainScreen");
            if (mainScreenFragment instanceof MainScreen) {
                ((MainScreen) mainScreenFragment).setKidsModeEnabled(isChecked);
            }
        });

        showFavoritesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment = new FavoritesFragment();
                requireActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, fragment)
                        .commit();
            }
        });
    }
}
