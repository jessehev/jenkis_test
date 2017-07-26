package com.utstar.appstoreapplication.activity.entity.net_entity;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class SearchEntity {
  public List<SearchResultEntity> list;
  public int size;

  public static class SearchResultEntity {
    @SerializedName("productid") public String productId;
    @SerializedName("productname") public String productName;
    @SerializedName("imageuri") public String img;
    public boolean isBuy;
    public boolean isAddPackage;
    public String packageId;
  }
}
