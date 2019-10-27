package com.prasanth.lastmile.rest;


import com.prasanth.lastmile.rest.responses.ConfigurationResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface Configurations {
    //CONFIGURATIONS
    @GET("/configuration")
    Call<ConfigurationResponse> configurations(@Query("api_key") String apiKey);
}
