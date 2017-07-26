package com.utstar.appstoreapplication.activity.commons.widget.my_game_detail;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.arialyy.aria.core.Aria;
import com.arialyy.aria.core.DownloadEntity;
import com.arialyy.frame.util.AndroidUtils;
import com.arialyy.frame.util.FileUtil;
import com.arialyy.frame.util.show.FL;
import com.arialyy.frame.util.show.L;
import com.arialyy.frame.util.show.T;
import com.makeramen.roundedimageview.RoundedImageView;
import com.ut.wb.ui.MarqueTextView;
import com.ut.wb.ui.TagUtil;
import com.ut.wb.ui.progress.RoundProgressBarWidthNumber;
import com.utstar.appstoreapplication.activity.Probe.ProbeService;
import com.utstar.appstoreapplication.activity.R;
import com.utstar.appstoreapplication.activity.entity.db_entity.DownloadInfo;
import com.utstar.appstoreapplication.activity.entity.db_entity.UpdateInfo;
import com.utstar.appstoreapplication.activity.entity.net_entity.MyGameDetailEntity;
import com.utstar.appstoreapplication.activity.manager.ImageManager;
import com.utstar.appstoreapplication.activity.manager.TurnManager;
import com.utstar.appstoreapplication.activity.utils.ApkUtil;
import com.utstar.appstoreapplication.activity.utils.DownloadHelpUtil;
import com.utstar.appstoreapplication.activity.utils.GameUpdateUtil;
import com.utstar.appstoreapplication.activity.windows.common.AuthUtil;
import com.utstar.appstoreapplication.activity.windows.common.MsgDialog;
import com.utstar.appstoreapplication.activity.windows.my_game.my_game_detail.MyGameDetailActivity;
import java.io.File;
import org.litepal.crud.DataSupport;

/**
 * Created by Aria.Lao on 2016/12/27.
 * 我的游戏详情item
 */
@SuppressLint("ViewConstructor") final class MyGameDetailItemView extends RelativeLayout {
  ImageView mGameIcon, mPause, mHasUpdate, mTag, mHotTag;
  RoundedImageView mFloorImg;
  MarqueTextView mGameName;
  CheckBox mCheckBox;
  RoundProgressBarWidthNumber mPb;
  View mMaskBg, mBg;
  TextView mState;
  private int mType;
  private DownloadEntity mDownloadEntity;
  private MyGameDetailEntity mEntity;
  /**
   * 当前是否被选中
   */
  private boolean isChecked = false;
  private boolean isHasFocus = false;
  private boolean isShowCheckBox = false;
  private onRemoveCallback mCallback;

  public MyGameDetailItemView(Context context, int type) {
    super(context);
    mType = type;
    init();
  }

  interface onRemoveCallback {
    public void onRemove(View view);
  }

  private void init() {
    LayoutInflater.from(getContext()).inflate(R.layout.item_my_game_detail, this, true);
    mGameIcon = getView(R.id.game_icon);
    mPause = getView(R.id.pause);
    mHasUpdate = getView(R.id.has_update);
    mFloorImg = getView(R.id.floor_img);
    mGameName = getView(R.id.game_name);
    mCheckBox = getView(R.id.check);
    mPb = getView(R.id.pb);
    mMaskBg = getView(R.id.mask_bg);
    mState = getView(R.id.state);
    mBg = getView(R.id.bg);
    mTag = getView(R.id.tag);
    mHotTag = getView(R.id.hot_tag);
  }

  public void setOnRemoveCallback(onRemoveCallback callback) {
    mCallback = callback;
  }

  /**
   * 绑定数据
   */
  void bindData(MyGameDetailEntity entity) {
    if (entity == null) {
      L.w("数据不能为 null");
      return;
    }
    mEntity = entity;
    int radius = (int) getResources().getDimension(R.dimen.dimen_15dp);
    ImageManager.getInstance().loadRoundedImg(mGameIcon, entity.gameIcon, radius);
    mGameName.setText(entity.gameName);
    if (mType == MyGameDetailLayout.UPDATE) {   //待更新
      handleUI();
      handleHotTag(mHotTag, entity.hotTag);
      handleTag(mTag, entity.tag, entity.isBuy);
    } else if (mType == MyGameDetailLayout.MY_GAME) {  //我的游戏
      handleUI();
    } else if (mType == MyGameDetailLayout.DOWNLOADING) {  //下载中
      handleUI();
    }
  }

