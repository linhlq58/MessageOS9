package com.linhleeproject.mymessage.messengeros10.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.linhleeproject.mymessage.messengeros10.dialogs.LanguageDialog;
import com.google.gson.Gson;
import com.linhleeproject.mymessage.messengeros10.MyApplication;
import com.linhleeproject.mymessage.messengeros10.R;
import com.linhleeproject.mymessage.messengeros10.adapters.ListSettingsAdapter;
import com.linhleeproject.mymessage.messengeros10.dialogs.ResetToDefaultDialog;
import com.linhleeproject.mymessage.messengeros10.models.SettingsObject;
import com.linhleeproject.mymessage.messengeros10.models.ThemeObject;
import com.linhleeproject.mymessage.messengeros10.utils.Constant;
import com.linhleeproject.mymessage.messengeros10.utils.SimpleDividerItemDecoration;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;

/**
 * Created by Linh Lee on 12/5/2016.
 */
public class SettingsActivity extends AppCompatActivity implements View.OnClickListener {
    private static final int PICK_IMAGE = 100;

    private RecyclerView listSettingsView;
    private ListSettingsAdapter adapter;
    private ArrayList<SettingsObject> listSettings;
    private RelativeLayout titleLayout;
    private TextView backButton;
    private String mSelectImage = "Select Image";
    private String mImageType = "image/*";
    private Bitmap mySelectedImage;
    private String themeJson;
    private SharedPreferences sharedPreferences;
    private Gson gson;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

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
        listSettingsView = (RecyclerView) findViewById(R.id.list_settings);
        backButton = (TextView) findViewById(R.id.back_btn);

        if (themeJson.equals("")) {
            titleLayout.setBackgroundResource(R.color.colorPrimary);
        } else {
            ThemeObject theme = gson.fromJson(themeJson, ThemeObject.class);
            titleLayout.setBackgroundColor(Color.parseColor("#" + theme.getKeyshadowColor()));
        }

        listSettings = new ArrayList<>();
        listSettings.add(new SettingsObject(0, getResources().getString(R.string.interface_text)));
        listSettings.add(new SettingsObject(R.mipmap.ic_theme, getResources().getString(R.string.theme)));
        listSettings.add(new SettingsObject(R.mipmap.ic_background, getResources().getString(R.string.background)));
        listSettings.add(new SettingsObject(R.mipmap.ic_blur, getResources().getString(R.string.blur)));
        listSettings.add(new SettingsObject(R.mipmap.ic_textsize, getResources().getString(R.string.textsize)));
        listSettings.add(new SettingsObject(R.mipmap.ic_language, getResources().getString(R.string.language)));
        listSettings.add(new SettingsObject(0, getResources().getString(R.string.other_settings)));
        listSettings.add(new SettingsObject(R.mipmap.ic_sound, getResources().getString(R.string.sound)));
        listSettings.add(new SettingsObject(R.mipmap.ic_vibrate, getResources().getString(R.string.vibrate)));
        listSettings.add(new SettingsObject(R.mipmap.ic_reset, getResources().getString(R.string.reset_to_default)));

