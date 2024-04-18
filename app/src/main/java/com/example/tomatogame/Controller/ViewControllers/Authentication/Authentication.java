package com.example.tomatogame.Controller.ViewControllers.Authentication;



import android.animation.ObjectAnimator;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;


import com.example.tomatogame.Controller.ViewControllers.Help.Help;
import com.example.tomatogame.Controller.ViewControllers.Home.Home;
import com.example.tomatogame.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.concurrent.TimeUnit;
/**
 * Activity for user sign-up with phone number verification.
 * This activity handles Firebase authentication for user sign-up and phone number verification.
 */
public class Authentication extends AppCompatActivity {
    private static final String TAG = "GoogleSignInActivity";
    private static final int RC_SIGN_IN = 123;
    private FirebaseAuth mAuth;
    private TextView userNameTextView;
    private GoogleSignInClient mGoogleSignInClient;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    private String verificationId;
    EditText editTextPhoneNumber, editTextVerificationCode,editTextUserName;
    private Button buttonSendOTP, buttonVerifyOTP;

    LinearLayout otp, sendotp;
    private ProgressBar progressBar;

    private TextView authfail;
    String fullPhoneNumber;
    private ObjectAnimator zoomInX, zoomOutX, zoomInY, zoomOutY;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_authentication);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        mAuth = FirebaseAuth.getInstance();


        // Configure Google Sign-In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        findViewById(R.id.google_sign_in_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signInWithGoogle();
            }
        });


        otp = findViewById(R.id.otplayout);
        sendotp = findViewById(R.id.phonelayout);

        otp.setVisibility(View.GONE);

        mAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();

        authfail = findViewById(R.id.authfail);
        authfail.setVisibility(View.GONE);

        editTextPhoneNumber = findViewById(R.id.editTextPhoneNumber);
        editTextVerificationCode = findViewById(R.id.editTextVerificationCode);
        editTextUserName = findViewById(R.id.editTextUserName);
        buttonSendOTP = findViewById(R.id.buttonSendOTP);
        buttonVerifyOTP = findViewById(R.id.buttonVerifyOTP);
        progressBar = findViewById(R.id.progressBar);

        buttonSendOTP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String phoneNumber = editTextPhoneNumber.getText().toString().trim();
                String username = editTextUserName.getText().toString().trim();
                authfail.setVisibility(View.GONE);

                if (username.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Enter Username", Toast.LENGTH_LONG).show();
                } if (phoneNumber.isEmpty()){
                    Toast.makeText(getApplicationContext(), "Enter Phone Number", Toast.LENGTH_LONG).show();

                }else {
                    sendVerificationCode(); // Call the function to send the OTP
                }
            }
        });

        buttonVerifyOTP.setOnClickListener(v -> {
            String otp = editTextVerificationCode.getText().toString().trim();

            if (!otp.isEmpty()) {
                progressBar.setVisibility(View.VISIBLE);
                buttonVerifyOTP.setVisibility(View.GONE);
                String code = editTextVerificationCode.getText().toString().trim();
                verifyVerificationCode(code);
            } else {
                Toast.makeText(getApplicationContext(), "Enter OTP CODE", Toast.LENGTH_LONG).show();
            }
        });

        startLoopAnimations(); // Start animations
    }



    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
//        updateUI(currentUser);
    }

    private void signInWithGoogle() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account.getIdToken());
                Home.registerButton.setVisibility(View.GONE);

            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e);
