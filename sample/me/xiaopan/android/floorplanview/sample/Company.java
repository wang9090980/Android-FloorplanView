package me.xiaopan.android.floorplanview.sample;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * 企业
 */
public class Company {
	@Expose
	@SerializedName("comid")
	private String id;// id

	@Expose
	@SerializedName("comname")
	private String name;// 公司名称
	
	@Expose
	@SerializedName("comlogo")
	private String logo;//展商Logo

	@Expose
	@SerializedName("pdtbrand")
	private String brand;//展品品牌
	
	@Expose
	@SerializedName("comcount")
	private String companycount;// 展商总数

	@Expose
	@SerializedName("summary")
	private String intro;// 简介

	@Expose
	@SerializedName("psnid")
	private String contacterId;// 联系人id
	
	@Expose
	@SerializedName("psnname")
	private String contacterName;// 联系人姓名
	
	@Expose
	@SerializedName("phone")
	private String contacterphone;// 联系人电话

	@Expose
	@SerializedName("mobile")
	private String contacterMobileNumber;// 联系人手机号
	
	@Expose
	@SerializedName("dept")
	private String contacterDept;// 联系人部门
	
	@Expose
	@SerializedName("rank")
	private String contacterRank;// 联系人职务

	@Expose
	@SerializedName("fax")
	private String fax;// 传真

	@Expose
	@SerializedName("area")
	private String area;// 地区

	@Expose
	@SerializedName("address")
	private String ContacterAddress;// 地址

	@Expose
	@SerializedName("email")
	private String contacteremail;// 邮箱

	@Expose
	@SerializedName("bthid")
	private String boothID;//展位id
	
	@Expose
	@SerializedName("x")
	private int positionX;// x轴
	
	@Expose
	@SerializedName("y")
	private int positionY;// y轴
	
	@Expose
	@SerializedName("w")
	private int width;// 宽
	
	@Expose
	@SerializedName("h")
	private int height;// 高
	
	@Expose
	@SerializedName("bthcode")
	private String boothNumber;// 展位号
	
	@Expose
	@SerializedName("industry")
	private String type;	//企业类型
	
	@Expose
	@SerializedName("srmid")
	private String hallId;	//展馆id
	
	@Expose
	@SerializedName("srmname")
	private String hallName;	//展馆名称
	
	@Expose
	@SerializedName("pdtintro")
	private String mainProduct;	//主营产品

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

	public String getLogo() {
		return logo;
	}

	public void setLogo(String logo) {
		this.logo = logo;
	}

	public String getIntro() {
		return intro;
	}

	public void setIntro(String intro) {
		this.intro = intro;
	}

	public String getFax() {
		return fax;
	}

	public void setFax(String fax) {
		this.fax = fax;
	}

	public String getArea() {
		return area;
	}

	public void setArea(String area) {
		this.area = area;
	}

	public String getBoothNumber() {
		return boothNumber;
	}

	public void setBoothNumber(String boothNumber) {
		this.boothNumber = boothNumber;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getHallId() {
		return hallId;
	}

	public void setHallId(String hallId) {
		this.hallId = hallId;
	}

	public String getBrand() {
		return brand;
	}

	public void setBrand(String brand) {
		this.brand = brand;
	}

	public String getBoothID() {
		return boothID;
	}

	public void setBoothID(String boothID) {
		this.boothID = boothID;
	}

	public int getPositionX() {
		return positionX;
	}

	public void setPositionX(int positionX) {
		this.positionX = positionX;
	}

	public int getPositionY() {
		return positionY;
	}

	public void setPositionY(int positionY) {
		this.positionY = positionY;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public String getCompanycount() {
		return companycount;
	}

	public void setCompanycount(String companycount) {
		this.companycount = companycount;
	}

	public String getContacterId() {
		return contacterId;
	}

	public void setContacterId(String contacterId) {
		this.contacterId = contacterId;
	}

	public String getContacterName() {
		return contacterName;
	}

	public void setContacterName(String contacterName) {
		this.contacterName = contacterName;
	}

	public String getContacterphone() {
		return contacterphone;
	}

	public void setContacterphone(String contacterphone) {
		this.contacterphone = contacterphone;
	}

	public String getContacterMobileNumber() {
		return contacterMobileNumber;
	}

	public void setContacterMobileNumber(String contacterMobileNumber) {
		this.contacterMobileNumber = contacterMobileNumber;
	}

	public String getContacterDept() {
		return contacterDept;
	}

	public void setContacterDept(String contacterDept) {
		this.contacterDept = contacterDept;
	}

	public String getContacterRank() {
		return contacterRank;
	}

	public void setContacterRank(String contacterRank) {
		this.contacterRank = contacterRank;
	}

	public String getContacterAddress() {
		return ContacterAddress;
	}

	public void setContacterAddress(String contacterAddress) {
		ContacterAddress = contacterAddress;
	}

	public String getContacteremail() {
		return contacteremail;
	}

	public void setContacteremail(String contacteremail) {
		this.contacteremail = contacteremail;
	}

	public String getHallName() {
		return hallName;
	}

	public void setHallName(String hallName) {
		this.hallName = hallName;
	}

	public String getMainProduct() {
		return mainProduct;
	}

	public void setMainProduct(String mainProduct) {
		this.mainProduct = mainProduct;
	}
}