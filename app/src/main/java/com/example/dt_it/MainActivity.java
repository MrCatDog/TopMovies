package com.example.dt_it;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dt_it.endless_recycler.EndlessRecyclerViewScrollListener;
import com.example.dt_it.endless_recycler.RecyclerAdapter;

public class MainActivity extends AppCompatActivity {

    public static final String CHANNEL_ID = "TopMovies";

    private RecyclerView filmList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        createNotificationChannel();

        filmList = findViewById(R.id.list_of_films);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        filmList.setLayoutManager(linearLayoutManager);

        RecyclerAdapter RA = new RecyclerAdapter(this);
        filmList.setAdapter(RA);
        RA.receiveData(filmList);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(filmList.getContext(), linearLayoutManager.getOrientation());
        dividerItemDecoration.setDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.divider_item_shape, null));

        filmList.addItemDecoration(dividerItemDecoration);

        filmList.addOnScrollListener(new EndlessRecyclerViewScrollListener(linearLayoutManager, RA));
    }

    private void createNotificationChannel() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_name);
            String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

}
