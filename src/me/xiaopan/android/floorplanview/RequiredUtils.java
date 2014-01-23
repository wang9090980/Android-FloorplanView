package me.xiaopan.android.floorplanview;

import java.text.DecimalFormat;

import android.annotation.TargetApi;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.FontMetrics;
import android.graphics.PointF;
import android.graphics.Rect;
import android.os.Build;
import android.view.ViewTreeObserver;

class RequiredUtils {
	/**
	 * 缩放处理
	 * @param bitmap 原图
	 * @param scaling 缩放比例
	 * @return 缩放后的图片
	 */
	public static Bitmap scale(Bitmap bitmap, float scaling) {
		Matrix matrix = new Matrix();
		matrix.postScale(scaling, scaling);
		return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
	}
	
	/**
	 * 缩放处理
	 * @param bitmap 原图
	 * @param newWidth 新的宽度
	 * @return
	 */
	public static Bitmap scaleByWidth(Bitmap bitmap, int newWidth) {
		return scale(bitmap, (float) newWidth / bitmap.getWidth());
	}
	
	/**
	 * 缩放处理
	 * @param bitmap 原图
	 * @param newHeight 新的高度
	 * @return
	 */
	public static Bitmap scaleByHeight(Bitmap bitmap, int newHeight) {
		return scale(bitmap, (float) newHeight / bitmap.getHeight());
	}
	
	/**
	 * 删除监听器
	 * @param viewTreeObserver
	 * @param onGlobalLayoutListener
	 */
	@SuppressWarnings("deprecation")
	@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
	public static final void removeOnGlobalLayoutListener(ViewTreeObserver viewTreeObserver, ViewTreeObserver.OnGlobalLayoutListener onGlobalLayoutListener){
		if(Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN){
			viewTreeObserver.removeGlobalOnLayoutListener(onGlobalLayoutListener);
		}else{
			viewTreeObserver.removeOnGlobalLayoutListener(onGlobalLayoutListener);
		}
	}
	
	/**
	 * 计算百分比
	 * @param value1 值1
	 * @param value2 值2
	 * @param decimalPointAfterLength 小数点位数
	 * @param replaceWithZero 当小数点位数不足时是否使用0代替
	 * @param removePercent 删除字符串末尾的百分号
	 * @return
	 */
	public static String percent(double value1, double value2, int decimalPointAfterLength, boolean replaceWithZero, boolean removePercent){
		StringBuffer buffString = new StringBuffer();
		buffString.append("#");
		if(decimalPointAfterLength > 0){
			buffString.append(".");
		}
		for(int w = 0; w < decimalPointAfterLength; w++){
			buffString.append(replaceWithZero?"0":"#");
		}
		buffString.append("%");
		if(buffString.length() > 0){
			String result = new DecimalFormat(buffString.toString()).format(value1/value2);
			if(removePercent && result.length() > 0){
				result = result.substring(0, result.length() - 1);
			}
			return result;
		}else{
			return null;
		}
	}
	
	/**
	 * 判断给定的字符串是否为null或者是空的
	 * @param string 给定的字符串
	 * @return 
	 */
	public static boolean isEmpty(String string){
		return string == null || "".equals(string.trim());
	}
	
	/**
	 * 判断给定的字符串是否不为null且不为空
	 * @param string 给定的字符串
	 * @return 
	 */
	public static boolean isNotEmpty(String string){
		return !isEmpty(string);
	}
	
	/**
	 * 检查字符串长度，如果字符串的长度超过maxLength，就截取前maxLength个字符串并在末尾拼上appendString
	 * @param string
	 * @param maxLength
	 * @param appendString
	 * @return
	 */
	public static String checkLength(String string, int maxLength, String appendString){
		if(string.length()  > maxLength){
			string = string.substring(0, maxLength);
			if(appendString != null){
				string += appendString;
			}
		}
		return string;
	}
	
	/**
	 * 检查字符串长度，如果字符串的长度超过maxLength，就截取前maxLength个字符串并在末尾拼上…
	 * @param string
	 * @param maxLength
	 * @return
	 */
	public static String checkLength(String string, int maxLength){
		return checkLength(string, maxLength, "…");
	}
	
	/**
	 * 获取当给定的文本使用给定的画笔绘制时的宽度
	 * @param paint 指定的画笔
	 * @param text 指定的文本 
     * @return 当给定的文本使用给定的画笔绘制时的宽度
     */  
    public static float getTextWidth(Paint paint, String text) {  
        return paint.measureText(text);  
    }
	
	/**
	 * 获取给定文本的高度
	 * @param text 要计算的文本
	 * @param textSize 文本大小
	 * @return 文本的高度
	 */
	public static int getTextHeightByBounds(String text, float textSize){
        Paint paint = new Paint();
        Rect bounds = new Rect();
        paint.setTextSize(textSize);
        paint.getTextBounds(text, 0, text.length(), bounds);
        return bounds.height();
	}
    
    /**
     * 获取指定画笔的文字离顶部的基准距离
     * @return 返回指定笔离文字顶部的基准距离  
     */
    public static float getTextLeading(Paint paint)  {    
        FontMetrics fm = paint.getFontMetrics();   
        return fm.leading- fm.ascent;    
    }
    
    /**
	 * 使用X轴射线法判断给定的点是否在给定的多边形内部
	 * @param point 给定的点
	 * @param vertexPoints 多边形的所有顶点坐标
	 * @return
	 */
	public static boolean isPolygonContainPoint(PointF point,PointF[] vertexPoints){
		int nCross = 0;
		for (int i = 0; i < vertexPoints.length; i++) {
			PointF p1 = vertexPoints[i];
			PointF p2 = vertexPoints[(i + 1) % vertexPoints.length];
			if (p1.y == p2.y)
				continue;
			if (point.y < Math.min(p1.y, p2.y))
				continue;
			if (point.y >= Math.max(p1.y, p2.y))
				continue;
			double x = (double) (point.y - p1.y) * (double) (p2.x - p1.x)
					/ (double) (p2.y - p1.y) + p1.x;
			if (x > point.x)
				nCross++;
		}
		return (nCross % 2 == 1);
	}
}
