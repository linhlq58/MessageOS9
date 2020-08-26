package com.linhleeproject.mymessage.messengeros10.activities;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.telephony.SmsManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.github.siyamed.shapeimageview.CircularImageView;
import com.google.gson.Gson;
import com.linhleeproject.mymessage.messengeros10.MyApplication;
import com.linhleeproject.mymessage.messengeros10.R;
import com.linhleeproject.mymessage.messengeros10.adapters.ListMessageAdapter;
import com.linhleeproject.mymessage.messengeros10.database.DatabaseHelper;
import com.linhleeproject.mymessage.messengeros10.dialogs.DetailDialog;
import com.linhleeproject.mymessage.messengeros10.models.MessageObject;
import com.linhleeproject.mymessage.messengeros10.models.ThemeObject;
import com.linhleeproject.mymessage.messengeros10.services.SmsSentReceiver;
import com.linhleeproject.mymessage.messengeros10.utils.Constant;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Linh Lee on 11/30/2016.
 */
public class MessageActivity extends AppCompatActivity implements View.OnClickListener {
    private static final int ID_UP = 1;
    private static final int ID_DOWN = 2;
    private static final int ID_SEARCH = 3;
    private static final int ID_INFO = 4;
    private static final int ID_ERASE = 5;
    private static final int ID_OK = 6;
    private String SENT = "SMS_SENT";
    private String DELIVERED = "SMS_DELIVERED";
    private DatabaseHelper db;
    private int threadId;
    private String address;
    private RelativeLayout titleLayout;
    private TextView backButton;
    private ImageView infoButton;
    private ImageView avatar;
    private CircularImageView circleAvatar;
    private TextView firstLetter;
    private TextView person;
    private ImageView addButton;
    private TextView sendButton;
    private EditText messageInput;
    private RecyclerView listMessageView;
    private ListMessageAdapter adapter;
    private ArrayList<MessageObject> listMessage;
    private int firstItemOfMain = 0;
    private String themeJson;
    private String personText;
    private SharedPreferences sharedPreferences;
    private Gson gson;

