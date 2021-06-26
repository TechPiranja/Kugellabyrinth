package com.example.kugellabyrinth;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

/**
 * The type Scoreboard activity.
 */
public class ScoreboardActivity extends AppCompatActivity{

    private Intent intent;
    private ListView scoreListView;
    private int levelToDisplay = 1;
    /**
     * The Level text.
     */
    TextView levelText;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if(intent.getStringExtra("ACTION").equals("Refresh-List")){
            setScoreAdapter(GameFieldFragment.currentLevel);
            levelText.setText("Level " + (GameFieldFragment.currentLevel + 1));
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void setScoreAdapter(int level) {
        Score.scoreArrayList.sort((Score s1, Score s2)->s1.getTimeSpent()-s2.getTimeSpent());
        ArrayList<Score> filteredScoreList = Score.getScoresForLevel(level + 1);
        ScoreAdapter scoreAdapter = new ScoreAdapter(getApplicationContext(), filteredScoreList);
        scoreListView.setAdapter(scoreAdapter);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        levelToDisplay = GameFieldFragment.currentLevel;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scoreboard);

        intent = new Intent(this, GameActivity.class);
        scoreListView = findViewById(R.id.scoreListView);
        levelText = findViewById(R.id.levelText);
        levelText.setText("Level " + (GameFieldFragment.currentLevel + 1));
        final ImageButton leftLevelButton = findViewById(R.id.leftLevel);
        final ImageButton rightLevelButton = findViewById(R.id.rightLevel);
        setScoreAdapter(GameFieldFragment.currentLevel);

        final ImageButton openMenu = findViewById(R.id.openMenu);
        openMenu.setOnClickListener(v -> {
            Intent menuScreen = new Intent(this, MenuActivity.class);
            startActivity(menuScreen);
        });

        final Button nextLevelButton = findViewById(R.id.nextLevel);
        nextLevelButton.setOnClickListener(v -> {
            intent.putExtra("ACTION","Start-Game");
            startActivity(intent);
        });
        leftLevelButton.setOnClickListener(v -> {
            // -1 for getting the last level
            levelToDisplay = (levelToDisplay-1) % 5;
            if (levelToDisplay < 0)
                levelToDisplay = 5 + levelToDisplay;
            setScoreAdapter(levelToDisplay);

            // adding + 1 so the user sees numbers starting at 1 and not 0
            levelText.setText("Level " + (levelToDisplay + 1));

        });
        rightLevelButton.setOnClickListener(v -> {
            // +1 for getting the next level
            levelToDisplay = (levelToDisplay + 1) % 5;
            setScoreAdapter(levelToDisplay);

            // adding + 1 so the user sees numbers starting at 1 and not 0
            levelText.setText("Level " + (levelToDisplay + 1));
        });
    }
}
