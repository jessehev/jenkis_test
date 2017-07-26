package com.utstar.appstoreapplication.activity.windows.game_search;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v17.leanback.widget.VerticalGridView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import butterknife.Bind;
import com.arialyy.frame.util.show.L;
import com.arialyy.frame.util.show.T;
import com.ut.wb.ui.TabLayout.TabLayout;
import com.ut.wb.ui.adapter.RvItemClickSupport;
import com.utstar.appstoreapplication.activity.R;
import com.utstar.appstoreapplication.activity.databinding.FragmentSearchBinding;
import com.utstar.appstoreapplication.activity.entity.net_entity.SearchEntity;
import com.utstar.appstoreapplication.activity.manager.TurnManager;
import com.utstar.appstoreapplication.activity.windows.base.BaseFragment;
import com.utstar.appstoreapplication.activity.windows.main.MainActivity;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Aria.Lao on 2016/12/15.
 * 搜索
 */
public class SearchFragment extends BaseFragment<FragmentSearchBinding> {
  /**
   * 猜你喜欢
   */
  static final int RECOMMEND_RESULT = 0xac1;
  /**
   * 搜索
   */
  static final int SEARCH_RESULT = 0xac2;

  @Bind(R.id.search_et) EditText mSearchEd;
  @Bind(R.id.search_key) VerticalGridView mSearchKeyGrid;
  @Bind(R.id.result_title) TextView mResultTitle;
  @Bind(R.id.search_result) VerticalGridView mContentGrid;
  private List<SearchEntity.SearchResultEntity> mData;
  private SearchContentAdapter mContendAdapter;
  private int mPage = 1;

  public static SearchFragment newInstance() {
    Bundle args = new Bundle();
    SearchFragment fragment = new SearchFragment();
    fragment.setArguments(args);
    return fragment;
  }

  @Override protected void init(Bundle savedInstanceState) {
    Drawable rightIcon = getContext().getResources().getDrawable(R.mipmap.icon_search_et);
    if (rightIcon != null) {
      int size = (int) getContext().getResources().getDimension(R.dimen.dimen_40dp);
      rightIcon.setBounds(0, 0, size, size);
      mSearchEd.setCompoundDrawables(null, null, rightIcon, null);
    }
    mData = new ArrayList<>();
    mContendAdapter = new SearchContentAdapter(getContext(), mData);
    mContentGrid.setAdapter(mContendAdapter);
    final List<SearchKeyEntity> keyData = getModule(SearchModule.class).createKeyData();
    final SearchKeyAdapter keyAdapter = new SearchKeyAdapter(getContext(), keyData);
    mSearchKeyGrid.setAdapter(keyAdapter);
    handleKeyEvent(keyData);
    handleContent();
    setBgParam(5, 1);
  }

  public void requestFocus() {
    mSearchKeyGrid.getChildAt(0).requestFocus();
  }

