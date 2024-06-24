package com.example.movieapp;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.IBinder;
import android.text.format.DateUtils;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;

import com.example.movieapp.Activities.IntroActivity;

import java.util.Calendar;

public class MyForegroundService extends Service {

    private static final String CHANNEL_ID = "MyForegroundServiceChannel";
    private static final int NOTIFICATION_ID = 12345;
    private static final int UPDATE_REQUEST_CODE = 101;
    private static final String PREF_NAME = "MyForegroundServicePrefs";
    private static final String IS_SCHEDULED_KEY = "isScheduled";
    private static final String LAST_SCHEDULED_TIME_KEY = "lastScheduledTime";

    private boolean isScheduled = false;

    @Override
    public void onCreate() {
        super.onCreate();
        startForeground(NOTIFICATION_ID, createTemporaryNotification());
        stopForeground(true); // Dừng thông báo foreground
        showNotification(); // Hiển thị thông báo bình thường
        scheduleUpdate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        SharedPreferences sharedPreferences = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        isScheduled = sharedPreferences.getBoolean(IS_SCHEDULED_KEY, false);
        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void scheduleUpdate() {
        SharedPreferences sharedPreferences = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        long lastScheduledTime = sharedPreferences.getLong(LAST_SCHEDULED_TIME_KEY, 0);
        boolean isNewDay = !DateUtils.isToday(lastScheduledTime);
        if (isNewDay) {
            isScheduled = false;
            updateIsScheduled(false);
        }
        if (!isScheduled) {
            Calendar currentTime = Calendar.getInstance();
            int currentHour = currentTime.get(Calendar.HOUR_OF_DAY);
            int currentMinute = currentTime.get(Calendar.MINUTE);
            boolean isWithinTimeRange = currentHour == 18 && currentMinute >= 0 || (currentHour > 18 && currentHour < 24);

            if (isWithinTimeRange) {
                AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                if (alarmManager != null) {
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTimeInMillis(System.currentTimeMillis());
                    calendar.set(Calendar.HOUR_OF_DAY, currentHour);
                    calendar.set(Calendar.MINUTE, currentMinute);
                    calendar.set(Calendar.SECOND, 0);

                    Intent updateIntent = new Intent(this, AlarmReceiver.class);
                    PendingIntent updatePendingIntent = PendingIntent.getBroadcast(this, UPDATE_REQUEST_CODE, updateIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

                    alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, updatePendingIntent);
                    isScheduled = true;
                    updateIsScheduled(true);
                }
            }
        }
    }

    public static boolean isServiceRunning(Context context) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (MyForegroundService.class.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    private Notification createTemporaryNotification() {
        createNotificationChannel();

        return new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("MovieApp")
                .setContentText("Starting service...")
                .setSmallIcon(R.mipmap.ic_launcher)
                .build();
    }

    @SuppressLint("NotificationPermission")
    private void showNotification() {
        createNotificationChannel();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                // Yêu cầu quyền nếu chưa được cấp
                Intent permissionIntent = new Intent(this, IntroActivity.class);
                permissionIntent.putExtra("requestPermission", true);
                permissionIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(permissionIntent);
                return;
            }
        }

        Intent notificationIntent = new Intent(this, IntroActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE);

        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("MovieApp")
                .setContentText("Những bộ phim đặc sắc nhất đang chờ đón bạn!\nHãy vào ứng dụng và thưởng thức ngay nhé!")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)  // Cho phép người dùng xóa thông báo
                .build();

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificationManager != null) {
            notificationManager.notify(NOTIFICATION_ID, notification);
        }
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "MyForegroundServiceChannel";
            String description = "Channel for MyForegroundService";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }
    }

    private void updateIsScheduled(boolean newValue) {
        SharedPreferences sharedPreferences = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(IS_SCHEDULED_KEY, newValue);
        editor.putLong(LAST_SCHEDULED_TIME_KEY, System.currentTimeMillis());
        editor.apply();
    }
}
