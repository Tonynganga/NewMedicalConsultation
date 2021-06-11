package com.example.medicalconsultation;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;

import android.util.Log;
import android.util.Patterns;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;


import com.example.medicalconsultation.HelperClasses.Patient;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;

import static com.example.medicalconsultation.MainActivity.APP_USER;
import static com.example.medicalconsultation.MainActivity.USER_PATIENT;


public class PatientRegister extends AppCompatActivity {
    private static final String TAG = "PatientRegister";
    private FirebaseAuth mAuth;
    private TextInputLayout edtname, edtemail, edtpassword,edtage, edtlocation, edtconfirmpassword;
    private RadioGroup gender;
    private RadioButton selectedRadioButton;
    private Button edtRegister, loginback;
    TextView alreadyhaveanaccount;
    ProgressDialog mLoadingBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_register);

        edtname = findViewById(R.id.tlpatient_name);
        edtemail = findViewById(R.id.tlpatient_email);
        edtage = findViewById(R.id.tlpatient_age);
        edtlocation = findViewById(R.id.tlpatient_location);
        edtpassword = findViewById(R.id.tlpatient_password);
        edtconfirmpassword = findViewById(R.id.tlpatient_confirmpassword);
        alreadyhaveanaccount = findViewById(R.id.alraedyhaveanaccount);

        mLoadingBar = new ProgressDialog(this);
        

        gender = findViewById(R.id.radioGroup);
       edtRegister = findViewById(R.id.buttonRegister);
        //loginback = (Button)findViewById(R.id.buttonloginback);




        mAuth = FirebaseAuth.getInstance();

        alreadyhaveanaccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), LogInPage.class);
                intent.putExtra(APP_USER,USER_PATIENT);
                startActivity(intent);
                finish();
            }
        });

        edtRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String patientname = edtname.getEditText().getText().toString().trim();
                String patientemail = edtemail.getEditText().getText().toString().trim();
                String patientage = edtage.getEditText().getText().toString().trim();
                String patientlocation = edtlocation.getEditText().getText().toString().trim();
                String patientpassword = edtpassword.getEditText().getText().toString().trim();
                String patientconfirmpassword = edtconfirmpassword.getEditText().getText().toString().trim();
                String patientgender = ((RadioButton)findViewById(gender.getCheckedRadioButtonId())).getText().toString();


                //validate values
                if (patientname.isEmpty()) {
                    setError(edtname, "Please Fill Out this Field");
                    return;
                }
                if (patientemail.isEmpty()) {
                   setError(edtemail, "Please Fill Out this Field");
                    return;
                }
                if (!Patterns.EMAIL_ADDRESS.matcher(patientemail).matches()) {
                    setError(edtemail,"Please provide a valid email");
                    return;
                }
                if (patientpassword.isEmpty()) {
                    edtpassword.setError("Please Fill Out this Field");
                    edtpassword.requestFocus();
                    return;
                }

                if (patientpassword.length() < 6) {
                    setError(edtpassword,"The password length must be 6 characters long");
                    return;
                }
                if (!patientconfirmpassword.matches(patientpassword)){
                    setError(edtconfirmpassword, "Password Does Not Match");
                    return;
                }

                if (patientage.isEmpty()) {
                    setError(edtage,"Please Fill Out this Field");
                    return;
                }
                if (patientlocation.isEmpty()) {
                    setError(edtlocation,"Please Fill Out this Field");
                    return;
                }


                mLoadingBar.setTitle("SignUp");
                mLoadingBar.setMessage("Creating User");
                mLoadingBar.show();
                Patient patientUser = new Patient(patientname,patientemail,patientlocation,patientgender,Integer.parseInt(patientage));

                mAuth.createUserWithEmailAndPassword(patientemail,patientpassword)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if(task.isSuccessful()){
                                    mLoadingBar.dismiss();
                                    Toast.makeText(PatientRegister.this, "Register Successfull", Toast.LENGTH_LONG).show();
//
                                    FirebaseUtils.registerPatientUser(patientUser);

                                } else {
                                    mLoadingBar.dismiss();
                                    throwRegisterError(task);

                                }
                        }
                });

            }
        });




    }

    private void setError(TextInputLayout field, String text) {

        field.setError(text);
        field.requestFocus();
    }

    private void throwRegisterError(@NonNull Task<AuthResult> task) {
        try {
            throw task.getException();
        } catch(FirebaseAuthWeakPasswordException e) {
            edtpassword.setError(getString(R.string.error_weak_password));
            edtpassword.requestFocus();
        } catch(FirebaseAuthInvalidCredentialsException e) {
            edtemail.setError(getString(R.string.error_invalid_email));
            edtemail.requestFocus();
        } catch(FirebaseAuthUserCollisionException e) {
            edtemail.setError(getString(R.string.error_user_exists));
            edtemail.requestFocus();
        }catch(FirebaseException e){
            Toast.makeText(getApplicationContext(),R.string.error_no_internet,Toast.LENGTH_LONG).show();
        } catch(Exception e) {
            Log.e(TAG, e.getMessage());
        }
    }
}