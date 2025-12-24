package com.example.alex.raidcall;

import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;


public class ForgotAcitivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private EditText FPEmail;
    private Button FPsend;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_acitivity);

        FPEmail = (EditText) findViewById(R.id.FPEmail);
        FPsend = (Button) findViewById(R.id.FPsend);
        mAuth = FirebaseAuth.getInstance();

        FPsend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String Fmail = FPEmail.getText().toString();
                if (Fmail == null || Fmail.trim().length() == 0) {
                    Toast.makeText(ForgotAcitivity.this, "Please enter your email", Toast.LENGTH_SHORT).show();
                    return;
                }
                mAuth.sendPasswordResetEmail(Fmail).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                       if(task.isSuccessful()){
                           Toast.makeText(ForgotAcitivity.this, "Email sent", Toast.LENGTH_SHORT).show();
                           startActivity(new Intent(ForgotAcitivity.this, MainActivity.class ));
                       } else {
                           Toast.makeText(ForgotAcitivity.this, "Failed to send email", Toast.LENGTH_SHORT).show();
                       }
                    }
                });
            }
        });
    }
}
