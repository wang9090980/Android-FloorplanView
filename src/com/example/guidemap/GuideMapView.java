package com.example.guidemap;

import java.util.List;

import me.xiaopan.easy.android.util.Colors;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.example.guidemap.SimpleGestureDetector.SimpleGestureListener;

public class GuideMapView extends View implements SimpleGestureListener{
	private Matrix zoomMatrix;
	private Drawable mDrawable;
	private SimpleGestureDetector simpleGestureDetector;	//手势识别器

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
		simpleGestureDetector = new SimpleGestureDetector(getContext(), this);
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		if(mDrawable != null){
			canvas.save();
			if(zoomMatrix != null){
				canvas.concat(zoomMatrix);
			}
			mDrawable.draw(canvas);
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
		if(mDrawable != null){
			unscheduleDrawable(mDrawable);
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
		mDrawable = new BitmapDrawable(getResources(), showBitmap);
		mDrawable.setBounds(0, 0, mDrawable.getIntrinsicWidth(), mDrawable.getIntrinsicHeight());
		if(zoomMatrix == null){
			zoomMatrix = new Matrix();
		}
		invalidate();
	}
	
	@Override
	public void onMove(float distanceX, float distanceY) {
		if(mDrawable != null && zoomMatrix != null){
			zoomMatrix.postTranslate(-distanceX, -distanceY);
			invalidate();
		}
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
	public void onScale(float scaleFactor, float focusX, float focusY) {
		if(mDrawable != null && zoomMatrix != null){
			zoomMatrix.postScale(scaleFactor, scaleFactor, focusX, focusY);
			invalidate();
		}
	}

	@Override
	public void onDoubleTab(MotionEvent motionEvent) {
		
	}

	@Override
	public void onLongPress(MotionEvent motionEvent) {
		
	}

	@Override
	public void onFling(float velocityX, float velocityY) {
		
	}
    
	@Override
	protected void onDetachedFromWindow() {
		super.onDetachedFromWindow();
		if(mDrawable != null){
			unscheduleDrawable(mDrawable);
		}
	}
}