package com.example.guidemap;

import java.util.List;

import me.xiaopan.easy.android.util.AndroidLogger;
import me.xiaopan.easy.android.util.Colors;
import me.xiaopan.easy.android.util.ToastUtils;
import me.xiaopan.easy.java.util.MathUtils;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

public class GuideMapView extends View implements GestureDetector.OnGestureListener{
	private Bitmap mapBitmap;
	private List<Booth> booths;
	private Paint rectPaint;
	private float left;
	private float top;
    private int viewWidth = -1;
    private int viewHeight = -1;
//	private GestureDetector gestureDetector;	//手势识别器
	private Matrix zoomMatrix;	//缩放Matrix
	private float zoomScale = 1.0f;	//缩放比例
	private float incrementZoomScale;	//增量缩放比例
	private float oldMoveDistance;	//旧的滑动距离，用于联合新的滑动距离计算增量缩放比例
	private Mode mode;	//操作方式
	private PointF scaleCenterPoint;	//缩放中心点
	private Drawable mDrawable;
	private MotionEvent oldMotionEvent;

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
		rectPaint = new Paint();
		scaleCenterPoint = new PointF();
		rectPaint.setColor(Colors.BLUE_TRANSLUCENT);
//		gestureDetector = new GestureDetector(getContext(), this);
		zoomMatrix = new Matrix();
		mDrawable = getResources().getDrawable(R.drawable.shape);
		mDrawable.setBounds(0, 0, 500, 300);
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
//		AndroidLogger.e("left="+left+"; top"+top);
		if(mDrawable != null){
			int saveCount = canvas.getSaveCount();
			canvas.save();
//			if(mode == Mode.ZOOM){
//				canvas.translate(left, top);
//			}else{
//				canvas.translate(left, top);
//			}
//			canvas.translate(left, top);
			if(zoomMatrix != null){
				canvas.concat(zoomMatrix);
			}
			mDrawable.draw(canvas);
			canvas.restoreToCount(saveCount);
		}
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		switch(event.getAction() & MotionEvent.ACTION_MASK){
			case MotionEvent.ACTION_DOWN : 
				mode = Mode.MOVE;
//				gestureDetector.onTouchEvent(event);
				oldMotionEvent = null;
				break;
			case MotionEvent.ACTION_POINTER_DOWN : //当有多个手指按下的时候，就初始化旧距离以及缩放中心点
				mode = Mode.ZOOM;
				oldMoveDistance = (float) MathUtils.pythagoreanProposition(Math.abs(event.getX(0) - event.getX(1)), Math.abs(event.getY(0) - event.getY(1)));
				scaleCenterPoint.set((event.getX(0) + event.getX(1))/2, (event.getY(0) + event.getY(1))/2);
				break;
			case MotionEvent.ACTION_MOVE : 
				if(mode == Mode.ZOOM){
					/* 计算增量缩放比例 */
					float newDistance = (float) MathUtils.pythagoreanProposition(Math.abs(event.getX(0) - event.getX(1)), Math.abs(event.getY(0) - event.getY(1)));
					incrementZoomScale = newDistance/oldMoveDistance;
					oldMoveDistance = newDistance;
					
					//计算总的缩放比例，并保证原图不会被缩小（如果新的缩放比例小于1，就修正增量缩放比例）
					if((zoomScale * incrementZoomScale) < 1){
						incrementZoomScale = 1.0f/zoomScale;
					}
					zoomScale *= incrementZoomScale;
					zoomMatrix.postScale(incrementZoomScale, incrementZoomScale, scaleCenterPoint.x, scaleCenterPoint.y);
				}else{
					
//					gestureDetector.onTouchEvent(event);
				}
				break;
			case MotionEvent.ACTION_POINTER_UP : 
				mode = Mode.MOVE;
				break;
			case MotionEvent.ACTION_UP : 
//				gestureDetector.onTouchEvent(event);
				mode = Mode.NONE;
				oldMotionEvent = null;
				break;
		}
		
