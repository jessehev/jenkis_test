package com.utstar.appstoreapplication.activity.windows.game_hall.game_classify;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v17.leanback.widget.HorizontalGridView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import butterknife.Bind;
import com.arialyy.frame.util.show.L;
import com.arialyy.frame.util.show.T;
import com.utstar.appstoreapplication.activity.R;
import com.utstar.appstoreapplication.activity.commons.widget.WBNavView;
import com.utstar.appstoreapplication.activity.databinding.ActivityGameClassifyBinding;
import com.utstar.appstoreapplication.activity.entity.net_entity.GameClassifyEntity;
import com.utstar.appstoreapplication.activity.entity.net_entity.GameSizeEntity;
import com.utstar.appstoreapplication.activity.entity.net_entity.ModuleBgEntity;
import com.utstar.appstoreapplication.activity.manager.ImageManager;
import com.utstar.appstoreapplication.activity.manager.TurnManager;
import com.utstar.appstoreapplication.activity.module.CommonModule;
import com.utstar.appstoreapplication.activity.utils.IsUpdateIconUtil;
import com.utstar.appstoreapplication.activity.windows.base.BaseActivity;
import com.utstar.appstoreapplication.activity.windows.common.LoadingDialog;
import java.util.ArrayList;
import java.util.List;

/**
 * 游戏大厅分类
 */
public class GameClassifyActivity extends BaseActivity<ActivityGameClassifyBinding> {

  public static final String TAG = "GameClassifyActivity";
  /**
   * 手柄分类游戏
   */
  public static final int TYPE_MODE_SB = 1;
  /**
   * 遥控器分类游戏
   */
  public static final int TYPE_MODE_YKQ = 2;

  /**
   * 遥控器、手柄 传值键值
   */
  public static final String TYPE_MODE_KEY = "type_mode_key";
  /**
   * 其他分类 传值键值
   */
  public static final String TYPE_TAG_KEY = "type_tag_key";

  /**
   * 游戏分类名字 传值键值
   */
  public static final String TYPR_NAME = "type_classify_NAME";

  @Bind(R.id.wb_nav) WBNavView mWbNav;
  @Bind(R.id.hvg_list) HorizontalGridView mList;
  @Bind(R.id.tv_first) TextView mNameFirst;
  @Bind(R.id.tv_second) TextView mNameSecond;
  @Bind(R.id.num) TextView mNum;
  @Bind(R.id.left) ImageView mIvLeft;
  @Bind(R.id.right) ImageView mIvRight;

  private GameClassifyAdapter mAdapter;
  //分类成功返回码  mode
  static final int MODE_RESULT = 0X002;
  //tag
  static final int TAG_RESULT = 0X003;
  static final int TAG_MODE_RESULT = 0X004;
  static final int TAG_MODE_SIZE_RESULT = 0X005;
  static final int TAG_TYPE_SIZE_RESULT = 0X006;
  //错误回调
  static final int REQUEST_ERROR_RESULT = 0x007;
  private List<GameClassifyEntity> mGameClassifyEntity = new ArrayList<>();
  private GameClassifyModule mModule;
  private List<WBNavView.TabEntity> mTabList = new ArrayList<>();
  private Intent mIntent;
  private int mMode;
  private int mTypeId;
  private String mTypeName;
  //当前页码
  private int mCurrentPosition;
  private int mPage = 1;
  //分页的type（type对应mode）
  private int mTyPeMode;
  //分页的typeid
  private int mPageTypeId;
  //导航的当前位置
  private int mWbPosition;
  private int mItemGameCount;
  //游戏总数
  private int mSize = 8;
  //总页数
  private int mPageSum;
  //wbView是否得到焦点
  private boolean isWbFocused;

  private LoadingDialog mDialog;
  boolean isUpdate = false;

  @Override protected int setLayoutId() {
    return R.layout.activity_game_classify;
  }

