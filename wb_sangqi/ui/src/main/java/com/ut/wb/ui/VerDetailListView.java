package com.ut.wb.ui;

import activity.appstoreapplication.utstar.com.ui.R;
import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.ScaleAnimation;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;

public class VerDetailListView extends ListView implements AdapterView.OnItemSelectedListener {
  private Animation animIn;
  private int position;
  private boolean PosiDirection;
  // private ListView mListView;
  private OnUIChangeListenr onUIChangeListenr;
  private int mPosiSelector = 0;

  private ScaleAnimation animNarrow;
  private int FragmentTabLine = 0;
  private int key;
  private int z = 0;
  private boolean RightFlag = false;
  private ScaleAnimation animOut;
  private Context mContext;
  private View chlidView;
  private AdapterView<?> mMadapterView;
  private int mCount = 0;
  private int mSelPosMax;
  private int mTypeID;
  private String mTypeName;
  private int widthChlid;
  private boolean downFlag;

  public VerDetailListView(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    // TODO Auto-generated constructor stubN
    Init();
    this.mContext = context;
  }

  private void Init() {

    InitAnim();
    InitListen();
  }

  private void InitAnim() {
    animIn = new ScaleAnimation(1.1f, 1f, 1.1f, 1f, Animation.RELATIVE_TO_SELF, 0.5f,
        Animation.RELATIVE_TO_SELF, 0.5f);
    animNarrow = new ScaleAnimation(1.1f, 1f, 1.1f, 1f, Animation.RELATIVE_TO_SELF, 0.f,
        Animation.RELATIVE_TO_SELF, 0.5f);
    animOut = new ScaleAnimation(1, 1.1f, 1, 1.1f, Animation.RELATIVE_TO_SELF, 0.5f,
        Animation.RELATIVE_TO_SELF, 0.5f);
    animOut.setInterpolator(new DecelerateInterpolator());
    animIn.setInterpolator(new DecelerateInterpolator());
  }

  private void InitListen() {
    this.setOnItemSelectedListener(this);
    // this.setOnScrollListener(this);
    // this.setOnFocusChangeListener(this);

  }

  public VerDetailListView(Context context, AttributeSet attrs) {
    super(context, attrs);
    // TODO Auto-generated constructor stub
    // FragmentTabLine = getFragmentTabLine();
    Init();
    this.mContext = context;
  }

  public VerDetailListView(Context context) {
    super(context);
    Init();
    this.mContext = context;
    // TODO Auto-generated constructor stub
  }

  public VerDetailListView(Context context, int mFragmentTabLine) {
    super(context);
    this.FragmentTabLine = mFragmentTabLine;
    this.mContext = context;
    Init();
  }

  public void setOnUIChangeListenr(OnUIChangeListenr onUIChangeListenr) {
    this.onUIChangeListenr = onUIChangeListenr;
  }

  @Override public void onNothingSelected(AdapterView<?> arg0) {
    // TODO Auto-generated method stub

  }

  @Override
  public void onItemSelected(AdapterView<?> madapterView, View view, int position, long arg3) {
    //if (getContext() instanceof GameBarActivity) {
    //  ((GameBarActivity) getContext()).setFoucsView(view);
    //}
    mPosiSelector = position;
    chlidView = view;
    mMadapterView = madapterView;
    //  LogM.i("onItemSelected+onItemSelected----->" + mPosiSelector);
    if (this.isFocused() == true) {
      clearAnimation(madapterView, view);// 缩小
      if (view.getHeight() != 0) {
        widthChlid = view.getHeight();
      }
      if (0 < position && PosiDirection == false) {
        // this.smoothScrollBy(0, 600);
        if (position != 1)
        // this.smoothScrollToPosition(position+1);
        {
          this.smoothScrollBy(widthChlid, 400);
        }
      }

      if (PosiDirection == true && position != getAdapter().getCount() - 2) {
        // this.smoothScrollBy(0, 600);
        this.smoothScrollBy(-widthChlid, 400);
        // this.smoothScrollToPosition(position - 1);
      }
      ControlAnimation(madapterView, view, position);// 放大
    }
  }

  /**
   * 清理动画
   */
  private void clearAnimation(AdapterView<?> madapterView, View childAt) {
    for (int i = 0; i < madapterView.getChildCount(); i++) {
      if (madapterView.getChildAt(i) != childAt) {

        madapterView.getChildAt(i)
            .animate()
            .alpha(0.6f)
            .scaleX(1.0f)
            .scaleY(1.0f)
            .x(0)
            .setDuration(400)
            .setInterpolator(new DecelerateInterpolator())
            .start();
      }
    }
  }

  private void ControlAnimation(AdapterView<?> madapterView, View view, int position) {

    view.animate()
        .alpha(1)
        .scaleX(1.1f)
        .scaleY(1.1f)
        .x(5)
        .setInterpolator(new DecelerateInterpolator())
        .setDuration(400)
        .start();
    int n = madapterView.getChildCount();
    for (int i = 0; i < n; i++) {
      if (madapterView.getChildAt(i).isSelected() == true) {
        SetRran(madapterView, i);
      } else {
        ((ImageView) madapterView.getChildAt(i)
            .findViewById(R.id.detail_listview_imgv_baff)).setImageResource(
            R.drawable.title_baffle_bg);
      }
    }
  }

