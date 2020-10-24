package com.example.fitnessapp.worker;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.work.Worker;
import androidx.work.WorkerParameters;
import com.example.fitnessapp.R;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * A worker class that is used to:
 * 1) Receive scheduled notification data
 * 2) Determine if the received notification data should be shown,
 * 3) Generate the notification and show it to user (if condition is met)
 */
public class NotificationWorker extends Worker {

    public NotificationWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getApplicationContext().getString(R.string.channel_name);
            String description = getApplicationContext().getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel("1002", name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = getApplicationContext()
                    .getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    @NonNull
    @Override
    public Result doWork() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
        long scheduleNotificationDate = Long.parseLong(getInputData().getString("DATE_TO_SHOW_NOTIFICATION"));

        String dateToShowNotification = simpleDateFormat.format(new Date(scheduleNotificationDate));
        String currentDeviceTime = simpleDateFormat.format(new Date());

        // Check if schedule notification date is equal to current device time in the format "dd/MM/yyyy"
        // This prevent showing notification in the scenario where user change device time
        if (dateToShowNotification.equals(currentDeviceTime)) {
            String notificationTitle = getInputData().getString("NOTIFICATION_TITLE");
            String notificationMessage = getInputData().getString("NOTIFICATION_MESSAGE");
            buildNotification(notificationTitle, notificationMessage);
            return Result.success();
        }
        return Result.failure();
    }

    private void buildNotification(String notificationTitle, String notificationMessage) {
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(getApplicationContext(), "1002")
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .setAutoCancel(true)
                .setChannelId("1002")
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setContentTitle(notificationTitle)
                .setContentText(notificationMessage);

        showNotification(notificationBuilder.build());
    }

    private void showNotification(Notification notification) {
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getApplicationContext());
        notificationManager.notify(1002, notification);
    }
}
