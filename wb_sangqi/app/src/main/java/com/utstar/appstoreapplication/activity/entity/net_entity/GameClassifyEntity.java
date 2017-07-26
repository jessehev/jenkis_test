package com.utstar.appstoreapplication.activity.entity.net_entity;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;
import java.util.List;

public class GameClassifyEntity implements Serializable {
  @SerializedName("typeid") public int typeId;
  @SerializedName("typename") public String typeName;
  @SerializedName("sublist") public List<GameClassifySubEntity> gameClassifySubEntities;

  public static class GameClassifySubEntity {

    public boolean isAddPackage;
    public String desc;
    @SerializedName("hottag") public int hotTag;
    @SerializedName("providername") public String providerName;
    @SerializedName("productid") public String productId;
    public String packageId;
    @SerializedName("productstar") public int productStar;
    public int tag;
    @SerializedName("packagename") public String packageName;
    @SerializedName("imageuri") public String imageUri;
    public int praise;
    @SerializedName("productname") public String productName;
    @SerializedName("downcount") public int downCount;
    @SerializedName("productmode") public int mode;
    public boolean isBuy;
  }
}