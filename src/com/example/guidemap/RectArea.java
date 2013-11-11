package com.example.guidemap;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;

public abstract class RectArea implements Area{
	private PointF showBubblePoint;
	
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
	
	@Override
	public boolean isClickMe(float x, float y){
		return x >= getLeft() && x <= getRight() && y >= getTop() && y <= getBottom();
	}
	
	@Override
	public void draw(Canvas canvas, Paint paint){
		canvas.drawRect(getLeft(), getTop(), getRight(), getBottom(), paint);
	}

	@Override
	public PointF getCenterPoint() {
		float x = (getLeft() + getRight())/2;
		float y = (getTop() + getBottom())/2;
		x = (getLeft() + x)/2;
		y = (getTop() + y)/2;
		if(showBubblePoint == null){
			showBubblePoint = new PointF(x, y);
		}else{
			showBubblePoint.set(x, y);
		}
		return showBubblePoint;
	}
}