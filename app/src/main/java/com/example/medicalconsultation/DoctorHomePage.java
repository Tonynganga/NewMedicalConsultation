package com.example.medicalconsultation;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.medicalconsultation.HelperClasses.Doctor;
import com.example.medicalconsultation.HelperClasses.Patient;
import com.example.medicalconsultation.HelperClasses.PatientPostAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.example.medicalconsultation.FirebaseUtils.DOCTOR_USERS;
import static com.example.medicalconsultation.FirebaseUtils.PATIENT_USERS;
import static com.example.medicalconsultation.FirebaseUtils.mFireStore;
import static com.example.medicalconsultation.LogInPage.DOCTOR_DETAILS;
import static com.example.medicalconsultation.LogInPage.PATIENT_DETAILS;
import static com.example.medicalconsultation.MainActivity.APP_USER;
import static com.example.medicalconsultation.MainActivity.USER_DOCTOR;
import static com.example.medicalconsultation.MainActivity.USER_PATIENT;

public class DoctorHomePage extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{
    public static final String DOCTOR_ID = "Doctor Id";
    RecyclerView mRecyclerView;
    Button btnLogOut;
    public Doctor mDocDetails;

    Toolbar toolbar;
    NavigationView navigationView;
    DrawerLayout drawerLayout;

    FirebaseAuth mAuth;
    FirebaseUser mUser;
    private Doctor mDoctorDetails;

    CircleImageView DoctorprofileImageViewHeader;
    TextView DoctorprofileUsernameHeader;
    String patId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_home_page);
        Intent myIntent = getIntent();

        Intent intent=getIntent();
        mDoctorDetails = (Doctor) intent.getSerializableExtra(DOCTOR_DETAILS);
        patId= intent.getStringExtra(DOCTOR_ID);

        mAuth = FirebaseAuth.getInstance();
        mUser= mAuth.getCurrentUser();

        toolbar = findViewById(R.id.app_bardoctor);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Medical Consultation");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu);

        drawerLayout = findViewById(R.id.drawerLayoutdcoctor);
        navigationView = findViewById(R.id.navViewDoctor);

        View view = navigationView.inflateHeaderView(R.layout.drawer_header);
        DoctorprofileImageViewHeader = view.findViewById(R.id.header_profileimage);
        DoctorprofileUsernameHeader = view.findViewById(R.id.username_header);

        navigationView.setNavigationItemSelectedListener(this);

        mRecyclerView = findViewById(R.id.recyclerViewdoctor);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        mDocDetails = (Doctor) myIntent.getSerializableExtra(DOCTOR_DETAILS);

    }

    @Override
    protected void onStart() {
        super.onStart();
        getDoctor(patId);
    }

    private void getDoctor(String id) {
        FirebaseUtils.setCurrentUser();
        if(id==null) {
            mFireStore.collection(DOCTOR_USERS)
                    .whereEqualTo("email", FirebaseUtils.sUserEmail)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    mDoctorDetails = document.toObject(Doctor.class).withId(document.getId());
                                }
                                if (mDoctorDetails != null) {
                                    FirebaseUtils.setDoctorUser(mDoctorDetails);
                                    DoctorprofileUsernameHeader.setText(mDoctorDetails.getDoctorname());
                                    if (mDoctorDetails.getImgUri() != null) {
                                        showImage(mDoctorDetails.getImgUri());
                                    }
                                }
                            }
                        }
                    });

        }else{
            mFireStore.collection(DOCTOR_USERS)
                    .document(id).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    mDoctorDetails = documentSnapshot.toObject(Doctor.class).withId(documentSnapshot.getId());
                    if(mDoctorDetails!=null){
                        FirebaseUtils.setDoctorUser(mDoctorDetails);
                        DoctorprofileUsernameHeader.setText(mDoctorDetails.getDoctorname());
                        if(mDoctorDetails.getImgUri()!=null){
                            showImage(mDoctorDetails.getImgUri());
                        }
                    }
                }
            });
        }

    }
    public void showImage(String url){
        if(url!=null&&url.isEmpty()==false){
            //int width = Resources.getSystem().getDisplayMetrics().widthPixels;
            Glide.with(this).load(url).
                    centerCrop().into(DoctorprofileImageViewHeader);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        PatientPostAdapter adapter = new PatientPostAdapter(this,USER_DOCTOR);
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
                Intent intent = new Intent(getApplicationContext(), DoctorsProfile.class);
                intent.putExtra(DOCTOR_DETAILS, mDoctorDetails);
                startActivity(intent);
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
}