package com.example.pierwszaplikacjaandroid;

import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class LeyLines extends AppCompatActivity {

    private TextView stepCountTextView;
    private TextView congratulationTextView;
    private ProgressBar progressBar;
    private double MagnitudePrevious = 0;
    private Integer stepCount = 0;
    private int lastBackgroundChangeStep = 0;
    private int[] backgroundResources = {
            R.drawable.bg_leylines1,
            R.drawable.bg_leylines2,
            R.drawable.bg_leylines3,
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.leylines_window);
        stepCountTextView = findViewById(R.id.stepCount);
        congratulationTextView = findViewById(R.id.Congratulation);
        progressBar = findViewById(R.id.progressBar);
        progressBar.setMax(20);
        SensorManager sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        Sensor sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        congratulationTextView.setVisibility(View.GONE);

        SensorEventListener stepDetector = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent sensorEvent) {
                if (sensorEvent != null) {
                    float x_acceleration = sensorEvent.values[0];
                    float y_acceleration = sensorEvent.values[1];
                    float z_acceleration = sensorEvent.values[2];

                    double Magnitude = Math.sqrt(x_acceleration * x_acceleration + y_acceleration * y_acceleration + z_acceleration * z_acceleration);
                    double MagnitudeDelta = Magnitude - MagnitudePrevious;
                    MagnitudePrevious = Magnitude;

                    if (MagnitudeDelta > 4) {
                        stepCount++;
                        stepCountTextView.setText("Kroki: " + stepCount);
                        progressBar.setProgress(stepCount);

                        if (stepCount >= 20) {
                            congratulationTextView.setVisibility(View.VISIBLE);
                            congratulationTextView.setText("You got this! Congratulation!");
                        }
                        updateBackground(stepCount);
                    }
                }
    }
            @Override
            public void onAccuracyChanged(Sensor sensor, int i) {
            }
        };

        sensorManager.registerListener(stepDetector, sensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        super.onPause();
        saveStepCount();
    }

    @Override
    protected void onStop() {
        super.onStop();
        saveStepCount();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadStepCount();
    }

    private void saveStepCount() {
        SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("stepCount", stepCount);
        editor.apply();
    }

    private void loadStepCount() {
        SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
        stepCount = sharedPreferences.getInt("stepCount", 0);
        stepCountTextView.setText("Kroki: " + stepCount);
        progressBar.setProgress(stepCount);
    }


    public void onBackPressed(View view) {
        stepCount = 0;
        SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("stepCount", stepCount);
        editor.apply();
        congratulationTextView.setVisibility(View.GONE);

        Intent intent = new Intent(this, MainWindow.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }


    public void updateBackground(int currentSteps) {
        if ((currentSteps - lastBackgroundChangeStep) >= 10) {
            findViewById(R.id.leylines_window).setBackgroundResource(getNextBackgroundResource(currentSteps));
            lastBackgroundChangeStep = currentSteps;
        }
    }

    private int getNextBackgroundResource(int currentSteps) {
        int index = (currentSteps / 10) % backgroundResources.length;
        return backgroundResources[index];
    }

}
