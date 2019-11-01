package com.prasanth.lastmile.rest;

import com.prasanth.lastmile.rest.responses.GenreResponse;
import com.prasanth.lastmile.rest.responses.MovieDetailsResponse;
import com.prasanth.lastmile.rest.responses.MoviesResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface MoviesApiService {

    //POPULAR MOVIES
    @GET("3/movie/popular")
    Call<MoviesResponse> popularMovies(@Query("api_key") String apiKey, @Query("page") int page);

    //MOVIE DETAIL
    @GET("3/movie/{id}")
    Call<MovieDetailsResponse> movieDetails(@Path("id") int movieID, @Query("api_key") String apiKey);

    //MOVIE GENRE LIST
    @GET("3/genre/movie/list")
    Call<GenreResponse> movieGenreList(@Query("api_key") String apiKey);

}
