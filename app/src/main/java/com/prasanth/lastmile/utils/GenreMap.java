package com.prasanth.lastmile.utils;

import android.util.Log;

import com.prasanth.lastmile.models.GenreItem;

import java.util.HashMap;
import java.util.List;

public class GenreMap {
    private static final String TAG = "GenreMap";

    private static HashMap<Integer, String> mGenreMap = new HashMap<Integer, String>();

    public static String getGenreNames(List<Integer> ids) {
        StringBuilder genreNames = new StringBuilder();
        for(int id : ids){
            genreNames.append(mGenreMap.get(id));
            genreNames.append("  ");
        }
        return genreNames.toString();
    }

    public static String getGenres(List<GenreItem> genreItems) {
        StringBuilder genreNames = new StringBuilder();
        for(GenreItem genre : genreItems){
            genreNames.append(genre.getName());
            genreNames.append("  ");
        }
        return genreNames.toString();
    }

    public static void populateGenreMap(List<GenreItem> genres){
        if(genres != null) {
            for (GenreItem genre : genres) {
                Log.d(TAG, "Genre id:" + genre.getId() + ", Genre Name:" + genre.getName() );
                mGenreMap.put(genre.getId(), genre.getName());
            }
        }
    }
}
