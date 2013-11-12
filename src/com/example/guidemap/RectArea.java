package com.example.guidemap;

import me.xiaopan.easy.android.util.Colors;
import me.xiaopan.easy.android.util.TextUtils;
import me.xiaopan.easy.java.util.StringUtils;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
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
	
	public abstract int getXOffset();
	
	public abstract int getBubbleDrawableWidth();
	
	public abstract Drawable getBaseBubbleDrawable(Context context);
	
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
			if(bubbleDrawable == null){
				getBubbleDrawable(context);
			}
			x -= getXOffset() * ((float) getBaseBubbleDrawable(context).getIntrinsicWidth()/getBubbleDrawableWidth());
			y -= bubbleDrawable.getIntrinsicHeight();
			bubbleDrawableShowPoint = new PointF(x, y);
		}
		return bubbleDrawableShowPoint;
	} 
	
	@Override
	public Drawable getBubbleDrawable(Context context) {
		if(bubbleDrawable == null){
			Paint titlePaint = new Paint();
			titlePaint.setTextSize(40);
			titlePaint.setColor(Colors.BLACK);
			String title = StringUtils.checkLength(getTitle(), 20);
			int titleNeedWidth = (int) TextUtils.getTextWidth(titlePaint, title);
			int titleNeedHeight = TextUtils.getTextHeightByBounds(title, titlePaint.getTextSize());
			
			Paint subTitlePaint = new Paint();
			subTitlePaint.setTextSize(30);
			subTitlePaint.setColor(Colors.BLACK);
			String subTitle = StringUtils.checkLength(getSubTitle(), 20);
			int subTitleNeedWidth = (int) TextUtils.getTextWidth(subTitlePaint, subTitle);
			int subTitleNeedHeight = TextUtils.getTextHeightByBounds(subTitle, subTitlePaint.getTextSize());
			
			int finalNeedWidth = titleNeedWidth>subTitleNeedWidth?titleNeedWidth:subTitleNeedWidth;
			int finalNeedHeight = titleNeedHeight + 20 + subTitleNeedHeight;
			
			Drawable backgDrawable = getBaseBubbleDrawable(context);
			Rect paddingRect = new Rect();
			backgDrawable.getPadding(paddingRect);
			
			finalNeedWidth += paddingRect.left + paddingRect.right;
			finalNeedHeight += paddingRect.top + paddingRect.bottom;
			
			backgDrawable.setBounds(0, 0, backgDrawable.getMinimumWidth()>finalNeedWidth?backgDrawable.getMinimumWidth():finalNeedWidth, backgDrawable.getMinimumHeight()>finalNeedHeight?backgDrawable.getMinimumHeight():finalNeedHeight);
			
			Bitmap bitmap = Bitmap.createBitmap(backgDrawable.getBounds().width(), backgDrawable.getBounds().height(), Config.ARGB_8888);
			Canvas canvas = new Canvas(bitmap);
			backgDrawable.draw(canvas);
			
			canvas.drawText(title, paddingRect.left, paddingRect.top + TextUtils.getTextLeading(titlePaint), titlePaint);
			canvas.drawText(subTitle, paddingRect.left, paddingRect.top + TextUtils.getTextLeading(subTitlePaint) + 20 + titleNeedHeight, subTitlePaint);
			
			bubbleDrawable = new BitmapDrawable(context.getResources(), bitmap);
			bubbleDrawable.setBounds(0, 0, bubbleDrawable.getIntrinsicWidth(), bubbleDrawable.getMinimumHeight());
		}
		return bubbleDrawable;
	}
}