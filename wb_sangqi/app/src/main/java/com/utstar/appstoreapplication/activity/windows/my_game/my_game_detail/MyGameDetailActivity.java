package com.utstar.appstoreapplication.activity.windows.my_game.my_game_detail;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import butterknife.Bind;
import butterknife.OnClick;
import com.arialyy.aria.core.Aria;
import com.arialyy.aria.core.DownloadEntity;
import com.arialyy.aria.core.scheduler.OnSchedulerListener;
import com.arialyy.aria.core.task.Task;
import com.arialyy.frame.util.show.T;
import com.utstar.appstoreapplication.activity.BuildConfig;
import com.utstar.appstoreapplication.activity.R;
import com.utstar.appstoreapplication.activity.commons.constants.CommonConstant;
import com.utstar.appstoreapplication.activity.commons.widget.my_game_detail.MyGameDetailLayout;
import com.utstar.appstoreapplication.activity.databinding.ActivityMyGameDetailBinding;
import com.utstar.appstoreapplication.activity.entity.db_entity.DownloadInfo;
import com.utstar.appstoreapplication.activity.entity.net_entity.MyGameDetailEntity;
import com.utstar.appstoreapplication.activity.receiver.InstallReceiver;
import com.utstar.appstoreapplication.activity.tinker.BaseApp;
import com.utstar.appstoreapplication.activity.utils.ActionUtil;
import com.utstar.appstoreapplication.activity.utils.ApkUtil;
import com.utstar.appstoreapplication.activity.utils.DownloadHelpUtil;
import com.utstar.appstoreapplication.activity.windows.base.BaseActivity;
import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by Aria.Lao on 2016/12/27. 我的游戏详情
 */
public class MyGameDetailActivity extends BaseActivity<ActivityMyGameDetailBinding> {
  /**
   * 已有游戏
   */
  public static final int TYPE_MY_GAME = 0xa1;
  /**
   * 下载中
   */
  public static final int TYPE_DOWNLOADING = 0xa2;
  /**
   * 待更新
   */
  public static final int TYPE_UPDATE = 0xa3;
  /**
   * 传值键值
   */
  public static final String TYPE_KEY = "TYPE_KEY";

  //我的游戏
  static final int MY_DATA = 0xaa2;

  @Bind(R.id.del) Button mDel;
  @Bind(R.id.vp) ViewPager mVp;
  @Bind(R.id.arrow_left) ImageView mArrowLeft;
  @Bind(R.id.arrow_right) ImageView mArrowRight;
  private SimpleViewPagerAdapter mAdapter;

  private int mType = -1;
  private int mCurrentPosition = 0;
  static final int CANCEL = 0x0a1;
  static final int FAIL = 0x0a2;
  static final int COMPLETE = 0x0a3;
  static final int RUNNING = 0x0a4;
  static final int NORMAL = 0x0a5;
  static final int STOP = 0x0a6;
  private Map<Integer, MyGameDetailFragment> mFragments = new HashMap<>();
  List<MyGameDetailEntity> mTotalList = new ArrayList<>();
  private DownloadListener mListener;
  private int mPage = 0;

  private int mTotalSize;
  private Handler mHandler = new Handler() {
    @Override public void handleMessage(Message msg) {
      super.handleMessage(msg);
      Task task = (Task) msg.obj;
      if (!checkFragmentNoNull()) {
        return;
      }
      MyGameDetailFragment fragment = mFragments.get(mCurrentPosition);
      if (fragment.mType == MyGameDetailLayout.UPDATE) {
        return;
      }

      switch (msg.what) {
        case STOP:
          fragment.updateState(task);
          break;
        case CANCEL:
          fragment.removeItemByUrl(task);
          setPositionText(0);
          break;
        case FAIL:
          new Thread() {
            @Override public void run() {
              Looper.prepare();
              T.showLong(BaseApp.context, "下载失败");
              Looper.loop();
            }
          }.start();
          fragment.updateState(task);
          break;
        case COMPLETE:
          if (mType == TYPE_DOWNLOADING || mType == TYPE_MY_GAME) {
            fragment.updateState(task);
          } else {
            DownloadHelpUtil.handleDownloadComplete(task.getDownloadEntity().getDownloadUrl());
            fragment.updateState(task);
          }
          break;
        case RUNNING:
          if (checkFragmentNoNull()) {
            fragment.updateProgress(task);
          }
          break;
        case NORMAL:
          fragment.updateState(task);
          break;
      }
    }

    private boolean checkFragmentNoNull() {
      return (mFragments != null && mFragments.get(mCurrentPosition) != null);
    }
  };

