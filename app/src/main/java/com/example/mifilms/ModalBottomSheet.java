package com.example.mifilms;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Button;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ModalBottomSheet extends BottomSheetDialogFragment {

    private SimpleAdapter adapter;
    private List<String> items;
    public static final String TAG = "ModalBottomSheet";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.modal_bottom_sheet_content, container, false);
        String[] playlists = getResources().getStringArray(R.array.playlists);
        items = new ArrayList<>(Arrays.asList(playlists));

        RecyclerView recyclerView = rootView.findViewById(R.id.list);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        adapter = new SimpleAdapter(items, new SimpleAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(String position) {
                int pos = Integer.parseInt(position);
                showDeleteDialog(pos);
            }
        });

        Button newPlaylist = rootView.findViewById(R.id.newPlaylist);

        newPlaylist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showNewPlaylistDialog();
            }
        });

        recyclerView.setAdapter(adapter);

        return rootView;
    }

    private void showNewPlaylistDialog() {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(getContext());
        builder.setTitle(getContext().getResources().getString(R.string.titleNewPlaylist));
        builder.setMessage(getContext().getResources().getString(R.string.supporting_textNewPlaylist));

        final EditText editText = new EditText(getContext());
        builder.setView(editText);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String inputText = editText.getText().toString();
                items.add(inputText);
                adapter.notifyDataSetChanged();
            }
        });

            builder.show();
    }

    private void showDeleteDialog(int position) {
        new MaterialAlertDialogBuilder(getContext())
                .setTitle(getContext().getResources().getString(R.string.title))
                .setMessage(getContext().getResources().getString(R.string.supporting_text))
                .setNegativeButton(getContext().getResources().getString(R.string.decline), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Respond to negative button press
                    }
                })
                .setPositiveButton(getContext().getResources().getString(R.string.accept), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        items.remove(position);
                        adapter.notifyItemRemoved(position);
                    }
                })
                .show();
    }
}
