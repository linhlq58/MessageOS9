package com.linhleeproject.mymessage.messengeros10.activities;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.util.Base64;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.siyamed.shapeimageview.CircularImageView;
import com.google.gson.Gson;
import com.linhleeproject.mymessage.messengeros10.MyApplication;
import com.linhleeproject.mymessage.messengeros10.R;
import com.linhleeproject.mymessage.messengeros10.database.DatabaseHelper;
import com.linhleeproject.mymessage.messengeros10.models.MessageObject;
import com.linhleeproject.mymessage.messengeros10.models.ThemeObject;
import com.linhleeproject.mymessage.messengeros10.utils.Constant;

/**
 * Created by Linh Lee on 12/5/2016.
 */
public class DetailActivity extends AppCompatActivity implements View.OnClickListener {
    private DatabaseHelper db;
    private int threadId;
    private int mesCount;
    private String contactId;
    private MessageObject contact;
    private RelativeLayout titleLayout;
    private TextView backButton;
    private ImageView avatar;
    private CircularImageView circleAvatar;
    private TextView firstLetter;
    private ImageView callButton;
    private ImageView infoButton;
    private TextView name;
    private TextView number;
    private String themeJson;
    private SharedPreferences sharedPreferences;
    private Gson gson;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

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
        avatar = (ImageView) findViewById(R.id.avatar);
        circleAvatar = (CircularImageView) findViewById(R.id.circle_avatar);
        firstLetter = (TextView) findViewById(R.id.first_letter);
        callButton = (ImageView) findViewById(R.id.call_btn);
        infoButton = (ImageView) findViewById(R.id.info_btn);
        name = (TextView) findViewById(R.id.name);
        number = (TextView) findViewById(R.id.number);

        if (themeJson.equals("")) {
            titleLayout.setBackgroundResource(R.color.colorPrimary);
        } else {
            ThemeObject theme = gson.fromJson(themeJson, ThemeObject.class);
            titleLayout.setBackgroundColor(Color.parseColor("#" + theme.getKeyshadowColor()));
        }

        contact = new MessageObject();
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            threadId = extras.getInt("threadId");
            mesCount = extras.getInt("mesCount");
            contact = db.getFirstMessageByThreadId(threadId);
            contactId = Constant.getContactId(this, contact.getAddress());

            if (contact.getThumbnailBase64().equals("")) {
                circleAvatar.setVisibility(View.GONE);
                avatar.setVisibility(View.VISIBLE);
                firstLetter.setVisibility(View.VISIBLE);
                String firstLetterPerson = String.valueOf(contact.getPerson().charAt(0));
                if (firstLetterPerson.matches("[a-zA-Z]+")) {
                    firstLetter.setText(firstLetterPerson.toUpperCase());
                } else {
                    firstLetter.setText("#");
                }
            } else {
                circleAvatar.setVisibility(View.VISIBLE);
                avatar.setVisibility(View.GONE);
                firstLetter.setVisibility(View.GONE);
                byte[] imageAsBytes = Base64.decode(contact.getThumbnailBase64(), Base64.DEFAULT);
                Bitmap myImage = BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.length);
                circleAvatar.setImageBitmap(myImage);
            }
        }

        backButton.setText(contact.getPerson());
        name.setText(contact.getPerson()+ " (" + mesCount + ")");
        number.setText(contact.getAddress());

        Constant.increaseHitArea(backButton);
        Constant.increaseHitArea(callButton);
        Constant.increaseHitArea(infoButton);

        backButton.setOnClickListener(this);
        callButton.setOnClickListener(this);
        infoButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back_btn:
                finish();
                Intent i = new Intent(DetailActivity.this, MessageActivity.class);
                i.putExtra("threadId", contact.getThreadId());
                startActivity(i);
                overridePendingTransition(R.anim.enter_anim_rtl, R.anim.exit_anim_rtl);
                break;
            case R.id.call_btn:
                TelephonyManager telMgr = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
                int simState = telMgr.getSimState();
                switch (simState) {
                    case TelephonyManager.SIM_STATE_ABSENT:
                        showAlertDialog();
                        break;
                    case TelephonyManager.SIM_STATE_NETWORK_LOCKED:
                        showAlertDialog();
                        break;
                    case TelephonyManager.SIM_STATE_PIN_REQUIRED:
                        showAlertDialog();
                        break;
                    case TelephonyManager.SIM_STATE_PUK_REQUIRED:
                        showAlertDialog();
                        break;
                    case TelephonyManager.SIM_STATE_READY:
                        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + contact.getAddress()));
                        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                            ActivityCompat.requestPermissions(DetailActivity.this,
                                    new String[]{Manifest.permission.CALL_PHONE}, 2);
                            return;
                        } else {
                            startActivity(intent);
                        }
                        break;
                    case TelephonyManager.SIM_STATE_UNKNOWN:
                        showAlertDialog();
                        break;
                }
                break;
            case R.id.info_btn:
                if (!contactId.equals("")) {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    Uri uri = Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_URI, String.valueOf(contactId));
                    intent.setData(uri);
                    startActivity(intent);
                } else {
                    Toast.makeText(this, "No information", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    private void showAlertDialog() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setMessage(getResources().getString(R.string.sim_error));
        dialog.show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 2: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    Toast.makeText(DetailActivity.this, "Permission granted!", Toast.LENGTH_SHORT).show();

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(DetailActivity.this, "Permission denied!", Toast.LENGTH_SHORT).show();
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    @Override
    public void onBackPressed() {
        finish();
        Intent i = new Intent(DetailActivity.this, MessageActivity.class);
        i.putExtra("threadId", contact.getThreadId());
        startActivity(i);
        overridePendingTransition(R.anim.enter_anim_rtl, R.anim.exit_anim_rtl);
    }
}
