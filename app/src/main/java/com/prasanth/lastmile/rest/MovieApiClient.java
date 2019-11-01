package com.prasanth.lastmile.rest;

import android.util.Log;


import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.prasanth.lastmile.models.GenreItem;
import com.prasanth.lastmile.models.MovieDetails;
import com.prasanth.lastmile.models.MovieItem;
import com.prasanth.lastmile.rest.responses.GenreResponse;
import com.prasanth.lastmile.rest.responses.MovieDetailsResponse;
import com.prasanth.lastmile.rest.responses.MoviesResponse;
import com.prasanth.lastmile.utils.AppExecutors;
import com.prasanth.lastmile.utils.GenreMap;
import com.prasanth.lastmile.utils.MovieDbConfig;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Response;

import static com.prasanth.lastmile.utils.Constants.NETWORK_TIMEOUT;
import static com.prasanth.lastmile.utils.Constants.RESPONSE_OK;

public class MovieApiClient {

    private static final String TAG = "MovieApiClient";

    private static MovieApiClient instance;
    private MutableLiveData<List<MovieItem>> mPopularMovies;
    private MutableLiveData<MovieDetails> mMovie;
    private GenreResponse mGenres;
    private RetrieveMovieDetailsRunnable mRetrieveMovieDetailsRunnable;
    private RetrievePopularMoviesRunnable mRetrievePopularMoviesRunnable;
    private RetrieveGenresRunnable mRetrieveGenresRunnable;
    private MutableLiveData<Boolean> mMovieRequestTimeout = new MutableLiveData<>();
    private NetworkListener mListener;

    public interface NetworkListener{
        public void onPopularMoviesNetworkError();
        public void onMovieDetailsNetworkError();
        public void onGenresNetworkError();
        public void onPopularMoviesResponse(List<MovieItem> movies);
        public void onMovieDetailsResponse(MovieDetails movieResponse);
        public void onGenresResponse(List<GenreItem> genres);
    }

    public static MovieApiClient getInstance(NetworkListener listener){
        if(instance == null){
            instance = new MovieApiClient(listener);
        }
        return instance;
    }

    private MovieApiClient(NetworkListener listener){
        mListener = listener;
        mPopularMovies = new MutableLiveData<>();
        mMovie = new MutableLiveData<>();
        mGenres = new GenreResponse();
    }

    public LiveData<List<MovieItem>> getMovies(){
        return mPopularMovies;
    }

    public LiveData<MovieDetails> getMovie(){
        return mMovie;
    }

    public GenreResponse getGenres(){
        return mGenres;
    }

    public LiveData<Boolean> isMovieRequestTimedOut(){
        return mMovieRequestTimeout;
    }


    public void searchGenresList() {
        Log.d(TAG, "searchGenresList()");
        if (mRetrieveGenresRunnable != null) {
            mRetrieveGenresRunnable = null;
        }
        mRetrieveGenresRunnable = new RetrieveGenresRunnable();

        final Future handler = AppExecutors.getInstance().networkIO().submit(mRetrieveGenresRunnable);

        AppExecutors.getInstance().networkIO().schedule(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG,"TIME OUT Cancelling the Genres Task");
                handler.cancel(true);
            }
        }, NETWORK_TIMEOUT, TimeUnit.MILLISECONDS);
    }

    public void searchPopularMovies(int pageNumber){
        Log.d(TAG, "searchGenresList() pageNumber: " + pageNumber);
        if(mRetrievePopularMoviesRunnable != null){
            mRetrievePopularMoviesRunnable = null;
        }
        mRetrievePopularMoviesRunnable = new RetrievePopularMoviesRunnable(pageNumber);

        final Future handler = AppExecutors.getInstance().networkIO().submit(mRetrievePopularMoviesRunnable);

        AppExecutors.getInstance().networkIO().schedule(new Runnable() {
            @Override
            public void run() {
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

        mMovieRequestTimeout.setValue(false);
        AppExecutors.getInstance().networkIO().schedule(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG,"TIME OUT Cancelling the MovieDetails Task");
                mMovieRequestTimeout.postValue(true);
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
                if(response.code() == RESPONSE_OK){
                    List<MovieItem> list = new ArrayList<>(((MoviesResponse)response.body()).getMovies());
                    if(pageNumber == 1){
                        mPopularMovies.postValue(list);
                    }
                    else{
                        List<MovieItem> currentMovies = mPopularMovies.getValue();
                        currentMovies.addAll(list);
                        mPopularMovies.postValue(currentMovies);
                    }
                    mListener.onPopularMoviesResponse(list);
                }
                else{
                    String error = response.errorBody().string();
                    Log.e(TAG, "RetrievePopularMoviesRunnable run error: " + error );
                    mListener.onPopularMoviesNetworkError();
                }
            } catch (IOException e) {
                e.printStackTrace();
                mListener.onPopularMoviesNetworkError();
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
                if(response.code() == RESPONSE_OK){
                    MovieDetailsResponse movieResponse = ((MovieDetailsResponse)response.body());
                    MovieDetails movieDetails = new MovieDetails(movieResponse);
                    mMovie.postValue(movieDetails);
                    mListener.onMovieDetailsResponse(movieDetails);
                }
                else{
                    String error = response.errorBody().string();
                    Log.e(TAG, "RetrieveMovieDetailsRunnable run error: " + error );
                    mListener.onMovieDetailsNetworkError();
                }
            } catch (IOException e) {
                e.printStackTrace();
                mListener.onMovieDetailsNetworkError();
            }
        }

        private Call<MovieDetailsResponse> getMovie(String movieId){
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
                if(response.code() == RESPONSE_OK){
                    mGenres = ((GenreResponse)response.body());
                    GenreMap.populateGenreMap(mGenres.getGenres());
                    mListener.onGenresResponse(mGenres.getGenres());
                }
                else{
                    String error = response.errorBody().string();
                    Log.e(TAG, "RetrieveGenresRunnable run error: " + error );
                    mListener.onGenresNetworkError();
                }
            } catch (IOException e) {
                e.printStackTrace();
                mListener.onGenresNetworkError();
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
