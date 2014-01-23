package me.xiaopan.android.floorplanview.sample;

import java.util.LinkedList;
import java.util.List;

import me.xiaopan.android.easy.util.Colors;
import me.xiaopan.android.easy.util.TextUtils;
import me.xiaopan.android.floorplanview.R;
import me.xiaopan.android.floorplanview.RectArea;
import me.xiaopan.java.easy.util.StringUtils;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * 展位
 */
public class Booth extends RectArea{
	private RectF rect;
	private static Rect areaPadding = new Rect(2, 2, 2, 2);
	
	@Expose
	@SerializedName("bthId")
	private String id;	//ID

	@Expose
	@SerializedName("bthCode")
	private String number;	//编号
	
	@Expose
	@SerializedName("x")
	private int left;	//左外边距
	
	@Expose
	@SerializedName("y")
	private int top;	//顶外边距
	
	@Expose
	@SerializedName("w")
	private int width;	//宽度
	
	@Expose
	@SerializedName("h")
	private int height;	//高度
	
	@Expose
	@SerializedName("atrs")
	private List<Company> companys;	//所关联的企业的信息
	
	private Company currentCompany;
	
	/**
	 * 获取ID
	 * @return ID
	 */
	public String getId() {
		return id;
	}
	
	/**
	 * 设置ID
	 * @param id ID
	 */
	public void setId(String id) {
		this.id = id;
	}
	
	/**
	 * 获取编号
	 * @return 编号
	 */
	public String getNumber() {
		return number;
	}

	/**
	 * 设置编号
	 * @param number 编号
	 */
	public void setNumber(String number) {
		this.number = number;
	}

	/**
	 * 获取左外边距
	 * @return 左外边距
	 */ 
	public int getLeft() {
		return left;
	}

	/**
	 * 设置左外边距
	 * @param left 左外边距
	 */
	public void setLeft(int left) {
		this.left = left;
	}

	/**
	 * 获取左外边距
	 * @return 顶外边距
	 */
	public int getTop() {
		return top;
	}

	/**
	 * 设置左外边距
	 * @param top 顶外边距
	 */
	public void setTop(int top) {
		this.top = top;
	}

	/**
	 * 获取右外边距
	 * @return 右外边距
	 */
	public int getRight() {
		return left + width;
	}

	/**
	 * 获取底外边距
	 * @return 底外边距
	 */
	public int getBottom() {
		return top + height;
	}

	/**
	 * 获取宽度
	 * @return 宽度
	 */
	public int getWidth() {
		return width;
	}
	
	/**
	 * 设置宽度
	 * @param width 宽度
	 */
	public void setWidth(int width) {
		this.width = width;
	}
	
	/**
	 * 获取高度
	 * @return 高度
	 */
	public int getHeight() {
		return height;
	}
	
	/**
	 * 设置高度
	 * @param height 高度
	 */
	public void setHeight(int height) {
		this.height = height;
	}

	/**
	 * 获取企业
	 * @return 企业
	 */
	public List<Company> getCompanys() {
		return companys;
	}

	/**
	 * 设置企业
	 * @param company 企业
	 */
	public void setCompanys(List<Company> companys) {
		this.companys = companys;
	}

	public Company getCurrentCompany() {
		if(currentCompany == null && companys != null && companys.size() > 0){
			currentCompany = companys.get(0);
		}
		return currentCompany;
	}

	public void setCurrentCompany(Company currentCompany) {
		this.currentCompany = currentCompany;
	}

	@Override
	public String getTitle() {
		return getCurrentCompany() != null && getCurrentCompany().getName() != null?getCurrentCompany().getName():"此展位没有展商入驻";
	}

	@Override
	public String getSubTitle() {
		return number != null?number:"";
	}
	
	@Override 
	public int getBubbleXOffset(){
		return 20;
	}
	
	@Override 
	public int getBubbleDrawableOriginalWidth(){
		return 200;
	}

