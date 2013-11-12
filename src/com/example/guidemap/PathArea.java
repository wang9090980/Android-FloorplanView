package com.example.guidemap;

import me.xiaopan.easy.android.util.GeometryUtils;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;

public abstract class PathArea implements Area{
	/**
	 * 获取所有坐标点
	 * @return
	 */
	public abstract PointF[] getCoordinates();
	
	@Override
	public boolean isClickArea(float x, float y){
		return GeometryUtils.isPolygonContainPoint(new PointF(x, y), getCoordinates());
	}
	
	@Override
	public void drawArea(Canvas canvas, Paint paint){
		PointF[] coordinates = getCoordinates();
		if(coordinates != null && coordinates.length >= 3){
			Path path = new Path();
			path.moveTo(coordinates[0].x, coordinates[0].y);
			for(int w = 1; w < coordinates.length; w++){
				path.lineTo(coordinates[w].x, coordinates[w].y);
			}
			path.close();
			canvas.drawPath(path, paint);
		}
	}
}