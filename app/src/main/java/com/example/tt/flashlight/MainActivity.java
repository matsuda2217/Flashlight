package com.example.tt.flashlight;

import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.ToggleButton;

public class MainActivity extends ActionBarActivity {
    Camera cam;
    Parameters camParams;
    boolean hasCam;
    int freg;
    StroboRuner sr;
    Thread t;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ToggleButton toggleButton = (ToggleButton) findViewById(R.id.toogleButton);
        toggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                turnOnOff(isChecked);
            }
        });
        SeekBar seekBar = (SeekBar) findViewById(R.id.seekBar);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                freg = progress;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            cam = Camera.open();
            camParams = cam.getParameters();
            cam.startPreview();
            hasCam = true;

        } catch (Exception e) {
        }
    }

    private void turnOnOff(boolean on) {
        if (on) {
            if (freg != 0) {
                sr = new StroboRuner();
                sr.freg = freg;
                t = new Thread(sr);
                t.start();
                return;
            } else {
                camParams.setFlashMode(Parameters.FLASH_MODE_TORCH);
            }
        } else if (!on) {
            if (t != null) {
                sr.stopRunning = true;
                t = null;
                return;
            } else {
                camParams.setFlashMode(Parameters.FLASH_MODE_OFF);
            }

        }
        cam.setParameters(camParams);
        cam.startPreview();
    }

    private class StroboRuner implements Runnable {
        int freg;
        boolean stopRunning =false;
        @Override
        public void run() {
            Camera.Parameters parametersOn = cam.getParameters();
            Camera.Parameters parametersOff = camParams;
            parametersOn.setFlashMode(Parameters.FLASH_MODE_TORCH);
            parametersOff.setFlashMode(Parameters.FLASH_MODE_OFF);
            try {
                while (!stopRunning) {
                    cam.setParameters(parametersOn);
                    cam.startPreview();
                    Thread.sleep(300 - freg);
                    cam.setParameters(parametersOff);
                    cam.startPreview();
                    Thread.sleep(freg);

                }
            } catch (Exception e) {
            }
        }
    }
}
