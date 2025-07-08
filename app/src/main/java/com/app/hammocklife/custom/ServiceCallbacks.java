package com.app.hammocklife.custom;

import com.app.hammocklife.model.APIinstagram;
import com.app.hammocklife.model.APIinstagram2;
import com.google.gson.JsonObject;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Query;
import retrofit2.http.Url;

public interface ServiceCallbacks {
    @Headers({"Content-Type: application/json"})
    @POST("fcm/send")
    Call<JsonObject> postToken(
            @Header("Authorization") String Authorization,
            @Body Map<String, Object> body);

    @Headers({"Accept: application/json"})
    @FormUrlEncoded
    @POST("access_token")
    Call<APIinstagram> postApi(
            @Field("client_id") String client_id,
            @Field("client_secret") String client_secret,
            @Field("grant_type") String grant_type,
            @Field("redirect_uri") String redirect_uri,
            @Field("code") String code);

    @Headers({"Accept: application/json"})
    @GET()
    Call<APIinstagram2> getInfo(@Url String url);

}
