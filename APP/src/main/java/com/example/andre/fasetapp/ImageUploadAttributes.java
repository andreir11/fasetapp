package com.example.andre.fasetapp;

import com.google.firebase.database.Exclude;

/**
 * Created by ANDRE on 4/11/2018.
 */
public class ImageUploadAttributes {

    private String brand;
    private String season;
    private String size;
    private String category;
    public String name;
    public String date;
    public String price;
    public String tag;
    public String imageURL;
    private String mKey;
    public String id;

    public ImageUploadAttributes() {

    }

    public ImageUploadAttributes(String id, String name, String url, String date, String tag, String price, String brnd, String sson, String ctgory, String sze) {
        this.id = id;
        this.name = name;
        this.imageURL= url;
        this.date = date;
        this.tag = tag;
        this.price = price;
        this.brand = brnd;
        this.season = sson;
        this.category=ctgory;
        this.size = sze;
    }

    public String getid() {return id;}

    public String getname() {
        return name;
    }

    public String getImageURL() {
        return imageURL;
    }

    public String getdate() {
        return date;
    }

    public String getprice() {
        return price;
    }

    public String gettag() {
        return tag;
    }

    public String getbrand() {
        return brand;
    }

    public String getseason() {
        return season;
    }

    public String getcategory() {
        return category;
    }

    public String getsize() {
        return size;
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