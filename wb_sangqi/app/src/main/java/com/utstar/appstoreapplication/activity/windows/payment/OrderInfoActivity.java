package com.utstar.appstoreapplication.activity.windows.payment;

import android.app.LauncherActivity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v17.leanback.widget.VerticalGridView;
import android.text.Html;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import butterknife.Bind;
import com.arialyy.frame.util.show.FL;
import com.arialyy.frame.util.show.L;
import com.ut_sdk.pay.IWBPay;
import com.ut_sdk.pay.PayEntity;
import com.utstar.appstoreapplication.activity.R;
import com.utstar.appstoreapplication.activity.commons.constants.CommonConstant;
import com.utstar.appstoreapplication.activity.databinding.ActivityOrderInfoBinding;
import com.utstar.appstoreapplication.activity.entity.net_entity.OrderInfoEntity;
import com.utstar.appstoreapplication.activity.manager.AnimManager;
import com.utstar.appstoreapplication.activity.manager.TurnManager;
import com.utstar.appstoreapplication.activity.utils.EpgUserUtil;
import com.utstar.appstoreapplication.activity.utils.JsonCodeAnalysisUtil;
import com.utstar.appstoreapplication.activity.windows.base.BaseActivity;

/**
 * Created by JesseHev on 2017/5/10.
 * 游戏购买信息展示页
 */
