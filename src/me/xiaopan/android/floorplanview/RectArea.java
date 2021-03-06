/*
 * Copyright (C) 2013 Peng fei Pan <sky@xiaopan.me>
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package me.xiaopan.android.floorplanview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;

/**
 * 矩形区域
 */
public abstract class RectArea implements Area{
	private int baseBubbleDrawableWidth = -100;
	private boolean isShowBubble;
	private boolean isClickedArea;
	private RectF bubbleRect;
	protected Drawable bubbleDrawable;
	
	/**
	 * 获取绘制区域的坐标
	 * @return
	 */
	public abstract RectF getAreaRect();
	
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
	public void drawArea(Canvas canvas, Paint paint, float scale){
		paint.setColor(getAreaColor());
		paint.setAntiAlias(true);
		RectF rect = new RectF(getAreaRect());
		rect.left *= scale;
		rect.top *= scale;
		rect.right *= scale;
		rect.bottom *= scale;
		canvas.drawRect(rect, paint);
	}

	@Override
	public void drawBubble(Context context, Canvas canvas) {
		canvas.save();
		canvas.translate(getBubbleRect(context).left, getBubbleRect(context).top);
		getBubbleDrawable(context).draw(canvas);
		canvas.restore();
	}
	
	@Override
	public void drawPressed(Context context, Canvas canvas){
		Paint paint = new Paint();
		paint.setColor(getPressedColor());
		paint.setAntiAlias(true);
		
		if(isShowBubble()){
			if(!isClickedArea() && bubbleRect != null && bubbleDrawable != null){
				canvas.save();
				canvas.translate(bubbleRect.left, bubbleRect.top);
				canvas.drawRect(0, 0, bubbleDrawable.getBounds().width(), bubbleDrawable.getBounds().height() - (getVoidHeight() * getScale(context)), paint);
				canvas.restore();
			}
		}else{
			canvas.drawRect(getAreaRect(), paint);
		}
	}
	
	private float getScale(Context context){
		if(baseBubbleDrawableWidth == -100){
			baseBubbleDrawableWidth = getBaseBubbleDrawable(context).getIntrinsicWidth();
		}
		return (float) baseBubbleDrawableWidth/getBubbleDrawableOriginalWidth();
	}
	
	@Override
	public boolean isClickArea(float x, float y){
		return getAreaRect().contains(x, y);
	}

	@Override
	public boolean isClickBubble(Context context, float x, float y) {
		if(bubbleRect != null && bubbleDrawable != null){
			return x >= bubbleRect.left && x <= bubbleRect.right && y >= bubbleRect.top && y <= (bubbleRect.bottom - (getVoidHeight() * getScale(context)));
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
	public RectF getBubbleRect(Context context) {
		if(bubbleRect == null){
			bubbleRect = new RectF();	
			bubbleRect.left = (getAreaRect().left + getAreaRect().right)/2;//默认位置为矩形的中心
			bubbleRect.top = (getAreaRect().top + getAreaRect().bottom)/2;
			bubbleRect.left = (getAreaRect().left + bubbleRect.left)/2;	//再次将位置移动至矩形的四分之一处（左上角）
			bubbleRect.top = (getAreaRect().top + bubbleRect.top)/2;
			bubbleRect.top -= getBubbleDrawable(context).getBounds().height();	//再次根据气泡的高度便宜Y坐标
			if(getBubbleXOffset() > 0){
				bubbleRect.left -= getBubbleXOffset() * getScale(context);	//再次根据气泡的X轴偏移量 便宜X坐标
			}else{
				bubbleRect.left -= Math.abs(getBubbleDrawable(context).getBounds().width()) - (Math.abs(getBubbleXOffset()) * getScale(context));	//再次根据气泡的X轴偏移量便移X坐标
			}
			bubbleRect.right = bubbleRect.left + getBubbleDrawable(context).getBounds().width();
			bubbleRect.bottom = bubbleRect.top + getBubbleDrawable(context).getBounds().height();
		}
		return bubbleRect;
	}

	@Override
	public Drawable getBubbleDrawable(Context context) {
		if(bubbleDrawable == null){
			Paint paint = new Paint();
			paint.setAntiAlias(true);//抗锯齿
			paint.setFilterBitmap(true);//对文字进行滤波处理，增强绘制效果
			
			/* 测量标题需要的宽和高 */
			paint.setTextSize(getTilteTextSize());
			paint.setColor(getTitleTextColor());
			String title = RequiredUtils.checkLength(getTitle(), getTilteMaxLength());
			int titleNeedWidth = (int) RequiredUtils.getTextWidth(paint, title);
			int titleNeedHeight = RequiredUtils.getTextHeightByBounds(title, paint.getTextSize());
			
			/* 测量副标题需要的宽和高 */
			paint.setTextSize(getSubTilteTextSize());
			paint.setColor(getSubTitleTextColor());
			String subTitle = RequiredUtils.checkLength(getSubTitle(), getSubTilteMaxLength());
			int subTitleNeedWidth = (int) RequiredUtils.getTextWidth(paint, subTitle);
			int subTitleNeedHeight = RequiredUtils.getTextHeightByBounds(subTitle, paint.getTextSize());
			
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
			canvas.drawText(title, paddingRect.left, paddingRect.top + RequiredUtils.getTextLeading(paint), paint);

			/* 在新的气泡图片上绘制副标题 */
			paint.setTextSize(getSubTilteTextSize());
			paint.setColor(getSubTitleTextColor());
			canvas.drawText(subTitle, backgDrawable.getBounds().width() - paddingRect.right - subTitleNeedWidth, paddingRect.top + RequiredUtils.getTextLeading(paint) + getIntervalOfHeight() + titleNeedHeight, paint);
			
			bubbleDrawable = new BitmapDrawable(context.getResources(), bitmap);
			bubbleDrawable.setBounds(0, 0, bubbleDrawable.getIntrinsicWidth(), bubbleDrawable.getMinimumHeight());
		}
		return bubbleDrawable;
	}
}