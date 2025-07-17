package com.app.hammocklife.api;

public class APIUtils {
    public static String URL_FCM = "https://fcm.googleapis.com/";
    public static String URL_OAUTH = "https://oauth2.googleapis.com/";

    public static APIService getMessageService() {
        return RetrofitClient.getClient(URL_FCM).create(APIService.class);
    }

    public static APIService getOauthService() {
        return RetrofitClient.getClient(URL_OAUTH).create(APIService.class);
    }
}
