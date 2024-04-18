package com.example.tomatogame.Controller.ViewControllers.Home;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.example.tomatogame.Controller.ViewControllers.Game.MainActivity;
import com.example.tomatogame.Controller.ViewControllers.Help.Help;
import com.example.tomatogame.Controller.ViewControllers.LeaderBoard.LeaderBoard;
import com.example.tomatogame.R;
import com.example.tomatogame.Controller.ViewControllers.Authentication.Authentication;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
/**
 * Main activity representing the home screen of the game.
 * This activity handles user authentication, background music, and button animations.
 */
public class Home extends AppCompatActivity {

    public static ImageButton registerButton;

    private ImageButton playButton,signOut;
    private Button HighScore,Help;
    private MediaPlayer backgroundMediaPlayer;
    private MediaPlayer buttonClickMediaPlayer;

    // Animators for the buttons
    private ObjectAnimator zoomInPlayX, zoomOutPlayX, zoomInPlayY, zoomOutPlayY;
    private ObjectAnimator zoomInRegisterX, zoomOutRegisterX, zoomInRegisterY, zoomOutRegisterY;
    private ObjectAnimator zoomInButtoX, zoomOutButtoX, zoomInButtoY, zoomOutButtoY;

    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;

    LinearLayout info;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        info = findViewById(R.id.info);
        HighScore = findViewById(R.id.button);
        Help = findViewById(R.id.help);
        signOut = findViewById(R.id.signout);


//        signOut.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                startBounceAnimation(signOut);
//                firebaseAuth.signOut();
//                signOut.setVisibility(View.GONE);
//            }
//        });
        HighScore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startBounceAnimation(HighScore);
                startActivity(new Intent(getApplicationContext(), LeaderBoard.class));
            }
        });
        Help.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startBounceAnimation(Help);
                startActivity(new Intent(getApplicationContext(), com.example.tomatogame.Controller.ViewControllers.Help.Help.class));

            }
        });




        if(firebaseUser == null){
            HighScore.setVisibility(View.GONE);
        }else {
            HighScore.setVisibility(View.VISIBLE);

        }

        // Initialize MediaPlayer with background music
        backgroundMediaPlayer = MediaPlayer.create(this, R.raw.bgsound);
        backgroundMediaPlayer.setLooping(true);
        backgroundMediaPlayer.start();

        // Initialize MediaPlayer with button click sound
        buttonClickMediaPlayer = MediaPlayer.create(this, R.raw.btnclick);

        registerButton = findViewById(R.id.registerButton);
        playButton = findViewById(R.id.playButton);

        if (firebaseUser != null) {
            registerButton.setVisibility(View.GONE);
        }

        // Start looping animations
        startLoopAnimations();

        // Enable edge-to-edge display
        EdgeToEdge.enable(this);

        // Apply window insets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Set click listeners for buttons
        registerButton.setOnClickListener(v -> {
            // Start a "bounce" animation
            startBounceAnimation(registerButton);

            playButtonClickSound(() -> {
                // Start the new activity after the sound completes
                Intent intent = new Intent(Home.this, Authentication.class);
                startActivity(intent);
                Animatoo.INSTANCE.animateSplit(this);
            });
        });

        playButton.setOnClickListener(v -> {
            // Start a "bounce" animation
            startBounceAnimation(playButton);
            playButtonClickSound(() -> {
                // Start the new activity after the sound completes
                Intent intent = new Intent(Home.this, MainActivity.class);
                startActivity(intent);
                Animatoo.INSTANCE.animateSplit(this);
            });
        });


    }

    private void playButtonClickSound(Runnable onCompletionListener) {
        buttonClickMediaPlayer = MediaPlayer.create(this, R.raw.btnclick);
        if (buttonClickMediaPlayer != null) {
            buttonClickMediaPlayer.setOnCompletionListener(mp -> {
                mp.release(); // Release MediaPlayer resources
                if (onCompletionListener != null) {
                    onCompletionListener.run(); // Execute the callback
                }
            });
            buttonClickMediaPlayer.start();
        }
    }

    private void startLoopAnimations() {
        // Create zoom in and zoom out animators for the playButton scaleX
        zoomInPlayX = ObjectAnimator.ofFloat(playButton, "scaleX", 1.0f, 1.2f);
        zoomInPlayX.setRepeatCount(ObjectAnimator.INFINITE);
        zoomInPlayX.setRepeatMode(ObjectAnimator.REVERSE);
        zoomInPlayX.setDuration(1000);

        zoomOutPlayX = ObjectAnimator.ofFloat(playButton, "scaleX", 1.2f, 1.0f);
        zoomOutPlayX.setRepeatCount(ObjectAnimator.INFINITE);
        zoomOutPlayX.setRepeatMode(ObjectAnimator.REVERSE);
        zoomOutPlayX.setDuration(1000);

        // Create zoom in and zoom out animators for the playButton scaleY
        zoomInPlayY = ObjectAnimator.ofFloat(playButton, "scaleY", 1.0f, 1.2f);
        zoomInPlayY.setRepeatCount(ObjectAnimator.INFINITE);
        zoomInPlayY.setRepeatMode(ObjectAnimator.REVERSE);
        zoomInPlayY.setDuration(1500);

        zoomOutPlayY = ObjectAnimator.ofFloat(playButton, "scaleY", 1.2f, 1.0f);
        zoomOutPlayY.setRepeatCount(ObjectAnimator.INFINITE);
        zoomOutPlayY.setRepeatMode(ObjectAnimator.REVERSE);
        zoomOutPlayY.setDuration(1500);

        // Start the animations for playButton
        zoomInPlayX.start();
        zoomOutPlayX.start();
        zoomInPlayY.start();
        zoomOutPlayY.start();

        // Create zoom in and zoom out animators for the registerButton scaleX
        zoomInRegisterX = ObjectAnimator.ofFloat(registerButton, "scaleX", 1.0f, 1.2f);
        zoomInRegisterX.setRepeatCount(ObjectAnimator.INFINITE);
        zoomInRegisterX.setRepeatMode(ObjectAnimator.REVERSE);
        zoomInRegisterX.setDuration(1000);

        zoomOutRegisterX = ObjectAnimator.ofFloat(registerButton, "scaleX", 1.2f, 1.0f);
        zoomOutRegisterX.setRepeatCount(ObjectAnimator.INFINITE);
        zoomOutRegisterX.setRepeatMode(ObjectAnimator.REVERSE);
        zoomOutRegisterX.setDuration(1000);

        // Create zoom in and zoom out animators for the registerButton scaleY
        zoomInRegisterY = ObjectAnimator.ofFloat(registerButton, "scaleY", 1.0f, 1.2f);
        zoomInRegisterY.setRepeatCount(ObjectAnimator.INFINITE);
        zoomInRegisterY.setRepeatMode(ObjectAnimator.REVERSE);
        zoomInRegisterY.setDuration(1500);

        zoomOutRegisterY = ObjectAnimator.ofFloat(registerButton, "scaleY", 1.2f, 1.0f);
        zoomOutRegisterY.setRepeatCount(ObjectAnimator.INFINITE);
        zoomOutRegisterY.setRepeatMode(ObjectAnimator.REVERSE);
        zoomOutRegisterY.setDuration(1500);

        // Start the animations for registerButton
        zoomInRegisterX.start();
        zoomOutRegisterX.start();
        zoomInRegisterY.start();
        zoomOutRegisterY.start();

        // Create zoom in and zoom out animators for the butto scaleX
        zoomInButtoX = ObjectAnimator.ofFloat(info, "scaleX", 1.0f, 1.2f);
        zoomInButtoX.setRepeatCount(ObjectAnimator.INFINITE);
        zoomInButtoX.setRepeatMode(ObjectAnimator.REVERSE);
        zoomInButtoX.setDuration(1000);

        zoomOutButtoX = ObjectAnimator.ofFloat(info, "scaleX", 1.2f, 1.0f);
        zoomOutButtoX.setRepeatCount(ObjectAnimator.INFINITE);
        zoomOutButtoX.setRepeatMode(ObjectAnimator.REVERSE);
        zoomOutButtoX.setDuration(1000);

        // Create zoom in and zoom out animators for the butto scaleY
        zoomInButtoY = ObjectAnimator.ofFloat(info, "scaleY", 1.0f, 1.2f);
        zoomInButtoY.setRepeatCount(ObjectAnimator.INFINITE);
        zoomInButtoY.setRepeatMode(ObjectAnimator.REVERSE);
        zoomInButtoY.setDuration(1500);

        zoomOutButtoY = ObjectAnimator.ofFloat(info, "scaleY", 1.2f, 1.0f);
        zoomOutButtoY.setRepeatCount(ObjectAnimator.INFINITE);
        zoomOutButtoY.setRepeatMode(ObjectAnimator.REVERSE);
        zoomOutButtoY.setDuration(1500);

        // Start the animations for butto
        zoomInButtoX.start();
        zoomOutButtoX.start();
        zoomInButtoY.start();
        zoomOutButtoY.start();
    }

    // Method to handle button click sound
    private void playButtonClickSound() {
        if (buttonClickMediaPlayer != null) {
            buttonClickMediaPlayer.start();
        }
    }

    private void startBounceAnimation(View view) {
        // Create scale up and scale down animators for the clicked view
        ObjectAnimator scaleXAnimator = ObjectAnimator.ofFloat(view, View.SCALE_X, 1f, 1.2f, 1f);
        scaleXAnimator.setDuration(300);
        ObjectAnimator scaleYAnimator = ObjectAnimator.ofFloat(view, View.SCALE_Y, 1f, 1.2f, 1f);
        scaleYAnimator.setDuration(300);

        // Play the animations together
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(scaleXAnimator, scaleYAnimator);
        animatorSet.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Release MediaPlayer resources
        if (backgroundMediaPlayer != null) {
            backgroundMediaPlayer.release();
            backgroundMediaPlayer = null;
        }
        if (buttonClickMediaPlayer != null) {
            buttonClickMediaPlayer.release();
            buttonClickMediaPlayer = null;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Start background music
        startBackgroundMusic();
    }

    private void startBackgroundMusic() {
        // Release previous instance to avoid potential issues
        if (backgroundMediaPlayer != null) {
            backgroundMediaPlayer.release();
        }
        // Initialize and start background music
        backgroundMediaPlayer = MediaPlayer.create(this, R.raw.bgsound);
        backgroundMediaPlayer.setLooping(true);
        backgroundMediaPlayer.start();
    }
}
