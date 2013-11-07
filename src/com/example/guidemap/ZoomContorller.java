package com.example.guidemap;

import me.xiaopan.easy.android.util.AndroidLogger;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;

/**
 * 缩放控制器
 */
public class ZoomContorller implements ScaleGestureDetector.OnScaleGestureListener{
	private float currentScale = 1.0f;
	private float[] toggleScales;
	private ScaleGestureDetector scaleGestureDetector;
//	private int index;
	private SimpleGestureDetector simpleGestureDetector;
	
	public ZoomContorller(SimpleGestureDetector simpleGestureDetector){
		this.simpleGestureDetector = simpleGestureDetector;
		scaleGestureDetector = new ScaleGestureDetector(simpleGestureDetector.getGuideMapView().getContext(), this);
		toggleScales = new float[]{1.0f, 2.0f, 3.0f};
	}
	
	public boolean onTouchEvent(MotionEvent motionEvent){
		return scaleGestureDetector.onTouchEvent(motionEvent);
	}

	@Override
	public boolean onScale(ScaleGestureDetector detector) {
		if(simpleGestureDetector.getGuideMapView().getDrawable() != null && simpleGestureDetector.getGuideMapView().getDrawMatrix() != null){
			postScale(detector.getScaleFactor(), detector.getFocusX(), detector.getFocusY());
			return true;
		}else{
			return false;
		}
	}
	
	/**
	 * 更新缩放比例
	 * @param newScaleFactor
	 * @return
	 */
	public void postScale(float newScaleFactor, float focusX, float focusY){
		if(currentScale * newScaleFactor < toggleScales[0]/2){
			newScaleFactor = toggleScales[0]/2/currentScale;
			currentScale = toggleScales[0]/2;
		}else if(currentScale * newScaleFactor > toggleScales[toggleScales.length - 1]){
			newScaleFactor = toggleScales[toggleScales.length - 1]/currentScale;
			currentScale = toggleScales[toggleScales.length - 1];
		}else{
			currentScale *= newScaleFactor;
		}
		simpleGestureDetector.getGuideMapView().getDrawMatrix().postScale(newScaleFactor, newScaleFactor, focusX, focusY);
		simpleGestureDetector.checkMatrixBounds();
		simpleGestureDetector.getGuideMapView().invalidate();
		AndroidLogger.d("缩放比例："+currentScale);
	}
	
	/**
	 * 设置缩放
	 * @param newScale
	 * @param focusX
	 * @param focusY
	 * @param animate
	 */
	public void setScale(float newScale, float focusX, float focusY, boolean animate) {
		if (animate) {
			simpleGestureDetector.getGuideMapView().post(new AnimatedZoomRunnable(simpleGestureDetector, currentScale, newScale, focusX, focusY));
		} else {
			currentScale = newScale;
			simpleGestureDetector.getGuideMapView().getDrawMatrix().setScale(newScale, newScale, focusX, focusY);
			simpleGestureDetector.checkMatrixBounds();
			simpleGestureDetector.getGuideMapView().invalidate();
		}
	}
	
	public void init(){
		if(simpleGestureDetector.getGuideMapView().getDrawable() != null && simpleGestureDetector.getGuideMapView().getWidth() > 0 && simpleGestureDetector.getGuideMapView().getHeight() > 0){
			AndroidLogger.d("GuideMapView宽高："+simpleGestureDetector.getGuideMapView().getWidth()+","+simpleGestureDetector.getGuideMapView().getHeight()+"；Drawable宽高："+simpleGestureDetector.getGuideMapView().getDrawable().getIntrinsicWidth()+","+simpleGestureDetector.getGuideMapView().getDrawable().getIntrinsicHeight());
			float widthScale = (float) simpleGestureDetector.getGuideMapView().getWidth()/(float) simpleGestureDetector.getGuideMapView().getDrawable().getIntrinsicWidth();
			float heightScale = (float) simpleGestureDetector.getGuideMapView().getHeight()/(float) simpleGestureDetector.getGuideMapView().getDrawable().getIntrinsicHeight();
			toggleScales[0] = widthScale < heightScale?(widthScale<1.0f?widthScale:1.0f):(heightScale<1.0f?heightScale:1.0f);
			if(toggleScales[0] < 1.0f){
				toggleScales[1] = 1.0f;
				toggleScales[2] = 2.0f;
			}else{
				toggleScales[1] = 2.0f;
				toggleScales[2] = 3.0f;
			}
		}else{
			toggleScales[0] = 1.0f;
			toggleScales[1] = 2.0f;
			toggleScales[2] = 3.0f;
		}
	}
	
	/**
	 * 双击
	 * @param ev
	 */
	public void doubleTap(MotionEvent ev){
        float scale = currentScale;
        float x = ev.getX();
        float y = ev.getY();
        if (scale < toggleScales[1]) {
            setScale(toggleScales[1], x, y, true);
        } else if (scale >= toggleScales[1] && scale < toggleScales[2]) {
            setScale(toggleScales[2], x, y, true);
        } else {
            setScale(toggleScales[0], x, y, true);
        }
	}

	@Override
	public boolean onScaleBegin(ScaleGestureDetector detector) {
		return true;
	}

	@Override
	public void onScaleEnd(ScaleGestureDetector detector) {
		
	}

	public float getCurrentScale() {
		return currentScale;
	}

	public float[] getToggleScales() {
		return toggleScales;
	}
}