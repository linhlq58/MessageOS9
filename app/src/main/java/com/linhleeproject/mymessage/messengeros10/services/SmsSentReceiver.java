package com.linhleeproject.mymessage.messengeros10.services;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.telephony.SmsManager;
import android.widget.Toast;

import com.linhleeproject.mymessage.messengeros10.MyApplication;
import com.linhleeproject.mymessage.messengeros10.database.DatabaseHelper;
import com.linhleeproject.mymessage.messengeros10.models.MessageObject;
import com.linhleeproject.mymessage.messengeros10.utils.Constant;

/**
 * Created by Linh Lee on 12/2/2016.
 */
public class SmsSentReceiver extends BroadcastReceiver {
    public static final String SENT = "content://sms/sent";

    @Override
    public void onReceive(Context context, Intent intent) {
        switch (getResultCode()) {
            case Activity.RESULT_OK:
                getRecentSentMessage(context);
                break;
            case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                Toast.makeText(context, "Generic failure",
                        Toast.LENGTH_SHORT).show();
                break;
            case SmsManager.RESULT_ERROR_NO_SERVICE:
                Toast.makeText(context, "No service",
                        Toast.LENGTH_SHORT).show();
                break;
            case SmsManager.RESULT_ERROR_NULL_PDU:
                Toast.makeText(context, "Null PDU",
                        Toast.LENGTH_SHORT).show();
                break;
            case SmsManager.RESULT_ERROR_RADIO_OFF:
                Toast.makeText(context, "Radio off",
                        Toast.LENGTH_SHORT).show();
                break;
        }
    }

    private void getRecentSentMessage(Context context) {
        DatabaseHelper db = MyApplication.getDb();

        String SORT_ORDER = "date DESC";
        Cursor cursor = context.getContentResolver().query(Uri.parse(SENT), new String[]{"_id", "thread_id", "address", "person", "date", "body", "read"}, null, null, SORT_ORDER);

        if (cursor.moveToFirst()) {
            int messageId = cursor.getInt(0);
            int threadId = cursor.getInt(1);
            String address = cursor.getString(2);
            int contactId = cursor.getInt(3);
            long date = cursor.getLong(4);
            String body = cursor.getString(5);
            int read = cursor.getInt(6);

            MessageObject messageObject = new MessageObject(messageId, threadId, address, Constant.getContactName(context, address), date, body, read, 2);
            db.addMessage(messageObject);

            Intent i = new Intent("update_conversation");
            i.putExtra("threadId", threadId);
            context.sendBroadcast(i);
        }
    }
}
