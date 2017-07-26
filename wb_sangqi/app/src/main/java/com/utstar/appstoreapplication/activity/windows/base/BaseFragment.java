package com.utstar.appstoreapplication.activity.windows.base;

import android.databinding.ViewDataBinding;
import android.view.View;
import com.arialyy.frame.core.AbsFragment;
import com.arialyy.frame.util.show.FL;
import com.utstar.appstoreapplication.activity.R;
import com.utstar.appstoreapplication.activity.entity.net_entity.ModuleBgEntity;
import com.utstar.appstoreapplication.activity.manager.ImageManager;
import com.utstar.appstoreapplication.activity.module.CommonModule;
import com.utstar.appstoreapplication.activity.windows.common.LoadingDialog;
import com.utstar.appstoreapplication.activity.windows.main.MainActivity;
import java.util.concurrent.TimeUnit;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by “Aria.Lao” on 2015/11/6.
 * fragment基类
 */
public abstract class BaseFragment<VB extends ViewDataBinding> extends AbsFragment<VB> {
  private LoadingDialog mLoadingDialog;
  protected ModuleBgEntity mBgEntity;
  public int mBgType, mPkg;

  public View getRootView() {
    return mRootView;
  }

  @Override protected void onDelayLoad() {
  }

  protected void setBgParam(int type, int page) {
    mBgType = type;
    mPkg = page;
  }

  /**
   * 更改当前framgent所处的窗口的背景
   */
  public void changeBackground() {
    try {
      CommonModule module = getModule(CommonModule.class);
      if (mBgEntity == null && module != null) {
        module.getEachModuleBackground(mBgType, mPkg);
      } else {
        ImageManager.getInstance().loadBackground(getActivity(), mBgEntity);
      }
    } catch (Exception e) {
      FL.e(TAG, FL.getExceptionString(e));
      //ImageManager.getInstance().loadBackground(getActivity(), mBgEntity);
    }
  }

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
          loadingDialog.show(mActivity.getSupportFragmentManager(), "loadingDialog");
        });
  }

  /**
   * 关闭加载等待对话框，保证对话框至少显示1秒
   */
  protected void dismissLoadingDialog() {
    Observable.just("").delay(1000, TimeUnit.MILLISECONDS, Schedulers.newThread()).subscribe(s -> {
      if (mLoadingDialog != null) {
        mLoadingDialog.dismiss();
      }
    });
  }

  public void updateData() {

  }

  @Override protected void dataCallback(int result, Object obj) {
    if (result == CommonModule.MODULEBG_SUCCESS) {
      mBgEntity = (ModuleBgEntity) obj;
      ImageManager.getInstance().loadBackground(getActivity(), mBgEntity);
    } else if (result == CommonModule.MODULEBG_FAILURE) {
      ((BaseActivity) getActivity()).getRootView().setBackgroundResource(R.mipmap.bg_base);
    }
  }

  /**
   * 网络数据返回为空
   */
  protected void onNetDataNull() {
  }

  /**
   * 网络错误
   */
  protected void onNetError() {
  }
}