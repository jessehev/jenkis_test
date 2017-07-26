package com.utstar.appstoreapplication.activity.windows.activity.draw_award;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.text.TextUtils;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.Bind;
import com.arialyy.frame.core.AbsDialog;
import com.utstar.appstoreapplication.activity.R;
import com.utstar.appstoreapplication.activity.entity.net_entity.DrawAwardEntity;
import com.utstar.appstoreapplication.activity.entity.net_entity.DrawAwardIconEntity;
import com.utstar.appstoreapplication.activity.manager.ImageManager;

/**
 * Created by JesseHev on 2017/1/12.
 * 中奖信息对话框
 */

public class AwardInfoDialog extends AbsDialog {

  @Bind(R.id.im_bg) ImageView mImBg;
  @Bind(R.id.tv_dialog_info_award_rank) TextView mAwardRank;
  @Bind(R.id.tv_award_name) TextView mAwardName;
  @Bind(R.id.tv_content) TextView mContent;

  private DrawAwardEntity mDrawAwardEntity;
  private DrawAwardIconEntity mIconEntity;

  Handler mHandler = new Handler() {
    @Override public void handleMessage(Message msg) {
      super.handleMessage(msg);
      if (msg.what == 1) {
        setWindowBackground((Bitmap) msg.obj);
        init();
      }
    }
  };

  public AwardInfoDialog(Context context, DrawAwardEntity drawAwardEntity,
      DrawAwardIconEntity iconEntity) {
    super(context, R.style.blur_dialog);
    this.mDrawAwardEntity = drawAwardEntity;
    this.mIconEntity = iconEntity;
    downLoadImg();
    initWindow();
    //init();
  }

  @Override protected int setLayoutId() {
    return R.layout.dialog_award_info;
  }

  @Override protected void dataCallback(int result, Object obj) {

  }

  /**
   * 下载背景图
   */
  public void downLoadImg() {
    new Thread(() -> {
      Bitmap bitmap = ImageManager.getInstance().downLoadImg(mIconEntity.imageType11);
      mHandler.obtainMessage(1, bitmap).sendToTarget();
    }).start();
  }

  private void init() {

    handlerString(mDrawAwardEntity.winningInfo);

    ImageManager.getInstance().loadImg(mImBg, mDrawAwardEntity.img);

    mAwardRank.setText(mDrawAwardEntity.typeName);

    mAwardName.setText(mDrawAwardEntity.award);
  }

  /**
   * 格式化字符串字体大小
   */
  public void handlerString(String content) {
    if (!TextUtils.isEmpty(content)) {
      mContent.setText(Html.fromHtml(content));
    }
  }

  private void initWindow() {
    if (getWindow() != null) {
      WindowManager.LayoutParams params = getWindow().getAttributes();
      params.width = getDimen(R.dimen.dimen_740dp);
      params.height = getDimen(R.dimen.dimen_528dp);

      //Drawable drawable = new BitmapDrawable(bitmap);
      //getWindow().setBackgroundDrawable(drawable);
      //getWindow().setBackgroundDrawable(
      //    getContext().getResources().getDrawable(R.mipmap.bg_zhongjiang_3));
    }
  }

  /**
   * 设置窗口背景
   */
  public void setWindowBackground(Bitmap bitmap) {
    Drawable drawable = new BitmapDrawable(bitmap);
    getWindow().setBackgroundDrawable(drawable);
  }

  public int getDimen(int dimenId) {
    return (int) getContext().getResources().getDimension(dimenId);
  }
}
