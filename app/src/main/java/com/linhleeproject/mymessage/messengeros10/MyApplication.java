package com.linhleeproject.mymessage.messengeros10;

import android.app.Application;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.linhleeproject.mymessage.messengeros10.database.DatabaseHelper;
import com.linhleeproject.mymessage.messengeros10.utils.Constant;
import com.beolla.ZergitasSDK;

/**
 * Created by Linh Lee on 11/29/2016.
 */
public class MyApplication extends Application {
    private static DatabaseHelper db;
    private static SharedPreferences sharedPreferences;

    @Override
    public void onCreate() {
        super.onCreate();
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        ZergitasSDK.initApplication(this, this.getApplicationContext().getPackageName());

        String language = sharedPreferences.getString("language", "English");
        if (language.equals("English")) {
            Constant.changeLanguage(this, "en-US");
        } else if (language.equals("Tiếng Việt")) {
            Constant.changeLanguage(this, "vi");
        }
    }

    public static DatabaseHelper getDb() {
        return db;
    }

    public static void setDb(DatabaseHelper databaseHelper) {
        db = databaseHelper;
    }

    public static SharedPreferences getSharedPreferences() {
        return sharedPreferences;
    }
}
