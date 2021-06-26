package com.example.kugellabyrinth;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;

/**
 * The type Accelerometer.
 */
public abstract class Accelerometer implements SensorEventListener {
    /**
     * The M accel current.
     */
    float mAccelCurrent;
    /**
     * The M accel last.
     */
    float mAccelLast;

    /**
     * On acceleration change.
     *
     * @param xVal the x val
     * @param yVal the y val
     */
    public abstract void onAccelerationChange(float xVal, float yVal);

    /**
     * Set gravitational constant.
     *
     * @param gravConstant the grav constant
     */
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
