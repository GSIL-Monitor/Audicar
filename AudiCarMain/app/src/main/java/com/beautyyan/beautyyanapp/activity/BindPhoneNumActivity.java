package com.beautyyan.beautyyanapp.activity;

import android.app.ActionBar;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.admaster.jicesdk.api.JiceConfig;
import com.admaster.jicesdk.api.JiceSDK;
import com.beautyyan.beautyyanapp.R;
import com.beautyyan.beautyyanapp.http.HttpResponseListener;
import com.beautyyan.beautyyanapp.http.RequestHelp;
import com.beautyyan.beautyyanapp.http.ToBeanHelp;
import com.beautyyan.beautyyanapp.http.bean.CaptchDetil;
import com.beautyyan.beautyyanapp.http.bean.User;
import com.beautyyan.beautyyanapp.utils.Constant;
import com.beautyyan.beautyyanapp.utils.LogUtil;
import com.beautyyan.beautyyanapp.utils.MD5Util;
import com.beautyyan.beautyyanapp.utils.ToastUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Timer;

import butterknife.Bind;
import butterknife.ButterKnife;
import xmpp.constant.Constants;
import xmpp.util.HideSoftInputHelperTool;
import xmpp.util.ImageUtil;
import xmpp.xmpp.XmppConnection;


/**
 * Created by xuelu on 2017/3/22.
 */

