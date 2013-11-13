package com.example.guidemap;

import java.util.ArrayList;
import java.util.List;

import me.xiaopan.easy.android.util.Colors;
import me.xiaopan.easy.android.util.ViewUtils;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.TextView;

import com.example.guidemap.SimpleGestureDetector.SimpleGestureListener;

/**
 * 导览图
 */
public class GuideMapView extends View implements SimpleGestureListener{
	private RectF displayRect;
	private Matrix drawMatrix;
	private Listener listener;
	private Drawable drawable;
	private List<Area> areas;
	private List<Area> bubbleAreas;
	private InitialZoomMode initialZoomMode;
	private SimpleGestureDetector simpleGestureDetector;
	private Area currentDownArea;
	private TextView textView;
	private RectF offsetRect;

	public GuideMapView(Context context) {
		super(context);
		init();
	}

	public GuideMapView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public GuideMapView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}
	
	private void init(){
		displayRect = new RectF();
		offsetRect = new RectF();
		initialZoomMode = InitialZoomMode.DEFAULT;
		simpleGestureDetector = new SimpleGestureDetector(this, this);
	}
	
	@Override
	protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
		super.onLayout(changed, left, top, right, bottom);
		simpleGestureDetector.getScaleContorller().init();
	}

	@Override
	protected void onDraw(Canvas canvas) {
		if(drawable != null){
			if(drawMatrix != null){
				canvas.concat(drawMatrix);
			}
			drawable.draw(canvas);	//绘制底图
			offsetRect.set(0, 0, 0, 0);
			if(bubbleAreas != null && bubbleAreas.size() > 0){	//绘制气泡
				for(Area area : bubbleAreas){
					if(area.isShowBubble()){
						area.drawBubble(getContext(), canvas);
						
						/* 记录四边的值，以便扩大显示区域，显示超出部分 */
						float left = area.getBubbleDrawableShowPoint(getContext()).x * simpleGestureDetector.getScaleContorller().getCurrentScale();
						if(left < 0 && left < offsetRect.left){
							offsetRect.left = area.getBubbleDrawableShowPoint(getContext()).x;
						}

						float top = area.getBubbleDrawableShowPoint(getContext()).y * simpleGestureDetector.getScaleContorller().getCurrentScale();
						if(top < 0 && top < offsetRect.top){
							offsetRect.top = area.getBubbleDrawableShowPoint(getContext()).y;
						}
						
						float right = (area.getBubbleDrawableShowPoint(getContext()).x + area.getBubbleDrawable(getContext()).getIntrinsicWidth()) * simpleGestureDetector.getScaleContorller().getCurrentScale();
						if(right > offsetRect.right){
							offsetRect.right = right;
						}
						
						float bottom = (area.getBubbleDrawableShowPoint(getContext()).y + area.getBubbleDrawable(getContext()).getIntrinsicHeight()) * simpleGestureDetector.getScaleContorller().getCurrentScale();
						if(bottom > offsetRect.bottom){
							offsetRect.bottom = bottom;
						}
					}
				}
			}
			if(currentDownArea != null){	//绘制按下状态
				currentDownArea.drawPressed(getContext(), canvas);
			}
		}
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		boolean result = false;
		if(simpleGestureDetector != null){
			result = simpleGestureDetector.onTouchEvent(event);
			invalidate();
		}
		return result;
	}

	/**
	 * 设置地图
	 * @param mapBitmap
	 * @param newAreas
	 */
	public void setMap(Bitmap mapBitmap, List<Area> newAreas) {
		if(mapBitmap != null && newAreas != null && newAreas.size() > 0){
			this.areas = newAreas;
			//释放旧的图片
			if(drawable != null){
				unscheduleDrawable(drawable);
			}
			
			/* 绘制新的图片 */
			Bitmap showBitmap = mapBitmap.copy(Config.ARGB_8888, true);
			mapBitmap.recycle();
			Canvas canvas = new Canvas(showBitmap);
			Paint rectPaint = new Paint();
			rectPaint.setColor(Colors.BLUE_TRANSLUCENT);
			for(Area area : areas){
				area.drawArea(canvas, rectPaint);
			}
			drawable = new BitmapDrawable(getResources(), showBitmap);
			drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
			if(drawMatrix == null){
				drawMatrix = new Matrix();
			}
			simpleGestureDetector.getScaleContorller().init();
			invalidate();
		}
	}
	
    /**
     * 获取可用宽度（去除左右内边距）
     * @return
     */
	public int getAvailableWidth() {
        return this.getWidth() - this.getPaddingLeft() - this.getPaddingRight();
    }

    /**
     * 获取可用高度（去除上下内边距）
     * @return
     */
    public int getAvailableHeight() {
        return this.getHeight() - this.getPaddingTop() - this.getPaddingBottom();
    }
	
    /**
     * 获取显示区域
     * @return
     */
    public final RectF getDisplayRect() {
    	if (drawable != null) {
			displayRect.set(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
			drawMatrix.mapRect(displayRect);
			if(textView != null){
				textView.setText(displayRect.toString());
			}
			
			/* 尝试扩大显示区域以便显示出超出的部分 */
			displayRect.left += offsetRect.left;
			displayRect.top += offsetRect.top;
			if(offsetRect.right > displayRect.width()){
				displayRect.right += offsetRect.right - displayRect.width();
			}
			if(offsetRect.bottom > displayRect.height()){
				displayRect.bottom += offsetRect.bottom - displayRect.height();
			}
			return displayRect;
		} else {
			return null;
		}
    }
    
	@Override
	public void onDown(MotionEvent motionEvent) {
		currentDownArea = findClickArea(motionEvent);
	}

	@Override
	public void onSingleTapUp(MotionEvent e) {
		if(currentDownArea != null){
			if(currentDownArea.isShowBubble()){
				if(!currentDownArea.isClickedArea()){
					listener.onClickAreaBubble(currentDownArea);
				}
			}else{
				listener.onClickArea(currentDownArea);
			}
		}
	}
	
	/**
	 * 查找点击的区域
	 * @param x
	 * @param y
	 * @return
	 */
	private Area findClickArea(MotionEvent e){
		if(bubbleAreas != null || areas != null){
			RectF rectF = getDisplayRect();
			if(rectF != null){
				float x = (e.getX()-rectF.left)/simpleGestureDetector.getScaleContorller().getCurrentScale();
				float y = (e.getY()-rectF.top)/simpleGestureDetector.getScaleContorller().getCurrentScale();
				
				Area clickArea = null;
				if(bubbleAreas != null && bubbleAreas.size() > 0){
					for(Area area : bubbleAreas){
						if(area.isClickBubble(getContext(), x, y)){
							clickArea = area;
							clickArea.setClickedArea(false);
							break;
						}
					}
				}
				
				if(clickArea == null && areas != null && areas.size() > 0){
					for(Area area : areas){
						if(area.isClickArea(x, y)){
							clickArea = area;
							clickArea.setClickedArea(true);
							break;
						}
					}
				}
				return clickArea;
			}else{
				return null;
			}
		}else{
			return null;
		}
	}
	
	@Override
	public void onDoubleTab(MotionEvent motionEvent) {
		
	}

	@Override
	public void onLongPress(MotionEvent motionEvent) {
		
	}

	@Override
	public void onUp(MotionEvent motionEvent) {
		if(currentDownArea != null){
			currentDownArea = null;
		}
	}

	@Override
	protected void onDetachedFromWindow() {
		super.onDetachedFromWindow();
		if(drawable != null){
			unscheduleDrawable(drawable);
		}
	}
	
	/**
	 * 显示一个气泡
	 * @param newArea
	 */
	public void showBubble(Area newArea){
		if(newArea != null){
			if(bubbleAreas == null){
				bubbleAreas = new ArrayList<Area>();
			}
			newArea.setShowBubble(true);
			bubbleAreas.add(newArea);
			invalidate();
		}
	}
	
	/**
	 * 显示一个气泡，会把之前所有的气泡全部清除
	 * @param newArea
	 */
	public void showSingleBubble(Area newArea){
		if(newArea != null){
			if(bubbleAreas == null){
				bubbleAreas = new ArrayList<Area>(1);
			}else{
				clearAllBubble();
			}
			newArea.setShowBubble(true);
			bubbleAreas.add(newArea);
			invalidate();
		}
	}
	
	/**
	 * 清除所有气泡
	 */
	public void clearAllBubble(){
		if(bubbleAreas != null && bubbleAreas.size() > 0){
			for(Area bubbleArea : bubbleAreas){
				bubbleArea.setShowBubble(false);
			}
			bubbleAreas.clear();
		}
	}
	
	/**
	 * 移动到指定位置
	 * @param x 指定位置的X坐标，例如100
	 * @param y 指定位置的Y坐标，例如200
	 */
	public void setTranslate(float x, float y){
		simpleGestureDetector.setTranslate(-x * simpleGestureDetector.getScaleContorller().getCurrentScale(), -y * simpleGestureDetector.getScaleContorller().getCurrentScale());
	}
	
	/**
	 * 缩放
	 * @param newScale 新的缩放比例
	 * @param focusX 缩放中心点
	 * @param focusY 缩放中心点
	 * @param animate
	 */
	public void setScale(float newScale, float focusX, float focusY, boolean animate){
		simpleGestureDetector.getScaleContorller().setScale(newScale, focusX, focusY, animate);
	}
	
	/**
	 * 缩放
	 * @param newScale 新的缩放比例
	 * @param focusX 缩放中心点
	 * @param focusY 缩放中心点
	 * @param animate
	 */
	public void setScale(float newScale, boolean animate){
		simpleGestureDetector.getScaleContorller().setScale(newScale, getRight() / 2, getBottom() / 2, animate);
	}
	
	/**
	 * 定位
	 * @param x
	 * @param y
	 */
	public void location(final Area area){
		if(getWidth() > 0){
			showSingleBubble(area);
			setScale(1.0f, false);
			int offsetWidth = 50;
			if(getWidth() > area.getBubbleDrawable(getContext()).getIntrinsicWidth()){
				offsetWidth = (getWidth() - area.getBubbleDrawable(getContext()).getIntrinsicWidth())/2;
			}
			int offsetHeight = (getHeight() - area.getBubbleDrawable(getContext()).getIntrinsicHeight())/2;
			setTranslate(area.getBubbleDrawableShowPoint(getContext()).x- offsetWidth, area.getBubbleDrawableShowPoint(getContext()).y - offsetHeight);
		}else{
			getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
				@Override
				public void onGlobalLayout() {
					location(area);
					ViewUtils.removeOnGlobalLayoutListener(getViewTreeObserver(), this);
				}
			});
		}
	}
	
	public Matrix getDrawMatrix() {
		return drawMatrix;
	}

	public Drawable getDrawable() {
		return drawable;
	}
	
	public void setListener(Listener listener) {
		this.listener = listener;
	}
	
	public InitialZoomMode getInitialZoomMode() {
		return initialZoomMode;
	}

	public void setInitialZoomMode(InitialZoomMode initialZoomMode) {
		this.initialZoomMode = initialZoomMode;
	}

	public enum InitialZoomMode{
		MIN, DEFAULT, MAX;
	}

	public interface Listener{
		public void onClickArea(Area area);
		public void onClickAreaBubble(Area area);
	}

	public TextView getTextView() {
		return textView;
	}

	public void setTextView(TextView textView) {
		this.textView = textView;
	}
}