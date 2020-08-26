package com.linhleeproject.mymessage.messengeros10.dialogs;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.ActivityCompat;
import android.telephony.TelephonyManager;
import android.util.Base64;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.siyamed.shapeimageview.CircularImageView;
import com.linhleeproject.mymessage.messengeros10.MyApplication;
import com.linhleeproject.mymessage.messengeros10.R;
import com.linhleeproject.mymessage.messengeros10.database.DatabaseHelper;
import com.linhleeproject.mymessage.messengeros10.models.MessageObject;
import com.linhleeproject.mymessage.messengeros10.utils.Constant;

/**
 * Created by Linh Lee on 12/9/2016.
 */
public class DetailDialog extends Dialog implements View.OnClickListener {
    private Activity context;
    private int threadId;
    private int mesCount;

    private DatabaseHelper db;
    private TextView okButton;
    private String contactId;
    private MessageObject contact;
    private ImageView avatar;
    private CircularImageView circleAvatar;
    private TextView firstLetter;
    private ImageView callButton;
    private ImageView infoButton;
    private TextView name;
    private TextView number;

    public DetailDialog(Activity context, int threadId, int mesCount) {
        super(context);
        this.context = context;
        this.threadId = threadId;
        this.mesCount = mesCount;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        setContentView(R.layout.dialog_detail);

        db = MyApplication.getDb();

        okButton = (TextView) findViewById(R.id.ok_button);
        avatar = (ImageView) findViewById(R.id.avatar);
        circleAvatar = (CircularImageView) findViewById(R.id.circle_avatar);
        firstLetter = (TextView) findViewById(R.id.first_letter);
        callButton = (ImageView) findViewById(R.id.call_btn);
        infoButton = (ImageView) findViewById(R.id.info_btn);
        name = (TextView) findViewById(R.id.name);
        number = (TextView) findViewById(R.id.number);

        contact = db.getFirstMessageByThreadId(threadId);
        contactId = Constant.getContactId(context, contact.getAddress());

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

        name.setText(contact.getPerson()+ " (" + mesCount + ")");
        number.setText(contact.getAddress());

        Constant.increaseHitArea(callButton);
        Constant.increaseHitArea(infoButton);

        okButton.setOnClickListener(this);
        callButton.setOnClickListener(this);
        infoButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ok_button:
                dismiss();
                break;
            case R.id.call_btn:
                TelephonyManager telMgr = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
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
                        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                            ActivityCompat.requestPermissions(context,
                                    new String[]{Manifest.permission.CALL_PHONE}, 2);
                            return;
                        } else {
                            context.startActivity(intent);
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
                    context.startActivity(intent);
                } else {
                    Toast.makeText(context, "No information", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    private void showAlertDialog() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(context);
        dialog.setMessage(context.getResources().getString(R.string.sim_error));
        dialog.show();
    }
}
