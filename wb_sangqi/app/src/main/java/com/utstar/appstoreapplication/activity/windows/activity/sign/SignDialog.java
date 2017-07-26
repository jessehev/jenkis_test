package com.utstar.appstoreapplication.activity.windows.activity.sign;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.OnClick;
import com.arialyy.frame.core.AbsDialog;
import com.utstar.appstoreapplication.activity.R;
import com.utstar.appstoreapplication.activity.entity.net_entity.AwardEntity;
import com.utstar.appstoreapplication.activity.entity.net_entity.CalendarEntity;
import com.utstar.appstoreapplication.activity.entity.net_entity.SignEntity;
import com.utstar.appstoreapplication.activity.manager.ActivityManager;
import com.utstar.appstoreapplication.activity.utils.ActionUtil;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by JesseHev on 2017/1/9.
 */

public class SignDialog extends AbsDialog {

  public static final String LATEST_CHECKIN_DATE_MILLIS = "latest_checkin_date_millis";
  @Bind(R.id.root_bg) ImageView mRootBg;
  @Bind(R.id.sign_date) TextView mSignDate;
  @Bind(R.id.award_list) GridView mAwardGridView;
  @Bind(R.id.sign_days) TextView mSignDays;
  @Bind(R.id.sign) Button signButton;
  @Bind(R.id.calendar) GridView mGridView;

  Bitmap mBitmap;
  List<CalendarEntity> mList;
  CalendarAdapter mAdapter;
  AwardAdapter mAwardsAdapter;
  SpannableString mSps1;
  SpannableString mSps2;
  SpannableStringBuilder mSsb;
  SignEntity mSignEntity;
  /**
   * 已经签到天数
   */
  int signDay;
  List<AwardEntity> mAwardModels;
  Activity mContext;
  DisplayMetrics mDm;
  /**
   * 是否已经签到
   */
  int mIsSigned;

  public OnSureClickListener mOnSureClickLister;

  public interface OnSureClickListener {
    void onSureClick();
  }

  public void setOnSureClickListener(OnSureClickListener onsureClickLister) {
    mOnSureClickLister = onsureClickLister;
  }

  @Override protected int setLayoutId() {
    return R.layout.dialog_sign;
  }

  @Override protected void dataCallback(int result, Object obj) {

  }

  public SignDialog(Context context, SignEntity signEntity, int isSigned) {
    super(context, R.style.blur_dialog);
    this.mContext = (Activity) context;
    this.mSignEntity = signEntity;
    this.mIsSigned = isSigned;
    mDm = new DisplayMetrics();
    mContext.getWindowManager().getDefaultDisplay().getMetrics(mDm);

    init();
  }

  @Override public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    Window window = getWindow();
    WindowManager.LayoutParams params = window.getAttributes();
    params.width = mDm.widthPixels;
    params.height = mDm.heightPixels;
    window.setAttributes(params);

