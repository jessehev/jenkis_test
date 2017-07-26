package com.utstar.appstoreapplication.activity.entity.net_entity;

import com.google.gson.annotations.SerializedName;
import java.util.List;

/**
 * Created by JesseHev on 2016/12/21.
 */

public class GameRankEntity {
  @SerializedName("typename") public String typeName;
  @SerializedName("typeid") public int typeId;
  public int size;
  @SerializedName("sublist") public List<GameRankSubEntity> mGameRankSubEntity;

  public static class GameRankSubEntity {

    public boolean isAddPackage;
    @SerializedName("hottag") public int hotTag;
    @SerializedName("productid") public String productId;
    public int tag;
    @SerializedName("packagename") public String packageName;
    @SerializedName("imageuri") public String imageUri;
    @SerializedName("downcount") public int downCount;
    @SerializedName("productname") public String productName;
    //最新游戏套餐包id
    @SerializedName("packageId") public String newPackageId;
    //付费排行套餐包id
    @SerializedName("packageid") public String payPackageId;
    public String type = "-1";//付费排行默认值0
  }
}
