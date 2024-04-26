package com.example.mifilms;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class SimpleAdapter extends RecyclerView.Adapter<SimpleAdapter.ViewHolder> {
    private List<String> items;
    private OnItemClickListener listener;

    // Интерфейс для слушателя кликов на кнопке
    public interface OnItemClickListener {
        void onItemClick(String position);
    }


    // Конструктор для передачи данных и слушателя
    public SimpleAdapter(List<String> items, OnItemClickListener listener) {
        this.items = items;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);
        return new ViewHolder(view);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String item = items.get(position);
        holder.textView.setText(item);

        // Установка слушателя на кнопку в элементе списка
        holder.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int adapterPosition = holder.getAdapterPosition();
                if (adapterPosition != RecyclerView.NO_POSITION && listener != null) {
                    listener.onItemClick(String.valueOf(adapterPosition));
                }
            }
        });
    }


    @Override
    public int getItemCount() {
        return items.size();
    }

    // ViewHolder содержит ссылки на элементы макета
    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textView;
        Button button;

        ViewHolder(View view) {
            super(view);
            textView = view.findViewById(R.id.item_text);
            button = view.findViewById(R.id.menu_button); // Предполагается, что у вас есть кнопка в макете list_item.xml
        }
    }
}
