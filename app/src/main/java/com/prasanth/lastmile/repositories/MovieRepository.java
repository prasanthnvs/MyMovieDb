package com.prasanth.lastmile.repositories;

import android.content.Context;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.prasanth.lastmile.models.GenreItem;
import com.prasanth.lastmile.models.MovieDetails;
import com.prasanth.lastmile.models.MovieItem;
import com.prasanth.lastmile.rest.MovieApiClient;
import com.prasanth.lastmile.room.GenresDao;
import com.prasanth.lastmile.room.MovieDatabase;
import com.prasanth.lastmile.room.MovieDetailsDao;
import com.prasanth.lastmile.room.MoviesDao;
import com.prasanth.lastmile.utils.GenreMap;

import java.util.List;

import static com.prasanth.lastmile.utils.Constants.PAGE_SIZE;

public class MovieRepository implements MovieApiClient.NetworkListener {
    private static final String TAG = "MovieRepository";

    private static MovieRepository instance;
    private MovieApiClient mMovieApiClient;
    private int mPageNumber;
    private String mMovieId;
    private MutableLiveData<Boolean> mIsPopularMoviesQueryExhausted = new MutableLiveData<>();
    private MediatorLiveData<List<MovieItem>> mMovies = new MediatorLiveData<>();
    private MediatorLiveData<MovieDetails> mMovieDetails = new MediatorLiveData<>();
    private MoviesDao mMoviesDao;
    private MovieDetailsDao mMovieDetailsDao;
    private GenresDao mGenresDao;

    public static MovieRepository getInstance(Context context){
        if(instance == null){
            instance = new MovieRepository(context);
        }
        return instance;
    }

    private MovieRepository(Context context){
        mMovieApiClient = MovieApiClient.getInstance(this);
        mMoviesDao = MovieDatabase.getInstance(context).getMoviesDao();
        mMovieDetailsDao = MovieDatabase.getInstance(context).getMovieDetailsDao();
        mGenresDao = MovieDatabase.getInstance(context).getGenresDao();
        initMediators();
        initGenresList();
    }

    private void initGenresList(){
        mMovieApiClient.searchGenresList();
    }

    private void initMediators(){
        LiveData<MovieDetails> movieDetailsApiSource = mMovieApiClient.getMovie();
        mMovieDetails.addSource(movieDetailsApiSource, new Observer<MovieDetails>() {
            @Override
            public void onChanged(MovieDetails movieDetails) {
                if(movieDetails != null)
                    mMovieDetails.setValue(movieDetails);
            }
        });

        LiveData<List<MovieItem>> movieListApiSource = mMovieApiClient.getMovies();
        mMovies.addSource(movieListApiSource, new Observer<List<MovieItem>>() {
            @Override
            public void onChanged(@Nullable final List<MovieItem> movies) {

                if(movies != null){
                    Log.d(TAG, "Query success updating mMovies");
                    mMovies.setValue(movies);
                }
            }
        });
    }

    public LiveData<List<MovieItem>> getPopularMovies(){
        return mMovies;
    }

    public LiveData<MovieDetails> getMovie(){
        return mMovieDetails;
    }

    public LiveData<Boolean> isPopularMoviesQueryExhausted(){
        return mIsPopularMoviesQueryExhausted;
    }

    public void searchMovieById(String movieId){
        mMovieId = movieId;
        mMovieApiClient.searchMovieById(movieId);
    }

    public void searchPopularMovies(int pageNumber){
        if(GenreMap.getGenreMapSize() == 0)
            initGenresList();

        if(pageNumber == 0){
            pageNumber = 1;
        }
        mPageNumber = pageNumber;
        mIsPopularMoviesQueryExhausted.setValue(false);
        mMovieApiClient.searchPopularMovies(pageNumber);
    }

    public void searchNextPage(){
        searchPopularMovies(mPageNumber + 1);
    }

    public void cancelRequest(){
        mMovieApiClient.cancelRequest();
    }

    public LiveData<Boolean> isMovieRequestTimedOut(){
        return mMovieApiClient.isMovieRequestTimedOut();
    }

    @Override
    public void onMovieDetailsNetworkError() {
        Log.d(TAG,"onMovieDetailsNetworkError()");
        new Thread(new Runnable() {
            @Override
            public void run() {
                mMovieDetails.postValue(mMovieDetailsDao.getMovie(mMovieId));
            }
        }).start();
    }

    @Override
    public void onPopularMoviesNetworkError() {
        Log.d(TAG,"onPopularMoviesNetworkError()");
        new Thread(new Runnable() {
            @Override
            public void run() {
                List<MovieItem> popularMovies = mMoviesDao.searchPopularMovies(mPageNumber);

                if(popularMovies.size() > 0) {
                    mMovies.postValue(popularMovies);
                }

                if(popularMovies.size() % PAGE_SIZE != 0){
                    mIsPopularMoviesQueryExhausted.postValue(true);
                }else {
                    mIsPopularMoviesQueryExhausted.postValue(true);
                }

            }
        }).start();
    }

    @Override
    public void onGenresNetworkError(){
        Log.d(TAG,"onGenresNetworkError()");
        new Thread(new Runnable() {
            @Override
            public void run() {
                GenreMap.populateGenreMap(mGenresDao.getGenres());
            }
        }).start();
    }

    @Override
    public void onPopularMoviesResponse(final List<MovieItem> movies) {
        Log.d(TAG,"onPopularMoviesResponse");
        new Thread(new Runnable() {
            @Override
            public void run() {
                mMoviesDao.insertAllMovies(movies);
            }
        }).start();
    }

    @Override
    public void onMovieDetailsResponse(final MovieDetails movieResponse) {
        Log.d(TAG,"onMovieDetailsResponse");
        new Thread(new Runnable() {
                @Override
                public void run() {
                    mMovieDetailsDao.insertMovieDetails(movieResponse);
                }
            }).start();
    }

    @Override
    public void onGenresResponse(final List<GenreItem> genres) {
        Log.d(TAG,"onGenresResponse");
        new Thread(new Runnable() {
            @Override
            public void run() {
                mGenresDao.insertGenres(genres);
            }
        }).start();
    }
}
