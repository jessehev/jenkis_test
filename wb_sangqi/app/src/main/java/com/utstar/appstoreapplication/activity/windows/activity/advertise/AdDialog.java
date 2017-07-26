package com.utstar.appstoreapplication.activity.windows.activity.advertise;

import android.content.Context;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.Bind;
import com.arialyy.frame.core.AbsDialog;
import com.utstar.appstoreapplication.activity.R;
import com.utstar.appstoreapplication.activity.entity.net_entity.AdEntity;
import com.utstar.appstoreapplication.activity.manager.ImageManager;

/**
 * Created by JesseHev on 2017/1/9.
 *
 * 广告对话框
 */

public class AdDialog extends AbsDialog implements View.OnClickListener {

  @Bind(R.id.img) ImageView mImg;
  @Bind(R.id.bt) Button mBt;
  @Bind(R.id.second) TextView mSecond;

  private int mCurrentPosition = 0;
  private AdEntity mAdEntity;
  private CountDownTimer mTimer;

  private Handler mCountHandler = new Handler() {
    @Override public void handleMessage(Message msg) {
      super.handleMessage(msg);
      mSecond.setText(msg.obj + "S");
    }
  };

  public AdDialog(Context context, AdEntity adEntity) {
    super(context, R.style.blur_dialog);
    this.mAdEntity = adEntity;
    init();
    initWindow();
  }

  
  @Override protected int setLayoutId() {
    return R.layout.dialog_ad;
  }

  @Override protected void dataCallback(int result, Object obj) {

  }

  private void initWindow() {
    if (getWindow() != null) {
      WindowManager.LayoutParams params = getWindow().getAttributes();
      params.width =
          (int) (getWindow().getWindowManager().getDefaultDisplay().getWidth() - getDimen(
              R.dimen.dimen_320dp));
      params.height =
          (int) (getWindow().getWindowManager().getDefaultDisplay().getHeight() - getDimen(
              R.dimen.dimen_160dp));
      getWindow().setBackgroundDrawable(
          getContext().getResources().getDrawable(R.drawable.shape_ad_dialog_bg));
    }
  }

  public void init() {
    if (mAdEntity == null) {
      return;
    }
    //mAdEntity = adEntity;
    mBt.setOnClickListener(this);
    mBt.setText(mAdEntity.img.size() <= 1 ? "关闭" : "下一张");

    loadImg();
    startTimer();
  }
  //@Override protected void init(Bundle savedInstanceState) {
  //  super.init(savedInstanceState);
  //
  //  initWindow();
  //
  //  if (mAdEntity == null) {
  //    return;
  //  }
  //  //mAdEntity = adEntity;
  //  mBt.setOnClickListener(this);
  //  mBt.setText(mAdEntity.img.size() <= 1 ? "关闭" : "下一张");
  //
  //  loadImg();
  //  startTimer();
  //
  //  if (mOnDismissListenner != null) {
  //    getDialog().setOnDismissListener(dialog -> mOnDismissListenner.onDismiss(getDialog()));
  //  }
  //}

  //private void init(AdEntity adEntity) {
  //  if (adEntity == null) {
  //    return;
  //  }
  //  mAdEntity = adEntity;
  //  mBt.setOnClickListener(this);
  //  mBt.setText(mAdEntity.img.size() <= 1 ? "关闭" : "下一张");
  //
  //  loadImg();
  //  startTimer();
  //}

  private float getDimen(int dimenId) {
    return getContext().getResources().getDimension(dimenId);
  }

  @Override public void onClick(View v) {
    if (mAdEntity == null || mAdEntity.img == null) {
      return;
    }
    if (mCurrentPosition == mAdEntity.img.size()) {
      dismiss();
    } else {
      loadImg();
      startTimer();
      mBt.setText(mCurrentPosition == mAdEntity.img.size() ? "关闭" : "下一张");
    }
  }

  private void loadImg() {
    ImageManager.getInstance().clear(mImg);
    ImageManager.getInstance().setImg(mImg, mAdEntity.img.get(mCurrentPosition));
    mCurrentPosition++;
  }

  private void startTimer() {
    if (mTimer != null) {
      mTimer.cancel();
      mTimer = null;
    }
    mSecond.setText("5S");
    mTimer = new CountDownTimer(6 * 1000, 1000) {
      int second = 5;

      @Override public void onTick(long millisUntilFinished) {
        mCountHandler.obtainMessage(1, second--).sendToTarget();
      }

      @Override public void onFinish() {
        dismiss();
      }
    };
    mTimer.start();
  }

  @Override public void dismiss() {
    super.dismiss();
    if (mTimer != null) {
      mTimer.cancel();
      mTimer = null;
    }
  }
}
