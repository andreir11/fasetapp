package com.example.andre.fasetapp;

/**
 * Created by ANDRE on 4/1/2018.
 */
public class UserProfile {
    public String userAge;
    public String userEmail;
    public String userName;
    public String userUid;
    public String userSex;
    public UserProfile(){
    }

    public UserProfile(String userAge, String userEmail, String userName, String userSex) {
        this.userAge = userAge;
        this.userEmail = userEmail;
        this.userName = userName;
        this.userSex = userSex;
        //this.userUid = userUid;
    }

    public String getUserAge() {
        return userAge;
    }

    public void setUserAge(String userAge) {
        this.userAge = userAge;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getUserName() {
        return userName;
    }

    public String getUserSex() {
        return userSex;
    }

    public void setUserSex(String userSex) {
        this.userSex = userSex;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
/*
    public String getUserUid() {
        return userUid;
    }

    public void setUserUid(String userUid) {
        this.userUid = userUid;
    }
*/


}