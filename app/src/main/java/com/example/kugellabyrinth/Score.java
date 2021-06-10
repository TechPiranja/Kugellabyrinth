package com.example.kugellabyrinth;

import java.util.ArrayList;
import java.util.Date;

public class Score {

    public static ArrayList<Score> scoreArrayList = new ArrayList<>();

    int id;
    String username;
    String timeSpent;
    int level;

    public Score(int id, String username, int level, String timeSpent) {
        this.id = id;
        this.username = username;
        this.level = level;
        this.timeSpent = timeSpent;
    }

    public int getId() { return id;}
    public String getUsername(){
        return username;
    }
    public String getTimeSpent(){
        return timeSpent;
    }
    public int getLevel(){
        return level;
    }
}
