package com.linhleeproject.mymessage.messengeros10.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
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
import com.linhleeproject.mymessage.messengeros10.utils.VerticalSeekbar;

/**
 * Created by Linh Lee on 12/5/2016.
 */
public class BlurActivity extends AppCompatActivity implements View.OnClickListener {
    private RelativeLayout titleLayout;
    private TextView backButton;
    private VerticalSeekbar alphaSeekbar;
    private VerticalSeekbar blurSeekbar;
    private int alpha;
    private int blur;
    private String themeJson;
    private SharedPreferences sharedPreferences;
    private Gson gson;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blur);

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
        alphaSeekbar = (VerticalSeekbar) findViewById(R.id.alpha_seekbar);
        blurSeekbar = (VerticalSeekbar) findViewById(R.id.blur_seekbar);

        if (themeJson.equals("")) {
            titleLayout.setBackgroundResource(R.color.colorPrimary);
        } else {
            ThemeObject theme = gson.fromJson(themeJson, ThemeObject.class);
            titleLayout.setBackgroundColor(Color.parseColor("#" + theme.getKeyshadowColor()));
        }

        alpha = sharedPreferences.getInt("alpha", 50);
        blur = sharedPreferences.getInt("blur", 50);

        alphaSeekbar.setMax(100);
        alphaSeekbar.setProgress(alpha);
        alphaSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                Intent alphaIntent = new Intent("alpha_change");
                alphaIntent.putExtra("alpha", i);
                sendBroadcast(alphaIntent);
                sharedPreferences.edit().putInt("alpha", i).apply();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        blurSeekbar.setMax(100);
        blurSeekbar.setProgress(blur);
        blurSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                Intent blurIntent = new Intent("blur_change");
                blurIntent.putExtra("blur", i);
                sendBroadcast(blurIntent);
                sharedPreferences.edit().putInt("blur", i).apply();
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
        switch (view.getId()) {
            case R.id.back_btn:
                finish();
                Intent i = new Intent(BlurActivity.this, SettingsActivity.class);
                startActivity(i);
                overridePendingTransition(R.anim.enter_anim_rtl, R.anim.exit_anim_rtl);
                break;
        }
    }

    @Override
    public void onBackPressed() {
        finish();
        Intent i = new Intent(BlurActivity.this, SettingsActivity.class);
        startActivity(i);
        overridePendingTransition(R.anim.enter_anim_rtl, R.anim.exit_anim_rtl);
    }
}
