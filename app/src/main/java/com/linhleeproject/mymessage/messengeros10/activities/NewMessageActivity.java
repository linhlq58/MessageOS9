package com.linhleeproject.mymessage.messengeros10.activities;

import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.telephony.SmsManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.linhleeproject.mymessage.messengeros10.MyApplication;
import com.linhleeproject.mymessage.messengeros10.R;
import com.linhleeproject.mymessage.messengeros10.adapters.ListContactAdapter;
import com.linhleeproject.mymessage.messengeros10.adapters.ListMessageAdapter;
import com.linhleeproject.mymessage.messengeros10.database.DatabaseHelper;
import com.linhleeproject.mymessage.messengeros10.models.ContactObject;
import com.linhleeproject.mymessage.messengeros10.models.MessageObject;
import com.linhleeproject.mymessage.messengeros10.models.ThemeObject;
import com.linhleeproject.mymessage.messengeros10.utils.Constant;
import com.linhleeproject.mymessage.messengeros10.utils.SimpleDividerItemDecoration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

/**
 * Created by Linh Lee on 12/2/2016.
 */
public class NewMessageActivity extends AppCompatActivity implements View.OnClickListener {
    private String SENT = "SMS_SENT";
    private String DELIVERED = "SMS_DELIVERED";

    private DatabaseHelper db;
    private RelativeLayout titleLayout;
    private TextView cancelButton;
    private ImageView addContactButton;
    private ImageView addMoreButton;
    private TextView sendButton;
    private EditText contactInput;
    private EditText messageInput;
    private RecyclerView listContactView;
    private ListContactAdapter listContactAdapter;
    private ArrayList<ContactObject> listContact;
    private List<String> listAddress;
    private RecyclerView listMessageView;
    private ListMessageAdapter listMessageAdapter;
    private ArrayList<MessageObject> listMessage;
    private String listAddressString = "";
    private String themeJson;
    private SharedPreferences sharedPreferences;
    private Gson gson;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_message);

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
        cancelButton = (TextView) findViewById(R.id.cancel_btn);
        addContactButton = (ImageView) findViewById(R.id.add_contact);
        addMoreButton = (ImageView) findViewById(R.id.add_btn);
        sendButton = (TextView) findViewById(R.id.send_btn);
        contactInput = (EditText) findViewById(R.id.contact_input);
        messageInput = (EditText) findViewById(R.id.message_input);
        listContactView = (RecyclerView) findViewById(R.id.list_contact);
        listMessageView = (RecyclerView) findViewById(R.id.list_message);

        if (themeJson.equals("")) {
            titleLayout.setBackgroundResource(R.color.colorPrimary);
        } else {
            ThemeObject theme = gson.fromJson(themeJson, ThemeObject.class);
            titleLayout.setBackgroundColor(Color.parseColor("#" + theme.getKeyshadowColor()));
        }

        listContact = db.getAllContact();
        Collections.sort(listContact, new Comparator<ContactObject>() {
            @Override
            public int compare(ContactObject contactObject, ContactObject t1) {
                return contactObject.getName().compareToIgnoreCase(t1.getName());
            }
        });

        listContactAdapter = new ListContactAdapter(this, R.layout.item_contact, listContact, new ListContactAdapter.PositionClickListener() {
            @Override
            public void itemClicked(int position) {
                if (listAddressString.contains(",")) {
                    listAddressString = listAddressString.substring(0, listAddressString.lastIndexOf(",") + 1) + listContactAdapter.getItem(position).getName() + ",";
                } else {
                    listAddressString = listContactAdapter.getItem(position).getName() + ",";
                }
                contactInput.setText(listAddressString);
                contactInput.setSelection(contactInput.getText().length());
            }
        });
        listContactAdapter.setHasStableIds(true);

        listContactView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        listContactView.addItemDecoration(new SimpleDividerItemDecoration(this));
        listContactView.setAdapter(listContactAdapter);

        listMessage = new ArrayList<>();
        listMessageAdapter = new ListMessageAdapter(this, listMessage);
        listMessageAdapter.setHasStableIds(true);

        listMessageView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        listMessageView.setAdapter(listMessageAdapter);

        listMessageView.scrollToPosition(listMessage.size() - 1);

        contactInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                listContactView.setVisibility(View.VISIBLE);
                listMessageView.setVisibility(View.GONE);
                if (charSequence.toString().contains(",")) {
                    listContactAdapter.getFilter().filter(Constant.getLastSubstring(charSequence.toString()));
                } else {
                    listContactAdapter.getFilter().filter(charSequence);
                }

                listAddressString = contactInput.getText().toString();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        messageInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                listContactView.setVisibility(View.GONE);
                listMessageView.setVisibility(View.VISIBLE);
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

        Constant.increaseHitArea(cancelButton);
        Constant.increaseHitArea(addContactButton);
        Constant.increaseHitArea(addMoreButton);
        Constant.increaseHitArea(sendButton);

        cancelButton.setOnClickListener(this);
        addContactButton.setOnClickListener(this);
        addMoreButton.setOnClickListener(this);
        sendButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.cancel_btn:
                finish();
                Intent intent = new Intent(NewMessageActivity.this, MainActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.enter_anim_utd, R.anim.exit_anim_utd);
                break;
            case R.id.add_contact:
                listContactView.setVisibility(View.VISIBLE);
                break;
            case R.id.add_btn:
                break;
            case R.id.send_btn:
                listAddress = Arrays.asList(listAddressString.split(","));

                ArrayList<String> listNumber = new ArrayList<>();
                if (listAddress.size() > 0) {
                    for (int i = 0; i < listAddress.size(); i++) {
                        int j;
                        for (j = 0; j < listContact.size(); j++) {
                            if (listAddress.get(i).equals(listContact.get(j).getName())) {
                                listNumber.add(Constant.removeSpace(listContact.get(j).getPhoneNumber()));
                                break;
                            }
                        }

                        if (j >= listContact.size()) {
                            if (listAddress.get(i).matches("[0-9]+")) {
                                listNumber.add(Constant.removeSpace(listAddress.get(i)));
                            }
                        }
                    }
                }

                if (!messageInput.getText().toString().equals("")) {
                    if (listNumber.size() > 0) {
                        sendSms(listNumber, messageInput.getText().toString());
                    } else {
                        Toast.makeText(this, "Please enter number", Toast.LENGTH_SHORT).show();
                    }
                }
                break;
        }
    }

    private void sendSms(ArrayList<String> listNumber, String message) {
        messageInput.setText("");

        //PendingIntent sentPI = PendingIntent.getBroadcast(this, 0, new Intent(SENT), 0);
        //PendingIntent deliveredPI = PendingIntent.getBroadcast(this, 0, new Intent(DELIVERED), 0);

        SmsManager smsManager = SmsManager.getDefault();
        ArrayList<String> parts = smsManager.divideMessage(message);
        final int count = parts.size();

        for (int i = 0; i < listNumber.size(); i++) {
            ArrayList<PendingIntent> sentPIList = new ArrayList<>(count);
            for (int j = 0; j < count; j++) {
                PendingIntent sentPIs = PendingIntent.getBroadcast(this, j, new Intent(SENT), PendingIntent.FLAG_ONE_SHOT);
                sentPIList.add(sentPIs);
            }

            //smsManager.sendTextMessage(listNumber.get(i), null, message, sentPI, null);
            smsManager.sendMultipartTextMessage(listNumber.get(i), null, parts, sentPIList, null);

            if (Constant.checkDefaultSmsApp(this)) {
                ContentValues values = new ContentValues();
                values.put("address", listNumber.get(i));
                values.put("body", message);
                getContentResolver().insert(Uri.parse("content://sms/sent"), values);
            }
        }

        listMessage.add(new MessageObject((new Date()).getTime(), message, 1, 2));
        listMessageAdapter.notifyDataSetChanged();
        listMessageView.smoothScrollToPosition(listMessage.size() - 1);
    }

    @Override
    public void onBackPressed() {
        finish();
        Intent i = new Intent(NewMessageActivity.this, MainActivity.class);
        startActivity(i);
        overridePendingTransition(R.anim.enter_anim_utd, R.anim.exit_anim_utd);
    }
}
