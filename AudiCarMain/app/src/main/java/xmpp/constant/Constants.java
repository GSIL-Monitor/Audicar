package xmpp.constant;


import android.os.Environment;

import xmpp.model.User;
import xmpp.util.Util;

import static com.beautyyan.beautyyanapp.http.Api.SERVER_NAME;

public class Constants {
	public final static boolean IS_DEBUG = true;


	public final static String SERVER_URL = "http://"+SERVER_NAME+":5222/plugins/xmppservice/";

	//端口号
	public final static int SERVER_PORT = 5222;
	//图片文件和声音文件保存路径
	public final static String PATH =  Environment.getExternalStorageDirectory().getAbsolutePath()+"/xmpp";
	public final static String SAVE_IMG_PATH = PATH + "/images";//设置保存图片文件的路径
	public final static String SAVE_SOUND_PATH = PATH + "/sounds";//设置声音文件的路径
	public final static String SHARED_PREFERENCES = "openfile";
	public final static String LOGIN_CHECK = "check";
	public final static String LOGIN_ACCOUNT = "account";
	public final static String LOGIN_PWD = "pwd";	
	//URL
	public final static String URL_EXIT_ROOM = SERVER_URL+"exitroom";
	public final static String URL_EXIST_ROOM = SERVER_URL+"existroom";

	//openfire账号
	public static String USER_NAME = "admin";
	//openfire密码
	public static String PWD = "000000";
	public static User loginUser;

	public final static String REFRESH_MSGACTIVITY = "refresh_msgactivity";
	public final static String CHAT_NEW_MSG = "chat_new_msg";

	public final static String PIC_FROM_URL_PATH =  Util.getInstance().getExtPath()+"/urlpic";
	public final static String VOICE_FROM_URL = "voice_from_url";
}
