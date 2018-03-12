package com.example.alex.raidcall;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.IntentCompat;
import android.support.v7.app.AppCompatActivity;
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
import com.google.firebase.iid.FirebaseInstanceId;

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
        final String RegName = mRegNameField.getText().toString().trim();
        String Password = mRegPasswordField.getText().toString().trim();
        final String IGN = mRegIGNField.getText().toString().trim();
        final String Email = mRegEmailField.getText().toString().trim();

        if(!TextUtils.isEmpty(RegName) && !TextUtils.isEmpty(Password) && !TextUtils.isEmpty(IGN) && !TextUtils.isEmpty(Email) && Password.length() > 5)
        {

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
                        mDatabase.child(mAuth.getCurrentUser().getUid()).child("pushToken").setValue(FirebaseInstanceId.getInstance().getToken().toString());



                        mProgress.dismiss();

                        Intent regraidIntent = new Intent(RegisterActivity.this, RaidActivity.class);
                        regraidIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        regraidIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | IntentCompat.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(regraidIntent);
                        ActivityCompat.finishAffinity(RegisterActivity.this);




                    }

                }
            });

        } else{
            Toast.makeText(RegisterActivity.this, "Something went wrong, is the password at least 5 characters long?", Toast.LENGTH_LONG).show();
        }
    }
}
