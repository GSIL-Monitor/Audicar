package xmpp.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import xmpp.activites.PicSrcPickerActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.widget.Toast;

import com.beautyyan.beautyyanapp.utils.ToastUtils;

public class PhotoUtilsAbove23 {

	private static int SELECT_PIC_BY_PICK_PHOTO = 0x1;//选择照片
	private static int SELECT_PIC_BY_TACK_PHOTO = 0x2;//拍照
	static File photoFile;
	private static File mGalleryFile;



	static String mCurrentPhotoPath;

	public static File createImageFile(Context context) throws IOException {
		// Create an image file name
		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.CHINA).format(new Date());
		String imageFileName = "JPEG_" + timeStamp + "_";
		//.getExternalFilesDir()方法可以获取到 SDCard/Android/data/你的应用的包名/files/ 目录，一般放一些长时间保存的数据
		File storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
		//创建临时文件,文件前缀不能少于三个字符,后缀如果为空默认未".tmp"
		File image = File.createTempFile(
				imageFileName,  /* 前缀 */
				".jpg",         /* 后缀 */
				storageDir      /* 文件夹 */
				);
		mCurrentPhotoPath = "file:" + image.getAbsolutePath();
		return image;
	}

	public static void openPhotos(Context context){
//		mGalleryFile = new File(getExternalDir(), IMAGE_GALLERY_NAME);
		try {
			mGalleryFile=createImageFile(context);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
		intent.addCategory(Intent.CATEGORY_OPENABLE);
		intent.setType("image/*");
		Uri uriForFile = FileProvider.getUriForFile(context, context.getPackageName()+".fileprovider", mGalleryFile);
		intent.putExtra(MediaStore.EXTRA_OUTPUT, uriForFile);
		intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
		intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
		((PicSrcPickerActivity)context).startActivityForResult(intent, PicSrcPickerActivity.CHOOSE_PICTURE);

	}


	public static File getPhotoFile(){
		return photoFile;
	}

//	public static String getCurrentphotoPath(){
//		return mCurrentPhotoPath;
//	}

	public static File getGalleryFile(){
		return mGalleryFile;
	}


	/**
	 * 获取本地图片并指定高度和宽度
	 */
	public static Bitmap getNativeImage(String imagePath)
	{
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		// 获取这个图片的宽和高
		Bitmap myBitmap = BitmapFactory.decodeFile(imagePath, options); //此时返回myBitmap为空
		//计算缩放比
		int be = (int)(options.outHeight / (float)200);
		int ys = options.outHeight % 200;//求余数
		float fe = ys / (float)200;
		if (fe >= 0.5)
			be = be + 1;
		if (be <= 0)
			be = 1;
		options.inSampleSize = be;
		//重新读入图片，注意这次要把options.inJustDecodeBounds 设为 false
		options.inJustDecodeBounds = false;
		myBitmap = BitmapFactory.decodeFile(imagePath, options);
		return myBitmap;
	}
	/**
	 * 以最省内存的方式读取本地资源的图片 或者SDCard中的图片
	 * @param imagePath
	 * 图片在SDCard中的路径
	 * @return
	 */
	@SuppressWarnings("deprecation")
	public static Bitmap getSDCardImg(String imagePath)
	{
		BitmapFactory.Options opt = new BitmapFactory.Options();
		opt.inPreferredConfig = Bitmap.Config.RGB_565;
		opt.inPurgeable = true;
		opt.inInputShareable = true;
		//获取资源图片
		return BitmapFactory.decodeFile(imagePath, opt);
	}

	/**
	 * 根据路径获得突破并压缩返回bitmap用于显示
	 * @param filePath
	 * @return
	 */
	public static Bitmap getSmallBitmap(String filePath, int reqWidth, int reqHeight) {
		final BitmapFactory.Options options = new BitmapFactory.Options();
		//只返回图片的大小信息
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(filePath, options);
		options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
		options.inJustDecodeBounds = false;
		return BitmapFactory.decodeFile(filePath, options);
	}

	/**
	 * 图片的压缩
	 * @param options
	 * @param reqWidth
	 * @param reqHeight
	 * @return
	 */
	public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
		final int height = options.outHeight;
		final int width = options.outWidth;
		int inSampleSize = 1;
		if (height > reqHeight || width > reqWidth) {
			final int heightRatio = Math.round((float) height
					/ (float) reqHeight);
			final int widthRatio = Math.round((float) width / (float) reqWidth);
			inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
		}
		return inSampleSize;
	}

	@SuppressWarnings("unused")
	public static Bitmap getAddressByUriAbove23(Context context, String picPath, Uri photoUri, boolean isSelectPhoto){
		if (picPath != null
				&& (picPath.endsWith(".jpg")
				|| picPath.endsWith(".JPG")
				|| picPath.endsWith(".jpeg")
				|| picPath.endsWith(".JPEG")
				|| picPath.endsWith(".bmp") || picPath
				.endsWith(".BMP"))) {

		} else {

//			AlertUtil.ShowHintDialog(AutoRegister_UpLoadIDImg.this, "??????????????", null);
			ToastUtils.showShort(context, "未找到图片");
			return null;
			//				ToastUtils.show(FindPasswordApproveActivity.this,
			//						"?????????");
		}
//		LogUtil.i(this,"getPhotoUri");
		Bitmap bitmap=checkImgSizeAndRead(context,picPath,photoUri,isSelectPhoto);
		return bitmap;
	}


	/**
	 */
	@SuppressWarnings("deprecation")
	public static Bitmap checkImgSizeAndRead(Context context,String picPath,Uri photoUri,boolean isSelectPhoto) {
		File file = new File(picPath);
		Bitmap bitmap=null;

		if (android.os.Build.VERSION.SDK_INT>=24&&isSelectPhoto) {
			try {
				bitmap=MediaStore.Images.Media.getBitmap(context.getContentResolver(), photoUri);
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}else {
			try {
				bitmap = BitmapFactory.decodeStream(context
						.getApplicationContext().getContentResolver()
						.openInputStream(Uri.fromFile(file)));
			} catch (FileNotFoundException e) {

			}
		}

		if (bitmap==null) {
			ToastUtils.showShort(context, "未找到图片");
			return null;
		}


		System.gc();
		return bitmap;
	}
	
}
