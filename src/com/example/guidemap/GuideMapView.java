package com.example.guidemap;

import java.util.List;

import me.xiaopan.easy.android.util.AndroidLogger;
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
import android.widget.Scroller;

import com.example.guidemap.SimpleGestureDetector.SimpleGestureListener;

public class GuideMapView extends View implements SimpleGestureListener{
	private Matrix drawMatrix;
	private Drawable mDrawable;
	private SimpleGestureDetector simpleGestureDetector;	//手势识别器
	private final RectF mDisplayRect = new RectF();
	private FlingScrollRunnable flingScrollRunnable;
	private float scaling = 1.0f;
	private float minScaling;	//最小缩放比例

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
	protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
		super.onLayout(changed, left, top, right, bottom);
		AndroidLogger.d("宽："+getWidth()+"；高："+getHeight());
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		if(mDrawable != null){
			canvas.save();
			if(drawMatrix != null){
				canvas.concat(drawMatrix);
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
		if(drawMatrix == null){
			drawMatrix = new Matrix();
		}
		invalidate();
	}
	
	/**
	 * 检查矩阵边界，防止滑动时超出边界
	 */
	private void checkMatrixBounds(){
		RectF rect = getDisplayRect(drawMatrix);
		AndroidLogger.d(rect.toString());
		 final float height = rect.height(), width = rect.width();
        float deltaX = 0, deltaY = 0;
        
        final int viewHeight = getAvailableHeight();
        if (height <= viewHeight) {
            deltaY = (viewHeight - height) / 2 - rect.top;
        } else if (rect.top > 0) {
            deltaY = -rect.top;
        } else if (rect.bottom < viewHeight) {
            deltaY = viewHeight - rect.bottom;
        }

        final int viewWidth = getAvailableWidth();
        if (width <= viewWidth) {
            deltaX = (viewWidth - width) / 2 - rect.left;
        } else if (rect.left > 0) {
            deltaX = -rect.left;
        } else if (rect.right < viewWidth) {
            deltaX = viewWidth - rect.right;
        } else {
        }

        drawMatrix.postTranslate(deltaX, deltaY);
	}

    /**
     * 获取可用宽度（去除左右内边距）
     * @return
     */
    private int getAvailableWidth() {
        return this.getWidth() - this.getPaddingLeft() - this.getPaddingRight();
    }

    /**
     * 获取可用高度（去除上下内边距）
     * @return
     */
    private int getAvailableHeight() {
        return this.getHeight() - this.getPaddingTop() - this.getPaddingBottom();
    }
	
	 /**
     * Helper method that maps the supplied Matrix to the current Drawable
     * @param matrix - Matrix to map Drawable against
     * @return RectF - Displayed Rectangle
     */
    public RectF getDisplayRect(Matrix matrix) {
        if (null != mDrawable) {
            mDisplayRect.set(0, 0, mDrawable.getIntrinsicWidth(), mDrawable.getIntrinsicHeight());
            matrix.mapRect(mDisplayRect);
            return mDisplayRect;
        }else{
        	return null;
        }
    }

    public final RectF getDisplayRect() {
        checkMatrixBounds();
        return getDisplayRect(drawMatrix);
    }

	@Override
	public void onDown(MotionEvent motionEvent) {
		if(flingScrollRunnable != null){
			flingScrollRunnable.cancelFling();
			flingScrollRunnable = null;
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
	public void onMove(float distanceX, float distanceY) {
		if(mDrawable != null && drawMatrix != null){
			drawMatrix.postTranslate(-distanceX, -distanceY);
			checkMatrixBounds();
			invalidate();
		}
	}

	@Override
	public void onScale(float scaleFactor, float focusX, float focusY) {
		if(mDrawable != null && drawMatrix != null){
			scaling *= scaleFactor;
			AndroidLogger.d("缩放比例："+scaling);
			drawMatrix.postScale(scaleFactor, scaleFactor, focusX, focusY);
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
		flingScrollRunnable = new FlingScrollRunnable(getContext());
		flingScrollRunnable.fling(getAvailableWidth(), getAvailableHeight(), (int) velocityX, (int) velocityY);
		post(flingScrollRunnable);
	}
    
	@Override
	protected void onDetachedFromWindow() {
		super.onDetachedFromWindow();
		if(mDrawable != null){
			unscheduleDrawable(mDrawable);
		}
	}
	
	public class FlingScrollRunnable implements Runnable {
		private Scroller mScroller;
		private int mCurrentX, mCurrentY;
		
		public FlingScrollRunnable(Context context){
			mScroller = new Scroller(context);
		}
		
		public void cancelFling(){
			mScroller.forceFinished(true);
		}
		
		public void fling(int viewWidth, int viewHeight, int velocityX, int velocityY){
			final RectF rect = getDisplayRect();
            if (null == rect) {
                return;
            }

            final int startX = Math.round(-rect.left);
            final int minX, maxX, minY, maxY;

            if (viewWidth < rect.width()) {
                minX = 0;
                maxX = Math.round(rect.width() - viewWidth);
            } else {
                minX = maxX = startX;
            }

            final int startY = Math.round(-rect.top);
            if (viewHeight < rect.height()) {
                minY = 0;
                maxY = Math.round(rect.height() - viewHeight);
            } else {
                minY = maxY = startY;
            }

            mCurrentX = startX;
            mCurrentY = startY;

            if (startX != maxX || startY != maxY) {
            	mScroller.fling(startX, startY, velocityX, velocityY, minX, maxX, minY, maxY);
            }
		}
		
		@Override
		public void run() {
			if (mScroller.isFinished()) {	//如果已经完成了就结束
                return;
            }

            if (mScroller.computeScrollOffset()) {
                final int newX = mScroller.getCurrX();
                final int newY = mScroller.getCurrY();
                drawMatrix.postTranslate(-(mCurrentX - newX), -(mCurrentY - newY));
                checkMatrixBounds();
                invalidate();
                mCurrentX = newX;
                mCurrentY = newY;
                GuideMapView.this.postDelayed(this, 1000 / 60);
            }
		}
	}
	
	private void initScal
}