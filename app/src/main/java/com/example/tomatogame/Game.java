package com.example.tomatogame;

import android.content.DialogInterface;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.squareup.picasso.Picasso;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Game extends AppCompatActivity {
    private ApiService apiService;
    private ImageView questionImageView;
    private TextView solutionTextView;
    private TextView scoreTextView;
    private TextView heartTextView;
    private TextView timerTextView;
    private Button[] answerButtons;
    private int correctAnswer;
    private int score = 0;
    private int wrongAnswersCount = 0;
    private int remainingAttempts = 3; // Initially set to 3
    private CountDownTimer countDownTimer;
    private MediaPlayer buttonClickSound;
    private MediaPlayer backgroundMusicPlayer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_game);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        backgroundMusicPlayer = MediaPlayer.create(this, R.raw.gamebg);
        backgroundMusicPlayer.setLooping(true); // Loop the background music
        backgroundMusicPlayer.start(); // Start playing the background music

        // Initialize views
        questionImageView = findViewById(R.id.question_image_view);
        solutionTextView = findViewById(R.id.solution_text_view);
        scoreTextView = findViewById(R.id.score_text_view);
        heartTextView = findViewById(R.id.heart_text_view);
        timerTextView = findViewById(R.id.timer_text_view);

        // Initialize answer buttons
        answerButtons = new Button[]{
                findViewById(R.id.button0),
                findViewById(R.id.button1),
                findViewById(R.id.button2),
                findViewById(R.id.button3),
                findViewById(R.id.button4),
                findViewById(R.id.button5),
                findViewById(R.id.button6),
                findViewById(R.id.button7),
                findViewById(R.id.button8),
                findViewById(R.id.button9)
        };

        // Set click listeners for answer buttons
        for (Button button : answerButtons) {
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    playButtonClickSound(); // Play button click sound
                    animateButton(v); // Animate the button
                    int selectedAnswer = Integer.parseInt(((Button) v).getText().toString());
                    if (selectedAnswer == correctAnswer) {
                        score++;
                        scoreTextView.setText(String.valueOf(score));
                        fetchQuestion();
                    } else {
                        wrongAnswersCount++;
                        remainingAttempts--;
                        updateHearts(); // Update hearts after wrong answer
                        if (remainingAttempts == 0) {
                            showGameOverDialog();
                        } else {
                            Toast.makeText(Game.this, "Incorrect Answer", Toast.LENGTH_SHORT).show();
                        }
                    }
                    resetTimer();
                }
            });
        }

        // Initialize Retrofit
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://marcconrad.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        apiService = retrofit.create(ApiService.class);

        // Initialize button click sound
        buttonClickSound = MediaPlayer.create(this, R.raw.btnclick);

        // Fetch question data and start timer
        fetchQuestion();
        startTimer();
    }

    private void playButtonClickSound() {
        if (buttonClickSound != null) {
            buttonClickSound.start();
        }
    }

    private void animateButton(View view) {
        Animation animation = new ScaleAnimation(1.0f, 0.9f, 1.0f, 0.9f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        animation.setDuration(100);
        animation.setRepeatMode(Animation.REVERSE);
        animation.setRepeatCount(1);
        view.startAnimation(animation);
    }

    private void fetchQuestion() {
        Call<QuestionResponse> call = apiService.getQuestion();
        call.enqueue(new Callback<QuestionResponse>() {
            @Override
            public void onResponse(Call<QuestionResponse> call, Response<QuestionResponse> response) {
                if (response.isSuccessful()) {
                    QuestionResponse questionResponse = response.body();
                    if (questionResponse != null) {
                        // Display the question image
                        String questionImageUrl = questionResponse.getQuestionImageUrl();
                        Picasso.get().load(questionImageUrl).into(questionImageView);

                        // Display the solution
                        correctAnswer = questionResponse.getSolution();
                        solutionTextView.setText("Solution: " + correctAnswer);
                    }
                } else {
                    // Handle unsuccessful response
                }
            }

            @Override
            public void onFailure(Call<QuestionResponse> call, Throwable t) {
                // Handle failure
            }
        });
    }



    private void updateHearts() {
        StringBuilder hearts = new StringBuilder();
        for (int i = 0; i < remainingAttempts; i++) {
            hearts.append("❤️ ");
        }
        heartTextView.setText(hearts.toString());
    }

    private void showGameOverDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Game Over")
                .setMessage("You have given three wrong answers. Your total score is " + score +
                        ". Do you want to restart the game?")
                .setPositiveButton("Restart", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        restartGame();
                    }
                })
                .setNegativeButton("Exit", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                })
                .setCancelable(false)
                .show();
    }


    private void restartGame() {
        score = 0;
        wrongAnswersCount = 0;
        remainingAttempts = 3;
        scoreTextView.setText(String.valueOf(score));
        updateHearts();
        fetchQuestion();
        resetTimer();
        // Restart timer
    }

    private void startTimer() {
        countDownTimer = new CountDownTimer(30000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                // Update timer text view with remaining time
                timerTextView.setText(String.valueOf(millisUntilFinished / 1000));
            }

            @Override
            public void onFinish() {
                // Timer finished, show time-over dialog
                showTimeOverDialog();
            }
        }.start();
    }

    private void resetTimer() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
            startTimer();
        }
    }

    private void showTimeOverDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Time Over")
                .setMessage("You have not selected an answer within 30 seconds. Your total score is " + score +
                        ". Do you want to restart the game?")
                .setPositiveButton("Restart", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        restartGame();
                    }
                })
                .setNegativeButton("Exit", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                })
                .setCancelable(false)
                .show();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
        // Release the MediaPlayer resources
        if (buttonClickSound != null) {
            buttonClickSound.release();
            buttonClickSound = null;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Release the MediaPlayer resources
        if (backgroundMusicPlayer != null) {
            backgroundMusicPlayer.release();
            backgroundMusicPlayer = null;
        }
        // Your existing code...
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Start background music
        startBackgroundMusic();
    }

    private void startBackgroundMusic() {
        // Release previous instance to avoid potential issues
        if (backgroundMusicPlayer != null) {
            backgroundMusicPlayer.release();
        }
        // Initialize and start background music
        backgroundMusicPlayer = MediaPlayer.create(this, R.raw.gamebg);
        backgroundMusicPlayer.setLooping(true); // Loop the background music
        backgroundMusicPlayer.start();
    }
}