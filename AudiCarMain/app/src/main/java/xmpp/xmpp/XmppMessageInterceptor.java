package xmpp.xmpp;

import android.content.Intent;
import android.util.Log;

import org.jivesoftware.smack.PacketInterceptor;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;

import xmpp.constant.Constants;
import xmpp.constant.MyApplication;
import xmpp.dao.MsgDbHelper;
import xmpp.model.ChatItem;
import xmpp.util.DateUtil;
import xmpp.util.FileUtil;


public class XmppMessageInterceptor implements PacketInterceptor {

	@Override
	public void interceptPacket(Packet packet) {
		Message nowMessage = (Message) packet;
		if(Constants.IS_DEBUG)
			Log.e("xmppchat send2", nowMessage.toXML());
		if (nowMessage.getType() == Message.Type.groupchat || nowMessage.getType() == Message.Type.chat) {
			String chatName = "";
			String userName = "";
			int chatType = ChatItem.CHAT;
			//name
			if (nowMessage.getType() == Message.Type.groupchat) {
				chatName = XmppConnection.getRoomName(nowMessage.getTo());
				userName = nowMessage.getTo();
				chatType = ChatItem.GROUP_CHAT;
			}
			else {
				chatName = userName = XmppConnection.getUsername(nowMessage.getTo());
			}
			//type
			// 记录我们发出去的消息
			String msgBody = null;
			if(nowMessage.getMtype()!=null && !"chat".equals(nowMessage.getMtype())){
				if(nowMessage.getMtype().toString().equals("sendVoice"))
					msgBody = Constants.SAVE_SOUND_PATH + "/" + nowMessage.getProperty("filename");
				else if(nowMessage.getMtype().toString().equals("sendPicFile")) {
					msgBody = Constants.SAVE_IMG_PATH + "/" + nowMessage.getProperty("filename");
				}
			}
			else if (nowMessage.getType() == Message.Type.groupchat & nowMessage.getBody().contains(":::")) { //被迫的
				String[] msgAndData = nowMessage.getBody().split(":::");
				if(FileUtil.getType(msgAndData[0]) == FileUtil.SOUND)
					msgBody = Constants.SAVE_SOUND_PATH + "/" + msgAndData[0];
				else
					msgBody = Constants.SAVE_IMG_PATH + "/" + msgAndData[0];
			}
			else
				msgBody = nowMessage.getBody();

			if (nowMessage.getBody().contains("[RoomChange")) {
				System.out.println("房间要发生改变了");
			}
			else {
				if (userName != null) {
					userName = userName.toLowerCase();
					chatName = userName;
				}
				ChatItem msg = new ChatItem(chatType,chatName,userName, "", msgBody, DateUtil.now_MM_dd_HH_mm_ss(), 1, nowMessage.getProperty("shopname").toString());
				MsgDbHelper.getInstance(MyApplication.getInstance()).saveChatMsg(msg);
				Intent intent = new Intent(Constants.CHAT_NEW_MSG);
				intent.putExtra("isFromsend", true);
				MyApplication.getInstance().sendBroadcast(intent);
			}
		}
	}
}
