package com.example.andre.fasetapp;

import com.google.firebase.database.Exclude;

/**
 * Created by ANDRE on 4/11/2018.
 */
public class ImageUploadInfo2 {

    public String imageDateD;
    public String imageID;

    public String imageURL;
    private String mKey;


    public ImageUploadInfo2() {

    }

    public ImageUploadInfo2(String date, String name, String url) {
        this.imageID = date;
        this.imageDateD = name;
        this.imageURL= url;
    }
    public String getImageID() {
        return imageID;
    }

    public String getImageDateD() {
        return imageDateD;
    }

    public String getImageURL() {
        return imageURL;
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