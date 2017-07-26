package com.utstar.appstoreapplication.activity.windows.activity.discount_package;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.Bind;
import com.arialyy.frame.util.StringUtil;
import com.ut.wb.ui.viewpager.SimpleViewPagerAdapter;
import com.utstar.appstoreapplication.activity.R;
import com.utstar.appstoreapplication.activity.entity.net_entity.HalfPackageEntity;
import com.utstar.appstoreapplication.activity.windows.base.BaseDialog;
import com.utstar.appstoreapplication.activity.windows.base.BaseFragment;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by JesseHev on 2017/3/31.
 * 套餐包优惠活动（半价优惠、破冰活动）
 */

@SuppressLint("ValidFragment") public class GamePackageDialog extends BaseDialog
    implements DialogInterface.OnDismissListener {
  @Bind(R.id.vp) ViewPager mVp;
  @Bind(R.id.left) ImageView mLeft;
  @Bind(R.id.right) ImageView mRight;
  @Bind(R.id.hint) TextView mHint;
  @Bind(R.id.hitn_bottom) TextView mHitnBottom;

  private SimpleViewPagerAdapter mAdapter;
  private List<HalfPackageEntity.GamePackage> mList = new ArrayList<>();
  private Map<Integer, GamePackageDialogFragment> mFragments = new HashMap<>();

  private OnPackDialogLister mOnPackDialogLister;

  //0 半价包 1 破冰行动
  private int mType = 0;

  public interface OnPackDialogLister {
    void onDismiss();
  }

  @Override public void onDismiss(DialogInterface dialog) {
    super.onDismiss(dialog);
    if (mOnPackDialogLister != null) {
      BaseFragment fragment = getCurrentFragment();
      if (fragment instanceof GamePackageDialogFragment) {
        //未体验套餐包时正常返回流程
        if (!((GamePackageDialogFragment) fragment).getSuccess()) {
          mOnPackDialogLister.onDismiss();
        }
      }
    }
  }

  public void setOnPackDialogLister(OnPackDialogLister lister) {
    mOnPackDialogLister = lister;
  }

  public GamePackageDialog() {

  }

  public GamePackageDialog(Context context, List<HalfPackageEntity.GamePackage> list) {
    super(context);
    mList = list;
  }

  public GamePackageDialog(Context context, List<HalfPackageEntity.GamePackage> list, int type) {
    super(context);
    mList = list;
    mType = type;
  }

  public BaseFragment getCurrentFragment() {
    return (BaseFragment) ((SimpleViewPagerAdapter) mVp.getAdapter()).getItem(mVp.getCurrentItem());
  }

  @Override public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setStyle(STYLE_NO_TITLE, R.style.blur_dialog);
  }

  @Override protected int setLayoutId() {
    return mType == 0 ? R.layout.dialog_half_game_package : R.layout.dialog_ice_break;
  }

  @Override protected void init(Bundle savedInstanceState) {
    super.init(savedInstanceState);
    initWindow();
    initVp();
    initData();
    if (mType == 1) {

      mHint.setText(StringUtil.BoldAndHighLightStr(mHint.getText().toString(), "0元体验",
          Color.parseColor("#ffea00")));

      mHitnBottom.setText(StringUtil.highLightStr(mHitnBottom.getText().toString(), "温馨提示：",
          Color.parseColor("#ffea00")));
    }
  }

  public void initVp() {
    mAdapter = new SimpleViewPagerAdapter(getChildFragmentManager());
    mVp.setAdapter(mAdapter);
    mVp.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
      @Override
      public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        //控制显示箭头
        if (mFragments.size() > 1) {
          if (position == 0) {
            showArrow(false, true);
          } else if (position == mFragments.size() - 1) {
            showArrow(true, false);
          } else {
            showArrow(true, true);
          }
        } else {
          showArrow(false, false);
        }
      }

      @Override public void onPageSelected(int position) {

      }

      @Override public void onPageScrollStateChanged(int state) {

      }
    });
  }

  /**
   * 显示箭头指示
   *
   * @param isLeftShow 是否显示左边箭头
   * @param isRightShow 是否显示右边箭头
   */
  public void showArrow(boolean isLeftShow, boolean isRightShow) {
    mLeft.setVisibility(isLeftShow ? View.VISIBLE : View.GONE);
    mRight.setVisibility(isRightShow ? View.VISIBLE : View.GONE);
  }

  public void initWindow() {
    Window window = getDialog().getWindow();
    if (window != null) {
      WindowManager.LayoutParams params = window.getAttributes();
      if (mType == 0) {
        params.width = getDimen(R.dimen.dimen_1264dp);
        params.height = getDimen(R.dimen.dimen_1038dp);
        //params.height = getDimen(R.dimen.dimen_806dp);
        params.dimAmount = 0.7f;
        window.setBackgroundDrawable(getContext().getResources().getDrawable(R.mipmap.bg_package));
        window.setAttributes(params);
        window.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
      } else {
        window.setBackgroundDrawable(getContext().getResources().getDrawable(R.mipmap.bg_ice_m));
        params.width = window.getWindowManager().getDefaultDisplay().getWidth();
        params.height = window.getWindowManager().getDefaultDisplay().getHeight();
      }
    }
  }

  public int getDimen(int dimenId) {
    return (int) getContext().getResources().getDimension(dimenId);
  }

  public int getPageSize(double dataSize) {
    int page = (int) Math.ceil(dataSize / 4);
    return page;
  }

  public void initData() {
    GamePackageDialogFragment fragment = null;
    if (mList != null && mList.size() > 0) {
      for (int i = 0, count = getPageSize(mList.size()); i < count; i++) {
        int start = i * 4;
        int end = i * 4 + 4;
        if (end >= mList.size()) end = mList.size();
        fragment =
            new GamePackageDialogFragment(GamePackageDialog.this, mList.subList(start, end), mType);
        mFragments.put(i, fragment);//保存fragment页面
        mAdapter.addFrag(fragment, "i = " + i);
      }
      mAdapter.notifyDataSetChanged();
    }
  }
}
