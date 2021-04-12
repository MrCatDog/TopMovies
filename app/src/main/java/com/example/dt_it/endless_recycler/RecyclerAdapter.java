package com.example.dt_it.endless_recycler;

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

import com.example.dt_it.ErrorDialog;
import com.example.dt_it.MainActivity;
import com.example.dt_it.time_fragmetns.DatePickerFragment;
import com.example.dt_it.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Arrays;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.VH> {

    public static final String POSTER = "poster_path";
    public static final String TITLE = "title";
    public static final String DATE = "release_date";
    public static final String VOTE = "vote_average";
    public static final String OVERVIEW = "overview";
    public static final String ERROR_TAG = "TopMovie";

    private final JSONArray jsonArray = new JSONArray();
    private final SimpleDataReceiver dataReceiver = new SimpleDataReceiver();
    private final MainActivity activity;

    private int page = 1;

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
        try {
            JSONObject jsonObject = jsonArray.getJSONObject(position);
            new Thread(() -> {
                try {
                    holder.picture.setImageBitmap(dataReceiver.getImageBitmap(jsonObject.getString(POSTER)));
                } catch (Exception exception) {
                    Log.e(ERROR_TAG, exception.getMessage() + " at: " + Arrays.toString(exception.getStackTrace()));
                    holder.picture.setImageResource(R.drawable.no_image);
                }
            }
            ).start(); //мда. тут бы аналоги onPostExecute (мб BroadcastReceiver?) погуглить чтобы картинки ставить в поле только когда они догрузятся. Если успею - сделаю.
            //а в следующий раз использую Glide, обещаю
            //только ленту слишком не крутите)))

            holder.title.setText(jsonObject.getString(TITLE));
            holder.date.setText(jsonObject.getString(DATE));
            Float rating = Float.parseFloat(jsonObject.getString(VOTE)) * 10;
            holder.percent.setText(rating.intValue() + "%");
            holder.overview.setText(jsonObject.getString(OVERVIEW));

            holder.schedule.setOnClickListener((View v) ->
                    new DatePickerFragment(activity.getSupportFragmentManager(), holder.title.getText()).show()
            );

        } catch (org.json.JSONException exception) {
            showJsonError(exception);
        }
    }

    @Override
    public int getItemCount() {
        return jsonArray.length();
    }

    public void receiveData(RecyclerView recyclerView) {
        try {
            JSONArray answer = dataReceiver.getJSON(page++);
            for (int i = 0; i < answer.length(); i++) {
                JSONObject answerItem = answer.getJSONObject(i);
                //уберём лишние данные
                JSONObject saveItem = new JSONObject()
                        .put(POSTER, answerItem.getString(POSTER))
                        .put(TITLE, answerItem.getString(TITLE))
                        .put(DATE, answerItem.getString(DATE))
                        .put(VOTE, answerItem.getString(VOTE))
                        .put(OVERVIEW, answerItem.getString(OVERVIEW));
                this.jsonArray.put(saveItem);
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