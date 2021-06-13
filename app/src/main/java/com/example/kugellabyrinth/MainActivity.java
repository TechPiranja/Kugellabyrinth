package com.example.kugellabyrinth;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.media.MediaPlayer;

public class MainActivity extends AppCompatActivity implements EventListener{

    Boolean firstLoad = true;
    TextView timerTextView;
    Handler timerHandler = new Handler();
    Boolean timerStarted = false;
    int timeSpent;
    long startTime = 0;
    MediaPlayer mediaPlayer;
    Boolean isSoundMuted = false;

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

    public void StartTimer() {
        timerStarted = true;
        startTime = System.currentTimeMillis();
        timerHandler.postDelayed(timerRunnable, 0);
    }

    public void StopTimer() {
        timerStarted = false;
        timerHandler.removeCallbacks(timerRunnable);
        mediaPlayer.start();

        SQLiteManager sqliteManager = SQLiteManager.instanceOfDatabase(this);
        int id = Score.scoreArrayList.size();
        System.out.println("ID: " + id + " and " + timeSpent);

        String username = "Dummy";

        Score newScore = new Score(id, username, 1, timeSpent);
        Score.scoreArrayList.add(newScore);
        sqliteManager.addScoreToDB(newScore);
        OpenScoreboard();
    }

    public void OpenScoreboard() {
        Intent intent = new Intent(this, ScoreboardActivity.class);
        intent.putExtra("ACTION","Refresh-List");
        startActivity(intent);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        timerTextView = findViewById(R.id.timerTextView);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mediaPlayer = MediaPlayer.create(this, R.raw.game_won);

        if (firstLoad) {
            System.out.println("loaded from DB");
            loadFromDBToMemory();
            firstLoad = false;
        }

        final ImageButton button = findViewById(R.id.soundToggle);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                isSoundMuted = !isSoundMuted;
                if (isSoundMuted) {
                    mediaPlayer.setVolume(0, 0);
                    button.setImageResource(R.drawable.volume_mute);
                } else {
                    mediaPlayer.setVolume(1, 1);
                    button.setImageResource(R.drawable.volume_on);
                }
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void loadFromDBToMemory() {
        SQLiteManager sqliteManager = SQLiteManager.instanceOfDatabase(this);
        sqliteManager.populateScoreListArray();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
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
        if (data == "Stop-Timer" && timerStarted)
            StopTimer();
    }
}