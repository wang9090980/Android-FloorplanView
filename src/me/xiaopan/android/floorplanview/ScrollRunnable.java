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

package me.xiaopan.android.floorplanview;

import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.Scroller;

/**
 * 移动指定的偏移量
 */
public class ScrollRunnable implements Runnable {
	private Scroller mScroller;
	private int mCurrentX, mCurrentY;
	private SimpleGestureDetector simpleGestureDetector;
	
	public ScrollRunnable(SimpleGestureDetector simpleGestureDetector, int xOffset, int yOffset){
		this.simpleGestureDetector = simpleGestureDetector;
		mScroller = new Scroller(simpleGestureDetector.getGuideView().getContext(), new AccelerateDecelerateInterpolator());
		mScroller.startScroll(0, 0, xOffset, yOffset, 300);
	}
	
	@Override
	public void run() {
		if(simpleGestureDetector.getGuideView().isAllow() && !mScroller.isFinished() && mScroller.computeScrollOffset()){
			final int newX = mScroller.getCurrX();
			final int newY = mScroller.getCurrY();
			simpleGestureDetector.postTranslate(-(mCurrentX - newX), -(mCurrentY - newY));
			mCurrentX = newX;
			mCurrentY = newY;
			simpleGestureDetector.getGuideView().postDelayed(this, SimpleGestureDetector.SIXTY_FPS_INTERVAL);
		}
	}
}