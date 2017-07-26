package com.utstar.appstoreapplication.activity.windows.game_detail;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import butterknife.Bind;
import com.ut.wb.ui.adapter.AbsHolder;
import com.ut.wb.ui.adapter.AbsRVAdapter;
import com.utstar.appstoreapplication.activity.R;
import com.utstar.appstoreapplication.activity.entity.net_entity.ImgUrlEntity;
import com.utstar.appstoreapplication.activity.manager.AnimManager;
import com.utstar.appstoreapplication.activity.manager.ImageManager;
import java.util.List;

/**
 * Created by Aria.Lao on 2016/12/23.
 * 游戏截图适配器
 */
final class GameShotAdapter extends AbsRVAdapter<ImgUrlEntity, GameShotAdapter.GameShotHolder> {
  private int mRadius;

  GameShotAdapter(Context context, List<ImgUrlEntity> data) {
    super(context, data);
    mRadius = (int) context.getResources().getDimension(R.dimen.dimen_20dp);
  }

  @Override protected GameShotHolder getViewHolder(View convertView, int viewType) {
    return new GameShotHolder(convertView);
  }

  @Override protected int setLayoutId(int type) {
    return R.layout.item_game_shot;
  }

  @Override protected void bindData(GameShotHolder holder, int position, ImgUrlEntity item) {
    ImageManager.getInstance()
        .setImg(holder.img, item.imgUrl, R.mipmap.error_bg_shot, R.mipmap.error_bg_shot, mRadius);
    holder.itemView.setOnFocusChangeListener((v, hasFocus) -> {
      if (hasFocus) {
        AnimManager.getInstance().enlarge(v, 1.16f);
      } else {
        AnimManager.getInstance().narrow(v, 1.16f);
      }
    });
    holder.videoImg.setVisibility(TextUtils.isEmpty(item.videoUrl) ? View.INVISIBLE : View.VISIBLE);
  }

  class GameShotHolder extends AbsHolder {
    @Bind(R.id.img) ImageView img;
    @Bind(R.id.video_img) ImageView videoImg;

    GameShotHolder(View itemView) {
      super(itemView);
    }
  }
}
