package com.linhleeproject.mymessage.messengeros10.activities;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.Telephony;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.linhleeproject.mymessage.messengeros10.utils.Constant;
import com.google.gson.Gson;
import com.linhleeproject.mymessage.messengeros10.MyApplication;
import com.linhleeproject.mymessage.messengeros10.R;
import com.linhleeproject.mymessage.messengeros10.database.DatabaseHelper;
import com.linhleeproject.mymessage.messengeros10.models.ContactObject;
import com.linhleeproject.mymessage.messengeros10.models.MessageObject;
import com.linhleeproject.mymessage.messengeros10.models.ThemeObject;
import com.linhleeproject.mymessage.messengeros10.services.HeadlessSmsSendService;
import com.linhleeproject.mymessage.messengeros10.utils.BlurringView;

import java.util.ArrayList;

/**
 * Created by Linh Lee on 12/5/2016.
 */
public class BackgroundActivity extends AppCompatActivity {
    public static final String INBOX = "content://sms/inbox";
    public static final String SENT = "content://sms/sent";
    public static final String DRAFT = "content://sms/draft";

    private DatabaseHelper db;
    private ArrayList<MessageObject> listRawMessage;
    private ArrayList<ContactObject> listContact;
    private RelativeLayout titleLayout;
    private ImageView backgroundImage;
    private ImageView alphaView;
    private BlurringView blurringView;
    private String imageBase64;
    private Bitmap mySelectedImage;
    private int blurScore;
    private String themeJson;
    private SharedPreferences sharedPreferences;
    private Gson gson;

