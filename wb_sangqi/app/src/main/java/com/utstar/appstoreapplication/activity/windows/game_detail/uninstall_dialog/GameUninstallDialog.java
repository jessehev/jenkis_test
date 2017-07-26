package com.utstar.appstoreapplication.activity.windows.game_detail.uninstall_dialog;

import android.content.Context;
import android.support.v17.leanback.widget.VerticalGridView;
import android.view.KeyEvent;
import android.view.WindowManager;
import android.widget.Button;
import butterknife.Bind;
import butterknife.OnClick;
import com.arialyy.frame.core.AbsDialog;
import com.arialyy.frame.util.AndroidUtils;
import com.ut.wb.ui.adapter.RvItemClickSupport;
import com.utstar.appstoreapplication.activity.R;
import com.utstar.appstoreapplication.activity.entity.net_entity.GameUninstallEntity;
import com.utstar.appstoreapplication.activity.utils.ApkUtil;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by Aria.Lao on 2017/3/3.
 * 游戏卸载Dialog
 */
public class GameUninstallDialog extends AbsDialog {

  @Bind(R.id.list) VerticalGridView mList;
  @Bind(R.id.uninstall) Button mBt;
  private List<GameUninstallEntity> mData = new ArrayList<>();
  private GameUninstallAdapter mAdapter;

  public GameUninstallDialog(Context context, List<GameUninstallEntity> data) {
    super(context, R.style.blur_dialog);
    if (data == null || data.size() == 0) {
      dismiss();
      return;
    }
    mBt.requestFocus();
    mData.addAll(data);
    if (getWindow() != null) {
      WindowManager.LayoutParams lp = getWindow().getAttributes();
      lp.width = (int) (getWindow().getWindowManager().getDefaultDisplay().getWidth() - getDimen(
          R.dimen.dimen_600dp));
      lp.height = (int) (getWindow().getWindowManager().getDefaultDisplay().getHeight() - getDimen(
          R.dimen.dimen_120dp));

      getWindow().setBackgroundDrawable(
          getContext().getResources().getDrawable(R.drawable.bg_dialog_uninstall));
    }
    mAdapter = new GameUninstallAdapter(context, mData);
    mList.setAdapter(mAdapter);
    //mList.setScrollEnabled(true);
    mList.setVerticalScrollBarEnabled(true);
    RvItemClickSupport.addTo(mList).setOnItemClickListener((recyclerView, position, v) -> {
      mAdapter.check(position, !mAdapter.getCheck(position));
      //final boolean enable = !mAdapter.isNoCheck();
      //mBt.setEnabled(enable);
      //mBt.setFocusable(enable);
    });

    RvItemClickSupport.addTo(mList).setOnItemKeyListenr((v, keyCode, position, event) -> {
      if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
        int line = mData.size() / 5;
        if (line == 0 || line * 5 < mData.size()) line += 1;
        int cLine = position / 5;
        if (cLine == 0 || cLine * 5 < mData.size()) cLine += 1;

        if (cLine == line) {
          mBt.requestFocus();
        }
      }
      return false;
    });
  }

  private float getDimen(int dimenId) {
    return getContext().getResources().getDimension(dimenId);
  }

  @Override protected int setLayoutId() {
    return R.layout.dialog_uninstall;
  }

  @Override protected void dataCallback(int result, Object obj) {

  }

  @OnClick(R.id.uninstall) void uninstall() {
    if (mAdapter == null) return;
    Map<Integer, Boolean> checks = mAdapter.getChecks();
    Set<Integer> keys = checks.keySet();
    Set<Integer> checkedPosition = new HashSet<>();
    for (Integer position : keys) {
      if (checks.get(position)) {
        checkedPosition.add(position);
      }
    }

    if (checkedPosition.size() == 0) return;

    for (int index : checkedPosition) {
      if (index >= mData.size()) continue;
      ApkUtil.unInstallApk(getContext(), mData.get(index).packageName);
    }
    dismiss();
  }
}
