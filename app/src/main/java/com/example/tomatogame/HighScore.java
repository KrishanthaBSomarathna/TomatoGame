package com.example.tomatogame;

import static android.content.ContentValues.TAG;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class HighScore extends AppCompatActivity {
    DatabaseReference database;
    TextView player1Name, player2Name, player3Name, player4Name, player5Name;
    TextView player1Score, player2Score, player3Score, player4Score, player5Score, myScore;
    String higherScore = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_high_score);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        player1Name = findViewById(R.id.player1Name);
        player2Name = findViewById(R.id.player2Name);
        player3Name = findViewById(R.id.player3Name);
        player4Name = findViewById(R.id.player4Name);
        player5Name = findViewById(R.id.player5Name);

        player1Score = findViewById(R.id.player1Score);
        player2Score = findViewById(R.id.player2Score);
        player3Score = findViewById(R.id.player3Score);
        player4Score = findViewById(R.id.player4Score);
        player5Score = findViewById(R.id.player5Score);
        myScore = findViewById(R.id.myScore);

        // Initialize Firebase database reference
        database = FirebaseDatabase.getInstance().getReference();

        // get userScore
        retrieveScore();

        // Retrieve data from Firebase Realtime Database
        database.child("User")
                .orderByChild("Score")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        ArrayList<User> topPlayers = new ArrayList<>();
                        for (DataSnapshot playerSnapshot : snapshot.getChildren()) {
                            String userName = playerSnapshot.child("UserName").getValue(String.class);
                            String score = playerSnapshot.child("Score").getValue(String.class);
                            topPlayers.add(new User(userName, score));
                        }

                        // Sort the list of users based on their score
                        topPlayers.sort((user1, user2) -> Integer.compare(Integer.parseInt(user2.getScore()), Integer.parseInt(user1.getScore())));

                        // Now topPlayers list contains all users sorted by score in descending order
                        // Displaying the top 5 players in the UI
                        if (topPlayers.size() >= 5) {
                            player1Name.setText(topPlayers.get(0).getUserName());
                            player1Score.setText(topPlayers.get(0).getScore());
                            player2Name.setText(topPlayers.get(1).getUserName());
                            player2Score.setText(topPlayers.get(1).getScore());
                            player3Name.setText(topPlayers.get(2).getUserName());
                            player3Score.setText(topPlayers.get(2).getScore());
                            player4Name.setText(topPlayers.get(3).getUserName());
                            player4Score.setText(topPlayers.get(3).getScore());
                            player5Name.setText(topPlayers.get(4).getUserName());
                            player5Score.setText(topPlayers.get(4).getScore());
                        }

                        Log.d("TopPlayers", "Player 1 Name: " + player1Name + ", Score: " + player1Score);
                        Log.d("TopPlayers", "Player 2 Name: " + player2Name + ", Score: " + player2Score);
                        Log.d("TopPlayers", "Player 3 Name: " + player3Name + ", Score: " + player3Score);
                        Log.d("TopPlayers", "Player 4 Name: " + player4Name + ", Score: " + player4Score);
                        Log.d("TopPlayers", "Player 5 Name: " + player5Name + ", Score: " + player5Score);
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {
                        // Handle error
                    }
                });
    }

    private void retrieveScore() {
        String phoneNumber = FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber();
        DatabaseReference userRef = database.child("User").child(phoneNumber);
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    higherScore = dataSnapshot.child("Score").getValue(String.class);
                    Log.d(TAG, "Score: " + higherScore);
                    myScore.setText(higherScore);
                } else {
                    Log.d(TAG, "User data not found for phone number: " + phoneNumber);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, "retrieveScore:onCancelled", databaseError.toException());
            }
        });
    }
}


class User {
    private String userName;
    private String score;

    public User(String userName, String score) {
        this.userName = userName;
        this.score = score;
    }

    public String getUserName() {
        return userName;
    }

    public String getScore() {
        return score;
    }
}