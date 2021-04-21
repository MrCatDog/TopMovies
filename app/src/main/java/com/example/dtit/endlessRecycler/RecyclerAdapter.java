package com.example.dtit.endlessRecycler;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dtit.MainActivity;
import com.example.dtit.timeFragmetns.DatePickerFragment;
import com.example.dtit.R;

import java.util.Arrays;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.VH> {

    public static final String ERROR_TAG = "TopMovie";

    private final SimpleDataReceiver dataReceiver = new SimpleDataReceiver();
    private final MainActivity activity;
    private final DataHandler dataHandler;


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
        dataHandler = new DataHandler(activity);
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new VH(LayoutInflater.from(parent.getContext()).inflate(R.layout.film_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        DataHandler.FilmData filmData = dataHandler.getFilmInfo(position);
        new Thread(() -> {
            try {
                holder.picture.setImageBitmap(dataReceiver.getImageBitmap(filmData.getPicture()));
            } catch (Exception exception) {
                Log.e(ERROR_TAG, exception.getMessage() + " at: " + Arrays.toString(exception.getStackTrace()));
                holder.picture.setImageResource(R.drawable.no_image);
            }
        }
        ).start(); //мда. тут бы аналоги onPostExecute (мб BroadcastReceiver?) погуглить чтобы картинки ставить в поле только когда они догрузятся. Если успею - сделаю.
        //TODO а в следующий раз использую Glide, обещаю. И вынесу эту логику в другое место.
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
        return dataHandler.getFilmsCount();
    }

    public void receiveData(RecyclerView recyclerView) {
        new Thread(() -> { //TODO какой-то ад с потоками
            dataHandler.findData();
            recyclerView.post(this::notifyDataSetChanged);
        }).start();
    }
}