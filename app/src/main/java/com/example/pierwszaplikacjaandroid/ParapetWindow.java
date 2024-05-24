package com.example.pierwszaplikacjaandroid;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Random;

public class ParapetWindow extends AppCompatActivity implements StepListener{

    private StepCounter stepCounter;
    private ProgressBar progressBar;
    private TextView stepCount;
    private final int stepGoal = 20;
    private int lastBackgroundChangeStep = 0;

    private int[] backgroundResources = {
            R.drawable.bg_parapet1,
            R.drawable.bg_parapet2,
            R.drawable.bg_parapet3,
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.parapet_window);
        SensorManager sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        boolean isSensorPresent = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER) != null;
        if (!isSensorPresent) {
            Log.d("StepCounter", "Sensor kroków nie jest dostępny na tym urządzeniu.");

        } else {

            stepCounter = new StepCounter(this, stepGoal);
            stepCounter.setStepListener(this);
            progressBar = findViewById(R.id.progressBar);
            stepCount = findViewById(R.id.stepCount);

            progressBar.setMax(stepGoal);
        }
    }


    @Override
    public void onStep(int steps) {
        updateUI(steps);
        updateBackground(steps);
    }

    public void onBackPressed(View view) {
        stepCounter.unregisterSensor();
        stepCounter.resetStepCounter();
        Intent intent = new Intent(this, MainWindow.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();

        stepCounter.registerSensor();
    }

    @Override
    protected void onPause() {
        super.onPause();
        stepCounter.unregisterSensor();
        //stepCounter.resetSteps();
        saveStepCount();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stepCounter.resetStepCounter();
    }

    @Override
    protected void onStop() {
        super.onStop();
        stepCounter.unregisterSensor();
        //stepCounter.resetSteps();
        saveStepCount();
    }

    private void saveStepCount() {
        SharedPreferences prefs = getSharedPreferences("StepCounterPrefs", MODE_PRIVATE);
        prefs.edit().putInt("StepCount", stepCounter.getTotalSteps()).apply();
    }
    public void updateUI(int currentSteps) {
        stepCount.setText(getString(R.string.current_steps, currentSteps));
        progressBar.setProgress(currentSteps);
    }

    public void updateBackground(int currentSteps) {
        if ((currentSteps - lastBackgroundChangeStep) >= 10) {
            findViewById(R.id.parapet_window).setBackgroundResource(getNextBackgroundResource(currentSteps));
            lastBackgroundChangeStep = currentSteps;
        }
    }

    private int getNextBackgroundResource(int currentSteps) {

        int index = (currentSteps / 10) % backgroundResources.length;
        return backgroundResources[index];
    }

    private String[] quotes = {
            "Dragon",
            "Rider",
            "Basghiat"
    };

    private String getRandomQuote(){
        int randomIndex = new Random().nextInt(quotes.length);
        return quotes[randomIndex];
    }
}
