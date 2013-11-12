package com.example.guidemap;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.drawable.Drawable;

/**
 * 地图上的区域
 */
public interface Area {
	/**
	 * 是否点击了矩形区域
	 * @param x
	 * @param y
	 * @return
	 */
	public boolean isClickArea(float x, float y);
	
	/**
	 * 是否点击了气泡
	 * @param x
	 * @param y
	 * @return
	 */
	public boolean isClickBubble(float x, float y);
	
	/**
	 * 绘制矩形区域
	 * @param canvas
	 * @param paint
	 */
	public void drawArea(Canvas canvas, Paint paint);
	
	/**
	 * 绘制气泡
	 * @param context
	 * @param canvas
	 */
	public void drawBubble(Context context, Canvas canvas);
	
	/**
	 * 绘制按下的样式
	 * @param canvas
	 * @param paint
	 */
	public void drawPressed(Context context, Canvas canvas);
	
	/**
	 * 获取气泡图片
	 * @param context
	 * @return
	 */
	public Drawable getBubbleDrawable(Context context);
	
	/**
	 * 获取气泡图片显示位置
	 * @param context
	 * @return
	 */
	public PointF getBubbleDrawableShowPoint(Context context);
	
	/**
	 * 是否显示气泡
	 * @return
	 */
	public boolean isShowBubble();
	
	/**
	 * 设置是否显示气泡
	 * @param isShowBubble
	 */
	public void setShowBubble(boolean isShowBubble);
}