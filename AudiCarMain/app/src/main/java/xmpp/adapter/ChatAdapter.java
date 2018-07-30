package xmpp.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.CountDownTimer;
import android.os.Vibrator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.beautyyan.beautyyanapp.R;
import com.beautyyan.beautyyanapp.http.Api;
import com.beautyyan.beautyyanapp.utils.Util;
import com.beautyyan.beautyyanapp.view.BubbleImageView;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.squareup.picasso.Transformation;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import xmpp.activites.ChatActivity;
import xmpp.activites.ShowPicActivitiy;
import xmpp.constant.Constants;
import xmpp.constant.MyApplication;
import xmpp.d3View.expression.ExpressionUtil;
import xmpp.d3View.gifView.GifView;
import xmpp.model.ChatItem;
import xmpp.util.DateUtil;
import xmpp.util.FileUtil;
import xmpp.util.ImageUtil;
import xmpp.util.StringUtil;
import xmpp.util.Tool;


public class ChatAdapter extends ArrayAdapter<ChatItem> {
	private Activity cxt;
	private static int[] resTo = {R.drawable.voiceto0, R.drawable.voiceto1,
			R.drawable.voiceto2, R.drawable.voiceto3};
	private static int[] resFrom = {R.drawable.voicefrom0, R.drawable.voicefrom1,
			R.drawable.voicefrom2, R.drawable.voicefrom3};
	private String username = null;
	//	private Bitmap bitmap;
	private MediaPlayer mp;
	private float curX, curY, curPosX, curPosY;
	private boolean isAudiHelper;

	private final Map<String, Target> mTargetMap = new HashMap<>();

	public static interface MsgType {
		int MSG_OUT = 0;
		int MSG_IN = 1;
	}

	public ChatAdapter(Activity context, String username, boolean isAudiHelper) {
		super(context, 0);
		this.cxt = context;
		this.username = username;
		this.isAudiHelper = isAudiHelper;
	}

	@Override
	public int getItemViewType(int position) {
		ChatItem nowMsg = (ChatItem) getItem(position);
		if (nowMsg.inOrOut == 1) {
			return MsgType.MSG_OUT;
		} else {
			return MsgType.MSG_IN;
		}
	}

	@Override
	public int getViewTypeCount() {
		return 2;
	}


