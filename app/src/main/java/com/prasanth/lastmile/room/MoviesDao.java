package com.prasanth.lastmile.room;


import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Ignore;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;


import com.prasanth.lastmile.models.MovieItem;

import java.util.List;

import static androidx.room.OnConflictStrategy.IGNORE;
import static androidx.room.OnConflictStrategy.REPLACE;


@Dao
public interface MoviesDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertMovie(MovieItem... movieItem);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAllMovies(List<MovieItem>  movieItems);

    @Query("SELECT * FROM movies " + "ORDER BY popularity DESC LIMIT (:pageNumber * 20)")
    List<MovieItem>searchPopularMovies(int pageNumber);

    @Query("SELECT * FROM movies WHERE id = :movie_id")
    MovieItem getMovie(String movie_id);

}









