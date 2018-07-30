package xmpp.xmpp;

import android.content.Intent;
import android.util.Log;

import com.beautyyan.beautyyanapp.YuYuanApp;
import com.beautyyan.beautyyanapp.utils.ToastUtils;

import org.jivesoftware.smack.ConnectionListener;

import xmpp.constant.Constants;
import xmpp.constant.MyApplication;
import xmpp.util.MyAndroidUtil;


public class XmppConnecionListener implements ConnectionListener {

	@Override
	public void connectionClosed() {
		Log.e("smack xmpp", "close");
		XmppConnection.getInstance().setNull();
		new Thread(new Runnable() {
			@Override
			public void run() {
				XmppConnection.getInstance().closeConnection();
				XmppConnection.getInstance().login(Constants.USER_NAME, Constants.PWD);
			}
		}).start();
	}

	@Override
	public void connectionClosedOnError(Exception e) {
		if(e.getMessage().contains("conflict")){
			ToastUtils.showShort(YuYuanApp.getIns().getApplicationContext(), "聊天功能断开连接，请重新登录");
			MyAndroidUtil.removeXml(Constants.LOGIN_PWD);
			if (!MyApplication.sharedPreferences.getBoolean(Constants.LOGIN_CHECK, false)) {
				MyAndroidUtil.removeXml(Constants.LOGIN_ACCOUNT);
			}
//			Constants.USER_NAME = "";
			Constants.loginUser = null;
			XmppConnection.getInstance().closeConnection();
			MyApplication.getInstance().sendBroadcast(new Intent("conflict"));

//			Intent intent = new Intent();
//			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//			intent.putExtra("isRelogin", true);
//			intent.setClass(MyApplication.getInstance(), LoginActivity.class);
//			MyApplication.getInstance().startActivity(intent);
		}
	}

	@Override
	public void reconnectingIn(int seconds) {
	}

	@Override
	public void reconnectionSuccessful() {
		XmppConnection.getInstance().loadFriendAndJoinRoom();
	}

	@Override
	public void reconnectionFailed(Exception e) {
	}

	

}
