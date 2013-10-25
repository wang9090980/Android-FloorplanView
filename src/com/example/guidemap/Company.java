package com.example.guidemap;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * 企业
 */
public class Company {
	@Expose
	@SerializedName("atrId")
	private String atrId;// 展商id
	
	@Expose
	@SerializedName("bthCode")
	private String bthCode;// 展位号
	
	@Expose
	@SerializedName("atrName")
	private String atrName;// 展商名称
	
	@Expose
	@SerializedName("atrIntro")
	private String atrIntro;// 展商介绍
	
	@Expose
	@SerializedName("comMark")
	private String comMark;// 展商品牌
	
	@Expose
	@SerializedName("comPros")
	private String[] comPros;// 参展产品
	
	@Expose
	@SerializedName("psjName")
	private String psjName;// 联系人
	
	@Expose
	@SerializedName("psjTel")
	private String psjTel;// 电话
	
	@Expose
	@SerializedName("psjMobile")
	private String psjMobile;// 手机
	
	@Expose
	@SerializedName("psjFax")
	private String psjFax;// 传真
	
	@Expose
	@SerializedName("psjCity")
	private String psjCity;// 地区
	
	@Expose
	@SerializedName("psjZipCode")
	private String comPost;// 邮编
	
	@Expose
	@SerializedName("psjAddress")
	private String psjAddress;// 地址
	
	@Expose
	@SerializedName("psjEmail")
	private String psjEmail;// 邮箱

	@Expose
	@SerializedName("cltFlag")
	private String cltFlag;//0未关注，1关注过
	
	@Expose
	@SerializedName("bthId")
	private String bthId;//展位Id
	
	@Expose
	@SerializedName("srmId")
	private String srmId;//展馆Id
	
	@Expose
	@SerializedName("logoUrl")
	private String logoUrl;
	
	//TODO
	
	@Expose
	@SerializedName("atrEIntro")
	private String atrIntroOfEn;//企业英文简介
	
	@Expose
	@SerializedName("atrEName")
	private String atrNameOfEn;//企业英文名称
	
	@Expose
	@SerializedName("dctName")
	private String atrSort;//企业类别
	
	@Expose
	@SerializedName("psjWebUrl")
	private String atrWeb;//网址

	@Expose
	@SerializedName("psjRank")
	private String psjPost;//联系人职位

	public String getAtrId() {
		return atrId;
	}

	public void setAtrId(String atrId) {
		this.atrId = atrId;
	}

	public String getBthCode() {
		return bthCode;
	}

	public void setBthCode(String bthCode) {
		this.bthCode = bthCode;
	}

	public String getAtrName() {
		return atrName;
	}

	public void setAtrName(String atrName) {
		this.atrName = atrName;
	}

	public String getAtrIntro() {
		return atrIntro;
	}

	public void setAtrIntro(String atrIntro) {
		this.atrIntro = atrIntro;
	}

	public String getComMark() {
		return comMark;
	}

	public void setComMark(String comMark) {
		this.comMark = comMark;
	}

	public String[] getComPros() {
		return comPros;
	}

	public void setComPros(String[] comPros) {
		this.comPros = comPros;
	}

	public String getPsjName() {
		return psjName;
	}

	public void setPsjName(String psjName) {
		this.psjName = psjName;
	}

	public String getPsjTel() {
		return psjTel;
	}

	public void setPsjTel(String psjTel) {
		this.psjTel = psjTel;
	}

	public String getPsjMobile() {
		return psjMobile;
	}

	public void setPsjMobile(String psjMobile) {
		this.psjMobile = psjMobile;
	}

	public String getPsjFax() {
		return psjFax;
	}

	public void setPsjFax(String psjFax) {
		this.psjFax = psjFax;
	}

	public String getPsjCity() {
		return psjCity;
	}

	public void setPsjCity(String psjCity) {
		this.psjCity = psjCity;
	}

	public String getComPost() {
		return comPost;
	}

	public void setComPost(String comPost) {
		this.comPost = comPost;
	}

	public String getPsjAddress() {
		return psjAddress;
	}

	public void setPsjAddress(String psjAddress) {
		this.psjAddress = psjAddress;
	}

	public String getPsjEmail() {
		return psjEmail;
	}

	public void setPsjEmail(String psjEmail) {
		this.psjEmail = psjEmail;
	}

	public String getCltFlag() {
		return cltFlag;
	}

	public void setCltFlag(String cltFlag) {
		this.cltFlag = cltFlag;
	}

	public String getBthId() {
		return bthId;
	}

	public void setBthId(String bthId) {
		this.bthId = bthId;
	}

	public String getSrmId() {
		return srmId;
	}

	public void setSrmId(String srmId) {
		this.srmId = srmId;
	}

	public String getLogoUrl() {
		return logoUrl;
	}

	public void setLogoUrl(String logoUrl) {
		this.logoUrl = logoUrl;
	}

	public String getAtrIntroOfEn() {
		return atrIntroOfEn;
	}

	public void setAtrIntroOfEn(String atrIntroOfEn) {
		this.atrIntroOfEn = atrIntroOfEn;
	}

	public String getAtrNameOfEn() {
		return atrNameOfEn;
	}

	public void setAtrNameOfEn(String atrNameOfEn) {
		this.atrNameOfEn = atrNameOfEn;
	}

	public String getAtrSort() {
		return atrSort;
	}

	public void setAtrSort(String atrSort) {
		this.atrSort = atrSort;
	}

	public String getAtrWeb() {
		return atrWeb;
	}

	public void setAtrWeb(String atrWeb) {
		this.atrWeb = atrWeb;
	}

	public String getPsjPost() {
		return psjPost;
	}

	public void setPsjPost(String psjPost) {
		this.psjPost = psjPost;
	}
	
	
}