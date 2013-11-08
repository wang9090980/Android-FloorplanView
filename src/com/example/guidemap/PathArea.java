package com.example.guidemap;

import me.xiaopan.easy.android.util.Colors;
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
	public boolean isClickMe(float x, float y){
		return isPolygonContainPoint(new PointF(x, y), getCoordinates());
	}
	
	private boolean isPolygonContainPoint(PointF point,PointF[] vertexPoints){
		int nCross = 0;
		for (int i = 0; i < vertexPoints.length; i++) {
			PointF p1 = vertexPoints[i];
			PointF p2 = vertexPoints[(i + 1) % vertexPoints.length];
			if (p1.y == p2.y)
				continue;
			if (point.y < Math.min(p1.y, p2.y))
				continue;
			if (point.y >= Math.max(p1.y, p2.y))
				continue;
			double x = (double) (point.y - p1.y) * (double) (p2.x - p1.x)
					/ (double) (p2.y - p1.y) + p1.x;
			if (x > point.x)
				nCross++;
		}
		return (nCross % 2 == 1);
	}
	
	@Override
	public void draw(Canvas canvas, Paint paint){
		paint.setColor(Colors.RED);
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