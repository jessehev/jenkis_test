package com.utstar.appstoreapplication.activity.utils;

import android.content.Context;
import android.content.pm.IPackageStatsObserver;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageStats;
import android.os.Handler;
import android.os.Looper;
import android.os.RemoteException;
import android.text.TextUtils;
import com.arialyy.frame.util.AndroidUtils;
import com.arialyy.frame.util.show.FL;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 获取应用安装后的大小
 *
 * @author Aria.Lao
 */
public class ApkSizeUtil {
  private static final String TAG = "ApkSizeUtil";
  private long mTotalSize = 0;
  private Handler mHandler;
  private Map<String, ApkSizeCallback> mCallbacks = new HashMap<>();
  private boolean isCountAllAppSize = false;

  private int mCurrentPosition = 0, mAppInfoSize = 0;
  private ApkSizeCallback mCountCallback;

  interface ApkSizeCallback {
    public void result(long size);
  }

  public ApkSizeUtil() {
    mHandler = new Handler(Looper.getMainLooper());
  }

  /**
   * 指定应用的大小
   *
   * @param packageName 指定应用的包名
   */
  public void getAppSize(Context context, String packageName, ApkSizeCallback callback) {
    isCountAllAppSize = false;
    if (!TextUtils.isEmpty(packageName)) {
      if (callback != null) {
        mCallbacks.put(packageName, callback);
      }
      //使用放射机制得到PackageManager类的隐藏函数getPackageSizeInfo
      PackageManager pm = context.getPackageManager();
      try {
        //通过反射机制获得该隐藏函数
        Method getPackageSizeInfo = pm.getClass()
            .getMethod("getPackageSizeInfo", String.class, IPackageStatsObserver.class);
        getPackageSizeInfo.invoke(pm, packageName, new PkgSizeObserver());
      } catch (Exception ex) {
        FL.e(TAG, FL.getExceptionString(ex));
      }
    }
  }

  /**
   * 获取所有非系统应用的总大小
   */
  public void getAllAppSize(Context context, ApkSizeCallback callback) {
    isCountAllAppSize = true;
    if (callback != null) {
      mCountCallback = callback;
    }
    //使用放射机制得到PackageManager类的隐藏函数getPackageSizeInfo
    PackageManager pm = context.getPackageManager();  //得到pm对象
    try {
      Method getPackageSizeInfo =
          pm.getClass().getMethod("getPackageSizeInfo", String.class, IPackageStatsObserver.class);
      List<PackageInfo> infos = AndroidUtils.getAllNoSystemApps(context);
      mAppInfoSize = infos.size();
      for (PackageInfo info : infos) {
        getPackageSizeInfo.invoke(pm, info.packageName, new PkgSizeObserver());
      }
    } catch (Exception ex) {
      FL.e(TAG, FL.getExceptionString(ex));
    }
  }

  private class PkgSizeObserver extends IPackageStatsObserver.Stub {
    /*** 回调函数，
     * @param pStats ,返回数据封装在PackageStats对象中
     * @param succeeded  代表回调成功
     */
    @Override public void onGetStatsCompleted(PackageStats pStats, boolean succeeded)
        throws RemoteException {
      if (succeeded) {
        long cacheSize = pStats.cacheSize; //缓存大小
        long dataSize = pStats.dataSize;  //数据大小
        long codeSize = pStats.codeSize;  //应用程序大小
        if (isCountAllAppSize) {
          mCurrentPosition++;
          mTotalSize += cacheSize + dataSize + codeSize;
          if (mAppInfoSize != 0 && mCurrentPosition == mAppInfoSize) {
            if (mCountCallback != null) {
              mHandler.post(() -> mCountCallback.result(mTotalSize));
            }
          }
        } else {
          final long totalSize = cacheSize + dataSize + codeSize;
          final ApkSizeCallback callback = mCallbacks.get(pStats.packageName);
          if (callback != null) {
            mHandler.post(() -> callback.result(totalSize));
          }
        }
      } else {
        FL.e(TAG, "获取应用总大小失败");
      }
    }
  }
}