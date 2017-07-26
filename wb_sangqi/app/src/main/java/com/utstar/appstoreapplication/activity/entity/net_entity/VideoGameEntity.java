package com.utstar.appstoreapplication.activity.entity.net_entity;

import com.google.gson.annotations.SerializedName;
import java.util.List;

/**
 * Created by Aria.Lao on 2017/6/20.
 */

public class VideoGameEntity {
  @SerializedName("list1") public List<ChildEntity> videos;
  @SerializedName("list2") public List<ChildEntity> games;

  public boolean isBuy = false;


  public class ChildEntity {
    @SerializedName("Id") public int id;
    @SerializedName("gametag") public int gameTag;
    public String packageName;
    public String apkAddress;
    @SerializedName("downcount") public int downCount;
    public String name;
    public int type;
    public String code;
    @SerializedName("image") public String imgUrl;
  }
}
