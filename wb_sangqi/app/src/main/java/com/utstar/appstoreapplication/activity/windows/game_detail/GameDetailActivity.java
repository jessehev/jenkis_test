package com.utstar.appstoreapplication.activity.windows.game_detail;

import com.ut.wb.ui.adapter.RvItemClickSupport;
import com.utstar.appstoreapplication.activity.BuildConfig;
import com.utstar.appstoreapplication.activity.manager.ActivityManager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Looper;
import android.support.v17.leanback.widget.VerticalGridView;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;
import butterknife.Bind;
import butterknife.OnClick;
import butterknife.OnFocusChange;
import com.arialyy.aria.core.Aria;
import com.arialyy.aria.core.DownloadEntity;
import com.arialyy.aria.core.DownloadManager;
import com.arialyy.aria.core.scheduler.OnSchedulerListener;
import com.arialyy.aria.core.task.Task;
import com.arialyy.frame.core.AbsActivity;
import com.arialyy.frame.core.AbsFrame;
import com.arialyy.frame.util.AndroidUtils;
import com.arialyy.frame.util.FileUtil;
import com.arialyy.frame.util.show.FL;
import com.arialyy.frame.util.show.T;
import com.utstar.appstoreapplication.activity.Probe.ProbeService;
import com.utstar.appstoreapplication.activity.R;
import com.utstar.appstoreapplication.activity.databinding.ActivityGameDetailBinding;
import com.utstar.appstoreapplication.activity.entity.common_entity.UpdateIconEntity;
import com.utstar.appstoreapplication.activity.entity.net_entity.GameDetailEntity;
import com.utstar.appstoreapplication.activity.entity.net_entity.GameUninstallEntity;
import com.utstar.appstoreapplication.activity.entity.net_entity.GuessEntity;
import com.utstar.appstoreapplication.activity.manager.ActionManager;
import com.utstar.appstoreapplication.activity.manager.AnimManager;
import com.utstar.appstoreapplication.activity.manager.ImageManager;
import com.utstar.appstoreapplication.activity.manager.TurnManager;
import com.utstar.appstoreapplication.activity.receiver.InstallReceiver;
import com.utstar.appstoreapplication.activity.tinker.BaseApp;
import com.utstar.appstoreapplication.activity.utils.ApkUtil;
import com.utstar.appstoreapplication.activity.utils.DownloadHelpUtil;
import com.utstar.appstoreapplication.activity.windows.base.BaseActivity;
import com.utstar.appstoreapplication.activity.windows.game_detail.uninstall_dialog.GameUninstallDialog;
import com.utstar.appstoreapplication.activity.windows.main.MainActivity;
import com.utstar.appstoreapplication.activity.windows.monthly_payment.MothlyPayActivity;
import java.io.File;
import java.lang.ref.WeakReference;
import java.util.List;
import java.util.Stack;

import static com.utstar.appstoreapplication.activity.tinker.BaseApp.context;

/**
 * Created by Aria.Lao on 2016/12/23. 游戏详情
 */
public class GameDetailActivity extends BaseActivity<ActivityGameDetailBinding> {
  static final int GAME_DETAIL_RESULT = 0xac4;
  static final int PRAISE = 0Xac5;  //点赞
  static final int UN_PRAISE = 0Xac6;  //取消点赞
  static final int UNINSTALL_INFO = 0Xac7;  //卸载信息
  /**
   * 从套餐包进入
   */
  public final static int PACKAGE_GAME = 0Xe1;
  public final static String KEY_GAME_ID = "GAME_ID";
  public final static String TYPE_ID = "TYPE_ID";
  @Bind(R.id.game_icon) ImageView mGameIcon;
  @Bind(R.id.star_bar) RatingBar mStarBar;
  @Bind(R.id.download_num) TextView mDownloadNum;
  @Bind(R.id.game_size) TextView mGameSize;
  @Bind(R.id.sb) ImageView mSb;
  @Bind(R.id.ykq) ImageView mYkq;
  @Bind(R.id.zan) ImageView mZan;
  @Bind(R.id.del) Button mDel;
  @Bind(R.id.frame_download) FrameLayout mDownloadFrame;
  @Bind(R.id.download_pb) ProgressBar mDownloadPb;
  @Bind(R.id.download_bt) Button mDownloadBt;
  @Bind(R.id.screenshot_list) VerticalGridView mGameShot;
  @Bind(R.id.guess_list) VerticalGridView mGuess;
  @Bind(R.id.percent) TextView mPercent;

