package com.prasanth.lastmile.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.prasanth.lastmile.models.MovieItem;
import com.prasanth.lastmile.repositories.MovieRepository;

import java.util.List;

public class MovieListViewModel extends AndroidViewModel {

    private MovieRepository mMovieRepository;
    private boolean mIsViewingMovies;

    public MovieListViewModel(@NonNull Application application) {
        super(application);
        mMovieRepository = MovieRepository.getInstance(application);
    }

    public LiveData<List<MovieItem>> getMovies(){
        return mMovieRepository.getPopularMovies();
    }

    public void searchMoviesApi(int pageNumber){
        mIsViewingMovies = true;
        mMovieRepository.searchPopularMovies(pageNumber);
    }

    public void searchNextPage(){
        if(mIsViewingMovies) {
            mMovieRepository.searchNextPage();
        }
    }

    public boolean isViewingMovies(){
        return mIsViewingMovies;
    }

    public void setIsViewingMovies(boolean isViewingMovies){
        mIsViewingMovies = isViewingMovies;
    }

    public LiveData<Boolean> isPopularMoviesQueryExhausted(){
        return mMovieRepository.isPopularMoviesQueryExhausted();
    }
}