  private BroadcastReceiver mReceiver = new BroadcastReceiver() {

    @Override public void onReceive(Context context, Intent intent) {
      String action = intent.getAction();
      String packageName = intent.getStringExtra(InstallReceiver.DATA_PKG_NAME);
      if (InstallReceiver.ACTION_APK_INSTALL.equals(action)) {
        handleInstallApk(packageName);
      } else if (InstallReceiver.ACTION_APK_UNINSTALL.equals(action)) {
        //mFragments.get(mCurrentPosition).removeItemByPackageName(true, packageName);
        mFragments.get(findGameInFragment(packageName)).removeItemByPackageName(true, packageName);
        mTotalSize--;
        setPositionText(0);
      }
    }

    private int findGameInFragment(String packageName) {
      if (mTotalList == null || mTotalList.size() == 0) return 0;
      int position = 1;
      for (MyGameDetailEntity entity : mTotalList) {
        if (!entity.isAddPackage && entity.packageName.equals(packageName)) {
          break;
        }
        position++;
      }

      int location = position % 10;
      if (location == 0) return position / 10 - 1;
      if (position <= 10) return 0;
      return position / 10;
    }

    private void handleInstallApk(String packageName) {
      List<DownloadInfo> infos =
          DownloadInfo.where("packageName = ?", packageName).find(DownloadInfo.class);
      if (infos != null && infos.size() > 0) {
        DownloadInfo info = infos.get(0);
        String apkPath = info.getDownloadPath();
        if (!TextUtils.isEmpty(apkPath)) {
          File apk = new File(apkPath);
          if (apk.exists()) apk.delete();
        }
        if (mType == TYPE_DOWNLOADING) {
          mFragments.get(findGameInFragment(packageName)).removeItemByPackageName(packageName);
          mTotalSize--;
          setPositionText(0);
        } else {
          mFragments.get(mCurrentPosition)
              .updateState(info.getDownloadUrl(), DownloadEntity.STATE_COMPLETE);
        }
      }
    }
  };

  public int getTotalSize() {
    return mTotalSize;
  }

  public int downTotalSize() {
    mTotalSize--;
    return mTotalSize;
  }

  @Override protected int setLayoutId() {
    return R.layout.activity_my_game_detail;
  }

  @Override protected void init(Bundle savedInstanceState) {
    super.init(savedInstanceState);
    mType = getIntent().getIntExtra(TYPE_KEY, -1);
    if (mType == -1) {
      throw new IllegalArgumentException("必须传入type");
    }
    String typeName = "";
    String btName = "";
    mAdapter = new SimpleViewPagerAdapter(getSupportFragmentManager());
    mVp.setAdapter(mAdapter);
    if (mType == TYPE_MY_GAME) {
      typeName = "已有游戏";
      btName = "游戏卸载";
      getModule(MyGameDetailModule.class).getMyGame(1);
    } else if (mType == TYPE_DOWNLOADING) {
      typeName = "下载中";
      btName = "游戏删除";
      getModule(MyGameDetailModule.class).getDownloadingData();
    } else if (mType == TYPE_UPDATE) {
      typeName = "待更新";
      mDel.setVisibility(View.GONE);
      getModule(MyGameDetailModule.class).getUpdate(1);
    }
    getBinding().setTypeName(typeName);
    getBinding().setBtName(btName);

    mVp.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
      @Override
      public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

      }

      @Override public void onPageSelected(int position) {
        handleArrow(position);
      }

