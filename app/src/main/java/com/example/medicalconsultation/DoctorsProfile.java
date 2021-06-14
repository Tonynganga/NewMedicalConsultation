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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import static com.example.medicalconsultation.FirebaseUtils.DOCTOR_USERS;
import static com.example.medicalconsultation.FirebaseUtils.mFireStore;
import static com.example.medicalconsultation.HelperClasses.CommentsAdapter.SEND_DOCTOR_ID;


public class DoctorsProfile extends AppCompatActivity {
    private ImageView profileimage;
    private Button btnupload;
    private ProgressBar progressBar;

    //vars
    private final DatabaseReference root = FirebaseDatabase.getInstance().getReference("Image");
    private final StorageReference reference = FirebaseStorage.getInstance().getReference();
    private Uri imageUri;
    public Doctor mDocDetails;
    public EditText mTvDocName, mTvDocEmail, mTvDocPhoneNo, mTvDocDesc, mTvDocLoc;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctors_profile);
        Intent myIntent = getIntent();
        String docId=myIntent.getStringExtra(SEND_DOCTOR_ID);
        profileimage = findViewById(R.id.imageviewdprofile);
        btnupload = findViewById(R.id.buttonupload);
        mTvDocName = findViewById(R.id.dprofilename);
        mTvDocEmail = findViewById(R.id.dprofileemail);
        mTvDocPhoneNo = findViewById(R.id.dprofilephone);
        mTvDocDesc = findViewById(R.id.dprofiledesc);
        mTvDocLoc = findViewById(R.id.dprofilelocation);
        getDoctor(docId);
//        mDocDetails = (Doctor) myIntent.getSerializableExtra(DOCTOR_DETAILS);




        //progressBar.setVisibility(View.INVISIBLE);

        profileimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent galleryIntent = new Intent();
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, 2);
            }
        });


        btnupload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (imageUri != null) {
                    uploadToFirebase(imageUri);
                } else {
                    Toast.makeText(DoctorsProfile.this, "Please Select Image", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 2 && resultCode == RESULT_OK && data != null) {
            imageUri = data.getData();
            showImage(imageUri.toString());
//            profileimage.setImageURI(imageUri);
        }

    }
    private void getDoctor(String id){
//        FirebaseUtils.setCurrentUser();
        if(id==null){
            mFireStore.collection(DOCTOR_USERS)
                    .whereEqualTo("doctoremail", FirebaseUtils.sUserEmail)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    mDocDetails = document.toObject(Doctor.class).withId(document.getId());
                                }
                                if(mDocDetails!=null){
                                    FirebaseUtils.setDoctorUser(mDocDetails);
                                    mTvDocName.setText(mDocDetails.getDoctorname());
                                    mTvDocDesc.setText(mDocDetails.getDoctordescription());
                                    mTvDocEmail.setText(mDocDetails.getDoctoremail());
                                    mTvDocPhoneNo.setText(mDocDetails.getDoctorphone());
                                    mTvDocLoc.setText(mDocDetails.getDoctorlocation());
                                    if(mDocDetails.getImgUri()!=null){
                                        showImage(mDocDetails.getImgUri());
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
                    mDocDetails = documentSnapshot.toObject(Doctor.class).withId(documentSnapshot.getId());
                    if(mDocDetails!=null){
                        FirebaseUtils.setDoctorUser(mDocDetails);
                        mTvDocName.setText(mDocDetails.getDoctorname());
                        mTvDocDesc.setText(mDocDetails.getDoctordescription());
                        mTvDocEmail.setText(mDocDetails.getDoctoremail());
                        mTvDocPhoneNo.setText(mDocDetails.getDoctorphone());
                        mTvDocLoc.setText(mDocDetails.getDoctorlocation());
                        if(mDocDetails.getImgUri()!=null){
                            showImage(mDocDetails.getImgUri());
                        }
                    }
                }
            });
        }

    }
    private void uploadToFirebase(Uri uri) {

        final StorageReference fileRef = reference.child(System.currentTimeMillis() + "." + getFileExtension(uri));
        fileRef.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        mDocDetails.setImgUri(uri.toString());
                        FirebaseUtils.registerDoctorUser(mDocDetails);
                        String modelId = root.push().getKey();
                        progressBar.setVisibility(View.INVISIBLE);
                        Toast.makeText(getApplicationContext(), "Uploaded Successful", Toast.LENGTH_LONG).show();

                    }
                });
            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                progressBar.setVisibility(View.VISIBLE);

            }

        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressBar.setVisibility(View.INVISIBLE);
                Toast.makeText(getApplicationContext(), "Uploading Failed", Toast.LENGTH_LONG).show();
            }
        });


    }

    private String getFileExtension(Uri mUri) {
        ContentResolver cr = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cr.getType(mUri));
    }
    public void showImage(String url){
        if(url!=null&&url.isEmpty()==false){
            int width = Resources.getSystem().getDisplayMetrics().widthPixels;
            Glide.with(this).load(url).override(width*1/2, width*2/3).
                    centerCrop().into(profileimage);
        }
    }
}

