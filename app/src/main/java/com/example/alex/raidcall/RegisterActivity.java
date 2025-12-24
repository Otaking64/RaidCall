package com.example.alex.raidcall;

import android.app.ProgressDialog;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.IntentCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity {


    private EditText mRegNameField;
    private EditText mRegEmailField;
    private EditText mRegIGNField;
    private EditText mRegPasswordField;


    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    private ProgressDialog mProgress;

    private Button mNewRegistryBtn;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);


        mRegNameField =(EditText) findViewById(R.id.RegNameField);
        mRegEmailField =(EditText) findViewById(R.id.regEmailField);
        mRegIGNField =(EditText) findViewById(R.id.regignField);
        mRegPasswordField =(EditText) findViewById(R.id.regPasswordField);

        mNewRegistryBtn = (Button) findViewById(R.id.NewRegBtn);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Users");

        mProgress = new ProgressDialog(this);

        mNewRegistryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startRegister();
            }
        });


    }

    private void startRegister() {
        final String RegName = mRegNameField.getText().toString();
        String Password = mRegPasswordField.getText().toString();
        final String IGN = mRegIGNField.getText().toString();
        final String Email = mRegEmailField.getText().toString();

        if(RegName == null || RegName.trim().length() == 0 ||
           Password == null || Password.trim().length() == 0 ||
           IGN == null || IGN.trim().length() == 0 ||
           Email == null || Email.trim().length() == 0) {
            Toast.makeText(RegisterActivity.this, "All fields are required.", Toast.LENGTH_SHORT).show();
            return;
        }
        if(Password.trim().length() <= 5) {
            Toast.makeText(RegisterActivity.this, "Password must be at least 6 characters.", Toast.LENGTH_SHORT).show();
            return;
        }
        mProgress.setMessage("Being registered...");
        mProgress.show();

        mAuth.createUserWithEmailAndPassword(Email,Password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {


                if(task.isSuccessful()){

                    String User_ID = mAuth.getCurrentUser().getUid();

                    DatabaseReference current_user_id = mDatabase.child(User_ID);

                    current_user_id.child("In game name").setValue(IGN);
                    current_user_id.child("Name").setValue(RegName);
                    current_user_id.child("Email").setValue(Email);



                    mProgress.dismiss();

                    Intent regraidIntent = new Intent(RegisterActivity.this, RaidActivity.class);
                    regraidIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    regraidIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK );
                    startActivity(regraidIntent);
                    ActivityCompat.finishAffinity(RegisterActivity.this);




                }

            }
        });

    }
}
