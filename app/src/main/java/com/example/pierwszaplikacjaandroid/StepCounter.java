package com.example.pierwszaplikacjaandroid;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;


public class StepCounter implements SensorEventListener {

    protected StepCounter stepCounter;
    private SensorManager  sensorManager = null;
    private Sensor stepSensor;
    private int totalSteps = 0;
    private int previousTotalSteps = 0;
    private ProgressBar progressBar;
    private TextView stepCount;
    private int stepGoal;
    private boolean isGoalReached = false;
    private Context context;
    private StepListener stepListener;

    public StepCounter(Context context, int stepGoal) {

        this.context = context;
        this.stepGoal = stepGoal;

        sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        if (sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER) != null) {
            stepSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
            sensorManager.registerListener(this, stepSensor, SensorManager.SENSOR_DELAY_UI);
            resetStepCounter();
        }
    }


    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_STEP_COUNTER) {
            int currentSteps = (int) event.values[0];
            SharedPreferences prefs = context.getSharedPreferences("StepCounterPrefs", Context.MODE_PRIVATE);
            int initialStepCount = prefs.getInt("InitialStepCount", -1);

            if (initialStepCount == -1) {
                // Zapisz początkową wartość licznika kroków
                prefs.edit().putInt("InitialStepCount", currentSteps).apply();
                initialStepCount = currentSteps;
            }

            if (!isGoalReached) {
                // Oblicz liczbę kroków wykonanych od początku sesji
                totalSteps = currentSteps - initialStepCount;
                if (totalSteps >= stepGoal) {
                    isGoalReached = true;
                    unregisterSensor();
                    displayCongratulations();

                    if (stepListener != null) {
                        stepListener.onStep(totalSteps);
                    }
                } else {
                    if (stepListener != null) {
                        stepListener.onStep(totalSteps);
                    }
                }
            }
        }
    }


    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    public void resetSteps() {
        totalSteps = 0;
        previousTotalSteps = 0;
        isGoalReached = false;
    }

    public void registerSensor() {
        if (stepSensor != null) {
            sensorManager.registerListener(this, stepSensor, SensorManager.SENSOR_DELAY_UI);
        }
    }

    public void unregisterSensor() {
        if (stepSensor != null) {
            sensorManager.unregisterListener(this, stepSensor);
        }
    }

    public void resetStepCounter() {
        SharedPreferences prefs = context.getSharedPreferences("StepCounterPrefs", Context.MODE_PRIVATE);
        int initialStepCount = prefs.getInt("InitialStepCount", -1);

        if (initialStepCount != -1) {
            // Resetuj licznik kroków
            previousTotalSteps = initialStepCount;
            totalSteps = 0;
            isGoalReached = false;

            // Zaktualizuj interfejs użytkownika
            stepCount.setText(String.valueOf(totalSteps));
            progressBar.setProgress(totalSteps);

            // Zapisz nową wartość początkową licznika kroków
            prefs.edit().putInt("InitialStepCount", (int) stepSensor.getMaximumRange()).apply();
        }
    }


    public int getTotalSteps() {
        return totalSteps;
    }
    private void displayCongratulations() {
        Activity activity = (Activity) context;
        TextView textView = activity.findViewById(R.id.Congratulation);
        textView.setText("You got it! Congratulation!");
        textView.setVisibility(View.VISIBLE);
    }

    public void setStepListener(StepListener listener) {
        this.stepListener = listener;
    }


}
