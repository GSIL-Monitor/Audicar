package com.beautyyan.beautyyanapp.view;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import com.beautyyan.beautyyanapp.R;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by xuelu on 2017/10/10.
 */

public class RegulationIntroDialog extends Dialog {
    @Bind(R.id.cancel_img)
    ImageView cancelImg;

    public RegulationIntroDialog(Context context) {
        super(context, R.style.regular_intro_dialog);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_regulation_intro);
        ButterKnife.bind(this);
        cancelImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.y = -320;
        getWindow().setAttributes(lp);
    }


    @Override
    public void dismiss() {
        super.dismiss();
        ButterKnife.unbind(this);
    }
}
