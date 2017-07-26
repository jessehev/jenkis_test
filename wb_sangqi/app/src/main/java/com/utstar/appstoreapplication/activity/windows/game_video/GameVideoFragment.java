package com.utstar.appstoreapplication.activity.windows.game_video;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import butterknife.Bind;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.utstar.appstoreapplication.activity.R;
import com.utstar.appstoreapplication.activity.databinding.FragmentGameVideoBinding;
import com.utstar.appstoreapplication.activity.entity.net_entity.GameVideoEntity;
import com.utstar.appstoreapplication.activity.windows.base.BaseFragment;
import com.utstar.appstoreapplication.activity.windows.my_game.my_game_detail.SimpleViewPagerAdapter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * JesseHev 2017-03-01
 *
 * 游戏视频主页
 */
public class GameVideoFragment extends BaseFragment<FragmentGameVideoBinding> {
  @Bind(R.id.vp) ViewPager mVp;
  @Bind(R.id.narrow_left) ImageView mArrowLeft;
  @Bind(R.id.narrow_right) ImageView mArrowRight;

  private SimpleViewPagerAdapter mPagerAdapter;
  private Map<Integer, GameVideoListFragment> mFragments = new HashMap<>();
  private int mCurrentPage;
  public static final int GAMEVIDEO_RESULT = 0X901;
  private List<GameVideoEntity> mData;

  public static String BACK_FINISH = "back_finish";//从webview返回到玩吧界面

  public GameVideoFragment() {
  }

  public static GameVideoFragment newInstance() {
    GameVideoFragment fragment = new GameVideoFragment();
    return fragment;
  }

  @Override protected void init(Bundle savedInstanceState) {
    mPagerAdapter = new SimpleViewPagerAdapter(getChildFragmentManager());
    mVp.setAdapter(mPagerAdapter);

    getModule(GameVideoModule.class).getVideoList();
    //initData();
    setListener();
    setBgParam(4, 1);
  }

