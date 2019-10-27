package com.prasanth.lastmile.rest.responses;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.prasanth.lastmile.models.GenreItem;

import java.util.List;

public class GenreResponse {

    @SerializedName("genres")
    @Expose
    private List<GenreItem> genres;

    public List<GenreItem> getGenres() {
        return genres;
    }

    public void setGenres(List<GenreItem> genres) {
        this.genres = genres;
    }

    @Override
    public String toString() {
        return "GenreResponse{" +
                "genres=" + genres +
                '}';
    }
}
