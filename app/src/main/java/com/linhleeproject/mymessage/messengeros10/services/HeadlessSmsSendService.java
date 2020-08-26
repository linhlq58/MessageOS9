package com.linhleeproject.mymessage.messengeros10.services;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.linhleeproject.mymessage.messengeros10.R;
import com.linhleeproject.mymessage.messengeros10.activities.BackgroundActivity;
import com.linhleeproject.mymessage.messengeros10.utils.Constant;

/**
 * Created by Linh Lee on 12/7/2016.
 */
public class HeadlessSmsSendService extends Service {
    private int threadId;
    private String person;
    private String body;
    private BroadcastReceiver receiver;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Bundle extras = intent.getExtras();
                if (extras != null) {
                    if (Constant.checkDefaultSmsApp(getApplicationContext())) {
                        threadId = extras.getInt("threadId");
                        person = extras.getString("person");
                        body = extras.getString("body");
                        createNotification(person, body);
                    }
                }
            }
        };
        registerReceiver(receiver, new IntentFilter("sms_received"));
    }

    private void createNotification(String person, String body) {
        Intent notiIntent = new Intent(this, BackgroundActivity.class);
        notiIntent.putExtra("threadId", threadId);
        notiIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendInt = PendingIntent.getActivity(this, 0,
                notiIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Notification.Builder builder = new Notification.Builder(this);
        builder.setContentIntent(pendInt)
                .setSmallIcon(R.mipmap.ic_noti)
                .setOngoing(false)
                .setAutoCancel(true)
                .setContentTitle(person)
                .setContentText(body)
                .setPriority(Notification.PRIORITY_HIGH);

        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        builder.setSound(alarmSound);
        builder.setVibrate(new long[]{1000, 1000, 1000, 1000, 1000});

        Notification noti = builder.build();
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(1, noti);

        try {
            Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notification);
            r.play();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (receiver != null) {
            unregisterReceiver(receiver);
        }
    }
}
