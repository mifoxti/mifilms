package com.example.mifilms;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class FilmAdapter extends RecyclerView.Adapter<FilmAdapter.ViewHolder> {

    private List<Film> films;
    private Context context;
    private OnItemClickListener listener;

    public FilmAdapter(List<Film> films, Context context, OnItemClickListener listener) {
        this.films = films;
        this.context = context;
        this.listener = listener;
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

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            posterImageView = itemView.findViewById(R.id.galleryposter);
            filmNameTextView = itemView.findViewById(R.id.gallerytext);
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
        }
    }
}

