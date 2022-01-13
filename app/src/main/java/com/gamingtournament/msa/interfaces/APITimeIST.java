package com.gamingtournament.msa.interfaces;

import com.gamingtournament.msa.model.TimeISTModel;

import retrofit2.Call;
import retrofit2.http.GET;

public interface APITimeIST {
    @GET("app/time.php")
    Call<TimeISTModel> getTime();

}
