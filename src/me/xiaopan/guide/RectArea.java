package me.xiaopan.guide;

import me.xiaopan.easy.android.util.TextUtils;
import me.xiaopan.easy.java.util.StringUtils;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;

/**
 * 矩形区域
 */
public abstract class RectArea implements Area{
	protected PointF bubbleDrawableShowPoint;
	protected Drawable bubbleDrawable;
	private boolean isShowBubble;
	private boolean isClickedArea;
	private int baseBubbleDrawableWidth;
	
	/**
	 * 获取坐标
	 * @return
	 */
	public abstract RectF getRect();
	
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
	public int getBubbleXOffset(){
		return 0;
	}
	
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
		canvas.drawRect(getRect(), paint);
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
			if(!isClickedArea() && bubbleDrawableShowPoint != null && bubbleDrawable != null){
				canvas.translate(bubbleDrawableShowPoint.x, bubbleDrawableShowPoint.y);
				canvas.drawRect(0, 0, bubbleDrawable.getBounds().width(), bubbleDrawable.getBounds().height() - (getVoidHeight() * getScale(context)), paint);
			}
		}else{
			canvas.drawRect(getRect(), paint);
		}
		canvas.restore();
	}
	
	private float getScale(Context context){
		return (float) baseBubbleDrawableWidth/getBubbleDrawableOriginalWidth();
	}
	
	@Override
	public boolean isClickArea(float x, float y){
		return getRect().contains(x, y);
	}

	@Override
	public boolean isClickBubble(Context context, float x, float y) {
		if(bubbleDrawableShowPoint != null && bubbleDrawable != null){
			return x >= bubbleDrawableShowPoint.x && x <= (bubbleDrawableShowPoint.x +bubbleDrawable.getBounds().width()) && y >= bubbleDrawableShowPoint.y && y <= (bubbleDrawableShowPoint.y +(bubbleDrawable.getBounds().height() - (getVoidHeight() * getScale(context))));
		}else{
			return false;
		}
	}

	@Override
	public boolean isShowBubble() {
		return isShowBubble;
	}

	@Override
	public void setShowBubble(boolean isShowBubble, View view) {
		this.isShowBubble = isShowBubble;
		if(!isShowBubble && bubbleDrawable != null){
			bubbleDrawable.setCallback(null);
			view.unscheduleDrawable(bubbleDrawable);
			bubbleDrawable = null;
		}
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
			bubbleDrawableShowPoint = new PointF((getRect().left + getRect().right)/2, (getRect().top + getRect().bottom)/2);
			bubbleDrawableShowPoint.x = (getRect().left + bubbleDrawableShowPoint.x)/2;
			bubbleDrawableShowPoint.y = (getRect().top + bubbleDrawableShowPoint.y)/2;
			if(bubbleDrawable == null){
				getBubbleDrawable(context);
			}
			bubbleDrawableShowPoint.x -= getBubbleXOffset() * getScale(context);
			bubbleDrawableShowPoint.y -= bubbleDrawable.getBounds().height();
		}
		return bubbleDrawableShowPoint;
	} 
	
	@Override
	public Drawable getBubbleDrawable(Context context) {
		if(bubbleDrawable == null){
			Paint paint = new Paint();
			paint.setAntiAlias(true);//去除锯齿
			paint.setFilterBitmap(true);//对文字进行滤波处理，增强绘制效果
			
			/* 测量标题需要的宽和高 */
			paint.setTextSize(getTilteTextSize());
			paint.setColor(getTitleTextColor());
			String title = StringUtils.checkLength(getTitle(), getTilteMaxLength());
			int titleNeedWidth = (int) TextUtils.getTextWidth(paint, title);
			int titleNeedHeight = TextUtils.getTextHeightByBounds(title, paint.getTextSize());
			
			/* 测量副标题需要的宽和高 */
			paint.setTextSize(getSubTilteTextSize());
			paint.setColor(getSubTitleTextColor());
			String subTitle = StringUtils.checkLength(getSubTitle(), getSubTilteMaxLength());
			int subTitleNeedWidth = (int) TextUtils.getTextWidth(paint, subTitle);
			int subTitleNeedHeight = TextUtils.getTextHeightByBounds(subTitle, paint.getTextSize());
			
			/* 计算最终气泡需要的宽高 */
			int finalNeedWidth = titleNeedWidth>subTitleNeedWidth?titleNeedWidth:subTitleNeedWidth;
			int finalNeedHeight = titleNeedHeight + getIntervalOfHeight() + subTitleNeedHeight;
			Drawable backgDrawable = getBaseBubbleDrawable(context);
			baseBubbleDrawableWidth = backgDrawable.getIntrinsicWidth();
			Rect paddingRect = new Rect();
			backgDrawable.getPadding(paddingRect);
			finalNeedWidth += paddingRect.left + paddingRect.right;
			finalNeedHeight += paddingRect.top + paddingRect.bottom;
			backgDrawable.setBounds(0, 0, backgDrawable.getIntrinsicWidth()>finalNeedWidth?backgDrawable.getIntrinsicWidth():finalNeedWidth, backgDrawable.getIntrinsicHeight()>finalNeedHeight?backgDrawable.getIntrinsicHeight():finalNeedHeight);
			
			/* 创建新的气泡图片 */
			Bitmap bitmap = Bitmap.createBitmap(backgDrawable.getBounds().width(), backgDrawable.getBounds().height(), Config.ARGB_8888);
			Canvas canvas = new Canvas(bitmap);
			backgDrawable.draw(canvas);
			
			/* 在新的气泡图片上绘制标题 */
			paint.setTextSize(getTilteTextSize());
			paint.setColor(getTitleTextColor());
			canvas.drawText(title, paddingRect.left, paddingRect.top + TextUtils.getTextLeading(paint), paint);

			/* 在新的气泡图片上绘制副标题 */
			paint.setTextSize(getSubTilteTextSize());
			paint.setColor(getSubTitleTextColor());
			canvas.drawText(subTitle, backgDrawable.getBounds().width() - paddingRect.right - subTitleNeedWidth, paddingRect.top + TextUtils.getTextLeading(paint) + getIntervalOfHeight() + titleNeedHeight, paint);
			
			bubbleDrawable = new BitmapDrawable(context.getResources(), bitmap);
			bubbleDrawable.setBounds(0, 0, bubbleDrawable.getIntrinsicWidth(), bubbleDrawable.getMinimumHeight());
		}
		return bubbleDrawable;
	}
}