package ch.drabble.pillnotification.utils;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import java.util.Calendar;
import ch.drabble.pillnotification.broadcast_receiver.AlarmBroadcastReceiver;
import ch.drabble.pillnotification.broadcast_receiver.NotificationBroadcastReceiver;

public class Alarm {
    public static void set(Context context) {
        // Get hour and minute
        SharedPreferences SP = PreferenceManager.getDefaultSharedPreferences(context);

        String use_time_string = SP.getString("use_time", "11:00");
        String[] hour_minute = use_time_string.split(":");
        assert (hour_minute.length == 2);
        int hour = Integer.parseInt(hour_minute[0]);
        int minute = Integer.parseInt(hour_minute[1]);
        System.out.println(hour + ":" + minute);

        AlarmManager alarmMgr;
        PendingIntent alarmIntent;

        alarmMgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent notificationIntent = new Intent(context, NotificationBroadcastReceiver.class);
        alarmIntent = PendingIntent.getBroadcast(context, 0, notificationIntent, 0);

        // Set the alarm to start at
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);

        // Cancel previous alarm
        alarmMgr.cancel(alarmIntent);

        // With setInexactRepeating(), you have to use one of the AlarmManager interval
        // constants--in this case, AlarmManager.INTERVAL_DAY.
        alarmMgr.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                AlarmManager.INTERVAL_DAY, alarmIntent);
    }

    public static void snooze(Context context) {
        AlarmManager alarmMgr;
        PendingIntent alarmIntent;

        alarmMgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        Intent intent = new Intent(context, AlarmBroadcastReceiver.class);
        alarmIntent = PendingIntent.getBroadcast(context, 1, intent, 0);

        // Wake up the device to fire a one-time (non-repeating) alarm in one minute:
        alarmMgr.set(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                SystemClock.elapsedRealtime() +
                        60 * 60 * 1000, alarmIntent);
    }
}
