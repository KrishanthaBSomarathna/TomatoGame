package com.example.tomatogame.Controller.ViewControllers.Game;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Vibrator;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.tomatogame.Controller.GameControllers.GameApiService;
import com.example.tomatogame.Controller.ViewControllers.Events.ButtonAnimation;
import com.example.tomatogame.Controller.ViewControllers.Events.SaveProgress;
import com.example.tomatogame.Controller.ViewControllers.Events.UpdateHearts;
import com.example.tomatogame.Model.QuestionResponse;
import com.example.tomatogame.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
/**
 * Main class where the games are coming from.
 *
 */
public class MainActivity extends AppCompatActivity {
    private GameApiService gameApiService;
    private ImageView questionImageView;
    private TextView solutionTextView;
    private TextView scoreTextView;
    private TextView heartTextView;
    private TextView timerTextView;
    private Button[] answerButtons;
    private int correctAnswer;
    private int score = 0;
    String higherScore = "0";
    private int wrongAnswersCount = 0;
    private int remainingAttempts = 3; // Initially set to 3
    private CountDownTimer countDownTimer;
    private MediaPlayer buttonClickSound;
    private MediaPlayer backgroundMusicPlayer;
    private Vibrator vibrator; // Declare Vibrator object

    private ImageButton restartButton,exitButton;
    private FirebaseUser firebaseUser;
    private DatabaseReference databaseReference;
    private String userID="";

    SaveProgress saveProgress = SaveProgress.getInstance(this);


    ButtonAnimation buttonAnimation;

    UpdateHearts updateHearts;
    private SaveProgress saveProgressInstance;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });



       // Initialize UpdateHearts object
        updateHearts = new UpdateHearts();





        backgroundMusicPlayer = MediaPlayer.create(this, R.raw.gamebg);
        backgroundMusicPlayer.setLooping(true); // Loop the background music
        backgroundMusicPlayer.start(); // Start playing the background music

        //Initialize firebase
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference();


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
                    ButtonAnimation.animateButton(v); // Animate the button
                    int selectedAnswer = Integer.parseInt(((Button) v).getText().toString());
                    if (selectedAnswer == correctAnswer) {
                        score++;
                        scoreTextView.setText(String.valueOf(score));
                        fetchQuestion();
                    } else {
                        // Vibrate for 1 second
                        vibrator.vibrate(500);
                        wrongAnswersCount++;
                        remainingAttempts--;
                        updateHearts.update(remainingAttempts,heartTextView); // Update hearts after wrong answer
                        if (remainingAttempts == 0) {

                            showPopupWindow("You have selected three times wrong Answer");
                            if(score>Integer.parseInt(higherScore)) {

                                saveProgress.saveProgress(String.valueOf(score));


                            }
                        } else {
                            Toast.makeText(MainActivity.this, "Incorrect Answer", Toast.LENGTH_SHORT).show();
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

        gameApiService = retrofit.create(GameApiService.class);

        // Initialize button click sound
        buttonClickSound = MediaPlayer.create(this, R.raw.btnclick);

        // Initialize Vibrator object
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        // Fetch question data and start timer
        fetchQuestion();
        startTimer();

        // Retrieve the score
        if(firebaseUser != null){

            userID = firebaseUser.getUid();
            retrieveScore();
        }

    }

    private void retrieveScore() {
        DatabaseReference userRef = databaseReference.child("User").child(userID);
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    higherScore = dataSnapshot.child("Score").getValue(String.class);
                    Log.d(TAG, "Score: " + higherScore);
                } else {
                    Log.d(TAG, "User data not found for phone number: " + userID);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, "retrieveScore:onCancelled", databaseError.toException());
            }
        });
    }



    private void playButtonClickSound() {
        if (buttonClickSound != null) {
            buttonClickSound.start();
        }
    }



    private void fetchQuestion() {
        Call<QuestionResponse> call = gameApiService.getQuestion();
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
                        solutionTextView.setText("" + correctAnswer);
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



    private void showPopupWindow( String message) {
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.show_popup_windows, null);

        int width = LinearLayout.LayoutParams.MATCH_PARENT;
        int height = LinearLayout.LayoutParams.MATCH_PARENT;
        boolean focusable = true;

        PopupWindow popupWindow = new PopupWindow(popupView, width, height, focusable);

        // Initialize views from the popup layout
        TextView scoreTextView = popupView.findViewById(R.id.scoreTextView);
        restartButton = popupView.findViewById(R.id.restartButton);
        exitButton = popupView.findViewById(R.id.exitButton);
        TextView popuptext = popupView.findViewById(R.id.popuptext);

        popuptext.setText(message);
        ButtonAnimation.startLoopAnimations(restartButton);
        // Set score text
        scoreTextView.setText(String.valueOf(score));

        // Set click listeners for buttons
        restartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                restartGame();
                popupWindow.dismiss();
            }
        });

        exitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // Show the popup window
        popupWindow.showAtLocation(popupView, Gravity.CENTER, 0, 0);
    }

    private void restartGame() {
        score = 0;
        wrongAnswersCount = 0;
        remainingAttempts = 3;
        scoreTextView.setText(String.valueOf(score));
        updateHearts.update(remainingAttempts,heartTextView);
        fetchQuestion();
        resetTimer();
    }

    private void startTimer() {
        long initialTime = 0;

        if (score < 10) {
            initialTime = 100000;
        } else if (score < 20) {
            initialTime = 90000;
        } else if (score < 30) {
            initialTime = 80000;
        } else if (score < 40) {
            initialTime = 70000;
        } else if (score < 50) {
            initialTime = 60000;
        } else if (score < 60) {
            initialTime = 50000;
        }else if (score < 70) {
            initialTime = 40000;
        }else if (score < 80) {
            initialTime = 30000;
        }
        else {
            initialTime = 20000;
        }

        resetTimer(initialTime);
    }






    private void resetTimer() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
            startTimer();
        }
    }

    private void resetTimer(long timeInMillis) {
        if (countDownTimer != null) {
            countDownTimer.cancel(); // Cancel the current timer
        }

        // Start a new timer with the specified time duration
        countDownTimer = new CountDownTimer(timeInMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                // Update timer text view with remaining time
                timerTextView.setText(String.valueOf(millisUntilFinished / 1000));
            }

            @Override
            public void onFinish() {
                // Timer finished, show time-over dialog
                showPopupWindow("You have not selected an answer within times.");

                saveProgress.saveProgress(String.valueOf(score));


            }
        };

        countDownTimer.start(); // Start the new timer
    }
    @Override
    public void onBackPressed() {
        saveProgress.saveProgress(String.valueOf(score));
        super.onBackPressed();
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
