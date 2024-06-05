package com.example.mifilms;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class FavoritesFragment extends Fragment {

    private RecyclerView favoritesRecyclerView;
    private FilmAdapter filmAdapter;
    private List<Film> favoritesList;

    public FavoritesFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_favorites, container, false);
        favoritesRecyclerView = rootView.findViewById(R.id.favorites_recycler_view);
        favoritesRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Button backButton = view.findViewById(R.id.back_button);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment = new ProfileLogged();
                requireActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, fragment)
                        .commit();
            }
        });

        loadFavorites();
    }

    private void loadFavorites() {
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        favoritesList = new ArrayList<>();
        for (String key : sharedPreferences.getAll().keySet()) {
            if (key.startsWith("isFavorite_") && sharedPreferences.getBoolean(key, false)) {
                String filmTitle = key.replace("isFavorite_", "");

                String filmName = sharedPreferences.getString(filmTitle + "_name", "");
                String filmImagePath = sharedPreferences.getString(filmTitle + "_imagePath", "");
                Log.e("OWIHFE", "FilmName" + filmName + "FEFE");
                Film film = new Film();
                film.setTitle(filmName);
                film.setImg_src(filmImagePath);

                // Добавляем фильм в список избранных
                favoritesList.add(film);
            }
        }

        // Установка адаптера для RecyclerView
        filmAdapter = new FilmAdapter(favoritesList, getContext(), new FilmAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Film film) {
                // Обработка нажатия на элемент списка
            }
        });
        favoritesRecyclerView.setAdapter(filmAdapter);
    }
}
