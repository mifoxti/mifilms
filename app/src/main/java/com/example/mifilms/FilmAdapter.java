package com.example.mifilms;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.util.ArrayList;
import java.util.List;

public class FilmAdapter extends RecyclerView.Adapter<FilmAdapter.ViewHolder> {

    private List<Film> films;
    private Context context;
    private OnItemClickListener listener;
    private DatabaseReference userDatabase;
    private String userId;

    public FilmAdapter(List<Film> films, Context context, OnItemClickListener listener) {
        this.films = films != null ? films : new ArrayList<Film>();  // Проверка на null
        this.context = context;
        this.listener = listener;

        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser != null) {
            userId = currentUser.getUid();
            userDatabase = FirebaseDatabase.getInstance().getReference("User").child(userId).child("playlists");
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.gallery_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Film film = films.get(position);

        holder.bind(film, listener, holder.itemView);

        holder.updateLoveButtonIcon(holder.itemView);
    }

    @Override
    public int getItemCount() {
        return films.size();
    }

    public void setFilms(List<Film> films) {
        this.films = films != null ? films : new ArrayList<Film>();  // Проверка на null
        notifyDataSetChanged();
    }

    public interface OnItemClickListener {
        void onItemClick(Film film);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView posterImageView;
        private TextView filmNameTextView;
        private Button loveButton;
        private FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        private boolean isLoved;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            posterImageView = itemView.findViewById(R.id.galleryposter);
            filmNameTextView = itemView.findViewById(R.id.gallerytext);
            loveButton = itemView.findViewById(R.id.galleryBtnLove);

            if (currentUser != null) {
                loveButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        isLoved = !isLoved;
                        updateLoveButtonIcon(itemView);
                        handleLoveButtonClick(films.get(getAdapterPosition()));
                    }
                });
            } else {
                loveButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Snackbar.make(itemView, "Пожалуйста, войдите в аккаунт, чтобы добавлять фильмы в \"Любимое\"", Snackbar.LENGTH_LONG).show();
                    }
                });
            }
        }

        public void bind(final Film film, final OnItemClickListener listener, View itemView) {
            filmNameTextView.setText(film.getTitle());
            Glide.with(context)
                    .load(film.getImg_src())
                    .into(posterImageView);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(film);
                }
            });

            if (currentUser != null) {
                loadLoveButtonState(film);
            }
        }

        private void loadLoveButtonState(Film film) {
            isLoved = isFilmFavorite(film);
            updateLoveButtonIcon(itemView);
        }

        private void updateLoveButtonIcon(View itemView) {
            MaterialButton button = itemView.findViewById(R.id.galleryBtnLove);
            if (isLoved) {
                Drawable icon = ContextCompat.getDrawable(itemView.getContext(), R.drawable.ic_heart_filled);
                button.setIcon(icon);
            } else {
                Drawable icon = ContextCompat.getDrawable(itemView.getContext(), R.drawable.ic_heart);
                button.setIcon(icon);
            }
        }

        private boolean isFilmFavorite(Film film) {
            SharedPreferences sharedPreferences = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
            return sharedPreferences.getBoolean("isFavorite_" + film.getTitle(), false);
        }

        private void handleLoveButtonClick(Film film) {
            if (isLoved) {
                addToFavorites(film);
            } else {
                removeFromFavorites(film);
            }
        }

        private void addToFavorites(Film film) {
            // Добавляем фильм в плейлист пользователя
            userDatabase.child(film.getTitle()).setValue(film);

            // Записываем информацию о лайке в локальное хранилище
            SharedPreferences sharedPreferences = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("isFavorite_" + film.getTitle(), true);
            editor.putString(film.getTitle() + "_name", film.getTitle()); // Сохраняем название фильма
            editor.putString(film.getTitle() + "_imagePath", film.getImg_src()); // Сохраняем путь к изображению
            editor.apply();
        }

        private void removeFromFavorites(Film film) {
            // Удаляем фильм из плейлиста пользователя
            userDatabase.child(film.getTitle()).removeValue();

            // Удаляем информацию о лайке из локального хранилища
            SharedPreferences sharedPreferences = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("isFavorite_" + film.getTitle(), false);
            editor.remove(film.getTitle() + "_name"); // Удаляем сохраненное название фильма
            editor.remove(film.getTitle() + "_imagePath"); // Удаляем сохраненный путь к изображению
            editor.apply();
        }
    }
}
