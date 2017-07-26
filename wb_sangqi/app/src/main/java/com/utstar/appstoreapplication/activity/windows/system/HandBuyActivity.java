package com.utstar.appstoreapplication.activity.windows.system;

import android.os.Bundle;
import android.view.KeyEvent;
import com.arialyy.frame.util.show.T;
import com.utstar.appstoreapplication.activity.BuildConfig;
import com.utstar.appstoreapplication.activity.R;
import com.utstar.appstoreapplication.activity.databinding.ActivityHandBuyBinding;
import com.utstar.appstoreapplication.activity.entity.net_entity.HalfPackageEntity;
import com.utstar.appstoreapplication.activity.manager.ActivityManager;
import com.utstar.appstoreapplication.activity.manager.NetManager;
import com.utstar.appstoreapplication.activity.manager.TurnManager;
import com.utstar.appstoreapplication.activity.windows.activity.discount_package.GamePackageDialog;
import com.utstar.appstoreapplication.activity.windows.activity.discount_package.GamePackageMsgDialog;
import com.utstar.appstoreapplication.activity.windows.base.BaseActivity;
import com.utstar.appstoreapplication.activity.windows.common.IPChangeDialog;
import com.utstar.appstoreapplication.activity.windows.common.LogDialog;
import com.utstar.appstoreapplication.activity.windows.common.MsgDialog;
import java.util.ArrayList;
import java.util.List;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;

/**
 * 手柄购买
 */
public class HandBuyActivity extends BaseActivity<ActivityHandBuyBinding> {
  List<Integer> mPassWords = new ArrayList<>();
  List<Integer> mLogPw = new ArrayList<>();
  List<Integer> mTestFunPw = new ArrayList<>();

  @Override protected void init(Bundle savedInstanceState) {
    super.init(savedInstanceState);
  }

  @Override protected int setLayoutId() {
    return R.layout.activity_hand_buy;
  }

  @Override protected void dataCallback(int result, Object data) {

    super.dataCallback(result, data);
  }

  @Override public boolean onKeyDown(int keyCode, KeyEvent event) {
    if (event.getAction() == KeyEvent.ACTION_DOWN) {
      if (keyCode == KeyEvent.KEYCODE_VOLUME_UP
          || keyCode == KeyEvent.KEYCODE_VOLUME_DOWN
          || keyCode == KeyEvent.KEYCODE_1
          || keyCode == KeyEvent.KEYCODE_2) {
        mPassWords.add(keyCode);
        if (checkPassWords()) {
          changeServer();
        }
        return true;
      } else if (keyCode == KeyEvent.KEYCODE_ENTER || keyCode == KeyEvent.KEYCODE_DPAD_CENTER) {
        mLogPw.add(1);
        if (mLogPw.size() == 5) {
          showLog();
        }
        mTestFunPw.add(1);
        if (mTestFunPw.size() == 2) {
          if (BuildConfig.DEBUG) {
            androidTest();
          }
        }
        return true;
      }
    }
    return super.onKeyDown(keyCode, event);
  }

  /**
   * 测试方法
   */
  private void androidTest() {
    //showDialog("消费提示", "购买成功，快去体验吧！", true, "0", "");
    ActivityManager.getInstance().getIceBreakData(this);
    mTestFunPw.clear();
  }

  private void showDialog(String title, String msg, boolean success, String errorCode,
      String orderCode) {
    Observable.just("")
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(new Subscriber<String>() {
          @Override public void onCompleted() {
          }

          @Override public void onError(Throwable e) {
          }

          @Override public void onNext(String s) {
            MsgDialog dialog = new MsgDialog(HandBuyActivity.this, title, msg, false);
            dialog.setDialogCallback(new MsgDialog.OnMsgDialogCallback() {
              @Override public void onEnter() {
                finish();
              }

              @Override public void onCancel() {
              }
            });
            dialog.show();
          }
        });
  }
  /**
   * 显示日志
   * 5次确认
   */
  private void showLog() {
    LogDialog dialog = new LogDialog();
    dialog.show(getSupportFragmentManager(), "logDialog");
    mLogPw.clear();
  }

  /**
   * 密码 12121 或者 音量+-+-+
   */
  private boolean checkPassWords() {
    boolean result = false;
    if (mPassWords != null && mPassWords.size() == 5) {
      for (int i = 0; i < mPassWords.size(); i++) {
        Integer integer = mPassWords.get(i);
        if (i == 0 && (integer == KeyEvent.KEYCODE_VOLUME_UP || integer == KeyEvent.KEYCODE_1)) {
          result = true;
        } else if (i == 1 && (integer == KeyEvent.KEYCODE_VOLUME_DOWN
            || integer == KeyEvent.KEYCODE_2)) {
          result = true;
        } else if (i == 2 && (integer == KeyEvent.KEYCODE_VOLUME_UP
            || integer == KeyEvent.KEYCODE_1)) {
          result = true;
        } else if (i == 3 && (integer == KeyEvent.KEYCODE_VOLUME_DOWN
            || integer == KeyEvent.KEYCODE_2)) {
          result = true;
        } else if (i == 4 && (integer == KeyEvent.KEYCODE_VOLUME_UP
            || integer == KeyEvent.KEYCODE_1)) {
          result = true;
        } else {
          result = false;
        }
      }
    } else {
      result = false;
    }
    return result;
  }

  private void changeServer() {
    IPChangeDialog dialog = new IPChangeDialog();
    dialog.show(getSupportFragmentManager(), "ip_dialog");
    mPassWords.clear();
  }

  @Override protected void onPause() {
    super.onPause();
  }
}
