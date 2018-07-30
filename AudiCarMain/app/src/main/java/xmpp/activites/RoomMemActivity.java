/**
 * 
 */
package xmpp.activites;

import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.beautyyan.beautyyanapp.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import xmpp.adapter.SearchAdapter;
import xmpp.constant.Constants;
import xmpp.constant.MyApplication;
import xmpp.d3View.D3View;
import xmpp.dao.MsgDbHelper;
import xmpp.dao.NewMsgDbHelper;
import xmpp.model.ChatItem;
import xmpp.model.Room;
import xmpp.util.LoadThread;
import xmpp.util.Tool;
import xmpp.xmpp.XmppConnection;

/**
 * @author MZH
 *
 */
public class RoomMemActivity extends XMPPBaseActivity {
	@D3View(click="onClick") ImageView leftBtn;
	@D3View(click="onClick") TextView rightBtn;
	@D3View(click="onClick") Button exitBtn;
	@D3View
	ListView listView;
	private SearchAdapter adapter;
//	private List<RoomMem> roomMems;
	private String roomName;
	private List<String> members = new ArrayList<String>(); 
	public static boolean isExit = false;
	
	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.acti_room_mem);
		roomName = getIntent().getStringExtra("roomName");
		adapter = new SearchAdapter(getApplicationContext());
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Intent intent = new Intent(getApplicationContext(), FriendActivity.class);
				intent.putExtra("username", adapter.getItem(position));
				startActivity(intent);
			}
		});
		for (Room room : XmppConnection.getInstance().getMyRoom()) {
			if (room.name.equals(roomName)) {
				members = room.friendList;
				for (String mem : room.friendList) {
					adapter.add(mem);
				}
			}
		}
		
		
//		initData();
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (isExit) {
			isExit = false;
			finish();
		}
	}
	
	
	public void onClick(View v){
		int i = v.getId();
		if (i == R.id.leftBtn) {
			finish();

		} else if (i == R.id.rightBtn) {
			Intent intent = new Intent(getApplicationContext(), ChoseActivity.class);
			intent.putExtra("roomName", roomName);
			intent.putStringArrayListExtra("members", (ArrayList<String>) members);
			startActivity(intent);

		} else if (i == R.id.exitBtn) {
			Builder builder = new Builder(RoomMemActivity.this);
			builder
					.setMessage("确定要退出吗?")
					.setTitle("提示")
					.setPositiveButton("确认", new OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							Map<String, String> map = new HashMap<String, String>();
							map.put("jid", XmppConnection.getFullUsername(Constants.USER_NAME));
							for (Room room : XmppConnection.getInstance().getMyRoom()) {
								if (room.equals(new Room(roomName))) {
									map.put("roomId", room.roomid);
									map.put("roomName", roomName);
									for (String mem : room.friendList) {
										try {
											XmppConnection.getInstance().sendMsg(mem, "[RoomChange," + roomName + "," + Constants.USER_NAME, ChatItem.CHAT, roomName);
										} catch (Exception e) {
											e.printStackTrace();
										}
									}
								}
							}

							new LoadThread(RoomMemActivity.this, Constants.URL_EXIT_ROOM, map) {

								@Override
								protected void refreshUI(String result) {
									Tool.initToast(getApplicationContext(), "退出成功");
									XmppConnection.getInstance().getMyRoom().remove(new Room(roomName));
									NewMsgDbHelper.getInstance(getApplicationContext()).delNewMsg(roomName);
									MsgDbHelper.getInstance(getApplicationContext()).delChatMsg(roomName);
									MyApplication.getInstance().sendBroadcast(new Intent(Constants.CHAT_NEW_MSG));
									MyApplication.getInstance().sendBroadcast(new Intent("LeaveRoom"));
									XmppConnection.leaveRooms.add(new Room(roomName));
									XmppConnection.getInstance().reconnect();
									ChatActivity.isExit = true;
									finish();
								}
							};
						}
					})
					.setNegativeButton("取消", null)
					.show();

		} else {
		}
		
	}
}
