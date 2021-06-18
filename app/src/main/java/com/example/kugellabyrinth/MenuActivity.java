package com.example.kugellabyrinth;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.TextView;

public class MenuActivity extends AppCompatActivity{

    SoundPlayer soundPlayer;
    Boolean isSoundMuted = false;
    Boolean firstLoad = true;
    TextView timerTextView;
    public static final String mqttAddress = "127.0.0.1";
    public static final String username = "Unknown";
    MQTTClient client;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        final SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
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

        client = MQTTClient.getInstance();
        String serverUri = "tcp://" + pref.getString(mqttAddress, "127.0.0.1") + ":1883";
        startGame.setOnClickListener(v -> {
            if (MQTTClient.usingMQTT){
                client.connect(serverUri);
                client.publish("sensehat/message", "Start");
            }
            gameScreen.putExtra("ACTION","Restart-Game");
            startActivity(gameScreen);
        });

        final Button openScoreboard = findViewById(R.id.openScoreboard);
        openScoreboard.setOnClickListener(v -> {
            scoreBoardScreen.putExtra("ACTION","Refresh-List");
            startActivity(scoreBoardScreen);
        });

        Switch sensorSwitch = findViewById(R.id.sensorSwitch);
        EditText mqttAdressText = findViewById(R.id.mqttAddress);
        EditText usernameText = findViewById(R.id.usernameText);

        sensorSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if(isChecked){
                mqttAdressText.setEnabled(true);
                MQTTClient.usingMQTT = true;
            } else {
                mqttAdressText.setEnabled(false);
                if (MQTTClient.usingMQTT) {
                    MQTTClient.usingMQTT = false;
                    client.disconnect();
                }
            }
        });
        usernameText.setText(pref.getString(username, "Unknown"));
        mqttAdressText.setText(pref.getString(mqttAddress, "127.0.0.1"));
        mqttAdressText.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                pref.edit().putString(mqttAddress, s.toString()).commit();
            }
        });
        usernameText.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                pref.edit().putString(username, s.toString()).commit();
            }

            @Override
            public void afterTextChanged(Editable s) {
                pref.edit().putString(username, s.toString()).commit();
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