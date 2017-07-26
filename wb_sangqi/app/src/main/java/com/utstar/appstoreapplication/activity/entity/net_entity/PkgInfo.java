package com.utstar.appstoreapplication.activity.entity.net_entity;

import com.google.gson.annotations.SerializedName;
import java.util.List;

/**
 * @author zeng_yong_chang@163.com
 */
public class PkgInfo {
  @SerializedName("IMG") public String imgUrl;
  public String name;
  @SerializedName("PRICE") public String price;
  public boolean isBuy;
  public List<PkgItem> list;
  public int size;
  public int tag;
}
