package com.prasanth.lastmile.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.prasanth.lastmile.models.MovieItem;
import com.prasanth.lastmile.repositories.MovieRepository;

import java.util.List;

public class MovieListViewModel extends ViewModel {

    private MovieRepository mMovieRepository;
    private boolean mIsViewingMovies;
    private boolean mIsPerformingQuery;

    public MovieListViewModel() {
        mMovieRepository = MovieRepository.getInstance();
        mIsPerformingQuery = false;
    }

    public LiveData<List<MovieItem>> getMovies(){
        return mMovieRepository.getPopularMovies();
    }

    public void searchMoviesApi(int pageNumber){
        mIsViewingMovies = true;
        mIsPerformingQuery = true;
        mMovieRepository.searchPopularMovies(pageNumber);
    }

    public void searchNextPage(){
        if(!mIsPerformingQuery
                && mIsViewingMovies) {
            mMovieRepository.searchNextPage();
        }
    }

    public boolean isViewingMovies(){
        return mIsViewingMovies;
    }

    public void setIsViewingMovies(boolean isViewingMovies){
        mIsViewingMovies = isViewingMovies;
    }

    public void setIsPerformingQuery(Boolean isPerformingQuery){
        mIsPerformingQuery = isPerformingQuery;
    }

    public boolean onBackPressed(){
        if(mIsPerformingQuery){
            mMovieRepository.cancelRequest();
            mIsPerformingQuery = false;
        }
        if(mIsViewingMovies){
            mIsViewingMovies = false;
            return false;
        }
        return true;
    }
}
