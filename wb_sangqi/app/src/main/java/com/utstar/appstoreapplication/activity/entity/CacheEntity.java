package com.utstar.appstoreapplication.activity.entity;

import com.utstar.appstoreapplication.activity.manager.MemoryManager;

/**
 * Created by Aria.Lao on 2017/2/13.
 */

public class CacheEntity {
  private String data;
  private long cacheTime = 0;
  private long availableNum = 0;

  /**
   * 缓存是否有效
   */
  public boolean isAvailable() {
    if (System.currentTimeMillis() - cacheTime > MemoryManager.CACHE_VALID_TIME) {
      return false;
    } else if (availableNum >= MemoryManager.CACHE_VALID_NUM) {
      return false;
    }
    return true;
  }

  public String getData() {
    return data;
  }

  public void setData(String data) {
    this.data = data;
  }

  public long getCacheTime() {
    return cacheTime;
  }

  public void setCacheTime(long cacheTime) {
    this.cacheTime = cacheTime;
  }

  public long getAvailableNum() {
    return availableNum;
  }

  public void setAvailableNum(long availableNum) {
    this.availableNum = availableNum;
  }
}
