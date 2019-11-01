package com.prasanth.lastmile.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.prasanth.lastmile.R;
import com.prasanth.lastmile.models.MovieItem;
import com.prasanth.lastmile.utils.GenreMap;

import java.util.ArrayList;
import java.util.List;

import static com.prasanth.lastmile.utils.Constants.EXHAUSTED;
import static com.prasanth.lastmile.utils.Constants.LOADING;
import static com.prasanth.lastmile.utils.MovieDbConfig.IMAGE_URL_BASE_PATH;

public class MovieRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int MOVIE_TYPE = 1;
    private static final int LOADING_TYPE = 2;
    private static final int EXHAUSTED_TYPE = 3;

    private List<MovieItem> mMovies = new ArrayList<>();
    private OnMovieListener mOnMovieListener;

    public MovieRecyclerAdapter(OnMovieListener mOnMovieListener) {
        this.mOnMovieListener = mOnMovieListener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View view = null;
        switch (i){

            case MOVIE_TYPE:{
                view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.layout_movie_list_item, viewGroup, false);
                return new MovieViewHolder(view, mOnMovieListener);
            }

            case LOADING_TYPE:{
                view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.layout_loading_list_item, viewGroup, false);
                return new LoadingViewHolder(view);
            }

            case EXHAUSTED_TYPE:{
                view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.layout_search_exhausted, viewGroup, false);
                return new SearchExhaustedViewHolder(view);
            }

            default:{
                view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.layout_movie_list_item, viewGroup, false);
                return new MovieViewHolder(view, mOnMovieListener);
            }
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {

        int itemViewType = getItemViewType(i);
        if(itemViewType == MOVIE_TYPE){
            RequestOptions requestOptions = new RequestOptions()
                    .placeholder(R.drawable.ic_launcher_background);

            Glide.with(viewHolder.itemView.getContext())
                    .setDefaultRequestOptions(requestOptions)
                    .load(IMAGE_URL_BASE_PATH + mMovies.get(i).getPosterPath())
                    .into(((MovieViewHolder)viewHolder).mMovie_Image);

            ((MovieViewHolder)viewHolder).mMovie_Title.setText(mMovies.get(i).getTitle());
            ((MovieViewHolder)viewHolder).mGenre.setText(GenreMap.getGenreNames(mMovies.get(i).getGenreIds()));
            ((MovieViewHolder)viewHolder).mPopularity.setText(String.valueOf(Math.round(mMovies.get(i).getPopularity())));
            ((MovieViewHolder)viewHolder).mRelease_Year.setText(mMovies.get(i).getReleaseDate().split("-")[0]);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if(mMovies.get(position).getTitle().equals(LOADING)){
            return LOADING_TYPE;
        }
        else if(mMovies.get(position).getTitle().equals(EXHAUSTED)){
            return EXHAUSTED_TYPE;
        }
        else if(position == mMovies.size() - 1
                && position != 0
                && !mMovies.get(position).getTitle().equals(EXHAUSTED)){
            return LOADING_TYPE;
        }
        else{
            return MOVIE_TYPE;
        }
    }

    public void setPopularMovieQueryExhausted(){
        if(mMovies.size() > 0 && mMovies.get(0).getTitle().equals(EXHAUSTED))
            return;

        hideLoading();
        MovieItem mExhaustedMovieItem = new MovieItem();
        mExhaustedMovieItem.setTitle(EXHAUSTED);
        mMovies.add(mExhaustedMovieItem);
        notifyDataSetChanged();
    }

    private void hideLoading(){
        if(isLoading()){
            for(MovieItem movie: mMovies){
                if(movie.getTitle().equals(LOADING)){
                    mMovies.remove(movie);
                }
            }
            notifyDataSetChanged();
        }
    }

    private boolean isLoading(){
        if(mMovies != null){
            if(mMovies.size() > 0){
                if(mMovies.get(mMovies.size() - 1).getTitle().equals(LOADING)){
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public int getItemCount() {
        if(mMovies != null){
            return mMovies.size();
        }
        return 0;
    }

    public void setMovies(List<MovieItem> movies){
        mMovies = movies;
        notifyDataSetChanged();
    }

    public MovieItem getSelectedMovie(int position){
        if(mMovies != null){
            if(mMovies.size() > 0){
                return mMovies.get(position);
            }
        }
        return null;
    }

}
