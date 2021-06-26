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

/**
 * The type Game activity.
 */
public class GameActivity extends AppCompatActivity implements EventListener{

    /**
     * The Sqlite manager.
     */
    SQLiteManager sqliteManager;
    /**
     * The Timer text view.
     */
    TextView timerTextView;
    /**
     * The Timer handler.
     */
    Handler timerHandler = new Handler();
    /**
     * The Timer started.
     */
    Boolean timerStarted = false;
    /**
     * The Time spent.
     */
    int timeSpent;
    /**
     * The Start time.
     */
    long startTime = 0;
    /**
     * The Sound player.
     */
    SoundPlayer soundPlayer;
    /**
     * The Pref.
     */
    SharedPreferences pref;
    /**
     * The Game title.
     */
    TextView gameTitle;

    /**
     * The Timer runnable.
     */
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
        gameTitle.setText("Level " + 1);

        final ImageButton openMenu = findViewById(R.id.openMenu);
        openMenu.setOnClickListener(v -> {
            timerStarted = false;
            timerHandler.removeCallbacks(timerRunnable);
            startActivity(menuScreen);
        });
    }

    /**
     * Start timer.
     */
    public void StartTimer() {
        timerStarted = true;
        startTime = System.currentTimeMillis();
        timerHandler.postDelayed(timerRunnable, 0);
    }

    /**
     * Stop timer.
     */
    public void StopTimer() {
        // stop timer
        timerStarted = false;
        timerHandler.removeCallbacks(timerRunnable);
        soundPlayer.start();

        // creating and adding score to scoreArrayList
        int id = Score.scoreArrayList.size();
        String username = "";
        username = pref.getString(username, "Unknown");
        Score newScore = new Score(id, pref.getString(username, "Unknown"), GameFieldFragment.currentLevel + 1, timeSpent);
        Score.scoreArrayList.add(newScore);

        // adding score to database
        sqliteManager.addScoreToDB(newScore);
    }

    /**
     * Open scoreboard.
     */
    public void OpenScoreboard() {
        timerStarted = false;
        timerHandler.removeCallbacks(timerRunnable);
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
        if(intent.getStringExtra("ACTION").equals("Start-Game")){
            StartTimer();
            GameFieldFragment.currentLevel = (GameFieldFragment.currentLevel + 1) % 5;
            gameTitle.setText("Level " + (GameFieldFragment.currentLevel + 1));
        }
    }
}