package com.example.mifilms;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.MediaController;
import android.widget.SeekBar;



public class CustomMediaController extends MediaController {
    private MediaPlayerControl player;
    private SeekBar seekbar;
    private OnSeekBarChangeListener seekBarChangeListener;

    public CustomMediaController(Context context) {
        super(context);
    }

    @Override
    public void setMediaPlayer(MediaPlayerControl player) {
        super.setMediaPlayer(player);
        this.player = player;
    }

    @Override
    public void setAnchorView(View view) {
        super.setAnchorView(view);

        seekbar = new SeekBar(getContext());
        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        int margin = getResources().getDimensionPixelSize(R.dimen.media_controller_seekbar_margin);
        lp.setMargins(margin, 0, margin, 0);
        seekbar.setLayoutParams(lp);
        addView(seekbar);

        // Привязываем слушатель изменения значения слайдера
        seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser && seekBarChangeListener != null) {
                    seekBarChangeListener.onProgressChanged(seekBar, progress, fromUser);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });
    }

    // Установка слушателя изменения значения слайдера
    public void setOnSeekBarChangeListener(OnSeekBarChangeListener listener) {
        this.seekBarChangeListener = listener;
    }

    // Интерфейс для слушателя изменения значения слайдера
    public interface OnSeekBarChangeListener {
        void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser);
    }
}