	public View getView(int position, View convertView, ViewGroup parent) {
		final ChatItem item = (ChatItem) getItem(position);
		final ViewHolder viewHolder;
		final int msgType = getItemViewType(position);
		if (convertView == null) {
			if (msgType == MsgType.MSG_OUT) {
				convertView = LayoutInflater.from(cxt).inflate(R.layout.row_chat_mine, null);
			} else {
				convertView = LayoutInflater.from(cxt).inflate(R.layout.row_chat, null);
			}
			viewHolder = new ViewHolder();
			viewHolder.timeView = (TextView) convertView.findViewById(R.id.timeView);
			viewHolder.msgView = (TextView) convertView.findViewById(R.id.msgView);
			viewHolder.head = (ImageView) convertView.findViewById(R.id.headImg);
			viewHolder.img = (BubbleImageView) convertView.findViewById(R.id.imgView);
			viewHolder.gifImg = (ImageView) convertView.findViewById(R.id.gifImgView);
			viewHolder.voice = (ImageView) convertView.findViewById(R.id.voiceView);
			viewHolder.soundDuration = (TextView) convertView.findViewById(R.id.soundView);
			viewHolder.gif = (GifView) convertView.findViewById(R.id.gifView);
			viewHolder.nameView = (TextView) convertView.findViewById(R.id.nameView);
			viewHolder.jianImg = (ImageView) convertView.findViewById(R.id.jian_img);
			viewHolder.viewBlank = convertView.findViewById(R.id.view_blank);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
			viewHolder.timeView.setVisibility(View.VISIBLE);
			viewHolder.msgView.setVisibility(View.VISIBLE);
			viewHolder.head.setVisibility(View.VISIBLE);
			viewHolder.gifImg.setVisibility(View.GONE);
			viewHolder.voice.setVisibility(View.GONE);
			viewHolder.soundDuration.setVisibility(View.GONE);
			viewHolder.gif.setVisibility(View.GONE);
			viewHolder.nameView.setVisibility(View.GONE);
		}
		viewHolder.img.setVisibility(View.GONE);
		viewHolder.jianImg.setVisibility(View.VISIBLE);
		if (position == getCount() - 1) {
			viewHolder.viewBlank.setVisibility(View.VISIBLE);
		} else if (viewHolder.viewBlank.getVisibility() == View.VISIBLE) {
			viewHolder.viewBlank.setVisibility(View.GONE);
		}
		//head
		if (item.inOrOut == 0) {  //接收
			if (item.chatType == ChatItem.CHAT) {
				viewHolder.head.setImageResource(isAudiHelper ? R.mipmap.store_head_img : R.mipmap.store_headbg_fang);
			}
//			if (item.msg!=null && item.msg.contains(Api.DOWNLOAD_DATA_URL)) {
//				//图片
//				if (item.msg.startsWith("2")) {
//					Target target = new Target() {
//						@Override
//						public void onBitmapLoaded(final Bitmap bitmap, Picasso.LoadedFrom from) {
//							LinearLayout.LayoutParams params = (LinearLayout.LayoutParams)
//									viewHolder.img.getLayoutParams();
//							params.height = bitmap.getHeight();
//							params.width = bitmap.getWidth();
//							viewHolder.img.setImageBitmap(bitmap);
//						}
//
//						@Override
//						public void onBitmapFailed(Drawable errorDrawable) {
//
//						}
//
//						@Override
//						public void onPrepareLoad(Drawable placeHolderDrawable) {
//
//						}
//					};
//					mTargetMap.put("key", target);
//					Picasso.with(getContext())
//							.load(item.msg.substring(1, item.msg.length()))
//							.transform(getTransformation(viewHolder.img))
//							.into(mTargetMap.get("key"));
//					viewHolder.msgView.setVisibility(View.GONE);
//					viewHolder.img.setVisibility(View.VISIBLE);
//					viewHolder.jianImg.setVisibility(View.GONE);
//					viewHolder.img.setOnClickListener(new OnClickListener() {
//						@Override
//						public void onClick(View v) {
//							Intent intent = new Intent();
//							intent.putExtra("picPath", item.msg.substring(1, item.msg.length()));
//							intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//							intent.setClass(cxt, ShowPicActivitiy.class);
//							cxt.startActivity(intent);
//							cxt.overridePendingTransition(R.anim.zoom_enter, R.anim.do_nothing);
//						}
//					});
//				}
//				//语音
//				else if (item.msg.startsWith("1")) {
//
//				}
//			}
//
//			else {
//				viewHolder.msgView.setVisibility(View.VISIBLE);
//				viewHolder.img.setVisibility(View.GONE);
//				viewHolder.jianImg.setVisibility(View.VISIBLE);
//			}

		}

		//同一分钟内则不显示相同时间了
		ChatItem lastMsg = null;
		if (position != 0)
			lastMsg = (ChatItem) getItem(position - 1);
		if (lastMsg != null && DateUtil.getRecentTimeMM_dd(lastMsg.sendDate).equals(DateUtil.getRecentTimeMM_dd(item.sendDate))) {
			viewHolder.timeView.setVisibility(View.GONE);
		} else {
			viewHolder.timeView.setText(DateUtil.getRecentTimeMM_dd(item.sendDate));
		}

//		if (item.msg.contains("."))sadasd
		//text
		//适配图片、声音等文件
		if (item.msg != null && item.msg.contains(Constants.PATH)) {
			String path = item.msg;
			File file = new File(path);

			if (file.exists() && file.length() != 0) {    //  isExist
				viewHolder.msgView.setVisibility(View.GONE);
				int type = FileUtil.getType(path);

				if (type == FileUtil.IMG) {                 //isImg
					showImg(viewHolder.img, path);
					viewHolder.jianImg.setVisibility(View.GONE);
				} else if (type == FileUtil.SOUND) {                 //isSound
					playSound(path, viewHolder.soundDuration, viewHolder.voice, item.inOrOut == 1);
				}
			} else {
				viewHolder.msgView.setText("加载中...");
			}
		} else if (item.msg != null && item.msg.contains("[/g0")) { // isGif
			playGif(viewHolder.gif, viewHolder.msgView, viewHolder.gifImg, item.msg, position);
		} else if (item.msg != null && item.msg.contains("[/f0")) { // 适配表情
			viewHolder.msgView.setText(ExpressionUtil.getText(cxt, StringUtil.Unicode2GBK(item.msg)));
		} else if (item.msg != null && item.msg.contains("[/a0")) { // 适配地图
			viewHolder.msgView.setVisibility(View.GONE);
			showMap(viewHolder.img, item.msg);
		} else {
			viewHolder.msgView.setText(item.msg);
		}

		if (item.chatType == ChatItem.GROUP_CHAT && item.inOrOut == 0) {
			viewHolder.nameView.setVisibility(View.VISIBLE);
			viewHolder.nameView.setText(item.username);
		} else {
			viewHolder.nameView.setVisibility(View.GONE);
		}

		if (item.inOrOut == 0 && item.msg.contains(Constants.PIC_FROM_URL_PATH)) {
			final String[] strs = item.msg.split("/");
			String url = "";
			if (strs.length > 3) {
				url = Api.DOWNLOAD_DATA_URL  + strs[strs.length - 3] + '/' + strs[strs.length - 2] + '/' + strs[strs.length - 1];

			}
			final String url1 = url;
			final String path = Constants.PIC_FROM_URL_PATH + '/' + strs[strs.length - 1];
			if (FileUtil.isFileExist(path)) {
				showImg(viewHolder.img, path);
			} else if (!Util.isEmpty(url)) {
//			final String url = Api.DOWNLOAD_DATA_URL +  item.msg;
				Target target = new Target() {
					@Override
					public void onBitmapLoaded(final Bitmap bitmap, Picasso.LoadedFrom from) {
//						FileUtil.saveByBitmap(bitmap, path);
//						Bitmap mBitmap = createBitmap(path, Constant.getInstance().getDeviceWidth(), Constant.getInstance().getDeviceHeight());
//						FileUtil.saveByBitmap(mBitmap, path);
//						showImg(viewHolder.img, path);
//						notifyDataSetChanged();

						FileUtil.saveByBitmap(bitmap, path);
						showImg(viewHolder.img, path);
						notifyDataSetChanged();

					}

					@Override
					public void onBitmapFailed(Drawable errorDrawable) {

					}

					@Override
					public void onPrepareLoad(Drawable placeHolderDrawable) {

					}
				};
				mTargetMap.put("key", target);
				Picasso.with(getContext())
						.load(url)
						.into(mTargetMap.get("key"));
			}

			viewHolder.msgView.setVisibility(View.GONE);
			viewHolder.jianImg.setVisibility(View.GONE);
			viewHolder.img.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					ChatActivity.getIns().setViewBgGone(false);
					Intent intent = new Intent();
					intent.putExtra("picPath", FileUtil.isFileExist(path) ? path : url1);
					intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					intent.setClass(cxt, ShowPicActivitiy.class);
					cxt.startActivity(intent);
					cxt.overridePendingTransition(R.anim.zoom_enter, R.anim.do_nothing);
				}
			});
		}



		else if (item.inOrOut == 0) {
			viewHolder.msgView.setVisibility(View.VISIBLE);
			viewHolder.img.setVisibility(View.GONE);
			viewHolder.jianImg.setVisibility(View.VISIBLE);
		}




		//内容复制
		viewHolder.msgView.setOnLongClickListener(new OnLongClickListener() {
			@SuppressLint("NewApi")
			@Override
			public boolean onLongClick(View v) {
				TextView msgView = (TextView)v;
				ClipboardManager cm =(ClipboardManager) cxt.getSystemService(Context.CLIPBOARD_SERVICE);
				//将文本数据复制到剪贴板
				cm.setText(msgView.getText());
				Vibrator vib = (Vibrator) cxt.getSystemService(Context.VIBRATOR_SERVICE);  //震动提醒
				vib.vibrate(100);
				Tool.initToast(cxt,"复制成功");
				return false;
			}
		});

