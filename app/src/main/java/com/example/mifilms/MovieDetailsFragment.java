package com.example.mifilms;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ComposeShader;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.Shader;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MovieDetailsFragment extends Fragment {

    private static final String TAG = "MovieDetailsFragment";

    public interface OnPlayButtonClickListener {
        void onPlayButtonClick();
    }

    private OnPlayButtonClickListener listener;
    private ImageView imageView;
    private TextView filmNameView;
    private TextView filmOpisView;
    private MaterialButton loveButton;
    private boolean isLoved;
    private DatabaseReference userDatabase;
    private String userId;
    private FirebaseUser currentUser;

    private Bitmap gradientBitmap;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_movie_details, container, false);

        imageView = view.findViewById(R.id.poster);
        filmNameView = view.findViewById(R.id.filmNameView);
        filmOpisView = view.findViewById(R.id.filmOpisView);
        loveButton = view.findViewById(R.id.galleryBtnLove);

        // Получаем текущего пользователя
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            userId = currentUser.getUid();
            userDatabase = FirebaseDatabase.getInstance().getReference("User").child(userId).child("playlists");
        }

        // Получаем данные о фильме
        Bundle bundle = getArguments();
        if (bundle != null) {
            String filmName = bundle.getString("filmName");
            String imgPath = bundle.getString("imgPath");
            String filmDesr = bundle.getString("filmOpis");

            // Логируем данные из бандла
            Log.d(TAG, "Received filmName from bundle: " + filmName);
            Log.d(TAG, "Received imgPath from bundle: " + imgPath);

            if (filmName != null && imgPath != null) {
                // Загрузка изображения с помощью Glide
                Glide.with(this)
                        .asBitmap()
                        .load(imgPath)
                        .into(new CustomTarget<Bitmap>() {
                            @Override
                            public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                                // Применяем градиент поверх загруженного изображения
                                ConstraintLayout constraintLayout = getView().findViewById(R.id.fragmentMovie);
                                Drawable background = constraintLayout.getBackground();
                                ColorDrawable colorDrawable = (ColorDrawable) background;

                                // Получаем цвет фона в формате Color
                                int color = colorDrawable.getColor();
                                gradientBitmap = addGradient(resource, color);
                                imageView.setImageBitmap(gradientBitmap);
                            }

                            @Override
                            public void onLoadCleared(@Nullable Drawable placeholder) {
                                // Implement if needed
                            }
                        });

                filmNameView.setText(filmName);
                filmOpisView.setText(filmDesr);

                if (currentUser != null) {
                    loadLoveButtonState(filmName);
                }

                loveButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (currentUser != null) {
                            isLoved = !isLoved;
                            updateLoveButtonIcon();
                            handleLoveButtonClick(new Film(filmName, imgPath, filmDesr));  // Assuming NFK value is false
                        } else {
                            Snackbar.make(view, "Пожалуйста, войдите в аккаунт, чтобы добавлять фильмы в \"Любимое\"", Snackbar.LENGTH_LONG).show();
                        }
                    }
                });
            } else {
                Log.e(TAG, "filmName or imgPath is null");
            }
        } else {
            Log.e(TAG, "Bundle is null");
        }

        // Устанавливаем слушатель на кнопку playButton
        Button playButton = view.findViewById(R.id.playButton);
        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onPlayButtonClick();
                }
            }
        });

        return view;
    }

    public void setOnPlayButtonClickListener(OnPlayButtonClickListener listener) {
        this.listener = listener;
    }

    /**
     * Adds a gradient effect to the input bitmap.
     */
    public Bitmap addGradient(Bitmap src, int color) {
        int w = src.getWidth();
        int h = src.getHeight();
        Bitmap result = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(result);

        Paint paint = new Paint();
        BitmapShader shader = new BitmapShader(src, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
        LinearGradient gradient = new LinearGradient(0, 0, 0, h, Color.TRANSPARENT, color, Shader.TileMode.CLAMP);
        paint.setShader(new ComposeShader(shader, gradient, PorterDuff.Mode.SRC_OVER));

        canvas.drawRect(0, 0, w, h, paint);

        return result;
    }

    private void loadLoveButtonState(String filmName) {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        isLoved = sharedPreferences.getBoolean("isFavorite_" + filmName, false);
        updateLoveButtonIcon();
    }

    private void updateLoveButtonIcon() {
        if (isLoved) {
            Drawable icon = ContextCompat.getDrawable(getContext(), R.drawable.ic_heart_filled);
            loveButton.setIcon(icon);
        } else {
            Drawable icon = ContextCompat.getDrawable(getContext(), R.drawable.ic_heart);
            loveButton.setIcon(icon);
        }
    }

    private void handleLoveButtonClick(Film film) {
        if (isLoved) {
            addToFavorites(film);
        } else {
            removeFromFavorites(film);
        }
    }

    private void addToFavorites(Film film) {
        userDatabase.child(film.getTitle()).setValue(film);
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("isFavorite_" + film.getTitle(), true);
        editor.putString(film.getTitle() + "_name", film.getTitle()); // Сохраняем название фильма
        editor.putString(film.getTitle() + "_imagePath", film.getImg_src()); // Сохраняем путь к изображению
        editor.apply();
    }

    private void removeFromFavorites(Film film) {
        userDatabase.child(film.getTitle()).removeValue();
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("isFavorite_" + film.getTitle(), false);
        editor.remove(film.getTitle() + "_name"); // Удаляем сохраненное название фильма
        editor.remove(film.getTitle() + "_imagePath"); // Удаляем сохраненный путь к изображению
        editor.apply();
    }
}