  private void SetRran(AdapterView<?> madapterView, int i) {
    ((ImageView) madapterView.getChildAt(i)
        .findViewById(R.id.detail_listview_imgv_baff)).setImageResource(R.drawable.baffle_tran);
  }

  @Override public boolean dispatchKeyEvent(KeyEvent event) {

    int keyCode = event.getKeyCode();

    if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == event.KEYCODE_DPAD_DOWN) {
      PosiDirection = false;
      if (mPosiSelector == (this.getCount() - 1) && downFlag) {
        //                KeyDownAnimtran(this);
        return true;
      }
    }
    if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == event.KEYCODE_DPAD_UP) {
      PosiDirection = true;
      //            if (mPosiSelector == 0 && downFlag)
      //                KeyUpAnimtran(this);
    }

    if (event.getAction() == KeyEvent.ACTION_DOWN) {
      downFlag = false;
    } else if (event.getAction() == KeyEvent.ACTION_UP) {
      downFlag = true;
    }
    if (event.getAction() == KeyEvent.ACTION_UP && keyCode == event.KEYCODE_DPAD_RIGHT) {

  //    this.setNextFocusRightId(R.id.lvRelationImages);

      //            if (chlidView != null && RightFlag == false)
      //            {
      //                for (int i = 0; i < mMadapterView.getChildCount(); i++)
      //                {
      //                    ((ImageView) mMadapterView.getChildAt(i).findViewById(
      //                            R.id.detail_listview_imgv_baff))
      //                            .setImageResource(R.drawable.baffle_bg);
      //                }
      //                ((ImageView) chlidView
      //                        .findViewById(R.id.detail_listview_imgv_baff))
      //                        .setImageResource(R.drawable.baffle_tran);
      //                startAnimation(chlidView);
      //            }
    }
    if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == event.KEYCODE_DPAD_LEFT) {
  //    LogM.i("LEFT---->" + mPosiSelector);

      //            if (chlidView != null)
      //            {
      //                for (int i = 0; i < mMadapterView.getChildCount(); i++)
      //                {
      //
      //                    ((ImageView) mMadapterView.getChildAt(i).findViewById(
      //                            R.id.detail_listview_imgv)).setSelected(false);
      //                    ((ImageView) mMadapterView.getChildAt(i).findViewById(
      //                            R.id.detail_listview_imgv_baff))
      //                            .setImageResource(R.drawable.baffle_tran);
      //                }
      //
      //                clearAnimation(chlidView);
      //            }

      RightFlag = false;
      mCount = 0;
    }
    return super.dispatchKeyEvent(event);
  }

  @Override
  protected void onFocusChanged(boolean gainFocus, int direction, Rect previouslyFocusedRect) {
    if (mMadapterView == null || previouslyFocusedRect == null) {
      super.onFocusChanged(gainFocus, direction, previouslyFocusedRect);
      return;
    }
    if (!gainFocus) {
      for (int i = 0; i < mMadapterView.getChildCount(); i++) {

        //                 ((ImageView) mMadapterView.getChildAt(i).findViewById(
        //                         R.id.detail_listview_imgv)).setSelected(false);
        ((ImageView) mMadapterView.getChildAt(i)
            .findViewById(R.id.detail_listview_imgv_baff)).setImageResource(R.drawable.baffle_tran);
      }
      clearAnimation(chlidView);
    } else {
      if (chlidView != null) {
        for (int i = 0; i < mMadapterView.getChildCount(); i++) {
          ((ImageView) mMadapterView.getChildAt(i)
              .findViewById(R.id.detail_listview_imgv_baff)).setImageResource(R.drawable.baffle_bg);
        }
        ((ImageView) chlidView.findViewById(R.id.detail_listview_imgv_baff)).setImageResource(
            R.drawable.baffle_tran);
        startAnimation(chlidView);
      }
    }
    super.onFocusChanged(gainFocus, direction, previouslyFocusedRect);
  }

  private void clearAnimation(View childAt) {

    childAt.animate()
        .scaleX(1.0f)
        .scaleY(1.0f)
        .setInterpolator(new DecelerateInterpolator())
        .setDuration(400)
        .start();
  }

  /**
   * 播放动画
   */
  private void startAnimation(View view) {

    view.animate()
        .scaleX(1.1f)
        .scaleY(1.1f)
        .setInterpolator(new DecelerateInterpolator())
        .setDuration(400)
        .start();
  }

  public int getmPosiSelector() {
    return mPosiSelector;
  }

  public void setmPosiSelector(int mPosiSelector) {
    this.mPosiSelector = mPosiSelector;
  }

  @Override public void setSelection(int position) {
    // TODO Auto-generated method stub
    super.setSelection(position);
  }

  public int getFragmentTabLine() {
    return FragmentTabLine;
  }

  public void setFragmentTabLine(int fragmentTabLine) {
    FragmentTabLine = fragmentTabLine;
  }

  public interface OnUIChangeListenr {

    /**
     * 此方法用于从服务器获取数据
     */
    public void loadData();

    /**
     * 此方法用于焦点变化时更新UI
     */
    public void updateUI(int position);
  }
}
