package com.example.mifilms;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class MainActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        EdgeToEdge.enable(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();

        // Проверяем аутентификацию пользователя
        checkAuthentication();

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
        // Здесь вы можете добавить дополнительные действия, если пользователь вошел в систему, например, обновление данных пользователя или выполнение других задач
    }
}
