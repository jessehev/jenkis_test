package com.utstar.appstoreapplication.activity.windows.game_hall;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.support.v17.leanback.widget.HorizontalGridView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import butterknife.Bind;
import com.arialyy.frame.util.show.L;
import com.ut.wb.ui.metro.MetroItemEntity;
import com.ut.wb.ui.metro.MetroLayout;
import com.utstar.appstoreapplication.activity.BuildConfig;
import com.utstar.appstoreapplication.activity.R;
import com.utstar.appstoreapplication.activity.databinding.FragmentGameHallBinding;
import com.utstar.appstoreapplication.activity.entity.net_entity.ModuleBgEntity;
import com.utstar.appstoreapplication.activity.manager.ImageManager;
import com.utstar.appstoreapplication.activity.module.CommonModule;
import com.utstar.appstoreapplication.activity.windows.base.BaseFragment;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Aria.Lao on 2016/12/15.
 * 游戏大厅
 */
@SuppressLint("ValidFragment") public class MetroFragment
    extends BaseFragment<FragmentGameHallBinding> {
  static final int GET_METRO_DATA = 0xaa1;
  public static final int GAME_HALL = 0xab1;
  public static final int GAME_PACKAGE = 0xab2;
  @Bind(R.id.list) HorizontalGridView mGrid;
  @Bind(R.id.arrow_left) ImageView mArrowLeft;
  @Bind(R.id.arrow_right) ImageView mArrowRight;
  private int mPage = 1;
  private List<List<MetroItemEntity>> mData = new ArrayList<>();
  private MetroAdapter mAdapter;
  private int mCurrentPosition = 0;
  private static final int MAX_PAGE = 4;
  private int mType;
  private boolean hasMorePage = false;
  private boolean isUpdate = false;
  private int mPageNo = 1;
  public static MetroFragment newInstance(int type) {
    Bundle args = new Bundle();
    args.putInt("type", type);
    MetroFragment fragment = new MetroFragment();
    fragment.setArguments(args);
    return fragment;
  }

  @Override protected int setLayoutId() {
    return R.layout.fragment_game_hall;
  }

  @Override protected void init(Bundle savedInstanceState) {
    mType = getArguments().getInt("type", GAME_HALL);
    mAdapter = new MetroAdapter(getContext(), mData);
    mGrid.setAdapter(mAdapter);
    mGrid.addOnScrollListener(new RecyclerView.OnScrollListener() {
      int dx = 0;

      @Override public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
        super.onScrollStateChanged(recyclerView, newState);
        //L.d(TAG, "state = " + newState);
        if (newState == RecyclerView.SCROLL_STATE_IDLE) {
          handleScroll(dx);
        }
      }

      @Override public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);
        //L.d(TAG, "dx = " + dx + ",dy = " + dy);
        this.dx = dx;
      }
    });
    loadBackground();
  }

  private void loadBackground() {
    if (mType == MetroFragment.GAME_PACKAGE) {
      //2.配置背景图    精品专区  type-2
      //getModule(CommonModule.class).getEachModuleBackground(2, 1);
      setBgParam(2, mPageNo);
    } else if (mType == MetroFragment.GAME_HALL) {
      //3.配置背景图    游戏大厅  type-3
      //getModule(CommonModule.class).getEachModuleBackground(3, 1);
      setBgParam(3, mPageNo);
    }
  }

  /**
   * 处理滚动
   */
  private void handleScroll(int dx) {
    int count = mGrid.getAdapter().getItemCount();
    if (dx < 0) {
      mCurrentPosition--;
      if (mCurrentPosition < 0) mCurrentPosition = 0;
      //loadPageBackground(mCurrentPosition+1);
    } else {
      mCurrentPosition++;
      if (mCurrentPosition >= count) mCurrentPosition = count - 1;
      //loadPageBackground( mCurrentPosition+1);
    }
    loadMore(count);
  }

  /*
   *分页配置背景图
   */
  private void loadPageBackground(int pageNo) {
    if (mType == MetroFragment.GAME_PACKAGE) {
      //2.配置背景图    精品专区  type-2
      getModule(CommonModule.class).getEachModuleBackground(2, pageNo);
    } else if (mType == MetroFragment.GAME_HALL) {
      //3.配置背景图    游戏大厅  type-3
      getModule(CommonModule.class).getEachModuleBackground(3, pageNo);
    }
  }

  /**
   * 处理翻页
   */
  private void handleFlip(boolean isDown) {
    int count = mGrid.getAdapter().getItemCount();
    if (!isDown) {
      mCurrentPosition--;
      if (mCurrentPosition < 0) mCurrentPosition = 0;
    } else {
      mCurrentPosition++;
      if (mCurrentPosition >= count) mCurrentPosition = count - 1;
    }
    loadMore(count);
  }

  /**
   * 加载更多
   */
  private void loadMore(int count) {

    //加载下一页
    isUpdate = false;
    if (mCurrentPosition == count - 1) {
      if (mPage > 1) {
        mArrowLeft.setVisibility(View.VISIBLE);
      }
      if (mPage >= MAX_PAGE) {
        mArrowRight.setVisibility(View.GONE);
        return;
      }
      mPage++;
      getModule(MetroModule.class).getMetroData(mType, mPage);
    } else if (mCurrentPosition == 0) {
      mArrowLeft.setVisibility(View.GONE);
      mArrowRight.setVisibility(hasMorePage ? View.VISIBLE : View.GONE);
    } else {
      mArrowLeft.setVisibility(View.VISIBLE);
      mArrowRight.setVisibility(View.VISIBLE);
    }
  }

  public MetroLayout getMetro() {
    //L.d(TAG, "mCurrentPosition ==> " + mCurrentPosition);
    if (mGrid == null) return null;
    View view = mGrid.getLayoutManager().findViewByPosition(mCurrentPosition);
    if (view != null) {
      return (MetroLayout) view.findViewById(R.id.metro);
    }
    return null;
  }

  public boolean onKeyDown(int keyCode, KeyEvent event) {
    if (mGrid == null || mGrid.getAdapter() == null) return false;
    int count = mGrid.getAdapter().getItemCount();
    //只有一页不做处理
    if (count == 1) {
      return false;
    }
    if (BuildConfig.DEBUG) {
      if (keyCode == KeyEvent.KEYCODE_PAGE_DOWN || keyCode == KeyEvent.KEYCODE_2) {//下一页
        if (mCurrentPosition < count) {
          mGrid.scrollToPosition(mCurrentPosition + 1);
          handleFlip(true);
          return true;
        }
      } else if (keyCode == KeyEvent.KEYCODE_PAGE_UP || keyCode == KeyEvent.KEYCODE_1) {//上一页
        if (mCurrentPosition >= 1) {
          mGrid.scrollToPosition(mCurrentPosition - 1);
          handleFlip(false);
          return true;
        }
      }
    } else {
      if (keyCode == KeyEvent.KEYCODE_PAGE_DOWN) {
        mGrid.scrollToPosition(mCurrentPosition + 1);
        handleFlip(true);
      } else if (keyCode == KeyEvent.KEYCODE_PAGE_UP) {
        mGrid.scrollToPosition(mCurrentPosition - 1);
        handleFlip(false);
      }
    }
    return false;
  }

  @Override protected void onDelayLoad() {
    super.onDelayLoad();
    getModule(MetroModule.class).getMetroData(mType, mPage);
    mPage++;
    new Handler().postDelayed(() -> {
      getModule(MetroModule.class).getMetroData(mType, mPage);
    }, 1000);
  }

  @Override public void updateData() {
    super.updateData();
    isUpdate = true;
    getModule(MetroModule.class).getMetroData(mType, mCurrentPosition + 1);
  }

  /**
   * 加载数据
   */
  private void setData(List<MetroItemEntity> itemData) {
    if (itemData != null && itemData.size() > 0) {
      if (isUpdate) {
        mData.set(mCurrentPosition, itemData);
        //mAdapter.notifyItemChanged(mCurrentPosition);
        mAdapter.update(mCurrentPosition);
        return;
      }
      mData.add(itemData);
      mAdapter.notifyItemInserted(mPage - 1);
      if (mPage > 1) {
        switch (mType) {
          case GAME_HALL:
            mArrowRight.setVisibility(itemData.size() >= 1 ? View.VISIBLE : View.GONE);
            break;
          case GAME_PACKAGE:
            mArrowRight.setVisibility(itemData.size() > 0 ? View.VISIBLE : View.GONE);
            break;
        }
      }
      hasMorePage = true;
    } else {
      mPage--;
      mArrowRight.setVisibility(View.GONE);
    }
  }

  @Override protected void dataCallback(int result, Object obj) {
    super.dataCallback(result, obj);
    if (result == GET_METRO_DATA) {
      setData((List<MetroItemEntity>) obj);
    } else if (result == CommonModule.MODULEBG_SUCCESS) {
      ImageManager.getInstance().loadBackground(getActivity(), (ModuleBgEntity) obj);
    }
  }
}
