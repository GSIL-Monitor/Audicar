package com.beautyyan.beautyyanapp.view;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.beautyyan.beautyyanapp.R;
import com.beautyyan.beautyyanapp.listener.MsgSubmitListener;
import com.beautyyan.beautyyanapp.utils.Util;

import butterknife.Bind;
import butterknife.ButterKnife;


/**
 * Created by xuelu on 2017/5/17.
 */

public class MsgDialog extends Dialog implements View.OnClickListener {


    @Bind(R.id.star_1)
    ImageView star1;
    @Bind(R.id.star_2)
    ImageView star2;
    @Bind(R.id.star_3)
    ImageView star3;
    @Bind(R.id.star_4)
    ImageView star4;
    @Bind(R.id.star_5)
    ImageView star5;
    @Bind(R.id.radio_group)
    RadioGroup radioGroup;
    @Bind(R.id.ll)
    LinearLayout ll;
    @Bind(R.id.submit_txt)
    TextView submitTxt;
    @Bind(R.id.close_img)
    ImageView closeImg;

    private int score = 0;
    private MsgSubmitListener listener;

    public MsgDialog(Context context, MsgSubmitListener listener) {
        super(context, R.style.goods_pop_dialog);
        this.listener = listener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.msg_dialog);
        ButterKnife.bind(this);
        getWindow().setLayout(Util.dip2px(getContext(), 320), Util.dip2px(getContext(), 300));
        initListener();
    }

    private void initListener() {
        star1.setOnClickListener(this);
        star2.setOnClickListener(this);
        star3.setOnClickListener(this);
        star4.setOnClickListener(this);
        star5.setOnClickListener(this);
        submitTxt.setOnClickListener(this);
        closeImg.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.star_1:
                score = 1;
                star1.setImageResource(R.mipmap.star_dialog_selected);
                star2.setImageResource(R.mipmap.star_dialog_unselected);
                star3.setImageResource(R.mipmap.star_dialog_unselected);
                star4.setImageResource(R.mipmap.star_dialog_unselected);
                star5.setImageResource(R.mipmap.star_dialog_unselected);
                break;
            case R.id.star_2:
                score = 2;
                star1.setImageResource(R.mipmap.star_dialog_selected);
                star2.setImageResource(R.mipmap.star_dialog_selected);
                star3.setImageResource(R.mipmap.star_dialog_unselected);
                star4.setImageResource(R.mipmap.star_dialog_unselected);
                star5.setImageResource(R.mipmap.star_dialog_unselected);
                break;
            case R.id.star_3:
                score = 3;
                star1.setImageResource(R.mipmap.star_dialog_selected);
                star2.setImageResource(R.mipmap.star_dialog_selected);
                star3.setImageResource(R.mipmap.star_dialog_selected);
                star4.setImageResource(R.mipmap.star_dialog_unselected);
                star5.setImageResource(R.mipmap.star_dialog_unselected);
                break;
            case R.id.star_4:
                score = 4;
                star1.setImageResource(R.mipmap.star_dialog_selected);
                star2.setImageResource(R.mipmap.star_dialog_selected);
                star3.setImageResource(R.mipmap.star_dialog_selected);
                star4.setImageResource(R.mipmap.star_dialog_selected);
                star5.setImageResource(R.mipmap.star_dialog_unselected);
                break;
            case R.id.star_5:
                score = 5;
                star1.setImageResource(R.mipmap.star_dialog_selected);
                star2.setImageResource(R.mipmap.star_dialog_selected);
                star3.setImageResource(R.mipmap.star_dialog_selected);
                star4.setImageResource(R.mipmap.star_dialog_selected);
                star5.setImageResource(R.mipmap.star_dialog_selected);
                break;
            case R.id.submit_txt:
                listener.click(score);
                dismiss();
                break;
            case R.id.close_img:
                dismiss();
                break;
        }
    }
}
