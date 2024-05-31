package com.example.mifilms;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class FilmAdapter extends RecyclerView.Adapter<FilmAdapter.ViewHolder> {

    private List<Film> films;
    private Context context;
    private OnItemClickListener listener;
    private DatabaseReference userDatabase;
    private String userId;

    public FilmAdapter(List<Film> films, Context context, OnItemClickListener listener) {
        this.films = films;
        this.context = context;
        this.listener = listener;

        // Initialize Firebase references
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser != null) {
            userId = currentUser.getUid();
            userDatabase = FirebaseDatabase.getInstance().getReference("User").child(userId).child("playlists");
        } else {
            // Handle the case when the current user is null (e.g., not authenticated)
            // You can add your custom logic here
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
        holder.bind(film, listener);

        // Установка состояния кнопки "Любимое" на основе сохраненного состояния
        holder.updateLoveButtonIcon();
    }

    @Override
    public int getItemCount() {
        return films.size();
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
                        updateLoveButtonIcon();
                        handleLoveButtonClick(films.get(getAdapterPosition()));
                    }
                });
            } else {
                loveButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Snackbar.make(itemView,  "Пожалуйста, войдите в аккаунт, чтобы добавлять фильмы в \"Любимое\"", Snackbar.LENGTH_LONG).show();
                    }
                });
            }
        }

        public void bind(final Film film, final OnItemClickListener listener) {
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

            // Load the initial state of the love button (for example, check if the film is already in the favorites list)
            if (currentUser != null) {
                loadLoveButtonState(film);
            }
        }

        private void loadLoveButtonState(Film film) {
            // Assuming that there's a method in your Film class to check if it's a favorite
            isLoved = isFilmFavorite(film);
            updateLoveButtonIcon();
        }

        private void updateLoveButtonIcon() {
            // Update the button's icon based on the current state
            // Uncomment and modify this code according to your icon requirements
//            if (isLoved) {
//                loveButton.setBackgroundResource(R.drawable.ic_heart_filled);
//            } else {
//                loveButton.setBackgroundResource(R.drawable.ic_heart);
//            }

        }

        private boolean isFilmFavorite(Film film) {
            // Implement this method to check if the film is in the user's favorites list
            // You can use SharedPreferences or a database query to determine this
            SharedPreferences sharedPreferences = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
            return sharedPreferences.getBoolean(film.getTitle(), false);
        }

        private void handleLoveButtonClick(Film film) {
            // Update user's favorite list in Firebase
            if (isLoved) {
                addToFavorites(film);
            } else {
                removeFromFavorites(film);
            }
        }

        private void addToFavorites(Film film) {
            // Implement the logic to add the film to the user's favorites in Firebase
            userDatabase.child(film.getTitle()).setValue(true);
            // Save the state in SharedPreferences
            SharedPreferences sharedPreferences = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean(film.getTitle(), true);
            editor.apply();
        }

        private void removeFromFavorites(Film film) {
            // Implement the logic to remove the film from the user's favorites in Firebase
            userDatabase.child(film.getTitle()).removeValue();
            // Save the state in SharedPreferences
            SharedPreferences sharedPreferences = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean(film.getTitle(), false);
            editor.apply();
        }
    }
}
