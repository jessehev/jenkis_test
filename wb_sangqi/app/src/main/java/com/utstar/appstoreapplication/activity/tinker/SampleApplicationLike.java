package com.utstar.appstoreapplication.activity.tinker;

import android.annotation.TargetApi;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.os.Build;
import android.support.multidex.MultiDex;
import com.arialyy.aria.orm.DbUtil;
import com.arialyy.frame.core.AbsFrame;
import com.tencent.tinker.anno.DefaultLifeCycle;
import com.tencent.tinker.lib.tinker.Tinker;
import com.tencent.tinker.lib.tinker.TinkerInstaller;
import com.tencent.tinker.loader.app.DefaultApplicationLike;
import com.tencent.tinker.loader.shareutil.ShareConstants;
import com.utstar.appstoreapplication.activity.BuildConfig;
import com.utstar.appstoreapplication.activity.db.sdcard.RecordDbUtil;
import com.utstar.appstoreapplication.activity.tinker.Log.MyLogImp;
import com.utstar.appstoreapplication.activity.tinker.util.TinkerManager;
import org.litepal.LitePalApplication;

/**
 * Created by Aria.Lao on 2016/12/7.
 * Application 代理类
 */
@SuppressWarnings("unused")
@DefaultLifeCycle(application = "com.utstar.appstoreapplication.activity.tinker.SampleApplication", flags = ShareConstants.TINKER_ENABLE_ALL, loadVerifyFlag = false)
public class SampleApplicationLike extends DefaultApplicationLike {
  public SampleApplicationLike(Application application, int tinkerFlags,
      boolean tinkerLoadVerifyFlag, long applicationStartElapsedTime,
      long applicationStartMillisTime, Intent tinkerResultIntent, Resources[] resources,
      ClassLoader[] classLoader, AssetManager[] assetManager) {
    super(application, tinkerFlags, tinkerLoadVerifyFlag, applicationStartElapsedTime,
        applicationStartMillisTime, tinkerResultIntent, resources, classLoader, assetManager);
  }

  @Override public void onCreate() {
    super.onCreate();
  }

  /**
   * install multiDex before install tinker
   * so we don't need to put the tinker lib classes in the main dex
   */
  @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH) @Override public void onBaseContextAttached(
      Context base) {
    super.onBaseContextAttached(base);
    //you must install multiDex whatever tinker is installed!
    MultiDex.install(base);

    BaseApp.application = getApplication();
    BaseApp.context = base;

    //AbsFrame.init(this).openCrashHandler("serverHost", "paramsKey");
    if (!BuildConfig.DEBUG) {
      AbsFrame.init(base).openCrashHandler();
    } else {
      AbsFrame.init(base);
    }
    //AbsFrame.init(base).openCrashHandler();

    //DbUtil.init(base);
    RecordDbUtil.init(base);
    LitePalApplication.initialize(base);

    TinkerManager.setTinkerApplicationLike(this);
    //TinkerManager.initFastCrashProtect();
    //should set before tinker is installed
    TinkerManager.setUpgradeRetryEnable(true);

    //optional set logIml, or you can use default debug log
    TinkerInstaller.setLogIml(new MyLogImp());

    //installTinker after load multiDex
    //or you can put com.tencent.tinker.** to main dex
    TinkerManager.installTinker(this);
    Tinker tinker = Tinker.with(getApplication());
  }

  @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
  public void registerActivityLifecycleCallbacks(Application.ActivityLifecycleCallbacks callback) {
    getApplication().registerActivityLifecycleCallbacks(callback);
  }
}
