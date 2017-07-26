package com.utstar.appstoreapplication.activity.windows.game_rank;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import butterknife.Bind;
import com.ut.wb.ui.viewpager.SimpleViewPagerAdapter;
import com.utstar.appstoreapplication.activity.R;
import com.utstar.appstoreapplication.activity.databinding.ActivityGameRankBinding;
import com.utstar.appstoreapplication.activity.entity.net_entity.GameRankEntity;
import com.utstar.appstoreapplication.activity.windows.base.BaseActivity;
import java.util.ArrayList;
import java.util.List;

public class GameRankActivity extends BaseActivity<ActivityGameRankBinding> {

  @Bind(R.id.vp_rank) ViewPager mVp;
  private SimpleViewPagerAdapter mPagerAdapter;
  private List<Fragment> mFragmentList = new ArrayList<>();

  @Override protected int setLayoutId() {
    return R.layout.activity_game_rank;
  }

  @Override protected void init(Bundle savedInstanceState) {
    super.init(savedInstanceState);
    initVp();
    getModule(GameRankModule.class).getRankData();
  }

  @Override protected void dataCallback(int result, Object data) {
    if (GameRankFragment.RANKFRAGMENT_RESULT == result) {
      List<GameRankEntity> gameRankEntity = (List<GameRankEntity>) data;
      if (gameRankEntity != null) {
        int num = (int) Math.ceil((double) gameRankEntity.size() / 3);
        for (int i = 0; i < num; i++) {
          //mFragmentList.add(GameRankFragment.newInstance(i));
          mFragmentList.add(new GameRankFragment(i, gameRankEntity));
        }
        mPagerAdapter.update(mFragmentList);
      }
    }
    super.dataCallback(result, data);
  }

  public void initVp() {
    mPagerAdapter = new SimpleViewPagerAdapter(getSupportFragmentManager());
    // mPagerAdapter.addFrag(GameRankFragment.newInstance(), "1");
    //mPagerAdapter.addFrag(GameRankFragment.newInstance(), "2");
    mVp.setAdapter(mPagerAdapter);
    mVp.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
      @Override
      public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        //  L.d("onPageScrolled position===" + position);
      }

      @Override public void onPageSelected(int position) {
        //   L.d("onPageSelected position===" + position);
      }

      @Override public void onPageScrollStateChanged(int state) {
        //   L.d("onPageScrollStateChanged state===" + state);
      }
    });
  }
}
