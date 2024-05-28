package com.example.mifilms;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
import java.util.HashMap;
import java.util.List;

public class MainScreen extends Fragment {

    private static final String TAG = "MainScreen";

    private RecyclerView filmsRecyclerView;
    private FilmAdapter filmAdapter;
    private List<Film> filmsList;
    private HashMap<String, Film> filmsMap;
    private DatabaseReference mDatabase;

    public MainScreen() {
        // Required empty public constructor
    }

    public static MainScreen newInstance(String param1, String param2) {
        MainScreen fragment = new MainScreen();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mDatabase = FirebaseDatabase.getInstance().getReference("films");
        filmsList = new ArrayList<>();
        filmsMap = new HashMap<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main_screen, container, false);
        filmsRecyclerView = rootView.findViewById(R.id.my_recycler_view);
        filmsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Создание адаптера с использованием списка фильмов filmsList и обработчика нажатий listener
        filmAdapter = new FilmAdapter(filmsList, getContext(), new FilmAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Film film) {
                // Handle item click
                Intent intent = new Intent(getActivity(), MainActivity2.class);
                intent.putExtra("filmName", film.getTitle());
                intent.putExtra("imgPath", film.getImg_src());
                intent.putExtra("videoPath", film.getVide_src());
                intent.putExtra("filmOpis", film.getDescription());
                startActivity(intent);
            }
        });
        filmsRecyclerView.setAdapter(filmAdapter);

        // Загрузка данных из Firebase Realtime Database
        loadFilmsFromDatabase();

        return rootView;
    }

    private void loadFilmsFromDatabase() {
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                filmsList.clear();
                filmsMap.clear();

                for (DataSnapshot filmSnapshot : dataSnapshot.getChildren()) {
                    Film film = filmSnapshot.getValue(Film.class);
                    if (film != null) {
                        filmsList.add(film);
                        filmsMap.put(film.getTitle(), film);
                    }
                }

                filmAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, "Failed to load films from database", databaseError.toException());
            }
        });
    }
}
