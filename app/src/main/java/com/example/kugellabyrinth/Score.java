package com.example.kugellabyrinth;

import android.os.Build;

import androidx.annotation.RequiresApi;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Date;

/**
 * The type Score.
 */
public class Score {

    /**
     * The constant scoreArrayList.
     */
    public static ArrayList<Score> scoreArrayList = new ArrayList<>();

    /**
     * The Id.
     */
    int id;
    /**
     * The Username.
     */
    String username;
    /**
     * The Time spent.
     */
    int timeSpent;
    /**
     * The Level.
     */
    int level;

    /**
     * Instantiates a new Score.
     *
     * @param id        the id
     * @param username  the username
     * @param level     the level
     * @param timeSpent the time spent
     */
    public Score(int id, String username, int level, int timeSpent) {
        this.id = id;
        this.username = username;
        this.level = level;
        this.timeSpent = timeSpent;
    }

    /**
     * Gets id.
     *
     * @return the id
     */
    public int getId() { return id;}

    /**
     * Get username string.
     *
     * @return the string
     */
    public String getUsername(){
        return username;
    }

    /**
     * Get time spent int.
     *
     * @return the int
     */
    public int getTimeSpent(){
        return timeSpent;
    }

    /**
     * Get level int.
     *
     * @return the int
     */
    public int getLevel(){
        return level;
    }

    /**
     * Gets scores for level.
     *
     * @param level the level
     * @return the scores for level
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    public static ArrayList<Score> getScoresForLevel(int level) {
        ArrayList<Score> toReturn = new ArrayList<>();
        for (Score s : scoreArrayList
                .stream()
                .filter(s -> s.level == level)
                .toArray(Score[]::new)) {
            toReturn.add(s);
        }
        return toReturn;
    }
}
