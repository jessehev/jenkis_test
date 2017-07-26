package com.utstar.appstoreapplication.activity.windows.recommend;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.KeyEvent;

import com.ut.wb.ui.metro.MetroItemEntity;
import com.ut.wb.ui.viewpager.SimpleViewPagerAdapter;
import com.utstar.appstoreapplication.activity.R;
import com.utstar.appstoreapplication.activity.databinding.FragmentRecommendBinding;
import com.utstar.appstoreapplication.activity.windows.base.BaseFragment;

import java.util.ArrayList;

import butterknife.Bind;

/**
 * Created by Aria.Lao on 2016/12/12.
 * 首页推荐
 */
public class RecommendFragment extends BaseFragment<FragmentRecommendBinding> {
  static final int GET_RECOMMEND_DATA = 0xac2;
  @Bind(R.id.vp) ViewPager mVp;
  private SimpleViewPagerAdapter mAdapter;

  public static RecommendFragment newInstance() {
    Bundle args = new Bundle();
    RecommendFragment fragment = new RecommendFragment();
    fragment.setArguments(args);
    return fragment;
  }

  public void requestFocus() {
    BaseFragment currentFm = getCurrentFragment();
    if (currentFm == null) {
      return;
    }
    if (currentFm instanceof RecommendMainFragment) {
      ((RecommendMainFragment) currentFm).requestFocus();
    } else if (currentFm instanceof RecommendSecondFragment) {
      ((RecommendSecondFragment) currentFm).requestFocus();
    }
  }

  /**
   * 获取推荐第一页
   */
  public RecommendMainFragment getRecommendMainFragment() {
    SimpleViewPagerAdapter adapter = (SimpleViewPagerAdapter) mVp.getAdapter();
    return (RecommendMainFragment) adapter.getItem(0);
  }

  public boolean isLeftView() {
    BaseFragment currentFm = getCurrentFragment();
    return currentFm != null
        && currentFm instanceof RecommendMainFragment
        && ((RecommendMainFragment) currentFm).isLeftView();
  }

  public boolean handleLeftViewKey(int keyCode, KeyEvent event) {
    BaseFragment currentFm = getCurrentFragment();
    return currentFm != null
        && currentFm instanceof RecommendMainFragment
        && ((RecommendMainFragment) currentFm).handleLeftViewKey(keyCode, event);
  }

  public BaseFragment getCurrentFragment() {
    if (mAdapter == null || mVp == null) {
      return null;
    }
    Fragment fragment = mAdapter.getItem(mVp.getCurrentItem());
    return fragment == null ? null : (BaseFragment) fragment;
  }



  @Override protected void init(Bundle savedInstanceState) {
    mRootView.setBackgroundColor(getArguments().getInt("color"));
    getModule(RecommendModule.class).getRecommendData(2);
    //1.配置背景图    热门推荐第一页  type-1
    setBgParam(1, 1);
    changeBackground();
  }

  @Override public void updateData() {
    super.updateData();
    BaseFragment currentFragment = getCurrentFragment();
    if (currentFragment != null) {
      currentFragment.updateData();
    }
  }

  @Override protected int setLayoutId() {
    return R.layout.fragment_recommend;
  }

  private void initVp(ArrayList<MetroItemEntity> secondData) {
    mAdapter = new SimpleViewPagerAdapter(getChildFragmentManager());
    RecommendMainFragment mainFragment;
    RecommendSecondFragment secondFragment = null;
    if (secondData != null && secondData.size() > 0) {
      secondFragment = RecommendSecondFragment.newInstance(secondData);
    }
    mainFragment = RecommendMainFragment.newInstance(secondFragment != null);
    mAdapter.addFrag(mainFragment, "main");
    if (secondFragment != null) mAdapter.addFrag(secondFragment, "second");
    mVp.setAdapter(mAdapter);
    mVp.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
      @Override
      public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

      }

      @Override public void onPageSelected(int position) {
      }

      @Override public void onPageScrollStateChanged(int state) {

      }
    });
  }

  @Override protected void dataCallback(int result, Object obj) {
    super.dataCallback(result, obj);
    if (result == RecommendFragment.GET_RECOMMEND_DATA) {
      initVp((ArrayList<MetroItemEntity>) obj);
    }
  }
}
