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
	private GuideMapView guideMapView;
	private ScaleGestureDetector scaleGestureDetector;
	
	public ZoomContorller(GuideMapView guideMapView){
		this.guideMapView = guideMapView;
		scaleGestureDetector = new ScaleGestureDetector(guideMapView.getContext(), this);
		toggleScales = new float[]{1.0f, 2.0f, 3.0f};
	}
	
	public boolean onTouchEvent(MotionEvent motionEvent){
		return scaleGestureDetector.onTouchEvent(motionEvent);
	}

	@Override
	public boolean onScale(ScaleGestureDetector detector) {
		if(guideMapView.getDrawable() != null && guideMapView.getDrawMatrix() != null){
			float newScaleFactor = updateScale(detector.getScaleFactor());
			guideMapView.getDrawMatrix().postScale(newScaleFactor, newScaleFactor, detector.getFocusX(), detector.getFocusY());
			guideMapView.invalidate();
			return true;
		}else{
			return false;
		}
	}
	
	private float updateScale(float newScaleFactor){
		if(currentScale * newScaleFactor < toggleScales[0]){
			newScaleFactor = toggleScales[0]/currentScale;
			currentScale = toggleScales[0];
		}else if(currentScale * newScaleFactor > toggleScales[toggleScales.length - 1]){
			newScaleFactor = toggleScales[toggleScales.length - 1]/currentScale;
			currentScale = toggleScales[toggleScales.length - 1];
		}else{
			currentScale *= newScaleFactor;
		}
		AndroidLogger.d("缩放比例："+currentScale);
		return newScaleFactor;
	}
	
	public void init(){
		if(guideMapView.getDrawable() != null && guideMapView.getWidth() > 0 && guideMapView.getHeight() > 0){
			AndroidLogger.d("GuideMapView宽高："+guideMapView.getWidth()+","+guideMapView.getHeight()+"；Drawable宽高："+guideMapView.getDrawable().getIntrinsicWidth()+","+guideMapView.getDrawable().getIntrinsicHeight());
			float widthScale = (float) guideMapView.getWidth()/(float) guideMapView.getDrawable().getIntrinsicWidth();
			float heightScale = (float) guideMapView.getHeight()/(float) guideMapView.getDrawable().getIntrinsicHeight();
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

	@Override
	public boolean onScaleBegin(ScaleGestureDetector detector) {
		return true;
	}

	@Override
	public void onScaleEnd(ScaleGestureDetector detector) {
		
	}
}