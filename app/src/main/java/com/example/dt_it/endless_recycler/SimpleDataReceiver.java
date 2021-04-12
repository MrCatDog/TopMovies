package com.example.dt_it.endless_recycler;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class SimpleDataReceiver {

    public static final String URL_BASE = "https://api.themoviedb.org/3/discover/movie?api_key=";
    public static final String API_KEY = "69b692b77e12ed10980e6d0ffc4fbc9e";
    public static final String FILTERS = "&primary_release_year=2019&sort_by=popularity.desc";
    public static final String PAGE_FILTER = "&page=";
    public static final String URL_PIC = "https://image.tmdb.org/t/p/";
    public static final String PIC_SIZE = "w500";
    public static final String JSON_TAG_FOR_RESULTS = "results";

    private final OkHttpClient client = new OkHttpClient();
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    public Callable<byte[]> receiveData(String url) {
        return () -> {
            //ip: 54.230.183.78 можно было бы попробовать обратиться напрямую, но, скорее всего, блок там по IP, так что только VPN
            //additional info: https://www.themoviedb.org/talk/6036ee9d4fd141003eae8534
            Request request = new Request.Builder().url(url).build();
            Response response = this.client.newCall(request).execute();
            byte[] answer;
            if (response.isSuccessful()) {
                answer = response.body().bytes();
            } else {
                answer = null;
            }
            response.close();
            return answer;
        };
    }

    public Bitmap getImageBitmap(String path) throws ExecutionException, InterruptedException {
        FutureTask<byte[]> future = new FutureTask<>(receiveData(URL_PIC + PIC_SIZE + path));
        executor.submit(future);
        return BitmapFactory.decodeStream(new ByteArrayInputStream(future.get()));
    }

    public JSONArray getJSON(int page) throws ExecutionException, InterruptedException, JSONException {
        FutureTask<byte[]> future = new FutureTask<>(receiveData(URL_BASE + API_KEY + FILTERS + PAGE_FILTER + page));
        executor.submit(future);
        JSONObject ans = new JSONObject(new String(future.get()));
        return ans.getJSONArray(JSON_TAG_FOR_RESULTS);
    }
}
