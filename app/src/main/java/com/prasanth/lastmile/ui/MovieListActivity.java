package com.prasanth.lastmile.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;

import com.prasanth.lastmile.R;
import com.prasanth.lastmile.adapters.MovieRecyclerAdapter;
import com.prasanth.lastmile.adapters.OnMovieListener;
import com.prasanth.lastmile.models.MovieItem;
import com.prasanth.lastmile.utils.Testing;
import com.prasanth.lastmile.utils.VerticalItemDecorator;
import com.prasanth.lastmile.viewmodels.MovieListViewModel;

import java.util.List;


public class MovieListActivity extends BaseActivity implements OnMovieListener {

    private static final String TAG = "MovieListActivity";

    private MovieListViewModel mMovieListViewModel;
    private RecyclerView mRecyclerView;
    private MovieRecyclerAdapter mAdapter;
    private SearchView mSearchView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_list);
        mRecyclerView = findViewById(R.id.movie_list);
        mSearchView = findViewById(R.id.search_view);

        mMovieListViewModel = ViewModelProviders.of(this).get(MovieListViewModel.class);

        initRecyclerView();
        subscribeObservers();
        initSearchView();
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
                        Testing.printMovies(movies, "Movies test");
                        mMovieListViewModel.setIsPerformingQuery(false);
                        mAdapter.setMovies(movies);
                    }
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

    private void initSearchView(){
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {

                mAdapter.displayLoading();
                mMovieListViewModel.searchMoviesApi(1);
                mSearchView.clearFocus();

                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });
    }

    @Override
    public void onMovieClick(int position) {
        Intent intent = new Intent(this, MovieDetailsActivity.class);
        Log.d(TAG, "movie id : " + mAdapter.getSelectedMovie(position).getId());
        intent.putExtra("movieid", mAdapter.getSelectedMovie(position).getId());
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
