package me.xiaopan.android.planview.sample;

import java.util.List;

import me.xiaopan.android.planview.PathArea;
import me.xiaopan.android.planview.R;
import me.xiaopan.easy.android.util.Colors;
import android.content.Context;
import android.graphics.PointF;
import android.graphics.drawable.Drawable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * 展馆
 */
public class ExhibitionHall extends PathArea {
	@Expose
	@SerializedName("roomId")
	private String id;
	
	@Expose
	@SerializedName("roomName")
	private String name;
	
	@Expose
	@SerializedName("booths")
	private List<Booth> booths;
	
	private PointF[] coordinates;
	
	public ExhibitionHall(){
		
	}
	
	public ExhibitionHall(String id, String name, PointF... coordinates){
		this.id = id;
		this.name = name;
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
		return "name";
	}

	@Override
	public String getSubTitle() {
		return "";
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setCoordinates(PointF[] coordinates) {
		this.coordinates = coordinates;
	}

	@Override
	public int getPressedColor() {
		return super.getAreaColor();
	}

	@Override
	public int getAreaColor() {
		return Colors.TRANSPARENT;
	}

	public List<Booth> getBooths() {
		return booths;
	}

	public void setBooths(List<Booth> booths) {
		this.booths = booths;
	}
}