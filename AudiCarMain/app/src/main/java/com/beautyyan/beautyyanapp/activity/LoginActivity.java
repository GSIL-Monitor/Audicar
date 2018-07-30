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
import android.widget.TextView;
import android.widget.Toast;

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
import com.beautyyan.beautyyanapp.utils.Util;
import com.umeng.socialize.UMAuthListener;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.UMShareConfig;
import com.umeng.socialize.bean.SHARE_MEDIA;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;
import java.util.Timer;

import butterknife.Bind;
import butterknife.ButterKnife;
import xmpp.constant.Constants;
import xmpp.util.HideSoftInputHelperTool;
import xmpp.util.ImageUtil;
import xmpp.util.XmppLoadThread;
import xmpp.xmpp.XmppConnection;


/**
 * Created by xuelu on 2017/3/22.
 */

public class LoginActivity
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
	@Bind (R.id.qq_img)
	ImageView			   qqImg;
	@Bind (R.id.weibo_img)
	ImageView			   weiboImg;
	@Bind (R.id.weixin_img)
	ImageView			   weixinImg;
	@Bind (R.id.textView)
	TextView			   textView;
	@Bind (R.id.code_pic_img)
	ImageView			   codePicImg;
	@Bind (R.id.code_pic_et)
	EditText               codePicEt;

	private String         md5Str;

	private boolean		   isRun		 = false;
	/**
	 * 超时时间 单位秒
	 */
	private int			   timeout		 = 5;

	private Handler		   handler		 = new Handler();
	private Timer		   timer;
	private String		   phoneNum;

	private ProgressDialog mdialog;
	private User		   user;
	private String		   platformType	 = "";
	private String		   platformAccId = "";

	@Override
	protected void onDestroy() {
		HideSoftInputHelperTool.hideSoftInputBoard(checkCodeEt, LoginActivity.this);
		isRun = false;
		super.onDestroy();
		ButterKnife.unbind(this);
		if (timer != null)
		{
			timer.cancel();
			timer = null;
		}
	}

	@Override
	protected void bindButterKnife() {
		ButterKnife.bind(this);
	}

	@Override
	protected void initView() {
		setContentView(R.layout.activity_login);
		setTitleName(getString(R.string.title_login));
		setTitleBgColor(0xffffffff);
		setTitleTxtColor(0xff000000);
		setBackImageRes(R.mipmap.back_bg_black);
		setViewAutoBgColor(getResources().getColor(R.color.no_color));
		requestCaptcha();
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

	@Override
	protected void initData() {
		UMShareConfig config = new UMShareConfig();
		config.setSinaAuthType(UMShareConfig.AUTH_TYPE_SSO);
		UMShareAPI.get(LoginActivity.this).setShareConfig(config);
	}

	private UMAuthListener umAuthListener = new UMAuthListener()
	{
		@Override
		public void onStart(SHARE_MEDIA platform) {
			//授权开始的回调
		}

		@Override
		public void onComplete(SHARE_MEDIA platform, int action, Map<String, String> data) {
			Toast.makeText(getApplicationContext(), "授权成功", Toast.LENGTH_SHORT).show();
			if (data == null) { return; }
			String platType = "";
			String platAccId = "";
			if (platform == SHARE_MEDIA.SINA)
			{ // 新浪微博
				platType = "SINA";
				platAccId = data.get("uid");
			}
			else if (platform == SHARE_MEDIA.QQ)
			{ // QQ
				platType = "QQ";
				platAccId = data.get("uid");
			}
			else if (platform == SHARE_MEDIA.WEIXIN)
			{ // 微信
				platType = "WECHAT";
				platAccId = data.get("openid");
			}
			if (mdialog == null)
			{
				mdialog = ProgressDialog.show(LoginActivity.this, null, getResources().getString(R.string.dialog_load_content));
				mdialog.setCancelable(false);
				//						mdialog.setContentView(R.layout.dialog_loadding);
				mdialog.getWindow().setLayout(ActionBar.LayoutParams.WRAP_CONTENT, ActionBar.LayoutParams.WRAP_CONTENT);
				mdialog.setIndeterminateDrawable(getResources().getDrawable(R.drawable.progress_dialog_style));
			}
			else
			{
				mdialog.show();
			}
			platformType = platType;
			platformAccId = platAccId;
			RequestHelp.bindAccount(LoginActivity.this, new ThirdWayLogin(), platType, platAccId);

		}

		@Override
		public void onError(SHARE_MEDIA platform, int action, Throwable t) {
			LogUtil.i(t.toString() + "action" + action);
			Toast.makeText(getApplicationContext(), "授权失败", Toast.LENGTH_SHORT).show();
		}

		@Override
		public void onCancel(SHARE_MEDIA platform, int action) {
			Toast.makeText(getApplicationContext(), "取消授权", Toast.LENGTH_SHORT).show();
		}
	};

	@Override
	protected void initListener() {
		codePicImg.setOnClickListener(this);
		loginBtn.setOnClickListener(this);
		checkCodeBtn.setOnClickListener(this);
		qqImg.setOnClickListener(this);
		weixinImg.setOnClickListener(this);
		weiboImg.setOnClickListener(this);
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

	private class CaPicCodeListener implements TextWatcher {

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

	private void checkSendCodeBtnIsEnabled() {
		String str = phoneNumEt.getText().toString().trim();
		String str1 = codePicEt.getText().toString().trim();
		String str2 = checkCodeEt.getText().toString().trim();
		if (str.length() > 11)
		{
			phoneNumEt.setText(str.substring(0, 11));
			phoneNumEt.setSelection(str.length() - 1);
		}
		if (str.length() >= 11 && !str1.equals("") && !str2.equals("")) {
			loginBtn.setEnabled(true);
			loginBtn.setTextColor(getResources().getColor(R.color.button_selected_text_color));
		}
		else {
			loginBtn.setEnabled(false);
			loginBtn.setTextColor(getResources().getColor(R.color.button_unselected_text_color));
		}
		if (isRun) { return; }
		if (str.length() >= 11 && !str1.equals(""))
		{
			checkCodeBtn.setEnabled(true);
			checkCodeBtn.setTextColor(getResources().getColor(R.color.button_selected_text_color));
		}

		else {
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
					showToast(getResources().getString(R.string.phone_number_cannot_be_empty));
					return;
				}
				if (!Constant.getInstance().isMobile(phoneNum))
				{
					showToast(getResources().getString(R.string.input_correct_phone_number));
					return;
				}
				if (!checkPicCodeIsCorrect()) {
					return;
				}
				RequestHelp.sendCode(this, new SendCode(), phoneNum, "");
				break;
			case R.id.qq_img:
				boolean isauthQq = UMShareAPI.get(LoginActivity.this).isAuthorize(LoginActivity.this, SHARE_MEDIA.QQ);
				if (isauthQq)
				{
					UMShareAPI.get(LoginActivity.this).deleteOauth(LoginActivity.this, SHARE_MEDIA.QQ, null);
				}
				UMShareAPI.get(LoginActivity.this).doOauthVerify(LoginActivity.this, SHARE_MEDIA.QQ, umAuthListener);

				break;
			case R.id.weibo_img:
				boolean isauthSina = UMShareAPI.get(LoginActivity.this).isAuthorize(LoginActivity.this, SHARE_MEDIA.SINA);
				if (isauthSina)
				{
					UMShareAPI.get(LoginActivity.this).deleteOauth(LoginActivity.this, SHARE_MEDIA.SINA, null);
				}
				UMShareAPI.get(LoginActivity.this).doOauthVerify(LoginActivity.this, SHARE_MEDIA.SINA, umAuthListener);

				break;
			case R.id.weixin_img:
				boolean isInstall = UMShareAPI.get(LoginActivity.this).isInstall(LoginActivity.this, SHARE_MEDIA.WEIXIN);
				if (!isInstall)
				{
					ToastUtils.showShort(LoginActivity.this, "请安装微信");
					return;
				}
				boolean isauthWeixin = UMShareAPI.get(LoginActivity.this).isAuthorize(LoginActivity.this, SHARE_MEDIA.WEIXIN);
				if (isauthWeixin)
				{
					UMShareAPI.get(LoginActivity.this).deleteOauth(LoginActivity.this, SHARE_MEDIA.WEIXIN, null);

				}
				UMShareAPI.get(LoginActivity.this).doOauthVerify(LoginActivity.this, SHARE_MEDIA.WEIXIN, umAuthListener);
				return;

			default:
				break;
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
							if (checkCodeBtn == null) {
								return;
							}
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
						if (checkCodeBtn == null) {
							return;
						}
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
			ToastUtils.showShort(LoginActivity.this, "验证码发送成功");
			checkCodeBtn.setEnabled(false);
			checkCodeBtn.setTextColor(getResources().getColor(R.color.button_unselected_text_color));
			isRun = true;
			startTimeUp();
		}

		@Override
		public void onFail(String data, int errorCode, String message) throws JSONException {
			ToastUtils.showShort(LoginActivity.this, "验证码发送失败");
		}
	}

	private class VaildCode
			implements
			HttpResponseListener
	{

		@Override
		public void onSuccess(String data, String message) throws JSONException {
			RequestHelp.login(LoginActivity.this, new Login(), phoneNum);
		}

		@Override
		public void onFail(String data, int errorCode, String message) throws JSONException {
			ToastUtils.showShort(LoginActivity.this, message);
			mdialog.dismiss();
		}
	}

	private class Login
			implements
			HttpResponseListener
	{

		@Override
		public void onSuccess(String data, String message) throws JSONException {
			user = ToBeanHelp.userBean(data);
			if (user != null)
			{
				new Thread(new Runnable()
				{
					@Override
					public void run() {
						final boolean isSuccess = XmppConnection.getInstance().login(String.valueOf(user.getUserId()), Constants.PWD);
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
									new Thread(new Runnable()
									{
										@Override
										public void run() {
											final boolean isSuccess = XmppConnection.getInstance().login(String.valueOf(user.getPhone()), Constants.PWD);
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
//														ToastUtils.showCenterShort(LoginActivity.this, "客服模块登录异常");
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
		}

		@Override
		public void onFail(String data, int errorCode, String message) throws JSONException {
			mdialog.dismiss();
			ToastUtils.showCenterShort(LoginActivity.this, "登录失败");
		}
	}

	private void loginSuccess(User user) {
		Constant.getInstance().setUser(user);
		MainActivity.getInstance().handler.sendEmptyMessage(Constant.MESSAGE.REFRESH_MINE);
		mdialog.dismiss();
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


	private void loginAccount(final String userName, final String password) {
		new XmppLoadThread(this)
		{

			@Override
			protected Object load() {
				//                timer = new Timer();
				//                TimerTask timerTask = new TimerTask() {
				//                    @Override
				//                    public void run() {
				//                        timeout--;
				//                        if (timeout == 0) {
				//                            handler.post(new Runnable() {
				//                                @Override
				//                                public void run() {
				//                                    timer.cancel();
				//                                    result(false);
				//                                    mdialog.dismiss();
				//                                    XmppConnecionListener xmLis = XmppConnection.getInstance().getConnectLis();
				//                                    if (xmLis != null) {
				//                                        XmppConnection.getInstance().getConnection().removeConnectionListener(xmLis);
				//                                    }
				//                                }
				//                            });
				//                        }
				//                    }
				//                };
				//                timer.schedule(timerTask, 1000, 1000);
				LogUtil.i("stop login xmpp!");
				boolean isSuccess = XmppConnection.getInstance().login(userName, password);
				//                if (isSuccess) {
				//                    Constants.USER_NAME = name;
				//                    Constants.PWD = password;
				//                }
				return isSuccess;
			}

			@Override
			protected void result(Object o) {
				boolean isSuccess = (Boolean) o;
				if (isSuccess)
				{
					mdialog.dismiss();
					Constant.getInstance().setUser(user);
					Intent intent = new Intent();
					intent.putExtra("userName", user.getPhone());
					intent.putExtra("userId", user.getUserId());
					setResult(0, intent);
					HideSoftInputHelperTool.hideSoftInputBoard(checkCodeEt, LoginActivity.this);
					finish();
				}
				else
				{
					mdialog.dismiss();
					showToast(getResources().getString(R.string.login_error));
					LogUtil.i("xmpp login failed !");
					//                    XmppConnection.getInstance().closeConnection();
					//                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
					//                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
					//                    startActivity(intent);
					//                    finish();
				}
			}

		};
	}

	private class ThirdWayLogin
			implements
			HttpResponseListener
	{

		@Override
		public void onSuccess(String data, String message) throws JSONException {
			user = ToBeanHelp.userBean(data);
			if (user == null) { return; }
			if (Util.isEmpty(user.getPhone()))
			{
				Intent intent = new Intent(LoginActivity.this, BindPhoneNumActivity.class);
				intent.putExtra("userId", user.getUserId());
				intent.putExtra("platType", platformType);
				intent.putExtra("platAccId", platformAccId);
				startActivity(intent);
				mdialog.dismiss();
				finish();
				return;
			}
			//                mdialog.dismiss();
			//                Constant.getInstance().setUser(user);
			//                Intent intent = new Intent();
			//                intent.putExtra("userName", user.getPhone());
			//                intent.putExtra("userId", user.getUserId());
			//                setResult(0, intent);
			//                HideSoftInputHelperTool.hideSoftInputBoard(checkCodeEt, LoginActivity.this);
			new Thread(new Runnable()
			{
				@Override
				public void run() {
					final boolean isSuccess = XmppConnection.getInstance().login(String.valueOf(user.getUserId()), Constants.PWD);
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
								new Thread(new Runnable()
								{
									@Override
									public void run() {
										final boolean isSuccess = XmppConnection.getInstance().login(String.valueOf(user.getPhone()), Constants.PWD);
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
//													ToastUtils.showCenterShort(LoginActivity.this, "客服模块登录异常");
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
			ToastUtils.showShort(LoginActivity.this, "登录失败");
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		UMShareAPI.get(this).onActivityResult(requestCode, resultCode, data);

	}

}
