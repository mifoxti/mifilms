package com.example.mifilms;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private StorageReference mStorage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        EdgeToEdge.enable(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference("films");
        mStorage = FirebaseStorage.getInstance().getReference(); // Получаем ссылку на корневую папку

        // Проверяем аутентификацию пользователя
        checkAuthentication();

        // Вызываем метод для сканирования Firebase Storage и отправки фильмов в Realtime Database
        scanFirebaseStorage();

        NavigationBarView navigationView = findViewById(R.id.bottom_navigation);
        navigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                Fragment fragment;
                if (item.getItemId() == R.id.home) {
                    fragment = new MainScreen();
                } else if (item.getItemId() == R.id.search) {
                    fragment = new Search();
                } else if (item.getItemId() == R.id.profile) {
                    // Проверяем аутентификацию пользователя
                    checkAuthentication();
                    FirebaseUser currentUser = mAuth.getCurrentUser();
                    if (currentUser != null) {
                        // Пользователь вошел в систему, открываем профиль
                        fragment = new ProfileLogged();
                    } else {
                        fragment = new Profile();
                    }
                } else {
                    return false;
                }
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment).commit();
                return true;
            }
        });

        // Создаем и отображаем фрагмент по умолчанию
        MainScreen mainScreenFragment = new MainScreen();
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, mainScreenFragment).commit();
    }

    // Метод для проверки аутентификации пользователя
    private void checkAuthentication() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        // Здесь вы можете добавить дополнительные действия, если пользователь вошел в систему
    }

    private void scanFirebaseStorage() {
        // Получаем ссылку на место в Realtime Database для сохранения фильмов
        DatabaseReference filmsDatabaseRef = mDatabase;

        // Сканируем все файлы в корневой директории Firebase Storage
        mStorage.listAll().addOnSuccessListener(listResult -> {
            Map<String, String> jpegMap = new HashMap<>();
            Map<String, String> mp4Map = new HashMap<>();

            for (StorageReference item : listResult.getItems()) {
                String fileName = item.getName();
                String fileBaseName = fileName.substring(0, fileName.lastIndexOf('.'));
                String fileExtension = fileName.substring(fileName.lastIndexOf('.') + 1);

                Log.d("scanFirebaseStorage", "Found file: " + fileName);

                item.getDownloadUrl().addOnSuccessListener(uri -> {
                    if (fileExtension.equals("jpeg") || fileExtension.equals("jpg")) {
                        jpegMap.put(fileBaseName, uri.toString());
                    } else if (fileExtension.equals("mp4")) {
                        mp4Map.put(fileBaseName, uri.toString());
                    }

                    if (jpegMap.containsKey(fileBaseName) && mp4Map.containsKey(fileBaseName)) {
                        String imgSrc = jpegMap.get(fileBaseName);
                        String videoSrc = mp4Map.get(fileBaseName);

                        // Проверяем наличие записи с таким же именем файла
                        checkIfFilmExists(fileBaseName, new OnFilmExistenceCheckListener() {
                            @Override
                            public void onFilmExistenceChecked(boolean exists) {
                                if (!exists) {
                                    // Создаем объект Film
                                    Film film = new Film(fileBaseName, imgSrc, videoSrc);

                                    // Добавляем фильм в базу данных
                                    filmsDatabaseRef.child(fileBaseName).setValue(film)
                                            .addOnCompleteListener(task -> {
                                                if (task.isSuccessful()) {
                                                    Log.d("FirebaseDatabase", "Film added successfully");
                                                } else {
                                                    Log.e("FirebaseDatabase", "Error adding film", task.getException());
                                                }
                                            });
                                } else {
                                    Log.d("FirebaseDatabase", "Film already exists: " + fileBaseName);
                                }
                            }
                        });
                    }
                }).addOnFailureListener(e -> {
                    Log.e("FirebaseStorage", "Error getting download URL", e);
                });
            }
        }).addOnFailureListener(e -> {
            Log.e("MainActivity", "Ошибка при сканировании Firebase Storage: " + e.getMessage());
        });
    }

    private void checkIfFilmExists(String fileBaseName, OnFilmExistenceCheckListener listener) {
        mDatabase.child(fileBaseName).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                listener.onFilmExistenceChecked(dataSnapshot.exists());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("FirebaseDatabase", "Error checking film existence", databaseError.toException());
            }
        });
    }

    interface OnFilmExistenceCheckListener {
        void onFilmExistenceChecked(boolean exists);
    }
}
