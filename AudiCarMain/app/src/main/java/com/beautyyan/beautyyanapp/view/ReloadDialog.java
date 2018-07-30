package com.beautyyan.beautyyanapp.view;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.beautyyan.beautyyanapp.R;
import com.beautyyan.beautyyanapp.listener.ReloadListener;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by xuelu on 2017/5/18.
 */

public class ReloadDialog extends Dialog {

    @Bind(R.id.reload_btn)
    TextView reloadBtn;

    private  ReloadListener listener;

    public ReloadDialog(Context context, ReloadListener listener) {
        super(context);
        this.listener = listener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reload_dialog);
        ButterKnife.bind(this);
        setCancelable(false);
        reloadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.reLoad();
            }
        });
    }
}
