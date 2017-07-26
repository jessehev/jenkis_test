package com.utstar.appstoreapplication.activity.commons.widget;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.arialyy.frame.util.AndroidUtils;
import com.utstar.appstoreapplication.activity.R;
import java.util.List;

/**
 * Created by lyy on 2016/11/21.
 * 首页导航栏
 */
public class WBNavView extends ViewGroup {
  private static final String TAG = "LineTabView";
  private List<TabEntity> mTabs;
  private int HEIGHT = 0;
  private int mCurrentPosition = 0;
  private OnLineTabViewListener mListener;
  private TabSelectedView mSelectedView;
  private boolean isFirst = true;
  private int mItemNum;

  /**
   * 导航栏监听
   */
  public interface OnLineTabViewListener {
    /**
     * item被选中回调
     *
     * @param position 当前item位置
     * @param view 当前item
     */
    public void onItemSelected(int position, View view);

    /**
     * 方向键右键事件
     *
     * @param event 方向键右键事件
     */
    public void onRightKeyEvent(int position, KeyEvent event);
  }

  public WBNavView(Context context) {
    this(context, null);
  }

  public WBNavView(Context context, AttributeSet attrs) {
    this(context, attrs, -1);
  }

  public WBNavView(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    init(context, attrs);
  }

  private void init(Context context, AttributeSet attrs) {
    mSelectedView = new TabSelectedView(context);
    setClipChildren(false);
    //getViewTreeObserver().addOnGlobalFocusChangeListener((oldFocus, newFocus) -> {
    //  if (hasFocus()) {
    //    setItemSelected(mCurrentPosition, mCurrentPosition);
    //  }
    //});
  }

