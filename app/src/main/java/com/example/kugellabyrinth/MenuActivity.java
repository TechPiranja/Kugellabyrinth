package com.example.kugellabyrinth;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

public class MenuActivity extends AppCompatActivity{

    SoundPlayer soundPlayer;
    Boolean isSoundMuted = false;
    Boolean firstLoad = true;
    TextView timerTextView;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        timerTextView = findViewById(R.id.timerTextView);
        soundPlayer = SoundPlayer.getInstance(this);

        if (firstLoad) {
            System.out.println("loaded from DB");
            loadFromDBToMemory();
            firstLoad = false;
        }

        final ImageButton soundToggle = findViewById(R.id.soundToggle);
        soundToggle.setOnClickListener(v -> {
            isSoundMuted = !isSoundMuted;
            if (isSoundMuted) {
                soundPlayer.setVolume(0, 0);
                soundToggle.setImageResource(R.drawable.volume_mute);
            } else {
                soundPlayer.setVolume(1, 1);
                soundToggle.setImageResource(R.drawable.volume_on);
            }
        });

        Intent gameScreen = new Intent(this, GameActivity.class);
        Intent scoreBoardScreen = new Intent(this, ScoreboardActivity.class);

        final Button startGame = findViewById(R.id.startGame);
        startGame.setOnClickListener(v -> {
            startActivity(gameScreen);
        });

        final Button openScoreboard = findViewById(R.id.openScoreboard);
        openScoreboard.setOnClickListener(v -> {
            scoreBoardScreen.putExtra("ACTION","Refresh-List");
            startActivity(scoreBoardScreen);
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void loadFromDBToMemory() {
        SQLiteManager sqliteManager = SQLiteManager.instanceOfDatabase(this);
        sqliteManager.populateScoreListArray();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}