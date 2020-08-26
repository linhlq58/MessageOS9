package com.linhleeproject.mymessage.messengeros10.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.TypedValue;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.google.gson.Gson;
import com.linhleeproject.mymessage.messengeros10.MyApplication;
import com.linhleeproject.mymessage.messengeros10.R;
import com.linhleeproject.mymessage.messengeros10.models.ThemeObject;
import com.linhleeproject.mymessage.messengeros10.utils.Constant;

/**
 * Created by Linh Lee on 12/7/2016.
 */
public class TextSizeActivity extends AppCompatActivity implements View.OnClickListener {
    private RelativeLayout titleLayout;
    private TextView backButton;
    private TextView contentText;
    private TextView dragText;
    private SeekBar textSeekbar;
    private String themeJson;
    private int textSize;
    private SharedPreferences sharedPreferences;
    private Gson gson;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text_size);

        sharedPreferences = MyApplication.getSharedPreferences();
        gson = new Gson();
        themeJson = sharedPreferences.getString("current_theme", "");
        textSize = sharedPreferences.getInt("text_size", 2);

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
        contentText = (TextView) findViewById(R.id.content_text);
        dragText = (TextView) findViewById(R.id.drag_text);
        textSeekbar = (SeekBar) findViewById(R.id.text_seekbar);

        if (themeJson.equals("")) {
            titleLayout.setBackgroundResource(R.color.colorPrimary);
        } else {
            ThemeObject theme = gson.fromJson(themeJson, ThemeObject.class);
            titleLayout.setBackgroundColor(Color.parseColor("#" + theme.getKeyshadowColor()));
        }

        contentText.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize * 2 + 10);
        dragText.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize * 2 + 10);

        textSeekbar.setMax(4);
        textSeekbar.setProgress(textSize);
        textSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                contentText.setTextSize(TypedValue.COMPLEX_UNIT_SP, i * 2 + 10);
                dragText.setTextSize(TypedValue.COMPLEX_UNIT_SP, i * 2 + 10);
                sharedPreferences.edit().putInt("text_size", i).apply();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        Constant.increaseHitArea(backButton);

        backButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        finish();
        Intent i = new Intent(TextSizeActivity.this, SettingsActivity.class);
        startActivity(i);
        overridePendingTransition(R.anim.enter_anim_rtl, R.anim.exit_anim_rtl);
    }

    @Override
    public void onBackPressed() {
        finish();
        Intent i = new Intent(TextSizeActivity.this, SettingsActivity.class);
        startActivity(i);
        overridePendingTransition(R.anim.enter_anim_rtl, R.anim.exit_anim_rtl);
    }
}
