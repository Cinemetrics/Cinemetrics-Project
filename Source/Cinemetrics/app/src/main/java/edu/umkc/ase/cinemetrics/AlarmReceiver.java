package edu.umkc.ase.cinemetrics;

/**
 * Created by Esha Mayuri on 4/24/2017.
 */

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.Ringtone;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

public class AlarmReceiver extends BroadcastReceiver {
    Ringtone ringtone;
    @Override
    public void onReceive(Context context, Intent intent)
    {
        Bundle bundle = intent.getExtras();
        String movieName = bundle.getString("movie");

        String notificatioTitle = "Cinemetrics App Notification!";
        String notificatioText = "Reminder: " +movieName;

        Toast.makeText(context, movieName, Toast.LENGTH_LONG).show();
//            Uri alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
//            if (alarmUri == null) {
//                alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
//            }
//            ringtone = RingtoneManager.getRingtone(context, alarmUri);
//            ringtone.play();
//
//        Intent service1 = new Intent(context, AlarmReceiver.class);
//        context.startService(service1);

            Intent notificationIntent = new Intent(context, MovieDetailsActivity.class);
            Bundle extras = new Bundle();
            extras.putString("movieName", movieName); //movie name
            intent.putExtras(extras);

            TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
            stackBuilder.addParentStack(MovieDetailsActivity.class);
            stackBuilder.addNextIntent(notificationIntent);

            PendingIntent pendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

            NotificationCompat.Builder builder = new NotificationCompat.Builder(context);

            Notification notification = builder.setContentTitle("Cinemetrics App Notification")
                    .setContentText("New Notification From Cinemetrics App..")
                    .setTicker("New Message Alert!")
//                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setSmallIcon(R.drawable.logo3)
                    .setContentTitle(notificatioTitle)
                    .setContentText(notificatioText)
                    .setContentIntent(pendingIntent).build();

            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(0, notification);

            Vibrator vibrator  = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
            vibrator.vibrate(1000);
//        }
//        else
//        {
//            ringtone.stop();
//        }
    }
}