  /**
   * 游戏Id
   */
  private int mGameId = -1;
  /**
   * 从哪个模块跳转到游戏详情界面的 ID（后台做统计功能)
   * type=1  从热门推荐进入
   * type=2  从套餐包进入
   */
  private int mType = 0;
  private GameDetailEntity mGameDetailEntity;
  private GameDetailModule mModule;
  private DownloadListener mListener;
  private boolean isBuySuccess = false;
  private DownloadEntity mDownloadEntity;
  private boolean isKilled = false;

  private BroadcastReceiver mReceiver = new BroadcastReceiver() {

    @Override public void onReceive(Context context, Intent intent) {
      if (mGameDetailEntity == null) {
        return;
      }
      String action = intent.getAction();
      String packageName = intent.getStringExtra(InstallReceiver.DATA_PKG_NAME);
      if (InstallReceiver.ACTION_APK_INSTALL.equals(action)) {
        if (mGameDetailEntity.packageName.equals(packageName)) {
          mModule.setDownloadBtInfo(mDownloadBt, mGameDetailEntity);
        }
      } else if (InstallReceiver.ACTION_APK_UNINSTALL.equals(action)) {
        if (mGameDetailEntity.packageName.equals(packageName)) {
          mModule.setDownloadBtInfo(mDownloadBt, mGameDetailEntity);
        }
      }
    }
  };

  @Override protected int setLayoutId() {
    return R.layout.activity_game_detail;
  }

  @Override protected void init(Bundle savedInstanceState) {
    super.init(savedInstanceState);
    mGameId = getIntent().getIntExtra(KEY_GAME_ID, -1);
    if (mGameId == -1) {
      throw new IllegalArgumentException("游戏ID不能为null");
    }
    mType = getIntent().getIntExtra(TYPE_ID, 0);
    mModule = getModule(GameDetailModule.class);
    mModule.getGameDetailData(mGameId, mType);
    //getModule(GameDetailModule.class).getUninstallInfo();
    //if (BuildConfig.DEBUG) {
    //  getModule(GameDetailModule.class).getUninstallInfo();
    //}
  }

  /**
   * 设置游戏详情数据
   */
  private void setGameData(GameDetailEntity detailEntity) {
    if (detailEntity == null) {
      return;
    }
    if (mListener != null) {
      mListener.setEntity(detailEntity);
    }
    mGameDetailEntity = detailEntity;
    Aria.get(this).openBroadcast(true);
    mDownloadFrame.requestFocus();
    int radius = (int) getResources().getDimension(R.dimen.dimen_15dp);
    ImageManager.getInstance().loadRoundedImg(mGameIcon, detailEntity.imgUrl, radius);
    ActivityGameDetailBinding bind = getBinding();
    bind.setGameDetail(detailEntity);
    bind.setDownloadNum(mModule.getDownloadNumText(detailEntity.downloadNum));
    bind.setApkSize("大小：" + detailEntity.apkSize + "M");
    bind.setShowSb((detailEntity.mode & 1) == 1);
    bind.setShowYkq((detailEntity.mode & 2) == 2);
    mStarBar.setProgress(detailEntity.star);
    setZan();
    final GameShotAdapter shotAdapter = new GameShotAdapter(this, detailEntity.gameShots);
    mGameShot.setAdapter(shotAdapter);
    final GameGuessAdapter guessAdapter = new GameGuessAdapter(this, detailEntity.guessList);
    mGuess.setAdapter(guessAdapter);
    mModule.setDownloadBtInfo(mDownloadBt, mGameDetailEntity);
    RvItemClickSupport.addTo(mGuess).setOnItemClickListener((recyclerView, position, v) -> {
      GuessEntity guess = mGameDetailEntity.guessList.get(position);
      //if (guess.isAddPackage) {
      //  TurnManager.getInstance()
      //      .turnPackage(GameDetailActivity.this, guess.packageId, guess.gameId + "");
      //} else {
      //  TurnManager.getInstance().turnGameDetail(GameDetailActivity.this, guess.gameId);
      //}
      TurnManager.getInstance().turnGameDetail(GameDetailActivity.this, guess.gameId);
    });
    RvItemClickSupport.addTo(mGameShot).setOnItemClickListener((recyclerView, position, v) -> {
      String videoUrl = detailEntity.gameShots.get(position).videoUrl;
      if (!TextUtils.isEmpty(videoUrl)) {
        TurnManager.getInstance().turnVideoPlay(GameDetailActivity.this, videoUrl);
      }
    });
  }

