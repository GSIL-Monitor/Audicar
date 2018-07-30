package xmpp.constant;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

public class MyApplication {
	private static Application instance;
	public static SharedPreferences sharedPreferences;

	public static Application getInstance() {
		return instance;
	}

	public static void setInstance(Application app) {
		instance = app;
		sharedPreferences = app.getSharedPreferences(Constants.SHARED_PREFERENCES, Context.MODE_PRIVATE);
	}
}
