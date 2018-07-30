package xmpp.util;

import android.app.Activity;
import android.content.Context;
import android.os.IBinder;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.beautyyan.beautyyanapp.utils.LogUtil;

//软键盘隐
public class HideSoftInputHelperTool {

	public static void hide(Activity ctx, MotionEvent ev) {
		if (ev.getAction() == MotionEvent.ACTION_DOWN) {

			// 获得当前得到焦点的View，一般情况下就是EditText（特殊情况就是轨迹求或�?实体案件会移动焦点）
			View v = ctx.getCurrentFocus();

			if (isShouldHideInput(v, ev)) {
				hideSoftInput(ctx, v.getWindowToken());
			}
		}

	}

	/**
	 * 根据EditText坐标和用户点击的坐标相对比，来判断是否隐藏键盘，因为当用户点击EditText时没必要隐藏
	 *
	 * @param v
	 * @param event
	 * @return
	 */
	private static boolean isShouldHideInput(View v, MotionEvent event) {
		if (v != null && (v instanceof EditText)) {
			int[] l = { 0, 0 };
			v.getLocationInWindow(l);
			int left = l[0], top = l[1], bottom = top + v.getHeight(), right = left + v.getWidth();
			if (event.getX() > left && event.getX() < right && event.getY() > top && event.getY() < bottom) {
				// 点击EditText的事件，忽略它
				return false;
			} else {
				return true;
			}
		}
		// 如果焦点不是EditText则忽略，这个发生在视图刚绘制完，第一个焦点不在EditView上，和用户用轨迹球�?择其他的焦点
		return false;
	}

	/**
	 * 多种隐藏软件盘方法的其中�
	 *
	 * @param token
	 */
	public static void hideSoftInput(Context ctx, IBinder token) {
		if (token != null) {
			InputMethodManager im = (InputMethodManager) ctx.getSystemService(Context.INPUT_METHOD_SERVICE);
			im.hideSoftInputFromWindow(token, InputMethodManager.HIDE_NOT_ALWAYS);
		}
	}

	public static void hideSoftInputBoard(View v, Context context)
	{
		InputMethodManager in = (InputMethodManager) context
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		if (isActive(context))
		{
			LogUtil.d("hideSoftInputBoard()");
			in.hideSoftInputFromWindow(v.getApplicationWindowToken(),
					InputMethodManager.HIDE_NOT_ALWAYS);
		}
	}

	public static boolean isActive(Context context) {
		InputMethodManager in = (InputMethodManager) context
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		return in.isActive();
	}

};
