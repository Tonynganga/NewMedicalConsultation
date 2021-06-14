package com.example.medicalconsultation.HelperClasses;

import android.net.Uri;

public class PatientPost {

    private String userId;
    private String id;
    private String patientName;
    private String patientProblem;
    private String category;
    private String imageUrl;
    private String datePost;

    private PatientPost(){
    }



    public PatientPost(String uid, String name, String description, String mCategoryVal, String imageUrl, String datePost) {
        this.userId = uid;
        this.patientName = name;
        this.patientProblem = description;
        this.category = mCategoryVal;
        this.imageUrl = imageUrl;
        this.datePost = datePost;
    }



    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPatientName() {
        return patientName;
    }

    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }

    public String getPatientProblem() {
        return patientProblem;
    }

    public void setPatientProblem(String patientProblem) {
        this.patientProblem = patientProblem;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getDatePost() {
        return datePost;
    }

    public void setDatePost(String datePost) {
        this.datePost = datePost;
    }

    public PatientPost withId(String id){
        setId(id);
        return this;
    }



}
