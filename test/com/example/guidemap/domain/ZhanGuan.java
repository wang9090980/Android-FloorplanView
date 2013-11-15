package com.example.guidemap.domain;

import me.xiaopan.guide.PathArea;
import me.xiaopan.guide.R;
import android.content.Context;
import android.graphics.PointF;
import android.graphics.drawable.Drawable;

public class ZhanGuan extends PathArea {

	private PointF[] coordinates;
	
	public ZhanGuan(PointF... coordinates){
		this.coordinates = coordinates;
	}
	
	@Override
	public PointF[] getCoordinates() {
		return coordinates;
	}

	@Override
	public int getBubbleXOffset() {
		return 55;
	}

	@Override
	public Drawable getBaseBubbleDrawable(Context context) {
		return context.getResources().getDrawable(R.raw.bubble);
	}

	@Override
	public int getBubbleDrawableOriginalWidth() {
		return 200;
	}

	@Override
	public int getVoidHeight() {
		return 36;
	}

	@Override
	public String getTitle() {
		return "这是一个矩形展馆";
	}

	@Override
	public String getSubTitle() {
		return "展馆名称N2";
	}
}