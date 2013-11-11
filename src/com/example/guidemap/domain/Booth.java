package com.example.guidemap.domain;

import com.example.guidemap.RectArea;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * 展位
 */
public class Booth extends RectArea{
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
}