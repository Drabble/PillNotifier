package ch.drabble.pillnotification.broadcast_receiver;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.AudioAttributes;
import android.net.Uri;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import ch.drabble.pillnotification.R;
import ch.drabble.pillnotification.activities.MainActivity;

import static android.app.Notification.VISIBILITY_PUBLIC;

public class NotificationBroadcastReceiver extends BroadcastReceiver {

    public static String CHANNEL_ID = "PillNotifier";

    public static int NOTIFICATION_ID = 0;
    @Override
    public void onReceive(Context context, Intent intent) {
        // If it is the right day
        SharedPreferences SP = PreferenceManager.getDefaultSharedPreferences(context);

        boolean notify = SP.getBoolean("notifications", true);
        boolean vibrate = SP.getBoolean("notifications_vibrate_active", true);
        boolean sound = SP.getBoolean("notifications_sound_active", true);
        String ringtone = SP.getString("notifications_sound", "default ringtone");

        if(notify){
            int days_with = Integer.parseInt(SP.getString("days_with", "24"));
            int days_without = Integer.parseInt(SP.getString("days_without", "3"));
            String use_date_string = SP.getString("use_date", "2019-04-01");

            Calendar current_cal = Calendar.getInstance();
            current_cal.setTime(new Date());
            current_cal.set(Calendar.HOUR_OF_DAY, 0);
            current_cal.set(Calendar.MINUTE, 0);
            current_cal.set(Calendar.SECOND, 0);
            current_cal.set(Calendar.MILLISECOND, 0);

            DateFormat df = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
            Date use_date = new Date();
            try {
                use_date =  df.parse(use_date_string);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            Calendar use_cal = Calendar.getInstance();
            use_cal.setTime(use_date);
            use_cal.set(Calendar.HOUR_OF_DAY, 0);
            use_cal.set(Calendar.MINUTE, 0);
            use_cal.set(Calendar.SECOND, 0);
            use_cal.set(Calendar.MILLISECOND, 0);


            long difference = Math.abs(current_cal.getTime().getTime() - use_cal.getTime().getTime());
            long days_between = difference / (24 * 60 * 60 * 1000);
            boolean use_today = (days_between % (days_with + days_without)) - days_with < 0;

            if(use_today) {
                createNotificationChannel(context, vibrate, sound, ringtone);

                Intent notificationIntent = new Intent(context, MainActivity.class);

                notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                        | Intent.FLAG_ACTIVITY_SINGLE_TOP);

                PendingIntent mainActivityIntent = PendingIntent.getActivity(context, 0,
                        notificationIntent, 0);

                Intent snoozeIntent = new Intent(context, SnoozeBroadcastReceiver.class);
                snoozeIntent.putExtra("notificationId",NOTIFICATION_ID);
                PendingIntent snoozePendingIntent =
                        PendingIntent.getBroadcast(context, 0, snoozeIntent, 0);

                Intent acceptIntent = new Intent(context, AcceptBroadcastReceiver.class);
                acceptIntent.putExtra("notificationId",NOTIFICATION_ID);
                PendingIntent acceptPendingIntent = PendingIntent.getBroadcast(context, 0, acceptIntent,0);

                NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                        .setSmallIcon(R.drawable.ic_icon_notification)
                        .setContentTitle(context.getString(R.string.pill_notification_title))
                        .setContentText(context.getString(R.string.pill_notification_text))
                        .setStyle(new NotificationCompat.BigTextStyle()
                                .bigText(context.getString(R.string.pill_notification_text)))
                        .setDefaults(Notification.DEFAULT_ALL)
                        .setPriority(NotificationCompat.PRIORITY_HIGH)
                        // Set the intent that will fire when the user taps the notification
                        .setContentIntent(mainActivityIntent)
                        .setAutoCancel(true)
                        .addAction(R.drawable.ic_check_black_24dp, context.getString(R.string.accept),
                                acceptPendingIntent)
                        .addAction(R.drawable.ic_snooze_black_24dp, context.getString(R.string.snooze),
                                snoozePendingIntent)
                        .setVisibility(VISIBILITY_PUBLIC);

                if(vibrate){
                    builder.setVibrate(new long[] { 1000, 1000, 1000, 1000, 1000 });

                    //LED
                    builder.setLights(Color.RED, 3000, 3000);
                }
                //Ton
                if(sound){
                    builder.setSound(Uri.parse(ringtone));
                }

                NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);

                // notificationId is a unique int for each notification that you must define
                notificationManager.notify(NOTIFICATION_ID, builder.build());
            }
        }
    }


    private void createNotificationChannel(Context context, boolean vibrate, boolean sound, String ringtone) {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = context.getString(R.string.channel_name);
            String description = context.getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);

            if(vibrate){
                channel.setVibrationPattern(new long[] { 1000, 1000, 1000, 1000, 1000 });
            }
            //Ton
            if(sound){
                // Creating an Audio Attribute
                AudioAttributes audioAttributes = new AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .setUsage(AudioAttributes.USAGE_ALARM)
                        .build();
                channel.setSound(Uri.parse(ringtone), audioAttributes);
            }

            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}
