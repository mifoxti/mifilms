package com.example.mifilms;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Arrays;
import java.util.concurrent.ExecutionException;


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
        // scanFirebaseStorage();

        NavigationBarView navigationView = findViewById(R.id.bottom_navigation);
        navigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                Fragment fragment;
                if (item.getItemId() == R.id.home) {
                    fragment = new MainScreen();
                    StorageReference storageRef = mStorage.child("Barbie.jpeg");
                    storageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            Log.d("uriuri", uri.toString());
                        }
                    });
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
        DatabaseReference filmsDatabaseRef = mDatabase.child("films");

        // Сканируем все файлы в корневой директории Firebase Storage
        mStorage.listAll().addOnSuccessListener(listResult -> {
            for (StorageReference item : listResult.getItems()) {
                String fileName = item.getName();
                if (fileName.endsWith("_mp4")) {
                    // Получаем имя файла без расширения
                    String filmId = fileName.substring(0, fileName.lastIndexOf('_'));

//                    // Создаем объект Film только с title, img_src и vide_src
//                    Film film = new Film("Название", item.getPath(), );
//                    film.setTitle(filmId);
//                    film.setImgSrc(""); // Пустая строка для img_src
//                    film.setVideoSrc(item.getPath());

                    // Добавляем фильм в базу данных
//                    filmsDatabaseRef.child(filmId).setValue(film);
                }
            }
        }).addOnFailureListener(e -> {
            Log.e("MainActivity", "Ошибка при сканировании Firebase Storage: " + e.getMessage());
        });
    }
}
