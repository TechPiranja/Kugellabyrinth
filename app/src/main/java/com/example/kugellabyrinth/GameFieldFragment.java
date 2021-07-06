package com.example.kugellabyrinth;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import org.eclipse.paho.client.mqttv3.IMqttMessageListener;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

/**
 * The type Game field fragment.
 */
public class GameFieldFragment extends Fragment {

    private GameView gameView;
    /**
     * The M sensor manager.
     */
    SensorManager mSensorManager;
    /**
     * The Accelerometer.
     */
    Accelerometer accelerometer;
    private EventListener listener;
    /**
     * The Is game running.
     */
    Boolean isGameRunning = false;
    /**
     * The Stop message.
     */
    Boolean stopMessage = false;
    /**
     * The Client.
     */
    MQTTClient client;
    /**
     * The constant currentLevel.
     */
    public static int currentLevel = 0;

    /**
     * The EventListener which communicates between gamefragment and activity
     * @param activity
     */
    @Override
    public void onAttach(Activity activity)
    {
        super.onAttach(activity);
        if(activity instanceof EventListener)
            listener = (EventListener)activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // gets GameView which draws the maze and player
        gameView = new GameView(getActivity());
        // gets singleton mqttclient instance
        client = MQTTClient.getInstance();
        // gets sensormanager in order to controll ball with smartphone controller
        mSensorManager = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);
        return gameView;
    }

    /**
     * Subscribes to the sensehat data and handles messageArrived
     *
     * @param topic the topic
     */
    public void subscribe(String topic) {
        try {
            client.client.subscribe(topic, client.qos, new IMqttMessageListener() {
                @Override
                public void messageArrived(String topic, MqttMessage msg) throws Exception {
                    String message = new String(msg.getPayload());
                    String[] msgArray = message.split(", ", 3);

                    if (gameView.user.x != 0 && gameView.user.y != 0 && !isGameRunning && !stopMessage) {
                        listener.sendDataToActivity("Start-Timer");
                        isGameRunning = true;
                    }
                    if (isGameRunning || gameView.user.x != 19 && gameView.user.y != 20 && !stopMessage){
                        gameView.PlayerInput(-1 * Float.parseFloat(msgArray[0]), Float.parseFloat(msgArray[1]));
                    }
                    if (gameView.user.x == 19 && gameView.user.y == 20 && isGameRunning)
                    {
                        isGameRunning = false;
                        stopMessage = true;
                        gameView.ResetPlayerPoint();
                        listener.sendDataToActivity("Stop-Timer");
                        new Thread(() -> client.publish("sensehat/message", "blinken")).start();
                    }
                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
            Log.d("MQTT", "Error on Subscribe. Error: " + e);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        gameView.ResetPlayerPoint();
        gameView.loadNextLevel(currentLevel);
        client = MQTTClient.getInstance();
        if  (MQTTClient.usingMQTT){
            stopMessage = false;
            if (client.client == null || !client.client.isConnected())
                client.connect();
            subscribe("sensor/data");
        }
        else {
            accelerometer = new Accelerometer() {
                @Override
                public void onAccelerationChange(float x, float y) {
                    if (gameView.user.x != 0 && gameView.user.y != 0 && !isGameRunning) {
                        isGameRunning = true;
                        listener.sendDataToActivity("Start-Timer");
                    }
                    if (gameView.user.x == 19 && gameView.user.y == 20 && isGameRunning)
                    {
                        isGameRunning = false;
                        gameView.ResetPlayerPoint();
                        listener.sendDataToActivity("Stop-Timer");
                    }
                    gameView.PlayerInput(x, y);
                }
            };
            accelerometer.setGravitationalConstant(SensorManager.GRAVITY_EARTH);
            mSensorManager.registerListener(accelerometer, mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        isGameRunning = false;
        if (client.usingMQTT)
            client.disconnect();
        else
            mSensorManager.unregisterListener(accelerometer);
    }
}