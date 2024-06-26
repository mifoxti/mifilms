package com.example.mifilms;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class Search extends Fragment {

    private static final String PREFS_NAME = "MyPrefs";
    private static final String KIDS_MODE_KEY = "kids_mode";

    private SearchView searchView;
    private RecyclerView searchResultsRecyclerView;
    private FilmAdapter filmAdapter;
    private List<Film> allFilms;
    private List<Film> filteredFilms;
    private boolean isChildModeEnabled;
    private DatabaseReference mDatabase;

    public Search() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        searchView = view.findViewById(R.id.search_view);
        searchResultsRecyclerView = view.findViewById(R.id.search_results_recycler_view);

        allFilms = new ArrayList<>();
        filteredFilms = new ArrayList<>();

        filmAdapter = new FilmAdapter(filteredFilms, getContext(), film -> {
            // Handle item click - переход к деталям фильма
            Intent intent = new Intent(getActivity(), MainActivity2.class);
            intent.putExtra("filmName", film.getTitle());
            intent.putExtra("imgPath", film.getImg_src());
            intent.putExtra("videoPath", film.getVide_src());
            intent.putExtra("filmOpis", film.getDescription());
            startActivity(intent);
        });

        searchResultsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        searchResultsRecyclerView.setAdapter(filmAdapter);

        // Load kids mode state
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        isChildModeEnabled = sharedPreferences.getBoolean(KIDS_MODE_KEY, false);

        mDatabase = FirebaseDatabase.getInstance().getReference("films");

        loadFilmsFromFirebase();

        setupSearch();

        return view;
    }

    private void setupSearch() {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                filterFilms(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterFilms(newText);
                return true;
            }
        });
    }

    private void loadFilmsFromFirebase() {
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                allFilms.clear();
                for (DataSnapshot filmSnapshot : snapshot.getChildren()) {
                    Film film = filmSnapshot.getValue(Film.class);
                    if (film != null && (!isChildModeEnabled || !film.isNfk())) {
                        allFilms.add(film);
                    }
                }
                filterFilms(searchView.getQuery().toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle possible errors.
            }
        });
    }

    private void filterFilms(String query) {
        filteredFilms.clear();
        if (query.isEmpty()) {
            filmAdapter.notifyDataSetChanged();
            return;
        }

        for (Film film : allFilms) {
            if (film.getTitle().toLowerCase().contains(query.toLowerCase())) {
                filteredFilms.add(film);
            }
        }
        filmAdapter.notifyDataSetChanged();
    }

    public void setChildModeEnabled(boolean enabled) {
        isChildModeEnabled = enabled;
        loadFilmsFromFirebase();
    }
}
