package xmpp.xmpp;

import android.content.Intent;
import android.util.Log;

import com.beautyyan.beautyyanapp.YuYuanApp;
import com.beautyyan.beautyyanapp.utils.Util;

import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Message.Type;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smackx.packet.DelayInformation;

import java.util.List;

import xmpp.constant.Constants;
import xmpp.constant.MyApplication;
import xmpp.dao.MsgDbHelper;
import xmpp.dao.NewMsgDbHelper;
import xmpp.model.ChatItem;
import xmpp.model.Room;
import xmpp.util.DateUtil;
import xmpp.util.FileUtil;
import xmpp.util.MyAndroidUtil;


public class XmppMessageListener implements PacketListener {

	@Override
	public void processPacket(Packet packet) {
		final Message nowMessage = (Message) packet;
		if(Constants.IS_DEBUG)
			Log.e("xmppchat come", nowMessage.toXML());

		if (nowMessage.toXML().contains("<invite")) {
			String noti = "你被邀请加入群组"+ XmppConnection.getRoomName(nowMessage.getFrom());
			MyAndroidUtil.showNoti(noti);
			String userName = XmppConnection.getRoomName(nowMessage.getFrom());;
			ChatItem msg =  new ChatItem(ChatItem.GROUP_CHAT,userName,userName, "", noti, DateUtil.now_MM_dd_HH_mm_ss(), 0, nowMessage.getProperty("shopname").toString());
			NewMsgDbHelper.getInstance(MyApplication.getInstance()).saveNewMsg(userName);
			MsgDbHelper.getInstance(MyApplication.getInstance()).saveChatMsg(msg);
			MyApplication.getInstance().sendBroadcast(new Intent(Constants.CHAT_NEW_MSG));

			XmppConnection.getInstance().joinMultiUserChat(Constants.USER_NAME, XmppConnection.getRoomName(nowMessage.getFrom()), true);
		}


		Type type = nowMessage.getType();
		if ((type == Type.groupchat || type == Type.chat)&&!nowMessage.getBody().equals("")) {
			String chatName = "";
			String userName = "";
			int chatType = ChatItem.CHAT;

			//name
			if (type == Type.groupchat) {
				chatName = XmppConnection.getRoomName(nowMessage.getFrom());
				userName = XmppConnection.getRoomUserName(nowMessage.getFrom());
				chatType = ChatItem.GROUP_CHAT;
			}
			else {
				chatName = userName = XmppConnection.getUsername(nowMessage.getFrom());
			}

			if (!userName.equals(Constants.USER_NAME)) {  //不是自己发出的,防群聊
				//time
				String dateString;
				DelayInformation inf = (DelayInformation) nowMessage.getExtension("x",
						"jabber:x:delay");
				if (inf == null)
					dateString = DateUtil.now_MM_dd_HH_mm_ss();
				else
					dateString = DateUtil.dateToStr_MM_dd_HH_mm_ss(inf.getStamp());

				//msg
				ChatItem msg = null;
				String msgBody = "";
//				//判断是否图片
//				if (nowMessage.getProperty("sendVoice") != null) {
//					String[] strs = nowMessage.getProperty("sendVoice").toString().split("\\.");
//					String body = strs[0] + '.' + strs[1];
//					msgBody = 1 + Api.DOWNLOAD_DATA_URL + body;
//					FileUtil.saveFileByBase64(nowMessage.getProperty("imgData").toString(),msgBody );
//				}
//				else if (nowMessage.getProperty("sendPicFile") != null) {
//					String[] strs = nowMessage.getProperty("sendPicFile").toString().split("\\.");
//					String body = strs[0] + '.' + strs[1];
//					msgBody = 2 + Api.DOWNLOAD_DATA_URL + body;
//					FileUtil.saveFileByBase64(nowMessage.getProperty("imgData").toString(),msgBody );
//				}
				//判断是否图片
				if (nowMessage.getBody() != null && nowMessage.getMtype().toString().equals("sendPicFile")) {
					String[] strs = nowMessage.getBody().toString().split("\\.");
					if (strs != null && strs.length > 1) {
						String body = strs[0] + '.' + strs[1];
						msgBody = Constants.PIC_FROM_URL_PATH + '/' + body;
					}
//					String[] strs2 = msgBody.split("/");
//					if (strs2 != null && strs2.length > 0) {
//						String body = strs2[strs2.length - 1];
//						msgBody = Constants.PIC_FROM_URL_PATH + '/' + body;
//					}
				}
				else if (nowMessage.getType() == Type.groupchat & nowMessage.getBody().contains(":::")) { //被迫的
					String[] msgAndData = nowMessage.getBody().split(":::");
					if(FileUtil.getType(msgAndData[0]) == FileUtil.SOUND)
						msgBody = Constants.SAVE_SOUND_PATH + "/" + msgAndData[0];
					else
						msgBody = Constants.SAVE_IMG_PATH + "/" + msgAndData[0];
					FileUtil.saveFileByBase64(msgAndData[1],msgBody );
				}
				else
					msgBody = nowMessage.getBody();


				if (type == Type.groupchat && XmppConnection.leaveRooms.contains(new Room(chatName))) {  //正常保存了
					System.out.println("我已经离开这个房间了");
				}
				else if (nowMessage.getBody().contains("[RoomChange")) {
//					String leaveRoom = nowMessage.getBody().split(",")[1];
//					String leaveUser = nowMessage.getBody().split(",")[2];
//					XmppConnection.getInstance().getMyRoom()
//					.get(XmppConnection.getInstance().getMyRoom().indexOf(new Room(leaveRoom))).friendList.remove(leaveUser);
					XmppConnection.getInstance().reconnect();
				}
				else{
					if (userName != null) {
						userName = userName.toLowerCase();
						chatName = userName;
					}
					List<ChatItem> list =  MsgDbHelper.getInstance(MyApplication.getInstance()).getChatMsg(chatName);
					String shopname = "";
					if (list != null && list.size() > 0) {
						shopname = list.get(0).shopname;
					}
					if (Util.isEmpty(shopname)) {
						shopname = nowMessage.getSubject().toString();
					}
					msg =  new ChatItem(chatType,chatName,userName, "", msgBody, dateString, 0, shopname);
					if ("message".equals(nowMessage.getMtype().toString())) {
						Intent intent = new Intent();
						YuYuanApp.getIns().getApplicationContext().sendBroadcast(new Intent("showDialog"));
						return;
					}
					if ("timeToClose".equalsIgnoreCase(msg.msg) || "sendFace".equals(nowMessage.getMtype().toString())
							|| "sendPingjia".equals(nowMessage.getMtype().toString())
							|| "sendCarInfo".equals(nowMessage.getMtype().toString())
							|| "sendPdfFilepdf".equals(nowMessage.getMtype().toString())
							|| "sendExcFile".equals(nowMessage.getMtype().toString())
							|| "sendWorFile".equals(nowMessage.getMtype().toString())
							|| "sendFormed".equals(nowMessage.getMtype().toString())
							|| "sendAct".equals(nowMessage.getMtype().toString())) {
						return;
					}
					NewMsgDbHelper.getInstance(MyApplication.getInstance()).saveNewMsg(chatName);
					MsgDbHelper.getInstance(MyApplication.getInstance()).saveChatMsg(msg);
					MyApplication.getInstance().sendBroadcast(new Intent(Constants.CHAT_NEW_MSG));
					MyAndroidUtil.showNoti(msgBody);//here
				}
			}
		}
	}
}
