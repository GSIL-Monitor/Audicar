package com.beautyyan.beautyyanapp.view;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import com.beautyyan.beautyyanapp.R;
import com.beautyyan.beautyyanapp.listener.MapChooseListener;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by xuelu on 2017/6/16.
 */

public class MapChooseDialog extends Dialog implements View.OnClickListener {

    @Bind(R.id.cancel_txt)
    TextView cancelTxt;
    @Bind(R.id.baidu)
    TextView baidu;
    @Bind(R.id.gaode)
    TextView gaode;
    @Bind(R.id.tercent)
    TextView tercent;
    @Bind(R.id.baidu_view)
    View baiduView;
    @Bind(R.id.gaode_view)
    View gaodeView;
    @Bind(R.id.tercent_view)
    View tercentView;

    private MapChooseListener listener;

    public MapChooseDialog(Context context, MapChooseListener listener) {
        super(context, R.style.price_range_dialog);
        getWindow().setGravity(Gravity.BOTTOM);
        this.listener = listener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_map_choose);
        ButterKnife.bind(this);
        initListener();
    }

    private void initListener() {
        cancelTxt.setOnClickListener(this);
        baidu.setOnClickListener(this);
        gaode.setOnClickListener(this);
        tercent.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        dismiss();
        switch (v.getId()) {
            case R.id.cancel_txt:
                break;
            case R.id.baidu:
                listener.clickBaiduMap();
                break;
            case R.id.gaode:
                listener.clickGaodeMap();
                break;
            case R.id.tercent:
                listener.clickTercentMap();
                break;
            default:
                break;
        }

    }

    public void setBaiduGone() {
        baidu.setVisibility(View.GONE);
        baiduView.setVisibility(View.GONE);
        setBg();
    }

    public void setGaodeGone() {
        gaode.setVisibility(View.GONE);
        gaodeView.setVisibility(View.GONE);
        setBg();
    }

    public void setTercentGone() {
        tercent.setVisibility(View.GONE);
        tercentView.setVisibility(View.GONE);
        setBg();
    }

    private void setBg() {
        if (tercent.getVisibility() == View.VISIBLE) {
            tercent.setBackgroundResource(R.drawable.map_choose_bg2);
        }
        else if (gaode.getVisibility() == View.VISIBLE) {
            gaode.setBackgroundResource(R.drawable.map_choose_bg2);
        }
        else if (baidu.getVisibility() == View.VISIBLE) {
            baidu.setBackgroundResource(R.drawable.map_choose_bg2);
        }
    }

}
