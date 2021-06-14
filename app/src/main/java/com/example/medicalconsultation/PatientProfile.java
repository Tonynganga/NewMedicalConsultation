package com.example.medicalconsultation;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.medicalconsultation.HelperClasses.Doctor;
import com.example.medicalconsultation.HelperClasses.Patient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.example.medicalconsultation.FirebaseUtils.DOCTOR_USERS;
import static com.example.medicalconsultation.FirebaseUtils.PATIENT_USERS;
import static com.example.medicalconsultation.FirebaseUtils.mFireStore;
import static com.example.medicalconsultation.HelperClasses.CommentsAdapter.SEND_DOCTOR_ID;
import static com.example.medicalconsultation.LogInPage.PATIENT_DETAILS;

public class PatientProfile extends AppCompatActivity {
    public static final String PATIENT_ID = "Patient Id";

    private CircleImageView pprofileimage;
    private Button btnupload;
    FirebaseAuth mAuth;
    FirebaseUser mUser;

    //vars
//    private final DatabaseReference root = FirebaseDatabase.getInstance().getReference("Image");
//    private final StorageReference reference = FirebaseStorage.getInstance().getReference();
//    private Uri imageUri;
    public Patient mPatDetails;
    public EditText mTvPatName, mTvPatEmail, mTvPatAge, mTvPatGender, mTvPatLoc;
    private Patient mPatientDetails;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_profile);

        Intent intent=getIntent();
        mPatientDetails = (Patient) intent.getSerializableExtra(PATIENT_DETAILS);
        String patId= intent.getStringExtra(PATIENT_ID);

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

        pprofileimage = findViewById(R.id.imageviewpprofile);
        btnupload = findViewById(R.id.buttonupload);
        mTvPatName = findViewById(R.id.pprofilename);
        mTvPatEmail = findViewById(R.id.pprofileemail);
        mTvPatAge = findViewById(R.id.pprofileage2);
        mTvPatGender = findViewById(R.id.pprofilegender);
        mTvPatLoc = findViewById(R.id.pprofilelocation);
        getPatient(patId);
//        mDocDetails = (Doctor) myIntent.getSerializableExtra(DOCTOR_DETAILS);





//        profileimage.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent galleryIntent = new Intent();
//                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
//                galleryIntent.setType("image/*");
//                startActivityForResult(galleryIntent, 2);
//            }
//        });
//
//
//        btnupload.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if (imageUri != null) {
//                    uploadToFirebase(imageUri);
//                } else {
//                    Toast.makeText(DoctorsProfile.this, "Please Select Image", Toast.LENGTH_SHORT).show();
//                }
//            }
//        });
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
                                    mTvPatName.setText(mPatDetails.getName());
                                    mTvPatGender.setText(mPatDetails.getGender());
                                    mTvPatEmail.setText(mPatDetails.getEmail());
                                    mTvPatAge.setText(String.valueOf(mPatDetails.getAge()));
                                    mTvPatLoc.setText(mPatDetails.getLocation());
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
                        mTvPatName.setText(mPatDetails.getName());
                        mTvPatGender.setText(mPatDetails.getGender());
                        mTvPatEmail.setText(mPatDetails.getEmail());
                        mTvPatAge.setText(String.valueOf(mPatDetails.getAge()));
                        mTvPatLoc.setText(mPatDetails.getLocation());
                        if(mPatDetails.getImageUrl()!=null){
                            showImage(mPatDetails.getImageUrl());
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
                    centerCrop().into(pprofileimage);
        }
    }

}