  /**
   * 下载按钮和下载进度条
   */
  @OnFocusChange(R.id.frame_download) public void onFrameFocusChange(View view, boolean hasFocus) {
    if (hasFocus) {
      AnimManager.getInstance().enlarge(view, 1.2f);
    } else {
      AnimManager.getInstance().narrow(view, 1.0f);
    }
    if (mGameDetailEntity == null) {
      return;
    }
    DownloadEntity dEntity = mModule.getDownloadEntity(mGameDetailEntity.downloadUrl);
    if (dEntity == null) {
      mDownloadBt.setVisibility(View.VISIBLE);
      mDownloadPb.setVisibility(View.INVISIBLE);
      return;
    }
    Task task = DownloadManager.getInstance().getTaskQueue().getTask(dEntity);
    if (hasFocus || task == null) {
      mDownloadBt.setVisibility(View.VISIBLE);
      mDownloadPb.setVisibility(View.INVISIBLE);
      mPercent.setVisibility(View.GONE);
    } else if (task.isDownloading()) {
      mDownloadBt.setVisibility(View.INVISIBLE);
      mDownloadPb.setVisibility(View.VISIBLE);
      mPercent.setVisibility(View.VISIBLE);
    } else {
      mDownloadBt.setVisibility(View.VISIBLE);
      mDownloadPb.setVisibility(View.INVISIBLE);
      mPercent.setVisibility(View.GONE);
    }
  }

  /**
   * 点赞按钮事件
   */
  @OnClick(R.id.zan) public void zan() {
    if (mGameDetailEntity == null) {
      return;
    }
    if (mGameDetailEntity.isPraise.equals("0")) {
      mModule.doPraise(mGameDetailEntity.gameId);
    } else if (mGameDetailEntity.isPraise.equals("1")) {
      mModule.unPraise(mGameDetailEntity.gameId);
    }
  }

