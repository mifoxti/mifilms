package com.example.mifilms;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Profile extends Fragment {

    private FirebaseAuth mAuth;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mAuth = FirebaseAuth.getInstance();

        Button loginButton = view.findViewById(R.id.loginButton);
        TextInputEditText loginEdit = view.findViewById(R.id.loginText);
        TextInputEditText passEdit = view.findViewById(R.id.passwordText);

        InputMethodManager imm = (InputMethodManager) requireContext().getSystemService(Context.INPUT_METHOD_SERVICE);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String login = loginEdit.getText().toString();
                String pass = passEdit.getText().toString();
                if (login.isEmpty() || pass.isEmpty()) {
                    Snackbar.make(v, "Логин и пароль должны быть введены", Snackbar.LENGTH_LONG).show();
                } else {
                    mAuth.signInWithEmailAndPassword(login, pass)
                            .addOnCompleteListener(requireActivity(), task -> {
                                if (task.isSuccessful()) {
                                    // Вход выполнен успешно
                                    Snackbar.make(v, "Вход выполнен успешно", Snackbar.LENGTH_LONG).show();
                                    // Перезапускаем фрагмент, чтобы обновить его содержимое
                                    Fragment fragment = new ProfileLogged();
                                    requireActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment).commit();
                                } else {
                                    // Вход не удался
                                    Snackbar.make(v, "Ошибка при входе: " + task.getException().getMessage(), Snackbar.LENGTH_LONG).show();
                                }
                            });
                }
            }
        });

        Button registerButton = view.findViewById(R.id.registerButton);
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String login = loginEdit.getText().toString();
                String pass = passEdit.getText().toString();
                if (login.isEmpty() || pass.isEmpty()) {
                    Snackbar.make(v, "Логин и пароль должны быть введены", Snackbar.LENGTH_LONG).show();
                } else {
                    mAuth.createUserWithEmailAndPassword(login, pass)
                            .addOnCompleteListener(requireActivity(), task -> {
                                if (task.isSuccessful()) {
                                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                                    Snackbar.make(view, "Пользователь успешно зарегистрирован", Snackbar.LENGTH_SHORT).show();

                                    // Создание записи пользователя в базе данных
                                    String userId = mAuth.getCurrentUser().getUid(); // Получаем идентификатор текущего пользователя
                                    DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("User").child(userId);

                                    // Создаем объект пользователя и добавляем его в базу данных
                                    User newUser = new User(userId, login, pass);
                                    userRef.setValue(newUser);

                                    Fragment fragment = new ProfileLogged();
                                    requireActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment).commit();
                                } else {
                                    // Ошибка при регистрации
                                    Snackbar.make(view, "Ошибка при регистрации: " + task.getException().getMessage(), Snackbar.LENGTH_LONG).show();
                                }
                            });
                }
            }
        });
    }
}