  public int getType() {
    return mType;
  }

  protected void handleHotTag(ImageView img, int tag) {
    TagUtil.handleHotTag(img, tag);
  }

  protected void handleTag(ImageView img, int tag, boolean isBuy) {
    TagUtil.handleTag(img, tag, isBuy);
  }

  /**
   * 处理点击
   */
  public void handleClick() {
    if (mType == MyGameDetailLayout.DOWNLOADING) {
      handleDownloadClick();
    } else if (mType == MyGameDetailActivity.TYPE_UPDATE) {
      handleUpdateClick();
    } else if (mType == MyGameDetailLayout.MY_GAME) {
      handleMyGameClick();
    }
  }

  private void handleMyGameClick() {
    if (isShowCheckBox) {
      setChecked(isChecked = !isChecked);
      return;
    }
    if (mEntity.isAddPackage) {
      TurnManager.getInstance().turnPackage(getContext(), mEntity.packageId);
    } else {
      startApp();
    }
  }

  /**
   * 处理待更新的点击事件
   */
  private void handleUpdateClick() {
    startDownload(null);
  }

  private void installApk(File apk) {
    if (apk.exists() && FileUtil.checkMD5(mEntity.md5, apk)) {
      //AndroidUtils.install(getContext(), apk);
      ApkUtil.installApk(getContext(), apk.getPath());
    } else {
      FL.d(this, "下载地址【" + mEntity.getDownloadUrl() + "】的apk文件md5码错误，即将重新下载");
      //startDownload(mDownloadEntity);
      T.showShort(getContext(), "文件错误");
    }
  }

  private void handleDownloadClick() {
    if (isShowCheckBox) {
      setChecked(isChecked = !isChecked);
      return;
    }
    if (mEntity.isAddPackage) {
      TurnManager.getInstance().turnPackage(getContext(), mEntity.packageId);
    } else {
      File apk =
          new File(DownloadHelpUtil.getApkDownloadPath(mEntity.downloadUrl, mEntity.packageName));
      if (mDownloadEntity == null) return;
      if (mDownloadEntity.isDownloadComplete()) {
        if (AndroidUtils.isInstall(getContext(), mEntity.packageName)) {
          if (GameUpdateUtil.isUpdate(mEntity.packageName)) {
            installApk(apk);
          } else {
            startApp();
          }
        } else {
          installApk(apk);
        }
      } else {
        if (mDownloadEntity == null) {
          mDownloadEntity = createDownloadEntity();
          return;
        }
        switch (mDownloadEntity.getState()) {
          case DownloadEntity.STATE_WAIT:
          case DownloadEntity.STATE_OTHER:
          case DownloadEntity.STATE_FAIL:
            startDownload(mDownloadEntity);
            break;
          case DownloadEntity.STATE_POST_PRE:
          case DownloadEntity.STATE_PRE:
          case DownloadEntity.STATE_DOWNLOAD_ING:
            //stopDownload();
            break;
          case DownloadEntity.STATE_STOP:
            resumeDownload();
            break;
          case DownloadEntity.STATE_COMPLETE:
            if (AndroidUtils.isInstall(getContext(), mEntity.packageName)) {
              startApp();
            } else if (apk.exists()) {
              //AndroidUtils.install(getContext(), apk);
              ApkUtil.installApk(getContext(), apk.getPath());
            } else {
              startDownload(mDownloadEntity);
            }
            break;
        }
      }
    }
  }

