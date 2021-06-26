package com.example.kugellabyrinth;

import android.content.Context;
import android.media.MediaPlayer;

/**
 * A Single Instance SoundPlayer to use the same MediaPlayer over all Activity's.
 */
public class SoundPlayer {

    /**
     * The Media player.
     */
    public MediaPlayer mediaPlayer;

    private static SoundPlayer instance;

    /**
     * Gets single instance of the Sound Player
     *
     * @param context of the Application
     * @return the instance
     */
    public static SoundPlayer getInstance (Context context) {
        if (SoundPlayer.instance == null) {
            SoundPlayer.instance = new SoundPlayer(context);
        }
        return SoundPlayer.instance;
    }

    /**
     * Constructor of the MediaPlayer with the game sound
     * @param context
     */
    private SoundPlayer(Context context) {
        mediaPlayer = MediaPlayer.create(context, R.raw.game_won);
    }

    /**
     * Sets the volume of the game sound.
     *
     * @param left volume output left
     * @param right volume output left
     */
    public void setVolume(int left, int right) {
        mediaPlayer.setVolume(left, right);
    }

    /**
     * Plays the MediaPlayer sound
     */
    public void start() {
        mediaPlayer.start();
    }
}
