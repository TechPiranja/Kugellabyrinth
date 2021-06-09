package com.example.kugellabyrinth;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;

public abstract class Accelerometer implements SensorEventListener {
    final String TAG = "Accelerometer";
    float mAccelCurrent;
    float mAccelLast;

    public abstract void onAccelerationChange(float xVal, float yVal);

    public void setGravitationalConstant(float gravConstant){
        mAccelCurrent = gravConstant;
        mAccelLast = gravConstant;
    }

    public void onSensorChanged(SensorEvent se){
        onAccelerationChange(se.values[0], se.values[1]);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
