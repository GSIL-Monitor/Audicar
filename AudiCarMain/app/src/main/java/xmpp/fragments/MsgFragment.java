/**
 *
 */
package xmpp.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.beautyyan.beautyyanapp.R;
import com.beautyyan.beautyyanapp.YuYuanApp;
import com.beautyyan.beautyyanapp.utils.Constant;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import xmpp.activites.ChatActivity;
import xmpp.adapter.MsgAdapter;
import xmpp.constant.Constants;
import xmpp.d3View.D3Fragment;
import xmpp.d3View.D3View;
import xmpp.d3View.expression.ExpressionUtil;
import xmpp.dao.MsgDbHelper;
import xmpp.dao.NewMsgDbHelper;
import xmpp.model.ChatItem;
import xmpp.util.StringUtil;

import static com.beautyyan.beautyyanapp.R.id.searchText;


/**
 * @author MZH
 */
public class MsgFragment extends D3Fragment {
    @D3View
    ListView listView;
    public List<ChatItem> lastMsgs = new ArrayList<ChatItem>();
    @D3View
    LinearLayout infoLayout;
    private MsgDbHelper msgDbHelper;
    private MsgAdapter adapter;
    private NewMsgReceiver newMsgReceiver;
    @D3View
    ImageView headImg;
    @D3View
    TextView nickView;
    @D3View
    TextView msgView;
    @D3View
    TextView countView;
    @D3View
    TextView dateView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = setContentView(inflater, R.layout.acti_msg);
        initView();
        initData();
//		Intent intent = new Intent();
//		intent.setClass(getActivity(), ChatActivity.class);
//		intent.putExtra("chatName", "sa33058");
//		intent.putExtra("chatType", 0);
//		getActivity().startActivity(intent);
        ButterKnife.bind(this, view);
        return view;
    }

    public void initView() {
        msgDbHelper = MsgDbHelper.getInstance(getActivity());
        adapter = new MsgAdapter(getActivity());
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                ChatItem chatItem = lastMsgs.get(position);
                Intent intent = new Intent();
                intent.setClass(getActivity(), ChatActivity.class);
                intent.putExtra("chatName", chatItem.chatName);
                intent.putExtra("name", chatItem.shopname);
                intent.putExtra("chatType", chatItem.chatType);
                getActivity().startActivity(intent);
            }

        });

//		listView.setOnItemLongClickListener(new OnItemLongClickListener() {
//
//			@Override
//			public boolean onItemLongClick(AdapterView<?> parent, View view,
//					final int position, long id) {
//				new AlertDialog.Builder(getActivity())
//						.setTitle("提示")
//						.setMessage("确认删除信息？删除后不可恢复？")
//						.setPositiveButton("是", new OnClickListener() {
//
//							@Override
//							public void onClick(DialogInterface dialog, int which) {
//								String username = adapter.getItem(position).chatName;
//								NewMsgDbHelper.getInstance(getActivity()).delNewMsg(username);
//								MyApplication.getInstance().sendBroadcast(new Intent("ChatNewMsg"));
//								MsgDbHelper.getInstance(getActivity()).delChatMsg(username);
//								adapter.notifyDataSetChanged();
//							}
//						})
//						.setNegativeButton("否", null)
//			 	.show();
//				return true;
//			}
//		});

        newMsgReceiver = new NewMsgReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(Constants.REFRESH_MSGACTIVITY);
        filter.addAction(Constants.CHAT_NEW_MSG);
        getActivity().registerReceiver(newMsgReceiver, filter);

    }


    public void initData() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                lastMsgs = msgDbHelper.getLastMsg();
                int pos = -1;
                if (lastMsgs != null && lastMsgs.size() > 0)
                    for (int i = 0; i < lastMsgs.size(); i++) {
                        if (Constant.AUDI_CHAT_SERVICE.equals(lastMsgs.get(i).chatName)) {
                            pos = i;
                        }
                    }
                if (pos != -1) {
                    lastMsgs.remove(pos);
                }
                YuYuanApp.getIns().handler.post(new Runnable() {
                    @Override
                    public void run() {
                        setUi();
                    }
                });
            }
        }).start();

        infoLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ChatActivity.class);
                intent.putExtra("chatName", Constant.AUDI_CHAT_SERVICE);
                intent.putExtra("name", "奥迪购车小助手");
                intent.putExtra("chatType", ChatItem.CHAT);
                intent.putExtra("is_audi_helper", true);
                startActivity(intent);
            }
        });
//		msgView.setText(msgDbHelper.getChatMsg());
    }

    public void onClick(View v) {
        int i = v.getId();
        if (i == searchText) {
        } else {
        }
    }

    private void setUi() {
        YuYuanApp.getIns().getSp().setUnreadMsgCount(0);
        adapter.clear();
        adapter.addAll(lastMsgs);
        if (adapter.getCount() == 0) {
            listView.setVisibility(View.GONE);
        } else {
            listView.setVisibility(View.VISIBLE);
        }
        nickView.setText("奥迪购车小助手");

        List<ChatItem> chats = msgDbHelper.getChatMsg(Constant.AUDI_CHAT_SERVICE);
        if (chats != null && chats.size() > 0) {
            ChatItem item = chats.get(chats.size() - 1);
            if (item.msg != null) {
                if(item.msg.contains(Constants.SAVE_IMG_PATH) || item.msg.contains(Constants.PIC_FROM_URL_PATH))
                    msgView.setText("[图片]");
                else if (item.msg.contains(Constants.SAVE_SOUND_PATH))
                    msgView.setText("[语音]");
                else if (item.msg.contains("[/g0"))
                    msgView.setText("[动画表情]");
                else if (item.msg.contains("[/f0"))  //适配表情
                    msgView.setText(ExpressionUtil.getText(getActivity(), StringUtil.Unicode2GBK(item.msg)));
                else if (item.msg.contains("[/a0"))
                    msgView.setText("[位置]");
                else {
                    msgView.setText(item.msg);
                }
            }
            countView.setVisibility(View.GONE);
            NewMsgDbHelper newMsgDbHelper = NewMsgDbHelper.getInstance(getActivity());
            int newCount = newMsgDbHelper.getMsgCount(Constant.AUDI_CHAT_SERVICE);
            if(newCount!=0 && item.inOrOut == 0){
                countView.setVisibility(View.VISIBLE);
                int value = YuYuanApp.getIns().getSp().getUnreadMsgCount() + 1;
                YuYuanApp.getIns().getSp().setUnreadMsgCount(value);
//			holder.countView.setText(""+newCount);
            }
            dateView.setText(item.sendDate);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    private class NewMsgReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            initData();
        }
    }

    @Override
    public void onDestroy() {
        try {
            if (newMsgReceiver != null)
                getActivity().unregisterReceiver(newMsgReceiver);
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.onDestroy();
    }

    @Override
    public void onResume() {
        super.onResume();
    }
}
