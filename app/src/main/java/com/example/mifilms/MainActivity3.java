package com.example.mifilms;

import android.content.pm.ActivityInfo;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.VideoView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity3 extends AppCompatActivity {

    private VideoView videoView;
    private Button btnPlayPause, btnForward, btnRewind;
    private static final int HIDE_CONTROL_DELAY = 3000; // Задержка скрытия кнопок управления (в миллисекундах)
    private Runnable mHideControlsRunnable = new Runnable() {
        @Override
        public void run() {
            hideControls();
        }
    };
    private Handler mHideControlsHandler = new Handler();
    private int forwardTime = 10000; // 10 секунд
    private int backwardTime = 10000; // 10 секунд
    private SeekBar seekBar;
    private boolean isDragging = false;
    private int duration; // переместили объявление переменной сюда

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        EdgeToEdge.enable(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);

        videoView = findViewById(R.id.videoView);
        btnPlayPause = findViewById(R.id.btnPlayPause);
        btnForward = findViewById(R.id.btnForward);
        btnRewind = findViewById(R.id.btnRewind);

        seekBar = findViewById(R.id.seekBar);
        setupSeekBar();

        String videoUrl = "https://firebasestorage.googleapis.com/v0/b/mifilms-134eb.appspot.com/o/woah.mp4?alt=media&token=337fdcf6-4a61-4cef-b94f-8f417744444c";
        playVideo(videoUrl);

        btnPlayPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (videoView.isPlaying()) {
                    videoView.pause();
                } else {
                    videoView.start();
                }
            }
        });

        btnForward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int currentPosition = videoView.getCurrentPosition();
                if (currentPosition + forwardTime <= videoView.getDuration()) {
                    videoView.seekTo(currentPosition + forwardTime);
                } else {
                    videoView.seekTo(videoView.getDuration());
                }
            }
        });

        btnRewind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int currentPosition = videoView.getCurrentPosition();
                if (currentPosition - backwardTime >= 0) {
                    videoView.seekTo(currentPosition - backwardTime);
                } else {
                    videoView.seekTo(0);
                }
            }
        });

        videoView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleControlsVisibility();
            }
        });
    }

    private void toggleControlsVisibility() {
        if (btnPlayPause.getVisibility() == View.VISIBLE) {
            hideControls();
        } else {
            showControls();
            mHideControlsHandler.postDelayed(mHideControlsRunnable, HIDE_CONTROL_DELAY);
        }
    }

    private void showControls() {
        btnPlayPause.setVisibility(View.VISIBLE);
        btnForward.setVisibility(View.VISIBLE);
        btnRewind.setVisibility(View.VISIBLE);
        seekBar.setVisibility(View.VISIBLE);
    }

    private void hideControls() {
        btnPlayPause.setVisibility(View.INVISIBLE);
        btnForward.setVisibility(View.INVISIBLE);
        btnRewind.setVisibility(View.INVISIBLE);
        seekBar.setVisibility(View.INVISIBLE);
        mHideControlsHandler.removeCallbacks(mHideControlsRunnable);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mHideControlsHandler.removeCallbacks(mHideControlsRunnable);
    }

    private void playVideo(String videoUrl) {
        Uri videoUri = Uri.parse(videoUrl);
        videoView.setVideoURI(videoUri);
        videoView.start();
    }

    private void setupSeekBar() {
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    if (videoView != null && duration != 0) {
                        int newPosition = (progress * duration) / 100;
                        videoView.seekTo(newPosition);
                    }
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                isDragging = true;
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                isDragging = false;
                if (videoView != null && duration != 0) {
                    int progress = seekBar.getProgress();
                    int newPosition = (progress * duration) / 100;
                    // Проверяем, не находится ли текущая позиция на конце видео
                    if (newPosition != duration) {
                        videoView.seekTo(newPosition);
                    }
                }
            }
        });

        final Handler handler = new Handler();
        MainActivity3.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (!isDragging && videoView != null) {
                    int currentPosition = videoView.getCurrentPosition();
                    seekBar.setProgress(currentPosition);
                }
                handler.postDelayed(this, 1); // Обновление каждую секунду
            }
        });

        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                duration = videoView.getDuration(); // Здесь устанавливаем значение duration
                seekBar.setMax(duration);

                // Обновляем прогресс SeekBar каждые 100 миллисекунд
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        int currentPosition = videoView.getCurrentPosition();
                        seekBar.setProgress(currentPosition);
                        new Handler().postDelayed(this, 100);
                    }
                }, 100);
            }
        });
    }
}