  /**
   * 下载按钮事件
   */
  @OnClick(R.id.frame_download) public void onDownloadBtClick(View view) {
    if (mGameDetailEntity == null) return;
    if (!mGameDetailEntity.isBuy) {
      if (mGameDetailEntity.isAddPackage) {
        TurnManager.getInstance()
            .turnOrderDetail(this, 0, mGameDetailEntity.packageId, mGameDetailEntity.gameId);
        //上报apk用户日志，方便后台做统计功能
        ActionManager.getInstance()
            .statistcsBuy(mType, mGameDetailEntity.packageId, mGameDetailEntity.gameId);
      } else {
        TurnManager.getInstance().turnOrderDetail(this, 1, mGameDetailEntity.gameId + "");
        //上报apk用户日志，方便后台做统计功能
        ActionManager.getInstance()
            .statistcsBuy(mType, mGameDetailEntity.packageId, mGameDetailEntity.gameId);
      }
      return;
    } else if (mGameDetailEntity.isShelves) {
      T.show(GameDetailActivity.this, "游戏已下架", Toast.LENGTH_SHORT);
      return;
    }

    DownloadEntity entity = mModule.getDownloadEntity(mGameDetailEntity.downloadUrl);
    final File apk = new File(DownloadHelpUtil.getApkDownloadPath(mGameDetailEntity.downloadUrl,
        mGameDetailEntity.packageName));
    if (entity == null) {
      if (AndroidUtils.isInstall(this, mGameDetailEntity.packageName)) {
        //AndroidUtils.startOtherApp(this, mGameDetailEntity.packageName);
        //ProbeService.start(this, mGameDetailEntity.packageName);
        ApkUtil.startGame(this, mGameDetailEntity.packageName);
      } else if (apk.exists() && FileUtil.checkMD5(mGameDetailEntity.apkMd5Code, apk)) {
        //AndroidUtils.install(this, apk);
        ApkUtil.installApk(this, apk.getPath());
      } else {
        entity = mModule.createDownloadEntity(mGameDetailEntity.downloadUrl,
            mGameDetailEntity.packageName);
        handleDownload(entity);
      }
      //entity = mModule.createDownloadEntity(mGameDetailEntity.downloadUrl,
      //    mGameDetailEntity.packageName);
      //handleDownload(entity);
    } else {
      switch (entity.getState()) {
        case DownloadEntity.STATE_WAIT:
        case DownloadEntity.STATE_FAIL:
        case DownloadEntity.STATE_CANCEL:
          Aria.whit(this).load(entity).start();
          mZan.requestFocus();
          //mModule.uploadLog(mGameDetailEntity.gameId);
          break;
        case DownloadEntity.STATE_PRE:
        case DownloadEntity.STATE_POST_PRE:
        case DownloadEntity.STATE_DOWNLOAD_ING:
          //Aria.whit(this).load(entity).stop();
          break;
        case DownloadEntity.STATE_STOP:
          Aria.whit(this).load(entity).resume();
          mZan.requestFocus();
          break;
        case DownloadEntity.STATE_COMPLETE:
        case DownloadEntity.STATE_OTHER:
          if (AndroidUtils.isInstall(this, mGameDetailEntity.packageName)) {
            //AndroidUtils.startOtherApp(this, mGameDetailEntity.packageName);
            //ProbeService.start(this, mGameDetailEntity.packageName);
            ApkUtil.startGame(this, mGameDetailEntity.packageName);
          } else if (apk.exists() && FileUtil.checkMD5(mGameDetailEntity.apkMd5Code, apk)) {
            //AndroidUtils.install(this, apk);
            ApkUtil.installApk(this, apk.getPath());
          } else {
            handleDownload(entity);
          }
          break;
      }
    }
  }

  private void handleDownload(DownloadEntity entity) {
    mDownloadEntity = entity;
    if (mModule.isNeedNoticeUninstallMsg()) {
      mModule.getUninstallInfo();
    } else {
      download();
    }
  }

  private void download() {
    if (mGameDetailEntity != null && mDownloadEntity != null && mModule.checkoutSpace(
        mGameDetailEntity.apkSize)) {
      DownloadHelpUtil.createDownloadInfo(mDownloadEntity.getDownloadPath(), mGameDetailEntity);
      if (DownloadHelpUtil.isMaxDownload()) {
        mDownloadBt.setText("等待中");
      }
      DownloadHelpUtil.download(this, mDownloadEntity);
    }
  }

  @OnFocusChange(R.id.zan) void onBtFocusChange(View view, boolean focusChange) {
    if (focusChange) {
      AnimManager.getInstance().enlarge(view, 1.2f);
    } else {
      AnimManager.getInstance().narrow(view, 1.0f);
    }
  }

