package com.example.dtit;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;
import androidx.fragment.app.FragmentActivity;

public class NotificationPublisher extends BroadcastReceiver {

    public static String NOTIFICATION_ID = "1";
    public static String NOTIFICATION = "TopMovie";

    public void onReceive(Context context, Intent intent) {

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        Notification notification = intent.getParcelableExtra(NOTIFICATION);
        int id = intent.getIntExtra(NOTIFICATION_ID, 0);
        notificationManager.notify(id, notification);
    }

    public void scheduleNotification(FragmentActivity activity, CharSequence title, long time) {
        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(activity, MainActivity.CHANNEL_ID)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle(title)
                        .setContentText(activity.getResources().getString(R.string.notification_text))
                        .setWhen(time)
                        .setShowWhen(true);

        Intent notificationIntent = new Intent(activity, getClass());
        notificationIntent.putExtra(NOTIFICATION_ID, 1);
        notificationIntent.putExtra(NOTIFICATION, builder.build());
        PendingIntent pendingIntent = PendingIntent.getBroadcast(activity, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        AlarmManager alarmManager = (AlarmManager) activity.getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, time, pendingIntent);
    }
}