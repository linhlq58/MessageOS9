package com.linhleeproject.mymessage.messengeros10.utils;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Build;
import android.provider.ContactsContract;
import android.provider.Telephony;
import android.util.Log;
import android.util.TypedValue;
import android.view.TouchDelegate;
import android.view.View;

import com.linhleeproject.mymessage.messengeros10.activities.BackgroundActivity;
import com.linhleeproject.mymessage.messengeros10.models.MessageObject;

import java.io.FileNotFoundException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Linh Lee on 11/29/2016.
 */
public class Constant {
    public static final String INBOX = "content://sms/inbox";
    public static final String SENT = "content://sms/sent";
    public static final String DRAFT = "content://sms/draft";

    public static String getContactName(Context context, String phoneNumber) {
        ContentResolver cr = context.getContentResolver();
        Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI,
                Uri.encode(phoneNumber));
        Cursor cursor = cr.query(uri,
                new String[]{ContactsContract.PhoneLookup.DISPLAY_NAME}, null, null, null);
        if (cursor == null) {
            return null;
        }
        String contactName = null;
        if (cursor.moveToFirst()) {
            contactName = cursor.getString(cursor
                    .getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME));
        }
        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }
        if (contactName != null) {
            return contactName;
        } else {
            return phoneNumber;
        }
    }

    public static int getThumbnailId(Context context, String phoneNumber) {
        ContentResolver cr = context.getContentResolver();
        Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI,
                Uri.encode(phoneNumber));
        Cursor cursor = cr.query(uri,
                new String[]{ContactsContract.Contacts.PHOTO_ID}, null, null, null);
        if (cursor == null) {
            return 0;
        }
        int thumbnailId = 0;
        if (cursor.moveToFirst()) {
            thumbnailId = cursor.getInt(cursor.getColumnIndex(ContactsContract.Contacts.PHOTO_ID));
        }
        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }
        return thumbnailId;
    }

    public static Bitmap fetchThumbnail(Context context, final int thumbnailId) {
        ContentResolver cr = context.getContentResolver();
        Uri uri = ContentUris.withAppendedId(ContactsContract.Data.CONTENT_URI, thumbnailId);
        Cursor cursor = cr.query(uri, new String[] {ContactsContract.CommonDataKinds.Photo.PHOTO}, null, null, null);

        try {
            Bitmap thumbnail = null;
            if (cursor.moveToFirst()) {
                final byte[] thumbnailBytes = cursor.getBlob(0);
                if (thumbnailBytes != null) {
                    thumbnail = BitmapFactory.decodeByteArray(thumbnailBytes, 0, thumbnailBytes.length);
                }
            }
            return thumbnail;
        }
        finally {
            cursor.close();
        }

    }

    public static String getContactId(Context context, String phoneNumber) {
        ContentResolver cr = context.getContentResolver();
        Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI,
                Uri.encode(phoneNumber));
        Cursor cursor = cr.query(uri,
                new String[]{ContactsContract.PhoneLookup._ID}, null, null, null);
        if (cursor == null) {
            return null;
        }
        String contactId = null;
        if (cursor.moveToFirst()) {
            contactId = cursor.getString(cursor
                    .getColumnIndex(ContactsContract.PhoneLookup._ID));
        }
        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }
        if (contactId != null) {
            return contactId;
        } else {
            return "";
        }
    }

    public static void deleteSmsByThreadId(Context context, int threadIdToDel) {
        Cursor cursor1 = context.getContentResolver().query(Uri.parse(INBOX), new String[]{"_id", "thread_id", "address", "person", "date", "body", "read"}, null, null, null);

        if (cursor1.moveToFirst()) { // must check the result to prevent exception
            do {
                int messageId = cursor1.getInt(0);
                int threadId = cursor1.getInt(1);

                if (threadIdToDel == threadId) {
                    context.getContentResolver().delete(
                            Uri.parse("content://sms/" + messageId), null, null);
                }

            } while (cursor1.moveToNext());
        }
        cursor1.close();

        Cursor cursor2 = context.getContentResolver().query(Uri.parse(SENT), new String[]{"_id", "thread_id", "address", "person", "date", "body", "read"}, null, null, null);

        if (cursor2.moveToFirst()) { // must check the result to prevent exception
            do {
                int messageId = cursor2.getInt(0);
                int threadId = cursor2.getInt(1);

                if (threadIdToDel == threadId) {
                    context.getContentResolver().delete(
                            Uri.parse("content://sms/" + messageId), null, null);
                }

            } while (cursor2.moveToNext());
        }
        cursor2.close();
    }

    public static void deleteSmsByMesId(Context context, int mesIdToDel) {
        Cursor cursor1 = context.getContentResolver().query(Uri.parse(INBOX), new String[]{"_id", "thread_id", "address", "person", "date", "body", "read"}, null, null, null);

        if (cursor1.moveToFirst()) { // must check the result to prevent exception
            do {
                int messageId = cursor1.getInt(0);

                if (mesIdToDel == messageId) {
                    context.getContentResolver().delete(
                            Uri.parse("content://sms/" + messageId), null, null);
                }

            } while (cursor1.moveToNext());
        }
        cursor1.close();

        Cursor cursor2 = context.getContentResolver().query(Uri.parse(SENT), new String[]{"_id", "thread_id", "address", "person", "date", "body", "read"}, null, null, null);

        if (cursor2.moveToFirst()) { // must check the result to prevent exception
            do {
                int messageId = cursor2.getInt(0);

                if (mesIdToDel == messageId) {
                    context.getContentResolver().delete(
                            Uri.parse("content://sms/" + messageId), null, null);
                }

            } while (cursor2.moveToNext());
        }
        cursor2.close();
    }

    public static void markMessageRead(Context context, MessageObject message) {
        Cursor cursor = context.getContentResolver().query(Uri.parse(INBOX), null, null, null, null);
        try{

            while (cursor.moveToNext()) {
                if (cursor.getString(cursor.getColumnIndex("address")).equals(message.getAddress())) {
                    if (cursor.getString(cursor.getColumnIndex("body")).startsWith(message.getBody())) {
                        ContentValues values = new ContentValues();
                        values.put("read", true);
                        context.getContentResolver().update(Uri.parse(INBOX), values, "_id=" + message.getMessageId(), null);
                        return;
                    }
                }
            }
        }catch(Exception e)
        {
            Log.e("Mark Read", "Error in Read: " + e.toString());
        }
    }

    public static boolean checkDefaultSmsApp(Context context) {
        boolean isDefault = true;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            String defaultSmsPackageName = Telephony.Sms.getDefaultSmsPackage(context);
            if (defaultSmsPackageName.equals(context.getPackageName())) {
                isDefault = true;
            } else {
                isDefault = false;
            }
        }

        return isDefault;
    }

    public static String convertTimestamp(long milisecond) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        Timestamp timestamp = new Timestamp(milisecond);
        Date date = new Date(timestamp.getTime());

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.DATE, -1);
        String yesterday = dateFormat.format(calendar.getTime());

        if (dateFormat.format(date).equals(dateFormat.format(new Date()))) {
            return "Today";
        } else if (dateFormat.format(date).equals(yesterday)) {
            return "Yesterday";
        }

        return dateFormat.format(date);
    }

    public static void increaseHitArea(final View button) {
        final View parent = (View) button.getParent();  // button: the view you want to enlarge hit area
        parent.post(new Runnable() {
            public void run() {
                final Rect rect = new Rect();
                button.getHitRect(rect);
                rect.top -= 100;    // increase top hit area
                rect.left -= 100;   // increase left hit area
                rect.bottom += 100; // increase bottom hit area
                rect.right += 100;  // increase right hit area
                parent.setTouchDelegate(new TouchDelegate(rect, button));
            }
        });
    }

    public static String getLastSubstring(String s) {
        Pattern p = Pattern.compile(".*,\\s*(.*)");
        Matcher m = p.matcher(s);

        if (m.find()) {
            return  m.group(1);
        }

        return "";
    }

    public static String removeSpace(String s) {
        return s.replace(" ", "");
    }

    public static Bitmap decodeUri(Activity context, Uri selectedImage) throws FileNotFoundException {

        // Decode image size
        BitmapFactory.Options o = new BitmapFactory.Options();
        o.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(context.getContentResolver().openInputStream(selectedImage), null, o);

        // The new size we want to scale to
        final int REQUIRED_SIZE = 500;

        // Find the correct scale value. It should be the power of 2.
        int width_tmp = o.outWidth, height_tmp = o.outHeight;
        int scale = 1;
        while (true) {
            if (width_tmp / 2 < REQUIRED_SIZE
                    || height_tmp / 2 < REQUIRED_SIZE) {
                break;
            }
            width_tmp /= 2;
            height_tmp /= 2;
            scale *= 2;
        }

        // Decode with inSampleSize
        BitmapFactory.Options o2 = new BitmapFactory.Options();
        o2.inSampleSize = scale;
        return BitmapFactory.decodeStream(context.getContentResolver().openInputStream(selectedImage), null, o2);

    }

    public static int convertDpIntoPixels(int sizeInDp, Context context) {
        float scale = context.getResources().getDisplayMetrics().density;
        int dpAsPixels = (int) (sizeInDp * scale + 0.5f);
        return dpAsPixels;
    }

    public static void triggerRebirth(Context context) {
        Intent mStartActivity = new Intent(context, BackgroundActivity.class);
        int mPendingIntentId = 123456;
        PendingIntent mPendingIntent = PendingIntent.getActivity(context, mPendingIntentId, mStartActivity, PendingIntent.FLAG_CANCEL_CURRENT);
        AlarmManager mgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 100, mPendingIntent);
        System.exit(0);
    }

    public static int convertSpToPixels(float sp, Context context) {
        int px = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp, context.getResources().getDisplayMetrics());
        return px;
    }

    public static boolean isMyServiceRunning(Context context, Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    public static void changeLanguage(Context context, String language) {
        Locale locale = new Locale(language);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.setLocale(locale);
        context.getResources().updateConfiguration(config, context.getResources().getDisplayMetrics());
    }
}
