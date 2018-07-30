/**
 *
 */
package xmpp.activites;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.beautyyan.beautyyanapp.R;
import com.beautyyan.beautyyanapp.YuYuanApp;
import com.beautyyan.beautyyanapp.http.Api;
import com.beautyyan.beautyyanapp.http.HttpResponseListener;
import com.beautyyan.beautyyanapp.http.HttpsPostWithJSON;
import com.beautyyan.beautyyanapp.http.RequestHelp;
import com.beautyyan.beautyyanapp.http.ToBeanHelp;
import com.beautyyan.beautyyanapp.listener.MsgSubmitListener;
import com.beautyyan.beautyyanapp.utils.Constant;
import com.beautyyan.beautyyanapp.utils.LogUtil;
import com.beautyyan.beautyyanapp.utils.ToastUtils;
import com.beautyyan.beautyyanapp.view.MsgDialog;

import org.json.JSONException;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.Bind;
import butterknife.ButterKnife;
import xmpp.adapter.ChatAdapter;
import xmpp.constant.Constants;
import xmpp.constant.MyApplication;
import xmpp.d3View.D3Activity;
import xmpp.d3View.D3View;
import xmpp.d3View.MyListView;
import xmpp.d3View.RecordButton;
import xmpp.d3View.expression.ExpressionListener;
import xmpp.d3View.expression.ExpressionView;
import xmpp.dao.MsgDbHelper;
import xmpp.dao.NewMsgDbHelper;
import xmpp.model.ChatItem;
import xmpp.util.FileUtil;
import xmpp.util.Tool;
import xmpp.util.sreenshot.CropImage;
import xmpp.xmpp.XmppConnection;


/**
 * @author MZH
 */
@SuppressLint("NewApi")
public class ChatActivity extends D3Activity {
    @D3View(click = "onClick")
    ImageView expandBtn;
    @D3View
    View viewBg;
    @D3View
    LinearLayout editLayout;
    @D3View(click = "onClick")
    ImageView voiceImg;
    @D3View(click = "onClick")
    ImageView moreBtn;
    @D3View(click = "onClick")
    LinearLayout takePicBtn;
    @D3View(click = "onClick")
    LinearLayout chosePicBtn;
    @D3View(click = "onClick")
    TextView sendBtn;
    @D3View
    LinearLayout moreLayout;
    @D3View
    RecordButton recordBtn;
    @D3View
    ExpressionView expView;
    @D3View
    EditText msgText;
    @D3View
    MyListView listView;
    @D3View
    TextView title_text;
    @D3View(click = "onClick")
    View back_btn;
    @Bind(R.id.view_auto)
    View viewAuto;
    private Bitmap mBitmap;
    private ChatAdapter adapter;
    private List<ChatItem> chatItems = new ArrayList<ChatItem>();
    private UpMessageReceiver mUpMessageReceiver;
    private String chatName;   //群的时候是群名称
    private int chatType = ChatItem.CHAT;
    //	public LocationClient mLocationClient;
    public static boolean isExit = false;
    private NewMsgReceiver newMsgReceiver;
    public List<ChatItem> lastMsgs = new ArrayList<ChatItem>();
    private MsgDbHelper msgDbHelper;
    private String name;
    private MediaPlayer mp;
    private boolean isAudiHelper;
    private static ChatActivity ins;

    @Override
    protected void onCreate(Bundle arg0) {
        if (null != arg0) {
            arg0 = null;
        }
        super.onCreate(arg0);
        ins = this;
        setContentView(R.layout.acti_chat);
        ButterKnife.bind(this);
//        YuYuanApp.getIns().setStatusBarForDetail(this, viewAuto);
        newMsgReceiver = new NewMsgReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction("showDialog");
        registerReceiver(newMsgReceiver, filter);
        isAudiHelper = getIntent().getBooleanExtra("is_audi_helper", false);
        chatName = getIntent().getStringExtra("chatName");
        RequestHelp.addLinkman(ChatActivity.this, new HttpResponseListener() {
            @Override
            public void onSuccess(String data, String message) throws JSONException {

            }

            @Override
            public void onFail(String data, int errorCode, String message) throws JSONException {

            }
        }, Constant.getInstance().getUser().getUserId(), chatName);
        chatName = chatName.toLowerCase();
        name = getIntent().getStringExtra("name");
        chatType = ChatItem.CHAT;
        initView();
        initData();
        title_text.setText(name);
//		mLocationClient = new LocationClient(this.getApplicationContext());
    }

