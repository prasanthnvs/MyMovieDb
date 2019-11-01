package com.prasanth.lastmile.models;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.prasanth.lastmile.rest.responses.MovieDetailsResponse;
import com.prasanth.lastmile.utils.GenreMap;

@Entity(tableName = "movies_details")
public class MovieDetails {

    @PrimaryKey(autoGenerate = true)
    @NonNull
    private Integer id;

    @ColumnInfo(name = "title")
    private String title;

    @ColumnInfo(name = "popularity")
    private Double popularity;

    @ColumnInfo(name = "releaseDate")
    private String releaseDate;

    @ColumnInfo(name = "genre_ids")
    private String genreIds;

    @ColumnInfo(name = "runtime")
    private Integer runtime;

    @ColumnInfo(name = "overview")
    private String overview;

    @ColumnInfo(name = "homepage")
    private String homepage;

    @ColumnInfo(name = "poster_path")
    private String posterPath;


    public MovieDetails() {

    }

    public MovieDetails(MovieDetailsResponse response){
        this.id = response.getId();
        this.genreIds = GenreMap.getGenres(response.getGenres());
        this.overview = response.getOverview();
        this.popularity = response.getPopularity();
        this.homepage = response.getHomepage();
        this.releaseDate = response.getReleaseDate();
        this.runtime = response.getRuntime();
        this.title = response.getTitle();
        this.posterPath = response.getPosterPath();
    }

    @NonNull
    public Integer getId() {
        return id;
    }

    public void setId(@NonNull Integer id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Double getPopularity() {
        return popularity;
    }

    public void setPopularity(Double popularity) {
        this.popularity = popularity;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public String getGenreIds() {
        return genreIds;
    }

    public void setGenreIds(String genreIds) {
        this.genreIds = genreIds;
    }

    public Integer getRuntime() {
        return runtime;
    }

    public void setRuntime(Integer runtime) {
        this.runtime = runtime;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public String getHomepage() {
        return homepage;
    }

    public void setHomepage(String homepage) {
        this.homepage = homepage;
    }

    public String getPosterPath() {
        return posterPath;
    }

    public void setPosterPath(String posterPath) {
        this.posterPath = posterPath;
    }
}