		if(mode == Mode.MOVE){
			if(oldMotionEvent == null){
				AndroidLogger.e("滑动，eventX="+event.getX()+"; eventY="+event.getY());
			}else{
				AndroidLogger.e("滑动，oldEventX="+oldMotionEvent.getX()+"; oldEventY="+oldMotionEvent.getY()+"; eventX="+event.getX()+"; eventY="+event.getY());
			}
			if(oldMotionEvent != null){
				float distanceX = oldMotionEvent.getX() - event.getX();
				float distanceY = oldMotionEvent.getY() - event.getY();
				left -= distanceX;
		        top -= distanceY;
//		        AndroidLogger.e("滑动，distanceX="+distanceX+"; distanceY="+distanceY+"; left="+left+"; top="+top);
		        preventOutof(distanceX, distanceY);
				scrollTo((int) -left, (int) -top);
			}
			oldMotionEvent = event;
		}
		
		invalidate();
		return true;
	}
	
	@Override
	public boolean onDown(MotionEvent e) {
		return false;
	}

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
		return false;
	}

	@Override
	public void onLongPress(MotionEvent e) {
		
	}

	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
		left -= distanceX;
        top -= distanceY;
        preventOutof(distanceX, distanceY);
		scrollTo((int) -left, (int) -top);
		return false;
	}

	@Override
	public void onShowPress(MotionEvent e) {
		
	}

	@Override
	public boolean onSingleTapUp(MotionEvent e) {
		Booth booth = findBooth(e.getX()+left, e.getY()+top);
		if(booth != null){
			ToastUtils.toastS(getContext(), booth.getCompany().getAtrName());
		}else{
			ToastUtils.toastS(getContext(), "没有公司");
		}
		return false;
	}

	public void setMapBitmap(Bitmap mapBitmap) {
		this.mapBitmap = mapBitmap;
	}

	public void setBooths(List<Booth> booths) {
		this.booths = booths;
		if(mDrawable != null){
			unscheduleDrawable(mDrawable);
		}
		Bitmap showBitmap = mapBitmap.copy(Config.ARGB_8888, true);
		Canvas canvas = new Canvas(showBitmap);
		for(Booth booth : booths){
			canvas.drawRect(booth.getLeft(), booth.getTop(), booth.getRight(), booth.getBottom(), rectPaint);
		}
		mDrawable = new BitmapDrawable(getResources(), showBitmap);
		mDrawable.setBounds(0, 0, (int) getBorderWidth(), (int) getBorderHeight());
		invalidate();
	}
	
	private float getBorderWidth(){
		return mDrawable.getIntrinsicWidth() * zoomScale;
	}
	
	private float getBorderHeight(){
		return mDrawable.getIntrinsicHeight() * zoomScale;
	}

    /**
     * 防止超出范围
     */
    private void preventOutof(float distanceX, float distanceY){
        //初始化当前视图显示宽高
        if(viewWidth < 0){
            Rect viewRect = new Rect();
            getLocalVisibleRect(viewRect);
            viewWidth = viewRect.right - viewRect.left;
            viewHeight = viewRect.bottom - viewRect.top;
        }

        /* 限制其滚动超出范围 */
        if(distanceX > 0){
            if(left < -(getBorderWidth() - viewWidth)){
                left = -(getBorderWidth() - viewWidth);
            }
        }else{
            if(left > 0){
                left = 0;
            }
        }

        if(distanceY > 0){
            if(top < -(getBorderHeight() - viewHeight)){
                top = -(getBorderHeight() - viewHeight);
            }
        }else{
            if(top > 0){
                top = 0;
            }
        }
    }
    
    public Booth findBooth(float x, float y){
		for(Booth booth2 : booths){
			if(x >= booth2.getLeft() && x <= booth2.getRight() && y >= booth2.getTop() && y <= booth2.getBottom()){
				return booth2;
			}
		}
		return null;
    }
    
    public enum Mode{
    	NONE, MOVE, ZOOM;
    }

	@Override
	protected void onDetachedFromWindow() {
		super.onDetachedFromWindow();
		if(mapBitmap != null && !mapBitmap.isRecycled()){
			mapBitmap.recycle();
		}
		if(mDrawable != null){
			unscheduleDrawable(mDrawable);
		}
	}
}