    public static ChatActivity getIns() {
        if (ins == null) {
            ins = new ChatActivity();
        }
        return ins;
    }

    private class NewMsgReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            new MsgDialog(ChatActivity.this, new MsgSubmitListener() {
                @Override
                public void click(int score) {
                    RequestHelp.submitScore(ChatActivity.this, new SubmitScore(), name, Constant.getInstance().getUser().getUserId(), chatName, score);
                    ToastUtils.showCenterShort(ChatActivity.this, "感谢您的评价，祝您生活愉快！");
                }
            }).show();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    private void initView() {
        msgDbHelper = MsgDbHelper.getInstance(this);
        adapter = new ChatAdapter(ChatActivity.this, chatName, isAudiHelper);
        listView.setAdapter(adapter);
		listView.setonRefreshListener(new MyListView.OnRefreshListener() {

			@Override
			public void onRefresh() {
				new Thread(new Runnable() {
					@Override
					public void run() {
						try {
							Thread.sleep(1000);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						handler.sendEmptyMessage(0);
					}
				}).start();
			}
		});

        msgText.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                expView.setVisibility(View.GONE);
            }
        });
        msgText.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if ("".equals(msgText.getText().toString())) {
                    setShowGone(true);
                } else
                    setShowGone(false);
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                // TODO Auto-generated method stub
            }

            @Override
            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub
            }
        });

        recordBtn.setOnFinishedRecordListener(new RecordButton.OnFinishedRecordListener() {
            @Override
            public void onFinishedRecord(final String audioPath, int time) {
                if (audioPath != null) {
                        if (!judgeIsLogined()) {
                            return;
                        }
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                String[] dataUrl = {null};
                                try {
                                    String result = HttpsPostWithJSON.postData(Api.POST_DATA_URL, audioPath);
                                    if (result != null) {
                                        dataUrl[0] = ToBeanHelp.getDataUrl(result);
                                        if (audioPath != null) {
                                            try {
                                                XmppConnection.getInstance().sendMsgWithParms(dataUrl[0],
                                                        new String[]{"sendVoice"}, new Object[]{dataUrl[0]}, chatType, getVoiceLength(audioPath), name,FileUtil.getFileName(audioPath));
                                            } catch (Exception e) {
                                                autoSendIfFail(dataUrl[0], new String[]{"sendVoice"}, new Object[]{dataUrl[0]}, getVoiceLength(audioPath), FileUtil.getFileName(audioPath));
                                                e.printStackTrace();
                                            }
                                        }
                                    }
                                    else {
                                        YuYuanApp.getIns().handler.post(new Runnable() {
                                            @Override
                                            public void run() {
                                                ToastUtils.showShort(ChatActivity.this, "语音发送失败");
                                            }
                                        });
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }).start();


                } else {
                    Tool.initToast(ChatActivity.this, "发送失败");
                }

            }
        });
        expView.setEditText(msgText);
        expView.setGifListener(new ExpressionListener() {
            @Override
            public void clickGif(String msg) {
                try {
                    XmppConnection.getInstance().sendMsg(msg, chatType, name);
                } catch (Exception e) {
                    autoSendIfFail(msg);
                    e.printStackTrace();
                }
            }
        });
        // 会话内容改变，接受广播
        mUpMessageReceiver = new UpMessageReceiver();
        registerReceiver(mUpMessageReceiver, new IntentFilter(Constants.CHAT_NEW_MSG));
        registerReceiver(mUpMessageReceiver, new IntentFilter("LeaveRoom"));
        if (Constant.getInstance().isXmppHasLogined()) {
            XmppConnection.getInstance().setRecevier(chatName, chatType);
        }

    }

    private int getVoiceLength(String file) {
        final MediaPlayer mp = getMediaPlayer();
        mp.reset();
        try {
            mp.setDataSource(file);
            mp.prepare();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return mp.getDuration()/1000 == 0 ? 1 : mp.getDuration()/1000;
    }

    private MediaPlayer getMediaPlayer()
    {
        synchronized (ChatAdapter.class)
        {
            if (null == mp)
            {
                mp = new MediaPlayer();
            }
            return mp;
        }

    }

    private void setShowGone(boolean isEmpty) {
        sendBtn.setVisibility(isEmpty ? View.GONE : View.VISIBLE);
        expandBtn.setVisibility(isEmpty ? View.VISIBLE : View.GONE);
    }

    private void initData() {
        chatItems = MsgDbHelper.getInstance(getApplicationContext()).getChatMsg(chatName);
        adapter.clear();
        adapter.addAll(chatItems);
        listView.setSelection(adapter.getCount() + 1);
    }

    private class SubmitScore implements HttpResponseListener {

        @Override
        public void onSuccess(String data, String message) throws JSONException {
        }

        @Override
        public void onFail(String data, int errorCode, String message) throws JSONException {

        }
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 0) {
                listView.onRefreshComplete();
                List<ChatItem> moreChatItems = MsgDbHelper.getInstance(getApplicationContext()).getChatMsgMore(
                        listView.getCount() - 1, chatName);
                for (int i = 0; i < moreChatItems.size(); i++) {
                    chatItems.add(i, moreChatItems.get(i));
                }
                adapter.clear();
                adapter.addAll(chatItems);
                adapter.notifyDataSetChanged();
            }
        }
    };


    public void onClick(View v) {
        int i = v.getId();
        switch (i) {
            case R.id.back_btn:
                finish();
                break;
            case R.id.rightBtn:
                Intent intent = new Intent();
                if (chatType == ChatItem.CHAT) {
                    intent.setClass(getApplicationContext(), FriendActivity.class);
                    intent.putExtra("username", chatName);
                    startActivity(intent);
                } else if (chatType == ChatItem.GROUP_CHAT) {
                    intent.setClass(getApplicationContext(), RoomMemActivity.class);
                    intent.putExtra("roomName", chatName);
                    startActivity(intent);
                }
                break;
            case R.id.expandBtn:
                if (moreLayout.getVisibility() == View.GONE) {
                    InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputMethodManager.hideSoftInputFromWindow(getCurrentFocus()
                            .getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                    moreLayout.setVisibility(View.VISIBLE);
                    expView.setVisibility(View.GONE);
                } else {
                    moreLayout.setVisibility(View.GONE);
                }
                break;
            case R.id.sendBtn:
                if (!judgeIsLogined()) {
                    return;
                }
                String msg = msgText.getText().toString();

//                if (Util.containsEmoji(msg)) {
//                    ToastUtils.showShort(YuYuanApp.getIns(), "暂不支持表情发送");
//                    return;
//                }
                if (!msg.isEmpty()) {
                    try {
                        XmppConnection.getInstance().sendMsg(msg, chatType, name);
                    } catch (Exception e) {
                        autoSendIfFail(msg);
                        e.printStackTrace();
                    }
                    msgText.setText("");
                } else if (msgText.getVisibility() == View.GONE) { //文本为空，从语音输入模式切换到文本输入模式
                    msgText.setVisibility(View.VISIBLE);
                    recordBtn.setVisibility(View.GONE);
                }
                break;
            case R.id.msgText:
                expView.setVisibility(View.GONE);
                listView.setSelection(adapter.getCount());
                break;
            case R.id.voiceImg:
                if (ContextCompat.checkSelfPermission(ChatActivity.this, Manifest.permission.RECORD_AUDIO)
                        != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, 1);
                    return;
                }

                if (ContextCompat.checkSelfPermission(ChatActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 2);
                    return;
                }

                if (recordBtn.getVisibility() == View.VISIBLE) {
                    voiceImg.setImageResource(R.drawable.msg_voice_selector);
                    if (msgText.getVisibility() == View.GONE) {
                        msgText.setVisibility(View.VISIBLE);
                        recordBtn.setVisibility(View.GONE);
                        if ("".equals(msgText.getText().toString()))
                            setShowGone(true);
                        else
                            setShowGone(false);
                    }
                    return;
                }
                voiceImg.setImageResource(R.mipmap.icon_keyboard);

                msgText.setVisibility(View.GONE);
                recordBtn.setVisibility(View.VISIBLE);
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(msgText.getWindowToken(), 0);
                break;
            case R.id.takePicBtn:
                if (ContextCompat.checkSelfPermission(ChatActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 3);
                    return;
                }
                if (ContextCompat.checkSelfPermission(ChatActivity.this, Manifest.permission.CAMERA)
                        != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 5);
                    return;
                }
                Intent intent1 = new Intent();
                CropImageActivity.isAutoSend = true;
                intent1.setClass(this, PicSrcPickerActivity.class);
                intent1.putExtra("type", PicSrcPickerActivity.TAKE_PIC);
                startActivityForResult(intent1, PicSrcPickerActivity.CROP);
                break;
            case R.id.chosePicBtn:
                if (ContextCompat.checkSelfPermission(ChatActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 4);
                    return;
                }
                Intent intent2 = new Intent();
                CropImageActivity.isAutoSend = true;
                intent2.setClass(this, PicSrcPickerActivity.class);
                intent2.putExtra("type", PicSrcPickerActivity.CHOSE_PIC);
                startActivityForResult(intent2, PicSrcPickerActivity.CROP);
                break;
            case R.id.adrBtn:
                break;
            case R.id.expBtn:
                if (expView.getVisibility() == View.GONE) {
                    InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputMethodManager.hideSoftInputFromWindow(getCurrentFocus()
                            .getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                    expView.setVisibility(View.VISIBLE);
                    moreLayout.setVisibility(View.GONE);
                } else {
                    expView.setVisibility(View.GONE);
                }
                //Determine the voice
                if (msgText.getVisibility() == View.GONE) {
                    msgText.setVisibility(View.VISIBLE);
                    recordBtn.setVisibility(View.GONE);
                    if ("".equals(msgText.getText().toString()))
                        setShowGone(true);
                    else
                        setShowGone(false);
                }
                break;
            default:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (RESULT_OK == resultCode) {
            LogUtil.i("recode : " );
            switch (requestCode) {
                case PicSrcPickerActivity.CROP:
                    final String path = data.getStringExtra("path");
                    if (!judgeIsLogined()) {
                        return;
                    }
                    try {
                        mBitmap = createBitmap(path, Constant.getInstance().getDeviceWidth(), Constant.getInstance().getDeviceHeight());
                        if (mBitmap == null) {
                            Tool.initToast(this, "没有找到图片");
                            return;
                        }
                    } catch (Exception e) {
                        Tool.initToast(this, "没有找到图片");
                        return;
                    }
                    saveToLocal(mBitmap, PicSrcPickerActivity.img_path);



                    final String imgName = data.getStringExtra("imgName");
                    final String base64String = data.getStringExtra("base64String");
                    final String imgPath = data.getStringExtra("imgPath");
                    final String[] dataUrl = {null};
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                String result = HttpsPostWithJSON.postData(Api.POST_DATA_URL, path);
                                if (result != null) {
                                    dataUrl[0] = ToBeanHelp.getDataUrl(result);
                                    if (imgName != null) {
                                        try {
                                            XmppConnection.getInstance().sendMsgWithParms(dataUrl[0], new String[]{"sendPicFile"}, new Object[]{dataUrl[0]}, chatType, 0, name, imgName);
                                        } catch (Exception e) {
                                            autoSendIfFail(dataUrl[0], new String[]{"sendPicFile"}, new Object[]{dataUrl[0]}, 0, imgName);
                                            e.printStackTrace();
                                        }
                                    }
                                }
                                else {
                                    YuYuanApp.getIns().handler.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            ToastUtils.showShort(ChatActivity.this, "图片发送失败");
                                        }
                                    });
                                }
                                LogUtil.i("recode : " + result);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }).start();
                    break;

                default:
                    break;
            }


        }
    }

    public String saveToLocal(Bitmap bm ,String path)
    {
        try
        {
            FileOutputStream fos = new FileOutputStream(path);
            bm.compress(Bitmap.CompressFormat.JPEG, CropImage.COMPRESS, fos);
            fos.flush();
            fos.close();
        } catch (FileNotFoundException e)
        {
            e.printStackTrace();
            return null;
        } catch (IOException e)
        {
            e.printStackTrace();
            return null;
        }

        return path;
    }

    public Bitmap createBitmap(String path, int w, int h) {
        try {
            BitmapFactory.Options opts = new BitmapFactory.Options();
            opts.inJustDecodeBounds = true;
            // 这里是整个方法的关键，inJustDecodeBounds设为true时将不为图片分配内存。
            BitmapFactory.decodeFile(path, opts);
            int srcWidth = opts.outWidth;// 获取图片的原始宽度
            int srcHeight = opts.outHeight;// 获取图片原始高度
            int destWidth = 0;
            int destHeight = 0;
            // 缩放的比例
            double ratio = 0.0;
            if (srcWidth < w || srcHeight < h) {
                ratio = 0.0;
                destWidth = srcWidth;
                destHeight = srcHeight;
            } else if (srcWidth > srcHeight) {// 按比例计算缩放后的图片大小，maxLength是长或宽允许的最大长度
                ratio = (double) srcWidth / w;
                destWidth = w;
                destHeight = (int) (srcHeight / ratio);
            } else {
                ratio = (double) srcHeight / h;
                destHeight = h;
                destWidth = (int) (srcWidth / ratio);
            }
            BitmapFactory.Options newOpts = new BitmapFactory.Options();
            // 缩放的比例，缩放是很难按准备的比例进行缩放的，目前我只发现只能通过inSampleSize来进行缩放，其值表明缩放的倍数，SDK中建议其值是2的指数值
            newOpts.inSampleSize = (int) ratio + 1;
            // inJustDecodeBounds设为false表示把图片读进内存中
            newOpts.inJustDecodeBounds = false;
            // 设置大小，这个一般是不准确的，是以inSampleSize的为准，但是如果不设置却不能缩放
            newOpts.outHeight = destHeight;
            newOpts.outWidth = destWidth;
            // 获取缩放后图片
            return BitmapFactory.decodeFile(path, newOpts);
        } catch (Exception e) {
            // TODO: handle exception
            return null;
        }
    }

    private boolean judgeIsLogined() {
        if (!Constant.getInstance().isXmppHasLogined()) {
            ToastUtils.showCenterShort(ChatActivity.this, getResources().getString(R.string.xmpp_no_logined));
        }
        return (Constant.getInstance().isXmppHasLogined() &&
                XmppConnection.getInstance().getConnection().isAuthenticated());
    }

    @Override
    protected void onDestroy() {
        try {
            adapter.mping.stop();
            if (MyApplication.getInstance() != null) {
                unregisterReceiver(mUpMessageReceiver);
                unregisterReceiver(newMsgReceiver);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        clearMsgCount();
        finish();
        ins = null;
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        msgText.clearFocus();
        if (isExit) {
            isExit = false;
            finish();
        }
    }

    public void setViewBgGone(boolean isGone) {
        viewBg.setVisibility(isGone ? View.GONE : View.VISIBLE);
    }


    private void clearMsgCount() {
        NewMsgDbHelper newMsgDbHelper = NewMsgDbHelper.getInstance(this);
        int newCount = newMsgDbHelper.getMsgCount(chatName);
        if(newCount!=0){
            int value = YuYuanApp.getIns().getSp().getUnreadMsgCount() - 1;
            YuYuanApp.getIns().getSp().setUnreadMsgCount(value);
        }
        NewMsgDbHelper.getInstance(getApplicationContext()).delNewMsg(chatName);
        MyApplication.getInstance().sendBroadcast(new Intent(Constants.REFRESH_MSGACTIVITY));
    }

    private class UpMessageReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals("LeaveRoom")) {
                finish();
            } else {
                initData();
            }
        }
    }

    //下面是断线发不出内容时自动重发
    public static boolean isLeaving = false;

    public void autoSendIfFail(final String msg) {
//        Tool.initToast(MyApplication.getInstance(), "发送中..");
        final Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            int count = 0;

            @Override
            public void run() {
                try {
                    count++;
                    if (!isLeaving) {
                        XmppConnection.getInstance().setRecevier(chatName, chatType);
                        XmppConnection.getInstance().sendMsg(msg, chatType, name);
                        timer.cancel();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    Log.e("muc", "autosend      " + count);
                    if (count > 8) {
                        Tool.initToast(MyApplication.getInstance(), "发送失败");
                        timer.cancel();
                    }
                }
            }
        }, 1000, 1000);
    }

    public void autoSendIfFail(final String msg, final String[] s, final Object[] obj, final int voiceLength, final String filename) {
//        Tool.initToast(MyApplication.getInstance(), "发送中..");
        final Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            int count = 0;

            @Override
            public void run() {
                try {
                    count++;
                    if (!isLeaving) {
                        XmppConnection.getInstance().setRecevier(chatName, chatType);
                        XmppConnection.getInstance().sendMsgWithParms(msg, s, obj, chatType, voiceLength, name, filename);
                        timer.cancel();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    Log.e("muc", "autosend      " + count);
                    if (count > 8) {
                        Tool.initToast(MyApplication.getInstance(), "发送失败");
                        timer.cancel();
                    }
                }
            }
        }, 1000, 1000);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if (isShouldHideInput(v, ev)) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
            if (isShouldHideExp(ev)) {
                if (moreLayout.getVisibility() == View.VISIBLE) {
                    moreLayout.setVisibility(View.GONE);
                }
            }
            return super.dispatchTouchEvent(ev);
        }
        // 必不可少，否则所有的组件都不会有TouchEvent了
        if (getWindow().superDispatchTouchEvent(ev)) {
            return true;
        }
        return super.dispatchTouchEvent(ev);
    }

    public boolean isShouldHideInput(View v, MotionEvent event) {
        if (v != null && (v instanceof EditText)) {
            int[] leftTop = {0, 0};
            //获取输入框当前的location位置
            v.getLocationInWindow(leftTop);
            int left = 0;
            int top = leftTop[1];
            int bottom = top + v.getHeight();
            int right = Constant.getInstance().getDeviceWidth();
            if (event.getX() > left && event.getX() < right
                    && event.getY() > top && event.getY() < bottom) {
                // 点击的是输入框区域，保留点击EditText的事件
                return false;
            } else {
                return true;
            }
        }
        return false;
    }

    public boolean isShouldHideExp(MotionEvent event) {
        int[] leftTop = {0, 0};
        //获取输入框当前的location位置
        editLayout.getLocationInWindow(leftTop);
        int left = 0;
        int top = leftTop[1];
        int bottom = top + editLayout.getHeight();
        int right = Constant.getInstance().getDeviceWidth();
        if (event.getX() > left && event.getX() < right
                && event.getY() > top) {
            // 点击的是输入框区域，保留点击EditText的事件
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        switch (requestCode) {
            case 1:
                if (ContextCompat.checkSelfPermission(ChatActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 2);
                    return;
                }
                if (recordBtn.getVisibility() == View.VISIBLE) {
                    voiceImg.setImageResource(R.drawable.msg_voice_selector);
                    if (msgText.getVisibility() == View.GONE) {
                        msgText.setVisibility(View.VISIBLE);
                        recordBtn.setVisibility(View.GONE);
                        if ("".equals(msgText.getText().toString()))
                            setShowGone(true);
                        else
                            setShowGone(false);
                    }
                    return;
                }
                voiceImg.setImageResource(R.mipmap.icon_keyboard);

                msgText.setVisibility(View.GONE);
                recordBtn.setVisibility(View.VISIBLE);
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(msgText.getWindowToken(), 0);
                break;
            case 2:
                if (recordBtn.getVisibility() == View.VISIBLE) {
                    voiceImg.setImageResource(R.drawable.msg_voice_selector);
                    if (msgText.getVisibility() == View.GONE) {
                        msgText.setVisibility(View.VISIBLE);
                        recordBtn.setVisibility(View.GONE);
                        if ("".equals(msgText.getText().toString()))
                            setShowGone(true);
                        else
                            setShowGone(false);
                    }
                    return;
                }
                voiceImg.setImageResource(R.mipmap.icon_keyboard);

                msgText.setVisibility(View.GONE);
                recordBtn.setVisibility(View.VISIBLE);
                InputMethodManager imm2 = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm2.hideSoftInputFromWindow(msgText.getWindowToken(), 0);
                break;
            case 3:
                if (ContextCompat.checkSelfPermission(ChatActivity.this, Manifest.permission.CAMERA)
                        != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 5);
                    return;
                }
                Intent intent1 = new Intent();
                CropImageActivity.isAutoSend = true;
                intent1.setClass(this, PicSrcPickerActivity.class);
                intent1.putExtra("type", PicSrcPickerActivity.TAKE_PIC);
                startActivityForResult(intent1, PicSrcPickerActivity.CROP);
                break;
            case 4:
                Intent intent2 = new Intent();
                CropImageActivity.isAutoSend = true;
                intent2.setClass(this, PicSrcPickerActivity.class);
                intent2.putExtra("type", PicSrcPickerActivity.CHOSE_PIC);
                startActivityForResult(intent2, PicSrcPickerActivity.CROP);
                break;
            case 5:
                Intent intent3 = new Intent();
                CropImageActivity.isAutoSend = true;
                intent3.setClass(this, PicSrcPickerActivity.class);
                intent3.putExtra("type", PicSrcPickerActivity.TAKE_PIC);
                startActivityForResult(intent3, PicSrcPickerActivity.CROP);
                break;
            default:
                break;
        }
    }

}
