/**
 * 
 */
package xmpp.activites;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.beautyyan.beautyyanapp.R;

import org.jivesoftware.smackx.muc.MultiUserChat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import xmpp.adapter.ChoseAdapter;
import xmpp.constant.Constants;
import xmpp.d3View.D3View;
import xmpp.model.ChatItem;
import xmpp.model.Friend;
import xmpp.model.Room;
import xmpp.util.LoadThread;
import xmpp.util.Tool;
import xmpp.util.XmppLoadThread;
import xmpp.xmpp.XmppConnection;


/**
 * @author MZH
 *
 */
public class ChoseActivity extends XMPPBaseActivity {
	@D3View(click="onClick") ImageView selectAllBtn;
	@D3View
	LinearLayout nameLayout;
	@D3View
	EditText nameText;
	@D3View
	ListView listView;
	@D3View
	EditText searchText;
	private ChoseAdapter adapter;
	private List<Friend> friends = new ArrayList<Friend>();
	private List<String> members = new ArrayList<String>();
	private String roomName; 
	private int inviteCount = 0; //邀请人数
	private boolean isChoseAll = false;
	
	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.acti_chose);
		roomName = getIntent().getStringExtra("roomName");
		members = getIntent().getStringArrayListExtra("members");
		if (members == null) {
			members = new ArrayList<String>();
		}
		if (roomName!=null) {
			nameText.setText(roomName);
		}
		adapter = new ChoseAdapter(getApplicationContext());
		listView.setAdapter(adapter);
		friends = XmppConnection.getInstance().getFriendBothList();
		adapter.addAll(friends);
		
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Friend item = (Friend) adapter.getItem(position);
				item.isChose = !item.isChose;
				adapter.notifyDataSetChanged();
			}
		});
		
		searchText.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				adapter.clear();
				if (s.toString().equals("")) {
					adapter.addAll(friends);
				}
				else {
					List<Friend>  friendTemps = new ArrayList<Friend>();
					for (Friend friend : friends) {
						if (friend.username.contains(s.toString())) {
							friendTemps.add(friend);
						}
					}
					adapter.addAll(friendTemps);
				}
			}
		});
	}
	
	
	public void onClick(View v){
		int i1 = v.getId();
		if (i1 == R.id.leftBtn1) {
			finish();

		} else if (i1 == R.id.rightBtn) {
			if (roomName == null) {
				nameLayout.setVisibility(View.VISIBLE);
			} else {
				invite();
			}


		} else if (i1 == R.id.subBtn) {
			if (roomName == null) {
				final String name = nameText.getText().toString();
				if (isRoomName(name)) {
					Map<String, String> map = new HashMap<>();
					map.put("roomName", name);
					new LoadThread(ChoseActivity.this, Constants.URL_EXIST_ROOM, map) {

						@Override
						protected void refreshUI(String result) {
							if (result.contains("yes")) {
								Tool.initToast(getApplicationContext(), "该群已存在,请取另外的名字");
							} else {
								for (Friend friend : friends) {
									if (friend.isChose) {
										inviteCount++;
									}
								}
								if (inviteCount > 0 && !XmppConnection.getInstance().getMyRoom().contains(new Room(name))) {
									new XmppLoadThread(ChoseActivity.this) {
										@Override
										protected void result(Object object) {
											MultiUserChat muc = (MultiUserChat) object;
											if (muc != null && inviteCount > 0) {
												invite();
											}
										}

										@Override
										protected Object load() {
											Tool.initToast(getApplicationContext(), "创建中...");
											return XmppConnection.getInstance().createRoom(name);
										}
									};
								} else if (XmppConnection.getInstance().getMyRoom().contains(new Room(name))) {
									Tool.initToast(getApplicationContext(), "已在此群");
								} else {
									Tool.initToast(getApplicationContext(), "一个人怎么聊啊亲!");
								}
							}
						}
					};
				} else {
					Tool.initToast(getApplicationContext(), "房间名只能使用中文,英文或者数字");
				}
			} else {
				if (inviteCount > 0) {
					invite();
				}
			}

		} else if (i1 == R.id.cancelBtn) {
			nameLayout.setVisibility(View.GONE);

		} else if (i1 == R.id.selectAllBtn) {
			for (int i = 0; i < adapter.getCount(); i++) {
				adapter.getItem(i).isChose = !isChoseAll;
			}
			isChoseAll = !isChoseAll;
			if (isChoseAll) {
				selectAllBtn.setImageResource(R.drawable.login_checked);
			} else {
				selectAllBtn.setImageResource(R.drawable.login_check);
			}
			adapter.notifyDataSetChanged();

		} else {
		}
		
	}
	
	
	@Override
	protected void onResume() {
		nameText.clearFocus();
		searchText.clearFocus();
		super.onResume();
	}
	
	public void invite(){
		String name = nameText.getText().toString();
		MultiUserChat muc = new MultiUserChat(XmppConnection.getInstance().getConnection(), XmppConnection.getFullRoomname(name));
		for (Friend friend : friends) {
			if (friend.isChose &&  !members.contains(friend.username)) {
				muc.invite(XmppConnection.getFullUsername(friend.username), name);
			}
		}
		for (String mem : members) {
			try {
				XmppConnection.getInstance().sendMsg(mem,"[RoomChange,"+roomName+","+ Constants.USER_NAME, ChatItem.CHAT, name);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		nameLayout.setVisibility(View.GONE);
		RoomMemActivity.isExit = true;
		ChatActivity.isExit = true;
		XmppConnection.getInstance().reconnect();
		finish();
	}
	
	public boolean isRoomName(String text){
		Pattern p = Pattern.compile("^[a-zA-Z0-9\u4E00-\u9FA5]+$");    
        Matcher m = p.matcher(text);  
        if(m.find()){  
        	return true;
        } 
		return false;
	}
}
