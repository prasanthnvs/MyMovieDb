package com.prasanth.lastmile.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;

import com.prasanth.lastmile.R;
import com.prasanth.lastmile.adapters.MovieRecyclerAdapter;
import com.prasanth.lastmile.adapters.OnMovieListener;
import com.prasanth.lastmile.models.MovieItem;
import com.prasanth.lastmile.utils.VerticalItemDecorator;
import com.prasanth.lastmile.viewmodels.MovieListViewModel;

import java.util.List;

import static com.prasanth.lastmile.utils.Constants.MOVIE_ID;


public class MovieListActivity extends BaseActivity implements OnMovieListener {

    private static final String TAG = "MovieListActivity";

    private MovieListViewModel mMovieListViewModel;
    private RecyclerView mRecyclerView;
    private MovieRecyclerAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_list);
        mRecyclerView = findViewById(R.id.movie_list);

        mMovieListViewModel = ViewModelProviders.of(this).get(MovieListViewModel.class);

        initRecyclerView();
        subscribeObservers();
        if(!mMovieListViewModel.isViewingMovies()){

            mMovieListViewModel.setIsViewingMovies(false);
        }

        setSupportActionBar((Toolbar)findViewById(R.id.toolbar));
        mMovieListViewModel.searchMoviesApi(1);
    }

    private void subscribeObservers(){
        mMovieListViewModel.getMovies().observe(this, new Observer<List<MovieItem>>() {
            @Override
            public void onChanged(@Nullable List<MovieItem> movies) {
                if(movies != null){
                    if(mMovieListViewModel.isViewingMovies()){
                        mAdapter.setMovies(movies);
                    }
                }
            }
        });

        mMovieListViewModel.isPopularMoviesQueryExhausted().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean aBoolean) {
                Log.d(TAG, "onChanged: the popular movies query is exhausted." + aBoolean);
                if(aBoolean) {
                    mAdapter.setPopularMovieQueryExhausted();
                }
            }
        });
    }

    private void initRecyclerView(){
        mAdapter = new MovieRecyclerAdapter(this);
        VerticalItemDecorator itemDecorator = new VerticalItemDecorator(30);
        mRecyclerView.addItemDecoration(itemDecorator);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {

                if(!mRecyclerView.canScrollVertically(1)){
                    // search the next page
                    mMovieListViewModel.searchNextPage();
                }
            }
        });
    }

    @Override
    public void onMovieClick(int position) {
        Intent intent = new Intent(this, MovieDetailsActivity.class);
        intent.putExtra(MOVIE_ID, mAdapter.getSelectedMovie(position).getId());
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
