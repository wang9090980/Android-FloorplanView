package com.example.guidemap;

import android.content.Context;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;

/**
 * 缩放管理器
 */
public class ZommManager {
	private ScaleGestureDetector scaleGestureDetector;
	
	public ZommManager(Context context, SimpleGestureListener simpleGestureListener){
		scaleGestureDetector = new ScaleGestureDetector(context, this);
		generalGestureDetector = new GestureDetector(context, this);
		this.simpleGestureListener = simpleGestureListener;
	}
	
	public interface OnZoomListener{
		public void onDown(MotionEvent motionEvent);
		public void onMove(float distanceX, float distanceY);
		public void onScale(float scaleFactor, float focusX, float focusY);
		public void onDoubleTab(MotionEvent motionEvent);
		public void onLongPress(MotionEvent motionEvent);
		public void onSingleTapUp(MotionEvent motionEvent);
		public void onFling(float velocityX, float velocityY);
	}
}