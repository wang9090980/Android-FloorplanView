/*
 * Copyright (C) 2013 Peng fei Pan <sky@xiaopan.me>
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

import java.util.ArrayList;
import java.util.List;

import me.xiaopan.android.floorplanview.SimpleGestureDetector.SimpleGestureListener;
import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory.Options;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.TextView;

/**
 * 平面图
 */
public class FloorplanView extends View implements SimpleGestureListener{
	private RectF displayRect;
	private Matrix drawMatrix;
	private Listener listener;
	private BitmapDrawable drawable;
	private List<Area> areas;
	private List<Area> bubbleAreas;
	private InitialZoomMode initialZoomMode;
	private SimpleGestureDetector simpleGestureDetector;
	private Area currentDownArea;
	private TextView textView;
	private RectF offsetRect;
	private Area waitLocationArea;
	private boolean initFinsish;
	private int maxSideLength;

	public FloorplanView(Context context) {
		super(context);
		init();
	}

	public FloorplanView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public FloorplanView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}
	
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private void init(){
		maxSideLength = 2000;
		displayRect = new RectF();
		offsetRect = new RectF();
		initialZoomMode = InitialZoomMode.MIN;
	}
	
	@Override
	protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
		super.onLayout(changed, left, top, right, bottom);
		if(isAllow()){
			simpleGestureDetector.getScaleContorller().init();
		}
	}

	@Override
	protected void onDraw(Canvas canvas) {
		if(drawable != null){
			/* 设置矩阵以实现缩放、移动效果 */
			if(drawMatrix != null){
				canvas.concat(drawMatrix);
			}
			
			/* 绘制底图 */
			drawable.draw(canvas);	
			
			/* 绘制按下状态 */
			if(currentDownArea != null && !(currentDownArea.isShowBubble() && !currentDownArea.isClickedArea())){
				currentDownArea.drawPressed(getContext(), canvas);
			}
			
			/* 绘制气泡 */
			offsetRect.set(0, 0, 0, 0);
			if(bubbleAreas != null && bubbleAreas.size() > 0){
				for(Area area : bubbleAreas){
					if(area.isShowBubble()){
						area.drawBubble(getContext(), canvas);
						checkOffset(area);
					}
				}
			}
			
			/* 绘制按下状态 */
			if(currentDownArea != null && currentDownArea.isShowBubble() && !currentDownArea.isClickedArea()){
				currentDownArea.drawPressed(getContext(), canvas);
			}
		}
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if(isAllow()){
			boolean result = false;
			result = simpleGestureDetector.onTouchEvent(event);
			invalidate();
			return result;
		}else{
			return super.onTouchEvent(event);
		}
	}
	
	private void initStart(){
		if(drawable != null){
			if(drawable.getBitmap() != null && !drawable.getBitmap().isRecycled()){
				drawable.getBitmap().recycle();
			}
			drawable.setCallback(null);
			unscheduleDrawable(drawable);
			drawable = null;
		}
		
		if(drawMatrix != null){
			drawMatrix.reset();
			drawMatrix = null;
		}
		
		if(simpleGestureDetector != null){
			simpleGestureDetector = null;
		}
		
		if(areas != null){
			areas.clear();
			areas = null;
		}
		initFinsish = false;
	}
	
	private void initFinish(BitmapDrawable mapBitmapDrawable, List<Area> newAreas){
		if(mapBitmapDrawable != null){
			drawable = mapBitmapDrawable;
			drawMatrix = new Matrix();
			simpleGestureDetector = new SimpleGestureDetector(FloorplanView.this, FloorplanView.this);
			simpleGestureDetector.getScaleContorller().init();
			areas = newAreas;
			invalidate();
			initFinsish = true;
			if(waitLocationArea != null){
				location(waitLocationArea);
				waitLocationArea = null;
			}
		}
	}

	/**
	 * 设置地图
	 * @param baseMapBitmap 地图图片
	 * @param newAreas 地图上的区域
	 * @param suggestMapWidth 地图的宽，如果该值小于0将使用mapBitmap.getWidth()代替
	 * @param suggestMapHeight 地图的高，如果该值小于0将使用mapBitmap.getHeight()代替
	 */
	public void setMap(final Bitmap baseMapBitmap, final List<Area> newAreas, final int suggestMapWidth, final int suggestMapHeight) {
		initStart();
		if(baseMapBitmap != null && !baseMapBitmap.isRecycled()){
			new AsyncTask<Integer, Integer, BitmapDrawable>() {
				@Override
				protected void onPreExecute() {
					initFinsish = false;
					if(listener != null){
						listener.onInitStart();
					}
				}
				
				@Override
				protected BitmapDrawable doInBackground(Integer... params) {
					/* 初始化底图的最终宽高 */
					int mapWidth =  suggestMapWidth>0?suggestMapWidth:baseMapBitmap.getWidth();
					int mapHeight =  suggestMapHeight>0?suggestMapHeight:baseMapBitmap.getHeight();
					
					/* 限制底图的最大边长并创建底图副本 */
					Bitmap mapBitmap = null;
					if(baseMapBitmap.getWidth() > maxSideLength){
						mapBitmap = RequiredUtils.scaleByWidth(baseMapBitmap, maxSideLength);
						baseMapBitmap.recycle();
					}else if(baseMapBitmap.getHeight() > maxSideLength){
						mapBitmap = RequiredUtils.scaleByHeight(baseMapBitmap, maxSideLength);
						baseMapBitmap.recycle();
					}else{
						mapBitmap = baseMapBitmap.copy(Config.ARGB_8888, true);
						baseMapBitmap.recycle();
					}
					
					/* 在底图上绘制区域 */
					if(newAreas != null && newAreas.size() > 0){
						Canvas canvas = new Canvas(mapBitmap);
						Paint rectPaint = new Paint();
						int w = 0;
						float scale = (float) mapBitmap.getWidth() / suggestMapWidth;
						for(Area area : newAreas){
							area.drawArea(canvas, rectPaint, scale);
							onProgressUpdate(Integer.valueOf(RequiredUtils.percent(++w, newAreas.size(), 0, false, true)));
						}
					}
					
					/* 创建底图 */
					BitmapDrawable bitmapDrawable = new BitmapDrawable(getResources(), mapBitmap);
					bitmapDrawable.setBounds(0, 0, mapWidth, mapHeight);
					return bitmapDrawable;
				}
				
				@Override
				protected void onProgressUpdate(Integer... values) {
					if(listener != null){
						listener.onInitProgressUpdate(values[0]);
					}
				}

				@Override
				protected void onPostExecute(BitmapDrawable result) {
					if(result != null){
						initFinish(result, newAreas);
						if(listener != null){
							listener.onInitFinish();
						}
					}else{
						if(listener != null){
							listener.onInitFailure();
						}
					}
				}
			}.execute(0);
		}else{
			if(listener != null){
				listener.onInitFailure();
			}
		}
	}
	
	/**
	 * 设置地图，地图的宽高默认为mapBitmap的宽高
	 * @param mapBitmap
	 * @param newAreas
	 */
	public void setMap(Bitmap mapBitmap, List<Area> newAreas) {
		setMap(mapBitmap, newAreas, -1, -1);
	}
	
	/**
	 * 设置地图
	 * @param filePath 地图图片的路径
	 * @param newAreas 地图上的区域
	 */
	public void setMap(final String filePath, final List<Area> newAreas){
		initStart();
		if(RequiredUtils.isNotEmpty(filePath)){
			new AsyncTask<Integer, Integer, BitmapDrawable>() {
				@Override
				protected void onPreExecute() {
					initFinsish = false;
					if(listener != null){
						listener.onInitStart();
					}
				}
				
				@Override
				protected BitmapDrawable doInBackground(Integer... params) {
					/* 限制底图的最大边长并创建底图副本 */
					Bitmap mapBitmap = new BitmapDecoder((int) (Runtime.getRuntime().maxMemory()/4/4)).decodeFile(filePath);
					if(mapBitmap == null){
						return null;
					}
					if(mapBitmap.getWidth() > maxSideLength){
						Bitmap tempBitmap = RequiredUtils.scaleByWidth(mapBitmap, maxSideLength);
						mapBitmap.recycle();
						mapBitmap = tempBitmap;
					}else if(mapBitmap.getHeight() > maxSideLength){
						Bitmap tempBitmap = RequiredUtils.scaleByHeight(mapBitmap, maxSideLength);
						mapBitmap.recycle();
						mapBitmap = tempBitmap;
					}else{
						Bitmap tempBitmap = mapBitmap.copy(Config.ARGB_8888, true);
						mapBitmap.recycle();
						mapBitmap = tempBitmap;
					}
					
					/* 初始化底图的最终宽高 */
					Options options = BitmapDecoder.decodeSizeFromFile(filePath);
					int mapWidth = options.outWidth;
					int mapHeight = options.outHeight;
					
					/* 在底图上绘制区域 */
					if(newAreas != null && newAreas.size() > 0){
						Canvas canvas = new Canvas(mapBitmap);
						Paint rectPaint = new Paint();
						int w = 0;
						float scale = (float) mapBitmap.getWidth() / mapWidth;
						for(Area area : newAreas){
							area.drawArea(canvas, rectPaint, scale);
							onProgressUpdate(Integer.valueOf(RequiredUtils.percent(++w, newAreas.size(), 0, false, true)));
						}
					}
					
					/* 创建底图 */
					BitmapDrawable bitmapDrawable = new BitmapDrawable(getResources(), mapBitmap);
					bitmapDrawable.setBounds(0, 0, mapWidth, mapHeight);
					return bitmapDrawable;
				}
				
				@Override
				protected void onProgressUpdate(Integer... values) {
					if(listener != null){
						listener.onInitProgressUpdate(values[0]);
					}
				}

				@Override
				protected void onPostExecute(BitmapDrawable result) {
					if(result != null){
						initFinish(result, newAreas);
						if(listener != null){
							listener.onInitFinish();
						}
					}else{
						if(listener != null){
							listener.onInitFailure();
						}
					}
				}
			}.execute(0);
		}else{
			if(listener != null){
				listener.onInitFailure();
			}
		}
	}
	
	/**
	 * 设置地图，将从assets文件夹下获取文件
	 * @param fileName assets文件夹下的地图文件的名称
	 * @param newAreas 地图上的区域
	 */
	public void setMapFromAssets(final String fileName, final List<Area> newAreas){
		initStart();
		if(RequiredUtils.isNotEmpty(fileName)){
			new AsyncTask<Integer, Integer, BitmapDrawable>() {
				@Override
				protected void onPreExecute() {
					initFinsish = false;
					if(listener != null){
						listener.onInitStart();
					}
				}
				
				@Override
				protected BitmapDrawable doInBackground(Integer... params) {
					/* 限制底图的最大边长并创建底图副本 */
					Bitmap mapBitmap = new BitmapDecoder((int) (Runtime.getRuntime().maxMemory()/4/4)).decodeFromAssets(getContext(), fileName);
					if(mapBitmap == null){
						return null;
					}
					if(mapBitmap.getWidth() > maxSideLength){
						Bitmap tempBitmap = RequiredUtils.scaleByWidth(mapBitmap, maxSideLength);
						mapBitmap.recycle();
						mapBitmap = tempBitmap;
					}else if(mapBitmap.getHeight() > maxSideLength){
						Bitmap tempBitmap = RequiredUtils.scaleByHeight(mapBitmap, maxSideLength);
						mapBitmap.recycle();
						mapBitmap = tempBitmap;
					}else{
						Bitmap tempBitmap = mapBitmap.copy(Config.ARGB_8888, true);
						mapBitmap.recycle();
						mapBitmap = tempBitmap;
					}
					
					/* 初始化底图的最终宽高 */
					Options options = BitmapDecoder.decodeSizeFromAssets(getContext(), fileName);
					int mapWidth = options.outWidth;
					int mapHeight = options.outHeight;
					
					/* 在底图上绘制区域 */
					if(newAreas != null && newAreas.size() > 0){
						Canvas canvas = new Canvas(mapBitmap);
						Paint rectPaint = new Paint();
						int w = 0;
						float scale = (float) mapBitmap.getWidth() / mapWidth;
						for(Area area : newAreas){
							area.drawArea(canvas, rectPaint, scale);
							onProgressUpdate(Integer.valueOf(RequiredUtils.percent(++w, newAreas.size(), 0, false, true)));
						}
					}
					
					/* 创建底图 */
					BitmapDrawable bitmapDrawable = new BitmapDrawable(getResources(), mapBitmap);
					bitmapDrawable.setBounds(0, 0, mapWidth, mapHeight);
					return bitmapDrawable;
				}
				
				@Override
				protected void onProgressUpdate(Integer... values) {
					if(listener != null){
						listener.onInitProgressUpdate(values[0]);
					}
				}

				@Override
				protected void onPostExecute(BitmapDrawable result) {
					if(result != null){
						initFinish(result, newAreas);
						if(listener != null){
							listener.onInitFinish();
						}
					}else{
						if(listener != null){
							listener.onInitFailure();
						}
					}
				}
			}.execute(0);
		}else{
			if(listener != null){
				listener.onInitFailure();
			}
		}
	}
	
    /**
     * 获取可用宽度（去除左右内边距）
     * @return
     */
	public int getAvailableWidth() {
        return this.getWidth() - this.getPaddingLeft() - this.getPaddingRight();
    }

    /**
     * 获取可用高度（去除上下内边距）
     * @return
     */
    public int getAvailableHeight() {
        return this.getHeight() - this.getPaddingTop() - this.getPaddingBottom();
    }
	
    /**
     * 获取显示区域
     * @return
     */
    public final RectF getDisplayRect() {
    	if (isAllow()) {
			displayRect.set(drawable.getBounds());
			if(drawMatrix != null){
				drawMatrix.mapRect(displayRect);
			}
			if(textView != null){
				textView.setText(displayRect.toString());
			}
			
			/* 尝试扩大显示区域以便显示出超出的部分 */
			displayRect.left += offsetRect.left;
			displayRect.top += offsetRect.top;
			if(offsetRect.right > displayRect.width()){
				displayRect.right += offsetRect.right - displayRect.width();
			}
			if(offsetRect.bottom > displayRect.height()){
				displayRect.bottom += offsetRect.bottom - displayRect.height();
			}
			return displayRect;
		} else {
			return null;
		}
    }
    
	@Override
	public void onDown(MotionEvent motionEvent) {
		if(isAllow()){
			currentDownArea = findClickArea(motionEvent);
		}
	}

	@Override
	public void onSingleTapUp(MotionEvent e) {
		if(isAllow() && currentDownArea != null){
			if(currentDownArea.isShowBubble()){
				if(!currentDownArea.isClickedArea()){
					if(listener != null){
						listener.onClickBubble(currentDownArea);
					}
				}
			}else{
				if(listener != null){
					listener.onClickArea(currentDownArea);
				}
			}
		}
	}
	
	/**
	 * 查找点击的区域
	 * @param x
	 * @param y
	 * @return
	 */
	private Area findClickArea(MotionEvent e){
		if(isAllow() && (bubbleAreas != null || areas != null)){
			RectF rectF = getDisplayRect();
			if(rectF != null){
				float x = (e.getX()-rectF.left)/simpleGestureDetector.getScaleContorller().getCurrentScale();
				float y = (e.getY()-rectF.top)/simpleGestureDetector.getScaleContorller().getCurrentScale();
				x += offsetRect.left / simpleGestureDetector.getScaleContorller().getCurrentScale();
				y += offsetRect.top / simpleGestureDetector.getScaleContorller().getCurrentScale();
				Area clickArea = null;
				if(bubbleAreas != null && bubbleAreas.size() > 0){
					for(Area area : bubbleAreas){
						if(area.isClickBubble(getContext(), x, y)){
							clickArea = area;
							clickArea.setClickedArea(false);
							break;
						}
					}
				}
				
				if(clickArea == null && areas != null && areas.size() > 0){
					for(Area area : areas){
						if(area.isClickArea(x, y)){
							clickArea = area;
							clickArea.setClickedArea(true);
							break;
						}
					}
				}
				return clickArea;
			}else{
				return null;
			}
		}else{
			return null;
		}
	}
	
	@Override
	public void onDoubleTab(MotionEvent motionEvent) {
		
	}

	@Override
	public void onLongPress(MotionEvent motionEvent) {
		
	}

	@Override
	public void onUp(MotionEvent motionEvent) {
		if(isAllow() && currentDownArea != null){
			currentDownArea = null;
		}
	}

	@Override
	protected void onDetachedFromWindow() {
		super.onDetachedFromWindow();
		if(drawable != null){
			drawable.getBitmap().recycle();
			drawable.setCallback(null);
			unscheduleDrawable(drawable);
		}
	}
	
	/**
	 * 显示一个气泡
	 * @param newArea
	 */
	public void showBubble(Area newArea){
		if(isAllow() && newArea != null){
			if(bubbleAreas == null){
				bubbleAreas = new ArrayList<Area>();
			}
			newArea.setShowBubble(true, this);
			bubbleAreas.add(newArea);
			offsetRect.set(0, 0, 0, 0);
			checkOffset(newArea);
			simpleGestureDetector.checkMatrixBounds();
			invalidate();
		}
	}
	
	/**
	 * 显示一个气泡，会把之前所有的气泡全部清除
	 * @param newArea
	 */
	public void showSingleBubble(Area newArea){
		if(isAllow() && newArea != null){
			/* 清除已有的气泡 */
			if(bubbleAreas == null){
				bubbleAreas = new ArrayList<Area>(1);
			}else{
				clearAllBubble();
			}
			
			/* 添加气泡 */
			newArea.setShowBubble(true, this);
			bubbleAreas.add(newArea);
			invalidate();
			
			/* 尝试移动屏幕到气泡的位置，知道把气泡完全显示出来 */
			Point point = computeScrollOffset(bubbleAreas.get(0).getBubbleRect(getContext()));
			if(point != null && getHandler() != null){
				getHandler().post(new ScrollRunnable(simpleGestureDetector, point.x, point.y));
			}
		}
	}
	
	/**
	 * 计算滚动位置
	 * @param rectf
	 * @return
	 */
	private Point computeScrollOffset(RectF tempRectf){
		RectF rectf = new RectF(tempRectf);
		rectf.left *= simpleGestureDetector.getScaleContorller().getCurrentScale();
		rectf.top *= simpleGestureDetector.getScaleContorller().getCurrentScale();
		rectf.right *= simpleGestureDetector.getScaleContorller().getCurrentScale();
		rectf.bottom *= simpleGestureDetector.getScaleContorller().getCurrentScale();
		
		RectF offsetRect = new RectF(Math.abs(getDisplayRect().left) - rectf.left, Math.abs(getDisplayRect().top) - rectf.top, rectf.right - (Math.abs(getDisplayRect().left) + getWidth()), rectf.bottom - (Math.abs(getDisplayRect().top) + getHeight()));
		
		float xOffset = 0;
		if(offsetRect.left > 0){
			xOffset = offsetRect.left;
		}else if(offsetRect.right > 0){
			if(rectf.width() > getWidth()){
				xOffset = offsetRect.left;
			}else{
				xOffset = -offsetRect.right;
			}
		}
		
		float yOffset = 0;
		if(offsetRect.top > 0){
			yOffset = offsetRect.top;
		}else if(offsetRect.bottom > 0){
			yOffset = -offsetRect.bottom;
		}
		
		if(xOffset != 0 && rectf.width() < getWidth()){
			xOffset += (getWidth() - rectf.width()) / 2 * (xOffset > 0?1:-1);
		}
		
		if(yOffset != 0 && rectf.height() < getHeight()){
			yOffset += (getHeight() - rectf.height()) / 2 * (yOffset > 0?1:-1);
		}
		
		if(xOffset != 0 || yOffset != 0){
			return new Point((int) xOffset, (int) yOffset);
		}else{
			return null;
		}
	}
	
	/**
	 * 清除所有气泡
	 */
	public void clearAllBubble(){
		if(isAllow() && bubbleAreas != null && bubbleAreas.size() > 0){
			for(Area bubbleArea : bubbleAreas){
				bubbleArea.setShowBubble(false, this);
			}
			bubbleAreas.clear();
		}
	}
	
	/**
	 * 移动到指定位置
	 * @param x 指定位置的X坐标，例如100
	 * @param y 指定位置的Y坐标，例如200
	 */
	public void setTranslate(float x, float y){
		if(isAllow()){
			simpleGestureDetector.setTranslate(-x * simpleGestureDetector.getScaleContorller().getCurrentScale(), -y * simpleGestureDetector.getScaleContorller().getCurrentScale());
		}
	}
	
	/**
	 * 缩放
	 * @param newScale 新的缩放比例
	 * @param focusX 缩放中心点
	 * @param focusY 缩放中心点
	 * @param animate
	 */
	public void setScale(float newScale, float focusX, float focusY, boolean animate){
		if(isAllow()){
			simpleGestureDetector.getScaleContorller().setScale(newScale, focusX, focusY, animate);
		}
	}
	
	/**
	 * 缩放
	 * @param newScale 新的缩放比例
	 * @param focusX 缩放中心点
	 * @param focusY 缩放中心点
	 * @param animate
	 */
	public void setScale(float newScale, boolean animate){
		if(isAllow()){
			simpleGestureDetector.getScaleContorller().setScale(newScale, getRight() / 2, getBottom() / 2, animate);
		}
	}
	
	/**
	 * 定位
	 * @param x
	 * @param y
	 */
	public void location(final Area area){
		if(area != null){
			if(initFinsish){
				if(isAllow()){
					if(getWidth() > 0){
						setScale(1.0f, false);
						showSingleBubble(area);
					}else{
						getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
							@Override
							public void onGlobalLayout() {
								location(area);
								RequiredUtils.removeOnGlobalLayoutListener(getViewTreeObserver(), this);
							}
						});
					}
				}
			}else{
				waitLocationArea = area;
			}
		}
	}
	
	private void checkOffset(Area area){
		/* 记录四边的值，以便扩大显示区域，显示超出部分 */
		float left = area.getBubbleRect(getContext()).left * simpleGestureDetector.getScaleContorller().getCurrentScale();
		if(left < 0 && left < offsetRect.left){
			offsetRect.left = left;
		}

		float top = area.getBubbleRect(getContext()).top * simpleGestureDetector.getScaleContorller().getCurrentScale();
		if(top < 0 && top < offsetRect.top){
			offsetRect.top = top;
		}
		
		float right = area.getBubbleRect(getContext()).right * simpleGestureDetector.getScaleContorller().getCurrentScale();
		if(right > offsetRect.right){
			offsetRect.right = right;
		}
		
		float bottom = area.getBubbleRect(getContext()).bottom * simpleGestureDetector.getScaleContorller().getCurrentScale();
		if(bottom > offsetRect.bottom){
			offsetRect.bottom = bottom;
		}
	}
	
	public Matrix getDrawMatrix() {
		return drawMatrix;
	}

	public Drawable getDrawable() {
		return drawable;
	}
	
	public void setListener(Listener listener) {
		this.listener = listener;
	}
	
	public InitialZoomMode getInitialZoomMode() {
		return initialZoomMode;
	}

	public void setInitialZoomMode(InitialZoomMode initialZoomMode) {
		this.initialZoomMode = initialZoomMode;
	}

	public enum InitialZoomMode{
		MIN, DEFAULT, MAX;
	}

	public interface Listener{
		/**
		 * 开始初始化
		 */
		public void onInitStart();
		
		/**
		 * 更新初始化进度
		 * @param percent
		 */
		public void onInitProgressUpdate(int percent);
		
		/**
		 * 初始化完成
		 */
		public void onInitFinish();
		
		/**
		 * 由于解码图片失败或参数不合法导致初始化失败
		 */
		public void onInitFailure();
		
		/**
		 * 点击了某一个区域
		 * @param area
		 */
		public void onClickArea(Area area);
		
		/**
		 * 点击了某一个气泡
		 * @param area
		 */
		public void onClickBubble(Area area);
	}

	public TextView getTextView() {
		return textView;
	}

	public void setTextView(TextView textView) {
		this.textView = textView;
	}
	
	public int getMaxSideLength() {
		return maxSideLength;
	}

	public void setMaxSideLength(int maxSideLength) {
		this.maxSideLength = maxSideLength;
	}

	public boolean isAllow(){
		return drawable != null && drawMatrix != null && simpleGestureDetector != null;
	}
}