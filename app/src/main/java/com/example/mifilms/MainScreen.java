package com.example.mifilms;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

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

    private ListView filmsListView;
    private ArrayAdapter<String> filmsAdapter;
    private List<String> filmTitles;
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
        filmTitles = new ArrayList<>();
        filmsMap = new HashMap<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_main_screen, container, false);
        filmsListView = rootView.findViewById(R.id.my_list_view);

        filmsAdapter = new ArrayAdapter<>(rootView.getContext(), android.R.layout.simple_list_item_1, filmTitles);
        filmsListView.setAdapter(filmsAdapter);

        // Загрузка данных из Firebase Realtime Database
        loadFilmsFromDatabase();

        filmsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selectedTitle = filmTitles.get(position);
                Film selectedFilm = filmsMap.get(selectedTitle);

                if (selectedFilm != null) {
                    Intent intent = new Intent(getActivity(), MainActivity2.class);
                    intent.putExtra("filmName", selectedFilm.title);
                    intent.putExtra("imgPath", selectedFilm.img_src);
                    intent.putExtra("videoPath", selectedFilm.vide_src);
                    intent.putExtra("filmOpis", selectedFilm.description);
                    startActivity(intent);
                } else {
                    Log.e(TAG, "Selected film not found in filmsMap");
                }
            }
        });

        return rootView;
    }

    private void loadFilmsFromDatabase() {
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                filmTitles.clear();
                filmsMap.clear();

                for (DataSnapshot filmSnapshot : dataSnapshot.getChildren()) {
                    Film film = filmSnapshot.getValue(Film.class);
                    if (film != null) {
                        filmTitles.add(film.title);
                        filmsMap.put(film.title, film);
                    }
                }

                filmsAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, "Failed to load films from database", databaseError.toException());
            }
        });
    }
}
