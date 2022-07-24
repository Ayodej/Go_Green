package com.app.gogreen.models;

public class UserModelClass {
    private String userId;
    private String email;
    private String password;
    private String userName;
    private String address;

    public UserModelClass() { }

    public UserModelClass(String userId, String email, String password, String userName, String address) {
        this.userId = userId;
        this.email = email;
        this.password = password;
        this.userName = userName;
        this.address = address;
    }

    public String getUserId() {
        return userId;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getUserName() {
        return userName;
    }

    public String getAddress() {
        return address;
    }
}
