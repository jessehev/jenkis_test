package com.utstar.appstoreapplication.activity.windows.game_detail.uninstall_dialog;

import android.content.Context;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import butterknife.Bind;
import com.ut.wb.ui.MarqueTextView;
import com.ut.wb.ui.adapter.AbsHolder;
import com.ut.wb.ui.adapter.AbsRVAdapter;
import com.utstar.appstoreapplication.activity.R;
import com.utstar.appstoreapplication.activity.entity.net_entity.GameUninstallEntity;
import com.utstar.appstoreapplication.activity.manager.AnimManager;
import com.utstar.appstoreapplication.activity.manager.ImageManager;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by Aria.Lao on 2017/3/3.
 * 游戏卸载对话框适配器
 */
final class GameUninstallAdapter
    extends AbsRVAdapter<GameUninstallEntity, GameUninstallAdapter.GameUninstallHolder> {
  private Map<Integer, Boolean> mChecks = new HashMap<>();
  private int mRadius;

  GameUninstallAdapter(Context context, List<GameUninstallEntity> data) {
    super(context, data);
    for (int i = 0, len = data.size(); i < len; i++) {
      mChecks.put(i, true);
    }
    mRadius = (int) context.getResources().getDimension(R.dimen.dimen_8dp);
    //mRadius = 10;
  }

  @Override protected GameUninstallHolder getViewHolder(View convertView, int viewType) {
    return new GameUninstallHolder(convertView);
  }

  @Override protected int setLayoutId(int type) {
    return R.layout.item_game_uninstall;
  }

  void check(int position, boolean check) {
    mChecks.put(position, check);
    notifyItemChanged(position);
  }

  boolean isNoCheck() {
    Set<Integer> set = mChecks.keySet();
    int i = 0;
    for (Integer position : set) {
      if (mChecks.get(position)) i++;
    }
    return i != 0;
  }

  boolean getCheck(int position) {
    return mChecks.get(position);
  }

  Map<Integer, Boolean> getChecks() {
    return mChecks;
  }

  @Override
  protected void bindData(GameUninstallHolder holder, int position, GameUninstallEntity item) {
    ImageManager.getInstance().loadRoundedImgNoScaleType(holder.gameIcon, item.gameIcon, mRadius);
    holder.gameName.setText(item.gameName);
    holder.checkBox.setChecked(mChecks.get(position));
    holder.itemView.setOnFocusChangeListener((v, hasFocus) -> {
      ImageView bg = (ImageView) v.findViewById(R.id.bg);
      MarqueTextView gameName = (MarqueTextView) v.findViewById(R.id.game_name);
      if (hasFocus) {
        bg.setVisibility(View.VISIBLE);
        gameName.startMarquee();
        AnimManager.getInstance().enlarge(v, 1.2f);
      } else {
        bg.setVisibility(View.GONE);
        gameName.stopMarquee();
        AnimManager.getInstance().narrow(v, 1.2f);
      }
    });
  }

  static class GameUninstallHolder extends AbsHolder {
    @Bind(R.id.game_icon) ImageView gameIcon;
    @Bind(R.id.game_name) MarqueTextView gameName;
    @Bind(R.id.check) CheckBox checkBox;
    @Bind(R.id.bg) ImageView bg;

    GameUninstallHolder(View itemView) {
      super(itemView);
    }
  }
}