    mGridView.setAdapter(mAdapter);
    mGridView.setFocusable(false);
    mGridView.setFocusableInTouchMode(false);
    mSps1 = new SpannableString(mContext.getResources().getString(R.string.all_sign_day));
    mSps2 = new SpannableString(signDay + mContext.getResources().getString(R.string.day));
    int color1 = Color.parseColor("#fffa8c");
    int color2 = Color.parseColor("#ffe85a");
    mSps1.setSpan(new ForegroundColorSpan(color1), 0, mSps1.length(),
        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
    mSps2.setSpan(new ForegroundColorSpan(color2), 0, mSps2.length(),
        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
    mSsb.append(mSps1);
    mSsb.append(mSps2);
    mSignDays.setText(mSsb);
    mSignDate.append(mSignEntity.getSignDate());
    mAwardGridView.setNumColumns(mAwardModels.size());
    mAwardGridView.setFocusable(false);
    mAwardGridView.setFocusableInTouchMode(false);
    mAwardGridView.setAdapter(mAwardsAdapter);
    if (mIsSigned == 1) {
      updateSign();
    }
    //   closeSignDialog();
    try {
      mBitmap = blurBitmap(myShot(mContext), mContext);
    } catch (Exception e) {

    }
    mRootBg.setImageBitmap(mBitmap);
  }

  public void init() {
    mList = new ArrayList<>();
    mAwardModels = mSignEntity.list;
    signDay = mSignEntity.getSignDay();
    int maximum = Calendar.getInstance().getActualMaximum(Calendar.DAY_OF_MONTH);
    for (int i = 0; i < 30; i++) {
      CalendarEntity entity = new CalendarEntity();
      entity.setDate((i + 1) + "");
      if (i < signDay) {
        entity.setSign(true);
      }
      mList.add(entity);
    }
    for (int i = 0; i < mAwardModels.size(); i++) {
      AwardEntity model = mAwardModels.get(i);
      model.setRecive(model.getTargetDay() <= signDay);
    }
    mAdapter = new CalendarAdapter(mList);
    mAwardsAdapter = new AwardAdapter(mAwardModels);
    mSsb = new SpannableStringBuilder();
  }

  protected void updateSign() {
    //signDay++;
    for (int i = 0; i < mList.size(); i++) {
      CalendarEntity entity = mList.get(i);
      if (i < signDay) {
        entity.setSign(true);
      }
    }
    for (int i = 0; i < mAwardModels.size(); i++) {
      AwardEntity model = mAwardModels.get(i);
      if (model.getTargetDay() == signDay && model.getOkStatus().equals("1")) {
        showToast();
      }
      model.setRecive(model.getTargetDay() <= signDay);
    }
    mSsb.clear();
    mSps1 = new SpannableString("累计签到天数：");
    mSps2 = new SpannableString(signDay + "天");
    int color1 = Color.parseColor("#fffa8c");
    int color2 = Color.parseColor("#ffe85a");
    mSps1.setSpan(new ForegroundColorSpan(color1), 0, mSps1.length(),
        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
    mSps2.setSpan(new ForegroundColorSpan(color2), 0, mSps2.length(),
        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
    mSsb.append(mSps1);
    mSsb.append(mSps2);
    mSignDays.setText(mSsb);
    mAdapter.notifyDataSetChanged();
    mAwardsAdapter.notifyDataSetChanged();
    //        dismiss();
    // signButton.setEnabled(false);
    Calendar c = Calendar.getInstance();
    c.set(Calendar.HOUR, 0);
    c.set(Calendar.MINUTE, 0);
    c.set(Calendar.SECOND, 0);
    c.set(Calendar.MILLISECOND, 0);
    PreferenceManager.getDefaultSharedPreferences(getContext())
        .edit()
        .putLong(LATEST_CHECKIN_DATE_MILLIS, c.getTimeInMillis())
        .commit();
  }

  //public boolean isShowing() {
  //  return getDialog().isShowing();
  //}

  @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
  public Bitmap blurBitmap(Bitmap bitmap, Context mContext) {

    // Let's create an empty bitmap with the same size of the bitmap we want
    // to blur
    Bitmap outBitmap =
        Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);

    // Instantiate a new Renderscript
    RenderScript rs = RenderScript.create(mContext);

    // Create an Intrinsic Blur Script using the Renderscript
    ScriptIntrinsicBlur blurScript = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs));

    // Create the Allocations (in/out) with the Renderscript and the in/out
    // bitmaps
    Allocation allIn = Allocation.createFromBitmap(rs, bitmap);
    Allocation allOut = Allocation.createFromBitmap(rs, outBitmap);

    // Set the radius of the blur
    blurScript.setRadius(25.f);

    // Perform the Renderscript
    blurScript.setInput(allIn);
    blurScript.forEach(allOut);

    // Copy the final bitmap created by the out Allocation to the outBitmap
    allOut.copyTo(outBitmap);

    // recycle the original bitmap
    bitmap.recycle();

    // After finishing everything, we destroy the Renderscript.
    rs.destroy();

    return outBitmap;
  }

  public Bitmap myShot(Activity activity) {
    // 获取windows中最顶层的view
    View view = activity.getWindow().getDecorView();
    view.buildDrawingCache();

    // 获取状态栏高度
    Rect rect = new Rect();
    view.getWindowVisibleDisplayFrame(rect);
    int statusBarHeights = rect.top;
    // 获取屏幕宽和高
    int widths = mDm.widthPixels;
    int heights = mDm.heightPixels;

    // 允许当前窗口保存缓存信息
    view.setDrawingCacheEnabled(true);
    Bitmap drawingCache = view.getDrawingCache();
    // 去掉状态栏
    Bitmap bmp =
        Bitmap.createBitmap(drawingCache, 0, statusBarHeights, widths, heights - statusBarHeights);

    // 销毁缓存信息
    view.destroyDrawingCache();

    return bmp;
  }

  private void showToast() {
    //if (mContext instanceof MainActivity) {
    //  SystemFragment fragment = (SystemFragment) ((MainActivity) mContext).adapter.getItem(4);
    //  fragment.mAdapter.upDateNumer(0, "add");
    //}
  }

  @OnClick(R.id.sign) void sign() {
    if (mOnSureClickLister != null) {
      mOnSureClickLister.onSureClick();
    }
  }

  @Override public void dismiss() {
    super.dismiss();
    //处理中奖系统消息通知
    ActionUtil.updateSysMsgStatus(getContext());
  }
}
