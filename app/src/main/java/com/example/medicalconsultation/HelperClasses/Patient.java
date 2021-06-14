package com.example.medicalconsultation.HelperClasses;

import android.net.Uri;

import java.io.Serializable;

public class Patient implements Serializable {

    private String id;
    private String name;
    private String email;
    private String location;
    private String gender;
    private int age;
    private String imageUrl;

    public Patient(){}

    public Patient(String patientname, String patientemail, String patientlocation, String patientgender, int patientage, String imageUrl) {
        this.name = patientname;
        this.email = patientemail;
        this.location = patientlocation;
        this.gender = patientgender;
        this.age = patientage;
        this.imageUrl = imageUrl;
    }


    public Patient withId(String id){
        this.id=id;
        return this;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
