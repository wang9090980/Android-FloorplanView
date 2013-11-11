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
	 * 是否点击了我
	 * @param x
	 * @param y
	 * @return
	 */
	public boolean isClickMe(float x, float y);
	
	/**
	 * 绘制
	 * @param canvas
	 * @param paint
	 */
	public void draw(Canvas canvas, Paint paint);
	
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
}