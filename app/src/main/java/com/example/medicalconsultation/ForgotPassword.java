package com.example.medicalconsultation;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPassword extends AppCompatActivity {


    EditText inputemail;
    Button btnsendemail;
    FirebaseAuth mAuth;
    ProgressDialog mLoadingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);


        inputemail = findViewById(R.id.inputpasswordreset);
        btnsendemail=findViewById(R.id.btnresetpassword);
        mAuth= FirebaseAuth.getInstance();
        mLoadingBar = new ProgressDialog(this);

        btnsendemail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email=inputemail.getText().toString();
                if (email.isEmpty()){
                    Toast.makeText(ForgotPassword.this, "Please enter email", Toast.LENGTH_SHORT).show();
                }else{
                    mLoadingBar.setTitle("Password Reset");
                    mLoadingBar.setMessage("Sending Password Recovery Email.");
                    mLoadingBar.show();
                    mAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                mLoadingBar.dismiss();
                                Toast.makeText(ForgotPassword.this, "Please check your email", Toast.LENGTH_SHORT).show();
                                inputemail.setText("");
                            }else {
                                mLoadingBar.dismiss();
                                Toast.makeText(ForgotPassword.this, "Email not sent.try Again", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });
    }
}