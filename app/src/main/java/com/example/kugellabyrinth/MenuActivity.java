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
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Switch;

/**
 * This Activity can navigate to the Game and Scoreboard Screen
 * It is able to Configure the Game with Username, Sound, SensorInput and the Broker-IP Address
 */
public class MenuActivity extends AppCompatActivity{

    /**
     * The Sound player.
     */
    SoundPlayer soundPlayer;
    /**
     * The Is sound muted.
     */
    Boolean isSoundMuted = false;
    /**
     * The First load.
     */
    Boolean firstLoad = true;
    /**
     * The constant mqttAddress.
     */
    public static final String mqttAddress = "127.0.0.1";
    /**
     * The constant username.
     */
    public static final String username = "Unknown";
    /**
     * The Client.
     */
    MQTTClient client;
    /**
     * The Game screen.
     */
    Intent gameScreen;
    /**
     * The Score board screen.
     */
    Intent scoreBoardScreen;
    /**
     * The Pref.
     */
    SharedPreferences pref;

    /**
     * onCreate Function
     * @param savedInstanceState
     */
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pref = PreferenceManager.getDefaultSharedPreferences(this);
        setContentView(R.layout.activity_menu);

        // getting single Instance of SoundPlayer in order to toggle sound config
        soundPlayer = SoundPlayer.getInstance(this);

        // if the MenuScreen is started the first time, it should load the DB to the App Memory
        if (firstLoad) {
            loadFromDBToMemory();
            firstLoad = false;
        }

        // getting intents in order to navigate to them
        gameScreen = new Intent(this, GameActivity.class);
        scoreBoardScreen = new Intent(this, ScoreboardActivity.class);

        // getting single Instance of MQTTClient and setting default serverUri
        client = MQTTClient.getInstance();
        client.serverUri = "tcp://" + pref.getString(mqttAddress, "127.0.0.1") + ":1883";

        // initializing all Buttons and Text Components
        initView();
    }

    /**
     * Initializes all Buttons from Menu Screen.
     */
    private void initButtons(){
        Button startGame = findViewById(R.id.startGame);
        startGame.setOnClickListener(v -> {
            gameScreen.putExtra("ACTION","Start-Game");
            startActivity(gameScreen);
        });

        Button openScoreboard = findViewById(R.id.openScoreboard);
        openScoreboard.setOnClickListener(v -> {
            scoreBoardScreen.putExtra("ACTION","Refresh-List");
            startActivity(scoreBoardScreen);
        });

        ImageButton soundToggle = findViewById(R.id.soundToggle);
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
    }

    /**
     * Initializes all visible Components from Menu Screen.
     */
    private void initView(){
        initButtons();

        EditText mqttAddressText = findViewById(R.id.mqttAddress);


        Switch sensorSwitch = findViewById(R.id.sensorSwitch);
        sensorSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if(isChecked){
                mqttAddressText.setEnabled(true);
                MQTTClient.usingMQTT = true;
            } else {
                mqttAddressText.setEnabled(false);
                if (MQTTClient.usingMQTT) {
                    MQTTClient.usingMQTT = false;
                }
            }
            Log.d("MQTT", "Switching Sensor. MQTT is " + MQTTClient.usingMQTT);
        });

        mqttAddressText.setText(pref.getString(mqttAddress, "127.0.0.1"));
        mqttAddressText.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                pref.edit().putString(mqttAddress, s.toString()).commit();
                client.serverUri = "tcp://" + pref.getString(mqttAddress, "127.0.0.1") + ":1883";
            }

            @Override
            public void afterTextChanged(Editable s) {
                pref.edit().putString(mqttAddress, s.toString()).commit();
                client.serverUri = "tcp://" + pref.getString(mqttAddress, "127.0.0.1") + ":1883";
            }
        });

        EditText usernameText = findViewById(R.id.usernameText);
        usernameText.setText(pref.getString(username, "Unknown"));
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
}