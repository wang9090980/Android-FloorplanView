package me.xiaopan.android.planview;

import me.xiaopan.android.planview.PlanView.InitialZoomMode;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;


/**
 * 缩放控制器
 */
public class ScaleContorller implements ScaleGestureDetector.OnScaleGestureListener{
	private float currentScale = 1.0f;
	private float[] toggleScales;
	private ScaleGestureDetector scaleGestureDetector;
	private SimpleGestureDetector simpleGestureDetector;
	private boolean first = true;
	
	public ScaleContorller(SimpleGestureDetector simpleGestureDetector){
		this.simpleGestureDetector = simpleGestureDetector;
		scaleGestureDetector = new ScaleGestureDetector(simpleGestureDetector.getGuideView().getContext(), this);
		toggleScales = new float[]{1.0f, 2.0f, 3.0f};
	}
	
	public boolean onTouchEvent(MotionEvent motionEvent){
		return scaleGestureDetector.onTouchEvent(motionEvent);
	}

	@Override
	public boolean onScale(ScaleGestureDetector detector) {
		if(simpleGestureDetector.getGuideView().isAllow()){
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
		if(simpleGestureDetector.getGuideView().isAllow()){
			if(currentScale * newScaleFactor < toggleScales[0]/2){
				newScaleFactor = toggleScales[0]/2/currentScale;
				currentScale = toggleScales[0]/2;
			}else if(currentScale * newScaleFactor > toggleScales[toggleScales.length - 1]){
				newScaleFactor = toggleScales[toggleScales.length - 1]/currentScale;
				currentScale = toggleScales[toggleScales.length - 1];
			}else{
				currentScale *= newScaleFactor;
			}
			simpleGestureDetector.getGuideView().getDrawMatrix().postScale(newScaleFactor, newScaleFactor, focusX, focusY);
			simpleGestureDetector.checkMatrixBounds();
			simpleGestureDetector.getGuideView().invalidate();
		}
	}
	
	/**
	 * 缩放
	 * @param newScale
	 * @param focusX
	 * @param focusY
	 * @param animate
	 */
	public void setScale(float newScale, float focusX, float focusY, boolean animate) {
		if(simpleGestureDetector.getGuideView().isAllow()){
			if(newScale < toggleScales[0]){
				newScale = toggleScales[0];
			}else if(newScale > toggleScales[toggleScales.length - 1]){
				newScale = toggleScales[toggleScales.length - 1];
			}
			if (animate) {
				simpleGestureDetector.getGuideView().post(new AnimatedZoomRunnable(simpleGestureDetector, currentScale, newScale, focusX, focusY));
			} else {
				currentScale = newScale;
				simpleGestureDetector.getGuideView().getDrawMatrix().setScale(newScale, newScale, focusX, focusY);
				simpleGestureDetector.checkMatrixBounds();
				simpleGestureDetector.getGuideView().invalidate();
			}
		}
	}
	
	/**
	 * 初始化
	 */
	public void init(){
		if(simpleGestureDetector.getGuideView().isAllow()){
			if(simpleGestureDetector.getGuideView().getWidth() > 0 && simpleGestureDetector.getGuideView().getHeight() > 0){
				float widthScale = (float) simpleGestureDetector.getGuideView().getWidth()/(float) simpleGestureDetector.getGuideView().getDrawable().getBounds().width();
				float heightScale = (float) simpleGestureDetector.getGuideView().getHeight()/(float) simpleGestureDetector.getGuideView().getDrawable().getBounds().height();
				toggleScales[0] = widthScale < heightScale?(widthScale<1.0f?widthScale:1.0f):(heightScale<1.0f?heightScale:1.0f);
				if(toggleScales[0] < 1.0f){
					toggleScales[1] = 1.0f;
					toggleScales[2] = 2.0f;
				}else{
					toggleScales[1] = 2.0f;
					toggleScales[2] = 3.0f;
				}
				if(first){
					if(simpleGestureDetector.getGuideView().getInitialZoomMode() == InitialZoomMode.MIN){
						setScale(toggleScales[0], 0, 0, false);
					}else if(simpleGestureDetector.getGuideView().getInitialZoomMode() == InitialZoomMode.DEFAULT){
						setScale(1.0f, 0, 0, false);
					}else if(simpleGestureDetector.getGuideView().getInitialZoomMode() == InitialZoomMode.MAX){
						setScale(toggleScales[toggleScales.length - 1], 0, 0, false);
					}
					first = false;
				}
			}
		}
	}
	
	/**
	 * 双击
	 * @param ev
	 */
	public void doubleTap(MotionEvent ev){
		if(simpleGestureDetector.getGuideView().isAllow()){
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