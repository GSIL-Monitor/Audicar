package xmpp.d3View.gifView;

import android.graphics.Bitmap;

public class GifFrame {
        /**
         * ���캯��
         * @param im ͼƬ
         * @param del ��ʱ
         */
        public GifFrame(Bitmap im, int del) {
                image = im;
                delay = del;
        }
        
        public GifFrame(String name,int del){
            imageName = name;
            delay = del;
        }
        
        /**ͼƬ*/
        public Bitmap image;
        /**��ʱ*/
        public int delay;
        /**��ͼƬ����ļ�ʱ���ļ���*/
        public String imageName = null;
        
        /**��һ֡*/
        public GifFrame nextFrame = null;
}