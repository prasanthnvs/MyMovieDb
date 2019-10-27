package com.prasanth.lastmile.viewmodels;


import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.prasanth.lastmile.repositories.MovieRepository;
import com.prasanth.lastmile.rest.responses.MovieResponse;


public class MovieViewModel extends ViewModel {

    private MovieRepository mMovieRepository;
    private String mMovieId;
    private boolean mDidRetrieveMovie;

    public MovieViewModel() {
        mMovieRepository = MovieRepository.getInstance();
        mDidRetrieveMovie = false;
    }

    public LiveData<MovieResponse> getMovie(){
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
