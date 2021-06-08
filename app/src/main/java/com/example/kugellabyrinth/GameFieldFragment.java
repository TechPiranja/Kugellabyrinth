package com.example.kugellabyrinth;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class GameFieldFragment extends Fragment {

    private Accelerometer accelerometer;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        accelerometer = new Accelerometer(requireActivity());
        accelerometer.setListener(new Accelerometer.Listener(){

            @Override
            public void onTranslation(float tx, float ty, float tz) {
                if (tx > 1.0f){
                    System.out.println("GAME: its turning left" + tx);
                } else if (tx < -1.0f) {
                    System.out.println("GAME: its turning right" + tx);
                }
            }
        });
        return inflater.inflate(R.layout.fragment_game_field, container);
    }

    @Override
    public void onResume() {
        super.onResume();
        accelerometer.register();
    }

    @Override
    public void onPause() {
        super.onPause();
        accelerometer.unregister();
        System.out.println("onPause");
    }
}