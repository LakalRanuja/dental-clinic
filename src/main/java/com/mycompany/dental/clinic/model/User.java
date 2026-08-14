package com.mycompany.dental.clinic.model;



public class User {

    private int user_id;
    private String username;

   // @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    private String fullName;
    private String role;

    public User() {
    }

    public User(int user_id, String username, String password, String fullName, String role) {
        this.user_id = user_id;
        this.username = username;
        this.password = password;
        this.fullName = fullName;
        this.role = role;
    }

    public int getUserId() {
        return user_id;
    }

    public void setUserId(int userId) {
        this.user_id = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
