package com.example.kugellabyrinth;

import android.os.Build;

import androidx.annotation.RequiresApi;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Date;

public class Score {

    public static ArrayList<Score> scoreArrayList = new ArrayList<>();

    int id;
    String username;
    int timeSpent;
    int level;

    public Score(int id, String username, int level, int timeSpent) {
        this.id = id;
        this.username = username;
        this.level = level;
        this.timeSpent = timeSpent;
    }

    public int getId() { return id;}
    public String getUsername(){
        return username;
    }
    public int getTimeSpent(){
        return timeSpent;
    }
    public int getLevel(){
        return level;
    }

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
