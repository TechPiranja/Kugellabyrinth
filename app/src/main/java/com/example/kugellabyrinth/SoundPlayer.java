package com.example.kugellabyrinth;

import android.content.Context;
import android.media.MediaPlayer;

public class SoundPlayer {

    public MediaPlayer mediaPlayer;

    private static SoundPlayer instance;

    // Anwenden des Singelton Patterns
    public static SoundPlayer getInstance (Context context) {
        if (SoundPlayer.instance == null) {
            SoundPlayer.instance = new SoundPlayer(context);
        }
        return SoundPlayer.instance;
    }

    private SoundPlayer(Context context) {
        mediaPlayer = MediaPlayer.create(context, R.raw.game_won);
    }

    public void setVolume(int left, int right){
        mediaPlayer.setVolume(left, right);
    }

    public void start(){
        mediaPlayer.start();
    }
}
