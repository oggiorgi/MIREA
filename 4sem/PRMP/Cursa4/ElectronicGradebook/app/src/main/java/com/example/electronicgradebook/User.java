package com.example.electronicgradebook;

public class User {
    private String id;
    private String name;
    private String email;
    private String role;
    private String group;

    public User() {}

    public User(String id, String name, String email, String role, String group) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.role = role;
        this.group = group;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String getGroup() { return group; }
    public void setGroup(String group) { this.group = group; }
} 