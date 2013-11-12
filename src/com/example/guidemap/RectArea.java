package com.example.guidemap;

import me.xiaopan.easy.android.util.Colors;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.drawable.Drawable;

public abstract class RectArea implements Area{
	protected PointF bubbleDrawableShowPoint;
	protected Drawable bubbleDrawable;
	protected boolean isShowBubble;
	
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
	public void drawArea(Canvas canvas, Paint paint){
		canvas.save();
		canvas.drawRect(getLeft(), getTop(), getRight(), getBottom(), paint);
		canvas.restore();
	}

	@Override
	public void drawBubble(Context context, Canvas canvas) {
		canvas.save();
		canvas.translate(getBubbleDrawableShowPoint(context).x, getBubbleDrawableShowPoint(context).y);
		getBubbleDrawable(context).draw(canvas);
		canvas.restore();
	}
	
	@Override
	public void drawPressed(Context context, Canvas canvas){
		Paint paint = new Paint();
		paint.setColor(Colors.RED_TRANSLUCENT);
		
		canvas.save();
		if(isShowBubble()){
			canvas.translate(getBubbleDrawableShowPoint(context).x, getBubbleDrawableShowPoint(context).y);
			canvas.drawRect(0, 0, bubbleDrawable.getIntrinsicWidth(), bubbleDrawable.getIntrinsicHeight(), paint);
		}else{
			canvas.drawRect(getLeft(), getTop(), getRight(), getBottom(), paint);
		}
		canvas.restore();
	}
	
	@Override
	public boolean isClickArea(float x, float y){
		return x >= getLeft() && x <= getRight() && y >= getTop() && y <= getBottom();
	}

	@Override
	public boolean isClickBubble(float x, float y) {
		return x >= bubbleDrawableShowPoint.x && x <= (bubbleDrawableShowPoint.x +bubbleDrawable.getIntrinsicWidth()) && y >= bubbleDrawableShowPoint.y && y <= (bubbleDrawableShowPoint.y +bubbleDrawable.getIntrinsicHeight());
	}

	@Override
	public boolean isShowBubble() {
		return isShowBubble;
	}

	@Override
	public void setShowBubble(boolean isShowBubble) {
		this.isShowBubble = isShowBubble;
	}

	@Override
	public PointF getBubbleDrawableShowPoint(Context context) {
		if(bubbleDrawableShowPoint == null){
			float x = (getLeft() + getRight())/2;
			float y = (getTop() + getBottom())/2;
			x = (getLeft() + x)/2;
			y = (getTop() + y)/2;
			x -= 40;
			if(bubbleDrawable == null){
				getBubbleDrawable(context);
			}
			y -= bubbleDrawable.getIntrinsicHeight();
			bubbleDrawableShowPoint = new PointF(x, y);
		}
		return bubbleDrawableShowPoint;
	}
}