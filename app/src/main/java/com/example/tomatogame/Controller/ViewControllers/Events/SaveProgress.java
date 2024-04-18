package com.example.tomatogame.Controller.ViewControllers.Events;

import android.content.Context;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SaveProgress {

    // Singleton instance
    private static SaveProgress instance;

    // Context to display toast messages
    private Context context;

    // Private constructor to prevent instantiation from outside
    private SaveProgress(Context context) {
        this.context = context;
    }

    // Static method to get the singleton instance
    public static SaveProgress getInstance(Context context) {
        if (instance == null) {
            instance = new SaveProgress(context);
        }
        return instance;
    }

    // Method to save progress
    public void saveProgress(String score) {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        // Check if user is authenticated
        if (firebaseUser != null) {
            String userID = firebaseUser.getUid();

            // Check if userID is not null
            if (userID != null) {
                // Get reference to the Firebase Database
                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
                // Save score under the user's ID
                databaseReference.child("User").child(userID).child("Score").setValue(score);
            } else {
                // Display toast message if userID is null
                Toast.makeText(context, "User ID is null. Unable to save progress.", Toast.LENGTH_LONG).show();
            }
        } else {
            // Display toast message if user is not authenticated
            Toast.makeText(context, "Register to Save Progress", Toast.LENGTH_LONG).show();
        }
    }
}
