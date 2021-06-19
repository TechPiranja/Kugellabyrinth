package com.example.kugellabyrinth;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.eclipse.paho.client.mqttv3.MqttException;

public class GameFieldFragment extends Fragment {

    private GameView gameView;
    SensorManager mSensorManager;
    Accelerometer accelerometer;
    private EventListener listener;
    Boolean isGameRunning = false;
    MQTTClient client;
    public static int currentLevel = 0;

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
        gameView = new GameView(getActivity());
        client = MQTTClient.getInstance();
        mSensorManager = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);
        if (MQTTClient.usingMQTT){
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
        }
        return gameView;
    }

    public void subscribe(String topic) {
        try {
            client.client.subscribe(topic, client.qos, (topic1, msg) -> {
                String message = new String(msg.getPayload());
                String[] msgArray = message.split(", ", 3);
                gameView.PlayerInput(-1 * Float.parseFloat(msgArray[0]), Float.parseFloat(msgArray[1]));

                if (gameView.user.x != 0 && gameView.user.y != 0 && !isGameRunning) {
                    isGameRunning = true;
                    listener.sendDataToActivity("Start-Timer");
                }
                if (gameView.user.x == 19 && gameView.user.y == 20 && isGameRunning)
                {
                    isGameRunning = false;
                    gameView.ResetPlayerPoint();
                    listener.sendDataToActivity("Stop-Timer");
                    client.publish("sensehat/message", "blinken");
                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        currentLevel = (currentLevel + 1) % 5;
        gameView.ResetPlayerPoint();
        gameView.loadNextLevel(currentLevel);
        if (MQTTClient.usingMQTT){
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
        mSensorManager.unregisterListener(accelerometer);
    }
}