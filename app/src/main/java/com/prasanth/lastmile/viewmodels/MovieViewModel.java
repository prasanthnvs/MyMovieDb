package com.prasanth.lastmile.viewmodels;


import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.prasanth.lastmile.models.MovieDetails;
import com.prasanth.lastmile.repositories.MovieRepository;


public class MovieViewModel extends AndroidViewModel {

    private MovieRepository mMovieRepository;
    private String mMovieId;
    private boolean mDidRetrieveMovie;

    public MovieViewModel(@NonNull Application application) {
        super(application);
        mMovieRepository = MovieRepository.getInstance(application);
        mDidRetrieveMovie = false;
    }

    public LiveData<MovieDetails> getMovie(){
        return mMovieRepository.getMovie();
    }

    public LiveData<Boolean> isMovieRequestTimedOut(){
        return mMovieRepository.isMovieRequestTimedOut();
    }

    public void searchMovieById(String movieId){
        mMovieId = movieId;
        mMovieRepository.searchMovieById(movieId);
    }

    public String getMovieId() {
        return mMovieId;
    }

    public void setRetrievedMovie(boolean retrievedMovie){
        mDidRetrieveMovie = retrievedMovie;
    }

    public boolean didRetrieveMovie(){
        return mDidRetrieveMovie;
    }
}
