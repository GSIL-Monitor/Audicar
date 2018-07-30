package com.beautyyan.beautyyanapp.view;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.beautyyan.beautyyanapp.R;
import com.beautyyan.beautyyanapp.listener.PriceRangeDiaListener;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by xuelu on 2017/6/16.
 */

public class PriceRangeDialog extends Dialog implements View.OnClickListener {
    @Bind(R.id.cancel_img)
    ImageView cancelImg;
    @Bind(R.id.tv0)
    TextView tv0;
    @Bind(R.id.gou0)
    ImageView gou0;
    @Bind(R.id.tv1)
    TextView tv1;
    @Bind(R.id.gou1)
    ImageView gou1;
    @Bind(R.id.tv2)
    TextView tv2;
    @Bind(R.id.gou2)
    ImageView gou2;
    @Bind(R.id.tv3)
    TextView tv3;
    @Bind(R.id.gou3)
    ImageView gou3;
    @Bind(R.id.tv4)
    TextView tv4;
    @Bind(R.id.gou4)
    ImageView gou4;
    @Bind(R.id.tv5)
    TextView tv5;
    @Bind(R.id.gou5)
    ImageView gou5;
    @Bind(R.id.tv6)
    TextView tv6;
    @Bind(R.id.gou6)
    ImageView gou6;
    @Bind(R.id.btn_finish)
    Button btnFinish;
    private Context context;
    private PriceRangeDiaListener listener;
    private int which;
    private String str = "";
    private int whichBefore;

    public PriceRangeDialog(Context context, int which, PriceRangeDiaListener listener) {
        super(context, R.style.price_range_dialog);
        getWindow().setGravity(Gravity.BOTTOM);
        this.listener = listener;
        this.context = context;
        this.which = which;
        this.whichBefore = which;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.price_range_dialog);
        ButterKnife.bind(this);
        selectT(which);
        cancelImg.setOnClickListener(this);
        tv0.setOnClickListener(this);
        tv1.setOnClickListener(this);
        tv2.setOnClickListener(this);
        tv3.setOnClickListener(this);
        tv4.setOnClickListener(this);
        tv5.setOnClickListener(this);
        tv6.setOnClickListener(this);
        btnFinish.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.cancel_img:
                dismiss();
                break;
            case R.id.tv0:
                selectT(0);
                str = tv0.getText().toString();
                break;
            case R.id.tv1:
                selectT(1);
                str = tv1.getText().toString();
                break;
            case R.id.tv2:
                selectT(2);
                str = tv2.getText().toString();
                break;
            case R.id.tv3:
                selectT(3);
                str = tv3.getText().toString();
                break;
            case R.id.tv4:
                selectT(4);
                str = tv4.getText().toString();
                break;
            case R.id.tv5:
                selectT(5);
                str = tv5.getText().toString();
                break;
            case R.id.tv6:
                selectT(6);
                str = tv6.getText().toString();
                break;
            case R.id.btn_finish:
                dismiss();
                if (whichBefore == which) {
                    return;
                }
                listener.clickOk(which, str);
                break;
            default:
                break;


        }
    }

    public void setWhich(int which) {
        this.which = which;
        this.whichBefore = which;
        selectT(which);
    }

    private void selectT(int w) {
        which = w;
        Drawable drawable0 = context.getResources().getDrawable((w == 0 ? R.mipmap.price_icon_selected : R.mipmap.price_icon_unselected));
        drawable0.setBounds(0, 0, drawable0.getMinimumWidth(), drawable0.getMinimumHeight());

        Drawable drawable1 = context.getResources().getDrawable((w == 1 ? R.mipmap.price_icon_selected : R.mipmap.price_icon_unselected));
        drawable1.setBounds(0, 0, drawable1.getMinimumWidth(), drawable1.getMinimumHeight());

        Drawable drawable2 = context.getResources().getDrawable((w == 2 ? R.mipmap.price_icon_selected : R.mipmap.price_icon_unselected));
        drawable2.setBounds(0, 0, drawable2.getMinimumWidth(), drawable2.getMinimumHeight());

        Drawable drawable3 = context.getResources().getDrawable((w == 3? R.mipmap.price_icon_selected : R.mipmap.price_icon_unselected));
        drawable3.setBounds(0, 0, drawable3.getMinimumWidth(), drawable3.getMinimumHeight());

        Drawable drawable4 = context.getResources().getDrawable((w == 4 ? R.mipmap.price_icon_selected : R.mipmap.price_icon_unselected));
        drawable4.setBounds(0, 0, drawable4.getMinimumWidth(), drawable4.getMinimumHeight());

        Drawable drawable5 = context.getResources().getDrawable((w == 5 ? R.mipmap.price_icon_selected : R.mipmap.price_icon_unselected));
        drawable5.setBounds(0, 0, drawable5.getMinimumWidth(), drawable5.getMinimumHeight());

        Drawable drawable6 = context.getResources().getDrawable((w == 6 ? R.mipmap.price_icon_selected : R.mipmap.price_icon_unselected));
        drawable6.setBounds(0, 0, drawable6.getMinimumWidth(), drawable6.getMinimumHeight());


        tv0.setTextColor(context.getResources().getColor(w == 0 ? R.color.red_used : R.color.text_black_4));
        tv0.setCompoundDrawables(drawable0, null, null, null);
        gou0.setVisibility(w == 0 ?  View.VISIBLE : View.GONE);

        tv1.setTextColor(context.getResources().getColor(w == 1 ? R.color.red_used : R.color.text_black_4));
        tv1.setCompoundDrawables(drawable1, null, null, null);
        gou1.setVisibility(w == 1 ?  View.VISIBLE : View.GONE);

        tv2.setTextColor(context.getResources().getColor(w == 2 ? R.color.red_used : R.color.text_black_4));
        tv2.setCompoundDrawables(drawable2, null, null, null);
        gou2.setVisibility(w == 2 ?  View.VISIBLE : View.GONE);

        tv3.setTextColor(context.getResources().getColor(w == 3 ? R.color.red_used : R.color.text_black_4));
        tv3.setCompoundDrawables(drawable3, null, null, null);
        gou3.setVisibility(w == 3 ?  View.VISIBLE : View.GONE);

        tv4.setTextColor(context.getResources().getColor(w == 4 ? R.color.red_used : R.color.text_black_4));
        tv4.setCompoundDrawables(drawable4, null, null, null);
        gou4.setVisibility(w == 4 ?  View.VISIBLE : View.GONE);

        tv5.setTextColor(context.getResources().getColor(w == 5 ? R.color.red_used : R.color.text_black_4));
        tv5.setCompoundDrawables(drawable5, null, null, null);
        gou5.setVisibility(w == 5 ?  View.VISIBLE : View.GONE);

        tv6.setTextColor(context.getResources().getColor(w == 6 ? R.color.red_used : R.color.text_black_4));
        tv6.setCompoundDrawables(drawable6, null, null, null);
        gou6.setVisibility(w == 6 ?  View.VISIBLE : View.GONE);
    }
}
