package com.gamingtournament.msa.cashfree;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface API {
    @FormUrlEncoded
    @POST("app/cashfree.php")
    Call<ResponseModel> getToken(@Field("orderAmount") String orderAmount, @Field("orderId") String orderId);
}
