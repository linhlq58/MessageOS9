package com.linhleeproject.mymessage.messengeros10.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.linhleeproject.mymessage.messengeros10.utils.Constant;

/**
 * Created by Linh Lee on 12/9/2016.
 */
public class BootCompletedReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
            if (!Constant.isMyServiceRunning(context, HeadlessSmsSendService.class)) {
                Intent startServiceIntent = new Intent(context, HeadlessSmsSendService.class);
                context.startService(startServiceIntent);
            }
        }
    }
}
