package com.example.guidemap;

import android.graphics.Canvas;
import android.graphics.Paint;

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
}