  /**
   * 设置点赞按钮
   */
  private void setZan() {
    mZan.setImageResource(
        mGameDetailEntity.isPraise.equals("0") ? R.mipmap.icon_zan : R.mipmap.icon_zan_yes);
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
      mListener = new DownloadListener(mModule, mDownloadPb, mDownloadBt, mPercent);
      Aria.whit(this).addSchedulerListener(mListener);
    }
  }

  @Override protected void onDestroy() {
    super.onDestroy();
    if (mReceiver != null) {
      unregisterReceiver(mReceiver);
    }
    Aria.whit(this).removeSchedulerListener();
  }

  @Override protected void dataCallback(int result, Object data) {
    super.dataCallback(result, data);
    if (result == GAME_DETAIL_RESULT && data != null) {
      setGameData((GameDetailEntity) data);
    } else if (result == PRAISE) {
      mGameDetailEntity.isPraise = (boolean) data ? "1" : "0";
      mGameDetailEntity.starNum++;
      getBinding().setGameDetail(mGameDetailEntity);
      setZan();
    } else if (result == UN_PRAISE) {
      mGameDetailEntity.isPraise = (boolean) data ? "0" : "1";
      mGameDetailEntity.starNum--;
      getBinding().setGameDetail(mGameDetailEntity);
      setZan();
    } else if (result == UNINSTALL_INFO) {
      if (data == null) {
        download();
        return;
      }
      List<GameUninstallEntity> list = (List<GameUninstallEntity>) data;
      if (list.size() == 0) {
        download();
        return;
      }
      GameUninstallDialog dialog = new GameUninstallDialog(this, list);
      dialog.setOnDismissListener(dialog1 -> download());
      dialog.show();
    }
  }

  @Override public void finish() {
    try {
      UpdateIconEntity entity =
          new UpdateIconEntity(isBuySuccess, mGameDetailEntity != null && mGameDetailEntity.isBuy);
      Intent intent = new Intent();
      intent.putExtra(TurnManager.UPDATE_ICON_KEY, entity);
      intent.putExtra(TurnManager.PAY_SUCCESS_KEY, isBuySuccess);
      setResult(TurnManager.PAY_RESULT_CODE, intent);
      super.finish();
    } catch (Exception e) {
      FL.d(TAG, FL.getExceptionString(e));
    }
  }

  @Override protected void onSaveInstanceState(Bundle outState) {
    FL.d(TAG, "Activity被销毁，不是用户主动按back销毁");
    //mModule.getAvailMemory();
    //mModule.getTotalMemory();
    outState.putParcelable("data", mGameDetailEntity);

    super.onSaveInstanceState(outState);
  }

  @Override public void onRestoreInstanceState(Bundle savedInstanceState) {
    super.onRestoreInstanceState(savedInstanceState);
    try {
      if (mModule == null) {
        reNewModule();
        mModule = getModule(GameDetailModule.class);
      }
      mGameDetailEntity = savedInstanceState.getParcelable("data");
      isKilled = true;
      setGameData(mGameDetailEntity);
      mDownloadBt.setVisibility(View.VISIBLE);
      getBinding().setDownloadBtText("打开");
    } catch (Exception e) {
      FL.d(TAG, FL.getExceptionString(e));
    }
    FL.d(TAG, "Activity被kill, 重新调用");
  }

  @Override public void onBackPressed() {
    try {
      if (mGameDetailEntity != null) {
        if (mGameDetailEntity.isAddPackage) {
          Stack<AbsActivity> stack = AbsFrame.getInstance().getActivityStack();
          if (stack == null) {
            super.onBackPressed();
            return;
          }
          int index = 0;
          for (AbsActivity activity : stack) {
            if (activity.getClass().getName().equals(MothlyPayActivity.class.getName())
                && index == stack.size() - 2) {
              super.onBackPressed();
              return;
            }
            index++;
          }
          AbsFrame.getInstance().finishActivity(GameDetailActivity.class);
          TurnManager.getInstance()
              .turnPackage(this, mGameDetailEntity.packageId,
                  String.valueOf(mGameDetailEntity.gameId));
        } else if (isKilled) {
          AndroidUtils.reStartApp(BaseApp.application);
        } else {
          if (AbsFrame.getInstance().activityExists(MainActivity.class)) {
            super.onBackPressed();
          } else {
            AbsFrame.getInstance().finishActivity(GameDetailActivity.class);
            startActivity(new Intent(this, MainActivity.class));
          }
        }
      } else {
        super.onBackPressed();
      }
    } catch (Exception e) {
      FL.d(TAG, FL.getExceptionString(e));
      super.onBackPressed();
    }
  }

  @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    if (requestCode == TurnManager.PAY_REQUEST_CODE
        && resultCode == TurnManager.PAY_RESULT_CODE
        && data != null
        && data.getBooleanExtra(TurnManager.PAY_SUCCESS_KEY, false)) {
      mGameDetailEntity.isBuy = true;
      isBuySuccess = true;
      mModule.setDownloadBtInfo(mDownloadBt, mGameDetailEntity);
      //弹出推半价优惠荐页
      ActivityManager.getInstance().getPackageData(this, ActivityManager.SWICH_OFF);
    }
  }

  /**
   * 下载监听
   */
  private static class DownloadListener implements OnSchedulerListener {
    WeakReference<ProgressBar> mPb;
    WeakReference<Button> mBt;
    WeakReference<GameDetailModule> mModule;
    WeakReference<TextView> mPercent;
    WeakReference<GameDetailEntity> mEntity;

    DownloadListener(GameDetailModule module, ProgressBar pb, Button bt, TextView percent) {
      mModule = new WeakReference<>(module);
      mPb = new WeakReference<>(pb);
      mBt = new WeakReference<>(bt);
      mPercent = new WeakReference<>(percent);
    }

    public void setEntity(GameDetailEntity entity) {
      mEntity = new WeakReference<>(entity);
    }

    @Override public void onTaskPre(Task task) {
      if (isSlfe(task)) {
        DownloadEntity entity = task.getDownloadEntity();
        int percent = 0;
        if (entity.getFileSize() != 0) {
          percent = (int) (entity.getCurrentProgress() * 100 / entity.getFileSize());
        }
        mPercent.get().setText(percent + "%");
        //mBt.get().setText("暂停");
        mBt.get().setText("下载中");
        showBt(false);
      }
    }

    @Override public void onTaskResume(Task task) {
      if (isSlfe(task)) {
        //mBt.get().setText("暂停");
        mBt.get().setText("下载中");
        showBt(false);
        DownloadEntity entity = task.getDownloadEntity();
        if (entity != null && task.getDownloadEntity().getFileSize() != 0) {
          mPb.get().setProgress((int) (entity.getCurrentProgress() * 100 / entity.getFileSize()));
        }
      }
    }

    @Override public void onTaskStart(Task task) {

    }

    @Override public void onTaskStop(Task task) {
      if (isSlfe(task)) {
        mBt.get().setText("恢复下载");
        showBt(true);
      }
    }

    @Override public void onTaskCancel(Task task) {

    }

    @Override public void onTaskFail(Task task) {
      if (isSlfe(task)) {
        new Thread() {
          @Override public void run() {
            Looper.prepare();
            T.showLong(context, "下载失败");
            Looper.loop();
          }
        }.start();
        mBt.get().setText("下载");
        showBt(true);
      }
    }

    @Override public void onTaskComplete(Task task) {
      if (isSlfe(task)) {
        mModule.get().setDownloadBtInfo(mBt.get(), mEntity.get());
        showBt(true);
      }
    }

    @Override public void onTaskRunning(Task task) {
      if (isSlfe(task)) {
        DownloadEntity entity = task.getDownloadEntity();
        if (entity.getFileSize() != 0) {
          int percent = (int) (entity.getCurrentProgress() * 100 / entity.getFileSize());
          mPb.get().setProgress(percent);
          mPercent.get().setText(percent + "%");
          //mPercent.get().setText(FileUtil.formatFileSize(task.getSpeed()) + "/s");
        }
      }
    }

    private boolean isSlfe(Task task) {
      if (task == null) return false;
      DownloadEntity entity = task.getDownloadEntity();
      return entity != null && !(TextUtils.isEmpty(entity.getDownloadUrl())
          || mEntity == null
          || mEntity.get() == null) && mEntity.get().downloadUrl.equals(entity.getDownloadUrl());
    }

    private void showBt(boolean isShow) {
      if (isShow) {
        mBt.get().setVisibility(View.VISIBLE);
        mPb.get().setVisibility(View.INVISIBLE);
        mPercent.get().setVisibility(View.GONE);
      } else {
        mBt.get().setVisibility(View.INVISIBLE);
        mPb.get().setVisibility(View.VISIBLE);
        mPercent.get().setVisibility(View.VISIBLE);
      }
    }
  }
}
