package com.example.alex.raidcall;

import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AccountActivity extends AppCompatActivity {

    private Button mLogOutButton;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference mDatabaseUsers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        mDatabaseUsers = FirebaseDatabase.getInstance().getReference().child("Users");

        mLogOutButton = (Button) findViewById(R.id.LogOutbtn);

        TextView nameView = (TextView) findViewById(R.id.nameView);
        TextView IGNView = (TextView) findViewById(R.id.IGNView);
        TextView EmailView = (TextView) findViewById(R.id.EmailView);

        Bundle accBundle = getIntent().getExtras();
        if (accBundle != null) {
            nameView.setText("Name: "+accBundle.getString("bamName"));
            IGNView.setText("In game Name: "+accBundle.getString("bamIGN"));
            EmailView.setText("Email: "+accBundle.getString("bamEmail"));
        }

        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                if (firebaseAuth.getCurrentUser() == null) {

                    startActivity(new Intent(AccountActivity.this, MainActivity.class));
                }
            }
        };

        mLogOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logOut();
            }
        });
    }

    private void logOut() {
        mDatabaseUsers.child(mAuth.getCurrentUser().getUid()).child("pushToken").setValue("123ABC");

        mAuth.signOut();
        Toast.makeText(AccountActivity.this, "You Logged Out", Toast.LENGTH_SHORT).show();
        Intent MainIntent = new Intent(AccountActivity.this, MainActivity.class);
        MainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(MainIntent);
    }
}
