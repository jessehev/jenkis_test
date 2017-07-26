package com.utstar.appstoreapplication.activity.windows.game_video;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v17.leanback.widget.VerticalGridView;
import android.view.KeyEvent;
import android.view.View;
import butterknife.Bind;
import com.ut.wb.ui.TabLayout.TabLayout;
import com.ut.wb.ui.adapter.RvItemClickSupport;
import com.utstar.appstoreapplication.activity.R;
import com.utstar.appstoreapplication.activity.databinding.FragmentGameVideoListBinding;
import com.utstar.appstoreapplication.activity.entity.net_entity.GameVideoEntity;
import com.utstar.appstoreapplication.activity.manager.TurnManager;
import com.utstar.appstoreapplication.activity.windows.base.BaseFragment;
import com.utstar.appstoreapplication.activity.windows.main.MainActivity;
import java.util.ArrayList;
import java.util.List;

/**
 * 游戏视频列表
 */
@SuppressLint("ValidFragment") public class GameVideoListFragment
    extends BaseFragment<FragmentGameVideoListBinding> {
  @Bind(R.id.vgv_rank_detail) VerticalGridView mGrid;

  private int mCurrentPage;//当前页

  private GameVideoListAdapter mAdapter;

  private int currentPosition = 0; //当前下标

  private List<GameVideoEntity> mData = new ArrayList<>();

  public GameVideoListFragment() {
  }

  public GameVideoListFragment(int currentPage, List<GameVideoEntity> list) {
    mCurrentPage = currentPage;
    mData.clear();
    mData.addAll(list);
  }

  @Override protected void init(Bundle savedInstanceState) {
    //mAdapter = new GameVideoListAdapter(mData);
    //mGrid.setAdapter(mAdapter);
    //setListener();
  }

  @Override protected void onDelayLoad() {
    super.onDelayLoad();
    mAdapter = new GameVideoListAdapter(mData);
    mGrid.setAdapter(mAdapter);
    setListener();
  }

  public void requestFocus() {
    int count = mGrid.getLayoutManager().getChildCount();
    if (count > 0) {
      //View view = mGrid.getLayoutManager().getChildAt(currentPosition);
      View view = mGrid.getLayoutManager().getChildAt(0);
      if (view != null) {
        view.requestFocus();
      }
    }
  }

  public void setListener() {
    RvItemClickSupport.addTo(mGrid).setOnItemKeyListenr((v, keyCode, position, event) -> {
      if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_DPAD_UP) {
        if (position >= 0 && position < 4) {
          currentPosition = position;
          TabLayout tab = ((MainActivity) getActivity()).getTabLayout();
          tab.setSelected(3, false);
          tab.requestFocus();
          v.clearFocus();
          return true;
        }
      }
      return false;
    });
    RvItemClickSupport.addTo(mGrid).setOnItemClickListener((recyclerView, position, v) -> {
      GameVideoEntity gameVideoEntity = mData.get(position);
      TurnManager.getInstance().turnWebGameVideo(getContext(), gameVideoEntity.address);
    });
  }

  @Override protected int setLayoutId() {
    return R.layout.fragment_game_video_list;
  }

  @Override protected void dataCallback(int result, Object obj) {
    super.dataCallback(result, obj);
  }
}
