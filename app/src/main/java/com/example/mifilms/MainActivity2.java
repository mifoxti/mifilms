package com.example.mifilms;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;


public class MainActivity2 extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        EdgeToEdge.enable(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        // Получаем данные о фильме
        String filmName = getIntent().getStringExtra("filmName");
        String imgPath = getIntent().getStringExtra("imgPath");

        // Передаем данные фрагменту
        Bundle bundle = new Bundle();
        bundle.putString("filmName", filmName);
        bundle.putString("imgPath", imgPath);


        // Создаем и отображаем фрагмент
        MovieDetailsFragment fragment = new MovieDetailsFragment();
        fragment.setArguments(bundle);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
        transaction.commit();


//        ExtendedFloatingActionButton extendedFab = findViewById(R.id.extended_fab);
//        extendedFab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                ModalBottomSheet modalBottomSheet = new ModalBottomSheet();
//                modalBottomSheet.show(getSupportFragmentManager(), ModalBottomSheet.TAG);
//            }
//        });

        if (fragment != null) {
            // Устанавливаем обработчик нажатия на кнопку в фрагменте
            fragment.setPlayButtonClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(MainActivity2.this, MainActivity3.class);
                    startActivity(intent);
                }
            });
        }
    }
}


