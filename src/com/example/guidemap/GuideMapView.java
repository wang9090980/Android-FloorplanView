package com.example.guidemap;

import java.util.List;

import me.xiaopan.easy.android.util.Colors;
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

import com.example.guidemap.SimpleGestureDetector.SimpleGestureListener;

/**
 * 导览图
 */
public class GuideMapView extends View implements SimpleGestureListener{
	private Matrix drawMatrix;
	private Drawable drawable;
	private SimpleGestureDetector simpleGestureDetector;	//手势识别器
	private final RectF mDisplayRect = new RectF();

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
			canvas.save();
			if(drawMatrix != null){
				canvas.concat(drawMatrix);
			}
			drawable.draw(canvas);
			canvas.restore();
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

	public void setMap(Bitmap mapBitmap, List<Booth> booths) {
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
		for(Booth booth : booths){
			canvas.drawRect(booth.getLeft(), booth.getTop(), booth.getRight(), booth.getBottom(), rectPaint);
		}
		drawable = new BitmapDrawable(getResources(), showBitmap);
		drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
		if(drawMatrix == null){
			drawMatrix = new Matrix();
		}
		simpleGestureDetector.getZoomContorller().init();
		invalidate();
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
			mDisplayRect.set(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
			drawMatrix.mapRect(mDisplayRect);
			return mDisplayRect;
		} else {
			return null;
		}
    }

	@Override
	public void onDown(MotionEvent motionEvent) {
		
	}

	@Override
	public void onSingleTapUp(MotionEvent e) {
//		Booth booth = findBooth(e.getX()+left, e.getY()+top);
//		if(booth != null){
//			ToastUtils.toastS(getContext(), booth.getCompany().getAtrName());
//		}else{
//			ToastUtils.toastS(getContext(), "没有公司");
//		}
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
	
	public Matrix getDrawMatrix() {
		return drawMatrix;
	}

	public Drawable getDrawable() {
		return drawable;
	}
}