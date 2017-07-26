package com.utstar.appstoreapplication.activity.windows.base;

import android.app.Application;
import com.arialyy.aria.orm.DbUtil;
import com.arialyy.frame.core.AbsFrame;
import com.utstar.appstoreapplication.activity.db.sdcard.RecordDbUtil;
import com.utstar.appstoreapplication.activity.entity.db_entity.GameRecord;
import com.utstar.appstoreapplication.activity.tinker.BaseApp;
import org.litepal.LitePalApplication;

/**
 * Created by Aria.Lao on 2016/12/8.
 */
public class BaseApplication extends Application {

  private static BaseApplication sInstance;

  @Override public void onCreate() {
    super.onCreate();
    BaseApp.application = this;
    BaseApp.context = this;
    //AbsFrame.init(this).openCrashHandler("serverHost", "paramsKey");
    //DbUtil.init(this);
    AbsFrame.init(this);
    RecordDbUtil.init(this);
    LitePalApplication.initialize(this);
  }
}
