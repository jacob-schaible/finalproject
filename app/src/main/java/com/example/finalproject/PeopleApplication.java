package com.example.finalproject;

import android.app.Activity;
import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class PeopleApplication extends Application implements Application.ActivityLifecycleCallbacks {
    private static final String TAG = "PeopleApplication";
    private static final String CHANNEL_ID = "primary";

    private static boolean mainActivityVisible;
    private static boolean displayActivityVisible;
    private static boolean detailActivityVisible;

    public static boolean isMainActivityVisible() {
        return mainActivityVisible;
    }

    public static boolean isDisplayActivityVisible() {
        return displayActivityVisible;
    }

    public static boolean isDetailActivityVisible() {
        return detailActivityVisible;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        registerActivityLifecycleCallbacks(this);
        createNotificationChannel();
    }

    @Override
    public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, activity.getLocalClassName() + " created");
        if (activity instanceof MainActivity)
            mainActivityVisible = true;
        else if (activity instanceof DisplayPeopleActivity)
            displayActivityVisible = true;
        else if (activity instanceof UserDetailActivity)
            detailActivityVisible = true;
    }

    @Override
    public void onActivityStarted(@NonNull Activity activity) {
        Log.d(TAG, activity.getLocalClassName() + " started");
        if (activity instanceof MainActivity)
            mainActivityVisible = true;
        else if (activity instanceof DisplayPeopleActivity)
            displayActivityVisible = true;
        else if (activity instanceof UserDetailActivity)
            detailActivityVisible = true;
    }

    @Override
    public void onActivityResumed(@NonNull Activity activity) {
        Log.d(TAG, activity.getLocalClassName() + " resumed");
        if (activity instanceof MainActivity)
            mainActivityVisible = true;
        else if (activity instanceof DisplayPeopleActivity)
            displayActivityVisible = true;
        else if (activity instanceof UserDetailActivity)
            detailActivityVisible = true;
    }

    @Override
    public void onActivityPaused(@NonNull Activity activity) {
        Log.d(TAG, activity.getLocalClassName() + " paused");
        if (activity instanceof MainActivity)
            mainActivityVisible = false;
        else if (activity instanceof DisplayPeopleActivity)
            displayActivityVisible = false;
        else if (activity instanceof UserDetailActivity)
            detailActivityVisible = false;
    }

    @Override
    public void onActivityStopped(@NonNull Activity activity) {
        Log.d(TAG, activity.getLocalClassName() + " stopped");
        if (activity instanceof MainActivity)
            mainActivityVisible = false;
        else if (activity instanceof DisplayPeopleActivity)
            displayActivityVisible = false;
        else if (activity instanceof UserDetailActivity)
            detailActivityVisible = false;

        if (!(mainActivityVisible || displayActivityVisible || detailActivityVisible)) {
            createNotification();
        }
    }

    @Override
    public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle outState) {
        Log.d(TAG, activity.getLocalClassName() + " save instance state");
    }

    @Override
    public void onActivityDestroyed(@NonNull Activity activity) {
        Log.d(TAG, activity.getLocalClassName() + " destroyed");
        if (activity instanceof MainActivity)
            mainActivityVisible = false;
        else if (activity instanceof DisplayPeopleActivity)
            displayActivityVisible = false;
        else if (activity instanceof UserDetailActivity)
            detailActivityVisible = false;
    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_name);
            String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    public void createNotification() {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(getString(R.string.notification_title))
            .setContentText(getString(R.string.notification_message));
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(1, builder.build());
    }
}
