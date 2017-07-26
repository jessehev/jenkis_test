package com.utstar.appstoreapplication.activity.entity.common_entity;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Aria.Lao on 2016/12/20.
 * 网络数据组装容器，用于直接处理没有进行实体封装的数据
 */
public class NetObjectEntity<T> {
  @SerializedName("list") public T obj;
}