      @Override public void onPageScrollStateChanged(int state) {

      }
    });
  }

  @Override public boolean onKeyDown(int keyCode, KeyEvent event) {
    if (event.getAction() == KeyEvent.ACTION_DOWN) {
      if (BuildConfig.DEBUG) {
        if (keyCode == KeyEvent.KEYCODE_PAGE_DOWN || keyCode == KeyEvent.KEYCODE_2) {
          return handleDown();
        } else if (keyCode == KeyEvent.KEYCODE_PAGE_UP || keyCode == KeyEvent.KEYCODE_1) {
          return handleUp();
        }
      } else {
        if (keyCode == KeyEvent.KEYCODE_PAGE_DOWN) {
          return handleDown();
        } else if (keyCode == KeyEvent.KEYCODE_PAGE_UP) {
          return handleUp();
        }
      }
    }
    return super.onKeyDown(keyCode, event);
  }

  private boolean handleDown() {
    if (mCurrentPosition == mFragments.size() - 1) {
      return false;
    } else {
      mVp.setCurrentItem(mCurrentPosition + 1, true);
      return true;
    }
  }

  private boolean handleUp() {
    if (mCurrentPosition == 0) {
      return false;
    } else {
      mVp.setCurrentItem(mCurrentPosition - 1, true);
      return true;
    }
  }

  public int getPageCount() {
    if (mVp != null && mVp.getAdapter() != null) {
      return mVp.getAdapter().getCount();
    } else {
      return 0;
    }
  }

  /**
   * 处理箭头
   */
  private void handleArrow(int currentPosition) {
    mCurrentPosition = currentPosition;
    if (mPage > 1) {
      if (currentPosition == 0) {
        mArrowLeft.setVisibility(View.GONE);
        mArrowRight.setVisibility(View.VISIBLE);
      } else if (currentPosition == mFragments.size() - 1) {
        mArrowLeft.setVisibility(View.VISIBLE);
        mArrowRight.setVisibility(View.GONE);
      } else {
        mArrowLeft.setVisibility(View.VISIBLE);
        mArrowRight.setVisibility(View.VISIBLE);
      }
    } else {
      mCurrentPosition = 0;
      mArrowRight.setVisibility(View.GONE);
      mArrowLeft.setVisibility(View.GONE);
    }
    setPositionText(mFragments.get(currentPosition).getCurrentItem());
  }

  /**
   * 处理item删除操作
   */
  private void handleRemove(MyGameDetailEntity entity) {
    mTotalList.remove(entity);
    if (mTotalList.size() <= 0) finish();
    if (mPage == 1) {
      mFragments.get(0).updateDate(mTotalList);
      return;
    }
    int page = getModule(MyGameDetailModule.class).countPage(mTotalList);
    if (mPage == page) {
      for (int i = 0, count = getPageSize(mTotalList.size()); i < count; i++) {
        int start = i * 10;
        int end = i * 10 + 10;
        if (end >= mTotalList.size()) end = mTotalList.size();
        List<MyGameDetailEntity> subList = mTotalList.subList(start, end);
        mFragments.get(i).updateDate(subList);
      }
    } else {
      mAdapter.removeAll();
      mAdapter = new SimpleViewPagerAdapter(getSupportFragmentManager());
      if (mVp == null) return;
      mVp.setAdapter(mAdapter);
      mVp.removeAllViewsInLayout();
      MyGameDetailFragment fragment;
      for (int i = 0, count = getPageSize(mTotalList.size()); i < count; i++) {
        int start = i * 10;
        int end = i * 10 + 10;
        if (end >= mTotalList.size()) end = mTotalList.size();
        fragment = new MyGameDetailFragment(mType, mTotalList.subList(start, end));
        mFragments.put(i, fragment);
        mAdapter.addFrag(fragment, "i = " + i);
      }
      mAdapter.notifyDataSetChanged();
      //mPage = mFragments.size();
      mVp.setOffscreenPageLimit(mPage);
    }
    mPage = page;
    handleArrow(mCurrentPosition);
  }

  public synchronized void update(boolean isUnInstall, MyGameDetailEntity entity) {
    if (isUnInstall) {
      handleRemove(entity);
    } else {
      if (mType == TYPE_MY_GAME) {
        getModule(MyGameDetailModule.class).getMyGame(1);
      } else if (mType == TYPE_DOWNLOADING) {
        getModule(MyGameDetailModule.class).getDownloadingData();
      } else if (mType == TYPE_UPDATE) {
        getModule(MyGameDetailModule.class).getUpdate(1);
      }
      mArrowLeft.setVisibility(View.GONE);
      mCurrentPosition = 0;
      setPositionText(0);
      mVp.setCurrentItem(0);
    }
  }

  /**
   * 删除按钮事件
   */
  @OnClick(R.id.del) public void delBt() {
    if (mFragments.size() > 0) {
      boolean isShowCheckBox = mFragments.get(0).isShowCheckBox();
      setBtText(!isShowCheckBox);
      if (!isShowCheckBox) {
        Set<Integer> keys = mFragments.keySet();
        for (Integer key : keys) {
          mFragments.get(key).showCheckBox(true);
        }
      } else {
        Set<Integer> keys = mFragments.keySet();
        for (Integer key : keys) {
          mFragments.get(key).handleRemove();
          mFragments.get(key).showCheckBox(false);
        }
      }
    }
  }

  private void setBtText(boolean isSelected) {
    String btName = "";
    if (mType == TYPE_MY_GAME) {
      btName = isSelected ? "完成卸载" : "游戏卸载";
    } else {
      btName = isSelected ? "完成删除" : "游戏删除";
    }
    getBinding().setBtName(btName);
  }

  @Override protected void onResume() {
    super.onResume();
    IntentFilter filter = new IntentFilter();
    filter.addDataScheme(getPackageName());
    filter.addAction(InstallReceiver.ACTION_APK_INSTALL);
    filter.addAction(InstallReceiver.ACTION_APK_UNINSTALL);
    if (mReceiver != null) {
      registerReceiver(mReceiver, filter);
    }
    if (mListener == null) {
      mListener = new DownloadListener(mHandler);
      Aria.whit(this).addSchedulerListener(mListener);
    }
  }

  @Override protected void onDestroy() {
    super.onDestroy();
    unregisterReceiver(mReceiver);
    Aria.whit(this).removeSchedulerListener();
  }

  private void initGameDetailLayout(List<MyGameDetailEntity> list) {
    if (list != null && list.size() > 0) {
      mTotalList.clear();
      mTotalList = list;
      mFragments.clear();
      mTotalSize = list.size();
      MyGameDetailFragment fragment;
      if (list.size() <= 10) {
        fragment = new MyGameDetailFragment(mType, list);
        mFragments.put(0, fragment);
        mAdapter.addFrag(fragment, "i = " + 0);
      } else {
        //for (int i = 0, count = list.size() / 10 + 1; i < count; i++) {
        for (int i = 0, count = getPageSize(list.size()); i < count; i++) {
          int start = i * 10;
          int end = i * 10 + 10;
          if (end >= list.size()) end = list.size();
          fragment = new MyGameDetailFragment(mType, list.subList(start, end));
          mFragments.put(i, fragment);
          mAdapter.addFrag(fragment, "i = " + i);
        }
      }
      mAdapter.notifyDataSetChanged();
      mPage = mFragments.size();
      mVp.setOffscreenPageLimit(mPage);
    }
    handleArrow(0);
  }

  public int getPageSize(double dataSize) {
    return (int) Math.ceil(dataSize / 10);
  }

  void setPositionText(int position) {
    int p = mCurrentPosition * 10 + position + 1;
    if (mTotalSize <= 0) {
      finish();
    }
    getBinding().setPosition("（" + p + "/" + mTotalSize + "）");
  }

  @Override protected void dataCallback(int result, Object data) {
    super.dataCallback(result, data);
    if (result == MY_DATA) {
      initGameDetailLayout((List<MyGameDetailEntity>) data);
    }
  }

  @Override public void finish() {
    setResult(Activity.RESULT_OK);
    super.finish();
  }

  /**
   * 下载监听
   */
  private static class DownloadListener implements OnSchedulerListener {
    WeakReference<Handler> handler;

    DownloadListener(Handler handler) {
      this.handler = new WeakReference<>(handler);
    }

    @Override public void onTaskPre(Task task) {
      handler.get().obtainMessage(NORMAL, task).sendToTarget();
    }

    @Override public void onTaskResume(Task task) {
      handler.get().obtainMessage(NORMAL, task).sendToTarget();
    }

    @Override public void onTaskStart(Task task) {
      handler.get().obtainMessage(NORMAL, task).sendToTarget();
    }

    @Override public void onTaskStop(Task task) {
      handler.get().obtainMessage(STOP, task).sendToTarget();
    }

    @Override public void onTaskCancel(Task task) {
      handler.get().obtainMessage(CANCEL, task).sendToTarget();
    }

    @Override public void onTaskFail(Task task) {
      handler.get().obtainMessage(FAIL, task).sendToTarget();
    }

    @Override public void onTaskComplete(Task task) {
      handler.get().obtainMessage(COMPLETE, task).sendToTarget();
    }

    @Override public void onTaskRunning(Task task) {
      handler.get().obtainMessage(RUNNING, task).sendToTarget();
    }
  }
}
