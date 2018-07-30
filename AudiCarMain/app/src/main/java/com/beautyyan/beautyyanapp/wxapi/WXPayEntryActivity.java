package com.beautyyan.beautyyanapp.wxapi;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.beautyyan.beautyyanapp.utils.Constant;
import com.beautyyan.beautyyanapp.utils.LogUtil;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.umeng.analytics.MobclickAgent;

public class WXPayEntryActivity extends Activity implements IWXAPIEventHandler {
	
	private static final String TAG = "MicroMsg.SDKSample.WXPayEntryActivity";

    private IWXAPI api;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.pay_result);
		api = WXAPIFactory.createWXAPI(this, Constant.getInstance().getWxPayId());
		api.handleIntent(getIntent(), this);
    }

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		setIntent(intent);
        api.handleIntent(intent, this);
	}

	@Override
	public void onReq(BaseReq req) {
	}

	@Override
	public void onResp(BaseResp baseResp) {
		Intent intent = new Intent(Constant.BroadCastCode.code1);
		intent.putExtra("result", baseResp.errCode);
		sendBroadcast(intent);
		finish();

		LogUtil.i("wxpayerror : " + baseResp.errCode);
//		int result = 0;
//		String msg = "支付失败";
//
//		switch (baseResp.errCode) {
//			case BaseResp.ErrCode.ERR_OK:
//				msg = "支付成功";
//				break;
//			case BaseResp.ErrCode.ERR_USER_CANCEL:
//				msg = "已取消";
//				break;
//			case BaseResp.ErrCode.ERR_AUTH_DENIED:
//				msg = "授权失败";
//				break;
//			case BaseResp.ErrCode.ERR_UNSUPPORT:
//				msg = "微信不支持";
//				break;
//			default:
//				break;
//		}
//
//		if (msg.length() > 0) {
//			ToastUtils.showShort(this, msg);
//
//		}
	}

	@Override
	protected void onStop() {
		super.onStop();
		if (!Constant.getInstance().isAppOnForeground()) {
			Constant.getInstance().setActive(false);
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
		if (!Constant.getInstance().isActive()) {
			Constant.getInstance().setActive(true);
			Constant.getInstance().getAudiBi(WXPayEntryActivity.this);
		}
	}

}