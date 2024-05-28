package com.example.mifilms;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity2 extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private static final String TAG = "MainActivity2";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        EdgeToEdge.enable(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        mAuth = FirebaseAuth.getInstance();
        // Получаем данные о фильме
        String filmName = getIntent().getStringExtra("filmName");
        String imgPath = getIntent().getStringExtra("imgPath");
        String videoPath = getIntent().getStringExtra("videoPath");
        String filmDesr = getIntent().getStringExtra("filmOpis");

        // Логируем полученные данные
        Log.d("RTCVD", "Received filmName: " + filmName);
        Log.d("RTCVD", "Received imgPath: " + imgPath);
        Log.d("RTCVD", "Received videoPath: " + videoPath);

        // Передаем данные фрагменту
        Bundle bundle = new Bundle();
        bundle.putString("filmName", filmName);
        bundle.putString("imgPath", imgPath);
        bundle.putString("videoPath", videoPath);
        bundle.putString("filmOpis", filmDesr);

        // Создаем и отображаем фрагмент
        MovieDetailsFragment fragment = new MovieDetailsFragment();
        fragment.setArguments(bundle);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
        transaction.commit();

        // Устанавливаем обработчик нажатия на кнопку в фрагменте
        getSupportFragmentManager().executePendingTransactions(); // Убедитесь, что транзакция выполнена
        fragment.setOnPlayButtonClickListener(new MovieDetailsFragment.OnPlayButtonClickListener() {
            @Override
            public void onPlayButtonClick() {
                checkAuthentication();
                FirebaseUser currentUser = mAuth.getCurrentUser();
                if (currentUser != null) {
                    Intent intent = new Intent(MainActivity2.this, MainActivity3.class);
                    intent.putExtra("videoPath", videoPath);
                    startActivity(intent);
                } else {
                    Snackbar.make(findViewById(R.id.fragment_container), "Пожалуйста, войдите в аккаунт, чтобы смотреть фильмы", Snackbar.LENGTH_LONG).show();
                }
            }
        });
    }

    private void checkAuthentication() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        // Здесь вы можете добавить дополнительные действия, если пользователь вошел в систему
    }
}
