package com.utstar.appstoreapplication.activity.manager;

import android.text.TextUtils;
import com.arialyy.frame.cache.CacheUtil;
import com.arialyy.frame.util.FileUtil;
import com.arialyy.frame.util.show.L;
import com.bumptech.glide.Glide;
import com.utstar.appstoreapplication.activity.entity.CacheEntity;
import com.utstar.appstoreapplication.activity.tinker.BaseApp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by “Aria.Lao” on 2016/10/26.
 * 内存管理
 */
public class MemoryManager extends CommonManager {
  private static final Object LOCK = new Object();
  private static volatile MemoryManager INSTANCE = null;
  private CacheUtil mCacheUtil;
  /**
   * 缓存列表
   */
  private static List<String> CACHE_LIST = new ArrayList<>();

  /**
   * 缓存有效时间
   */
  public static long CACHE_VALID_TIME = 1000 * 60;

  /**
   * 缓存有效次数
   */
  public static int CACHE_VALID_NUM = 5;

  static {
    CACHE_LIST.add("shop/shopDetailedList.do");
  }

  public static MemoryManager getInstance() {
    if (INSTANCE == null) {
      synchronized (LOCK) {
        INSTANCE = new MemoryManager();
      }
    }
    return INSTANCE;
  }

  @Override void init() {
    CacheUtil.Builder builder = new CacheUtil.Builder(BaseApp.context);
    mCacheUtil = builder.openDiskCache().openDiskCache().build();
  }

  @Override String initName() {
    return "MemoryManager";
  }

  /**
   * 存储缓存
   */
  public void saveCache(String url, String value) {
    if (TextUtils.isEmpty(value)) {
      L.e("缓存数据位null");
      return;
    }
    for (String key : CACHE_LIST) {
      if (url.contains(key)) return;
    }

    CacheEntity cache = mCacheUtil.getObjectCache(CacheEntity.class, url);
    if (cache == null) {
      cache = new CacheEntity();
    }
    cache.setCacheTime(System.currentTimeMillis());
    cache.setAvailableNum(cache.getAvailableNum() + 1);
    cache.setData(value);
    mCacheUtil.putObjectCache(CacheEntity.class, url, cache);
  }

  /**
   * 获取有效的缓存
   */
  public CacheEntity getCache(String url) {
    if (TextUtils.isEmpty(url)) {
      return null;
    }
    CacheEntity cache = mCacheUtil.getObjectCache(CacheEntity.class, url);
    if (cache == null) {
      return null;
    }
    if (cache.isAvailable()) {
      return cache;
    }
    return null;
  }

  /**
   * 通过url和请求参数获取key
   */
  public String getKey(String url, Map<String, String> headers) {
    if (headers == null || headers.size() > 0) {
      return url;
    }
    String p = "";
    Set<String> keys = headers.keySet();
    for (String key : keys) {
      p += key + headers.get(key);
    }
    url += "," + p;
    return url;
  }

  /**
   * 获取总的缓存大小
   */
  public long getCacheSize() {
    return getImgCacheSize() + getOtherCacheSize();
  }

  /**
   * 获取其它缓存大小
   */
  public long getOtherCacheSize() {
    return mCacheUtil.getCacheSize();
  }

  /**
   * 获取图片磁盘缓存大小
   */
  public long getImgCacheSize() {
    return FileUtil.getDirSize(Glide.getPhotoCacheDir(BaseApp.context).getPath());
  }

  /**
   * 清楚缓存
   */
  public void clearCache() {
    if (mCacheUtil != null) {
      mCacheUtil.clearCache();
    }
    Glide.get(BaseApp.context).clearMemory();
    new Thread(() -> {
      Glide.get(BaseApp.context).clearDiskCache();
    }).start();
  }

  @Override void onDestroy() {
    if (mCacheUtil != null) {
      mCacheUtil.close();
    }
    Glide.get(BaseApp.context).clearMemory();
  }
}
