package com.utstar.appstoreapplication.activity.windows.base;

import android.databinding.ViewDataBinding;
import com.arialyy.frame.core.AbsActivity;
import com.utstar.appstoreapplication.activity.windows.common.LoadingDialog;
import java.util.Observer;
import java.util.concurrent.TimeUnit;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by “Aria.Lao” on 2016/10/17.
 * 基础的Activity，所有Activity都应该继承该类
 */
public abstract class BaseActivity<VB extends ViewDataBinding> extends AbsActivity<VB> {

  private LoadingDialog mLoadingDialog;

  /**
   * 显示加载等待对话框
   */
  protected void showLoadingDialog() {
    showLoadingDialog(true);
  }

  /**
   * 显示加载等待对话框
   *
   * @param canCancel 能被取消？
   */
  protected void showLoadingDialog(final boolean canCancel) {
    Observable.just("")
        .subscribeOn(Schedulers.newThread())
        .map(s -> new LoadingDialog(canCancel))
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(loadingDialog -> {
          mLoadingDialog = loadingDialog;
          loadingDialog.show(getSupportFragmentManager(), "loadingDialog");
        });
  }

  /**
   * 关闭加载等待对话框，保证对话框至少显示1秒
   */
  protected void dismissLoadingDialog() {
    Observable.just("").delay(1000, TimeUnit.MILLISECONDS, Schedulers.newThread()).subscribe(s -> {
      if (mLoadingDialog != null && mLoadingDialog.isVisible()) {
        mLoadingDialog.dismiss();
      }
    });
  }

  @Override protected void dataCallback(int result, Object data) {

  }
}
