package com.utstar.appstoreapplication.activity.entity.net_entity;

import com.google.gson.annotations.SerializedName;
import com.ut.wb.ui.constance.TagConstance;
import com.utstar.appstoreapplication.activity.R;

/**
 * Created by Aria.Lao on 2016/12/27.
 * 我的游戏详情实体
 */
public class MyGameDetailEntity {
  @SerializedName("drawableid") public int imgUrl;
  /**
   * {@link TagConstance}
   */
  public int tag;
  public boolean isBuy;
  public boolean isAddPackage;
  /**
   * {@link TagConstance}
   */
  @SerializedName("hottag") public int hotTag;
  public boolean isChecked;
  public int totalSize;
  public int downloadSize;
  public int downloadState;
  @SerializedName("versionname") public String versionName;
  @SerializedName("versioncode") public int versionCode;
  @SerializedName("packagename") public String packageName;
  public String md5;
  @SerializedName("apkuri") public String downloadUrl; // download
  @SerializedName("productid") public int gameId;
  @SerializedName("productname") public String gameName;
  public String providername;
  @SerializedName("productstar") public String productStar;
  public String praise;
  @SerializedName("imageuri") public String gameIcon;
  public String desc;
  @SerializedName("producttype") public int productType;
  @SerializedName("downcount") public int downloadNum;// 下载数量
  /**
   * 0 :套餐包，1: 游戏
   */
  public int type = 1;

  /**
   * 套餐包Id
   */
  public String packageId;
  /**
   * 是否有更新
   */
  public boolean hasUpdate;

  public boolean isLastItem = false;
  public boolean isTemp = false;

  public int getImgUrl() {
    return imgUrl;
  }

  public void setImgUrl(int imgUrl) {
    this.imgUrl = imgUrl;
  }

  public int getTag() {
    return tag;
  }

  public void setTag(int tag) {
    this.tag = tag;
  }

  public boolean isBuy() {
    return isBuy;
  }

  public void setBuy(boolean buy) {
    isBuy = buy;
  }

  public boolean isAddPackage() {
    return isAddPackage;
  }

  public void setAddPackage(boolean addPackage) {
    isAddPackage = addPackage;
  }

  public int getHotTag() {
    return hotTag;
  }

  public void setHotTag(int hotTag) {
    this.hotTag = hotTag;
  }

  public boolean isChecked() {
    return isChecked;
  }

  public void setChecked(boolean checked) {
    isChecked = checked;
  }

  public int getTotalSize() {
    return totalSize;
  }

  public void setTotalSize(int totalSize) {
    this.totalSize = totalSize;
  }

  public int getDownloadSize() {
    return downloadSize;
  }

  public void setDownloadSize(int downloadSize) {
    this.downloadSize = downloadSize;
  }

  public int getDownloadState() {
    return downloadState;
  }

  public void setDownloadState(int downloadState) {
    this.downloadState = downloadState;
  }

  public String getVersionName() {
    return versionName;
  }

  public void setVersionName(String versionName) {
    this.versionName = versionName;
  }

  public int getVersionCode() {
    return versionCode;
  }

  public void setVersionCode(int versionCode) {
    this.versionCode = versionCode;
  }

  public String getPackageName() {
    return packageName;
  }

  public void setPackageName(String packageName) {
    this.packageName = packageName;
  }

  public String getMd5() {
    return md5;
  }

  public void setMd5(String md5) {
    this.md5 = md5;
  }

  public String getDownloadUrl() {
    return downloadUrl;
  }

  public void setDownloadUrl(String downloadUrl) {
    this.downloadUrl = downloadUrl;
  }

  public int getGameId() {
    return gameId;
  }

  public void setGameId(int gameId) {
    this.gameId = gameId;
  }

  public String getGameName() {
    return gameName;
  }

  public void setGameName(String gameName) {
    this.gameName = gameName;
  }

  public String getProvidername() {
    return providername;
  }

  public void setProvidername(String providername) {
    this.providername = providername;
  }

  public String getProductStar() {
    return productStar;
  }

  public void setProductStar(String productStar) {
    this.productStar = productStar;
  }

  public String getPraise() {
    return praise;
  }

  public void setPraise(String praise) {
    this.praise = praise;
  }

  public String getGameIcon() {
    return gameIcon;
  }

  public void setGameIcon(String gameIcon) {
    this.gameIcon = gameIcon;
  }

  public String getDesc() {
    return desc;
  }

  public void setDesc(String desc) {
    this.desc = desc;
  }

  public int getProductType() {
    return productType;
  }

  public void setProductType(int productType) {
    this.productType = productType;
  }

  public int getDownloadNum() {
    return downloadNum;
  }

  public void setDownloadNum(int downloadNum) {
    this.downloadNum = downloadNum;
  }

  public int getType() {
    return type;
  }

  public void setType(int type) {
    this.type = type;
  }

  public String getPackageId() {
    return packageId;
  }

  public void setPackageId(String packageId) {
    this.packageId = packageId;
  }

  public boolean isHasUpdate() {
    return hasUpdate;
  }

  public void setHasUpdate(boolean hasUpdate) {
    this.hasUpdate = hasUpdate;
  }
}
