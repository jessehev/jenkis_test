package com.utstar.appstoreapplication.activity.entity.db_entity;

import org.litepal.crud.DataSupport;

/**
 * Created by Aria.Lao on 2017/3/8.
 * 更新数据
 */
public class UpdateInfo extends DataSupport {
  private String downloadUrl;
  private String md5Code;
  private String packageName;
  private String downloadPath;
  private String gameName;
  private String gameIcon;
  private int gameId;
  private boolean isAddPackage = false;
  private boolean isBuy;
  private boolean isComplete = false;

  public String getDownloadUrl() {
    return downloadUrl;
  }

  public void setDownloadUrl(String downloadUrl) {
    this.downloadUrl = downloadUrl;
  }

  public String getMd5Code() {
    return md5Code;
  }

  public void setMd5Code(String md5Code) {
    this.md5Code = md5Code;
  }

  public String getPackageName() {
    return packageName;
  }

  public void setPackageName(String packageName) {
    this.packageName = packageName;
  }

  public String getDownloadPath() {
    return downloadPath;
  }

  public void setDownloadPath(String downloadPath) {
    this.downloadPath = downloadPath;
  }

  public String getGameName() {
    return gameName;
  }

  public void setGameName(String gameName) {
    this.gameName = gameName;
  }

  public String getGameIcon() {
    return gameIcon;
  }

  public void setGameIcon(String gameIcon) {
    this.gameIcon = gameIcon;
  }

  public int getGameId() {
    return gameId;
  }

  public void setGameId(int gameId) {
    this.gameId = gameId;
  }

  public boolean isAddPackage() {
    return isAddPackage;
  }

  public void setAddPackage(boolean addPackage) {
    isAddPackage = addPackage;
  }

  public boolean isBuy() {
    return isBuy;
  }

  public void setBuy(boolean buy) {
    isBuy = buy;
  }

  public boolean isComplete() {
    return isComplete;
  }

  public void setComplete(boolean complete) {
    isComplete = complete;
  }
}
