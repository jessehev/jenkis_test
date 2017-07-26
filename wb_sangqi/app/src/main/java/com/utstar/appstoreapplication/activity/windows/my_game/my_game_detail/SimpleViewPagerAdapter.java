package com.utstar.appstoreapplication.activity.windows.my_game.my_game_detail;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import android.support.v4.app.FragmentStatePagerAdapter;
import java.util.ArrayList;
import java.util.List;

/**
 * 简单的Viewpager适配器
 * Created by lyy on 2015/12/3.
 */
public class SimpleViewPagerAdapter extends FragmentStatePagerAdapter {
  private final List<Fragment> mFragmentList = new ArrayList<>();
  private final List<String> mFragmentTitleList = new ArrayList<>();
  private boolean isUpdate = false;

  public SimpleViewPagerAdapter(FragmentManager fm) {
    super(fm);
  }

  public void addFrag(Fragment fragment, String title) {
    mFragmentList.add(fragment);
    mFragmentTitleList.add(title);
  }

  public void update(List<Fragment> fragments) {
    if (fragments != null && fragments.size() > 0) {
      isUpdate = true;
      mFragmentList.clear();
      mFragmentList.addAll(fragments);
      notifyDataSetChanged();
    }
  }

  public void setUpdate(boolean update) {
    isUpdate = update;
  }

  public void removeAll() {
    isUpdate = true;
    mFragmentList.clear();
  }

  public void remove(Fragment fragment) {
    mFragmentList.remove(fragment);
  }

  @Override public CharSequence getPageTitle(int position) {
    return mFragmentTitleList.get(position);
  }

  @Override public Fragment getItem(int position) {
    return mFragmentList.get(position);
  }

  @Override public int getCount() {
    return mFragmentList.size();
  }

  @Override public int getItemPosition(Object object) {
    return isUpdate ? POSITION_NONE : super.getItemPosition(object);
    //return super.getItemPosition(object);
    //int index = mFragmentList.indexOf(object);
    //
    //if (index == -1) {
    //  return POSITION_NONE;
    //} else {
    //  return index;
    //}
  }
}