package com.linhleeproject.mymessage.messengeros10.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.linhleeproject.mymessage.messengeros10.MyApplication;
import com.linhleeproject.mymessage.messengeros10.R;
import com.linhleeproject.mymessage.messengeros10.adapters.ListThemeAdapter;
import com.linhleeproject.mymessage.messengeros10.models.ThemeObject;
import com.linhleeproject.mymessage.messengeros10.utils.Constant;
import com.linhleeproject.mymessage.messengeros10.utils.GridAutofitLayoutManager;

import java.util.ArrayList;

/**
 * Created by Linh Lee on 12/6/2016.
 */
public class ThemeActivity extends AppCompatActivity implements View.OnClickListener {
    private RelativeLayout titleLayout;
    private TextView backButton;
    private RecyclerView listThemeView;
    private ListThemeAdapter adapter;
    private ArrayList<ThemeObject> listTheme;
    private String themeJson;
    private SharedPreferences sharedPreferences;
    private Gson gson;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_theme);

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
        listThemeView = (RecyclerView) findViewById(R.id.list_theme);

        if (themeJson.equals("")) {
            titleLayout.setBackgroundResource(R.color.colorPrimary);
        } else {
            ThemeObject theme = gson.fromJson(themeJson, ThemeObject.class);
            titleLayout.setBackgroundColor(Color.parseColor("#" + theme.getKeyshadowColor()));
        }

        listTheme = new ArrayList<>();
        listTheme.add(new ThemeObject(1, R.mipmap.theme_winter, R.mipmap.bg_winter, Integer.toHexString(getResources().getColor(R.color.colorWinterSolid)),
                Integer.toHexString(getResources().getColor(R.color.colorWinterStroke)), Integer.toHexString(getResources().getColor(R.color.colorWinterBar)),
                Integer.toHexString(getResources().getColor(R.color.colorWinterKeyshadow)), Integer.toHexString(getResources().getColor(R.color.colorWinterKeyshadowTrans))));
        listTheme.add(new ThemeObject(2, R.mipmap.theme_coffee, R.mipmap.bg_coffee, Integer.toHexString(getResources().getColor(R.color.colorCoffeeSolid)),
                Integer.toHexString(getResources().getColor(R.color.colorCoffeeStroke)), Integer.toHexString(getResources().getColor(R.color.colorCoffeeBar)),
                Integer.toHexString(getResources().getColor(R.color.colorCoffeeKeyshadow)), Integer.toHexString(getResources().getColor(R.color.colorCoffeeKeyshadowTrans))));
        listTheme.add(new ThemeObject(3, R.mipmap.theme_autumngirl, R.mipmap.bg_autumngirl, Integer.toHexString(getResources().getColor(R.color.colorAutumnSolid)),
                Integer.toHexString(getResources().getColor(R.color.colorAutumnStroke)), Integer.toHexString(getResources().getColor(R.color.colorAutumnBar)),
                Integer.toHexString(getResources().getColor(R.color.colorAutumnKeyshadow)), Integer.toHexString(getResources().getColor(R.color.colorAutumnKeyshadowTrans))));
        listTheme.add(new ThemeObject(4, R.mipmap.theme_skull, R.mipmap.bg_skull, Integer.toHexString(getResources().getColor(R.color.colorSkullSolid)),
                Integer.toHexString(getResources().getColor(R.color.colorSkullStroke)), Integer.toHexString(getResources().getColor(R.color.colorCoffeeBar)),
                Integer.toHexString(getResources().getColor(R.color.colorCoffeeKeyshadow)), Integer.toHexString(getResources().getColor(R.color.colorCoffeeKeyshadowTrans))));
        listTheme.add(new ThemeObject(5, R.mipmap.theme_violet, R.mipmap.bg_violet, Integer.toHexString(getResources().getColor(R.color.colorVioletSolid)),
                Integer.toHexString(getResources().getColor(R.color.colorVioletStroke)), Integer.toHexString(getResources().getColor(R.color.colorVioletBar)),
                Integer.toHexString(getResources().getColor(R.color.colorVioletKeyshadow)), Integer.toHexString(getResources().getColor(R.color.colorVioletKeyshadowTrans))));
        listTheme.add(new ThemeObject(6, R.mipmap.theme_rainbow, R.mipmap.bg_rainbow, Integer.toHexString(getResources().getColor(R.color.colorRainbowSolid)),
                Integer.toHexString(getResources().getColor(R.color.colorRainbowStroke)), Integer.toHexString(getResources().getColor(R.color.colorRainbowBar)),
                Integer.toHexString(getResources().getColor(R.color.colorRainbowKeyshadow)), Integer.toHexString(getResources().getColor(R.color.colorRainbowKeyshadowTrans))));
        listTheme.add(new ThemeObject(7, R.mipmap.theme_cloud, R.mipmap.bg_cloud, Integer.toHexString(getResources().getColor(R.color.colorCloudSolid)),
                Integer.toHexString(getResources().getColor(R.color.colorCloudStroke)), Integer.toHexString(getResources().getColor(R.color.colorCloudBar)),
                Integer.toHexString(getResources().getColor(R.color.colorCloudKeyshadow)), Integer.toHexString(getResources().getColor(R.color.colorCloudKeyshadowTrans))));
        listTheme.add(new ThemeObject(8, R.mipmap.theme_cake, R.mipmap.bg_cake, Integer.toHexString(getResources().getColor(R.color.colorCakeSolid)),
                Integer.toHexString(getResources().getColor(R.color.colorCakeStroke)), Integer.toHexString(getResources().getColor(R.color.colorCakeBar)),
                Integer.toHexString(getResources().getColor(R.color.colorCakeKeyshadow)), Integer.toHexString(getResources().getColor(R.color.colorCakeKeyshadowTrans))));

        adapter = new ListThemeAdapter(this, R.layout.item_theme, listTheme, new ListThemeAdapter.PositionClickListener() {
            @Override
            public void itemClicked(int position) {
                sharedPreferences.edit().putString("current_theme", gson.toJson(listTheme.get(position))).apply();
                sharedPreferences.edit().putString("image_base64", "").apply();
                sharedPreferences.edit().putInt("blur", 0).apply();
                sharedPreferences.edit().putInt("alpha", 0).apply();
                Intent i = getBaseContext().getPackageManager()
                        .getLaunchIntentForPackage( getBaseContext().getPackageName() );
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
            }
        });
        adapter.setHasStableIds(true);

        listThemeView.setLayoutManager(new GridAutofitLayoutManager(this, Constant.convertDpIntoPixels(100, this)));
        listThemeView.setAdapter(adapter);

        Constant.increaseHitArea(backButton);

        backButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back_btn:
                finish();
                Intent i = new Intent(ThemeActivity.this, SettingsActivity.class);
                startActivity(i);
                overridePendingTransition(R.anim.enter_anim_rtl, R.anim.exit_anim_rtl);
                break;
        }
    }

    @Override
    public void onBackPressed() {
        finish();
        Intent i = new Intent(ThemeActivity.this, SettingsActivity.class);
        startActivity(i);
        overridePendingTransition(R.anim.enter_anim_rtl, R.anim.exit_anim_rtl);
    }
}