    private BroadcastReceiver receiver, alphaReceiver, blurReceiver, changeBgReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_background);

        sharedPreferences = MyApplication.getSharedPreferences();
        gson = new Gson();
        blurScore = sharedPreferences.getInt("blur", 50);
        themeJson = sharedPreferences.getString("current_theme", "");

        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (themeJson.equals("")) {
                window.setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));
            } else {
                ThemeObject theme = gson.fromJson(themeJson, ThemeObject.class);
                Log.d("theme", theme.getStatusBarColor());
                window.setStatusBarColor(Color.parseColor("#" + theme.getStatusBarColor()));
            }
        }

        titleLayout = (RelativeLayout) findViewById(R.id.title_layout);
        backgroundImage = (ImageView) findViewById(R.id.background_img);
        alphaView = (ImageView) findViewById(R.id.alpha_view);
        blurringView = (BlurringView) findViewById(R.id.blurring_view);
        alphaView.setAlpha((float) sharedPreferences.getInt("alpha", 50) / 100);

        if (themeJson.equals("")) {
            titleLayout.setBackgroundResource(R.color.colorPrimary);
        } else {
            ThemeObject theme = gson.fromJson(themeJson, ThemeObject.class);
            titleLayout.setBackgroundColor(Color.parseColor("#" + theme.getKeyshadowColor()));
        }

        blurringView.setBlurredView(backgroundImage);
        if ((blurScore == 0) || (blurScore / 4 == 0)) {
            blurringView.setBlurRadius(1);
            blurringView.setDownsampleFactor(1);
            blurringView.invalidate();
        } else {
            blurringView.setBlurRadius(blurScore / 4);
            if (blurScore / 10 == 0) {
                blurringView.setDownsampleFactor(1);
            } else {
                blurringView.setDownsampleFactor(blurScore / 10);
            }
            blurringView.invalidate();
        }

        setBackgroundImage();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                    || checkSelfPermission(Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED
                    || checkSelfPermission(Manifest.permission.RECEIVE_SMS) != PackageManager.PERMISSION_GRANTED
                    || checkSelfPermission(Manifest.permission.RECEIVE_MMS) != PackageManager.PERMISSION_GRANTED
                    || checkSelfPermission(Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED
                    || checkSelfPermission(Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED
                    || checkSelfPermission(Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(BackgroundActivity.this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.READ_SMS,
                                Manifest.permission.RECEIVE_SMS, Manifest.permission.RECEIVE_MMS, Manifest.permission.SEND_SMS,
                                Manifest.permission.READ_CONTACTS, Manifest.permission.READ_PHONE_STATE, Manifest.permission.BROADCAST_SMS,
                                Manifest.permission.BROADCAST_WAP_PUSH, Manifest.permission.SEND_RESPOND_VIA_MESSAGE},
                        1);
            } else {
                initData();
                if (!Constant.isMyServiceRunning(getApplicationContext(), HeadlessSmsSendService.class)) {
                    startService(new Intent(BackgroundActivity.this, HeadlessSmsSendService.class));
                }

                onNewIntent(getIntent());
            }
        } else {
            initData();
            if (!Constant.isMyServiceRunning(getApplicationContext(), HeadlessSmsSendService.class)) {
                startService(new Intent(BackgroundActivity.this, HeadlessSmsSendService.class));
            }

            onNewIntent(getIntent());
        }

        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                finish();
            }
        };
        registerReceiver(receiver, new IntentFilter("close_app"));

        alphaReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Bundle extras = intent.getExtras();
                if (extras != null) {
                    float alpha = extras.getInt("alpha");
                    Log.d("alpha score", alpha + "");
                    alphaView.setAlpha(alpha / 100);
                }
            }
        };
        registerReceiver(alphaReceiver, new IntentFilter("alpha_change"));

        blurReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Bundle extras = intent.getExtras();
                if (extras != null) {
                    int blur = extras.getInt("blur");
                    if ((blur == 0) || (blur / 4 == 0)) {
                        blurringView.setBlurRadius(1);
                        blurringView.setDownsampleFactor(1);
                        blurringView.invalidate();
                    } else {
                        blurringView.setBlurRadius(blur / 4);
                        if (blur / 10 == 0) {
                            blurringView.setDownsampleFactor(1);
                        } else {
                            blurringView.setDownsampleFactor(blur / 10);
                        }
                        blurringView.invalidate();
                    }

                }
            }
        };
        registerReceiver(blurReceiver, new IntentFilter("blur_change"));

        changeBgReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                setBackgroundImage();
            }
        };
        registerReceiver(changeBgReceiver, new IntentFilter("change_bg"));
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Bundle extras = intent.getExtras();
        if (extras != null) {
            int threadId = extras.getInt("threadId");
            Intent mesIntent = new Intent(BackgroundActivity.this, MessageActivity.class);
            mesIntent.putExtra("threadId", threadId);
            startActivity(mesIntent);

            MessageObject newInbox = db.getFirstMessageByThreadId(threadId);
            newInbox.setRead(1);
            Constant.markMessageRead(BackgroundActivity.this, newInbox);
            db.updateMessage(newInbox);
        } else {
            Intent mainIntent = new Intent(BackgroundActivity.this, MainActivity.class);
            startActivity(mainIntent);

            if (!Constant.checkDefaultSmsApp(this)) {
                Intent sendIntent = new Intent(Telephony.Sms.Intents.ACTION_CHANGE_DEFAULT);
                sendIntent.putExtra(Telephony.Sms.Intents.EXTRA_PACKAGE_NAME, getPackageName());
                startActivity(sendIntent);
            }
        }
    }

    private void initData() {
        db = new DatabaseHelper(this);
        MyApplication.setDb(db);

        db.deleteAllMessage();
        db.deleteAllContact();

        listRawMessage = new ArrayList<>();
        listContact = new ArrayList<>();
        getAllContact();
        getInboxMessage();
        getSentMessage();
        db.addAllMessage(listRawMessage);
        db.addAllContact(listContact);
    }

    private void setBackgroundImage() {
        imageBase64 = sharedPreferences.getString("image_base64", "");
        if (imageBase64.equals("")) {
            if (themeJson.equals("")) {
                backgroundImage.setImageResource(android.R.color.white);
            } else {
                ThemeObject theme = gson.fromJson(themeJson, ThemeObject.class);
                backgroundImage.setImageResource(theme.getBgRes());
            }
        } else {
            byte[] imageAsBytes = Base64.decode(imageBase64, Base64.DEFAULT);
            mySelectedImage = BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.length);

            /*Matrix matrix = new Matrix();
            matrix.postRotate(90);
            Bitmap rotatedBitmap = Bitmap.createBitmap(mySelectedImage, 0, 0, mySelectedImage.getWidth(), mySelectedImage.getHeight(), matrix, true);*/
            backgroundImage.setImageBitmap(mySelectedImage);
        }
    }

    private void getInboxMessage() {
        String SORT_ORDER = "date DESC";
        Cursor cursor = getContentResolver().query(Uri.parse(INBOX), new String[]{"_id", "thread_id", "address", "person", "date", "body", "read"}, null, null, SORT_ORDER);

        if (cursor.moveToFirst()) { // must check the result to prevent exception
            do {
                int messageId = cursor.getInt(0);
                int threadId = cursor.getInt(1);
                String address = cursor.getString(2);
                int contactId = cursor.getInt(3);
                long date = cursor.getLong(4);
                String body = cursor.getString(5);
                int read = cursor.getInt(6);

                MessageObject messageObject = new MessageObject(messageId, threadId, address, address, date, body, read, 1);
                listRawMessage.add(messageObject);

            } while (cursor.moveToNext());
        }

        cursor.close();
    }

    private void getSentMessage() {
        String SORT_ORDER = "date DESC";
        Cursor cursor = getContentResolver().query(Uri.parse(SENT), new String[]{"_id", "thread_id", "address", "person", "date", "body", "read"}, null, null, SORT_ORDER);

        if (cursor.moveToFirst()) { // must check the result to prevent exception
            do {
                int messageId = cursor.getInt(0);
                int threadId = cursor.getInt(1);
                String address = cursor.getString(2);
                int contactId = cursor.getInt(3);
                long date = cursor.getLong(4);
                String body = cursor.getString(5);
                int read = cursor.getInt(6);

                MessageObject messageObject = new MessageObject(messageId, threadId, address, address, date, body, read, 2);
                listRawMessage.add(messageObject);

            } while (cursor.moveToNext());
        }

        cursor.close();
    }

    private void getAllContact() {
        Cursor cursor = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                String name = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                String phoneNumber = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));

                ContactObject contact = new ContactObject(name, phoneNumber);
                listContact.add(contact);
            } while (cursor.moveToNext());
        }

        cursor.close();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1: {

                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED
                        && grantResults[1] == PackageManager.PERMISSION_GRANTED
                        && grantResults[2] == PackageManager.PERMISSION_GRANTED
                        && grantResults[3] == PackageManager.PERMISSION_GRANTED
                        && grantResults[4] == PackageManager.PERMISSION_GRANTED
                        && grantResults[5] == PackageManager.PERMISSION_GRANTED
                        && grantResults[6] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                    Intent i = getBaseContext().getPackageManager()
                            .getLaunchIntentForPackage( getBaseContext().getPackageName() );
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(i);
                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(BackgroundActivity.this, "Permission denied!", Toast.LENGTH_SHORT).show();
                    finish();
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (receiver != null) {
            unregisterReceiver(receiver);
        }
        if (alphaReceiver != null) {
            unregisterReceiver(alphaReceiver);
        }
        if (blurReceiver != null) {
            unregisterReceiver(blurReceiver);
        }
        if (changeBgReceiver != null) {
            unregisterReceiver(changeBgReceiver);
        }
        if (mySelectedImage != null) {
            mySelectedImage.recycle();
        }

        if (db != null) {
            db.close();
        }
    }
}
