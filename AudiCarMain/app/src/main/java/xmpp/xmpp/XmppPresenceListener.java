package xmpp.xmpp;

import android.content.Intent;
import android.util.Log;

import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.packet.RosterPacket.ItemType;

import xmpp.constant.Constants;
import xmpp.constant.MyApplication;
import xmpp.dao.MsgDbHelper;
import xmpp.dao.NewFriendDbHelper;
import xmpp.dao.NewMsgDbHelper;
import xmpp.model.ChatItem;
import xmpp.model.Friend;
import xmpp.util.DateUtil;
import xmpp.util.MyAndroidUtil;


public class XmppPresenceListener implements PacketListener {

	@Override
	public void processPacket(Packet packet) {
		Presence presence = (Presence) packet;
		if(Constants.IS_DEBUG)
			Log.e("xmppchat come1", presence.toXML());

		String jid = presence.getFrom();//发送方
		String to = presence.getTo();//接收方
		//Presence.Type有7中状态
		if (presence.getType().equals(Presence.Type.subscribe)) {// 收到好友申请
			if (!XmppConnection.getInstance().getFriendList().contains(new Friend(XmppConnection.getUsername(jid)))) {
				Friend friend  = new Friend(XmppConnection.getUsername(jid));
				friend.type = ItemType.from;
				XmppConnection.getInstance().getFriendList().add(friend);
			}

			for (Friend friend : XmppConnection.getInstance().getFriendList()) {
				System.out.println("我好友"+friend.username+"和我的关系"+friend.type);
				if (friend.equals(new Friend(XmppConnection.getUsername(jid))) && friend.type == ItemType.to) {
					String userName = XmppConnection.getUsername(jid);
					MyAndroidUtil.showNoti(userName+"同意添加好友");
					ChatItem msg =  new ChatItem(ChatItem.CHAT,userName,userName, "", userName+"同意添加好友", DateUtil.now_MM_dd_HH_mm_ss(), 0, presence.getProperty("shopname") == null ? null : presence.getProperty("shopname").toString());
					NewMsgDbHelper.getInstance(MyApplication.getInstance()).saveNewMsg(userName);
					MsgDbHelper.getInstance(MyApplication.getInstance()).saveChatMsg(msg);
					MyApplication.getInstance().sendBroadcast(new Intent(Constants.CHAT_NEW_MSG));
					XmppConnection.getInstance().changeFriend(friend, ItemType.both);
					Log.e("friend", to+"我收到好友请求toBoth");
				}
				else if (friend.username.equals(XmppConnection.getUsername(jid))) {
					XmppConnection.getInstance().changeFriend(friend, ItemType.from);
					Log.e("friend", to+"我收到好友请求toFrom");
					MyAndroidUtil.showNoti(friend.username+"申请好友");
					NewFriendDbHelper.getInstance(MyApplication.getInstance()).saveNewFriend(XmppConnection.getUsername(jid));
				}
			}
			MyApplication.getInstance().sendBroadcast(new Intent("friendChange"));
		}
		else if (presence.getType().equals(Presence.Type.subscribed)) {// 同意添加好友，不知道为什么会自动同意
			if(Constants.IS_DEBUG)
				Log.e("friend", jid+"同意添加");
		}
		else if (presence.getType().equals(Presence.Type.unsubscribe) ||presence.getType().equals(Presence.Type.unsubscribed)) {// 拒绝添加好友 删除好友
			if(Constants.IS_DEBUG)
				Log.e("friend", "我被好友"+jid+"删除");
			for (Friend friend : XmppConnection.getInstance().getFriendList()) {
				if (friend.equals(new Friend(XmppConnection.getUsername(jid)))) {
					XmppConnection.getInstance().changeFriend(friend, ItemType.remove);
				}
			}
			MyApplication.getInstance().sendBroadcast(new Intent("friendChange"));
		}
	}
}
