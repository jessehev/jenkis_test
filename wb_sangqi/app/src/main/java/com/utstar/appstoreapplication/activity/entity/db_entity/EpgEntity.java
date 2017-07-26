package com.utstar.appstoreapplication.activity.entity.db_entity;

import org.litepal.crud.DataSupport;

/**
 * Created by “Aria.Lao” on 2016/10/26.
 * 用户实体
 */
public class EpgEntity extends DataSupport {
  public static final String DEF_EPG_SERVER =
      "http://192.168.0.12:33200/EPG/jsp/gdgaoqing/en/Category.jsp";
  private String epgUserId;
  private String epgSessionId = "";
  private String epgAreAId = "";
  private String epgServer = "";
  private String wbAccount = "";
  private String channel = "";

  public String getEpgUserId() {
    return epgUserId;
  }

  public void setEpgUserId(String epgUserId) {
    this.epgUserId = epgUserId;
  }

  public String getEpgSessionId() {
    return epgSessionId;
  }

  public void setEpgSessionId(String epgSessionId) {
    this.epgSessionId = epgSessionId;
  }

  public String getEpgAreAId() {
    return epgAreAId;
  }

  public void setEpgAreAId(String epgAreAId) {
    this.epgAreAId = epgAreAId;
  }

  public String getEpgServer() {
    return epgServer;
  }

  public void setEpgServer(String epgServer) {
    this.epgServer = epgServer;
  }

  public String getWbAccount() {
    return wbAccount;
  }

  public void setWbAccount(String wbAccount) {
    this.wbAccount = wbAccount;
  }

  public String getChannel() {
    return channel;
  }

  public void setChannel(String channel) {
    this.channel = channel;
  }
}
