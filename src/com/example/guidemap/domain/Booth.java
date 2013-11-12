package com.example.guidemap.domain;

import me.xiaopan.easy.android.util.Colors;
import me.xiaopan.easy.android.util.TextUtils;
import me.xiaopan.easy.java.util.StringUtils;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import com.example.guidemap.R;
import com.example.guidemap.RectArea;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * 展位
 */
public class Booth extends RectArea{
	private PointF bubbleDrawableShowPoint;
	private Drawable bubbleDrawable;
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
	@SerializedName("atr")
	private Company company;	//所关联的企业的信息

	@Override
	public String getTitle() {
		return company != null?company.getAtrName():null;
	}

	@Override
	public String getSubTitle() {
		return number;
	}
	
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
	@Override
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
	@Override
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
	@Override
	public int getRight() {
		return left + width;
	}

	/**
	 * 获取底外边距
	 * @return 底外边距
	 */
	@Override
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
	public Company getCompany() {
		return company;
	}

	/**
	 * 设置企业
	 * @param company 企业
	 */
	public void setCompany(Company company) {
		this.company = company;
	}

	@Override
	public PointF getBubbleDrawableShowPoint(Context context) {
		if(bubbleDrawableShowPoint == null){
			float x = (getLeft() + getRight())/2;
			float y = (getTop() + getBottom())/2;
			x = (getLeft() + x)/2;
			y = (getTop() + y)/2;
			x -= 40;
			if(bubbleDrawable == null){
				getBubbleDrawable(context);
			}
			y -= bubbleDrawable.getIntrinsicHeight();
			bubbleDrawableShowPoint = new PointF(x, y);
		}
		return bubbleDrawableShowPoint;
	}

	@Override
	public Drawable getBubbleDrawable(Context context) {
		if(bubbleDrawable == null){
			Paint titlePaint = new Paint();
			titlePaint.setTextSize(40);
			titlePaint.setColor(Colors.BLACK);
			String title = StringUtils.checkLength(getTitle(), 20);
			int titleNeedWidth = (int) TextUtils.getTextWidth(titlePaint, title);
			int titleNeedHeight = TextUtils.getTextHeightByBounds(title, titlePaint.getTextSize());
			
			Paint subTitlePaint = new Paint();
			subTitlePaint.setTextSize(30);
			subTitlePaint.setColor(Colors.BLACK);
			String subTitle = StringUtils.checkLength(getSubTitle(), 20);
			int subTitleNeedWidth = (int) TextUtils.getTextWidth(subTitlePaint, subTitle);
			int subTitleNeedHeight = TextUtils.getTextHeightByBounds(subTitle, subTitlePaint.getTextSize());
			
			int finalNeedWidth = titleNeedWidth>subTitleNeedWidth?titleNeedWidth:subTitleNeedWidth;
			int finalNeedHeight = titleNeedHeight + 20 + subTitleNeedHeight;
			
			Drawable backgDrawable = context.getResources().getDrawable(R.drawable.bubble);
			Rect paddingRect = new Rect();
			backgDrawable.getPadding(paddingRect);
			
			finalNeedWidth += paddingRect.left + paddingRect.right;
			finalNeedHeight += paddingRect.top + paddingRect.bottom;
			
			backgDrawable.setBounds(0, 0, backgDrawable.getMinimumWidth()>finalNeedWidth?backgDrawable.getMinimumWidth():finalNeedWidth, backgDrawable.getMinimumHeight()>finalNeedHeight?backgDrawable.getMinimumHeight():finalNeedHeight);
			
			Bitmap bitmap = Bitmap.createBitmap(backgDrawable.getBounds().width(), backgDrawable.getBounds().height(), Config.ARGB_8888);
			Canvas canvas = new Canvas(bitmap);
			backgDrawable.draw(canvas);
			
			canvas.drawText(title, paddingRect.left, paddingRect.top + TextUtils.getTextLeading(titlePaint), titlePaint);
			canvas.drawText(subTitle, paddingRect.left, paddingRect.top + TextUtils.getTextLeading(subTitlePaint) + 20 + titleNeedHeight, subTitlePaint);
			
			bubbleDrawable = new BitmapDrawable(context.getResources(), bitmap);
			bubbleDrawable.setBounds(0, 0, bubbleDrawable.getIntrinsicWidth(), bubbleDrawable.getMinimumHeight());
		}
		return bubbleDrawable;
	}

	@Override
	public boolean isClickMeBubble(float x, float y) {
		return x >= bubbleDrawableShowPoint.x && x <= (bubbleDrawableShowPoint.x +bubbleDrawable.getIntrinsicWidth()) && y >= bubbleDrawableShowPoint.y && y <= (bubbleDrawableShowPoint.y +bubbleDrawable.getIntrinsicHeight());
	}
}