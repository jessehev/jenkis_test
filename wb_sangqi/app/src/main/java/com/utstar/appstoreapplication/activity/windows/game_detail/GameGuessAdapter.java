package com.utstar.appstoreapplication.activity.windows.game_detail;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import butterknife.Bind;
import com.ut.wb.ui.MarqueTextView;
import com.ut.wb.ui.adapter.AbsHolder;
import com.ut.wb.ui.adapter.AbsRVAdapter;
import com.utstar.appstoreapplication.activity.R;
import com.utstar.appstoreapplication.activity.entity.net_entity.GuessEntity;
import com.utstar.appstoreapplication.activity.manager.AnimManager;
import com.utstar.appstoreapplication.activity.manager.ImageManager;
import java.util.List;

/**
 * Created by Aria.Lao on 2016/12/23.
 * 游戏详情猜你喜欢
 */
final class GameGuessAdapter extends AbsRVAdapter<GuessEntity, GameGuessAdapter.GameGuessHolder> {
  private int mRadius;

  public GameGuessAdapter(Context context, List<GuessEntity> data) {
    super(context, data);
    mRadius = (int) context.getResources().getDimension(R.dimen.dimen_16dp);
  }

  @Override protected GameGuessHolder getViewHolder(View convertView, int viewType) {
    return new GameGuessHolder(convertView);
  }

  @Override protected int setLayoutId(int type) {
    return R.layout.item_game_guess;
  }

  @Override protected void bindData(GameGuessHolder holder, int position, GuessEntity item) {
    ImageManager.getInstance().loadRoundedImg(holder.img, item.imgUrl, mRadius);
    holder.name.setText(item.name);
    holder.itemView.setOnFocusChangeListener((v, hasFocus) -> {
      if (hasFocus) {
        AnimManager.getInstance().enlarge(holder.img, 1.2f);
        holder.name.startMarquee();
      } else {
        AnimManager.getInstance().narrow(holder.img, 1.2f);
        holder.name.stopMarquee();
      }
    });
  }

  class GameGuessHolder extends AbsHolder {
    @Bind(R.id.img) ImageView img;
    @Bind(R.id.name) MarqueTextView name;

    GameGuessHolder(View itemView) {
      super(itemView);
    }
  }
}
