package com.prasanth.lastmile.models;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@Entity(tableName = "genres")
public class GenreItem {

    @SerializedName("id")
    @Expose
    @PrimaryKey(autoGenerate = true)
    @NonNull
    private Integer id;

    @SerializedName("name")
    @Expose
    @ColumnInfo(name = "genre_name")
    private String name;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    @Override
    public String toString() {
        return "GenreItem{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}