package com.utstar.appstoreapplication.activity.temp.activity;

import android.os.Bundle;
import com.arialyy.frame.util.AndroidUtils;
import com.arialyy.frame.util.SharePreUtil;
import com.utstar.appstoreapplication.activity.R;
import com.utstar.appstoreapplication.activity.commons.constants.KeyConstant;
import com.utstar.appstoreapplication.activity.databinding.ActivityTemp2Binding;
import com.utstar.appstoreapplication.activity.databinding.ActivityTempBinding;
import com.utstar.appstoreapplication.activity.windows.base.BaseActivity;

/**
 * Created by utstarcom on 2016/12/8.
 */

public class TempActivity_2 extends BaseActivity<ActivityTemp2Binding> {
  @Override protected int setLayoutId() {
    return R.layout.activity_temp_2;
  }

  @Override protected void init(Bundle savedInstanceState) {
    super.init(savedInstanceState);
    String oldTinkerId =
        SharePreUtil.getString(KeyConstant.PRE_NAME, this, KeyConstant.PRE.TINKER_ID);
    //getBinding().setVersion("热更新版本号:" + oldTinkerId);
    getBinding().setData("TEMP界面___3\n"
        + "apk版本号: "
        + AndroidUtils.getVersionCode(this)
        + ", 热更新版本号:"
        + oldTinkerId);
  }
}
