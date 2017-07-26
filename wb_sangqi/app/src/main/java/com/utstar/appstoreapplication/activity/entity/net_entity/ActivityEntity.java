package com.utstar.appstoreapplication.activity.entity.net_entity;

import com.google.gson.annotations.SerializedName;
import java.util.List;

/**
 * Created by Administrator on 2017-03-06.
 * 精品专区 活动配置实体
 */

public class ActivityEntity {

  @SerializedName("PopType") public int popType; //popType :0 未弹出,1 关联活动已经弹出过了
  @SerializedName("list") public List<ActivitySubEntity> activitys;

  public static class ActivitySubEntity {
    @SerializedName("activityname") public String activityName;
    public int type;//活动类型:1 是抽奖活动,2是签到活动
  }
}
