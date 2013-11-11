package com.example.guidemap;

import java.util.List;

import me.xiaopan.easy.android.util.Colors;
import me.xiaopan.easy.android.util.TextUtils;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.example.guidemap.SimpleGestureDetector.SimpleGestureListener;
import com.example.guidemap.domain.Booth;

/**
 * 导览图
 */
public class GuideMapView extends View implements SimpleGestureListener{
	private Matrix drawMatrix;
	private Drawable drawable;
	private SimpleGestureDetector simpleGestureDetector;	//手势识别器
	private RectF displayRect;
	private InitialZoomMode initialZoomMode;
	private List<Area> areas;
	private Listener listener;
	private boolean showBubble;
	private Area area;
	private Drawable bubbleDrawable;

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
		initialZoomMode = InitialZoomMode.DEFAULT;
		simpleGestureDetector = new SimpleGestureDetector(this, this);
	}
	
	@Override
	protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
		super.onLayout(changed, left, top, right, bottom);
		simpleGestureDetector.getZoomContorller().init();
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		if(drawable != null){
			if(drawMatrix != null){
				canvas.concat(drawMatrix);
			}
			drawable.draw(canvas);
			if(bubbleDrawable != null && showBubble){
				canvas.save();
				PointF centerPoint = area.getCenterPoint();
				pointHandle(centerPoint);
				canvas.translate(centerPoint.x, centerPoint.y);
				bubbleDrawable.draw(canvas);
				canvas.restore();
			}
		}
	}
	
	private void pointHandle(PointF pointF){
		pointF.x -= 40;
		pointF.y -= bubbleDrawable.getIntrinsicHeight();
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
				area.draw(canvas, rectPaint);
			}
			drawable = new BitmapDrawable(getResources(), showBitmap);
			drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
			if(drawMatrix == null){
				drawMatrix = new Matrix();
			}
			simpleGestureDetector.getZoomContorller().init();
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
			return displayRect;
		} else {
			return null;
		}
    }

	@Override
	public void onDown(MotionEvent motionEvent) {
		
	}

	@Override
	public void onSingleTapUp(MotionEvent e) {
		if(listener != null && drawable != null && areas != null && areas.size() > 0){
			RectF rectF = getDisplayRect();
			if(rectF != null){
				float x = (e.getX()-rectF.left)/simpleGestureDetector.getZoomContorller().getCurrentScale();
				float y = (e.getY()-rectF.top)/simpleGestureDetector.getZoomContorller().getCurrentScale();
				Area clickArea = null;
				for(Area area : areas){
					if(area.isClickMe(x, y)){
						clickArea = area;
						break;
					}
				}
				if(clickArea != null){
					listener.onClickArea(clickArea);
				}
			}
		}
	}
	
	@Override
	public void onDoubleTab(MotionEvent motionEvent) {
		
	}

	@Override
	public void onLongPress(MotionEvent motionEvent) {
		
	}

	@Override
	protected void onDetachedFromWindow() {
		super.onDetachedFromWindow();
		if(drawable != null){
			unscheduleDrawable(drawable);
		}
	}
	
	public void showBubble(Area area){
		if(area instanceof Booth){
			Booth booth = (Booth) area;
			this.area = area;
			showBubble = true;
			
			Paint paint = new Paint();
			paint.setTextSize(20);
			paint.setColor(Colors.BLACK);
			String text = booth.getCompany().getAtrName();
			int needWidth = (int) TextUtils.getTextWidth(paint, text);
			
			Drawable backgDrawable = getResources().getDrawable(R.drawable.bubble);
			Rect paddingRect = new Rect();
			backgDrawable.getPadding(paddingRect);
			if(backgDrawable.getMinimumWidth() > needWidth + paddingRect.left + paddingRect.right){
				backgDrawable.setBounds(0, 0, backgDrawable.getMinimumWidth(), backgDrawable.getMinimumHeight());
			}else{
				backgDrawable.setBounds(0, 0, (int) (needWidth + paddingRect.left + paddingRect.right), backgDrawable.getMinimumHeight());
			}
			
			Bitmap bitmap = Bitmap.createBitmap(backgDrawable.getBounds().width(), backgDrawable.getBounds().height(), Config.ARGB_8888);
			Canvas canvas = new Canvas(bitmap);
			backgDrawable.draw(canvas);
			
			canvas.drawText(booth.getCompany().getAtrName(), paddingRect.left, paddingRect.top + TextUtils.getTextLeading(paint), paint);
			
			bubbleDrawable = new BitmapDrawable(bitmap);
			bubbleDrawable.setBounds(0, 0, bubbleDrawable.getIntrinsicWidth(), bubbleDrawable.getMinimumHeight());
			invalidate();
		}
	}
	
	public Matrix getDrawMatrix() {
		return drawMatrix;
	}

	public Drawable getDrawable() {
		return drawable;
	}
	
	public InitialZoomMode getInitialZoomMode() {
		return initialZoomMode;
	}

	public void setInitialZoomMode(InitialZoomMode initialZoomMode) {
		this.initialZoomMode = initialZoomMode;
	}

	public void setListener(Listener listener) {
		this.listener = listener;
	}

	public enum InitialZoomMode{
		MIN, DEFAULT, MAX;
	}
	
	public interface Listener{
		public void onClickArea(Area area);
	}
}