package xmpp.d3View.gifView;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import java.io.InputStream;

/**
 * GifView<br>
 * ���������ʾһ��gif��������ʹ�÷�����android������view����imageview)һ����<br>
 * ���Ҫ��ʾ��gif̫�󣬻����OOM�����⡣���嵽tmp
 * @author liao
 *
 */
public class GifView extends ImageView implements GifAction {

        /**gif������*/
        private GifDecoder gifDecoder = null;
        /**��ǰҪ����֡��ͼ*/
        private Bitmap currentImage = null;
        
        private boolean isRun = true;
        
        private boolean pause = false;

        private DrawThread drawThread = null;
        
        private Context context = null;
        
        private boolean cacheImage = false;
        
        private View backView = null;
        
        private GifImageType animationType = GifImageType.SYNC_DECODER;

        /**
         * ��������У�Gif������ʾ�ķ�ʽ<br>
         * ���ͼƬ�ϴ���ô������̻�Ƚϳ��������������У�gif�����ʾ
         * @author liao
         *
         */
        public enum GifImageType{
                /**
                 * �ڽ�������У�����ʾͼƬ��ֱ������ȫ���ɹ�������ʾ
                 */
                WAIT_FINISH (0),
                /**
                 * �ͽ������ͬ����������е����ͼƬ��ʾ������
                 */
                SYNC_DECODER (1),
                /**
                 * �ڽ�������У�ֻ��ʾ��һ֡ͼƬ
                 */
                COVER (2);
                
                GifImageType(int i){
                        nativeInt = i;
                }
                final int nativeInt;
        }
        
        
        public GifView(Context context) {
        super(context);
        this.context = context;
        //gifDecoder = new GifDecoder(this);
        setScaleType(ScaleType.FIT_XY);
    }
    
    public GifView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
        
    }  
    
    public GifView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.context = context;
       // TypedArray a = context.obtainStyledAttributes(attrs,R.array.);
        //gifDecoder = new GifDecoder(this);
        setScaleType(ScaleType.FIT_XY);
    }
    
    /**
     * ����ͼƬ������ʼ����
     * @param gif Ҫ���õ�ͼƬ
     */
    private void setGifDecoderImage(byte[] gif){

        if(gifDecoder == null){
            gifDecoder = new GifDecoder(this);
        }
        gifDecoder.setGifImage(gif);
        gifDecoder.start();
    }
    
    /**
     * ����ͼƬ����ʼ����
     * @param is Ҫ���õ�ͼƬ
     */
    private void setGifDecoderImage(InputStream is){

        if(gifDecoder == null){
            gifDecoder = new GifDecoder(this);
        }
        gifDecoder.setGifImage(is);
        gifDecoder.start();
        
        
    }
    
    /**
     * �ѱ�Gif��������Ϊ����view�ı���
     * @param v Ҫʹ��gif��Ϊ������view
     */
    public void setAsBackground(View v){
        backView = v;
    }
    
    protected Parcelable onSaveInstanceState() {
        super.onSaveInstanceState();
        if(gifDecoder != null)
                gifDecoder.free();
        
                return null;
        }
    
    /**
     * @hide
     * ���û���ͼƬ<br>
     * �������ͼƬ��ÿһFrame�ļ��̫��Ļ����������֡������<br>
     * ��������˻���ͼƬ������������destroy��������ͼƬ������
     */   
