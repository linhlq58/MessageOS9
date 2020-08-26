package com.linhleeproject.mymessage.messengeros10.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.RelativeLayout;

import com.linhleeproject.mymessage.messengeros10.R;

/**
 * Created by lequy on 12/14/2016.
 */

public class LanguageDialog extends Dialog implements View.OnClickListener {
    private Activity context;
    private LanguageClicked listener;

    private RelativeLayout engLayout;
    private RelativeLayout viLayout;

    public LanguageDialog(Activity context, LanguageClicked listener) {
        super(context);
        this.context = context;
        this.listener = listener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        setContentView(R.layout.dialog_language);

        engLayout = (RelativeLayout) findViewById(R.id.eng_layout);
        viLayout = (RelativeLayout) findViewById(R.id.vi_layout);

        engLayout.setOnClickListener(this);
        viLayout.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.eng_layout:
                listener.engClicked();
                dismiss();
                break;
            case R.id.vi_layout:
                listener.viClicked();
                dismiss();
                break;
        }
    }

    public interface LanguageClicked {
        void engClicked();
        void viClicked();
    }
}
