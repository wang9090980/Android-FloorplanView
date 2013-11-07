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
		if(guideMapView.getDrawMatrix() != null){
			float newScaleFactor = detector.getScaleFactor();
			currentScale *= newScaleFactor;
			AndroidLogger.d("缩放比例："+currentScale);
			guideMapView.getDrawMatrix().postScale(newScaleFactor, newScaleFactor, detector.getFocusX(), detector.getFocusY());
			guideMapView.invalidate();
			return true;
		}else{
			return false;
		}
	}
	
	public void checkScale(float newScaleFactor){
		currentScale *= newScaleFactor;
		AndroidLogger.d("缩放比例："+currentScale);
	}
	
	public void init(){
		if(guideMapView.getDrawable() != null && guideMapView.getWidth() > 0 && guideMapView.getHeight() > 0){
			float widthScale = (float) guideMapView.getWidth()/(float) guideMapView.getDrawable().getIntrinsicWidth();
			float heightScale = (float) guideMapView.getWidth()/(float) guideMapView.getDrawable().getIntrinsicWidth();
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