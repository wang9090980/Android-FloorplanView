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
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.Scroller;

/**
 * 飞速滚动
 */
public class FlingScrollRunnable implements Runnable {
	private Scroller mScroller;
	private int mCurrentX, mCurrentY;
	private SimpleGestureDetector simpleGestureDetector;
	
	public FlingScrollRunnable(SimpleGestureDetector simpleGestureDetector){
		this.simpleGestureDetector = simpleGestureDetector;
		mScroller = new Scroller(simpleGestureDetector.getGuideView().getContext(), new AccelerateDecelerateInterpolator());
	}
	
	public void cancelFling(){
		mScroller.forceFinished(true);
	}
	
	public void fling(int viewWidth, int viewHeight, int velocityX, int velocityY){
		final RectF rect = simpleGestureDetector.getGuideView().getDisplayRect();
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
            simpleGestureDetector.postTranslate(-(mCurrentX - newX), -(mCurrentY - newY));
            mCurrentX = newX;
            mCurrentY = newY;
            simpleGestureDetector.getGuideView().postDelayed(this, SimpleGestureDetector.SIXTY_FPS_INTERVAL);
        }
	}
}