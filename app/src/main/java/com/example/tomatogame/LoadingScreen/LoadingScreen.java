package com.example.tomatogame.LoadingScreen;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ProgressBar;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.example.tomatogame.Home.Home;
import com.example.tomatogame.R;
/**
 * Activity representing the loading screen displayed while the game is loading.
 * This activity shows a progress bar indicating the loading progress.
 */
public class LoadingScreen extends AppCompatActivity {

    private ProgressBar progressBar;
    private int progressStatus = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading_screen);

        // Enable edge-to-edge display
        EdgeToEdge.enable(this);

        // Find the ProgressBar
        progressBar = findViewById(R.id.progressBar);

        // Set listener for window insets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Start updating progress
        updateProgress();
    }

    private void updateProgress() {
        // Update progress in a background thread
        Handler handler = new Handler();
        new Thread(() -> {
            while (progressStatus < 100) {
                progressStatus++;
                try {
                    Thread.sleep(20);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                // Update progress on the UI thread
                handler.post(() -> progressBar.setProgress(progressStatus));
                if (progressStatus == 100) {
                    startActivity(new Intent(LoadingScreen.this, Home.class));

                    Animatoo.INSTANCE.animateShrink(this);
                    finish(); // Finish this activity to prevent going back to it with the back button
                }
            }
        }).start();
    }
}
