package com.prasanth.lastmile.ui;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import com.prasanth.lastmile.R;
import com.prasanth.lastmile.models.MovieDetails;
import com.prasanth.lastmile.utils.GenreMap;
import com.prasanth.lastmile.viewmodels.MovieViewModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.prasanth.lastmile.utils.MovieDbConfig.IMAGE_URL_BASE_PATH;

public class MovieDetailsActivity extends BaseActivity {

    private static final String TAG = "MovieDetailsActivity";

    // UI components
    private AppCompatImageView mMovieImage;
    private TextView mMovieTitle, mMoviePopularity,mMovieOverview,
            mMovieGenres,mMovieReleaseDate,mMovieRunTime,mMovieHomePageLink;
    private LinearLayout mMovieDetailsContainer;
    private ScrollView mScrollView;

    private MovieViewModel mMovieViewModel;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie);

        mMovieImage = findViewById(R.id.movie_image);
        mMovieTitle = findViewById(R.id.movie_title);
        mMovieGenres = findViewById(R.id.movie_genre);
        mMoviePopularity = findViewById(R.id.movie_popularity);
        mMovieDetailsContainer = findViewById(R.id.movie_details_container);
        mMovieOverview = findViewById(R.id.movie_overview);
        mMovieReleaseDate = findViewById(R.id.movie_release_year);
        mMovieRunTime = findViewById(R.id.movie_runtime);
        mMovieHomePageLink = findViewById(R.id.movie_homepage_link);
        mScrollView = findViewById(R.id.parent);

        mMovieViewModel = ViewModelProviders.of(this).get(MovieViewModel.class);

        showProgressBar(true);
        subscribeObservers();
        getIncomingIntent();
    }

    private void getIncomingIntent(){
        if(getIntent().hasExtra("movieid")){
            Integer movie_id = getIntent().getIntExtra("movieid", 1);
            Log.d(TAG, "getIncomingIntent Movie ID: " + movie_id);
            mMovieViewModel.searchMovieById(String.valueOf(movie_id));
        }
    }

    private void subscribeObservers(){
        mMovieViewModel.getMovie().observe(this, new Observer<MovieDetails>() {
            @Override
            public void onChanged(@Nullable MovieDetails movie) {
                if(movie != null){
                    if(String.valueOf(movie.getId()).equals(mMovieViewModel.getMovieId())){
                        setMovieProperties(movie);
                        mMovieViewModel.setRetrievedMovie(true);
                    }
                }
            }
        });

        mMovieViewModel.isMovieRequestTimedOut().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean aBoolean) {
                if(aBoolean && !mMovieViewModel.didRetrieveMovie()){
                    Log.d(TAG, "onChanged: timed out..");
                    displayErrorScreen("Error retrieving data. Check network connection.");
                }
            }
        });
    }

    private void displayErrorScreen(String errorMessage){
        mMovieTitle.setText("Error retrieving Movie...");
        mMoviePopularity.setText("");
        mMovieGenres.setText("");
        TextView textView = new TextView(this);
        if(!errorMessage.equals("")){
            textView.setText(errorMessage);
        }
        else{
            textView.setText("Error");
        }
        textView.setTextSize(15);
        textView.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT
        ));
        mMovieDetailsContainer.removeAllViews();
        mMovieDetailsContainer.addView(textView);

        RequestOptions requestOptions = new RequestOptions()
                .placeholder(R.drawable.ic_launcher_background);

        Glide.with(this)
                .setDefaultRequestOptions(requestOptions)
                .load(R.drawable.ic_launcher_background)
                .into(mMovieImage);

        showParent();
        showProgressBar(false);
    }

    private void setMovieProperties(MovieDetails movie){
        if(movie != null){
            RequestOptions requestOptions = new RequestOptions()
                    .placeholder(R.drawable.ic_launcher_background);

            Glide.with(this)
                    .setDefaultRequestOptions(requestOptions)
                    .load(IMAGE_URL_BASE_PATH + movie.getPosterPath())
                    .into(mMovieImage);

            mMovieTitle.setText(movie.getTitle());
            String genres = movie.getGenreIds();

            if(genres.startsWith("[")) {
                genres = genres.substring(1,genres.length()-1);
                List<String> genresList = new ArrayList<String>(Arrays.asList(genres.split(",")));
                genres = GenreMap.getGenresList(genresList);
            }

            mMovieGenres.setText(genres);
            mMoviePopularity.setText(String.valueOf(Math.round(movie.getPopularity())));

            mMovieDetailsContainer.removeAllViews();
            mMovieOverview.setText("Overview : " + movie.getOverview());
            mMovieDetailsContainer.addView(mMovieOverview);
            mMovieReleaseDate.setText("ReleaseYear : " +  movie.getReleaseDate().split("-")[0]);
            mMovieDetailsContainer.addView(mMovieReleaseDate);
            if(movie.getRuntime() != null && !movie.getRuntime().toString().isEmpty())
                mMovieRunTime.setText( "RunTime : " + String.valueOf(movie.getRuntime() + " min"));
            mMovieDetailsContainer.addView(mMovieRunTime);
            if(movie.getHomepage() != null && !movie.getHomepage().isEmpty())
                mMovieHomePageLink.setText("HomePage : " + movie.getHomepage());
            mMovieDetailsContainer.addView(mMovieHomePageLink);
        }

        showParent();
        showProgressBar(false);
    }

    private void showParent(){
        mScrollView.setVisibility(View.VISIBLE);
    }
}