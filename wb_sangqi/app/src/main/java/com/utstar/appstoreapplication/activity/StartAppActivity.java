package com.utstar.appstoreapplication.activity;

import android.os.Bundle;
import android.widget.ImageView;

import com.utstar.appstoreapplication.activity.databinding.ActivityLauncherBinding;
import com.utstar.appstoreapplication.activity.windows.base.BaseActivity;
import com.utstar.appstoreapplication.activity.windows.launcher.LauncherModule;

import butterknife.Bind;

/**
 * Created by Aria.Lao on 2016/12/8.
 * 启动界面
 */
public class StartAppActivity extends BaseActivity<ActivityLauncherBinding> {
  @Bind(R.id.img) ImageView mImg;

  @Override protected int setLayoutId() {
    return R.layout.activity_launcher;
  }

  @Override protected void init(Bundle savedInstanceState) {
    super.init(savedInstanceState);
    getWindow().setBackgroundDrawableResource(R.mipmap.bg_launcher_2);
    getModule(LauncherModule.class).start(getIntent(), mImg);
    //getModule(LauncherModule.class).checkStart();
  }

  @Override public void onBackPressed() {
    //super.onBackPressed();
  }

  @Override protected void dataCallback(int result, Object data) {
    super.dataCallback(result, data);
    //if (result == KeyConstant.LAUNCHER_KEY.INIT_EPG_ID){
    //
    //}
  }
}