  private void startApp() {
    if (mEntity == null) return;
    AuthUtil util = new AuthUtil();
    util.auth(mEntity.getGameId(), new AuthUtil.AuthCallback() {
      @Override public void onSuccess() {
        //AndroidUtils.startOtherApp(getContext(), mEntity.packageName);
        //ProbeService.start(getContext(), mEntity.packageName);
        ApkUtil.startGame(getContext(), mEntity.packageName);
      }

      @Override public void onFailure(String errorCode) {
        if (errorCode.equals("3002") || errorCode.equals("1001")) {
          String msg = errorCode.equals("3002") ? "你的订购已过期，是否继续订购？" : "你没有订购该产品，是否进行订购？";
          final Context context = getContext();
          MsgDialog dialog = new MsgDialog(context, "系统提示", msg, true);
          dialog.setDialogCallback(new MsgDialog.OnMsgDialogCallback() {
            @Override public void onEnter() {
              TurnManager.getInstance().turnOrderDetail(getContext(), 1, mEntity.getGameId() + "");
            }

            @Override public void onCancel() {

            }
          });
          dialog.show();
        }
      }

      @Override public void onShelves(String state) {
        if (state.equals("已下架")) {
          MsgDialog dialog = new MsgDialog(getContext(), "系统提示", "该游戏已下线，请卸载游戏！", false);
          dialog.show();
        }
      }

      @Override public void onError() {

      }
    });
  }

  private void cancelDownload() {
    mPause.setVisibility(VISIBLE);
    mPb.setVisibility(GONE);
    Aria.whit(getContext()).load(mDownloadEntity).cancel();
  }

  private void resumeDownload() {
    mPause.setVisibility(GONE);
    mPb.setVisibility(VISIBLE);
    Aria.whit(getContext()).load(mDownloadEntity).resume();
  }

  private void stopDownload() {
    mPause.setVisibility(VISIBLE);
    mPb.setVisibility(GONE);
    Aria.whit(getContext()).load(mDownloadEntity).stop();
  }

  private DownloadEntity createDownloadEntity() {
    DownloadEntity entity = new DownloadEntity();
    entity.setDownloadPath(
        DownloadHelpUtil.getApkDownloadPath(mEntity.downloadUrl, mEntity.packageName));
    entity.setFileName(
        DownloadHelpUtil.getApkDownloadName(mEntity.downloadUrl, mEntity.packageName));
    entity.setDownloadUrl(mEntity.downloadUrl);
    return entity;
  }

  private void startDownload(DownloadEntity entity) {
    mPause.setVisibility(GONE);
    mPb.setVisibility(VISIBLE);
    if (mDownloadEntity == null) {
      mDownloadEntity = createDownloadEntity();
    }

    DownloadHelpUtil.createDownloadInfo(mDownloadEntity.getDownloadPath(), mEntity);
    Aria.whit(getContext()).load(mDownloadEntity).start();
  }

  private void delApk(File apk) {
    if (apk != null && apk.exists()) {
      apk.delete();
    }
  }

  public void handleRemove() {
    if (mType == MyGameDetailLayout.MY_GAME) {
      ApkUtil.unInstallApk(getContext(), mEntity.packageName);
    } else if (mType == MyGameDetailLayout.DOWNLOADING) {
      File apk =
          new File(DownloadHelpUtil.getApkDownloadPath(mEntity.downloadUrl, mEntity.packageName));
      if (mDownloadEntity == null) return;
      if (mDownloadEntity.isDownloadComplete()) {
        if (AndroidUtils.isInstall(getContext(), mEntity.packageName)) {
          if (GameUpdateUtil.isUpdate(mEntity.packageName)) {
            delApk(apk);
          } else {
            ApkUtil.unInstallApk(getContext(), mEntity.packageName);
          }
        } else {
          delApk(apk);
        }
      } else {
        cancelDownload();
      }
      DataSupport.deleteAll(DownloadInfo.class, "packageName = ?", mEntity.packageName);
      if (mDownloadEntity != null && mType != MyGameDetailLayout.MY_GAME) {
        mDownloadEntity.deleteData();
      }
    }

    if (mCallback != null) {
      mCallback.onRemove(this);
    }
  }

  public void setChecked(boolean isChecked) {
    this.isChecked = isChecked;
    mCheckBox.setChecked(isChecked);
  }