public class OrderInfoActivity extends BaseActivity<ActivityOrderInfoBinding>
    implements OrderInfoAdapater.onBtnChangeListener {

  @Bind(R.id.gird_order) VerticalGridView mGrid;
  @Bind(R.id.hint) TextView mHint;
  @Bind(R.id.back) Button mBack;
  private OrderInfoAdapater mAdapater;
  private OrderInfoEntity mEntity;

  /**
   * type == 0 为 产品ID，type ==1 为游戏ID；
   */
  private String mId;
  private int mGameId;
  /**
   * type == 0 为 产品ID，type ==1 为游戏ID；
   */
  public static final String ID = "ID";
  public static final String TYPE = "TYPE";
  public static final String GAMAID = "GAMAID";
  public static final String CP_SERVICE_ID = "CP_SERVICE_ID";
  public static final String CP_CONTENT_ID = "CP_CONTENT_ID";
  public static final String CP_SP_ID = "CP_SP_ID";
  public static final String CP_PACKAGE_NAME = "CP_PACKAGE_NAME";
  public static final String CP_FLOW_CODE = "CP_FLOW_CODE";

  /**
   * CP 支付参数
   */
  private String mCpServiceId, mCpContentId, mCpSpId, mCpPackageName, mCpFlowCode;
  private ServiceConnection mConnection;
  private int mType;
  //记录按钮位置
  private int mPosition;
  private boolean isThirdPay = false;

  private BroadcastReceiver mReceiver = new BroadcastReceiver() {
    @Override public void onReceive(Context context, Intent intent) {
      if (intent != null) {
        boolean success = intent.getBooleanExtra(TurnManager.PAY_SUCCESS_KEY, false);
        sendSuccessInfo2ThirdCp(success, intent);
        if (success) {
          OrderInfoActivity.this.setResult(TurnManager.PAY_RESULT_CODE, intent);
          finish();
        }
      }
    }
  };

  @Override protected int setLayoutId() {
    return R.layout.activity_order_info;
  }

  @Override protected void init(Bundle savedInstanceState) {
    super.init(savedInstanceState);

    getIntentData();
    getThirdPayParams(getIntent());
    if (TextUtils.isEmpty(mId) || mType == -1) {
      FL.e(TAG, "id错误或type错误");
      finish();
      return;
    }
    //请求数据
    if (!isThirdPay) {
      getModule(OrderInfoModule.class).getDateInfo(mType, mId, mGameId);
    }

    mBack.setOnFocusChangeListener((view, b) -> {
      if (b) {
        AnimManager.getInstance().enlarge(view, 1.15f);
        mHint.setText("");
      } else {
        AnimManager.getInstance().narrow(view, 1.0f);
      }
    });
  }

  @Override protected void onResume() {
    super.onResume();
    IntentFilter filter = new IntentFilter(PayWebView.KEY_PAY_FILTER);
    filter.addDataScheme(getPackageName());
    registerReceiver(mReceiver, filter);
  }

  /**
   * 获取Intent参数
   */
  public void getIntentData() {
    mId = getIntent().getStringExtra(ID);
    mType = getIntent().getIntExtra(TYPE, -1);
    mGameId = getIntent().getIntExtra(GAMAID, 0);
  }

  /**
   * 获取第三方支付需要的参数
   */
  private void getThirdPayParams(Intent intent) {
    mCpServiceId = intent.getStringExtra(CP_SERVICE_ID);
    mCpContentId = intent.getStringExtra(CP_CONTENT_ID);
    mCpSpId = intent.getStringExtra(CP_SP_ID);
    mCpPackageName = intent.getStringExtra(CP_PACKAGE_NAME);
    mCpFlowCode = intent.getStringExtra(CP_FLOW_CODE);
    if (!TextUtils.isEmpty(mCpPackageName)) {
      isThirdPay = true;
      JsonCodeAnalysisUtil.reLogin(new JsonCodeAnalysisUtil.LoginCallback() {
        @Override public void onSuccess() {
          getModule(OrderInfoModule.class).getDateInfo(mType, mId, mGameId);
        }

        @Override public void onFail() {
          getModule(OrderInfoModule.class).getDateInfo(mType, mId, mGameId);
        }
      });
    }
  }

  /**
   * 发送订购成功信息给第三方CP
   */
  private void sendSuccessInfo2ThirdCp(boolean success, Intent intent) {
    final PayEntity extra = new PayEntity();
    extra.serviceId = mCpServiceId;
    extra.contentId = mCpContentId;
    extra.productId = mId;
    extra.spId = mCpSpId;
    extra.flowCode = mCpFlowCode;
    extra.errorCode = intent.getStringExtra(PayWebView.KEY_ERROR_CODE);
    extra.orderCode = intent.getStringExtra(PayWebView.KEY_ORDER_CODE);
    extra.epgUserId = EpgUserUtil.getUserEntity().getEpgUserId();
    extra.payType = CommonConstant.WB_PAY_TYPE_WB;
    mConnection = new ServiceConnection() {
      IWBPay iThirdPay = null;

      @Override public void onServiceConnected(ComponentName name, IBinder service) {
        iThirdPay = IWBPay.Stub.asInterface(service);
        try {
          iThirdPay.wbPay(success, extra);
          getApplication().unbindService(mConnection);
        } catch (RemoteException e) {
          FL.e(TAG, FL.getExceptionString(e));
        }
      }

      @Override public void onServiceDisconnected(ComponentName name) {
        L.e(TAG, "Service has unexpectedly disconnected");
        iThirdPay = null;
      }
    };

    Intent aidlIntent = new Intent();
    aidlIntent.setAction(CommonConstant.THIRD_PAY_SERVICE_ACTION);
    aidlIntent.setPackage(mCpPackageName);
    getApplication().bindService(aidlIntent, mConnection, Context.BIND_AUTO_CREATE);
  }

  @Override protected void dataCallback(int result, Object data) {
    super.dataCallback(result, data);
    if (result == OrderInfoModule.ORDERINFO_CALL_SUCCESS) {
      mEntity = (OrderInfoEntity) data;
      mAdapater = new OrderInfoAdapater(this, mEntity.getInfos());
      mGrid.setAdapter(mAdapater);
      mAdapater.setOnBtnChangeListener(this);
      mGrid.requestFocus();
      mGrid.setSelectedPosition(0);
    }
  }

  @Override protected void onDestroy() {
    super.onDestroy();
    if (isThirdPay) {
      Intent intent = new Intent();
      intent.putExtra(PayWebView.KEY_ERROR_CODE, "-1");
      intent.putExtra(PayWebView.KEY_ORDER_CODE, "");
      sendSuccessInfo2ThirdCp(false, intent);
    }
    unregisterReceiver(mReceiver);
  }

  /**
   * 刷新消费提示
   */
  @Override public void changeUI(int position) {
    mPosition = position;
    mHint.setText(Html.fromHtml(
        getModule(OrderInfoModule.class).convert(mEntity.getInfos().get(position).getOrderInfo())));
  }

  /**
   * 购买
   */
  @Override public void buy(int position) {
    getModule(OrderInfoModule.class).getDateBuy(mType,
        mEntity.getInfos().get(position).getPackageId(), mGameId, isThirdPay);
  }

  /**
   * 返回
   */
  public void back(View v) {
    finish();
  }

  @Override public boolean onKeyDown(int keyCode, KeyEvent event) {
    if (event.getAction() == KeyEvent.ACTION_DOWN) {
      if (keyCode == KeyEvent.KEYCODE_DPAD_UP && mPosition == 0) {
        mBack.requestFocus();
      }
    }
    return super.onKeyDown(keyCode, event);
  }
}