	@Override
	public int getVoidHeight() {
		return 34;
	}
	
	@Override
	public int getTilteTextSize(){
		return 35;
	}
	
	@Override
	public int getSubTilteTextSize(){
		return 25;
	}
	
	@Override
	public int getTitleTextColor() {
		return Colors.ORANGE_DARK;
	}

	@Override
	public int getSubTitleTextColor() {
		return Colors.WHITE;
	}

	@Override
	public Drawable getBaseBubbleDrawable(Context context) {
		return context.getResources().getDrawable(R.raw.bubble);
	}

	@Override
	public RectF getAreaRect() {
		if(rect == null){
			rect = new RectF(getLeft(), getTop(), getRight(), getBottom());
		}
		return rect;
	}
	
	@Override
	public void drawArea(Canvas canvas, Paint paint, float scale) {
		paint.setColor(getAreaColor());
		RectF rect = new RectF(getAreaRect());
		rect.left *= scale;
		rect.top *= scale;
		rect.right *= scale;
		rect.bottom *= scale;
		rect.left += 1;
		rect.top += 1;
		rect.right += -1;
		rect.bottom += -1;
		canvas.drawRect(rect, paint);
		String boothNumber = getNumber();
		if(boothNumber != null){
			paint.setColor(Colors.BLACK);
			paint.setTextSize(16 * scale);
			paint.setAntiAlias(true);
			paint.setFilterBitmap(true);
			
			List<Booth.DrawText> drawTexts = new LinkedList<Booth.DrawText>();
			float textWidth = TextUtils.getTextWidth(paint, boothNumber);
			float textHeight = TextUtils.getTextHeight(paint);
			//如果不需要多行显示
			if(textWidth <= rect.width() - areaPadding.left - areaPadding.right){
				drawTexts.add(new DrawText(boothNumber, rect.left + areaPadding.left + ((rect.width() - textWidth)/2), rect.top + areaPadding.top + TextUtils.getTextLeading(paint) + ((rect.height() - textHeight)/2)));
			}else{
				/* 先按长度将字符串分割 */
				List<String> strins = new LinkedList<String>();
				StringBuffer stringBuffer = new StringBuffer();
				char[] chars = boothNumber.toCharArray();
				for(int w = 0; w < chars.length; w++){
					stringBuffer.append(chars[w]);
					String s = stringBuffer.toString();
					float tempTextWidth = TextUtils.getTextWidth(paint, s);
					if(tempTextWidth == rect.width() - areaPadding.left - areaPadding.right){
						strins.add(s);
						stringBuffer.delete(0, stringBuffer.length());
					}else if(tempTextWidth > rect.width() - areaPadding.left - areaPadding.right){
						strins.add(s.substring(0, s.length() - 1));
						stringBuffer.delete(0, stringBuffer.length() - 1);
					}
				}
				String s = stringBuffer.toString();
				if(StringUtils.isNotEmpty(s)){
					strins.add(s);
				}
				
				if(strins.size() > 0){
					//计算垂直方向起始位置
					float top = rect.top + areaPadding.top + TextUtils.getTextLeading(paint);
					float needHeight = (strins.size() * textHeight);
					if(needHeight < rect.height() - areaPadding.top - areaPadding.bottom){
						top += (rect.height() - needHeight)/2;
					}
					
					for(String string : strins){
						drawTexts.add(new DrawText(string, rect.left + areaPadding.left, top));
						top += textHeight;
					}
				}
			}
			if(drawTexts != null && drawTexts.size() > 0){
				for(DrawText drawText : drawTexts){
					canvas.drawText(drawText.text, drawText.left, drawText.top, paint);
				}
			}
		}
	}
	
	public class DrawText{
		private float left;
		private float top;
		private String text;
		
		public DrawText(String text, float left, float top){
			this.text = text;
			this.left = left;
			this.top = top;
		}
	}
}