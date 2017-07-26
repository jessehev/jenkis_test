package com.utstar.appstoreapplication.activity.windows.game_rank;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v17.leanback.widget.VerticalGridView;
import android.view.KeyEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import butterknife.Bind;
import com.ut.wb.ui.adapter.RvItemClickSupport;
import com.utstar.appstoreapplication.activity.R;
import com.utstar.appstoreapplication.activity.databinding.FragmentGameRankBinding;
import com.utstar.appstoreapplication.activity.entity.net_entity.GameRankEntity;
import com.utstar.appstoreapplication.activity.windows.base.BaseFragment;
import java.util.List;

/**
 * Created by JesseHev on 2016/12/14.
 *
 * 游戏排行
 */
@SuppressLint("ValidFragment") public class GameRankFragment
    extends BaseFragment<FragmentGameRankBinding> {
  @Bind(R.id.vgv_pay) VerticalGridView mVgvPayList;
  @Bind(R.id.vgv_download) VerticalGridView mVgvDownloadList;
  @Bind(R.id.vgv_newproduct) VerticalGridView mVgvNewproductList;

  @Bind(R.id.fl_first) FrameLayout mLayoutFirst;
  @Bind(R.id.fl_sec) FrameLayout mLayoutSec;
  @Bind(R.id.fl_third) FrameLayout mLayoutThird;

  private GameRankAdapter mPayAdapter;
  private GameRankAdapter mDownAdapter;
  private GameRankAdapter mNewProductAdapter;

  @Bind(R.id.tv_pay) TextView mTvPay;
  @Bind(R.id.tv_download) TextView mTvDownload;
  @Bind(R.id.tv_newproduct) TextView mTvNewproduct;
  @Bind(R.id.time) TextView mTvTime;

  private GameRankModule mModule;

  private List<GameRankEntity> mGameRankEntity;
  //排行榜单
  static final int RANKFRAGMENT_RESULT = 0x001;

  //当前fragment在Viewpage中的位置
  private int mFragmentPage;
  //从数据集中取的起始位置
  private int mFirstPosition = 0 + mFragmentPage * 3;
  private int mSecondPosition = 1 + mFragmentPage * 3;
  private int mThirdPosition = 2 + mFragmentPage * 3;

  private List<GameRankEntity.GameRankSubEntity> mDataList1;
  private List<GameRankEntity.GameRankSubEntity> mDataList2;
  private List<GameRankEntity.GameRankSubEntity> mDataList3;

  @Override protected void init(Bundle savedInstanceState) {

    // initList();
    setOnkeyListenner();
    //  mModule = getModule(GameRankModule.class);
    //mModule.setTime(mTvTime);
    //mModule.getRankData();
    if (mGameRankEntity != null && mGameRankEntity.size() > 0) {
      initList();
      updateData();
    }
  }

  public void initList() {
    //  mFragmentPage = getArguments().getInt("page");
    mPayAdapter = new GameRankAdapter(getContext(), mRootView, mVgvPayList, mDataList1);
    mDownAdapter = new GameRankAdapter(getContext(), mRootView, mVgvDownloadList, mDataList2);
    mNewProductAdapter =
        new GameRankAdapter(getContext(), mRootView, mVgvNewproductList, mDataList3);

    mVgvPayList.setAdapter(mPayAdapter);
    mVgvDownloadList.setAdapter(mDownAdapter);
    mVgvNewproductList.setAdapter(mNewProductAdapter);
    mVgvPayList.setSelectedPosition(0);
  }

  public void setOnkeyListenner() {
    RvItemClickSupport.addTo(mVgvPayList).setOnItemKeyListenr((v, keyCode, position, event) -> {
      if (position == 2
          && keyCode == KeyEvent.KEYCODE_DPAD_RIGHT
          && event.getAction() == KeyEvent.ACTION_DOWN) {
        return requeFocu(mVgvDownloadList, 0);
        //return true;
      } else if (position == 5
          && keyCode == KeyEvent.KEYCODE_DPAD_RIGHT
          && event.getAction() == KeyEvent.ACTION_DOWN) {
        return requeFocu(mVgvDownloadList, 3);
        //return true;
      } else if (position == 8
          && keyCode == KeyEvent.KEYCODE_DPAD_RIGHT
          && event.getAction() == KeyEvent.ACTION_DOWN) {
        return requeFocu(mVgvDownloadList, 6);
        //return true;
      }
      return false;
    });
    RvItemClickSupport.addTo(mVgvDownloadList)
        .setOnItemKeyListenr((v, keyCode, position, event) -> {
          if (position == 2
              && keyCode == KeyEvent.KEYCODE_DPAD_RIGHT
              && event.getAction() == KeyEvent.ACTION_DOWN) {
            return requeFocu(mVgvNewproductList, 0);
            //return true;
          } else if (position == 5
              && keyCode == KeyEvent.KEYCODE_DPAD_RIGHT
              && event.getAction() == KeyEvent.ACTION_DOWN) {
            return requeFocu(mVgvNewproductList, 3);
            //return true;
          } else if (position == 8
              && keyCode == KeyEvent.KEYCODE_DPAD_RIGHT
              && event.getAction() == KeyEvent.ACTION_DOWN) {
            return requeFocu(mVgvNewproductList, 6);
            //return true;
          } else if (position == 0
              && keyCode == KeyEvent.KEYCODE_DPAD_LEFT
              && event.getAction() == KeyEvent.ACTION_DOWN) {
            return requeFocu(mVgvPayList, 2);
            //return true;
          } else if (position == 3
              && keyCode == KeyEvent.KEYCODE_DPAD_LEFT
              && event.getAction() == KeyEvent.ACTION_DOWN) {
            return requeFocu(mVgvPayList, 5);
            //return true;
          } else if (position == 6
              && keyCode == KeyEvent.KEYCODE_DPAD_LEFT
              && event.getAction() == KeyEvent.ACTION_DOWN) {
            return requeFocu(mVgvPayList, 8);
            //return true;
          }
          return false;
        });
    RvItemClickSupport.addTo(mVgvNewproductList)
        .setOnItemKeyListenr((v, keyCode, position, event) -> {
          if (position == 0
              && keyCode == KeyEvent.KEYCODE_DPAD_LEFT
              && event.getAction() == KeyEvent.ACTION_DOWN) {
            return requeFocu(mVgvDownloadList, 2);
            //return true;
          } else if (position == 3
              && keyCode == KeyEvent.KEYCODE_DPAD_LEFT
              && event.getAction() == KeyEvent.ACTION_DOWN) {
            return requeFocu(mVgvDownloadList, 5);
            //return true;
          } else if (position == 6
              && keyCode == KeyEvent.KEYCODE_DPAD_LEFT
              && event.getAction() == KeyEvent.ACTION_DOWN) {
            return requeFocu(mVgvDownloadList, 8);
            //return true;
          } else if (position == 2 || position == 5 || position == 8) {
            if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT
                && event.getAction() == KeyEvent.ACTION_DOWN) {
            }
          }

          return false;
        });
  }

  /**
   * 获取焦点
   */
  private boolean requeFocu(VerticalGridView grid, int index) {
    if (grid.getLayoutManager().getItemCount() > index) {
      grid.getLayoutManager().getChildAt(index).requestFocus();
      return true;
    } else {
      return false;
    }
  }

  public void updateData() {
    if (mGameRankEntity.size() > mFirstPosition) {
      mTvPay.setText(mGameRankEntity.get(mFirstPosition).typeName);
      mDataList1 = mGameRankEntity.get(mFirstPosition).mGameRankSubEntity;
      if (mDataList1 != null) {
        mPayAdapter.upData(mDataList1, mGameRankEntity.get(mFirstPosition).typeId,
            mGameRankEntity.get(mFirstPosition).typeName);
      } else {
        mLayoutFirst.setVisibility(View.INVISIBLE);
      }
    }
    if (mGameRankEntity.size() > mSecondPosition) {
      mTvDownload.setText(mGameRankEntity.get(mSecondPosition).typeName);
      mDataList2 = mGameRankEntity.get(mSecondPosition).mGameRankSubEntity;

      if (mDataList2 != null) {
        mDownAdapter.upData(mDataList2, mGameRankEntity.get(mSecondPosition).typeId,
            mGameRankEntity.get(mSecondPosition).typeName);
      }
    }
    if (mGameRankEntity.size() > mThirdPosition) {
      mTvNewproduct.setText(mGameRankEntity.get(mThirdPosition).typeName);
      mDataList3 = mGameRankEntity.get(mThirdPosition).mGameRankSubEntity;
      if (mDataList3 != null) {
        mNewProductAdapter.upData(mDataList3, mGameRankEntity.get(mThirdPosition).typeId,
            mGameRankEntity.get(mThirdPosition).typeName);
      }
    }
  }

  //public GameRankFragment() {
  //}

  public GameRankFragment(int page, List<GameRankEntity> gameRankEntity) {
    mFragmentPage = page;
    mGameRankEntity = gameRankEntity;
  }

  //public static GameRankFragment newInstance(int page) {
  //  GameRankFragment fragment = new GameRankFragment();
  //  Bundle arg = new Bundle();
  //  arg.putInt("page", page);
  //  fragment.setArguments(arg);
  //  return fragment;
  //}

  @Override protected int setLayoutId() {
    return R.layout.fragment_game_rank;
  }

  //@Override protected void dataCallback(int result, Object obj) {
  //  //排行榜单
  //  if (result == RANKFRAGMENT_RESULT) {
  //    mGameRankEntity = (List<GameRankEntity>) obj;
  //    if (mGameRankEntity != null && mGameRankEntity.size() > 0) {
  //      initList();
  //      updateData();
  //    }
  //  }
  //  super.dataCallback(result, obj);
  //}
}
