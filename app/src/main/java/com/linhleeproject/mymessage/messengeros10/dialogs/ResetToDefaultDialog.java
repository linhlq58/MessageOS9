package com.linhleeproject.mymessage.messengeros10.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.linhleeproject.mymessage.messengeros10.R;

/**
 * Created by Linh Lee on 12/7/2016.
 */
public class ResetToDefaultDialog extends Dialog {
    private Activity context;
    private ButtonClicked listener;

    private TextView okButton;
    private TextView cancelButton;

    public ResetToDefaultDialog(Activity context, ButtonClicked listener) {
        super(context);
        this.context = context;
        this.listener = listener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        setContentView(R.layout.dialog_reset_to_default);

        okButton = (TextView) findViewById(R.id.ok_button);
        cancelButton = (TextView) findViewById(R.id.cancel_button);

        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.okClicked();
                dismiss();
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
    }

    public interface ButtonClicked {
        void okClicked();
    }
}
