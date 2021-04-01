package org.tanar.data.model;

import java.util.ArrayList;

public class Tutor {

    private String name;
    private ArrayList<String> subjectList;
    private Double rating;
    private Double distance;

    public String getName() {
        return name;
    }

    public ArrayList<String> getSubjectList() {
        return subjectList;
    }

    public Double getRating() {
        return rating;
    }

    public Double getDistance() {
        return distance;
    }

    public Tutor(String name, ArrayList<String> subjectList, Double rating, Double distance) {
        this.name = name;
        this.subjectList = subjectList;
        this.rating = rating;
        this.distance = distance;
    }
}
