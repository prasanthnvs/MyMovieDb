package com.prasanth.lastmile.rest;

import android.util.Log;


import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.prasanth.lastmile.models.MovieItem;
import com.prasanth.lastmile.rest.responses.GenreResponse;
import com.prasanth.lastmile.rest.responses.MovieResponse;
import com.prasanth.lastmile.rest.responses.MoviesResponse;
import com.prasanth.lastmile.utils.AppExecutors;
import com.prasanth.lastmile.utils.GenreMap;
import com.prasanth.lastmile.utils.MovieDbConfig;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Response;

import static com.prasanth.lastmile.utils.Constants.NETWORK_TIMEOUT;

public class MovieApiClient {

    private static final String TAG = "MovieApiClient";

    private static MovieApiClient instance;
    private MutableLiveData<List<MovieItem>> mPopularMovies;
    private MutableLiveData<MovieResponse> mMovie;
    private GenreResponse mGenres;
    private RetrieveMovieDetailsRunnable mRetrieveMovieDetailsRunnable;
    private RetrievePopularMoviesRunnable mRetrievePopularMoviesRunnable;
    private RetrieveGenresRunnable mRetrieveGenresRunnable;
    private MutableLiveData<Boolean> mMovieRequestTimeout = new MutableLiveData<>();

    public static MovieApiClient getInstance(){
        if(instance == null){
            instance = new MovieApiClient();
        }
        return instance;
    }

    private MovieApiClient(){
        mPopularMovies = new MutableLiveData<>();
        mMovie = new MutableLiveData<>();
    }

    public LiveData<List<MovieItem>> getMovies(){
        return mPopularMovies;
    }

    public LiveData<MovieResponse> getMovie(){
        return mMovie;
    }

    public GenreResponse getGenres(){
        return mGenres;
    }

    public LiveData<Boolean> isMovieRequestTimedOut(){
        return mMovieRequestTimeout;
    }


    public void searchGenresList() {
        if (mRetrieveGenresRunnable != null) {
            mRetrieveGenresRunnable = null;
        }
        mRetrieveGenresRunnable = new RetrieveGenresRunnable();

        final Future handler = AppExecutors.getInstance().networkIO().submit(mRetrieveGenresRunnable);

        AppExecutors.getInstance().networkIO().schedule(new Runnable() {
            @Override
            public void run() {
                // let the user know its timed out
                Log.d(TAG,"TIME OUT Cancelling the Genres Task");
                handler.cancel(true);
            }
        }, NETWORK_TIMEOUT, TimeUnit.MILLISECONDS);
    }

    public void searchPopularMovies(int pageNumber){
        if(mRetrievePopularMoviesRunnable != null){
            mRetrievePopularMoviesRunnable = null;
        }
        mRetrievePopularMoviesRunnable = new RetrievePopularMoviesRunnable(pageNumber);

        final Future handler = AppExecutors.getInstance().networkIO().submit(mRetrievePopularMoviesRunnable);

        AppExecutors.getInstance().networkIO().schedule(new Runnable() {
            @Override
            public void run() {
                // let the user know its timed out
                Log.d(TAG,"TIME OUT Cancelling the Popular Movies Task");
                handler.cancel(true);
            }
        }, NETWORK_TIMEOUT, TimeUnit.MILLISECONDS);
    }

    public void searchMovieById(String movieId){
        Log.d(TAG, "SearchMovieByID: " + movieId);
        if(mRetrieveMovieDetailsRunnable != null){
            mRetrieveMovieDetailsRunnable = null;
        }
        mRetrieveMovieDetailsRunnable = new RetrieveMovieDetailsRunnable(movieId);

        final Future handler = AppExecutors.getInstance().networkIO().submit(mRetrieveMovieDetailsRunnable);

        AppExecutors.getInstance().networkIO().schedule(new Runnable() {
            @Override
            public void run() {
                // let the user know its timed out
                Log.d(TAG,"TIME OUT Cancelling the MovieDetails Task");
                handler.cancel(true);
            }
        }, NETWORK_TIMEOUT, TimeUnit.MILLISECONDS);
    }

