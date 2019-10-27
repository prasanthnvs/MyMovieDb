package com.prasanth.lastmile.models;

import android.os.Parcelable;

public class Movie {

    private int id;
    private String original_title;
    private String release_date;
    private String title;

    public Movie(int id, String original_title, String release_date, String title) {
        this.id = id;
        this.original_title = original_title;
        this.release_date = release_date;
        this.title = title;
    }

    public Movie() {
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getOriginal_title() {
        return original_title;
    }

    public void setOriginal_title(String original_title) {
        this.original_title = original_title;
    }

    public String getRelease_date() {
        return release_date;
    }

    public void setRelease_date(String release_date) {
        this.release_date = release_date;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }


}
