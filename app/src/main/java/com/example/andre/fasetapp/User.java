package com.example.andre.fasetapp;

/**
 * Created by ANDRE on 2/2/2018.
 */

public class User {
    public String name;
    public String email;
    public String userid;
    public User(){

    }

    public User(String name, String email, String userid)
    {
        this.name = name;
        this.email=email;
        this.userid=userid;
    }

    public String getUserId(){
        return userid;
    }


    public void setUserId(String userid){

        this.userid=userid;
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


}
