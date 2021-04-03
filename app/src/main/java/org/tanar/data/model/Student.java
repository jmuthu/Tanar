package org.tanar.data.model;

public class Student {

    private final String name;
    private final String email;
    private final String phoneNumber;
    private final String subject;
    private final String classNumber;
    private final Double distance;
    private String status;

    public Student(String name, String email, String phoneNumber, String subject, String classNumber, Double distance, String status) {
        this.name = name;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.subject = subject;
        this.classNumber = classNumber;
        this.distance = distance;
        this.status = status;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getclassNumber() {
        return classNumber;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getSubject() {
        return subject;
    }

    public Double getDistance() {
        return distance;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