    private class RetrievePopularMoviesRunnable implements Runnable{

        private int pageNumber;
        boolean cancelRequest;

        public RetrievePopularMoviesRunnable(int pageNumber) {
            this.pageNumber = pageNumber;
            cancelRequest = false;
        }

        @Override
        public void run() {
            try {
                Response response = getPopularMovies(pageNumber).execute();
                if(cancelRequest){
                    return;
                }
                if(response.code() == 200){
                    List<MovieItem> list = new ArrayList<>(((MoviesResponse)response.body()).getMovies());
                    if(pageNumber == 1){
                        mPopularMovies.postValue(list);
                    }
                    else{
                        List<MovieItem> currentMovies = mPopularMovies.getValue();
                        currentMovies.addAll(list);
                        mPopularMovies.postValue(currentMovies);
                    }
                }
                else{
                    String error = response.errorBody().string();
                    Log.e(TAG, "run: " + error );
                    mPopularMovies.postValue(null);
                }
            } catch (IOException e) {
                e.printStackTrace();
                mPopularMovies.postValue(null);
            }
        }

        private Call<MoviesResponse> getPopularMovies(int pageNumber){
            return ServiceModule.getMoviesApi().popularMovies(
                    MovieDbConfig.API_KEY,
                    pageNumber
            );
        }

        private void cancelRequest(){
            Log.d(TAG, "cancelRequest: canceling the popular movies request.");
            cancelRequest = true;
        }
    }

    private class RetrieveMovieDetailsRunnable implements Runnable{

        private String movieId;
        boolean cancelRequest;

        public RetrieveMovieDetailsRunnable(String aMovieId) {
            this.movieId = aMovieId;
            cancelRequest = false;
        }

        @Override
        public void run() {
            try {
                Response response = getMovie(movieId).execute();
                if(cancelRequest){
                    return;
                }
                if(response.code() == 200){
                    Log.d(TAG, "Response success" );
                    MovieResponse movieResponse = ((MovieResponse)response.body());
                    mMovie.postValue(movieResponse);
                }
                else{
                    String error = response.errorBody().string();
                    Log.e(TAG, "run: " + error );
                    mMovie.postValue(null);
                }
            } catch (IOException e) {
                e.printStackTrace();
                mMovie.postValue(null);
            }
        }

        private Call<MovieResponse> getMovie(String movieId){
            return ServiceModule.getMoviesApi().movieDetails(
                    Integer.valueOf(movieId),
                    MovieDbConfig.API_KEY
            );
        }

        private void cancelRequest(){
            Log.d(TAG, "cancelRequest: canceling the Movie Details request.");
            cancelRequest = true;
        }
    }


    private class RetrieveGenresRunnable implements Runnable{

        boolean cancelRequest;

        public RetrieveGenresRunnable() {
            cancelRequest = false;
        }

        @Override
        public void run() {
            try {
                Response response = getGenres().execute();
                if(cancelRequest){
                    return;
                }
                if(response.code() == 200){
                    Log.d(TAG, "Genre Response:");
                    mGenres = ((GenreResponse)response.body());
                    GenreMap.populateGenreMap(mGenres.getGenres());
                }
                else{
                    String error = response.errorBody().string();
                    Log.e(TAG, "run: " + error );
                    mMovie.postValue(null);
                }
            } catch (IOException e) {
                e.printStackTrace();
                mMovie.postValue(null);
            }
        }

        private Call<GenreResponse> getGenres(){
            return ServiceModule.getMoviesApi().movieGenreList(
                    MovieDbConfig.API_KEY
            );
        }

        private void cancelRequest(){
            Log.d(TAG, "cancelRequest: canceling the Genres request.");
            cancelRequest = true;
        }
    }

    public void cancelRequest(){
        if(mRetrievePopularMoviesRunnable != null){
            mRetrievePopularMoviesRunnable.cancelRequest();
        }
        if(mRetrieveMovieDetailsRunnable != null){
            mRetrieveMovieDetailsRunnable.cancelRequest();
        }

        if(mRetrieveGenresRunnable != null){
            mRetrieveGenresRunnable.cancelRequest();
        }

    }
}