//    public void setCahceImage(){
//        if(gifDecoder == null){
//            gifDecoder = new GifDecoder(this);
//        }
//        cacheImage = true;
//        gifDecoder.setCacheImage(true, context);
//    }
    
    
    /**
     * ���ֽ�������ʽ����gifͼƬ
     * @param gif ͼƬ
     */
    public void setGifImage(byte[] gif){
        setGifDecoderImage(gif);
    }
    
    /**
     * ���ֽ�����ʽ����gifͼƬ
     * @param is ͼƬ
     */
    public void setGifImage(InputStream is){
        setGifDecoderImage(is);
    }
    
    /**
     * ����Դ��ʽ����gifͼƬ
     * @param resId gifͼƬ����ԴID
     */
    public void setGifImage(int resId){
		if (currentImage != null) {
			currentImage = null;
		}
    	if (gifDecoder != null) {
			stopDecodeThread();
			gifDecoder = null;
		}
        Resources r = getResources();
        InputStream is = r.openRawResource(resId);
        setGifDecoderImage(is);
    }
    
	
    
    
	/**
	 * �жϽ����߳�
	 */
	private void stopDecodeThread() {
		if (gifDecoder != null && gifDecoder.getState() != Thread.State.TERMINATED) {
			gifDecoder.interrupt();
			gifDecoder.destroy();
		}
	}
    
    public void destroy(){
        if(gifDecoder != null)
            gifDecoder.free();
    }
    
    /**
     * ֻ��ʾ��һ֡ͼƬ<br>
     * ���ñ�������gif������ʾ������ֻ����ʾgif�ĵ�һ֡ͼ
     */
    public void showCover(){
        if(gifDecoder == null)
                return;
        pause = true;
        currentImage = gifDecoder.getImage();
        invalidate();
    }
    
    /**
     * ������ʾ����<br>
     * �������ڵ���showCover�󣬻��ö���������ʾ�����û�е���showCover��������û���κ�Ч��
     */
    public void showAnimation(){
        if(pause){
                pause = false;
        }
    }
    
    /**
     * ����gif�ڽ�������е���ʾ��ʽ<br>
     * <strong>������ֻ����setGifImage����֮ǰ���ã�����������Ч</strong>
     * @param type ��ʾ��ʽ
     */
    public void setGifImageType(GifImageType type){
        if(gifDecoder == null)
                animationType = type;
    }
  

    
    /**
     * @hide
     */
    public void parseOk(boolean parseStatus,int frameIndex){
        if(parseStatus){
                if(gifDecoder != null){
                        switch(animationType){
                        case WAIT_FINISH:
                                if(frameIndex == -1){
                                        if(gifDecoder.getFrameCount() > 1){     //��֡������1ʱ�����������߳�
                                        DrawThread dt = new DrawThread();
                                dt.start();
                                }else{
                                        reDraw();
                                }
                                }
                                break;
                        case COVER:
                                if(frameIndex == 1){
                                        currentImage = gifDecoder.getImage();
                                        reDraw();
                                }else if(frameIndex == -1){
                                        if(gifDecoder.getFrameCount() > 1){
                                                if(drawThread == null){
                                                        drawThread = new DrawThread();
                                                        drawThread.start();
                                                }
                                        }else{
                                                reDraw();
                                        }
                                }
                                break;
                        case SYNC_DECODER:
                                if(frameIndex == 1){
                                        currentImage = gifDecoder.getImage();
                                        reDraw();
                                }else if(frameIndex == -1){
                                        reDraw();
                                }else{
                                        if(drawThread == null){
                                                drawThread = new DrawThread();
                                                drawThread.start();
                                        }
                                }
                                break;
                        }
 
                }else{
                        Log.e("gif","parse error");
                }
                
        }
    }
    
    private void reDraw(){
        if(redrawHandler != null){
                        Message msg = redrawHandler.obtainMessage();
                        redrawHandler.sendMessage(msg);
        }
        
    }
    
    private void drawImage(){
        setImageBitmap(currentImage);
        invalidate();
    }
     
    private Handler redrawHandler = new Handler(){
        public void handleMessage(Message msg) {
            try{
                    if(backView != null){
                    backView.setBackgroundDrawable(new BitmapDrawable(currentImage));
                }else{
                    drawImage();
                }
            }catch(Exception ex){
                Log.e("GifView", ex.toString());
            }
        }
    };
    
    /**
     * �����߳�
     * @author liao
     *
     */
    private class DrawThread extends Thread{    
        public void run(){
                if(gifDecoder == null){
                        return;
                }
                while(isRun){
                   
                if (pause == false) {
                    GifFrame frame = gifDecoder.next();

                    if (frame == null) {
                        SystemClock.sleep(50);
                        continue;
                    }
                    if (frame.image != null)
                        currentImage = frame.image;
                    else if (frame.imageName != null) {
                        currentImage = BitmapFactory.decodeFile(frame.imageName);
                    }
                    long sp = frame.delay;
                    if (redrawHandler != null) {
                        reDraw();
                        SystemClock.sleep(sp);
                    } else {
                        break;
                    }
                } else {
                    SystemClock.sleep(50);
                        }
                }
        }
    }
    
}