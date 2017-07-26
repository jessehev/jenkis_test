package com.utstar.appstoreapplication.activity.windows.activity.discount_package;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v17.leanback.widget.HorizontalGridView;
import butterknife.Bind;
import com.arialyy.frame.util.show.T;
import com.ut.wb.ui.adapter.RvItemClickSupport;
import com.utstar.appstoreapplication.activity.R;
import com.utstar.appstoreapplication.activity.databinding.FragmentGamePackageDialogBinding;
import com.utstar.appstoreapplication.activity.entity.net_entity.HalfPackageEntity;
import com.utstar.appstoreapplication.activity.manager.TurnManager;
import com.utstar.appstoreapplication.activity.windows.base.BaseFragment;
import com.utstar.appstoreapplication.activity.windows.payment.OrderInfoActivity;
import com.utstar.appstoreapplication.activity.windows.payment.OrderInfoModule;
import com.utstar.appstoreapplication.activity.windows.payment.PayWebView;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by JesseHev on 2017/3/31.
 * 套餐包优惠活动（半价优惠、破冰活动）
 */
@SuppressLint("ValidFragment") public class GamePackageDialogFragment
    extends BaseFragment<FragmentGamePackageDialogBinding> {

  @Bind(R.id.grid) HorizontalGridView mGrid;
  private List<HalfPackageEntity.GamePackage> mData = new ArrayList<>();
  private GamePackageDialogAdapater mAdapater;
  private GamePackageDialog mDailog;

  //活动类型 0 半价优惠 1破冰活动
  private int mType = 0;
  private int mPosition;

  //购买成功
  private boolean mSuccess;

  public GamePackageDialogFragment() {
    super();
  }

  public GamePackageDialogFragment(GamePackageDialog dialog,
      List<HalfPackageEntity.GamePackage> list) {
    super();
    mDailog = dialog;
    mData.clear();
    mData.addAll(list);
  }

  public GamePackageDialogFragment(GamePackageDialog dialog,
      List<HalfPackageEntity.GamePackage> list, int type) {
    super();
    mDailog = dialog;
    mData.clear();
    mData.addAll(list);
    mType = type;
  }

  @Override protected void init(Bundle savedInstanceState) {
    mAdapater = new GamePackageDialogAdapater(mData, mType);
    mGrid.setAdapter(mAdapater);
    RvItemClickSupport.addTo(mGrid).setOnItemClickListener((recyclerView, position, v) -> {
      if (mType == 0) {
        TurnManager.getInstance().turnOrderDetail(getContext(), 0, mData.get(position).packageId);
      } else if (mType == 1) {
        //购买
        mPosition = position;
        GamePackageMsgDialog gmDialog =
            new GamePackageMsgDialog(getContext(), mData.get(position).packgeName);
        gmDialog.setGamePackageMsgDialog(
            () -> getModule(OrderInfoModule.class).getDateBuy(0, mData.get(position).packageId, 0,
                false));
        gmDialog.show();
      }
    });

    IntentFilter filter = new IntentFilter(PayWebView.KEY_PAY_FILTER);
    filter.addDataScheme(getContext().getPackageName());
    getContext().registerReceiver(mReceiver, filter);
  }

  @Override protected int setLayoutId() {
    return R.layout.fragment_game_package_dialog;
  }

  public boolean getSuccess() {
    return mSuccess;
  }

  private BroadcastReceiver mReceiver = new BroadcastReceiver() {
    @Override public void onReceive(Context context, Intent intent) {
      if (intent != null) {
        mSuccess = intent.getBooleanExtra(TurnManager.PAY_SUCCESS_KEY, false);
        if (mSuccess) {
          switch (mType) {
            case 0:
              if (mDailog != null && mDailog.getDialog() != null && mDailog.getDialog()
                  .isShowing()) {
                mDailog.getDialog().dismiss();
              }
              break;
            case 1:
              if (mDailog != null && mDailog.getDialog() != null && mDailog.getDialog()
                  .isShowing()) {
                mDailog.getDialog().dismiss();
              }
              TurnManager.getInstance()
                  .turnPackage(getContext(), mData.get(mPosition).packageId, 0);
              break;
          }
        }
      }
    }
  };

  @Override public void onDestroy() {
    super.onDestroy();
    getContext().unregisterReceiver(mReceiver);
  }
}
