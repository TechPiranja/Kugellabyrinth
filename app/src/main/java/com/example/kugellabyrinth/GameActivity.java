package com.example.kugellabyrinth;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Handler;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageButton;
import android.widget.TextView;

public class GameActivity extends AppCompatActivity implements EventListener{

    SQLiteManager sqliteManager;
    TextView timerTextView;
    Handler timerHandler = new Handler();
    Boolean timerStarted = false;
    int timeSpent;
    long startTime = 0;
    SoundPlayer soundPlayer;
    SharedPreferences pref;
    TextView gameTitle;

    Runnable timerRunnable = new Runnable() {
        @Override
        public void run() {
            long millis = System.currentTimeMillis() - startTime;
            int seconds = (int) (millis / 1000);
            int minutes = seconds / 60;
            seconds = seconds % 60;
            timeSpent = (int)millis;
            String timeSpentText = String.format("%d:%02d", minutes, seconds);
            timerTextView.setText(timeSpentText);
            timerHandler.postDelayed(this, 500);
        }
    };

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pref = PreferenceManager.getDefaultSharedPreferences(this);
        setContentView(R.layout.activity_game);
        timerTextView = findViewById(R.id.timerTextView);
        soundPlayer = SoundPlayer.getInstance(this);
        Intent menuScreen = new Intent(this, MenuActivity.class);
        sqliteManager = SQLiteManager.instanceOfDatabase(this);
        gameTitle = findViewById(R.id.gameTitle);
        gameTitle.setText("Level " + 0);

        final ImageButton openMenu = findViewById(R.id.openMenu);
        openMenu.setOnClickListener(v -> {
            timerStarted = false;
            timerHandler.removeCallbacks(timerRunnable);
            startActivity(menuScreen);
        });
    }

    public void StartTimer() {
        timerStarted = true;
        startTime = System.currentTimeMillis();
        timerHandler.postDelayed(timerRunnable, 0);
    }

    public void StopTimer() {
        // stop timer
        timerStarted = false;
        timerHandler.removeCallbacks(timerRunnable);
        soundPlayer.start();

        // creating and adding score to scoreArrayList
        int id = Score.scoreArrayList.size();
        String username = "";
        username = pref.getString(username, "Unknown");
        Score newScore = new Score(id, pref.getString(username, "Unknown"), GameFieldFragment.currentLevel, timeSpent);
        Score.scoreArrayList.add(newScore);

        // adding score to database
        sqliteManager.addScoreToDB(newScore);
    }

    public void OpenScoreboard() {
        Intent intent = new Intent(this, ScoreboardActivity.class);
        intent.putExtra("ACTION","Refresh-List");
        startActivity(intent);
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

    @Override
    public void sendDataToActivity(String data) {
        System.out.println("Event" + data);
        if (data == "Start-Timer" && !timerStarted)
            StartTimer();
        if (data == "Stop-Timer" && timerStarted){
            StopTimer();
            OpenScoreboard();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if(intent.getStringExtra("ACTION").equals("Restart-Game")){
            StartTimer();
            gameTitle.setText("Level " + (GameFieldFragment.currentLevel + 1));
        }
    }
}