//                updateUI(null);
            }
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();


                            updateDB(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());

                        }
                    }
                });
    }

    private void updateDB(FirebaseUser user) {
        if (user != null) {
            // User is signed in
            String firstName = user.getDisplayName().split(" ")[0];
            String userID = user.getUid();

            databaseReference.child("User").child(userID).child("UserName").setValue(firstName);
            databaseReference.child("User").child(userID).child("Score").setValue("0");
            startActivity(new Intent(getApplicationContext(), Home.class));

        } else {
            // User is signed out
            userNameTextView.setText("Welcome");
        }
    }





    private void sendVerificationCode() {
        // Get the entered phone number
        String phoneNumber = editTextPhoneNumber.getText().toString().trim();
        // Concatenate the country code and phone number
        fullPhoneNumber = "+94" + phoneNumber;
        progressBar.setVisibility(View.VISIBLE);
        editTextUserName.setVisibility(View.GONE);
        editTextPhoneNumber.setVisibility(View.GONE);
        buttonSendOTP.setVisibility(View.GONE);

        // Initiate phone number verification
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                fullPhoneNumber,   // The combined phone number with country code
                60,                // Timeout duration
                TimeUnit.SECONDS,  // Timeout unit
                this,              // Activity
                new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                    @Override
                    public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                        signInWithPhoneAuthCredential(phoneAuthCredential);
                        progressBar.setVisibility(View.GONE);

                    }

                    @Override
                    public void onVerificationFailed(@NonNull FirebaseException e) {
                        try {
                            Toast.makeText(getApplicationContext(), "Verification Failed!" + e.getMessage(), Toast.LENGTH_SHORT).show();
                            authfail.setVisibility(View.VISIBLE);
                            progressBar.setVisibility(View.GONE);
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }

                    @Override
                    public void onCodeSent(@NonNull String verificationId, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                        Authentication.this.verificationId = verificationId;
                        Toast.makeText(getApplicationContext(), "OTP Code Sent", Toast.LENGTH_SHORT).show();
                        otp.setVisibility(View.VISIBLE);
                        sendotp.setVisibility(View.GONE);
                    }
                });
    }


    private void verifyVerificationCode(String code) {
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
        signInWithPhoneAuthCredential(credential);
        progressBar.setVisibility(View.VISIBLE);
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        String username = editTextUserName.getText().toString();
                        // Phone authentication successful
                        databaseReference.child("User").child(fullPhoneNumber).child("UserName").setValue(username);
                        databaseReference.child("User").child(fullPhoneNumber).child("Score").setValue("0");
                       startActivity(new Intent(getApplicationContext(), Help.class));
                    } else {
                        if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                            Toast.makeText(getApplicationContext(), "OTP Code Not Matched", Toast.LENGTH_SHORT).show();// The verification code entered was invalid
                        }
                    }
                });
    }

    private void startLoopAnimations() {
        // Create zoom in and zoom out animators for the sendotp view scaleX
        zoomInX = ObjectAnimator.ofFloat(buttonSendOTP, "scaleX", 1.0f, 1.2f);
        zoomInX.setRepeatCount(ObjectAnimator.INFINITE);
        zoomInX.setRepeatMode(ObjectAnimator.REVERSE);
        zoomInX.setDuration(1000);

        zoomOutX = ObjectAnimator.ofFloat(buttonSendOTP, "scaleX", 1.2f, 1.0f);
        zoomOutX.setRepeatCount(ObjectAnimator.INFINITE);
        zoomOutX.setRepeatMode(ObjectAnimator.REVERSE);
        zoomOutX.setDuration(1000);

        // Create zoom in and zoom out animators for the sendotp view scaleY
        zoomInY = ObjectAnimator.ofFloat(buttonSendOTP, "scaleY", 1.0f, 1.2f);
        zoomInY.setRepeatCount(ObjectAnimator.INFINITE);
        zoomInY.setRepeatMode(ObjectAnimator.REVERSE);
        zoomInY.setDuration(1500);

        zoomOutY = ObjectAnimator.ofFloat(buttonSendOTP, "scaleY", 1.2f, 1.0f);
        zoomOutY.setRepeatCount(ObjectAnimator.INFINITE);
        zoomOutY.setRepeatMode(ObjectAnimator.REVERSE);
        zoomOutY.setDuration(1500);

        // Start the animations for sendotp view
        zoomInX.start();
        zoomOutX.start();
        zoomInY.start();
        zoomOutY.start();
    }
}
