package com.linhleeproject.mymessage.messengeros10.services;

import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.SmsMessage;

import com.linhleeproject.mymessage.messengeros10.MyApplication;
import com.linhleeproject.mymessage.messengeros10.database.DatabaseHelper;
import com.linhleeproject.mymessage.messengeros10.models.MessageObject;
import com.linhleeproject.mymessage.messengeros10.utils.Constant;

/**
 * Created by Linh Lee on 12/2/2016.
 */
public class SmsReceivedReceiver extends BroadcastReceiver {
    public static final String INBOX = "content://sms/inbox";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (!Constant.checkDefaultSmsApp(context)) {
            /*Bundle extras = intent.getExtras();
            SmsMessage[] smsMessages = null;
            if (extras != null) {
                Object[] pdus = (Object[]) extras.get("pdus");
                smsMessages = new SmsMessage[pdus.length];
                for (int i = 0; i < smsMessages.length; i++) {
                    String format = extras.getString("format");
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                        smsMessages[i] = SmsMessage.createFromPdu((byte[]) pdus[i], format);
                    } else {
                        smsMessages[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
                    }

                    ContentValues values = new ContentValues();
                    values.put("address", smsMessages[i].getOriginatingAddress());
                    values.put("body", smsMessages[i].getMessageBody().toString());
                    context.getContentResolver().insert(Uri.parse("content://sms/inbox"), values);
                }
            }*/

            DatabaseHelper db = MyApplication.getDb();

            String SORT_ORDER = "date DESC";
            Cursor cursor = context.getContentResolver().query(Uri.parse(INBOX), new String[]{"_id", "thread_id", "address", "person", "date", "body", "read"}, null, null, SORT_ORDER);

            if (cursor.moveToFirst()) {
                int messageId = cursor.getInt(0);
                int threadId = cursor.getInt(1);
                String address = cursor.getString(2);
                int contactId = cursor.getInt(3);
                long date = cursor.getLong(4);
                String body = cursor.getString(5);
                int read = cursor.getInt(6);
                String person = Constant.getContactName(context, address);

                MessageObject messageObject = new MessageObject(messageId, threadId, address, person, date, body, read, 1);
                if (db != null) {
                    db.addMessage(messageObject);
                }

                Intent receivedIntent = new Intent("sms_received");
                receivedIntent.putExtra("threadId", threadId);
                receivedIntent.putExtra("person", person);
                receivedIntent.putExtra("body", body);
                context.sendBroadcast(receivedIntent);
            }
        }
    }
}