  @Override protected void init(Bundle savedInstanceState) {
    super.init(savedInstanceState);
    mModule = getModule(GameClassifyModule.class);
    mAdapter = new GameClassifyAdapter(GameClassifyActivity.this, mWbNav, 0, 1);
    mList.setAdapter(mAdapter);
    getIntenData();
    ////3.配置背景图   游戏大厅各个标签 参数tagId
    //getModule(CommonModule.class).getGameHallBg(mTypeId);
  }

  public void getIntenData() {
    mIntent = getIntent();
    mMode = mIntent.getIntExtra(TYPE_MODE_KEY, -1);
    if (mMode != -1) {
      mModule.getGameListByMode(mPage, mMode);
      mTyPeMode = mMode;
      //加载左侧导航栏的背景图
      loadBackGround(mTyPeMode);
    }
    mTypeId = mIntent.getIntExtra(TYPE_TAG_KEY, -1);

    if (mTypeId != -1) {
      mModule.getGameListByTag(mPage, mTypeId);
      //3.配置背景图   游戏大厅各个标签 参数tagId
      getModule(CommonModule.class).getGameHallBg(mTypeId);
    }
    mTypeName = mIntent.getStringExtra(TYPR_NAME);
  }

  private void loadBackGround(int mTyPeMode) {
    getModule(CommonModule.class).getGameHallLeftBg(mTyPeMode);
  }

  @Override protected void dataCallback(int result, Object data) {
    super.dataCallback(result, data);
    if (result == MODE_RESULT || result == TAG_RESULT) {
      //去掉空数据项
      for (GameClassifyEntity gameClassifyEntity : (List<GameClassifyEntity>) data) {
        if (gameClassifyEntity != null && gameClassifyEntity.gameClassifySubEntities.size() > 0) {
          mGameClassifyEntity.add(gameClassifyEntity);
        }
      }
      //没有数据，不要显示界面
      if (mGameClassifyEntity == null || mGameClassifyEntity.size() == 0) {
        T.showShort(this, "没有数据~~");
        finish();
        return;
      }
      //如果是刷新数据，不重新设置导航栏数据
      if (!isUpdate) {
        setTable(mGameClassifyEntity);
        setListener();
      }
    } else if (result == TAG_MODE_RESULT) {
      ArrayList<GameClassifyEntity.GameClassifySubEntity> list =
          (ArrayList<GameClassifyEntity.GameClassifySubEntity>) data;
      if (list != null && list.size() > 0) {
        setPageData((ArrayList<GameClassifyEntity.GameClassifySubEntity>) data);
      } else {
        mAdapter.setUp(false);
        mAdapter.notifyDataSetChanged();
      }
    } else if (result == TAG_MODE_SIZE_RESULT) {
      setGameSize((GameSizeEntity) data);
    } else if (result == TAG_TYPE_SIZE_RESULT) {
      setGameSize((GameSizeEntity) data);
    } else if (result == REQUEST_ERROR_RESULT) {
      T.showShort(this, "没有数据~~");
      finish();
      return;
    } else if (result == CommonModule.MODULEBG_SUCCESS) {
      //加载背景图片
      ImageManager.getInstance().loadBackground(GameClassifyActivity.this, (ModuleBgEntity) data);
    }
  }

  public void setGameSize(GameSizeEntity gameSizeEntity) {
    mPageSum = mModule.getPageSize(Double.parseDouble(gameSizeEntity.size));
    mItemGameCount = Integer.parseInt(gameSizeEntity.size);
    //为了解决mNum显示延迟bug
    mNum.setText("(1/" + mItemGameCount + ")");
  }

  /**
   * 设置游戏翻页的数据
   */
  public void setPageData(
      ArrayList<GameClassifyEntity.GameClassifySubEntity> gameClassifySubEntity) {
    mAdapter.setUp(false);
    mAdapter.upDate(mCurrentPosition, gameClassifySubEntity);
    //mAdapter.notifyDataSetChanged();
  }

