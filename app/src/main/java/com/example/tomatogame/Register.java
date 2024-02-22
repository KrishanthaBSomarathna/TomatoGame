package com.example.tomatogame;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;


import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.concurrent.TimeUnit;

public class Register extends AppCompatActivity {

    FirebaseAuth mAuth;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    private String verificationId;
    EditText editTextPhoneNumber, editTextVerificationCode,username;
    private Button buttonSendOTP, buttonVerifyOTP;

    LinearLayout otp,sendotp;
    private ProgressBar progressBar;

    private TextView authfail;
    String fullPhoneNumber;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

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
        username = findViewById(R.id.username);
        buttonSendOTP = findViewById(R.id.buttonSendOTP);
        buttonVerifyOTP = findViewById(R.id.buttonVerifyOTP);
        progressBar = findViewById(R.id.progressBar);


        buttonSendOTP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String phoneNumber = editTextPhoneNumber.getText().toString().trim();
                authfail.setVisibility(View.GONE);

                if (phoneNumber.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Enter Phone Number", Toast.LENGTH_LONG).show();
                } else {

                        sendVerificationCode(); // Call the function to send the OTP

                }
            }
        });




        buttonVerifyOTP.setOnClickListener(v -> {
            String otp = editTextVerificationCode.getText().toString().trim();

            if (!otp.isEmpty())
            {
                progressBar.setVisibility(View.VISIBLE);
                String code = editTextVerificationCode.getText().toString().trim();
                verifyVerificationCode(code);
            }
            else {
                Toast.makeText(getApplicationContext(), "Enter OTP CODE", Toast.LENGTH_LONG).show();
            }


        });
    }
    public void onBackPressed() {
        // Handle back button press, navigate to home screen
        super.onBackPressed();
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    private void sendVerificationCode() {
        // Get the selected country code from the Spinner
        String selectedCountryCode = ((Spinner) findViewById(R.id.spinnerCountryCode)).getSelectedItem().toString();

        // Get the entered phone number
        String phoneNumber = editTextPhoneNumber.getText().toString().trim();

        // Remove non-numeric characters from the country code
        String countryCode = selectedCountryCode.replaceAll("[^0-9]", "");

        // Concatenate the country code and phone number
        fullPhoneNumber = "+" + countryCode + phoneNumber;

        progressBar.setVisibility(View.VISIBLE);

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
                        Register.this.verificationId = verificationId;
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
                        // Phone authentication successful
                        String userName = username.getText().toString();
                        databaseReference.child("Passenger").child(fullPhoneNumber).child("UserName").setValue(userName);

                        startActivity(new Intent(Register.this, Home.class));

                    } else {
                        if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                            Toast.makeText(getApplicationContext(), "OTP Code Not Matched", Toast.LENGTH_SHORT).show();// The verification code entered was invalid
                        }
                    }
                });
    }

}