  public void setListener() {
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

  public boolean onKeyDown(int keyCode, KeyEvent event) {
    if (mFragments.size() == 1 || mFragments.size() == 0) { //数据为空、数据只有一页的时候不处理
      return false;
    }
    //if (BuildConfig.DEBUG) {
    //  if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
    //    handleDown();
    //  } else if (keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
    //    handleUp();
    //  }
    //} else {
    if (keyCode == KeyEvent.KEYCODE_PAGE_DOWN) {  //下一页
      handleDown();
    } else if (keyCode == KeyEvent.KEYCODE_PAGE_UP) { //上一页
      handleUp();
    }
    return false;
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

  public void requestFocus() {
    Fragment fragment = ((SimpleViewPagerAdapter) mVp.getAdapter()).getItem(mVp.getCurrentItem());
    if (fragment instanceof GameVideoListFragment) {
      ((GameVideoListFragment) fragment).requestFocus();
    }
  }

  public ViewPager getViewpager() {
    return mVp;
  }

  /**
   * 显示箭头指示
   *
   * @param isLeftShow 是否显示左边箭头
   * @param isRightShow 是否显示右边箭头
   */
  public void showArrow(boolean isLeftShow, boolean isRightShow) {
    mArrowLeft.setVisibility(isLeftShow ? View.VISIBLE : View.GONE);
    mArrowRight.setVisibility(isRightShow ? View.VISIBLE : View.GONE);
  }

  @Override protected int setLayoutId() {
    return R.layout.fragment_game_video;
  }

  @Override protected void dataCallback(int result, Object obj) {
    super.dataCallback(result, obj);

    if (result == GAMEVIDEO_RESULT) {
      GameVideoListFragment fragment = null;
      mData = (List<GameVideoEntity>) obj;
      if (mData != null && mData.size() > 0) {
        for (int i = 0, count = getModule(GameVideoModule.class).getPageSize(mData.size());
            i < count; i++) {
          int start = i * 8;
          int end = i * 8 + 8;
          if (end >= mData.size()) end = mData.size();
          fragment = new GameVideoListFragment(i, mData.subList(start, end));
          mFragments.put(i, fragment);//保存fragment页面
          mPagerAdapter.addFrag(fragment, "i = " + i);
        }
        mPagerAdapter.notifyDataSetChanged();
      }
    }
  }

  /**
   * 测试数据
   */
  public void testData() {
    GameVideoListFragment fragment = null;
    mData = new Gson().fromJson(json, new TypeToken<List<GameVideoEntity>>() {
    }.getType());

    if (mData != null && mData.size() > 0) {
      for (int i = 0, count = getModule(GameVideoModule.class).getPageSize(mData.size()); i < count;
          i++) {
        int start = i * 8;
        int end = i * 8 + 8;
        if (end >= mData.size()) end = mData.size();
        fragment = new GameVideoListFragment(i, mData.subList(start, end));
        mFragments.put(i, fragment);//保存fragment页面
        mPagerAdapter.addFrag(fragment, "i = " + i);
        mPagerAdapter.notifyDataSetChanged();
      }
    }
  }

  String json = "[\n"
      + "    {\n"
      + "        \"img\": \"http://cggzg.img48.wal8.com/img48/567144_20170302152304/148843954079.png\",\n"
      + "        \"address\": \"https://www.baidu.com/\",\n"
      + "        \"name\": \"电竞专区\"\n"
      + "    },\n"
      + "    {\n"
      + "        \"img\": \"http://cggzg.img48.wal8.com/img48/567144_20170302152304/148843979003.png\",\n"
      + "        \"address\": \"https://www.baidu.com/\",\n"
      + "        \"name\": \"小学生专区\"\n"
      + "    },\n"
      + "    {\n"
      + "        \"img\": \"http://cggzg.img48.wal8.com/img48/567144_20170302152304/148843954079.png\",\n"
      + "        \"address\": \"https://www.baidu.com/\",\n"
      + "        \"name\": \"小学生专区\"\n"
      + "    },\n"
      + "    {\n"
      + "        \"img\": \"http://cggzg.img48.wal8.com/img48/567144_20170302152304/148843979003.png\",\n"
      + "        \"address\": \"https://www.baidu.com/\",\n"
      + "        \"name\": \"小学生专区\"\n"
      + "    }, {\n"
      + "        \"img\": \"http://cggzg.img48.wal8.com/img48/567144_20170302152304/148843979003.png\",\n"
      + "        \"address\": \"https://www.baidu.com/\",\n"
      + "        \"name\": \"小学生专区\"\n"
      + "    }, {\n"
      + "        \"img\": \"http://cggzg.img48.wal8.com/img48/567144_20170302152304/148843979003.png\",\n"
      + "        \"address\": \"https://www.baidu.com/\",\n"
      + "        \"name\": \"小学生专区\"\n"
      + "    }, {\n"
      + "        \"img\": \"http://cggzg.img48.wal8.com/img48/567144_20170302152304/148843979003.png\",\n"
      + "        \"address\": \"https://www.baidu.com/\",\n"
      + "        \"name\": \"小学生专区\"\n"
      + "    }, {\n"
      + "        \"img\": \"http://cggzg.img48.wal8.com/img48/567144_20170302152304/148843979003.png\",\n"
      + "        \"address\": \"https://www.baidu.com/\",\n"
      + "        \"name\": \"小学生专区\"\n"
      + "    }, {\n"
      + "        \"img\": \"http://cggzg.img48.wal8.com/img48/567144_20170302152304/148843979003.png\",\n"
      + "        \"address\": \"https://www.baidu.com/\",\n"
      + "        \"name\": \"小学生专区\"\n"
      + "    }, {\n"
      + "        \"img\": \"http://cggzg.img48.wal8.com/img48/567144_20170302152304/148843979003.png\",\n"
      + "        \"address\": \"https://www.baidu.com/\",\n"
      + "        \"name\": \"小学生专区\"\n"
      + "    },  {\n"
      + "        \"img\": \"http://cggzg.img48.wal8.com/img48/567144_20170302152304/148843979003.png\",\n"
      + "        \"address\": \"https://www.baidu.com/\",\n"
      + "        \"name\": \"小学生专区\"\n"
      + "    }, {\n"
      + "        \"img\": \"http://cggzg.img48.wal8.com/img48/567144_20170302152304/148843979003.png\",\n"
      + "        \"address\": \"https://www.baidu.com/\",\n"
      + "        \"name\": \"小学生专区\"\n"
      + "    }, {\n"
      + "        \"img\": \"http://cggzg.img48.wal8.com/img48/567144_20170302152304/148843979003.png\",\n"
      + "        \"address\": \"https://www.baidu.com/\",\n"
      + "        \"name\": \"小学生专区\"\n"
      + "    },  {\n"
      + "        \"img\": \"http://cggzg.img48.wal8.com/img48/567144_20170302152304/148843979003.png\",\n"
      + "        \"address\": \"https://www.baidu.com/\",\n"
      + "        \"name\": \"小学生专区\"\n"
      + "    }, {\n"
      + "        \"img\": \"http://cggzg.img48.wal8.com/img48/567144_20170302152304/148843979003.png\",\n"
      + "        \"address\": \"https://www.baidu.com/\",\n"
      + "        \"name\": \"小学生专区\"\n"
      + "    },"
      + "  {\n"
      + "        \"img\": \"http://cggzg.img48.wal8.com/img48/567144_20170302152304/148843979003.png\",\n"
      + "        \"address\": \"https://www.baidu.com/\",\n"
      + "        \"name\": \"小学生专区\"\n"
      + "    }\n"
      + "]";
}
