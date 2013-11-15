package com.example.guidemap.domain;

import android.content.Context;
import android.graphics.PointF;
import android.graphics.drawable.Drawable;
import me.xiaopan.guide.PathArea;
import me.xiaopan.guide.R;

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
		if(Booth.baseBubbleDrawable == null){
			Booth.baseBubbleDrawable = context.getResources().getDrawable(R.raw.bubble);
		}
		return Booth.baseBubbleDrawable;
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