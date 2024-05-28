package com.example.mifilms;

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
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;

public class MovieDetailsFragment extends Fragment {

    private static final String TAG = "MovieDetailsFragment";

    public interface OnPlayButtonClickListener {
        void onPlayButtonClick();
    }

    private OnPlayButtonClickListener listener;
    private ImageView imageView;
    private TextView filmNameView;
    private TextView filmOpisView;

    private Bitmap gradientBitmap;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_movie_details, container, false);

        imageView = view.findViewById(R.id.poster);
        filmNameView = view.findViewById(R.id.filmNameView);
        filmOpisView = view.findViewById(R.id.filmOpisView);

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
}
