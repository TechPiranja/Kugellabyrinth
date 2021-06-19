package com.example.kugellabyrinth;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class ScoreboardActivity extends AppCompatActivity implements EventListener{

    private Intent intent;
    private ListView scoreListView;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if(intent.getStringExtra("ACTION").equals("Refresh-List")){
            setScoreAdapter();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void setScoreAdapter() {
        Score.scoreArrayList.sort((Score s1, Score s2)->s1.getTimeSpent()-s2.getTimeSpent());
        ArrayList<Score> filteredScoreList = Score.getScoresForLevel(GameFieldFragment.currentLevel+1);
        ScoreAdapter scoreAdapter = new ScoreAdapter(getApplicationContext(), filteredScoreList);
        scoreListView.setAdapter(scoreAdapter);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scoreboard);

        intent = new Intent(this, GameActivity.class);
        scoreListView = findViewById(R.id.scoreListView);
        setScoreAdapter();

        final Button button = findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                intent.putExtra("ACTION","Restart-Game");
                startActivity(intent);
            }
        });
    }

    @Override
    public void sendDataToActivity(String data) {

    }
}
