package com.example.medicalconsultation;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.medicalconsultation.HelperClasses.Patient;
import com.example.medicalconsultation.HelperClasses.PatientPost;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import static com.example.medicalconsultation.FirebaseUtils.mFirebaseAuth;
import static com.example.medicalconsultation.LogInPage.PATIENT_DETAILS;

public class PatientProblem extends AppCompatActivity implements  AdapterView.OnItemSelectedListener {
    private final String[] mDoctorCategory={"Dentistry","Optical","Skin Problems","Pediatrics","Gastrointestinal"};
    private String mCategoryVal;
    private EditText mEtDesc;
    private Button mBtPostProb;
    private Patient mPatientDetails;
    private Calendar calendar;
    private SimpleDateFormat dateFormat;
    private String date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_problem);
        Intent intent=getIntent();
        mPatientDetails = (Patient) intent.getSerializableExtra(PATIENT_DETAILS);

        Spinner spin = findViewById(R.id.categorySpinner);
        spin.setOnItemSelectedListener(this);
        mEtDesc = findViewById(R.id.etPatientProblemDesc);
        mBtPostProb = findViewById(R.id.button2);
        calendar = Calendar.getInstance();

        ArrayAdapter adapter = new ArrayAdapter(this, R.layout.spinner_item,mDoctorCategory);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //Setting the ArrayAdapter data on the Spinner
        spin.setAdapter(adapter);

//        dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
//        date = SimpleDateFormat.format(calendar.getTime());
        Date date = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("dd-M-yyyy HH:mm:ss");
        final String strDate = formatter.format(date);
        mBtPostProb.setOnClickListener( view -> {
            String description = mEtDesc.getText().toString().trim();
            if(description.length() == 0){
                mEtDesc.setError("Enter a Description");
                mEtDesc.requestFocus();
                return;
            }

            PatientPost post= new PatientPost(mFirebaseAuth.getUid(),mPatientDetails.getName(),description,mCategoryVal, mPatientDetails.getImageUrl(), strDate);
            FirebaseUtils.addPost(this,post);
            mEtDesc.setText("");



        });

    }

    //map what is selected to the mcategory value
    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
        mCategoryVal=mDoctorCategory[position];
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}