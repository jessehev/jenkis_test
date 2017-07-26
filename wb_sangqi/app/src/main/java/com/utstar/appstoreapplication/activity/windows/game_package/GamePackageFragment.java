package com.utstar.appstoreapplication.activity.windows.game_package;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.KeyEvent;
import butterknife.Bind;
import com.ut.wb.ui.metro.MetroItemEntity;
import com.ut.wb.ui.metro.MetroLayout;
import com.utstar.appstoreapplication.activity.R;
import com.utstar.appstoreapplication.activity.databinding.FragmentGamePackageBinding;
import com.utstar.appstoreapplication.activity.windows.base.BaseFragment;
import com.utstar.appstoreapplication.activity.windows.game_hall.MetroFragment;
import java.util.List;

/**
 * Created by Aria.Lao on 2016/12/15.
 * 请使用 {@link MetroFragment}
 */
@Deprecated @SuppressLint("ValidFragment") final class GamePackageFragment
    extends BaseFragment<FragmentGamePackageBinding> {
  static final int GET_PACKAGE_DATA = 0xac2;
  @Bind(R.id.metro) MetroLayout mMetro;
  private int mPage = 1;

  private static GamePackageFragment newInstance() {
    Bundle args = new Bundle();
    GamePackageFragment fragment = new GamePackageFragment();
    fragment.setArguments(args);
    return fragment;
  }

  @Override protected void init(Bundle savedInstanceState) {
    //mMetro.setItem(getModule(GamePackageModule.class).createGamePackageData());
  }

  public boolean onKeyDown(int keyCode, KeyEvent event) {

    return false;
  }

  public MetroLayout getMetro() {
    return mMetro;
  }

  @Override protected int setLayoutId() {
    return R.layout.fragment_game_package;
  }

  @Override protected void onDelayLoad() {
    super.onDelayLoad();
    //getModule(GamePackageModule.class).getPackageData(mPage);
  }

  @Override protected void dataCallback(int result, Object obj) {
    super.dataCallback(result, obj);
    if (result == GET_PACKAGE_DATA) {
      mMetro.setItem((List<MetroItemEntity>) obj);
    }
  }
}
