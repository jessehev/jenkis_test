package com.utstar.appstoreapplication.activity.windows.game_rank;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.Bind;
import com.ut.wb.ui.viewpager.SimpleViewPagerAdapter;
import com.utstar.appstoreapplication.activity.R;
import com.utstar.appstoreapplication.activity.commons.constants.CommonConstant;
import com.utstar.appstoreapplication.activity.databinding.ActivityGameDetailBinding;
import com.utstar.appstoreapplication.activity.entity.net_entity.GameRankMoreEntity;
import com.utstar.appstoreapplication.activity.utils.PageUtil;
import com.utstar.appstoreapplication.activity.windows.base.BaseActivity;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 排行更多列表Activity
 */
public class GameRankDetailActivity extends BaseActivity<ActivityGameDetailBinding> {

  @Bind(R.id.vp_rank_detail) ViewPager mVp;
  @Bind(R.id.rankName) TextView mTypeName;
  @Bind(R.id.left) ImageView mLeftArrow;
  @Bind(R.id.right) ImageView mRightArrow;
  //@Bind(R.id.num) TextView mGameRankNum;

  private SimpleViewPagerAdapter mAdapter;
  private Map<Integer, Fragment> mFragments = new HashMap<>();
  private int mCurrentPage;
  private static int TYPE_ID = 106;
  private static String TYPE_NAME = "最新游戏";
  private List<GameRankMoreEntity> mList;

  @Override protected int setLayoutId() {
    return R.layout.activity_game_rank_detail;
  }

  @Override protected void init(Bundle savedInstanceState) {
    super.init(savedInstanceState);
    //106  CommonConstant.C_iLineTab_Ranking
    //GameEntity gameEntity = new GameEntity(id, selectTypeId, 2, 10);
    getModule(GameRankModule.class).getRankGameList(
        new GameEntity(TYPE_ID, CommonConstant.C_iLineTab_Ranking, 1, 20));
    initVp();
  }

  @Override protected void dataCallback(int result, Object response) {
    super.dataCallback(result, response);
    if (result == GameRankDetailFragment.GAME_RANK_DETAIL_RESULT) {
      mList = (List<GameRankMoreEntity>) response;
      GameRankDetailFragment fragment = null;
      for (int i = 0, count = PageUtil.getPage(mList.size(), 10); i < count; i++) {
        int start = i * 10;
        int end = i * 10 + 10;
        if (end > mList.size()) end = mList.size();
        fragment = new GameRankDetailFragment(i,mList.subList(start, end));
        mFragments.put(i, fragment);
        mAdapter.addFrag(fragment, "i=" + i);
      }
      mAdapter.notifyDataSetChanged();
    }
  }
  public List<GameRankMoreEntity> getData() {
    return mList;
  }

  public void initVp() {
    mTypeName.setText(TYPE_NAME);
    mAdapter = new SimpleViewPagerAdapter(getSupportFragmentManager());
    mVp.setAdapter(mAdapter);
    mVp.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
      @Override
      public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
      }

      @Override public void onPageSelected(int position) {
        mCurrentPage = position;
        //控制显示箭头
        if (mFragments.size() > 1) {
          if (mCurrentPage == 0) {
            showArrow(false, true);
          } else if (mCurrentPage == mFragments.size() - 1) {
            showArrow(true, false);
          } else {
            showArrow(true, true);
          }
        } else {
          showArrow(false, false);
        }
      }

      @Override public void onPageScrollStateChanged(int state) {

      }
    });
  }

  @Override public boolean onKeyDown(int keyCode, KeyEvent event) {
    if (keyCode == KeyEvent.KEYCODE_PAGE_DOWN || keyCode == KeyEvent.KEYCODE_2) { //下一页
      handleDown();
    } else if (keyCode == KeyEvent.KEYCODE_PAGE_UP || keyCode == KeyEvent.KEYCODE_1) {//上一页
      handleUp();
    }
    return super.onKeyDown(keyCode, event);
  }

  private boolean handleDown() {
    if (mCurrentPage == mFragments.size() - 1) {
      return false;
    } else {
      mVp.setCurrentItem(mCurrentPage + 1, true);
      return true;
    }
  }

  private boolean handleUp() {
    if (mCurrentPage == 0) {
      return false;
    } else {
      mVp.setCurrentItem(mCurrentPage - 1, true);
      return true;
    }
  }

  /**
   * 显示箭头指示
   *
   * @param isLeftShow 是否显示左边箭头
   * @param isRightShow 是否显示右边箭头
   */
  public void showArrow(boolean isLeftShow, boolean isRightShow) {
    mLeftArrow.setVisibility(isLeftShow ? View.VISIBLE : View.GONE);
    mRightArrow.setVisibility(isRightShow ? View.VISIBLE : View.GONE);
  }
}
