package com.prasanth.lastmile.room;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.prasanth.lastmile.models.MovieDetails;

@Dao
public interface MovieDetailsDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertMovieDetails(MovieDetails... movieDetails);

    @Query("SELECT * FROM movies WHERE id = :movie_id")
    MovieDetails getMovie(String movie_id);
}