        adapter = new ListSettingsAdapter(this, listSettings, new ListSettingsAdapter.PositionClickListener() {
            @Override
            public void itemClicked(int position) {
                /*SwitchButton switchButton1 = (SwitchButton) listSettingsView.getChildAt(6).findViewById(R.id.switch_btn);
                SwitchButton switchButton2 = (SwitchButton) listSettingsView.getChildAt(7).findViewById(R.id.switch_btn);*/
                switch (position) {
                    case 1:
                        Intent themeIntent = new Intent(SettingsActivity.this, ThemeActivity.class);
                        startActivity(themeIntent);
                        finish();
                        overridePendingTransition(R.anim.enter_anim_ltr, R.anim.exit_anim_ltr);
                        break;
                    case 2:
                        Intent getIntent = new Intent(Intent.ACTION_GET_CONTENT);
                        getIntent.setType(mImageType);

                        Intent pickIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        pickIntent.setType(mImageType);

                        Intent chooserIntent = Intent.createChooser(getIntent, mSelectImage);
                        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[]{pickIntent});

                        startActivityForResult(chooserIntent, PICK_IMAGE);
                        break;
                    case 3:
                        Intent blurIntent = new Intent(SettingsActivity.this, BlurActivity.class);
                        startActivity(blurIntent);
                        finish();
                        overridePendingTransition(R.anim.enter_anim_ltr, R.anim.exit_anim_ltr);
                        break;
                    case 4:
                        Intent textSizeIntent = new Intent(SettingsActivity.this, TextSizeActivity.class);
                        startActivity(textSizeIntent);
                        finish();
                        overridePendingTransition(R.anim.enter_anim_ltr, R.anim.exit_anim_ltr);
                        break;
                    case 5:
                        LanguageDialog languageDialog = new LanguageDialog(SettingsActivity.this, new LanguageDialog.LanguageClicked() {
                            @Override
                            public void engClicked() {
                                sharedPreferences.edit().putString("language", "English").apply();
                                Constant.changeLanguage(SettingsActivity.this, "en-US");

                                Intent i = getBaseContext().getPackageManager()
                                        .getLaunchIntentForPackage( getBaseContext().getPackageName() );
                                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(i);
                            }

                            @Override
                            public void viClicked() {
                                sharedPreferences.edit().putString("language", "Tiếng Việt").apply();
                                Constant.changeLanguage(SettingsActivity.this, "vi");

                                Intent i = getBaseContext().getPackageManager()
                                        .getLaunchIntentForPackage( getBaseContext().getPackageName() );
                                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(i);
                            }
                        });
                        languageDialog.show();
                        break;
                    case 7:
                        //switchButton1.performClick();
                        break;
                    case 8:
                        //switchButton2.performClick();
                        break;
                    case 9:
                        ResetToDefaultDialog resetDialog = new ResetToDefaultDialog(SettingsActivity.this, new ResetToDefaultDialog.ButtonClicked() {
                            @Override
                            public void okClicked() {
                                sharedPreferences.edit().putString("current_theme", "").apply();
                                sharedPreferences.edit().putString("image_base64", "").apply();
                                sharedPreferences.edit().putInt("blur", 5).apply();
                                sharedPreferences.edit().putInt("alpha", 5).apply();
                                Intent i = getBaseContext().getPackageManager()
                                        .getLaunchIntentForPackage( getBaseContext().getPackageName() );
                                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(i);
                            }
                        });
                        resetDialog.show();
                        break;
                }
            }
        });
        adapter.setHasStableIds(true);

        listSettingsView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        listSettingsView.addItemDecoration(new SimpleDividerItemDecoration(this));
        listSettingsView.setAdapter(adapter);

        Constant.increaseHitArea(backButton);

        backButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back_btn:
                finish();
                Intent i = new Intent(SettingsActivity.this, MainActivity.class);
                startActivity(i);
                overridePendingTransition(R.anim.enter_anim_dtu, R.anim.exit_anim_dtu);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case PICK_IMAGE:
                if (resultCode == RESULT_OK) {
                    Uri selectedImage = data.getData();
                    try {
                        mySelectedImage = Constant.decodeUri(this, selectedImage);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }

                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    mySelectedImage.compress(Bitmap.CompressFormat.PNG, 100, baos);
                    byte[] byteArr = baos.toByteArray();
                    String encodedImage = Base64.encodeToString(byteArr, Base64.DEFAULT);

                    sharedPreferences.edit().putString("image_base64", encodedImage).apply();

                    Intent i = new Intent("change_bg");
                    sendBroadcast(i);
                }
                break;
        }
    }

    @Override
    public void onBackPressed() {
        finish();
        Intent i = new Intent(SettingsActivity.this, MainActivity.class);
        startActivity(i);
        overridePendingTransition(R.anim.enter_anim_dtu, R.anim.exit_anim_dtu);
    }
}
