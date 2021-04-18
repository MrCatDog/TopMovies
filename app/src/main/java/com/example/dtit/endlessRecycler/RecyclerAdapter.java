package com.example.dtit.endlessRecycler;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dtit.ErrorDialog;
import com.example.dtit.MainActivity;
import com.example.dtit.timeFragmetns.DatePickerFragment;
import com.example.dtit.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.VH> {

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

    private static class FilmData {

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

    static class VH extends RecyclerView.ViewHolder {

        private final ImageView picture;
        private final TextView percent;
        private final TextView title;
        private final TextView date;
        private final TextView overview;
        private final Button schedule;

        public VH(View itemView) {
            super(itemView);

            picture = itemView.findViewById(R.id.picture);
            percent = itemView.findViewById(R.id.percent);
            title = itemView.findViewById(R.id.title);
            date = itemView.findViewById(R.id.release_date);
            overview = itemView.findViewById(R.id.overview);
            schedule = itemView.findViewById(R.id.schedule_btn);
        }
    }

    public RecyclerAdapter(MainActivity activity) {
        this.activity = activity;
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new VH(LayoutInflater.from(parent.getContext()).inflate(R.layout.film_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        FilmData filmData = filmArray.get(position);
        new Thread(() -> {
            try {
                holder.picture.setImageBitmap(dataReceiver.getImageBitmap(filmData.getPicture()));
            } catch (Exception exception) {
                Log.e(ERROR_TAG, exception.getMessage() + " at: " + Arrays.toString(exception.getStackTrace()));
                holder.picture.setImageResource(R.drawable.no_image);
            }
        }
        ).start(); //мда. тут бы аналоги onPostExecute (мб BroadcastReceiver?) погуглить чтобы картинки ставить в поле только когда они догрузятся. Если успею - сделаю.
        //а в следующий раз использую Glide, обещаю
        //только ленту слишком не крутите)))

        holder.title.setText(filmData.getTitle());
        holder.date.setText(filmData.getDate());
        holder.percent.setText(filmData.getPercent() + "%");
        holder.overview.setText(filmData.getOverview());

        holder.schedule.setOnClickListener((View v) ->
                new DatePickerFragment(activity.getSupportFragmentManager(), holder.title.getText()).show()
        );
    }

    @Override
    public int getItemCount() {
        return filmArray.size();
    }

    public void receiveData(RecyclerView recyclerView) {
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
            showJsonError(exception);
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
        recyclerView.post(this::notifyDataSetChanged);
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
}