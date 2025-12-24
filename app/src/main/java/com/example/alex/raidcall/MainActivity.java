package com.example.alex.raidcall;

import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {
    private EditText mEmailField;
    private EditText mPasswordField;
    private Button mRegisterBtn;
    private Button mNewRegBtn;
    private DatabaseReference mDatabaseUsers;
    private TextView FPass;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mEmailField = (EditText) findViewById(R.id.EmailField);
        mPasswordField = (EditText) findViewById(R.id.PasswordField);
        mRegisterBtn = (Button) findViewById(R.id.RegisterBtn);
        mNewRegBtn = (Button) findViewById(R.id.NewregButton);
        mDatabaseUsers = FirebaseDatabase.getInstance().getReference().child("Users");
        FPass = (TextView) findViewById(R.id.FPass);
        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser() != null) {
                    Intent SignIntent = new Intent(MainActivity.this, RaidActivity.class);
                    SignIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(SignIntent);
                    ActivityCompat.finishAffinity(MainActivity.this);
                }
            }
        };
        mRegisterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startSignIn();
            }
        });
        mNewRegBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View a) {
                startActivity(new Intent(MainActivity.this, RegisterActivity.class));
            }
        });
        FPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, ForgotAcitivity.class ));
            }
        });
    }

    @Override
    protected void onStart(){
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    private void startSignIn(){
        String email = mEmailField.getText().toString();
        String Password = mPasswordField.getText().toString();
        if(TextUtils.isEmpty(email) || TextUtils.isEmpty(Password)){
            Toast.makeText(MainActivity.this, "One or more of the fields was empty.", Toast.LENGTH_SHORT).show();
            return;
        }
        mAuth.signInWithEmailAndPassword(email, Password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(MainActivity.this, "Welcome, you are signed in", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(MainActivity.this, "There was a sign in problem", Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}