  public MyGameDetailEntity getDetailEntity() {
    return mEntity;
  }

  public DownloadEntity getDownloadEntity() {
    return mDownloadEntity;
  }

  public boolean isChecked() {
    return isChecked;
  }

  public void showCheckBt(boolean isShow) {
    if (mEntity.isAddPackage) {
      return;
    }
    isShowCheckBox = isShow;
    if (!isShow) {
      isChecked = false;
      setChecked(isChecked);
    }
    mBg.setBackgroundResource(
        isShow ? R.drawable.bg_metro_item_focused : R.drawable.bg_metro_item_def);
    mCheckBox.setVisibility(isShow ? VISIBLE : GONE);
  }

  public void updateState(int state) {
    mEntity.downloadState = state;
    handleUI();
  }

  public void updateProgress(long currentProgress) {
    if (mDownloadEntity == null) {
      L.w("下载实体为 null");
      return;
    }
    mPause.setVisibility(GONE);
    mPb.setVisibility(VISIBLE);
    int Percentage = (int) (currentProgress * 100 / mDownloadEntity.getFileSize());
    mPb.setProgress(Percentage);
  }

  private synchronized void handleUI() {
    if (mType != MyGameDetailActivity.TYPE_UPDATE && mEntity.isAddPackage) {
      return;
    }
    mDownloadEntity = Aria.get(getContext()).getDownloadEntity(mEntity.downloadUrl);
    if (mDownloadEntity == null) return;
    mHasUpdate.setVisibility(mEntity.hasUpdate ? VISIBLE : GONE);
    if (mDownloadEntity.isDownloadComplete()) {
      mFloorImg.setVisibility(mType == MyGameDetailLayout.MY_GAME ? GONE : VISIBLE);
      mState.setVisibility(mType == MyGameDetailLayout.MY_GAME ? GONE : VISIBLE);
      if (!AndroidUtils.isInstall(getContext(), mEntity.packageName)) {
        mPause.setVisibility(GONE);
        mMaskBg.setVisibility(GONE);
        mPb.setVisibility(GONE);
        mFloorImg.setImageDrawable(new ColorDrawable(Color.parseColor("#22B1D7")));
        mState.setText("未安装");
      } else {
        mPause.setVisibility(GONE);
        mMaskBg.setVisibility(GONE);
        mPb.setVisibility(GONE);
        if (GameUpdateUtil.isUpdate(mEntity.packageName)) {
          mFloorImg.setImageDrawable(new ColorDrawable(Color.parseColor("#22B1D7")));
          //mState.setText("未安装");
          mState.setText("已更新");
        } else {
          mFloorImg.setImageDrawable(new ColorDrawable(Color.parseColor("#777AFF")));
          mState.setText("已安装");
        }
      }
    } else {
      mMaskBg.setVisibility(VISIBLE);
      switch (mEntity.downloadState) {
        case DownloadEntity.STATE_CANCEL:
        case DownloadEntity.STATE_FAIL:
        case DownloadEntity.STATE_OTHER:
        case DownloadEntity.STATE_WAIT:
        case DownloadEntity.STATE_STOP:
          mPause.setVisibility(VISIBLE);
          mPb.setVisibility(GONE);
          break;
        case DownloadEntity.STATE_PRE:
        case DownloadEntity.STATE_POST_PRE:
        case DownloadEntity.STATE_DOWNLOAD_ING:
          mPause.setVisibility(GONE);
          mPb.setVisibility(GONE);
          if (mDownloadEntity != null) {
            updateProgress(mDownloadEntity.getCurrentProgress());
          }
          break;
      }
    }
  }

  public void hasFocus(boolean isHasFocus) {
    this.isHasFocus = isHasFocus;
    if (isShowCheckBox) return;
    mBg.setBackgroundResource(
        isHasFocus ? R.drawable.bg_metro_item_focused : R.drawable.bg_metro_item_def);
  }

  private <T extends View> T getView(int id) {
    return (T) findViewById(id);
  }
}
