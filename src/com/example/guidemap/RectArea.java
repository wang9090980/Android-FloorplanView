package com.example.guidemap;

import android.graphics.Canvas;
import android.graphics.Paint;

public abstract class RectArea implements Area{
	
	/**
	 * 获取左外边距
	 * @return 左外边距
	 */ 
	public abstract int getLeft();
	
	/**
	 * 获取左外边距
	 * @return 顶外边距
	 */
	public abstract int getTop();

	/**
	 * 获取右外边距
	 * @return 右外边距
	 */
	public abstract int getRight();

	/**
	 * 获取底外边距
	 * @return 底外边距
	 */
	public abstract int getBottom();
	
	/**
	 * 获取标题
	 * @return
	 */
	public abstract String getTitle();
	
	/**
	 * 获取副标题
	 * @return
	 */
	public abstract String getSubTitle();
	
	@Override
	public boolean isClickMe(float x, float y){
		return x >= getLeft() && x <= getRight() && y >= getTop() && y <= getBottom();
	}
	
	@Override
	public void draw(Canvas canvas, Paint paint){
		canvas.drawRect(getLeft(), getTop(), getRight(), getBottom(), paint);
	}
}