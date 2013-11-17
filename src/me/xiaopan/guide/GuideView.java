package me.xiaopan.guide;

import java.util.ArrayList;
import java.util.List;

import me.xiaopan.easy.android.util.ViewUtils;
import me.xiaopan.guide.SimpleGestureDetector.SimpleGestureListener;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.TextView;

/**
 * 导览图
 */
public class GuideView extends View implements SimpleGestureListener{
	private RectF displayRect;
	private Matrix drawMatrix;
	private Listener listener;
	private BitmapDrawable drawable;
	private List<Area> areas;
	private List<Area> bubbleAreas;
	private InitialZoomMode initialZoomMode;
	private SimpleGestureDetector simpleGestureDetector;
	private Area currentDownArea;
	private TextView textView;
	private RectF offsetRect;
	Paint rectPaint = new Paint();

	public GuideView(Context context) {
		super(context);
		init();
	}

	public GuideView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public GuideView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}
	
	private void init(){
		displayRect = new RectF();
		offsetRect = new RectF();
		initialZoomMode = InitialZoomMode.MIN;
	}
	
	@Override
	protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
		super.onLayout(changed, left, top, right, bottom);
		if(isAllow()){
			simpleGestureDetector.getScaleContorller().init();
		}
	}

	@Override
	protected void onDraw(Canvas canvas) {
		if(drawable != null){
			if(drawMatrix != null){
				canvas.concat(drawMatrix);
			}
			drawable.draw(canvas);	//绘制底图
			for(Area area : areas){
				area.drawArea(canvas, rectPaint);
			}
			if(currentDownArea != null && !(currentDownArea.isShowBubble() && !currentDownArea.isClickedArea())){	//绘制按下状态
				currentDownArea.drawPressed(getContext(), canvas);
			}
			offsetRect.set(0, 0, 0, 0);
			if(bubbleAreas != null && bubbleAreas.size() > 0){	//绘制气泡
				for(Area area : bubbleAreas){
					if(area.isShowBubble()){
						area.drawBubble(getContext(), canvas);
						checkOffset(area);
					}
				}
			}
			if(currentDownArea != null && currentDownArea.isShowBubble() && !currentDownArea.isClickedArea()){	//绘制按下状态
				currentDownArea.drawPressed(getContext(), canvas);
			}
		}
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if(isAllow()){
			boolean result = false;
			result = simpleGestureDetector.onTouchEvent(event);
			invalidate();
			return result;
		}else{
			return super.onTouchEvent(event);
		}
	}

	/**
	 * 设置地图
	 * @param mapBitmap 地图图片
	 * @param newAreas 地图上的区域
	 * @param mapWidth 地图的宽，如果该值小于0将使用mapBitmap.getIntrinsicWidth()代替
	 * @param mapHeight 地图的高，如果该值小于0将使用mapBitmap.getIntrinsicHeight()代替
	 */
	public void setMap(Bitmap mapBitmap, List<Area> newAreas, int mapWidth, int mapHeight) {
		if(drawable != null){
			drawable.getBitmap().recycle();
			drawable.setCallback(null);
			unscheduleDrawable(drawable);
			drawable = null;
		}
		
		if(drawMatrix != null){
			drawMatrix.reset();
			drawMatrix = null;
		}
		
		if(simpleGestureDetector != null){
			simpleGestureDetector = null;
		}
		
		if(mapBitmap != null && newAreas != null && newAreas.size() > 0){
			this.areas = newAreas;
			drawable = new BitmapDrawable(getResources(), mapBitmap);
			drawable.setBounds(0, 0, mapWidth>0?mapWidth:drawable.getIntrinsicWidth(), mapHeight > 0?mapHeight:drawable.getIntrinsicHeight());
			drawMatrix = new Matrix();
			simpleGestureDetector = new SimpleGestureDetector(this, this);
			simpleGestureDetector.getScaleContorller().init();
			invalidate();
		}
	}

	/**
	 * 设置地图
	 * @param mapBitmap
	 * @param newAreas
	 */
	public void setMap(Bitmap mapBitmap, List<Area> newAreas) {
		setMap(mapBitmap, newAreas, -1, -1);
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
    	if (isAllow()) {
			displayRect.set(drawable.getBounds());
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
		if(isAllow()){
			currentDownArea = findClickArea(motionEvent);
		}
	}

	@Override
	public void onSingleTapUp(MotionEvent e) {
		if(isAllow() && currentDownArea != null){
			if(currentDownArea.isShowBubble()){
				if(!currentDownArea.isClickedArea()){
					if(listener != null){
						listener.onClickAreaBubble(currentDownArea);
					}
				}
			}else{
				if(listener != null){
					listener.onClickArea(currentDownArea);
				}
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
		if(isAllow() && bubbleAreas != null || areas != null){
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
		if(isAllow() && currentDownArea != null){
			currentDownArea = null;
		}
	}

	@Override
	protected void onDetachedFromWindow() {
		super.onDetachedFromWindow();
		if(drawable != null){
			drawable.getBitmap().recycle();
			drawable.setCallback(null);
			unscheduleDrawable(drawable);
		}
	}
	
	/**
	 * 显示一个气泡
	 * @param newArea
	 */
	public void showBubble(Area newArea){
		if(isAllow() && newArea != null){
			if(bubbleAreas == null){
				bubbleAreas = new ArrayList<Area>();
			}
			newArea.setShowBubble(true, this);
			bubbleAreas.add(newArea);
			offsetRect.set(0, 0, 0, 0);
			checkOffset(newArea);
			simpleGestureDetector.checkMatrixBounds();
			invalidate();
		}
	}
	
	/**
	 * 显示一个气泡，会把之前所有的气泡全部清除
	 * @param newArea
	 */
	public void showSingleBubble(Area newArea){
		if(isAllow() && newArea != null){
			/* 清除已有的气泡 */
			if(bubbleAreas == null){
				bubbleAreas = new ArrayList<Area>(1);
			}else{
				clearAllBubble();
			}
			
			/* 添加气泡 */
			newArea.setShowBubble(true, this);
			bubbleAreas.add(newArea);
			invalidate();
			
			/* 尝试移动屏幕到气泡的位置，知道把气泡完全显示出来 */
			Point point = computeScrollOffset(bubbleAreas.get(0).getBubbleRect(getContext()));
			if(point != null){
				getHandler().post(new ScrollRunnable(simpleGestureDetector, point.x, point.y));
			}
		}
	}
	
	/**
	 * 计算滚动位置
	 * @param rectf
	 * @return
	 */
	private Point computeScrollOffset(RectF tempRectf){
		RectF rectf = new RectF(tempRectf);
		rectf.left *= simpleGestureDetector.getScaleContorller().getCurrentScale();
		rectf.top *= simpleGestureDetector.getScaleContorller().getCurrentScale();
		rectf.right *= simpleGestureDetector.getScaleContorller().getCurrentScale();
		rectf.bottom *= simpleGestureDetector.getScaleContorller().getCurrentScale();
		
		RectF offsetRect = new RectF(Math.abs(getDisplayRect().left) - rectf.left, Math.abs(getDisplayRect().top) - rectf.top, rectf.right - (Math.abs(getDisplayRect().left) + getWidth()), rectf.bottom - (Math.abs(getDisplayRect().top) + getHeight()));
		
		float xOffset = 0;
		if(offsetRect.left > 0){
			xOffset = offsetRect.left;
		}else if(offsetRect.right > 0){
			if(rectf.width() > getWidth()){
				xOffset = offsetRect.left;
			}else{
				xOffset = -offsetRect.right;
			}
		}
		
		float yOffset = 0;
		if(offsetRect.top > 0){
			yOffset = offsetRect.top;
		}else if(offsetRect.bottom > 0){
			yOffset = -offsetRect.bottom;
		}
		
		if(xOffset != 0 && rectf.width() < getWidth()){
			xOffset += (getWidth() - rectf.width()) / 2 * (xOffset > 0?1:-1);
		}
		
		if(yOffset != 0 && rectf.height() < getHeight()){
			yOffset += (getHeight() - rectf.height()) / 2 * (yOffset > 0?1:-1);
		}
		
		if(xOffset != 0 || yOffset != 0){
			return new Point((int) xOffset, (int) yOffset);
		}else{
			return null;
		}
	}
	
	/**
	 * 清除所有气泡
	 */
	public void clearAllBubble(){
		if(isAllow() && bubbleAreas != null && bubbleAreas.size() > 0){
			for(Area bubbleArea : bubbleAreas){
				bubbleArea.setShowBubble(false, this);
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
		if(isAllow()){
			simpleGestureDetector.setTranslate(-x * simpleGestureDetector.getScaleContorller().getCurrentScale(), -y * simpleGestureDetector.getScaleContorller().getCurrentScale());
		}
	}
	
	/**
	 * 缩放
	 * @param newScale 新的缩放比例
	 * @param focusX 缩放中心点
	 * @param focusY 缩放中心点
	 * @param animate
	 */
	public void setScale(float newScale, float focusX, float focusY, boolean animate){
		if(isAllow()){
			simpleGestureDetector.getScaleContorller().setScale(newScale, focusX, focusY, animate);
		}
	}
	
	/**
	 * 缩放
	 * @param newScale 新的缩放比例
	 * @param focusX 缩放中心点
	 * @param focusY 缩放中心点
	 * @param animate
	 */
	public void setScale(float newScale, boolean animate){
		if(isAllow()){
			simpleGestureDetector.getScaleContorller().setScale(newScale, getRight() / 2, getBottom() / 2, animate);
		}
	}
	
	/**
	 * 定位
	 * @param x
	 * @param y
	 */
	public void location(final Area area){
		if(isAllow() && area != null){
			if(getWidth() > 0){
				setScale(1.0f, false);
				showSingleBubble(area);
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
	}
	
	private void checkOffset(Area area){
		/* 记录四边的值，以便扩大显示区域，显示超出部分 */
		float left = area.getBubbleRect(getContext()).left * simpleGestureDetector.getScaleContorller().getCurrentScale();
		if(left < 0 && left < offsetRect.left){
			offsetRect.left = left;
		}

		float top = area.getBubbleRect(getContext()).top * simpleGestureDetector.getScaleContorller().getCurrentScale();
		if(top < 0 && top < offsetRect.top){
			offsetRect.top = top;
		}
		
		float right = area.getBubbleRect(getContext()).right * simpleGestureDetector.getScaleContorller().getCurrentScale();
		if(right > offsetRect.right){
			offsetRect.right = right;
		}
		
		float bottom = area.getBubbleRect(getContext()).bottom * simpleGestureDetector.getScaleContorller().getCurrentScale();
		if(bottom > offsetRect.bottom){
			offsetRect.bottom = bottom;
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
	
	public boolean isAllow(){
		return drawable != null && drawMatrix != null && simpleGestureDetector != null;
	}
}