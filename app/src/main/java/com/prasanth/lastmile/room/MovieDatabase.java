package com.prasanth.lastmile.room;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.prasanth.lastmile.models.GenreItem;
import com.prasanth.lastmile.models.MovieDetails;
import com.prasanth.lastmile.models.MovieItem;


@Database(entities = {MovieItem.class, MovieDetails.class, GenreItem.class}, version = 1)
@TypeConverters({Converters.class})
public abstract class MovieDatabase extends RoomDatabase {

    public static final String DATABASE_NAME = "movies";

    private static MovieDatabase instance;

    public static MovieDatabase getInstance(final Context context){
        if(instance == null){
            instance = Room.databaseBuilder(
                    context.getApplicationContext(),
                    MovieDatabase.class,
                    DATABASE_NAME
            ).fallbackToDestructiveMigration()
                    .build();
        }
        return instance;
    }

    public abstract MoviesDao getMoviesDao();

    public abstract MovieDetailsDao getMovieDetailsDao();

    public abstract GenresDao getGenresDao();

}






