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

import android.content.Context;
import android.view.GestureDetector;
import android.view.GestureDetector.OnDoubleTapListener;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.ScaleGestureDetector.OnScaleGestureListener;

public class SimpleGestureDetector implements OnGestureListener, OnDoubleTapListener, OnScaleGestureListener{
	private GestureDetector generalGestureDetector;
	private ScaleGestureDetector scaleGestureDetector;
	private SimpleGestureListener simpleGestureListener;
	
	public SimpleGestureDetector(Context context, SimpleGestureListener simpleGestureListener){
		scaleGestureDetector = new ScaleGestureDetector(context, this);
		generalGestureDetector = new GestureDetector(context, this);
		this.simpleGestureListener = simpleGestureListener;
	}
	
	public boolean onTouchEvent(MotionEvent motionEvent){
		generalGestureDetector.onTouchEvent(motionEvent);
		return scaleGestureDetector.onTouchEvent(motionEvent);
	}
	
	@Override
	public boolean onScale(ScaleGestureDetector detector) {
		if(simpleGestureListener != null){
			simpleGestureListener.onScale(detector.getScaleFactor(), detector.getFocusX(), detector.getFocusY());
			return true;
		}else{
			return false;
		}
	}

	@Override
	public boolean onScaleBegin(ScaleGestureDetector detector) {
		return true;
	}

	@Override
	public void onScaleEnd(ScaleGestureDetector detector) {
		
	}

	@Override
	public boolean onDown(MotionEvent e) {
		return simpleGestureListener != null;
	}

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
		if(simpleGestureListener != null){
			simpleGestureListener.onFling(velocityX, velocityY);
			return true;
		}else{
			return false;
		}
	}

	@Override
	public void onLongPress(MotionEvent e) {
		if(simpleGestureListener != null){
			simpleGestureListener.onLongPress(e);
		}
	}

	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
		if(simpleGestureListener != null){
			simpleGestureListener.onMove(distanceX, distanceY);
			return true;
		}else{
			return false;
		}
	}

	@Override
	public void onShowPress(MotionEvent e) {
		
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
		if(simpleGestureListener != null){
			simpleGestureListener.onDoubleTab(e);
		}
		return true;
	}

	@Override
	public boolean onDoubleTapEvent(MotionEvent e) {
		return true;
	}

	@Override
	public boolean onSingleTapConfirmed(MotionEvent e) {
		return true;
	}
	
	public interface SimpleGestureListener{
		public void onMove(float distanceX, float distanceY);
		public void onScale(float scaleFactor, float focusX, float focusY);
		public void onDoubleTab(MotionEvent motionEvent);
		public void onLongPress(MotionEvent motionEvent);
		public void onSingleTapUp(MotionEvent motionEvent);
		public void onFling(float velocityX, float velocityY);
	}
}