public class BindPhoneNumActivity
        extends
        BaseActivity
        implements
        View.OnClickListener
{
	
	@Bind (R.id.phone_num_et)
	EditText			   phoneNumEt;
	@Bind (R.id.check_code_btn)
	Button				   checkCodeBtn;
	@Bind (R.id.check_code_et)
	EditText			   checkCodeEt;
	@Bind (R.id.login_btn)
	Button				   loginBtn;
	@Bind (R.id.ll_third)
	LinearLayout		   llThird;
	@Bind (R.id.textView)
	TextView			   textView;
	@Bind (R.id.code_pic_et)
	EditText			   codePicEt;
	@Bind (R.id.code_pic_img)
	ImageView			   codePicImg;
	@Bind (R.id.qq_img)
	ImageView			   qqImg;
	@Bind (R.id.weixin_img)
	ImageView			   weixinImg;
	@Bind (R.id.weibo_img)
	ImageView			   weiboImg;
	private String		   platAccId;
						   
	private boolean		   isRun	= false;
	/**
	 * 超时时间 单位秒
	 */
	private int			   timeout	= 5;
									
	private Handler		   handler	= new Handler();
	private Timer		   timer;
	private String		   phoneNum;
						   
	private ProgressDialog mdialog;
	private User		   user;
	private String		   platType	= "";
    private String         md5Str;
									
	@Override
	protected void onDestroy() {
		HideSoftInputHelperTool.hideSoftInputBoard(checkCodeEt, BindPhoneNumActivity.this);
		isRun = false;
		super.onDestroy();
		ButterKnife.unbind(this);
		if (timer != null)
		{
			timer.cancel();
			timer = null;
		}
	}

    private boolean checkPicCodeIsCorrect() {
        String codePicStr = codePicEt.getText().toString().toUpperCase();
        codePicStr = MD5Util.getMD5String(codePicStr);
        if (!codePicStr.equals(md5Str)) {
			requestCaptcha();
            showToast("请输入正确的图片验证码");
            return false;
        }
        return true;
    }
	
	@Override
	protected void bindButterKnife() {
		ButterKnife.bind(this);
	}
	
	@Override
	protected void initView() {
		setContentView(R.layout.activity_login);
		setTitleName("绑定手机");
		setTitleBgColor(0xffffffff);
		setTitleTxtColor(0xff000000);
		setBackImageRes(R.mipmap.back_bg_black);
		setViewAutoBgColor(getResources().getColor(R.color.no_color));
		loginBtn.setText("绑定并登录");
		llThird.setVisibility(View.GONE);
        requestCaptcha();
	}
	
	@Override
	protected void initData() {
		platAccId = getIntent().getStringExtra("platAccId");
		platType = getIntent().getStringExtra("platType");
	}
	
	@Override
	protected void initListener() {
        codePicImg.setOnClickListener(this);
		loginBtn.setOnClickListener(this);
		checkCodeBtn.setOnClickListener(this);
        checkCodeEt.addTextChangedListener(new CheckCodeEtListener());
        phoneNumEt.addTextChangedListener(new PhoneNumEtListener());
        codePicEt.addTextChangedListener(new CaPicCodeListener());
	}
	
	private class CheckCodeEtListener
	        implements
	        TextWatcher
	{
		
		@Override
		public void beforeTextChanged(CharSequence s, int start, int count, int after) {
		
		}
		
		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {
		
		}
		
		@Override
		public void afterTextChanged(Editable s) {
			String str = phoneNumEt.getText().toString().trim();
			String str1 = codePicEt.getText().toString().trim();
			String str2 = checkCodeEt.getText().toString().trim();
			if (str.length() < 11 || "".equals(str1) || "".equals(str2))
			{
				loginBtn.setEnabled(false);
				loginBtn.setTextColor(getResources().getColor(R.color.button_unselected_text_color));
				return;
			}
			loginBtn.setEnabled(true);
			loginBtn.setTextColor(getResources().getColor(R.color.button_selected_text_color));
		}
	}
	
	private class PhoneNumEtListener
	        implements
	        TextWatcher
	{
		
		@Override
		public void beforeTextChanged(CharSequence s, int start, int count, int after) {
		
		}
		
		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {
		
		}
		
		@Override
		public void afterTextChanged(Editable s) {
			checkSendCodeBtnIsEnabled();
		}
	}
	
	private class CaPicCodeListener
	        implements
	        TextWatcher
	{
		
		@Override
		public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
		
		}
		
		@Override
		public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
		
		}
		
		@Override
		public void afterTextChanged(Editable editable) {
			checkSendCodeBtnIsEnabled();
		}
	}

    private void requestCaptcha() {
        RequestHelp.getCaptcha(this, new Captcha());
    }

    private class Captcha
            implements
            HttpResponseListener
    {

        @Override
        public void onSuccess(String data, String message) throws JSONException {
            CaptchDetil captchDetil = ToBeanHelp.captchBean(data);
            String imageString = captchDetil.getImageFlow();
            Bitmap bitmap = ImageUtil.getBitmapFromBase64String(imageString);
            codePicImg.setImageBitmap(bitmap);
            md5Str = captchDetil.getSercertCode();

        }

        @Override
        public void onFail(String data, int errorCode, String message) throws JSONException {

        }
    }
	
	private void checkSendCodeBtnIsEnabled() {
		String str = phoneNumEt.getText().toString().trim();
		String str1 = codePicEt.getText().toString().trim();
		String str2 = checkCodeEt.getText().toString().trim();
		if (str.length() > 11)
		{
			phoneNumEt.setText(str.substring(0, 11));
			phoneNumEt.setSelection(str.length() - 1);
		}
		if (str.length() >= 11 && !str1.equals("") && !str2.equals(""))
		{
			loginBtn.setEnabled(true);
			loginBtn.setTextColor(getResources().getColor(R.color.button_selected_text_color));
		}
		else
		{
			loginBtn.setEnabled(false);
			loginBtn.setTextColor(getResources().getColor(R.color.button_unselected_text_color));
		}
		if (isRun) { return; }
		if (str.length() >= 11 && !str1.equals(""))
		{
			checkCodeBtn.setEnabled(true);
			checkCodeBtn.setTextColor(getResources().getColor(R.color.button_selected_text_color));
		}
		
		else
		{
			checkCodeBtn.setEnabled(false);
			checkCodeBtn.setTextColor(getResources().getColor(R.color.button_unselected_text_color));
		}
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
            case R.id.code_pic_img:
                requestCaptcha();
                break;
			case R.id.login_btn:
				phoneNum = phoneNumEt.getText().toString().trim();
				if (!Constant.getInstance().isMobile(phoneNum))
				{
					showToast(getResources().getString(R.string.input_correct_phone_number));
					return;
				}
                if (!checkPicCodeIsCorrect()) {
                    return;
                }
				//                if (UserDbHelper.getInstance(YuYuanApp.getIns().getApplicationContext()).getUser() != null)
				//                {
				//                    LogUtil.i("userId :" + UserDbHelper.getInstance(YuYuanApp.getIns().getApplicationContext()).getUser().getUserId());
				//                    LogUtil.i("userPhone :" + UserDbHelper.getInstance(LoginActivity.this).getUser().getPhone());
				//                }
				//                User user = Constant.getInstance().getUser();
				//                user.setPhone("1234");
				//                user.setUserId(32);
				//                UserDbHelper.getInstance(YuYuanApp.getIns().getApplicationContext()).addUser(user);
				
				if (mdialog == null)
				{
					mdialog = ProgressDialog.show(this, null, getResources().getString(R.string.dialog_load_content));
					mdialog.setCancelable(false);
					//						mdialog.setContentView(R.layout.dialog_loadding);
					mdialog.getWindow().setLayout(ActionBar.LayoutParams.WRAP_CONTENT, ActionBar.LayoutParams.WRAP_CONTENT);
					mdialog.setIndeterminateDrawable(getResources().getDrawable(R.drawable.progress_dialog_style));
				}
				else
				{
					mdialog.show();
				}
				HideSoftInputHelperTool.hideSoftInputBoard(checkCodeEt, this);
				phoneNum = phoneNumEt.getText().toString().trim();
				String codeStr = checkCodeEt.getText().toString().trim();
				RequestHelp.validCode(this, new VaildCode(), phoneNum, codeStr, "");
				
				//                loginAccount("450586", Constants.PWD);
				//                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
				////                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
				//                startActivity(intent);
				//                finish();
				break;
			case R.id.check_code_btn:
				String phoneNum = phoneNumEt.getText().toString().trim();
				if ("".equals(phoneNum))
				{
					showToast(getString(R.string.phone_number_cannot_be_empty));
					return;
				}
				if (!Constant.getInstance().isMobile(phoneNum))
				{
					showToast(getString(R.string.input_correct_phone_number));
					return;
				}
				if (!checkPicCodeIsCorrect()) {
					return;
				}
				RequestHelp.sendCode(this, new SendCode(), phoneNum, "");
				break;
			default:
				break;
		}
	}
	
	private void startTimeUp() {
		Thread thread = new Thread(new Runnable()
		{
			@Override
			public void run() {
				for (int i = 60; i > 0; i--)
				{
					if (!isRun) { return; }
					final int time = i;
					handler.post(new Runnable()
					{
						@Override
						public void run() {
							checkCodeBtn.setText(time + "s");
						}
					});
					try
					{
						Thread.sleep(1000);
					}
					catch (InterruptedException e)
					{
						e.printStackTrace();
					}
				}
				handler.post(new Runnable()
				{
					@Override
					public void run() {
						checkCodeBtn.setEnabled(true);
						checkCodeBtn.setText(getString(R.string.get_check_code));
						checkCodeBtn.setTextColor(0xffffffff);
					}
				});
				isRun = false;
			}
		});
		thread.start();
	}
	
	private class SendCode
	        implements
	        HttpResponseListener
	{
		
		@Override
		public void onSuccess(String data, String message) throws JSONException {
			ToastUtils.showShort(BindPhoneNumActivity.this, "验证码发送成功");
			checkCodeBtn.setEnabled(false);
			checkCodeBtn.setTextColor(getResources().getColor(R.color.button_unselected_text_color));
			isRun = true;
			startTimeUp();
		}
		
		@Override
		public void onFail(String data, int errorCode, String message) throws JSONException {
			ToastUtils.showShort(BindPhoneNumActivity.this, "验证码发送失败");
		}
	}
	
	private class VaildCode
	        implements
	        HttpResponseListener
	{
		
		@Override
		public void onSuccess(String data, String message) throws JSONException {
			RequestHelp.bindPhone(BindPhoneNumActivity.this, new BindAndLogin(), platType, platAccId, phoneNum);
		}
		
		@Override
		public void onFail(String data, int errorCode, String message) throws JSONException {
			ToastUtils.showShort(BindPhoneNumActivity.this, message);
			mdialog.dismiss();
		}
	}
	
	private class BindAndLogin
	        implements
	        HttpResponseListener
	{
		
		@Override
		public void onSuccess(String data, String message) throws JSONException {
			//                mdialog.dismiss();
			//                Constant.getInstance().setUser(user);
			//                Intent intent = new Intent();
			//                intent.putExtra("userName", user.getPhone());
			//                intent.putExtra("userId", user.getUserId());
			//                setResult(0, intent);
			//                HideSoftInputHelperTool.hideSoftInputBoard(checkCodeEt, LoginActivity.this);
			//                finish();
			user = ToBeanHelp.userBean(data);
			new Thread(new Runnable()
			{
				@Override
				public void run() {
					final boolean isSuccess = XmppConnection.getInstance().login(user.getUserId(), Constants.PWD);
					handler.post(new Runnable()
					{
						@Override
						public void run() {
							if (isSuccess && !isFinishing())
							{
								Constant.getInstance().setXmppHasLogined(true);
								loginSuccess(user);
							}
							else if (!isFinishing())
							{
								new Thread(new Runnable()
								{
									@Override
									public void run() {
										final boolean isSuccess = XmppConnection.getInstance().login(user.getPhone(), Constants.PWD);
										handler.post(new Runnable()
										{
											@Override
											public void run() {
												if (isSuccess && !isFinishing())
												{
													loginSuccess(user);
													Constant.getInstance().setXmppHasLogined(true);
												}
												else if (!isFinishing())
												{
													loginSuccess(user);
//													ToastUtils.showCenterShort(BindPhoneNumActivity.this, "客服模块登录异常");
													LogUtil.i("xmpp login failed !");
												}
												
											}
										});
									}
								}).start();
								//                    XmppConnection.getInstance().closeConnection();
								//                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
								//                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
								//                    startActivity(intent);
								//                    finish();
							}
						}
					});
					
				}
			}).start();
			
			// loginAccount(String.valueOf(user.getUserId()), Constants.PWD);
		}
		
		@Override
		public void onFail(String data, int errorCode, String message) throws JSONException {
			mdialog.dismiss();
			ToastUtils.showCenterShort(BindPhoneNumActivity.this, "登录失败");
		}
	}
	
	private void loginSuccess(User user) {
		MainActivity.getInstance().handler.sendEmptyMessage(Constant.MESSAGE.REFRESH_MINE);
		mdialog.dismiss();
		Constant.getInstance().setUser(user);
		Intent intent = new Intent();
		intent.putExtra("userName", user.getPhone());
		intent.putExtra("userId", user.getUserId());
        setResult(0, intent);
		finish();
		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject.put("name", user.getPhone());
		} catch (JSONException e) {
			e.printStackTrace();
		}
		JiceSDK.getInstance(getApplicationContext(), new JiceConfig()).trackRegisterEvent(user.getUserId(), jsonObject);
		JiceSDK.getInstance(getApplicationContext(), new JiceConfig()).trackLoginEvent(user.getUserId(), jsonObject);
    }


}