  @Override protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    if (mTabs != null && mTabs.size() > 0) {
      HEIGHT = MeasureSpec.getSize(heightMeasureSpec);
      //L.d(TAG, "HEIGHT = " + HEIGHT);
    }
    int childState = 0;
    for (int i = 0, count = getChildCount(); i < count; i++) {
      View child = getChildAt(i);
      measureChild(child, widthMeasureSpec, heightMeasureSpec);
      childState = combineMeasuredStates(childState, child.getMeasuredState());
    }
    int w = MeasureSpec.getSize(widthMeasureSpec);
    if (getChildAt(0) != null) {
      w = Math.max(getChildAt(0).getMeasuredWidth(), getSuggestedMinimumWidth());
    }
    setMeasuredDimension(resolveSizeAndState(w, widthMeasureSpec, childState),
        resolveSizeAndState(Math.max(HEIGHT, getSuggestedMinimumHeight()), heightMeasureSpec,
            childState << MEASURED_HEIGHT_STATE_SHIFT));
  }

  private void handleChild() {
    int temp = AndroidUtils.dp2px(1);
    for (int i = 0, count = getChildCount() - 1; i < count; i++) {
      View child = getChildAt(i);
      int childH = getChildAt(1).getMeasuredHeight();
      int interval = (HEIGHT
          - getPaddingTop()
          - getPaddingBottom()
          - getChildAt(0).getMeasuredHeight()
          - childH * (mTabs.size() - 1)) / (mTabs.size() - 1) - temp;
      if (i != 0) {
        //   ((ChildView) child).setTempHeight(interval);
      } else {
        // ((ChildView) child).getLine().setVisibility(GONE);
      }
    }
  }

  @Override protected void onLayout(boolean changed, int l, int t, int r, int b) {
    //handleChild();
    int h = 0;
    int top = 0;
    for (int i = 0, count = getChildCount() - 1; i < count; i++) {
      View child = getChildAt(i);
      int childH = child.getMeasuredHeight();
      int cl = 0;
      int ct = i == 0 ? 0 : h;
      int cr = child.getMeasuredWidth();
      int cb = ct + childH;
      h += childH;
      child.layout(cl, ct + top, cr, cb);
    }
    int sw = mSelectedView.getMeasuredWidth(), sh = mSelectedView.getMeasuredHeight();
    int[] xy = getRealPosition(0);
    mSelectedView.layout(xy[0], xy[1], xy[0] + sw, xy[1] + sh);

    setSelectedViewXy();
  }

  /**
   * 该方法为了解决初始化view的时候选中的指示图标位置不正确的bug
   */
  public void setSelectedViewXy() {
    int[] oldXY = getRealPosition(0);
    mSelectedView.setX(oldXY[0]);
    mSelectedView.setY(oldXY[1]);
  }

  /**
   * 设置Item选择监听
   *
   * @param onItemSelectedListener {@link OnLineTabViewListener}
   */
  public void setOnItemSelectedListener(@NonNull OnLineTabViewListener onItemSelectedListener) {
    mListener = onItemSelectedListener;
  }

  /**
   * 设置tab实体
   *
   * @param tabEntity tab 实体 {@link TabEntity}
   */
  public void setTabs(@NonNull List<TabEntity> tabEntity) {
    if (tabEntity.size() == 0) {
      //  L.e(TAG, "tab实体不能小于1");
      return;
    }
    if (mTabs != null) {
      mTabs.clear();
    }
    removeAllViews();
    mTabs = tabEntity;
    mItemNum = mTabs.size();
    int i = 0;
    for (TabEntity entity : tabEntity) {
      addView(createTabView(entity, i));
      i++;
    }
    addView(mSelectedView);
    invalidate();
  }

  public String getTabItem(int position) {
    if (mTabs != null && mTabs.size() > 0) {
      return mTabs.get(position).chinese;
    }
    return null;
  }

  /**
   * 创建tab
   *
   * @param entity Tab实体
   */
  private ChildView createTabView(TabEntity entity, int i) {
    ChildView tab = new ChildView(getContext());
    tab.setChinese(entity.chinese);
    // tab.setEnglish(entity.english);
    return tab;
  }

  /**
   * 设置Item被选中
   */
  public void setItemSelected(int position) {
    mCurrentPosition = position;
    setItemSelected(mCurrentPosition, mCurrentPosition);
  }

  /**
   * 选中item
   *
   * @param newPosition 0 - childCount
   */
  private void setItemSelected(int oldPosition, int newPosition) {
    if (oldPosition < 0 || oldPosition >= mItemNum) {
      return;
    }
    if (newPosition < 0 || newPosition >= mItemNum) {
      //   L.e(TAG, "选项错误");
      return;
    }
    mCurrentPosition = newPosition;
    getChildAt(newPosition).requestFocus();

    // changeBg(oldPosition, newPosition);
    //getChildAt(newPosition).findViewById(R.id.chinese)
    //    .setBackgroundColor(Color.parseColor("#251D4C"));
    //getChildAt(oldPosition).findViewById(R.id.chinese)
    //    .setBackgroundColor(Color.parseColor("#00ffffff"));

    int[] oldXY = getRealPosition(oldPosition);
    int[] newXY = getRealPosition(newPosition);

    mSelectedView.setX(oldXY[0]);
    mSelectedView.setY(oldXY[1]);
    //if (oldPosition != newPosition) {
    //}
    startAnim(newXY);

    if (mListener != null) {
      mListener.onItemSelected(mCurrentPosition, getChildAt(mCurrentPosition));
    }
  }

  public void changeBg(int oldPosition, int newPosition) {
    //new Handler().postDelayed(new Runnable() {
    //  @Override public void run() {
    getChildAt(newPosition).findViewById(R.id.chinese)
        .setBackgroundColor(Color.parseColor("#251D4C"));
    getChildAt(oldPosition).findViewById(R.id.chinese)
        .setBackgroundColor(Color.parseColor("#00ffffff"));
    //}
    //  }, 800);
  }

  @Override public void invalidate() {
    super.invalidate();
    new Handler().postDelayed(() -> {
      if (isFirst) {
        setItemSelected(0, 0);
        isFirst = false;
      }
    }, 200);
  }

  /**
   * 启动动画
   *
   * @param newXY 新坐标
   */
  private void startAnim(int[] newXY) {
    mSelectedView.animate().y(newXY[1]).setDuration(200).start();
    //ObjectAnimator sx =
    //    ObjectAnimator.ofFloat(mSelectedView, "scaleX", 1f, 1.5f, 1f).setDuration(1000);
    //ObjectAnimator sy =
    //    ObjectAnimator.ofFloat(mSelectedView, "scaleY", 1f, 1.5f, 1f).setDuration(1000);

    ObjectAnimator sx = ObjectAnimator.ofFloat(mSelectedView, "scaleX", 1f, 1f, 1f).setDuration(0);
    ObjectAnimator sy = ObjectAnimator.ofFloat(mSelectedView, "scaleY", 1f, 1f, 1f).setDuration(0);

    ObjectAnimator r =
        ObjectAnimator.ofFloat(mSelectedView, "rotation", 0f, 360f).setDuration(1000);
    AnimatorSet set = new AnimatorSet();
    //set.play(sx).with(sy).with(r);
    set.play(sx).with(sy);

    //set.start();
  }

  /**
   * 获取真实坐标
   */
  private int[] getRealPosition(int position) {
    int[] xy = new int[2];
    View child = getChildAt(position);
    if (child == null) {
      child = getChildAt(0);
    }
    if (child == null) {
      return xy;
    }
    View nav = child.findViewById(R.id.nav);
    View rl = child.findViewById(R.id.rl);
    int x = (int) (rl.getX() + (nav.getX() - (mSelectedView.getWidth() - nav.getWidth()) / 2));
    int y = (int) (child.getY() + (nav.getY() - (mSelectedView.getHeight() - nav.getHeight()) / 2));
    xy[0] = x;
    xy[1] = y;
    return xy;
  }

  @Override public boolean dispatchKeyEvent(KeyEvent event) {
    int keyCode = event.getKeyCode();
    if (event.getAction() == KeyEvent.ACTION_DOWN && (keyCode == KeyEvent.KEYCODE_DPAD_DOWN
        || keyCode == KeyEvent.KEYCODE_DPAD_LEFT
        || keyCode == KeyEvent.KEYCODE_DPAD_UP
        || keyCode == KeyEvent.KEYCODE_DPAD_RIGHT)) {
      if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
        if (mCurrentPosition == mItemNum - 1) {
          //return super.dispatchKeyEvent(event);
          return true;  //防止出现选中游戏的bug
        }
        setItemSelected(mCurrentPosition,
            mCurrentPosition >= mItemNum ? mItemNum : ++mCurrentPosition);
      } else if (keyCode == KeyEvent.KEYCODE_DPAD_UP) {
        if (mCurrentPosition == 0) {
          return super.dispatchKeyEvent(event);
        }
        setItemSelected(mCurrentPosition, mCurrentPosition > 0 ? --mCurrentPosition : 0);
      } else if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
        if (mListener != null) {
          mListener.onRightKeyEvent(mCurrentPosition, event);
        }
      }
      return true;
    }
    return super.dispatchKeyEvent(event);
  }

  /**
   * item 实体
   */
  public static class TabEntity {
    String chinese;
    String english;
    int id;

    public TabEntity(String chinese) {
      this.chinese = chinese;
      //  this.english = english;
      //this.id = id;
    }
  }

  /**
   * Created by lyy on 2016/8/24.
   * 被选择按钮
   */
  class TabSelectedView extends ImageView {

    public TabSelectedView(Context context) {
      this(context, null);
    }

    public TabSelectedView(Context context, AttributeSet attrs) {
      this(context, attrs, -1);
    }

    public TabSelectedView(Context context, AttributeSet attrs, int defStyleAttr) {
      super(context, attrs, defStyleAttr);
      init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
      MarginLayoutParams lp =
          new MarginLayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
      int margin = (int) getResources().getDimension(R.dimen.dimen_20dp);
      lp.setMargins(margin, 0, margin, 0);
      setLayoutParams(lp);
      setBackgroundResource(R.mipmap.icon_h_nav_selected);
    }
  }

  /**
   * Created by lyy on 2016/8/18.
   * 首页tab子项
   */
  class ChildView extends RelativeLayout {
    TextView mChinese, mEnglish;
    ImageView mNav, mLine;
    View mTemp;

    public ChildView(Context context) {
      this(context, null);
    }

    public ChildView(Context context, AttributeSet attrs) {
      this(context, attrs, -1);
    }

    public ChildView(Context context, AttributeSet attrs, int defStyleAttr) {
      super(context, attrs, defStyleAttr);
      init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
      LayoutInflater.from(context).inflate(R.layout.layout_wb_nav_line_tab, this, true);
      mChinese = (TextView) findViewById(R.id.chinese);
      //  mEnglish = (TextView) findViewById(R.id.english);
      mNav = (ImageView) findViewById(R.id.nav);
      mLine = (ImageView) findViewById(R.id.line);
      //    mTemp = findViewById(R.id.temp);
    }

    public void setTempHeight(final int height) {
      ViewGroup.LayoutParams lp = mTemp.getLayoutParams();
      lp.height = height;
      mTemp.setLayoutParams(lp);
    }

    /**
     * 获取线
     */
    public ImageView getLine() {
      return mLine;
    }

    /**
     * 获取小圆圈
     */
    public ImageView getNav() {
      return mNav;
    }

    /**
     * 设置中文
     */
    public void setChinese(CharSequence text) {
      mChinese.setText(text);
    }

    ///**
    // * 设置英文
    // */
    //public void setEnglish(CharSequence text) {
    //  mEnglish.setText(text);
    //}
  }
}
