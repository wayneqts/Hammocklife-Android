package com.app.hammocklife.api;

import com.google.gson.JsonObject;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Url;

public interface APIService {
    @Headers("Content-Type: application/json")
    @POST()
    Call<JsonObject> sendPush(@Url String url, @Header("Authorization") String token, @Body Map<String, Object> data);
    @FormUrlEncoded
    @POST("token")
    Call<JsonObject> getAccessToken(
            @Field("grant_type") String grantType,
            @Field("assertion") String assertion
    );
}
