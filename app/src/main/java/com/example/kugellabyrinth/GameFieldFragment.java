package com.example.kugellabyrinth;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class GameFieldFragment extends Fragment {

    private GameView gameView;
    SensorManager mSensorManager;
    Accelerometer accelerometer;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        gameView = new GameView(getActivity());
        mSensorManager = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);
        accelerometer = new Accelerometer() {
            @Override
            public void onAccelerationChange(float x, float y) {
                gameView.PlayerInput(x, y);
            }
        };
        accelerometer.setGravitationalConstant(SensorManager.GRAVITY_EARTH);
        return gameView;
    }

    @Override
    public void onResume() {
        super.onResume();
        mSensorManager.registerListener(accelerometer, mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    public void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(accelerometer);
    }
}