package org.tanar.data.model;

public class Subject {
    private String Id;
    private String Name;

    public String getId() {
        return Id;
    }

    public String getName() {
        return Name;
    }

    public Subject(String id, String name) {
        Id = id;
        Name = name;
    }
}
