package xmpp.d3View.expression;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.util.Log;

import com.beautyyan.beautyyanapp.R;

import java.lang.reflect.Field;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class ExpressionUtil {
	/**
	 * ��spanableString���������жϣ��������Ҫ�����Ա���ͼƬ����
	 */
    public static void dealExpression(Context context,SpannableString spannableString, Pattern patten, int start) throws Exception {
    	Matcher matcher = patten.matcher(spannableString);
        while (matcher.find()) {
            String key = matcher.group();
            if (matcher.start() < start) {
                continue;
            }
            Field field = R.drawable.class.getDeclaredField(key.substring(2,key.indexOf("]")));
			int resId = Integer.parseInt(field.get(null).toString());		
			
            if (resId != 0) {
                Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), resId);
                Matrix matrix = new Matrix(); 
                matrix.postScale(1.5f,1.5f); //���Ϳ�Ŵ���С�ı���
                Bitmap resizeBmp = Bitmap.createBitmap(bitmap,0,0,bitmap.getWidth(),bitmap.getHeight(),matrix,true);
                
                ImageSpan imageSpan = new ImageSpan(resizeBmp);				            
                int end = matcher.start() + key.length();					
                spannableString.setSpan(imageSpan, matcher.start(), end, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);	
                if (end < spannableString.length()) {						
                    dealExpression(context,spannableString,  patten, end);
                }
                break;
            }
        }
    }
    
    public static SpannableString getExpressionString(Context context,String str,String zhengze){
    	SpannableString spannableString = new SpannableString(str);
        Pattern sinaPatten = Pattern.compile(zhengze, Pattern.CASE_INSENSITIVE);		//ͨ�������������ʽ������һ��pattern
        try {
            dealExpression(context,spannableString, sinaPatten, 0);
        } catch (Exception e) {
            Log.e("dealExpression", e.getMessage());
        }
        return spannableString;
    }
    public static SpannableString getText(Context context,String text){
    	String zhengze = "\\[/f0[0-9]{2}\\]"; // ������ʽ�������ж���Ϣ���Ƿ��б���
    	SpannableString spannableString = null;
		try {
			spannableString = ExpressionUtil
					.getExpressionString(context, text, zhengze);
		} catch (Exception e) {
			e.printStackTrace();
		}
    	return spannableString;
    }
}