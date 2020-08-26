package com.linhleeproject.mymessage.messengeros10.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.beolla.ZergitasSDK;
import com.google.gson.Gson;
import com.linhleeproject.mymessage.messengeros10.adapters.ListConversationAdapter;
import com.linhleeproject.mymessage.messengeros10.database.DatabaseHelper;
import com.linhleeproject.mymessage.messengeros10.dialogs.ConfirmDeleteDialog;
import com.linhleeproject.mymessage.messengeros10.models.MessageObject;
import com.linhleeproject.mymessage.messengeros10.MyApplication;
import com.linhleeproject.mymessage.messengeros10.R;
import com.linhleeproject.mymessage.messengeros10.models.ThemeObject;
import com.linhleeproject.mymessage.messengeros10.utils.Constant;
import com.linhleeproject.mymessage.messengeros10.utils.SimpleDividerItemDecoration;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    public static final String INBOX = "content://sms/inbox";

    private DatabaseHelper db;
    private LinearLayout mainLayout;
    private RelativeLayout titleLayout;
    private TextView editButton;
    private ImageView settingsButton;
    private ImageView newMesButton;
    private RecyclerView listConversationView;
    private ListConversationAdapter adapter;
    private ArrayList<MessageObject> listConversation;
    private EditText searchInput;
    private RelativeLayout footerLayout;
    private TextView allButton;
    private TextView deleteButton;
    private boolean isEdit = false;
    private int firstItemVisible = 0;
    private String themeJson;
    private SharedPreferences sharedPreferences;
    private Gson gson;

    private BroadcastReceiver sentReceiver, receivedReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ZergitasSDK.init(this);

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
        mainLayout = (LinearLayout) findViewById(R.id.main_layout);
        editButton = (TextView) findViewById(R.id.edit_text);
        settingsButton = (ImageView) findViewById(R.id.settings_btn);
        newMesButton = (ImageView) findViewById(R.id.new_mes_btn);
        listConversationView = (RecyclerView) findViewById(R.id.list_conversation);
        searchInput = (EditText) findViewById(R.id.search_input);
        footerLayout = (RelativeLayout) findViewById(R.id.footer_layout);
        allButton = (TextView) findViewById(R.id.all_btn);
        deleteButton = (TextView) findViewById(R.id.delete_btn);

        if (themeJson.equals("")) {
            titleLayout.setBackgroundResource(R.color.colorPrimary);
        } else {
            ThemeObject theme = gson.fromJson(themeJson, ThemeObject.class);
            titleLayout.setBackgroundColor(Color.parseColor("#" + theme.getKeyshadowColor()));
        }

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            firstItemVisible = extras.getInt("firstItem");
        }

        setupRecyclerView();

        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... voids) {
                for (int i = 0; i < listConversation.size(); i++) {
                    MessageObject message = listConversation.get(i);
                    listConversation.get(i).setPerson(Constant.getContactName(MainActivity.this, message.getAddress()));
                }
                db.updateListMessage(listConversation);
                return null;
            }

            @Override
            protected void onProgressUpdate(Void... values) {
                super.onProgressUpdate(values);
                adapter.notifyDataSetChanged();
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                adapter.notifyDataSetChanged();
            }
        }.execute();

        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... voids) {
                ArrayList<MessageObject> listNeedUpdate = new ArrayList<>();
                for (int i = 0; i < listConversation.size(); i++) {
                    MessageObject message = listConversation.get(i);
                    int thumbnailId = Constant.getThumbnailId(MainActivity.this, message.getAddress());
                    if (thumbnailId != 0) {
                        Bitmap myThumbnail = Constant.fetchThumbnail(MainActivity.this, thumbnailId);
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        myThumbnail.compress(Bitmap.CompressFormat.PNG, 100, baos);
                        byte[] byteArr = baos.toByteArray();
                        String encodedImage = Base64.encodeToString(byteArr, Base64.DEFAULT);

                        listConversation.get(i).setThumbnailBase64(encodedImage);
                        listNeedUpdate.add(listConversation.get(i));
                    }
                }
                db.updateListMessage(listNeedUpdate);
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                adapter.notifyDataSetChanged();
            }

        }.execute();

        searchInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (searchInput.getText().toString().equals("")) {
                    mainLayout.requestFocus();
                }
                adapter.getFilter().filter(charSequence);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        sentReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Bundle extras = intent.getExtras();
                if (extras != null) {
                    int threadId = extras.getInt("threadId");
                    for (int i = 0; i < listConversation.size(); i++) {
                        if (listConversation.get(i).getThreadId() == threadId) {
                            listConversation.remove(i);
                            listConversation.add(0, db.getFirstMessageByThreadId(threadId));
                            adapter.notifyDataSetChanged();
                        }
                    }
                }
            }
        };
        registerReceiver(sentReceiver, new IntentFilter("update_conversation"));

        receivedReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Bundle extras = intent.getExtras();
                if (extras != null) {
                    int i;
                    int threadId = extras.getInt("threadId");
                    final MessageObject newInbox = db.getFirstMessageByThreadId(threadId);

                    new AsyncTask<Void, Void, Void>() {

                        @Override
                        protected Void doInBackground(Void... voids) {
                            int thumbnailId = Constant.getThumbnailId(MainActivity.this, newInbox.getAddress());
                            if (thumbnailId != 0) {
                                Bitmap myThumbnail = Constant.fetchThumbnail(MainActivity.this, thumbnailId);
                                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                                myThumbnail.compress(Bitmap.CompressFormat.PNG, 100, baos);
                                byte[] byteArr = baos.toByteArray();
                                String encodedImage = Base64.encodeToString(byteArr, Base64.DEFAULT);

                                newInbox.setThumbnailBase64(encodedImage);
                                db.updateMessage(newInbox);
                            }
                            return null;
                        }

                        @Override
                        protected void onPostExecute(Void aVoid) {
                            super.onPostExecute(aVoid);
                            adapter.notifyDataSetChanged();
                        }

                    }.execute();

                    for (i = 0; i < listConversation.size(); i ++) {
                        if (listConversation.get(i).getThreadId() == threadId) {
                            listConversation.remove(i);
                            listConversation.add(0, newInbox);
                            adapter.notifyDataSetChanged();
                            break;
                        }
                    }
                    if (i >= listConversation.size()) {
                        listConversation.add(0, newInbox);
                        adapter.notifyDataSetChanged();
                    }
                }
            }
        };
        registerReceiver(receivedReceiver, new IntentFilter("sms_received"));

        Constant.increaseHitArea(editButton);
        Constant.increaseHitArea(settingsButton);
        Constant.increaseHitArea(newMesButton);
        Constant.increaseHitArea(allButton);
        Constant.increaseHitArea(deleteButton);

        editButton.setOnClickListener(this);
        settingsButton.setOnClickListener(this);
        newMesButton.setOnClickListener(this);
        allButton.setOnClickListener(this);
        deleteButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.edit_text:
                if (!isEdit) {
                    editButton.setText(getResources().getString(R.string.cancel));
                    footerLayout.setVisibility(View.VISIBLE);
                    adapter.setIsEdit(true);
                    adapter.setupCheckList();
                    adapter.notifyDataSetChanged();
                    isEdit = true;
                } else {
                    editButton.setText(getResources().getString(R.string.edit));
                    footerLayout.setVisibility(View.GONE);
                    adapter.setIsEdit(false);
                    adapter.notifyDataSetChanged();
                    deleteButton.setVisibility(View.GONE);
                    isEdit = false;
                }
                break;
            case R.id.settings_btn:
                Intent settingsIntent = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(settingsIntent);
                overridePendingTransition(R.anim.enter_anim_utd, R.anim.exit_anim_utd);
                finish();
                break;
            case R.id.new_mes_btn:
                Intent newMesIntent = new Intent(MainActivity.this, NewMessageActivity.class);
                startActivity(newMesIntent);
                finish();
                overridePendingTransition(R.anim.enter_anim_dtu, R.anim.exit_anim_dtu);
                break;
            case R.id.all_btn:
                adapter.setAllChecked();
                adapter.notifyDataSetChanged();

                deleteButton.setVisibility(View.VISIBLE);
                break;
            case R.id.delete_btn:
                final ArrayList<Integer> listCheckedPosition = adapter.getAllCheckedPosition();
                ConfirmDeleteDialog dialog = new ConfirmDeleteDialog(this, new ConfirmDeleteDialog.ButtonClicked() {
                    @Override
                    public void okClicked() {
                        if (listCheckedPosition.size() > 0) {
                            for (int i = 0; i < listCheckedPosition.size(); i++) {
                                int threadIdToDel = listConversation.get(listCheckedPosition.get(i)).getThreadId();
                                Constant.deleteSmsByThreadId(MainActivity.this, threadIdToDel);
                                db.deleteAllMessageByThreadId(threadIdToDel);
                            }

                            setupRecyclerView();
                            editButton.setText(getResources().getString(R.string.edit));
                            footerLayout.setVisibility(View.GONE);
                            adapter.setIsEdit(false);
                            deleteButton.setVisibility(View.GONE);
                            isEdit = false;
                        }
                    }
                });
                dialog.show();
                break;
        }
    }

    private void setupRecyclerView() {
        listConversation = db.getMessageGroupByThread();

        adapter = new ListConversationAdapter(this, R.layout.item_conversation, listConversation, new ListConversationAdapter.PositionClickListener() {
            @Override
            public void itemClicked(int position) {
                if (!isEdit) {
                    Intent i = new Intent(MainActivity.this, MessageActivity.class);
                    i.putExtra("threadId", adapter.getItem(position).getThreadId());
                    i.putExtra("firstItem", ((LinearLayoutManager) listConversationView.getLayoutManager()).findFirstVisibleItemPosition());
                    startActivity(i);
                    finish();
                    overridePendingTransition(R.anim.enter_anim_ltr, R.anim.exit_anim_ltr);

                    if (adapter.getItem(position).getRead() == 0) {
                        adapter.getItem(position).setRead(1);
                        adapter.notifyDataSetChanged();
                        db.updateMessage(adapter.getItem(position));

                        Constant.markMessageRead(MainActivity.this, adapter.getItem(position));
                    }
                } else {
                    adapter.setCheckedPosition(position);
                    adapter.notifyDataSetChanged();

                    ArrayList<Integer> listCheckedPosition = adapter.getAllCheckedPosition();
                    if (listCheckedPosition.size() > 0) {
                        deleteButton.setVisibility(View.VISIBLE);
                    } else {
                        deleteButton.setVisibility(View.GONE);
                    }
                }
            }
        });
        adapter.setHasStableIds(true);

        listConversationView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        listConversationView.addItemDecoration(new SimpleDividerItemDecoration(this));
        listConversationView.setAdapter(adapter);
        listConversationView.scrollToPosition(firstItemVisible);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        mainLayout.requestFocus();
        Intent i = new Intent("close_app");
        sendBroadcast(i);
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
