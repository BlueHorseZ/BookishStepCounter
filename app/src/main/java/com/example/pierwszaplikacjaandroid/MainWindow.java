package com.example.pierwszaplikacjaandroid;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

public class MainWindow extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_window);
    }
    public void openStoryWindow(View view) {
        Intent intent = new Intent(this, StoryWindow.class);
        startActivity(intent);
    }

    public void openParapetWindow(View view) {
        Intent intent = new Intent(this, ParapetWindow.class);
        startActivity(intent);
    }

    public void openLeyLines(View view) {
        Intent intent = new Intent(this, LeyLines.class);
        startActivity(intent);
    }

    public void openTerrasenWindow(View view) {
        Intent intent = new Intent(this, TerrasenWindow.class);
        startActivity(intent);
    }

    public void openBadges(View view) {
        Intent intent = new Intent(this, BadgesWindow.class);
        startActivity(intent);
    }

    }
