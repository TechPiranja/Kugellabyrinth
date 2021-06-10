package com.example.kugellabyrinth;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class ScoreboardActivity extends AppCompatActivity implements EventListener{

    private Intent intent;
    private ListView scoreListView;

    @Override
    public void sendDataToActivity(String data) {

    }

    private void setScoreAdapter() {
        ScoreAdapter scoreAdapter = new ScoreAdapter(getApplicationContext(), Score.scoreArrayList);
        scoreListView.setAdapter(scoreAdapter);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scoreboard);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        intent = new Intent(this, MainActivity.class);

        scoreListView = findViewById(R.id.scoreListView);
        setScoreAdapter();

        final Button button = findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startActivity(intent);
            }
        });
    }
}
