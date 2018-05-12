package com.example.andre.fasetapp;

import com.google.firebase.database.Exclude;

/**
 * Created by ANDRE on 4/11/2018.
 */
public class ImageUploadInfo {

    public String imageName;
    public String id;
    public String date;
    public String imageURL;
    public String category;
    private String mKey;


    public ImageUploadInfo() {

    }

    public ImageUploadInfo(String id, String name, String url, String date, String category) {
        this.id = id;
        this.imageName = name;
        this.imageURL= url;
        this.date=date;
        this.category=category;
    }
    public String getDate() {
        return date;
    }

    public String getid() {
        return id;
    }

    public String getImageName() {
        return imageName;
    }

    public String getImageURL() {
        return imageURL;
    }

    public String getcategory() {
        return category;
    }

    @Exclude
    public String getKey(){
        return mKey;
    }

    @Exclude
    public void setKey(String key){
        mKey=key;
    }

}