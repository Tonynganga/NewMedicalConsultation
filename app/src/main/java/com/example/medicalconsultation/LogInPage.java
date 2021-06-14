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
import android.widget.TextView;
import android.widget.Toast;

import com.example.medicalconsultation.HelperClasses.Doctor;
import com.example.medicalconsultation.HelperClasses.Patient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.RuntimeExecutionException;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import static com.example.medicalconsultation.FirebaseUtils.DOCTOR_USERS;
import static com.example.medicalconsultation.FirebaseUtils.PATIENT_USERS;
import static com.example.medicalconsultation.FirebaseUtils.mFireStore;
import static com.example.medicalconsultation.FirebaseUtils.mFirebaseAuth;
import static com.example.medicalconsultation.MainActivity.APP_USER;
import static com.example.medicalconsultation.MainActivity.USER_PATIENT;

public class LogInPage extends AppCompatActivity {

    private static final String TAG = "Login Page";
    public static final String DOCTOR_DETAILS = "Doctor Details";
    public static final String PATIENT_DETAILS = "Patient Details";
    public static final String APP_USER="state user";
    public static final String USER_PATIENT="patient";
    public static final String USER_DOCTOR="doctor";
    FirebaseAuth fAuth;
    ProgressBar progressBar;
    private Button mButtonRegister,mButtonLogin;
    private TextInputLayout mEtPassword,mEtUsername;
    TextView donthaveaccount,forgotpassword;
    public Patient mUserPatientDetails;
    public String mUser;
    public Doctor mUserDoctorDetails;
    public Boolean mLogin;
    ProgressDialog mLoadingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in_page);

        fAuth = FirebaseAuth.getInstance();

        Intent myIntent = getIntent();

        mUser = myIntent.getStringExtra(APP_USER);
        mLoadingBar = new ProgressDialog(this);

       // mButtonRegister = (Button)findViewById(R.id.button_register);
        mButtonLogin = (Button)findViewById(R.id.buttonlogin);
        progressBar = findViewById(R.id.progressBar);


        mEtUsername = findViewById(R.id.etUserName);
        mEtPassword = findViewById(R.id.etPassword);
        donthaveaccount = findViewById(R.id.donthaveaccount);
        forgotpassword = findViewById(R.id.forgotpassword);

        forgotpassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ForgotPassword();
            }
        });

        donthaveaccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mUser.equals(USER_PATIENT)){
                Intent intent = new Intent(getApplicationContext(), PatientRegister.class);
                startActivity(intent);
                finish();
            }else{
                Intent intent = new Intent(getApplicationContext(), DoctorRegister.class);
                startActivity(intent);

            }
            }
        });

        mButtonLogin.setOnClickListener(v->{
            tryLogIn();
        });

    }

    private void tryLogIn() {

        String username = mEtUsername.getEditText().getText().toString().trim();
        String password = mEtPassword.getEditText().getText().toString().trim();
        if(username.length() == 0){
            setError(mEtUsername, "Please Fill Out This Form");
            return;
        }
        if(!Patterns.EMAIL_ADDRESS.matcher(username).matches()){
            setError(mEtUsername, "Email already Registered.");
            return;
        }
        if(password.length() == 0){
            setError(mEtUsername, "Please Fill Out This Form");
            return;
        }
        if(password.length()<6){
            setError(mEtUsername, "Minimum password length is 6 characters");
            return;
        }
        mLoadingBar.setTitle("Sign In!");
        mLoadingBar.show();
        fAuth.signInWithEmailAndPassword(username,password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            mLoadingBar.dismiss();
                            mEtUsername.getEditText().setText("");
                            mEtPassword.getEditText().setText("");
                            logIn();
                        }
                    }

                }).addOnFailureListener(new OnFailureListener(){

            @Override
            public void onFailure(@NonNull Exception e) {
                if(e instanceof FirebaseAuthInvalidUserException){
                    mLoadingBar.dismiss();
                    Toast.makeText(getApplicationContext(),R.string.error_invalid_email_or_password,Toast.LENGTH_LONG).show();
                }else{
                    mLoadingBar.dismiss();
                    Toast.makeText(getApplicationContext(),R.string.error_no_internet,Toast.LENGTH_LONG).show();
                }

            }
        });
    }

    private void setError(TextInputLayout field, String text) {
        field.setError(text);
        field.requestFocus();
    }

    private void ForgotPassword() {
        Intent intent = new Intent(getApplicationContext(), ForgotPassword.class);
        intent.putExtra(APP_USER, USER_DOCTOR);
        intent.putExtra(APP_USER, USER_PATIENT);
        startActivity(intent);
    }

    private void throwLoginError(@NonNull Task<AuthResult> task) {
        try {
            throw task.getException();
        } catch(FirebaseAuthWeakPasswordException e) {
            mEtPassword.setError(getString(R.string.error_weak_password));
            mEtPassword.requestFocus();
        } catch(FirebaseAuthInvalidCredentialsException e) {
            mEtUsername.setError(getString(R.string.error_invalid_email_or_password));
            mEtUsername.requestFocus();
        } catch(FirebaseAuthUserCollisionException e) {
            mEtUsername.setError(getString(R.string.error_user_exists));
            mEtUsername.requestFocus();
        }catch(FirebaseNetworkException e){
                Toast.makeText(getApplicationContext(),R.string.error_no_internet,Toast.LENGTH_LONG).show();
        } catch(Exception e) {
            Log.e(TAG, e.getMessage());
        }
    }

    private void logIn() {
        FirebaseUtils.setCurrentUser();
        if (mUser.equals(USER_PATIENT)){
            checkUserPatient();
        }else{
            checkUserDoctor();
        }
    }


    private void checkUserDoctor() {
        mFireStore.collection(DOCTOR_USERS)
                .whereEqualTo("doctoremail", FirebaseUtils.sUserEmail)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                mUserDoctorDetails = document.toObject(Doctor.class).withId(document.getId());
                            }
                            if(mUserDoctorDetails!=null){
                                FirebaseUtils.setDoctorUser(mUserDoctorDetails);
                                Intent intent = new Intent(getApplicationContext(), DoctorHomePage.class);
                                intent.putExtra(DOCTOR_DETAILS,mUserDoctorDetails);
                                startActivity(intent);
                            }else{
                                mFirebaseAuth.signOut();
                                Toast.makeText(getApplicationContext(), "No User Found",Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });

    }

    private void checkUserPatient() {
        mFireStore.collection(PATIENT_USERS)
                .whereEqualTo("email", FirebaseUtils.sUserEmail)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    mUserPatientDetails = document.toObject(Patient.class).withId(document.getId());
                                }
                                if(mUserPatientDetails!=null){
                                    Intent intent = new Intent(getApplicationContext(), PatientHomePage.class);
                                    intent.putExtra(PATIENT_DETAILS,mUserPatientDetails);
                                    startActivity(intent);
                                }else{
                                    mFirebaseAuth.signOut();
                                    Toast.makeText(getApplicationContext(), "No User Found",Toast.LENGTH_SHORT).show();
                                }


                        }
                    }
                });
    }

    @Override
    protected void onPause() {
        super.onPause();
//        FirebaseUtils.detachListener();
    }

    @Override
    protected void onResume() {
        super.onResume();
//        FirebaseUtils.attachListener();
    }
}