package com.utstar.appstoreapplication.activity.windows.recommend;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import butterknife.Bind;
import butterknife.OnClick;
import butterknife.OnFocusChange;
import com.ut.wb.ui.metro.MetroItemEntity;
import com.ut.wb.ui.metro.MetroLayout;
import com.utstar.appstoreapplication.activity.R;
import com.utstar.appstoreapplication.activity.databinding.FragmentRecommendMainBinding;
import com.utstar.appstoreapplication.activity.manager.AnimManager;
import com.utstar.appstoreapplication.activity.manager.TurnManager;
import com.utstar.appstoreapplication.activity.windows.activity.new_activity.NewEventActivity;
import com.utstar.appstoreapplication.activity.windows.base.BaseFragment;
import com.utstar.appstoreapplication.activity.windows.game_rank.GameRankActivity;
import com.utstar.appstoreapplication.activity.windows.game_rank.GameRankDetailActivity;
import com.utstar.appstoreapplication.activity.windows.main.MainActivity;
import com.utstar.appstoreapplication.activity.windows.my_game.MyGameActivity;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Aria.Lao on 2016/12/12. 热门推荐第一页
 */
public class RecommendMainFragment extends BaseFragment<FragmentRecommendMainBinding> {

  @Bind(R.id.metro) MetroLayout mMetro;
  @Bind(R.id.game_rank) ImageView mRankImg;
  @Bind(R.id.game_my) ImageView mGameMy;
  @Bind(R.id.new_activity) ImageView mNewActivity;
  @Bind(R.id.arrow_right) ImageView mRightImg;
  private boolean showRightArrowImg = false;
  String mBackgroundUrl;
  private ArrayList<MetroItemEntity> mData = new ArrayList<>();

  public static RecommendMainFragment newInstance(boolean showRightArrowImg) {
    Bundle args = new Bundle();
    RecommendMainFragment fragment = new RecommendMainFragment();
    args.putBoolean("show", showRightArrowImg);
    fragment.setArguments(args);
    return fragment;
  }

  @Override protected void init(Bundle savedInstanceState) {
    //mMetro.setItem(getModule(RecommendModule.class).createMainData());
    getModule(RecommendModule.class).getRecommendData(1);
    mMetro.setOnMetroClickListener(
        (view, position, entity) -> TurnManager.getInstance().turnMetro(getContext(), entity));
    showRightArrowImg(getArguments().getBoolean("show"));
    setBgParam(1, 1);
  }

  public void requestFocus() {
    if (mRankImg != null) {
      mRankImg.requestFocus();
    }
  }

  private void showRightArrowImg(boolean show) {
    if (mRightImg != null) {
      mRightImg.setVisibility(show ? View.VISIBLE : View.GONE);
    }
  }

  @OnFocusChange({ R.id.game_rank, R.id.game_my, R.id.new_activity })
  public void iconFocusChange(View view, boolean focused) {
    if (focused) {
      AnimManager.getInstance().enlarge(view, 1.45f);
      getModule(RecommendModule.class).preventCover(view);
    } else {
      AnimManager.getInstance().narrow(view, 1.0f);
    }
  }

  public boolean isLeftView() {
    return mRankImg.hasFocus() || mGameMy.hasFocus() || mNewActivity.hasFocus();
  }

  @Override protected int setLayoutId() {
    return R.layout.fragment_recommend_main;
  }

  @OnClick({ R.id.game_rank, R.id.game_my, R.id.new_activity }) public void onClick(View view) {
    switch (view.getId()) {
      case R.id.game_rank:
        //getContext().startActivity(new Intent(getContext(), GameRankActivity.class));
        getContext().startActivity(new Intent(getContext(), GameRankDetailActivity.class));
        break;
      case R.id.game_my:
        getContext().startActivity(new Intent(getContext(), MyGameActivity.class));
        break;
      case R.id.new_activity:
        //getContext().startActivity(new Intent(getContext(), NewEventActivity.class));
        if (getContext() instanceof Activity) {
          ((Activity) getContext()).startActivityForResult(new Intent(getContext(), NewEventActivity.class), TurnManager.DRAW_REQUEST_CODE);
        } else {
          getContext().startActivity(new Intent(getContext(), NewEventActivity.class));
        }
        break;
    }
  }

  boolean isUpdate = false;

  @Override public void updateData() {
    super.updateData();
    isUpdate = true;
    getModule(RecommendModule.class).getRecommendData(1);
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

  public boolean handleLeftViewKey(int keyCode, KeyEvent event) {
    View v;
    if (mGameMy.hasFocus()) {
      v = mGameMy;
    } else if (mRankImg.hasFocus()) {
      v = mRankImg;
    } else {
      v = mNewActivity;
    }
    if (event.getAction() == KeyEvent.ACTION_DOWN) {
      switch (v.getId()) {
        case R.id.game_rank:
          if (keyCode == KeyEvent.KEYCODE_PAGE_UP) {
            ((MainActivity) getActivity()).setTabSelected(0, false);
          } else if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
            ((MainActivity) getActivity()).setTabSelected(4, false);
            return true;
          }
          break;
        case R.id.game_my:
          if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
            ((MainActivity) getActivity()).setTabSelected(4, false);
            return true;
          }
          break;
        case R.id.new_activity:
          if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
            ((MainActivity) getActivity()).setTabSelected(4, false);
            return true;
          }
          break;
      }
    }
    return false;
  }
}
