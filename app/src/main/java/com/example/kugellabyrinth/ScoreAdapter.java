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

    private static final DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyy HH:mm:ss");

    public ScoreAdapter(@NonNull Context context, List<Score> scores) {
        super(context, 0, scores);
    }

    public View getView(int position, View convertView, ViewGroup parent){
        Score score = getItem(position);
        if(convertView == null)
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.score_cell, parent, false);

        TextView username = convertView.findViewById(R.id.cellUsername);
        TextView level = convertView.findViewById(R.id.cellLevel);
        TextView timeSpent = convertView.findViewById(R.id.cellTimeSpent);

        assert score != null;
        username.setText(score.getUsername());
        level.setText(String.valueOf(score.getLevel()));
        timeSpent.setText(score.getTimeSpent());
        return convertView;
    }
}
