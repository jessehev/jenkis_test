package com.utstar.appstoreapplication.activity.windows.recommend;

import android.os.Bundle;
import android.util.Log;
import butterknife.Bind;
import com.ut.wb.ui.metro.MetroItemEntity;
import com.ut.wb.ui.metro.MetroLayout;
import com.utstar.appstoreapplication.activity.R;
import com.utstar.appstoreapplication.activity.databinding.FragmentRecommendSecondBinding;
import com.utstar.appstoreapplication.activity.entity.net_entity.ModuleBgEntity;
import com.utstar.appstoreapplication.activity.manager.ImageManager;
import com.utstar.appstoreapplication.activity.manager.TurnManager;
import com.utstar.appstoreapplication.activity.module.CommonModule;
import com.utstar.appstoreapplication.activity.windows.base.BaseFragment;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by Aria.Lao on 2016/12/12.
 * 热门推荐第二页
 */
public class RecommendSecondFragment extends BaseFragment<FragmentRecommendSecondBinding> {

  @Bind(R.id.metro) MetroLayout mMetro;
  private ArrayList<MetroItemEntity> mData = new ArrayList<>();

  public static RecommendSecondFragment newInstance(ArrayList<MetroItemEntity> data) {
    Bundle args = new Bundle();
    RecommendSecondFragment fragment = new RecommendSecondFragment();
    args.putSerializable("data", data);
    fragment.setArguments(args);
    return fragment;
  }

  @Override protected void init(Bundle savedInstanceState) {
    //mMetro.setItem(getModule(RecommendModule.class).createSecondData());
    mData.clear();
    mData.addAll((Collection<? extends MetroItemEntity>) getArguments().getSerializable("data"));
    mMetro.setItem(mData);
    mMetro.setOnMetroClickListener(
        (view, position, entity) -> TurnManager.getInstance().turnMetro(getContext(), entity));
  }

  public void requestFocus() {
    if (mMetro != null) {
      mMetro.requestChildFocus(0);
    }
  }

  boolean isUpdate = false;

  @Override public void updateData() {
    super.updateData();
    isUpdate = true;
    getModule(RecommendModule.class).getRecommendData(2);
  }

  @Override protected void dataCallback(int result, Object obj) {
    super.dataCallback(result, obj);
    if (result == RecommendFragment.GET_RECOMMEND_DATA && obj != null) {
      mData.clear();
      mData.addAll((List<MetroItemEntity>) obj);
      if (isUpdate) {
        mMetro.update(mData);
      } else {
        mMetro.setItem(mData);
      }
    }
  }

  @Override protected void onDelayLoad() {
    super.onDelayLoad();
  }

  @Override protected int setLayoutId() {
    return R.layout.fragment_recommend_second;
  }
}
