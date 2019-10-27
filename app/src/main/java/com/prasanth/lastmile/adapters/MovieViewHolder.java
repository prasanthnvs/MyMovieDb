package com.prasanth.lastmile.adapters;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.RecyclerView;

import com.prasanth.lastmile.R;


public class MovieViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    TextView mMovie_Title, mGenre, mPopularity, mRelease_Year ;
    AppCompatImageView mMovie_Image;
    OnMovieListener mOnMovieListener;

    public MovieViewHolder(@NonNull View itemView, OnMovieListener onMovieListener) {
        super(itemView);

        this.mOnMovieListener = onMovieListener;

        mMovie_Title = itemView.findViewById(R.id.movie_title);
        mGenre = itemView.findViewById(R.id.movie_genre);
        mPopularity = itemView.findViewById(R.id.movie_popularity);
        mMovie_Image = itemView.findViewById(R.id.movie_image);
        mRelease_Year = itemView.findViewById(R.id.movie_release_year);

        itemView.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        mOnMovieListener.onMovieClick(getAdapterPosition());
    }
}





