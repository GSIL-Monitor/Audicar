package com.beautyyan.beautyyanapp.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import com.beautyyan.beautyyanapp.R;
import com.beautyyan.beautyyanapp.YuYuanApp;
import com.beautyyan.beautyyanapp.listener.LettersBgListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * ListView右侧的字母索引View
 */
public class SideBar extends View {

    public static String[] INDEX_STRING = {"A", "B", "C", "D", "E", "F", "G", "H", "I"
            };

    private OnTouchingLetterChangedListener onTouchingLetterChangedListener;
    private LettersBgListener lettersBgListener;
    private List<String> letterList;
    private int choose = -1;
    private Paint paint = new Paint();
    private TextView mTextDialog;
    private int h = 0;
    public int singleHeight;
    public final int RATE = 30;
    public SideBar(Context context) {
        this(context, null);
    }

    public SideBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SideBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        setBackgroundColor(Color.parseColor("#00000000"));
        letterList = Arrays.asList(INDEX_STRING);
    }

    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int height = getHeight();// 获取对应高度
        int width = getWidth();// 获取对应宽度
        singleHeight  = height / RATE;// 获取每一个字母的高度
        for (int i = 0; i < letterList.size(); i++) {
            paint.setColor(Color.parseColor("#666666"));
            paint.setTypeface(Typeface.DEFAULT);
            paint.setAntiAlias(true);
            paint.setTextSize(getResources().getDimension(R.dimen.sp11_text));
            // 选中的状态
            if (i == choose) {
//                paint.setColor(Color.parseColor("#4F41FD"));
                paint.setFakeBoldText(true);
            }
            // x坐标等于中间-字符串宽度的一半.
            float xPos = width / 2 - paint.measureText(letterList.get(i)) / 2;
            float yPos = height / 2 - singleHeight * letterList.size() / 2 + singleHeight * i + singleHeight * 2 /3;
            canvas.drawText(letterList.get(i), xPos, yPos, paint);
            paint.reset();// 重置画笔
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        final int action = event.getAction();
        final float y = event.getY();// 点击y坐标
        final int oldChoose = choose;
        final OnTouchingLetterChangedListener listener = onTouchingLetterChangedListener;
        final int c = (int) ((y - getHeight() / 2 + singleHeight * letterList.size() / 2) /  singleHeight);// 点击y坐标所占总高度的比例*b数组的长度就等于点击b中的个数.

        switch (action) {
            case MotionEvent.ACTION_UP:
                YuYuanApp.getIns().handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        lettersBgListener.gone();
                    }
                }, 300);
                choose = -1;
                invalidate();
                if (mTextDialog != null) {
                    YuYuanApp.getIns().handler.postDelayed(runnable, 600);
                }
                break;
            default:
                YuYuanApp.getIns().handler.removeCallbacks(runnable);
                if (oldChoose != c) {
                    if (c >= 0 && c < letterList.size()) {
                        if (listener != null) {
                            listener.onTouchingLetterChanged(letterList.get(c));
                        }
                        if (mTextDialog != null) {
                            mTextDialog.setText(letterList.get(c));
                            mTextDialog.setVisibility(View.VISIBLE);
                            lettersBgListener.visible();
                        }
                        choose = c;
                        invalidate();
                    }
                }
                break;
        }
        return true;
    }

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
//            new Thread(new Runnable() {
//                @Override
//                public void run() {
//                    setmTextDialogGone();
//                }
//            }).start();
            mTextDialog.setVisibility(View.GONE);
        }
    };


    private synchronized void setmTextDialogGone() {
        for(int i = 0; i < 21; i++) {
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            final int d = i;
            YuYuanApp.getIns().handler.post(new Runnable() {
                @Override
                public void run() {
                    mTextDialog.setAlpha((20 - d) / 20f);

                }
            });
        }
    }

    public void setIndexText(ArrayList<String> indexStrings) {
        this.letterList = indexStrings;
        invalidate();
    }

    /**
     * 为SideBar设置显示当前按下的字母的TextView
     *
     * @param mTextDialog
     */
    public void setTextView(TextView mTextDialog) {
        this.mTextDialog = mTextDialog;
    }

    /**
     * 向外公开的方法
     *
     * @param onTouchingLetterChangedListener
     */
    public void setOnTouchingLetterChangedListener(
            OnTouchingLetterChangedListener onTouchingLetterChangedListener) {
        this.onTouchingLetterChangedListener = onTouchingLetterChangedListener;
    }

    public void setLettersBgListener(LettersBgListener listener) {
        this.lettersBgListener = listener;
    }

    /**
     * 接口
     */
    public interface OnTouchingLetterChangedListener {
        void onTouchingLetterChanged(String s);
    }

}