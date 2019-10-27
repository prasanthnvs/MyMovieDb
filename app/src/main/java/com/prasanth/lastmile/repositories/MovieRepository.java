package com.prasanth.lastmile.repositories;

import android.util.Log;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.prasanth.lastmile.models.MovieItem;
import com.prasanth.lastmile.rest.MovieApiClient;
import com.prasanth.lastmile.rest.responses.MovieResponse;

import java.util.List;

public class MovieRepository {
    private static final String TAG = "MovieRepository";

    private static MovieRepository instance;
    private MovieApiClient mMovieApiClient;
    private int mPageNumber;
    private MutableLiveData<Boolean> mIsQueryExhausted = new MutableLiveData<>();
    private MediatorLiveData<List<MovieItem>> mMovies = new MediatorLiveData<>();

    public static MovieRepository getInstance(){
        if(instance == null){
            instance = new MovieRepository();
        }
        return instance;
    }

    private MovieRepository(){
        mMovieApiClient = MovieApiClient.getInstance();
        initMediators();
        initGenresList();
    }

    private void initGenresList(){
        mMovieApiClient.searchGenresList();
    }

    private void initMediators(){
        LiveData<List<MovieItem>> movieListApiSource = mMovieApiClient.getMovies();
        mMovies.addSource(movieListApiSource, new Observer<List<MovieItem>>() {
            @Override
            public void onChanged(@Nullable List<MovieItem> movies) {

                if(movies != null){
                    mMovies.setValue(movies);
                    doneQuery(movies);
                }
                else{
                    // search database cache
                    doneQuery(null);
                }
            }
        });
    }

    private void doneQuery(List<MovieItem> list){
        if(list != null){
            if (list.size() % 30 != 0) {
                mIsQueryExhausted.setValue(true);
            }
        }
        else{
            mIsQueryExhausted.setValue(true);
        }
    }

    public LiveData<List<MovieItem>> getPopularMovies(){
        return mMovies;
    }

    public LiveData<MovieResponse> getMovie(){
        return mMovieApiClient.getMovie();
    }

    public void searchMovieById(String movieId){
        mMovieApiClient.searchMovieById(movieId);
    }

    public void searchPopularMovies(int pageNumber){
        Log.d(TAG, "searchPopularMovies: pageNumber " + pageNumber);
        if(pageNumber == 0){
            pageNumber = 1;
        }
        mPageNumber = pageNumber;
        mIsQueryExhausted.setValue(false);
        mMovieApiClient.searchPopularMovies(pageNumber);
    }

    public void searchNextPage(){
        Log.d(TAG, "searchNextPage");
        searchPopularMovies(mPageNumber + 1);
    }

    public void cancelRequest(){
        mMovieApiClient.cancelRequest();
    }

    public LiveData<Boolean> isMovieRequestTimedOut(){
        return mMovieApiClient.isMovieRequestTimedOut();
    }
}




