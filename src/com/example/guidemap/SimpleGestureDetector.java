/*
 * Copyright 2013 Peng fei Pan
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.guidemap;

import android.graphics.RectF;
import android.view.GestureDetector;
import android.view.GestureDetector.OnDoubleTapListener;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;

/**
 * 综合手势识别器
 */
public class SimpleGestureDetector implements OnGestureListener, OnDoubleTapListener{
	public static final int ZOOM_DURATION = 200;
	public static final int SIXTY_FPS_INTERVAL = 1000 / 60;
	private GuideMapView guideMapView;
	private ScaleContorller scaleContorller;
	private GestureDetector generalGestureDetector;
	private FlingScrollRunnable flingScrollRunnable;
	private SimpleGestureListener simpleGestureListener;
	
	public SimpleGestureDetector(GuideMapView guideMapView, SimpleGestureListener simpleGestureListener){
		this.guideMapView = guideMapView;
		this.simpleGestureListener = simpleGestureListener;
		generalGestureDetector = new GestureDetector(guideMapView.getContext(), this);
		scaleContorller = new ScaleContorller(this);
	}
	
	public boolean onTouchEvent(MotionEvent motionEvent){
		generalGestureDetector.onTouchEvent(motionEvent);
		scaleContorller.onTouchEvent(motionEvent);
		if(motionEvent.getAction() == MotionEvent.ACTION_CANCEL || motionEvent.getAction() == MotionEvent.ACTION_OUTSIDE || motionEvent.getAction() == MotionEvent.ACTION_UP){
			if(scaleContorller.getCurrentScale() < scaleContorller.getToggleScales()[0]){
				scaleContorller.setScale(scaleContorller.getToggleScales()[0], motionEvent.getX(), motionEvent.getY(), true);
			}
			if(simpleGestureListener != null){
				simpleGestureListener.onUp(motionEvent);
			}
		}
		return true;
	}
	
	@Override
	public boolean onDown(MotionEvent e) {
		if(flingScrollRunnable != null){
			flingScrollRunnable.cancelFling();
			flingScrollRunnable = null;
		}
		if(simpleGestureListener != null){
			simpleGestureListener.onDown(e);
		}
		return true;
	}

	@Override
	public boolean onSingleTapUp(MotionEvent e) {
		if(simpleGestureListener != null){
			simpleGestureListener.onSingleTapUp(e);
			return true;
		}else{
			return false;
		}
	}

	@Override
	public boolean onDoubleTap(MotionEvent e) {
		scaleContorller.doubleTap(e);
		if(simpleGestureListener != null){
			simpleGestureListener.onDoubleTab(e);
	}
		return true;
	}

	@Override
	public void onLongPress(MotionEvent e) {
		if(simpleGestureListener != null){
			simpleGestureListener.onLongPress(e);
		}
	}

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
		flingScrollRunnable = new FlingScrollRunnable(this);
		flingScrollRunnable.fling(guideMapView.getAvailableWidth(), guideMapView.getAvailableHeight(), (int) velocityX, (int) velocityY);
		guideMapView.post(flingScrollRunnable);
		return true;
	}

	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
		if(guideMapView.getDrawable() != null && guideMapView.getDrawMatrix() != null){
			postTranslate(-distanceX, -distanceY);
			return true;
		}else{
			return false;
		}
	}

	@Override
	public void onShowPress(MotionEvent e) {
		
	}

	@Override
	public boolean onDoubleTapEvent(MotionEvent e) {
		return true;
	}

	@Override
	public boolean onSingleTapConfirmed(MotionEvent e) {
		return true;
	}
	
	/**
	 * 检查矩阵边界，防止滑动时超出边界
	 */
    public void checkMatrixBounds(){
		RectF rect = guideMapView.getDisplayRect();
		final float height = rect.height(), width = rect.width();
		float deltaX = 0, deltaY = 0;

		final int viewHeight = guideMapView.getAvailableHeight();
		if (height <= viewHeight) {
			deltaY = (viewHeight - height) / 2 - rect.top;
		} else if (rect.top > 0) {
			deltaY = -rect.top;
		} else if (rect.bottom < viewHeight) {
			deltaY = viewHeight - rect.bottom;
		}

		final int viewWidth = guideMapView.getAvailableWidth();
		if (width <= viewWidth) {
			deltaX = (viewWidth - width) / 2 - rect.left;
		} else if (rect.left > 0) {
			deltaX = -rect.left;
		} else if (rect.right < viewWidth) {
			deltaX = viewWidth - rect.right;
		} else {
		}

		guideMapView.getDrawMatrix().postTranslate(deltaX, deltaY);
	}
    
    /**
     * 增量移动
     * @param dx
     * @param dy
     */
    public void postTranslate(float dx, float dy){
    	guideMapView.getDrawMatrix().postTranslate(dx, dy);
        checkMatrixBounds();
        guideMapView.invalidate();
    }
    
    /**
     * 设置行的移动位置
     * @param x
     * @param y
     */
    public void setTranslate(float x, float y){
    	guideMapView.getDrawMatrix().setTranslate(x, y);
        checkMatrixBounds();
        guideMapView.invalidate();
    }
	
	public interface SimpleGestureListener{
		public void onDown(MotionEvent motionEvent);
		public void onDoubleTab(MotionEvent motionEvent);
		public void onLongPress(MotionEvent motionEvent);
		public void onSingleTapUp(MotionEvent motionEvent);
		public void onUp(MotionEvent motionEvent);
	}

	public ScaleContorller getScaleContorller() {
		return scaleContorller;
	}

	public GuideMapView getGuideMapView() {
		return guideMapView;
	}
}