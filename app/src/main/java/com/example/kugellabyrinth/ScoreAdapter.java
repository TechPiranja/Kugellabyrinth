package com.example.kugellabyrinth;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;


public class ScoreAdapter extends ArrayAdapter<Score> {
    public ScoreAdapter(@NonNull Context context, List<Score> scores) {
        super(context, 0, scores);
    }

    public View getView(int position, View convertView, ViewGroup parent){
        Score score = getItem(position);
        if(convertView == null)
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.score_cell, parent, false);

        TextView username = convertView.findViewById(R.id.cellUsername);
        TextView timeSpent = convertView.findViewById(R.id.cellTimeSpent);
        TextView winnerPlace = convertView.findViewById(R.id.winnerPlace);

        assert score != null;
        username.setText(score.getUsername());

        int seconds = score.getTimeSpent() / 1000;
        int minutes = seconds / 60;
        seconds = seconds % 60;
        int millis = score.getTimeSpent() % 1000;
        String timeSpentText = String.format("%d m %02d s %03d ms", minutes, seconds, millis);
        timeSpent.setText(timeSpentText);
        // we have to add 1, so the list starts at 1 ant not 0
        winnerPlace.setText(String.valueOf(position + 1));
        return convertView;
    }
}
