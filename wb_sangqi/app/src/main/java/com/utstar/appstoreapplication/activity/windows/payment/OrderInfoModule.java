package com.utstar.appstoreapplication.activity.windows.payment;

import android.content.Context;
import android.databinding.ViewDataBinding;
import android.text.TextUtils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.utstar.appstoreapplication.activity.commons.net.BasicDeserializer;
import com.utstar.appstoreapplication.activity.commons.net.HttpCallback;
import com.utstar.appstoreapplication.activity.entity.common_entity.NetObjectEntity;
import com.utstar.appstoreapplication.activity.entity.net_entity.OrderInfoEntity;
import com.utstar.appstoreapplication.activity.interfaces.net_interface.OrderDetailApi;
import com.utstar.appstoreapplication.activity.manager.TurnManager;
import com.utstar.appstoreapplication.activity.utils.EpgUserUtil;
import com.utstar.appstoreapplication.activity.windows.base.BaseModule;
import java.util.Map;
import java.util.WeakHashMap;

/**
 * Created by lt on 2017/1/12.
 */

public class OrderInfoModule extends BaseModule<ViewDataBinding> {
  static final int ORDERINFO_CALL_SUCCESS = 0xaac;
  static final int ORDERINFO_CALL_BUY_SUCCESS = 0xddc;

  public OrderInfoModule(Context context) {
    super(context);
  }

  public void getDateInfo(int mType, String mId, int mGameId) {
    Gson gson = new GsonBuilder().registerTypeAdapter(new TypeToken<OrderInfoEntity>() {
    }.getType(), new OrderInfoDeserializer()).create();
    Map<String, String> params = new WeakHashMap<>();
    params.put("PRODUCTID", mId);
    params.put("type", mType + "");
    params.put("gameId", mGameId + "");
    mNetManager.request(OrderDetailApi.class, gson)
        .getOrderDetail(params)
        .compose(new HttpCallback<OrderInfoEntity>() {
          @Override public void onResponse(OrderInfoEntity response) {
            if (response != null) {
              callback(ORDERINFO_CALL_SUCCESS, response);
            }
          }

          @Override public void onFailure(Throwable e) {
            super.onFailure(e);
          }
        });
  }

  String convert(String span) {
    String color = span.replaceAll("style=\"color:", "color=\"");
    color = color.replace(";", "");
    color = color.replace("span", "font");
    return color;
  }

 public void getDateBuy(int type, String productId, int gameId, boolean isThirdPay) {
    final Gson mGson =
        new GsonBuilder().registerTypeAdapter(new TypeToken<NetObjectEntity<String>>() {
        }.getType(), new BasicDeserializer<NetObjectEntity<String>>()).create();
    mNetManager.request(OrderDetailApi.class, mGson)
        .getOrderUrl(type, EpgUserUtil.getUserEntity().getEpgUserId(), productId, 0, gameId,
            !isThirdPay ? 1 : 2)
        .compose(new HttpCallback<NetObjectEntity<String>>() {
          @Override public void onResponse(NetObjectEntity<String> response) {
            if (!TextUtils.isEmpty(response.obj)) {
              TurnManager.getInstance().turnPayWeb(getContext(), response.obj);
            }
          }

          @Override public void onFailure(Throwable e) {
            super.onFailure(e);
          }
        });
  }
}
