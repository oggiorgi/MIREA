package com.example.electronicgradebook;

public class Student {
    private String id;
    private String lastName;
    private String firstName;
    private String groupId;

    public Student(String id, String lastName, String firstName, String groupId) {
        this.id = id;
        this.lastName = lastName;
        this.firstName = firstName;
        this.groupId = groupId;
    }

    public String getId() {
        return id;
    }

    public String getLastName() {
        return lastName;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getGroupId() {
        return groupId;
    }

    public String getFullName() {
        return lastName + " " + firstName;
    }

    @Override
    public String toString() {
        return getFullName();
    }
} 