//		viewHolder.msgView.setOnTouchListener(new View.OnTouchListener() {
//			@Override
//			public boolean onTouch(View v, MotionEvent event) {
//				if (event.getAction() == MotionEvent.ACTION_DOWN ) {
//					viewHolder.jianImg.setImageResource(msgType == MsgType.MSG_OUT ? R.mipmap.jian_mine_pressed : R.mipmap.jian_pressed);
//					viewHolder.msgView.setBackgroundResource(msgType == MsgType.MSG_OUT ? R.drawable.msg_mine_pressed_bg_shape : R.drawable.msg_pressed_bg_shape);
//					LogUtil.i("event----ACTION_DOWN");
//					curPosX = event.getX();
//					curPosY = event.getY();
//
//				}
//				else if (event.getAction() == MotionEvent.ACTION_UP) {
//					viewHolder.jianImg.setImageResource(msgType == MsgType.MSG_OUT ?
//							R.mipmap.jian_mine_unpressed : R.mipmap.jian_unpressed);
//					viewHolder.msgView.setBackgroundResource(msgType == MsgType.MSG_OUT ?
//							R.drawable.msg_mine_un_bg_shape : R.drawable.msg_un_bg_shape);
//					LogUtil.i("event----ACTION_UP");
//				}
//				else if (event.getAction() == MotionEvent.ACTION_MOVE) {
//					LogUtil.i("event----ACTION_Move");
//					curX = event.getX();
//					curY = event.getY();
//					if (curPosX != curX || curPosY != curY) {
//						viewHolder.jianImg.setImageResource(msgType == MsgType.MSG_OUT ?
//								R.mipmap.jian_mine_unpressed : R.mipmap.jian_unpressed);
//						viewHolder.msgView.setBackgroundResource(msgType == MsgType.MSG_OUT ?
//								R.drawable.msg_mine_un_bg_shape : R.drawable.msg_un_bg_shape);
//					}
//				}
//				return false;
//			}
//		});
		return convertView;
	}

	private Transformation getTransformation(final ImageView img) {
		Transformation transformation = new Transformation() {

			@Override
			public Bitmap transform(Bitmap source) {
				int targetWidth = img.getWidth();

				double aspectRatio = (double) source.getHeight() / (double) source.getWidth();
				int targetHeight = (int) (targetWidth * aspectRatio);
				Bitmap result = Bitmap.createScaledBitmap(source, targetWidth, targetHeight, false);
				if (result != source) {
					// Same bitmap is returned if sizes are the same
					source.recycle();
				}
				return result;
			}

			@Override
			public String key() {
				return "transformation" + " desiredWidth";
			}
		};
		return transformation;
	}


	private void showImg(ImageView img , final String path){
		img.setVisibility(View.VISIBLE);
		img.setImageBitmap(ImageUtil.createImageThumbnail(img, path,500*500));
		img.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				ChatActivity.getIns().setViewBgGone(false);
				Intent intent = new Intent();
				intent.putExtra("picPath", path);
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				intent.setClass(cxt, ShowPicActivitiy.class);
				cxt.startActivity(intent);
				cxt.overridePendingTransition(R.anim.zoom_enter, R.anim.do_nothing);
			}
		});

