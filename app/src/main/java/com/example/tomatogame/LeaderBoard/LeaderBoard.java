package com.example.tomatogame.LeaderBoard;

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

import com.example.tomatogame.Model.Users;
import com.example.tomatogame.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * Activity that displays the leaderboard of top players.
 * This activity retrieves user scores from Firebase Realtime Database and displays them.
 */
public class LeaderBoard extends AppCompatActivity {
    DatabaseReference database;
    TextView player1Name, player2Name, player3Name, player4Name, player5Name;
    TextView player1Score, player2Score, player3Score, player4Score, player5Score,myScore ;
    String higherScore = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_leader_board);
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
        // Retrieve data from Firebase Realtime Database
        database.child("User")
                .orderByChild("Score")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        ArrayList<Users> topPlayers = new ArrayList<>();
                        for (DataSnapshot playerSnapshot : snapshot.getChildren()) {
                            String userName = playerSnapshot.child("UserName").getValue(String.class);
                            String score = playerSnapshot.child("Score").getValue(String.class);
                            // Check if score is not null before adding it
                            if (score != null) {
                                topPlayers.add(new Users(userName, score));
                            }
                        }

                        // Sort the list of users based on their score
                        topPlayers.sort((user1, user2) -> {
                            // Convert scores to integers before comparing
                            int score1 = Integer.parseInt(user1.getScore());
                            int score2 = Integer.parseInt(user2.getScore());
                            return Integer.compare(score2, score1); // Compare in descending order
                        });

                        // Now topPlayers list contains all users sorted by score in descending order
                        // Displaying the top 5 players in the UI
                        if (!topPlayers.isEmpty()) {
                            int size = Math.min(topPlayers.size(), 5); // Ensure size is not greater than 5
                            for (int i = 0; i < size; i++) {
                                Users user = topPlayers.get(i);
                                switch (i) {
                                    case 0:
                                        player1Name.setText(user.getUserName());
                                        player1Score.setText(user.getScore());
                                        break;
                                    case 1:
                                        player2Name.setText(user.getUserName());
                                        player2Score.setText(user.getScore());
                                        break;
                                    case 2:
                                        player3Name.setText(user.getUserName());
                                        player3Score.setText(user.getScore());
                                        break;
                                    case 3:
                                        player4Name.setText(user.getUserName());
                                        player4Score.setText(user.getScore());
                                        break;
                                    case 4:
                                        player5Name.setText(user.getUserName());
                                        player5Score.setText(user.getScore());
                                        break;
                                }
                            }
                        }

                        // Log top players for debugging
                        for (int i = 0; i < topPlayers.size(); i++) {
                            Users user = topPlayers.get(i);
                            Log.d("TopPlayers", "Player " + (i + 1) + " Name: " + user.getUserName() + ", Score: " + user.getScore());
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {
                        // Handle error
                    }
                });
        retrieveScore();
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



