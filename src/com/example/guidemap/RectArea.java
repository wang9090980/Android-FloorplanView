package com.example.guidemap;

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

/**
 * 矩形区域
 */
public abstract class RectArea implements Area{
	protected PointF bubbleDrawableShowPoint;
	protected Drawable bubbleDrawable;
	private boolean isShowBubble;
	private boolean isClickedArea;
	
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
	
	/**
	 * 获取气泡X轴偏移
	 * @return
	 */
	public abstract int getBubbleXOffset();
	
	/**
	 * 获取标题和副标题之前的垂直间距
	 * @return
	 */
	public int getIntervalOfHeight(){
		return 20;
	}
	
	/**
	 * 获取标题文本大小
	 * @return
	 */
	public int getTilteTextSize(){
		return 40;
	}
	
	/**
	 * 获取副标题文本大小
	 * @return
	 */
	public int getSubTilteTextSize(){
		return 30;
	}
	
	/**
	 * 获取标题最大长度
	 * @return
	 */
	public int getTilteMaxLength(){
		return 20;
	}
	
	/**
	 * 获取副标题最大长度
	 * @return
	 */
	public int getSubTilteMaxLength(){
		return 15;
	}
	
	/**
	 * 获取气泡的原始宽度
	 * @return
	 */
	public abstract int getBubbleDrawableOriginalWidth();
	
	/**
	 * 获取最基础的气泡图片，我们需要在此图片上绘制标题以及副标题
	 * @param context
	 * @return
	 */
	public abstract Drawable getBaseBubbleDrawable(Context context);
	
	/**
	 * 获取无效的高度，一般为气泡小箭头的高度
	 * @return
	 */
	public int getVoidHeight(){
		return 0;
	}
	
	/**
	 * 获取区域的颜色
	 * @return
	 */
	public int getAreaColor(){
		return 0x800000ff;
	}
	
	/**
	 * 获取按下的时的颜色
	 * @return
	 */
	public int getPressedColor(){
		return 0x80ff0000;
	}
	
	/**
	 * 获取标题文本的颜色
	 * @return
	 */
	public int getTitleTextColor(){
		return 0xff000000;
	}
	
	/**
	 * 获取副标题文本的颜色
	 * @return
	 */
	public int getSubTitleTextColor(){
		return 0xff000000;
	}
	
	@Override
	public void drawArea(Canvas canvas, Paint paint){
		canvas.save();
		paint.setColor(getAreaColor());
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
		paint.setColor(getPressedColor());
		
		canvas.save();
		if(isShowBubble()){
			if(!isClickedArea()){
				canvas.translate(getBubbleDrawableShowPoint(context).x, getBubbleDrawableShowPoint(context).y);
				canvas.drawRect(0, 0, bubbleDrawable.getIntrinsicWidth(), bubbleDrawable.getIntrinsicHeight() - (getVoidHeight() * getScale(context)), paint);
			}
		}else{
			canvas.drawRect(getLeft(), getTop(), getRight(), getBottom(), paint);
		}
		canvas.restore();
	}
	
	public float getScale(Context context){
		return (float) getBaseBubbleDrawable(context).getIntrinsicWidth()/getBubbleDrawableOriginalWidth();
	}
	
	@Override
	public boolean isClickArea(float x, float y){
		return x >= getLeft() && x <= getRight() && y >= getTop() && y <= getBottom();
	}

	@Override
	public boolean isClickBubble(Context context, float x, float y) {
		return x >= bubbleDrawableShowPoint.x && x <= (bubbleDrawableShowPoint.x +bubbleDrawable.getIntrinsicWidth()) && y >= bubbleDrawableShowPoint.y && y <= (bubbleDrawableShowPoint.y +(bubbleDrawable.getIntrinsicHeight() - (getVoidHeight() * getScale(context))));
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
	public void setClickedArea(boolean isClickedArea) {
		this.isClickedArea = isClickedArea;
	}

	@Override
	public boolean isClickedArea() {
		return isClickedArea;
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
			x -= getBubbleXOffset() * getScale(context);
			y -= bubbleDrawable.getIntrinsicHeight();
			bubbleDrawableShowPoint = new PointF(x, y);
		}
		return bubbleDrawableShowPoint;
	} 
	
	@Override
	public Drawable getBubbleDrawable(Context context) {
		if(bubbleDrawable == null){
			Paint titlePaint = new Paint();
			titlePaint.setTextSize(getTilteTextSize());
			titlePaint.setColor(getTitleTextColor());
			String title = StringUtils.checkLength(getTitle(), getTilteMaxLength());
			int titleNeedWidth = (int) TextUtils.getTextWidth(titlePaint, title);
			int titleNeedHeight = TextUtils.getTextHeightByBounds(title, titlePaint.getTextSize());
			
			Paint subTitlePaint = new Paint();
			subTitlePaint.setTextSize(getSubTilteTextSize());
			subTitlePaint.setColor(getSubTitleTextColor());
			String subTitle = StringUtils.checkLength(getSubTitle(), getSubTilteMaxLength());
			int subTitleNeedWidth = (int) TextUtils.getTextWidth(subTitlePaint, subTitle);
			int subTitleNeedHeight = TextUtils.getTextHeightByBounds(subTitle, subTitlePaint.getTextSize());
			
			int finalNeedWidth = titleNeedWidth>subTitleNeedWidth?titleNeedWidth:subTitleNeedWidth;
			int finalNeedHeight = titleNeedHeight + getIntervalOfHeight() + subTitleNeedHeight;
			
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
			canvas.drawText(subTitle, paddingRect.left, paddingRect.top + TextUtils.getTextLeading(subTitlePaint) + getIntervalOfHeight() + titleNeedHeight, subTitlePaint);
			
			bubbleDrawable = new BitmapDrawable(context.getResources(), bitmap);
			bubbleDrawable.setBounds(0, 0, bubbleDrawable.getIntrinsicWidth(), bubbleDrawable.getMinimumHeight());
		}
		return bubbleDrawable;
	}
}