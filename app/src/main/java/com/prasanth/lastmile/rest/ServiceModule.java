package com.prasanth.lastmile.rest;

import com.prasanth.lastmile.utils.MovieDbConfig;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ServiceModule {

    private static <T> T builder(Class<T> endpoint) {
        return new Retrofit.Builder()
                .baseUrl(MovieDbConfig.API_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(endpoint);
    }

    private static MoviesApiService moviesApi = builder(MoviesApiService.class);

    public static MoviesApiService getMoviesApi(){
        return moviesApi;
    }

    public static Configurations configurations() {
        return builder(Configurations.class);
    }
}
