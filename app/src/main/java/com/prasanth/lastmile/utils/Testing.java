package com.prasanth.lastmile.utils;

import android.util.Log;

import com.prasanth.lastmile.models.MovieItem;

import java.util.List;

public class Testing {

    public static void printMovies(List<MovieItem>list, String tag){
        for(MovieItem movie: list){
            Log.d(tag, "onChanged: " + movie.getTitle());
        }
    }
}
