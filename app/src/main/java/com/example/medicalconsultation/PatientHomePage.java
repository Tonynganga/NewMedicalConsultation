package com.example.medicalconsultation;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.medicalconsultation.HelperClasses.Doctor;
import com.example.medicalconsultation.HelperClasses.Patient;
import com.example.medicalconsultation.HelperClasses.PatientPostAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.example.medicalconsultation.FirebaseUtils.PATIENT_USERS;
import static com.example.medicalconsultation.FirebaseUtils.mFireStore;
import static com.example.medicalconsultation.LogInPage.DOCTOR_DETAILS;
import static com.example.medicalconsultation.LogInPage.PATIENT_DETAILS;
import static com.example.medicalconsultation.MainActivity.APP_USER;
import static com.example.medicalconsultation.MainActivity.USER_DOCTOR;
import static com.example.medicalconsultation.MainActivity.USER_PATIENT;

public class PatientHomePage extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    public static final String PATIENT_ID = "Patient Id";

    Toolbar toolbar;
    NavigationView navigationView;
    DrawerLayout drawerLayout;

    RecyclerView mRecyclerView;
    Button btnLogOut;
    FirebaseAuth mAuth;
    FirebaseUser mUser;
    private Patient mPatientDetails;

    CircleImageView profileImageViewHeader;
    TextView profileUsernameHeader;

    public Patient mPatDetails;
    String patId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_home_page);
        Intent intent=getIntent();
        mPatientDetails = (Patient) intent.getSerializableExtra(PATIENT_DETAILS);
        patId= intent.getStringExtra(PATIENT_ID);

        mAuth = FirebaseAuth.getInstance();
        mUser= mAuth.getCurrentUser();

        toolbar = findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Medical Consultation");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu);


        drawerLayout = findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.navView);

        View view = navigationView.inflateHeaderView(R.layout.drawer_header);

        profileImageViewHeader = view.findViewById(R.id.header_profileimage);
        profileUsernameHeader = view.findViewById(R.id.username_header);

        navigationView.setNavigationItemSelectedListener(this);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PatientHomePage.this,PatientProblem.class);
                intent.putExtra(PATIENT_DETAILS, mPatientDetails);
                startActivity(intent);
            }
        });

        mRecyclerView = findViewById(R.id.recyclerView);

        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));



    }

    @Override
    protected void onStart() {
        super.onStart();
        getPatient(patId);

    }

    private void getPatient(String id){
//        FirebaseUtils.setCurrentUser();
        if(id==null) {
            mFireStore.collection(PATIENT_USERS)
                    .whereEqualTo("email", FirebaseUtils.sUserEmail)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    mPatDetails = document.toObject(Patient.class).withId(document.getId());
                                }
                                if (mPatDetails != null) {
                                    FirebaseUtils.setPatientUser(mPatDetails);
                                    profileUsernameHeader.setText(mPatDetails.getName());
                                    if (mPatDetails.getImageUrl() != null) {
                                        showImage(mPatDetails.getImageUrl());
                                    }
                                }
                            }
                        }
                    });

        }else{
            mFireStore.collection(PATIENT_USERS)
                    .document(id).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    mPatDetails = documentSnapshot.toObject(Patient.class).withId(documentSnapshot.getId());
                    if(mPatDetails!=null){
                        FirebaseUtils.setPatientUser(mPatDetails);
                        profileUsernameHeader.setText(mPatDetails.getName());
                        if(mPatDetails.getImageUrl()!=null){
                            showImage(mPatDetails.getImageUrl());
                        }
                    }
                }
            });
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        PatientPostAdapter adapter = new PatientPostAdapter(this,USER_PATIENT);
        mRecyclerView.setAdapter(adapter);
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home){
            drawerLayout.openDrawer(GravityCompat.START);
            return true;
        }
        return true;
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.home:
                Toast.makeText(this, "Home", Toast.LENGTH_SHORT).show();
                break;
            case R.id.profile:
                Intent intent = new Intent(getApplicationContext(), PatientProfile.class);
                intent.putExtra(PATIENT_DETAILS, mPatientDetails);
                startActivity(intent);
                intent.putExtra(PATIENT_DETAILS, mPatientDetails);
                break;
            case R.id.chat:
                //startActivity(new Intent(MainActivity.this, ChatUsersActivity.class));
                break;
            case R.id.logout:
                mAuth.signOut();
                Intent intent2 = new Intent(getApplicationContext(), LogInPage.class);
                startActivity(intent2);
                finish();
                break;
        }
        return true;
    }


    public void showImage(String url){
        if(url!=null&&url.isEmpty()==false){
            //int width = Resources.getSystem().getDisplayMetrics().widthPixels;
            Glide.with(this).load(url).
                    centerCrop().into(profileImageViewHeader);
        }
    }
}