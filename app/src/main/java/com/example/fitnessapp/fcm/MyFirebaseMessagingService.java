package com.example.fitnessapp.fcm;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import com.example.fitnessapp.R;
import com.example.fitnessapp.worker.NotificationWorker;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * A class that is used to receive Firebase Cloud Messaging (FCM) messages
 *
 * The main purpose of this class is:
 * 1) Receive FCM messages
 * 2) Show the notification immediately or schedule the notification (based on reminder date set by user)
 */
public class MyFirebaseMessagingService extends FirebaseMessagingService {

    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);
        getSharedPreferences("_", MODE_PRIVATE).edit().putString("deviceToken", s).apply();
    }

    public static String getToken(Context context) {
        return context.getSharedPreferences("_", MODE_PRIVATE).getString("deviceToken", "empty");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_name);
            String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(getString(
                    R.string.default_notification_channel_id), name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        if (remoteMessage != null && remoteMessage.getData().size() > 0) {
            Log.d("FCM", "Message Received");
            Map<String, String> notificationData = remoteMessage.getData();

            String title = notificationData.get("title");
            String message = notificationData.get("message");

            // Get reminder date in long format
            long reminderDate = Long.parseLong(notificationData.get("reminderDate"));
            scheduleNotification(title, message, reminderDate);
        }
    }

    private void scheduleNotification(String title, String message, long reminderDate) {
        long delay = calculateDelay(reminderDate);
        if (delay == 0) {
            // if current device time is greater than (passed) the reminder date
            showNotification(buildNotification(title, message));
        } else {
            Data data = new Data.Builder()
                    .putString("NOTIFICATION_TITLE", title)
                    .putString("NOTIFICATION_MESSAGE", message)
                    .putString("DATE_TO_SHOW_NOTIFICATION", String.valueOf(reminderDate))
                    .build();

            OneTimeWorkRequest notificationWork = new OneTimeWorkRequest.Builder(NotificationWorker.class)
                    .setInitialDelay(delay, TimeUnit.MILLISECONDS)
                    .setInputData(data)
                    .addTag("scheduledNotification")
                    .build();
            WorkManager.getInstance(getApplicationContext()).enqueue(notificationWork);

            showNotification(buildNotification("Scheduled Reminder",
                    "\"" + title + "\" is scheduled to be shown in " +
                            getScheduledDurationBreakdown(delay) + "from now"));
        }
    }

    private long calculateDelay(long reminderDate) {
        long currentDeviceTime = new Date().getTime();
        long delay = 0;

        if (reminderDate > currentDeviceTime) {
            // current device time is before the reminderDate
            // so calculate the difference to know when to show notification
            delay = reminderDate - currentDeviceTime;
        }
        return delay;
    }

    public String getScheduledDurationBreakdown(long millis) {
        if (millis < 0) {
            throw new IllegalArgumentException("Duration must be greater than zero!");
        }

        long days = TimeUnit.MILLISECONDS.toDays(millis);
        millis -= TimeUnit.DAYS.toMillis(days);
        long hours = TimeUnit.MILLISECONDS.toHours(millis);
        millis -= TimeUnit.HOURS.toMillis(hours);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(millis);

        StringBuilder sb = new StringBuilder(64);
        if (days > 0) {
            sb.append(days + " Days ");
        }
        if (hours > 0) {
            sb.append(hours + " Hours ");
        }
        if (minutes > 0) {
            sb.append(minutes + " Minutes ");
        }
        return (sb.toString());
    }

    private Notification buildNotification(String notificationTitle, String notificationMessage) {
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(getApplicationContext(),
                getString(R.string.default_notification_channel_id))
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .setAutoCancel(true)
                .setChannelId(getString(R.string.default_notification_channel_id))
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setContentTitle(notificationTitle)
                .setContentText(notificationMessage);

        return notificationBuilder.build();
    }

    private void showNotification(Notification notification) {
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getApplicationContext());
        notificationManager.notify(1001, notification);
    }
}

