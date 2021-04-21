package com.example.dtit.endlessRecycler;

import android.os.Bundle;
import android.util.Log;

import com.example.dtit.ErrorDialog;
import com.example.dtit.MainActivity;
import com.example.dtit.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;

public class DataHandler {

    public static final String POSTER = "poster_path";
    public static final String TITLE = "title";
    public static final String DATE = "release_date";
    public static final String VOTE = "vote_average";
    public static final String OVERVIEW = "overview";
    public static final String ERROR_TAG = "TopMovie";

    private final ArrayList<FilmData> filmArray = new ArrayList<>();
    private final SimpleDataReceiver dataReceiver = new SimpleDataReceiver();
    private final MainActivity activity;

    private int page = 1;

    public DataHandler(MainActivity mainActivity) {
        this.activity = mainActivity;
    }

    static class FilmData {

        private String picture;
        private int percent;
        private String title;
        private String date;
        private String overview;

        public FilmData setPicture(String picture) {
            this.picture = picture;
            return this;
        }

        public FilmData setOverview(String overview) {
            this.overview = overview;
            return this;
        }

        public FilmData setPercent(int percent) {
            this.percent = percent;
            return this;
        }

        public FilmData setPercent(Double percent) {
            return setPercent(Double.valueOf(percent * 10).intValue());
        }

        public FilmData setDate(String date) {
            this.date = date;
            return this;
        }

        public FilmData setTitle(String title) {
            this.title = title;
            return this;
        }

        public int getPercent() {
            return percent;
        }

        public String getDate() {
            return date;
        }

        public String getOverview() {
            return overview;
        }

        public String getPicture() {
            return picture;
        }

        public String getTitle() {
            return title;
        }
    }

    public void findData() {
        try {
            JSONArray answer = dataReceiver.getJSON(page++);
            for (int i = 0; i < answer.length(); i++) {
                JSONObject answerItem = answer.getJSONObject(i);
                //уберём лишние данные
                FilmData saveItem = new FilmData()
                        .setPicture(answerItem.getString(POSTER))
                        .setTitle(answerItem.getString(TITLE))
                        .setDate(answerItem.getString(DATE))
                        .setPercent(answerItem.getDouble(VOTE))
                        .setOverview(answerItem.getString(OVERVIEW));
                this.filmArray.add(saveItem);
            }
        } catch (org.json.JSONException exception) {
            showJsonError(exception);//TODO вынести обработчик ошибок в новый класс
        } catch (Exception exception) {
            Log.e(ERROR_TAG, exception.getMessage() + " at: " + Arrays.toString(exception.getStackTrace()));
            Bundle bundle = new Bundle(3);
            bundle.putInt(ErrorDialog.TITLE_KEY, R.string.error_title_other);
            bundle.putString(ErrorDialog.MESSAGE_KEY, exception.getMessage() + "\n\n" + activity.getResources().getString(R.string.error_msg_other));
            bundle.putInt(ErrorDialog.BUTTON_KEY, R.string.error_btn_other);
            ErrorDialog errorDialog = new ErrorDialog();
            errorDialog.setArguments(bundle);
            errorDialog.show(activity.getFragmentManager(), ERROR_TAG);
        }
    }

    private void showJsonError(Exception exception) {
        Log.e(ERROR_TAG, exception.getMessage() + " at: " + Arrays.toString(exception.getStackTrace()));
        Bundle bundle = new Bundle(3);
        bundle.putInt(ErrorDialog.TITLE_KEY, R.string.error_title_JSON);
        bundle.putString(ErrorDialog.MESSAGE_KEY, exception.getMessage());
        bundle.putInt(ErrorDialog.BUTTON_KEY, R.string.error_btn_JSON);
        ErrorDialog errorDialog = new ErrorDialog();
        errorDialog.setArguments(bundle);
        errorDialog.show(activity.getFragmentManager(), ERROR_TAG);
    }

    public FilmData getFilmInfo(int position) {
        return this.filmArray.get(position);
    }

    public int getFilmsCount() {
        return this.filmArray.size();
    }
}
