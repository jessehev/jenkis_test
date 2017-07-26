package com.utstar.appstoreapplication.activity.windows.game_rank;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v17.leanback.widget.VerticalGridView;
import butterknife.Bind;
import com.ut.wb.ui.adapter.RvItemClickSupport;
import com.utstar.appstoreapplication.activity.R;
import com.utstar.appstoreapplication.activity.databinding.FragmentGameDetailRankBinding;
import com.utstar.appstoreapplication.activity.entity.net_entity.GameRankMoreEntity;
import com.utstar.appstoreapplication.activity.manager.TurnManager;
import com.utstar.appstoreapplication.activity.windows.base.BaseFragment;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by JesseHev on 2016/12/14.
 * 排行更多列表fragment
 */
@SuppressLint("ValidFragment") public class GameRankDetailFragment
    extends BaseFragment<FragmentGameDetailRankBinding> {

  @Bind(R.id.vgv_rank_detail) VerticalGridView mGrid;
  private GameRankDetailAdapter mAdapter;
  static final int GAME_RANK_DETAIL_RESULT = 0x191;
  private List<GameRankMoreEntity> mData = new ArrayList<>();

  private int currentPage;

  public GameRankDetailFragment() {
  }

  public GameRankDetailFragment(int page, List<GameRankMoreEntity> list) {
    this.currentPage = page;
    mData.clear();
    mData.addAll(list);
  }

  @Override protected void init(Bundle savedInstanceState) {
  }

  @Override protected void onDelayLoad() {
    super.onDelayLoad();
    initList();
  }

  public void initList() {
    mAdapter = new GameRankDetailAdapter(getContext(), mRootView, mData,currentPage);
    mGrid.setAdapter(mAdapter);
    RvItemClickSupport.addTo(mGrid).setOnItemClickListener((recyclerView, position, v) -> {
      if (mData != null) {
        GameRankMoreEntity data = mData.get(position);
        TurnManager.getInstance().turnGameDetail(getActivity(), data.productId);
      }
    });
  }

  @Override protected int setLayoutId() {
    return R.layout.fragment_game_detail_rank;
  }

  @Override protected void dataCallback(int result, Object obj) {
    super.dataCallback(result, obj);
  }
}
