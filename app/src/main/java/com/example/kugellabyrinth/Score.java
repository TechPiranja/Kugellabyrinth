package com.example.kugellabyrinth;

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
}
