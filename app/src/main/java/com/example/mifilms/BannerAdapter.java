package com.example.mifilms;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class BannerAdapter extends RecyclerView.Adapter<BannerAdapter.ViewHolder> {

    private List<Film> films;
    private Context context;

    public BannerAdapter(List<Film> films, Context context) {
        this.films = films;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.banner_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Film film = films.get(position);
        holder.bind(film);
    }

    @Override
    public int getItemCount() {
        return films.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView posterImageView;
        private TextView filmNameTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            posterImageView = itemView.findViewById(R.id.bannerposter);
            filmNameTextView = itemView.findViewById(R.id.bannertext);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Переход к деталям фильма при нажатии на баннер
                    Film film = films.get(getAdapterPosition());
                    Intent intent = new Intent(context, MainActivity2.class);
                    intent.putExtra("filmName", film.getTitle());
                    intent.putExtra("imgPath", film.getImg_src());
                    intent.putExtra("videoPath", film.getVide_src());
                    intent.putExtra("filmOpis", film.getDescription());
                    context.startActivity(intent);
                }
            });
        }

        public void bind(Film film) {
            filmNameTextView.setText(film.getTitle());
            Glide.with(context)
                    .load(film.getImg_src())
                    .into(posterImageView);
        }
    }
}
