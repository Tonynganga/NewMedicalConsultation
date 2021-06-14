 package com.example.medicalconsultation;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.medicalconsultation.HelperClasses.Doctor;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import de.hdodenhof.circleimageview.CircleImageView;


 public class DoctorRegister extends AppCompatActivity  {
     private static final String TAG = "DoctorRegister";
     private TextInputLayout edtdname, edtdemail, edtdpassword, edtdphone, edtdlocation, edtddesc;
    private Button docregister;
    private FirebaseAuth mAuth;
    ProgressDialog mLoadingBar;
    TextView tvDIRegister;
    CircleImageView cIVDoctorImage;
    Uri imageUri;
     StorageReference storageReference;

    private final String[] mDoctorCategory={"Dentist","Optician","Dermatologist","Pediatrician","Gynaecologist","Gastrologist"};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_register);
        mLoadingBar = new ProgressDialog(this);
        mAuth = FirebaseAuth.getInstance();

        edtdname = findViewById(R.id.etDoctorName);
        edtdemail = findViewById(R.id.etDoctorEmail);
        edtdpassword = findViewById(R.id.etDoctorPassword);
        edtdphone = findViewById(R.id.etDoctorPhone);
        edtdlocation = findViewById(R.id.etDoctorLocation);
        edtddesc = findViewById(R.id.etDoctorDescription);
        docregister = findViewById(R.id.buttondocregister);
        tvDIRegister = findViewById(R.id.tvDIRegister);
        cIVDoctorImage = findViewById(R.id.cIVDoctorImage);

        storageReference = FirebaseStorage.getInstance().getReference();

        docregister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                registerDoctor();
            }
        });

        tvDIRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent = new Intent();
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, 2);
            }
        });

    }

     private void registerDoctor() {
         String doctorname = edtdname.getEditText().getText().toString().trim();
         String doctoremail = edtdemail.getEditText().getText().toString().trim();
         String doctorpassword = edtdpassword.getEditText().getText().toString().trim();
         String doctorphone = edtdphone.getEditText().getText().toString().trim();
         String doctorlocation = edtdlocation.getEditText().getText().toString().trim();
         String doctordescription= edtddesc.getEditText().getText().toString().trim();

         //validate values
         if (doctorname.isEmpty()) {
             setError(edtdname, "Please Fill Out This Field");
         }
         if (doctoremail.isEmpty()) {
             setError(edtdemail, "Please Fill Out This Field");
         }
         if (!Patterns.EMAIL_ADDRESS.matcher(doctoremail).matches()) {
             setError(edtdemail, "Please provide a valid email");
         }
         if (doctorpassword.isEmpty()) {
             setError(edtdpassword, "Please Fill Out This Field");
         }

         if (doctorpassword.length() < 6) {
             setError(edtdpassword, "The password length must be 6 characters long");
         }

         if (doctorphone.isEmpty()) {
             setError(edtdphone, "Please Fill Out This Field");
         }
         if (!Patterns.PHONE.matcher(doctorphone).matches()){
             setError(edtdphone, "Please provide a valid phone number");
         }
         if (doctorlocation.isEmpty()) {
             setError(edtdlocation, "Please Fill Out This Field");
         }
         if (doctordescription.isEmpty()) {
             setError(edtddesc, "Description is required.");
         }

         mLoadingBar.setTitle("SignUp");
         mLoadingBar.setMessage("Creating User..Please Wait!");
         mLoadingBar.show();
         final StorageReference Ref = storageReference.child("Images").child(System.currentTimeMillis() + "." + getFileExtension(Uri.parse(String.valueOf(imageUri))));
         UploadTask uploadTask =  Ref.putFile(imageUri);
         Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
             @Override
             public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                 if (task.isSuccessful()){

                 }
                 return Ref.getDownloadUrl();
             }
         }).addOnCompleteListener(new OnCompleteListener<Uri>() {
             @Override
             public void onComplete(@NonNull Task<Uri> task) {
                 Uri downloadUri = task.getResult();

                 Doctor doctoruser = new Doctor(doctorname,doctoremail,doctorlocation,doctordescription,doctorphone, downloadUri.toString());

                 mAuth.createUserWithEmailAndPassword(doctoremail,doctorpassword)
                         .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                             @Override
                             public void onComplete(@NonNull Task<AuthResult> task) {
                                 if(task.isSuccessful()){
                                     mLoadingBar.dismiss();
                                     Toast.makeText(DoctorRegister.this, "Register Successfull", Toast.LENGTH_LONG).show();
                                     edtdname.getEditText().setText("");
                                     edtdemail.getEditText().setText("");
                                     edtdlocation.getEditText().setText("");
                                     edtdphone.getEditText().setText("");
                                     edtdpassword.getEditText().setText("");
                                     edtddesc.getEditText().setText("");
                                     FirebaseUtils.registerDoctorUser(doctoruser);
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

     @Override
     protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
         super.onActivityResult(requestCode, resultCode, data);
         if (requestCode == 2 && resultCode == RESULT_OK && data != null) {
             imageUri = data.getData();
             //showImage(imageUri.toString());
             cIVDoctorImage.setImageURI(imageUri);
         }
     }

     private String getFileExtension(Uri mUri) {
         ContentResolver cr = getContentResolver();
         MimeTypeMap mime = MimeTypeMap.getSingleton();
         return mime.getExtensionFromMimeType(cr.getType(mUri));
     }

     private void throwRegisterError(@NonNull Task<AuthResult> task) {
         try {
             throw task.getException();
         } catch(FirebaseAuthWeakPasswordException e) {
             edtdpassword.setError(getString(R.string.error_weak_password));
             edtdpassword.requestFocus();
         } catch(FirebaseAuthInvalidCredentialsException e) {
             edtdemail.setError(getString(R.string.error_invalid_email));
             edtdemail.requestFocus();
         } catch(FirebaseAuthUserCollisionException e) {
             edtdemail.setError(getString(R.string.error_user_exists));
             edtdemail.requestFocus();
         }catch(FirebaseException e){
             Toast.makeText(getApplicationContext(),R.string.error_no_internet,Toast.LENGTH_LONG).show();
         } catch(Exception e) {
             Log.e(TAG, e.getMessage());
         }
     }


 }