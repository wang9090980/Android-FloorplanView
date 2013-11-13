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

import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Interpolator;

/**
 * 动画缩放
 */
public class AnimatedZoomRunnable  implements Runnable {
	private static final Interpolator sInterpolator = new AccelerateDecelerateInterpolator();
    private final float focusX, focusY;
    private final long mStartTime;
    private final float mZoomStart, mZoomEnd;
    private SimpleGestureDetector simpleGestureDetector;

    public AnimatedZoomRunnable(SimpleGestureDetector simpleGestureDetector, final float currentZoom, final float targetZoom, final float focalX, final float focalY) {
        this.simpleGestureDetector = simpleGestureDetector;
    	focusX = focalX;
        focusY = focalY;
        mStartTime = System.currentTimeMillis();
        mZoomStart = currentZoom;
        mZoomEnd = targetZoom;
    }

    @Override
    public void run() {
        float t = interpolate();
        simpleGestureDetector.getScaleContorller().postScale((mZoomStart + t * (mZoomEnd - mZoomStart)) / simpleGestureDetector.getScaleContorller().getCurrentScale(), focusX, focusY);
        if (t < 1f) {
            simpleGestureDetector.getGuideMapView().postDelayed(this, SimpleGestureDetector.SIXTY_FPS_INTERVAL);
        }
    }

    private float interpolate() {
        float t = 1f * (System.currentTimeMillis() - mStartTime) / SimpleGestureDetector.ZOOM_DURATION;
        t = Math.min(1f, t);
        t = sInterpolator.getInterpolation(t);
        return t;
    }
}