package com.bogucki.MapsAPI;

import netscape.javascript.JSObject;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONObject;

import java.io.IOException;

public class GoogleMaps {
    public static int getDistance(String origin, String destination){
        System.out.println("Get distance from API started");
        String key = "AIzaSyBDG4CZIG5D3gpQz5WOCt5xHw60_vayWc8";

        HttpUrl url = new HttpUrl.Builder()
                .scheme("https")
                .host("maps.googleapis.com")
                .addPathSegment("maps")
                .addPathSegment("api")
                .addPathSegment("distancematrix")
                .addPathSegment("json")
                .addQueryParameter("origins", origin)
                .addQueryParameter("destinations", destination)
                .addQueryParameter("key", key)
                .build();

        Request request = new Request.Builder().url(url).build();
        try {
            Response response = new OkHttpClient().newCall(request).execute();
            String textResponse = response.body().string();
            JSONObject jsonResponse = new JSONObject(textResponse);
            System.out.println("Get distance from API finished");
            return jsonResponse
                    .getJSONArray("rows")
                    .getJSONObject(0)
                    .getJSONArray("elements")
                    .getJSONObject(0)
                    .getJSONObject("duration")
                    .getInt("value");
        } catch (IOException e) {
            e.printStackTrace();
            return -1;
        }
    }
}
