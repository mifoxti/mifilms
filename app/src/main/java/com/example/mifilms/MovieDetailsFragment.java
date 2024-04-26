package com.example.mifilms;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ComposeShader;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
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


public class MovieDetailsFragment extends Fragment {

    private ImageView imageView;
    private TextView filmNameView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_movie_details, container, false);

        imageView = view.findViewById(R.id.poster);
        filmNameView = view.findViewById(R.id.filmNameView);

        // Получаем данные о фильме
        Bundle bundle = getArguments();
        if (bundle != null) {
            String filmName = bundle.getString("filmName");
            String imgPath = bundle.getString("imgPath");

            int imgSrcId = getResources().getIdentifier(imgPath, "drawable", requireActivity().getPackageName());

            imageView.setImageResource(imgSrcId);
            filmNameView.setText(filmName);
        }

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // Устанавливаем обработчик нажатия на кнопку playButton
        setPlayButtonClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Обработка нажатия на кнопку
                Intent intent = new Intent(getActivity(), MainActivity3.class);
                startActivity(intent);
            }
        });

        // Применяем градиент к изображению
        ConstraintLayout constraintLayout = getView().findViewById(R.id.fragmentMovie);
        Drawable background = constraintLayout.getBackground();
        if (background instanceof ColorDrawable) {
            // Приводим Drawable к типу ColorDrawable
            ColorDrawable colorDrawable = (ColorDrawable) background;

            // Получаем цвет фона в формате Color
            int color = colorDrawable.getColor();
            Log.d("MyTag", String.valueOf(color));
            applyGradientToImage(color);
        }
        else {
            Log.d("MyTag", "Smth went wrong");
        }
    }

    // Метод для установки обработчика нажатия на кнопку
    public void setPlayButtonClickListener(View.OnClickListener listener) {
        View view = getView();
        if (view != null) {
            Button playButton = view.findViewById(R.id.playButton);
            playButton.setOnClickListener(listener);
        }
    }

    /**
     * Applies gradient effect to the image.
     */
    private void applyGradientToImage(int color) {
        Bitmap originalBitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
        Bitmap gradientBitmap = addGradient(originalBitmap, color);
        imageView.setImageBitmap(gradientBitmap);
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
