package com.utstar.appstoreapplication.activity.windows.activity.draw_award;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.support.v17.leanback.widget.VerticalGridView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.Bind;
import com.arialyy.frame.core.AbsDialog;
import com.utstar.appstoreapplication.activity.R;
import com.utstar.appstoreapplication.activity.entity.net_entity.AwardListEntity;
import com.utstar.appstoreapplication.activity.entity.net_entity.DrawAwardIconEntity;
import com.utstar.appstoreapplication.activity.manager.ImageManager;
import java.util.List;

/**
 * Created by JesseHev on 2017/1/11.
 * 奖品列表
 */

public class AwardListDialog extends AbsDialog {

  @Bind(R.id.dialog_award_grid) VerticalGridView mGrid;

  private DialogAwardAdapater mAdapater;

  private List<AwardListEntity> mData;
  private DrawAwardIconEntity mIconEntity;

  Handler mHandler = new Handler() {
    @Override public void handleMessage(Message msg) {
      super.handleMessage(msg);
      if (msg.what == 1) {
        setWindowBackground((Bitmap) msg.obj);
        //   initWindow((Bitmap) msg.obj);
        init();
      }
    }
  };

  public AwardListDialog(Context context, List<AwardListEntity> list,
      DrawAwardIconEntity iconEntity) {
    super(context, R.style.blur_dialog);

    mData = list;
    mIconEntity = iconEntity;
    downLoadImg();
    initWindow();
    //init();
  }

  @Override protected int setLayoutId() {
    return R.layout.dialog_award_list;
  }

  @Override protected void dataCallback(int result, Object obj) {

  }

  /**
   * 下载背景图
   */
  public void downLoadImg() {
    new Thread(() -> {
      Bitmap bitmap = ImageManager.getInstance().downLoadImg(mIconEntity.imageType9);
      mHandler.obtainMessage(1, bitmap).sendToTarget();
    }).start();
  }

  public void init() {
    mAdapater = new DialogAwardAdapater();
    mGrid.setAdapter(mAdapater);
  }

  public void initWindow() {
    if (getWindow() != null) {
      WindowManager.LayoutParams params = getWindow().getAttributes();
      params.width =
          (int) (getWindow().getWindowManager().getDefaultDisplay().getWidth() - getDimen(
              R.dimen.dimen_840dp));
      params.height =
          (int) (getWindow().getWindowManager().getDefaultDisplay().getHeight() - getDimen(
              R.dimen.dimen_460dp));

      //getWindow().setBackgroundDrawable(
      //    getContext().getResources().getDrawable(R.mipmap.bg_zhongjiang_2));

    }
  }

  /**
   * 设置窗口背景
   */
  public void setWindowBackground(Bitmap bitmap) {
    Drawable drawable = new BitmapDrawable(bitmap);
    getWindow().setBackgroundDrawable(drawable);
  }

  private float getDimen(int dimenId) {
    return getContext().getResources().getDimension(dimenId);
  }

  class DialogAwardAdapater extends RecyclerView.Adapter<DialogAwardAdapater.DialogAwardHolder> {

    @Override public DialogAwardHolder onCreateViewHolder(ViewGroup parent, int viewType) {

      View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_egg_award, null);
      DialogAwardHolder holder = new DialogAwardHolder(v);
      return holder;
    }

    @Override public void onBindViewHolder(DialogAwardHolder holder, int position) {
      AwardListEntity entity = mData.get(position);

      holder.name.setText(entity.name);
      holder.rank.setText(entity.rank);

      ImageManager.getInstance().loadImg(holder.bg, entity.imgUrl);
    }

    @Override public int getItemCount() {
      if (mData.size() > 6) {
        return 6;
      }
      return mData.size();
    }

    public class DialogAwardHolder extends RecyclerView.ViewHolder {

      TextView name;
      TextView rank;
      ImageView bg;

      public DialogAwardHolder(View view) {
        super(view);
        name = (TextView) view.findViewById(R.id.tv_award_name);
        rank = (TextView) view.findViewById(R.id.tv_award_rank);
        bg = (ImageView) view.findViewById(R.id.iv_award_bg);
      }
    }
  }
}
