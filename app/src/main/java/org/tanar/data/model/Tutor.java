package org.tanar.data.model;

import java.util.ArrayList;

public class Tutor {

    private String name;
    private String email;
    private String phonenumber;
    private String subjectList;
    private String tutClass;
    private String expyear;
    private Double distance;

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getTutClass(){ return tutClass;}

    public String getPhonenumber() { return phonenumber; }

    public String getSubjectList() {
        return subjectList;
    }

    public String getExpyear() {
        return expyear;
    }

    public Double getDistance() {
        return distance;
    }

    public Tutor(String name, String email, String phonenumber, String subjectList, String tutClass, String expyear, Double distance) {
        this.name = name;
        this.tutClass=tutClass;
        this.email=email;
        this.phonenumber=phonenumber;
        this.subjectList = subjectList;
        this.expyear = expyear;
        this.distance = distance;
    }
}