  /**
   * 设置导航
   */
  public void setTable(List<GameClassifyEntity> gameClassifyEntity) {
    if (gameClassifyEntity != null) {
      for (int i = 0; i < gameClassifyEntity.size(); i++) {
        String name = gameClassifyEntity.get(i).typeName;
        if (name.equals("0")) {
          name = "全部";
        } else if (name.equals("1")) {
          name = "手柄";
        } else if (name.equals("2")) {
          name = "遥控器";
        }
        if (gameClassifyEntity.get(i).gameClassifySubEntities != null
            && gameClassifyEntity.get(i).gameClassifySubEntities.size() > 0) {
          mTabList.add(new WBNavView.TabEntity(name));
        }
      }
      mWbNav.setTabs(mTabList);
    }
  }

  @Override public void finish() {
    if (isUpdate) {
      Intent intent = getIntent();
      intent.putExtra(TurnManager.UPDATE_KEY, true);
      setResult(TurnManager.PAY_RESULT_CODE, intent);
    }
    super.finish();
  }

  /**
   * 设置tab信息
   */
  public void setTabInfo(int position) {
    mNameFirst.setText(mTypeName + "-");
    mNameSecond.setText(mWbNav.getTabItem(position));
  }

  @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if (requestCode == TurnManager.PAY_REQUEST_CODE
        && resultCode == TurnManager.PAY_RESULT_CODE
        && data != null
        && (isUpdate = IsUpdateIconUtil.isUpdateIcon(data))) {
      mGameClassifyEntity.clear();
      getIntenData();
    }
  }

  /**
   * 导航选中的时候初始化数据
   */
  public void setListener() {
    mWbNav.setOnItemSelectedListener(new WBNavView.OnLineTabViewListener() {

      @Override public void onItemSelected(int position, View view) {

        if (mGameClassifyEntity != null) {
          if (mGameClassifyEntity.size() >= position
              && mGameClassifyEntity.get(position).gameClassifySubEntities != null
              && mGameClassifyEntity.get(position).gameClassifySubEntities.size() > 0) {
            //总页数
            mPageSum = mModule.getPageSize(
                (double) mGameClassifyEntity.get(position).gameClassifySubEntities.size());
            ////每个分类游戏总数
            //mItemGameCount = mGameClassifyEntity.get(position).gameClassifySubEntities.size();
            //L.d("总页数==" + mPageSum);

            String name = mGameClassifyEntity.get(position).typeName;
            if (name.equals("0")) {
              mTyPeMode = 0;
            } else if (name.equals("1")) {
              mTyPeMode = 1;
            } else if (name.equals("2")) {
              mTyPeMode = 2;
            }

            mAdapter = new GameClassifyAdapter(GameClassifyActivity.this, mWbNav, position, 1,
                (ArrayList<GameClassifyEntity.GameClassifySubEntity>) mGameClassifyEntity.get(
                    position).gameClassifySubEntities);
            mList.setAdapter(mAdapter);

            mPageTypeId = mGameClassifyEntity.get(position).typeId;
            //初始化值
            mPage = 1;
            mSize = 8;
            mCurrentPosition = 0;
            mWbPosition = position;

            isWbFocused = true;
            //请求第二页数据
            //new Handler().postDelayed(() -> {
            //  mPage++;
            //  loadMore();
            //}, 10);

            setTabInfo(position);
            mNum.setVisibility(View.INVISIBLE);

            if (mPageSum == 1) {
              showArrow(false, false);
            } else {
              showArrow(false, true);
            }
          }
        }
      }

      @Override public void onRightKeyEvent(int position, KeyEvent event) {
        mList.clearFocus();
        if (mList.getLayoutManager().getItemCount() > 0) {
          LinearLayout item = (LinearLayout) mList.getLayoutManager().getChildAt(0);
          if (item != null) {
            item.requestFocus();
            mNum.setVisibility(View.VISIBLE);
          }
        }
        /**
         * 获取游戏size
         */
        if (mMode != -1) {
          getModule(GameClassifyModule.class).getGameSizeByMode(mMode,
              mGameClassifyEntity.get(position).typeId);
        } else {
          getModule(GameClassifyModule.class).getGameSizeByType(
              mGameClassifyEntity.get(position).typeId, mTyPeMode);
        }
        isWbFocused = false;

        mPage++;
        loadMore();
        ////请求第二页数据
        //new Handler().postDelayed(() -> {
        //  mPage++;
        //  loadMore();
        //}, 10);
      }
    });
    mList.addOnScrollListener(new RecyclerView.OnScrollListener() {
      int dx = 0;

      @Override public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
        super.onScrollStateChanged(recyclerView, newState);
        if (newState == RecyclerView.SCROLL_STATE_IDLE) {
          if (mPageSum == 1) {
            return;
          } else {
            handleScroll(dx);
          }
        }
      }

      @Override public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);
        this.dx = dx;
      }
    });
  }

  /**
   * 处理滚动
   */
  private void handleScroll(int dx) {
    if (dx < 0) {
      mCurrentPosition--;
      mPage--;
      if (mCurrentPosition < 0) {
        mCurrentPosition = 0;
        mPage = 1;
      }
      mAdapter.setUp(true);
      mList.scrollToPosition(mCurrentPosition);
      //mAdapter.notifyDataSetChanged(); //更新mCurrentPosition 重新刷新页面
    } else {
      //if (mPage != mPageSum) {
      if (mCurrentPosition != mPageSum - 1) {
        mCurrentPosition++;
        mPage++;
        loadMore();
      } else {
        L.d("最后一页===");
      }
    }
    setDirectionImg();
    new Handler().postDelayed(new Runnable() {
      @Override public void run() {
        mAdapter.notifyDataSetChanged();
      }
    }, 200);

    Log.d(TAG,
        "当前currentPosition===" + mCurrentPosition + "当前page===》" + mPage + "总页数====》" + mPageSum);
  }

  public void loadMore() {
    if (mWbPosition == 0) {
      if (mMode != -1) {
        getModule(GameClassifyModule.class).getGameListByModeAndType(0, mPage, mPageTypeId, mMode);
      } else {
        getModule(GameClassifyModule.class).getGameListByModeAndType(1, mPage, mPageTypeId, 0);
      }
    } else {
      getModule(GameClassifyModule.class).getGameListByModeAndType(mTyPeMode, mPage, mPageTypeId,
          mTyPeMode);
    }
  }

  public void showArrow(boolean showLeft, boolean showRight) {
    mIvLeft.setVisibility(showLeft ? View.VISIBLE : View.GONE);
    mIvRight.setVisibility(showRight ? View.VISIBLE : View.GONE);
  }

  private void setDirectionImg() {

    if (mPageSum == 1) {//总页数只有一页的情况
      mIvLeft.setVisibility(View.GONE);
      mIvRight.setVisibility(View.GONE);
    } else if (mCurrentPosition == mPageSum - 1) {
      mIvLeft.setVisibility(View.VISIBLE);
      mIvRight.setVisibility(View.GONE);
    } else if (mCurrentPosition == 0) {
      mIvLeft.setVisibility(View.GONE);
      mIvRight.setVisibility(View.VISIBLE);
    } else {
      mIvLeft.setVisibility(View.VISIBLE);
      mIvRight.setVisibility(View.VISIBLE);
    }
  }

  //获取每个分类游戏总数
  public int getItemGameCount() {
    return mItemGameCount;
    //return mPageSum;
  }

  //获取当前页
  public int getCurrentPosition() {
    //return mPageSum;
    return mCurrentPosition;
  }

  @Override public boolean onKeyDown(int keyCode, KeyEvent event) {
    if (keyCode == KeyEvent.KEYCODE_PAGE_DOWN || keyCode == KeyEvent.KEYCODE_2) { //下一页
      mList.scrollToPosition(mCurrentPosition + 1);
      handleScroll(1);
      return true;
    } else if (keyCode == KeyEvent.KEYCODE_PAGE_UP || keyCode == KeyEvent.KEYCODE_1) {//上一页
      //   mList.scrollToPosition(mCurrentPosition - 1);
      handleScroll(-1);
      return true;
    }
    return super.onKeyDown(keyCode, event);
  }

  @Override public void onBackPressed() {
    if (isWbFocused) {
      finish();
    } else {
      mWbNav.setItemSelected(mWbPosition);
    }
  }
}