//		//内容复制
		img.setOnLongClickListener(new OnLongClickListener() {
			@SuppressLint("NewApi")
			@Override
			public boolean onLongClick(View v) {
//				FileUtil.changeFile(path, Constants.IMG_PATH+"/"+FileUtil.getFileName(path));
				Tool.initToast(cxt,"图片已保存至本地"+path);
				MyApplication.getInstance().sendBroadcast(
						new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,Uri.parse("file://"+path)));
				return false;
			}
		});
	}

	private void showMap(ImageView img,String msg){
		img.setVisibility(View.VISIBLE);
		img.setImageResource(R.drawable.map);
		String[] adrs = msg.split(",");
		final double lat =  Double.valueOf(adrs[1]);
		final double lon =  Double.valueOf(adrs[2]);
	}


	/**
	 * play gif
	 * @param gif
	 * @param msg
	 */
	private void playGif(GifView gif, TextView msgView, ImageView img, String msg, int position){
		msgView.setVisibility(View.GONE);
		try {
			Field field = R.drawable.class.getDeclaredField(msg.substring(2,msg.indexOf("]")));
			int resId = Integer.parseInt(field.get(null).toString());
			if(getCount()-1 - position < 3 ){   //只显示三个动态
				gif.setVisibility(View.VISIBLE);
				gif.setGifImageType(GifView.GifImageType.COVER);
				gif.setGifImage(resId);
			}else{
				img.setVisibility(View.VISIBLE);
				img.setBackgroundResource(resId);
			}
		}catch (NoSuchFieldException e) {
			msgView.setVisibility(View.VISIBLE);
			msgView.setText(ExpressionUtil.getText(cxt, StringUtil.Unicode2GBK(msg)));
			e.printStackTrace();
		}catch (Exception e) {
			e.printStackTrace();
		}
	}


	/**
	 * play voice
	 * @param file
	 * @param soundDuration
	 * @param voice
	 * @param isOut
	 */
	public MediaPlayer mping = new MediaPlayer();
	private void playSound(final String file,TextView soundDuration,final ImageView voice,final boolean isOut) {
		final MediaPlayer mp = getMediaPlayer();
		mp.reset();
		voice.setVisibility(View.VISIBLE);
		soundDuration.setVisibility(View.VISIBLE);
		try {
			mp.setDataSource(file);
			mp.prepare();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		soundDuration.setText(""+ (mp.getDuration()/1000 == 0 ? 1 : mp.getDuration()/1000) + "\"");
		voice.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(mp.isPlaying()) {
					mp.stop();
				}
				try {
					mp.reset();
					mp.setDataSource(file);
					mp.prepare();
				} catch (IllegalArgumentException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (SecurityException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalStateException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				mp.start();
				mping = mp;
				new CountDownTimer(mp.getDuration(), 500) {
					int i =0;
					@Override
					public void onTick(long millisUntilFinished) {
						if (i <= mp.getDuration()/1000) {
							if(isOut)
								voice.setImageResource(resTo[i]);
							else
								voice.setImageResource(resFrom[i]);
							i++;
							if (i > 3)
								i = 0;
						}
					}

					@Override
					public void onFinish() {
						if(isOut)
							voice.setImageResource(resTo[0]);
						else
							voice.setImageResource(resFrom[0]);
					}
				}.start();
			}
		});
	}

	/**
	 *
	 * 获取MediaPlayer唯一实例
	 * @return
	 */
	private MediaPlayer getMediaPlayer()
	{
		synchronized (ChatAdapter.class)
		{
			if (null == mp)
			{
				mp = new MediaPlayer();
			}
			return mp;
		}

	}

	class ViewHolder {
		TextView timeView ,msgView ,soundDuration,nameView;
		ImageView head  ,gifImg ,voice, jianImg;
		BubbleImageView img;
		GifView gif;
		View viewBlank;
	}

	/*
     * 从网络获取图片
     */
	protected InputStream getImageStream(String path) throws Exception {
		URL url = new URL(path);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setConnectTimeout(10 * 1000);
		conn.setRequestMethod("GET");
		if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
			return conn.getInputStream();
		}
		return null;
	}

	/*
     * 保存文件
     */
	protected void saveFile(Bitmap bm, String fileName) throws IOException {
		File dirFile = new File(Constants.SAVE_IMG_PATH);
		if (!dirFile.exists()) {
			dirFile.mkdir();
		}
		File myCaptureFile = new File(Constants.SAVE_IMG_PATH + '/' + fileName);
		BufferedOutputStream bos = new BufferedOutputStream(
				new FileOutputStream(myCaptureFile));
		bm.compress(Bitmap.CompressFormat.JPEG, 80, bos);
		bos.flush();
		bos.close();
	}

}