package com.utstar.appstoreapplication.activity.windows.my_game.my_game_detail;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import butterknife.Bind;
import com.arialyy.aria.core.task.Task;
import com.utstar.appstoreapplication.activity.R;
import com.utstar.appstoreapplication.activity.commons.widget.my_game_detail.MyGameDetailLayout;
import com.utstar.appstoreapplication.activity.databinding.FragmentMyGameDetailBinding;
import com.utstar.appstoreapplication.activity.entity.net_entity.MyGameDetailEntity;
import com.utstar.appstoreapplication.activity.windows.base.BaseFragment;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Aria.Lao on 2017/1/4.
 */
@SuppressLint("ValidFragment") public class MyGameDetailFragment
    extends BaseFragment<FragmentMyGameDetailBinding> {
  @Bind(R.id.my_game_detail) MyGameDetailLayout mGameDetailLayout;
  ArrayList<MyGameDetailEntity> mData = new ArrayList<>();
  int mType;

  public MyGameDetailFragment() {

  }

  public MyGameDetailFragment(int type, List<MyGameDetailEntity> list) {
    mType = type;
    mData.clear();
    mData.addAll(list);
  }

  public List<MyGameDetailEntity> getData() {
    return mData;
  }

  public void updateDate(List<MyGameDetailEntity> data) {
    mData.clear();
    mData.addAll(data);
    if (mGameDetailLayout == null) return;
    mGameDetailLayout.bindData(mType, mData);
  }

  public void removeItemByUrl(Task task) {
    if (mGameDetailLayout == null) return;
    mGameDetailLayout.removeItemByUrl(task.getDownloadEntity().getDownloadUrl());
  }

  public void updateProgress(Task task) {
    if (mGameDetailLayout == null) return;
    mGameDetailLayout.updateProgress(task.getDownloadEntity());
  }

  public void updateState(Task task) {
    if (mGameDetailLayout == null) return;
    mGameDetailLayout.updateState(task.getDownloadEntity());
  }

  public void removeItemByPackageName(String packageName) {
    if (mGameDetailLayout == null) return;
    mGameDetailLayout.removeItemByPackageName(packageName);
  }

  public void removeItemByPackageName(boolean isUnInstall, String packageName) {
    if (mGameDetailLayout == null) return;
    mGameDetailLayout.removeItemByPackageName(isUnInstall, packageName);
  }

  public void updateState(String downloadUrl, int state) {
    if (mGameDetailLayout == null) return;
    mGameDetailLayout.updateState(downloadUrl, state);
  }

  public int getDataSize() {
    return mData.size();
  }

  public int getCurrentItem() {
    return mGameDetailLayout == null ? 0 : mGameDetailLayout.getCurrentPosition();
  }

  public boolean isShowCheckBox() {
    return mGameDetailLayout != null && mGameDetailLayout.isShowCheckBox();
  }

  public void showCheckBox(boolean show) {
    if (mGameDetailLayout == null) return;
    mGameDetailLayout.showCheckBox(show);
  }

  public void handleRemove() {
    if (mGameDetailLayout == null) return;
    mGameDetailLayout.handleRemove();
  }

  @Override protected void init(Bundle savedInstanceState) {
    if (mGameDetailLayout == null) return;
    mGameDetailLayout.bindData(mType, mData);
    mGameDetailLayout.setOnItemFocusChange((view, hasFocus, position, entity) -> {
      if (isVisible()) {
        ((MyGameDetailActivity) getActivity()).setPositionText(position);
      }
    });
  }

  @Override protected int setLayoutId() {
    return R.layout.fragment_my_game_detail;
  }
}
