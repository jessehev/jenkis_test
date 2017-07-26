package com.utstar.appstoreapplication.activity.entity.net_entity;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;
import java.util.List;

/**
 * Created by JesseHev on 2016/12/14.
 */

public class GameRankMoreEntity implements Serializable {
  public String desc;
  public int tag;
  @SerializedName("productstar") public int productStar;
  public String packagename;
  @SerializedName("packageid") public String packageId;
  @SerializedName("hottag") public int hotTag;
  @SerializedName("providername") public String providerName;
  @SerializedName("productid") public int productId;
  @SerializedName("imageuri") public String imageUri;
  public int praise;
  @SerializedName("productname") public String productName;
  @SerializedName("downcount") public int downCount;

  public boolean isBuy = false;
  @SerializedName("productmode") public int productMode;
}
