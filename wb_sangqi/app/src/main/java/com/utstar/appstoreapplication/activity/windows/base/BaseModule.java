package com.utstar.appstoreapplication.activity.windows.base;

import android.content.Context;
import android.databinding.ViewDataBinding;
import com.arialyy.frame.module.AbsModule;
import com.utstar.appstoreapplication.activity.manager.NetManager;

/**
 * Created by “Aria.Lao” on 2015/11/11.
 * 基本模型层
 */
public class BaseModule<A extends ViewDataBinding> extends AbsModule {
  A a;
  protected NetManager mNetManager = NetManager.getInstance();

  public BaseModule(Context context) {
    super(context);
  }
}