package com.prasanth.lastmile.room;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.prasanth.lastmile.models.GenreItem;

import java.util.List;

@Dao
public interface GenresDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertGenre(GenreItem... genre);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertGenres(List<GenreItem> genres);

    @Query("SELECT * FROM genres")
    List<GenreItem> getGenres();
}
