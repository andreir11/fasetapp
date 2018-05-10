package com.example.andre.fasetapp;

import com.google.firebase.database.Exclude;

/**
 * Created by ANDRE on 4/11/2018.
 */
public class ImageUploadInfoWithDateOnCalendar {

    public String imageName;

    public String imageURL;
    public String imageDailyD;
    private String mKey;


    public ImageUploadInfoWithDateOnCalendar() {

    }

    public ImageUploadInfoWithDateOnCalendar(String name, String url, String dt) {

        this.imageName = name;
        this.imageURL= url;
        this.imageDailyD = dt;
    }

    public String getImageName() {
        return imageName;
    }

    public String getImageURL() {
        return imageURL;
    }

    public String getimageDailyD() {
        return imageDailyD;
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