  /**
   * 处理键盘事件
   */
  private void handleKeyEvent(List<SearchKeyEntity> keyData) {
    RvItemClickSupport.addTo(mSearchKeyGrid).setOnItemClickListener((recyclerView, position, v) -> {
      SearchKeyEntity entity = keyData.get(position);
      String str = mSearchEd.getText().toString().trim();
      switch (entity.num) {
        case 0:
          str += 0;
          search(str);
          break;
        case 1:
          str += 1;
          search(str);
          break;
        case 2:
        case 3:
        case 4:
        case 5:
        case 6:
        case 7:
        case 8:
        case 9:
          showKeyPop(v, entity);
          break;
        case 10:  //删除
          if (str.length() >= 1) {
            str = str.substring(0, str.length() - 1);
          }
          if (TextUtils.isEmpty(str)) {
            str = "";
            mSearchEd.setText(str);
            getModule(SearchModule.class).getBack();
          } else {
            search(str);
          }
          break;
        case 11:  //清空
          str = "";
          mSearchEd.setText(str);
          getModule(SearchModule.class).getBack();
          break;
      }
      mSearchEd.setText(str);
    });

    RvItemClickSupport.addTo(mSearchKeyGrid).setOnItemKeyListenr((v, keyCode, position, event) -> {
      if (event.getAction() == KeyEvent.ACTION_DOWN) {
        if (position <= 2
            && getActivity() instanceof MainActivity
            && keyCode == KeyEvent.KEYCODE_DPAD_UP) {
          TabLayout tab = ((MainActivity) getActivity()).getTabLayout();
          tab.setSelected(3, false);
          tab.requestFocus();
          v.clearFocus();
        }
        if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT && (position + 1) % 3 == 0) {
          if (mContentGrid.getChildCount() > 1) {
            mContentGrid.setScrollY(0);
            RecyclerView.LayoutManager lm = mContentGrid.getLayoutManager();
            if (lm instanceof GridLayoutManager) {
              GridLayoutManager llm = (GridLayoutManager) lm;
              llm.scrollToPositionWithOffset(0, 0);
            }
            mContentGrid.getLayoutManager().getChildAt(0).requestFocus();
            return true;
          }
        }
        if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT && position % 3 == 0) {  //到搜索结果的最后一个
          int count = mContentGrid.getChildCount();
          if (count > 1) {
            mContentGrid.getLayoutManager().getChildAt(count > 6 ? 5 : count - 1).requestFocus();
            return true;
          }
        }
      }
      return false;
    });
  }

  /**
   * 处理搜索内容事件
   */
  private void handleContent() {
    RvItemClickSupport.addTo(mContentGrid).setOnItemKeyListenr((v, keyCode, position, event) -> {
      if (event.getAction() == KeyEvent.ACTION_DOWN) {
        if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
          int count = mContentGrid.getAdapter().getItemCount();
          if (count <= 6 && position == count - 1) {
            mSearchKeyGrid.getLayoutManager().getChildAt(0).requestFocus();
            return true;
          }

          if ((position + 1) % 6 == 0) {
            mSearchKeyGrid.getLayoutManager().getChildAt(0).requestFocus();
            return true;
          } else if (position == count - 1) {
            v.clearFocus();
            mSearchKeyGrid.getLayoutManager().getChildAt(0).requestFocus();
            return true;
          }
          return false;
        } else if (position <= 5
            && getActivity() instanceof MainActivity
            && keyCode == KeyEvent.KEYCODE_DPAD_UP) {
          TabLayout tab = ((MainActivity) getActivity()).getTabLayout();
          tab.setSelected(3, false);
          tab.requestFocus();
          v.clearFocus();
        }
      }
      return false;
    });
    RvItemClickSupport.addTo(mContentGrid).setOnItemClickListener((recyclerView, position, v) -> {
      if (mData == null || mData.size() <= position) return;
      SearchEntity.SearchResultEntity entity = mData.get(position);
      //if (entity.isAddPackage) {
      //  TurnManager.getInstance().turnPackage(getContext(), entity.packageId,entity.productId);
      //} else {
      //  TurnManager.getInstance().turnGameDetail(getContext(), Integer.parseInt(entity.productId));
      //}
      TurnManager.getInstance().turnGameDetail(getContext(), Integer.parseInt(entity.productId));
    });
  }

  /**
   * 显示按键悬浮框
   */
  private void showKeyPop(View item, SearchKeyEntity entity) {
    int[] location = new int[2];
    item.getLocationInWindow(location);
    int size = (int) getResources().getDimension(R.dimen.dimen_150dp);
    int x = location[0] - (size - item.getMeasuredWidth()) / 2;
    int y = location[1] - (size - item.getMeasuredHeight()) / 2;
    KeyPopupWindow pop = new KeyPopupWindow(item, getContext(), this, entity);
    pop.showAtLocation(mRootView, Gravity.NO_GRAVITY, x, y);
  }

  /**
   * 搜索
   */
  private void search(String key) {
    if (TextUtils.isEmpty(key)) {
      L.w("搜索关键字不能为 null");
      return;
    }
    getModule(SearchModule.class).search(key, mPage);
  }

  /**
   * 设置搜索数据
   */
  private void setGridData(int result, List<SearchEntity.SearchResultEntity> data) {
    if (data != null && data.size() > 0) {
      mData.clear();
      mData.addAll(data);
      mContendAdapter.update(mSearchEd.getText().toString());
    } else {
      T.showShort(getContext(), "没有搜索结果");
      mData.clear();
      mContendAdapter.notifyDataSetChanged();
    }
  }

  @Override protected void onDelayLoad() {
    super.onDelayLoad();
    getModule(SearchModule.class).getGameRecommendGame();
  }

  @Override protected int setLayoutId() {
    return R.layout.fragment_search;
  }

  @Override protected void dataCallback(int result, Object obj) {
    super.dataCallback(result, obj);
    if (result == KeyPopupWindow.KEY_POPUP_WINDOW_RESULT) { //popupWindow 返回的数据
      if (obj != null) {
        String str = mSearchEd.getText().toString().trim() + obj;
        mSearchEd.setText(str);
        search(str);
      }
    } else if (result == RECOMMEND_RESULT) {  //猜你喜欢
      mResultTitle.setText("猜你喜欢：");
      setGridData(result, (List<SearchEntity.SearchResultEntity>) obj);
    } else if (result == SEARCH_RESULT) {   //搜索结果
      mResultTitle.setText("搜索结果：");
      setGridData(result, (List<SearchEntity.SearchResultEntity>) obj);
    }
  }
}