    private BroadcastReceiver sentReceiver, receivedReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        db = MyApplication.getDb();
        sharedPreferences = MyApplication.getSharedPreferences();
        gson = new Gson();
        themeJson = sharedPreferences.getString("current_theme", "");

        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (themeJson.equals("")) {
                window.setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));
            } else {
                ThemeObject theme = gson.fromJson(themeJson, ThemeObject.class);
                window.setStatusBarColor(Color.parseColor("#" + theme.getStatusBarColor()));
            }
        }

        titleLayout = (RelativeLayout) findViewById(R.id.title_layout);
        backButton = (TextView) findViewById(R.id.back_btn);
        infoButton = (ImageView) findViewById(R.id.info_btn);
        avatar = (ImageView) findViewById(R.id.avatar);
        circleAvatar = (CircularImageView) findViewById(R.id.circle_avatar);
        firstLetter = (TextView) findViewById(R.id.first_letter);
        person = (TextView) findViewById(R.id.person);
        addButton = (ImageView) findViewById(R.id.add_btn);
        sendButton = (TextView) findViewById(R.id.send_btn);
        messageInput = (EditText) findViewById(R.id.message_input);
        listMessageView = (RecyclerView) findViewById(R.id.list_message);

        if (themeJson.equals("")) {
            titleLayout.setBackgroundResource(R.color.colorPrimary);
        } else {
            ThemeObject theme = gson.fromJson(themeJson, ThemeObject.class);
            titleLayout.setBackgroundColor(Color.parseColor("#" + theme.getKeyshadowColor()));
            /*switch (theme.getId()) {
                case 1:
                    titleLayout.setBackgroundResource(R.color.colorWinterKeyshadowTrans);
                    break;
                case 2:
                    titleLayout.setBackgroundResource(R.color.colorCoffeeKeyshadowTrans);
                    break;
                case 3:
                    titleLayout.setBackgroundResource(R.color.colorAutumnKeyshadowTrans);
                    break;
                case 4:
                    titleLayout.setBackgroundResource(R.color.colorCoffeeKeyshadowTrans);
                    break;
            }*/
        }

        listMessage = new ArrayList<>();

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            threadId = extras.getInt("threadId");
            firstItemOfMain = extras.getInt("firstItem");
            final MessageObject messageObject = db.getFirstMessageByThreadId(threadId);
            personText = messageObject.getPerson();
            listMessage = db.getMessageByThreadId(threadId);
            person.setText(personText);
            address = messageObject.getAddress();

            setAvatar(messageObject);

            new AsyncTask<Void, Void, Void>() {

                @Override
                protected Void doInBackground(Void... voids) {
                    personText = Constant.getContactName(MessageActivity.this, messageObject.getAddress());
                    messageObject.setPerson(personText);
                    return null;
                }

                @Override
                protected void onPostExecute(Void aVoid) {
                    super.onPostExecute(aVoid);
                    person.setText(personText);
                    setAvatar(messageObject);
                }
            }.execute();
        }

        adapter = new ListMessageAdapter(this, listMessage);
        adapter.setHasStableIds(true);

        listMessageView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        listMessageView.setAdapter(adapter);
        listMessageView.scrollToPosition(listMessage.size() - 1);

        messageInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.toString().equals("")) {
                    sendButton.setTextColor(getResources().getColor(R.color.colorDate));
                } else {
                    sendButton.setTextColor(getResources().getColor(R.color.colorText));
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        Constant.increaseHitArea(backButton);
        Constant.increaseHitArea(infoButton);
        Constant.increaseHitArea(addButton);
        Constant.increaseHitArea(sendButton);

        backButton.setOnClickListener(this);
        infoButton.setOnClickListener(this);
        addButton.setOnClickListener(this);
        sendButton.setOnClickListener(this);

        receivedReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Bundle extras = intent.getExtras();
                if (extras != null) {
                    int newThreadId = extras.getInt("threadId");
                    if (newThreadId == threadId) {
                        MessageObject newInbox = db.getFirstMessageByThreadId(threadId);
                        newInbox.setType(1);
                        listMessage.add(newInbox);
                        adapter.notifyDataSetChanged();
                        listMessageView.smoothScrollToPosition(listMessage.size() - 1);
                    }
                }
            }
        };
        registerReceiver(receivedReceiver, new IntentFilter("sms_received"));

        sentReceiver = new SmsSentReceiver();
        registerReceiver(sentReceiver, new IntentFilter(SENT));

    }

    private void setAvatar(MessageObject messageObject) {
        if (messageObject.getThumbnailBase64().equals("")) {
            circleAvatar.setVisibility(View.GONE);
            avatar.setVisibility(View.VISIBLE);
            firstLetter.setVisibility(View.VISIBLE);
            String firstLetterPerson = String.valueOf(messageObject.getPerson().charAt(0));
            if (firstLetterPerson.matches("[a-zA-Z]+")) {
                firstLetter.setText(firstLetterPerson.toUpperCase());
            } else {
                firstLetter.setText("#");
            }
        } else {
            circleAvatar.setVisibility(View.VISIBLE);
            avatar.setVisibility(View.GONE);
            firstLetter.setVisibility(View.GONE);
            byte[] imageAsBytes = Base64.decode(messageObject.getThumbnailBase64(), Base64.DEFAULT);
            Bitmap myImage = BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.length);
            circleAvatar.setImageBitmap(myImage);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back_btn:
                finish();
                Intent mainIntent = new Intent(MessageActivity.this, MainActivity.class);
                mainIntent.putExtra("firstItem", firstItemOfMain);
                startActivity(mainIntent);
                overridePendingTransition(R.anim.enter_anim_rtl, R.anim.exit_anim_rtl);
                break;
            case R.id.info_btn:
                /*finish();
                Intent infoIntent = new Intent(MessageActivity.this, DetailActivity.class);
                infoIntent.putExtra("threadId", threadId);
                infoIntent.putExtra("mesCount", listMessage.size());
                startActivity(infoIntent);
                overridePendingTransition(R.anim.enter_anim_ltr, R.anim.exit_anim_ltr);*/
                DetailDialog detailDialog = new DetailDialog(this, threadId, listMessage.size());
                detailDialog.show();
                break;
            case R.id.add_btn:
                break;
            case R.id.send_btn:
                if (!messageInput.getText().toString().equals("")) {
                    sendSms(address, messageInput.getText().toString());
                }
                break;
        }
    }

    private void sendSms(String address, String message) {
        messageInput.setText("");

        //PendingIntent sentPI = PendingIntent.getBroadcast(this, 0, new Intent(SENT), 0);
        //PendingIntent deliveredPI = PendingIntent.getBroadcast(this, 0, new Intent(DELIVERED), 0);

        SmsManager smsManager = SmsManager.getDefault();
        ArrayList<String> parts = smsManager.divideMessage(message);
        final int count = parts.size();

        ArrayList<PendingIntent> sentPIList = new ArrayList<>(count);

        for (int i = 0; i < count; i++) {
            PendingIntent sentPIs = PendingIntent.getBroadcast(this, i, new Intent(SENT), PendingIntent.FLAG_ONE_SHOT);
            sentPIList.add(sentPIs);
        }

        //smsManager.sendTextMessage(address, null, message, sentPI, null);
        smsManager.sendMultipartTextMessage(address, null, parts, sentPIList, null);

        if (Constant.checkDefaultSmsApp(this)) {
            ContentValues values = new ContentValues();
            values.put("address", address);
            values.put("body", message);
            getContentResolver().insert(Uri.parse("content://sms/sent"), values);
        }

        listMessage.add(new MessageObject(0, threadId, address, person.getText().toString(), (new Date()).getTime(), message, 1, 2));
        adapter.notifyDataSetChanged();
        listMessageView.smoothScrollToPosition(listMessage.size() - 1);
    }

    @Override
    public void onBackPressed() {
        finish();
        Intent i = new Intent(MessageActivity.this, MainActivity.class);
        i.putExtra("firstItem", firstItemOfMain);
        startActivity(i);
        overridePendingTransition(R.anim.enter_anim_rtl, R.anim.exit_anim_rtl);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (sentReceiver != null) {
            unregisterReceiver(sentReceiver);
        }
        if (receivedReceiver != null) {
            unregisterReceiver(receivedReceiver);
        }
    }
}
