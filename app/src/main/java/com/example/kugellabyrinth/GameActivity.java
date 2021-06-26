package com.example.kugellabyrinth;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Handler;
import android.preference.PreferenceManager;
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
     * The Timer runnable, which counts the time Played for the current Level.
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

    /**
     * resets the TimerText to 0:00
     */
    private void resetTimer(){
        timerTextView.setText(String.format("%d:%02d", 0, 0));
    }

    /**
     * onCreate Function
     * @param savedInstanceState
     */
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pref = PreferenceManager.getDefaultSharedPreferences(this);
        setContentView(R.layout.activity_game);

        // gets instances of classes and view components
        timerTextView = findViewById(R.id.timerTextView);
        soundPlayer = SoundPlayer.getInstance(this);
        Intent menuScreen = new Intent(this, MenuActivity.class);
        sqliteManager = SQLiteManager.instanceOfDatabase(this);
        gameTitle = findViewById(R.id.gameTitle);

        // setting the gameTitle initially to Level 1
        gameTitle.setText("Level " + 1);

        // setting the onClick function for the open menu imageButton
        final ImageButton openMenu = findViewById(R.id.openMenu);
        openMenu.setOnClickListener(v -> {
            timerStarted = false;
            timerHandler.removeCallbacks(timerRunnable);
            startActivity(menuScreen);
        });
    }

    /**
     * Starts the Timer for the Level.
     */
    public void StartTimer() {
        timerStarted = true;
        startTime = System.currentTimeMillis();
        timerHandler.postDelayed(timerRunnable, 0);
    }

    /**
     * Stops the Timer for the Level, plays winning sound and save score to Database
     */
    public void StopTimer() {
        // stop timer
        timerStarted = false;
        timerHandler.removeCallbacks(timerRunnable);

        // play the winning sound
        soundPlayer.start();

        // saving score to ScoreArrayList and Database
        SaveScore();
    }

    /**
     * Saves the Score to the ScoreArrayList and the Database
     */
    private void SaveScore(){
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
     * Opens Scoreboard with intent extra.
     */
    public void OpenScoreboard() {
        Intent intent = new Intent(this, ScoreboardActivity.class);
        intent.putExtra("ACTION","Refresh-List");
        startActivity(intent);
    }

    /**
     * This EventListener is used by the GameFieldFragment to communicate with the activity
     * @param data
     */
    @Override
    public void sendDataToActivity(String data) {
        // fragment sends start if the user tilted the device or controller
        if (data == "Start-Timer" && !timerStarted)
            StartTimer();

        // fragment sends stop if the user reached the goal
        if (data == "Stop-Timer" && timerStarted){
            StopTimer();
            OpenScoreboard();
        }
    }

    /**
     * If the Game Screen is started, reset the Timer and set the correct level text
     * @param intent
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if(intent.getStringExtra("ACTION").equals("Start-Game")){
            resetTimer();
            GameFieldFragment.currentLevel = (GameFieldFragment.currentLevel + 1) % 5;
            gameTitle.setText("Level " + (GameFieldFragment.currentLevel + 1));
        }
    }
}