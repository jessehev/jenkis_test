package com.utstar.appstoreapplication.activity.windows.monthly_payment;

import android.content.Context;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.utstar.appstoreapplication.activity.commons.net.BasicDeserializer;
import com.utstar.appstoreapplication.activity.commons.net.HttpCallback;
import com.utstar.appstoreapplication.activity.entity.net_entity.MothlyPaySecondEntity;
import com.utstar.appstoreapplication.activity.interfaces.net_interface.MothlyPayApi;
import com.utstar.appstoreapplication.activity.windows.base.BaseModule;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by lt on 2016/12/20.
 */
public class MothlyPayModule extends BaseModule {
  private static final int MOTHLYPAY_CALL_SUCCESS = 1;
  private final Gson mGson =
      new GsonBuilder().registerTypeAdapter(new TypeToken<MothlyPaySecondEntity>() {
      }.getType(), new BasicDeserializer<MothlyPaySecondEntity>()).create();

  public MothlyPayModule(Context context) {
    super(context);
  }

  public void getData(String mPackageId, int mPage) {
    Map<String, String> param = new HashMap<>();
    param.put("PRODUCTID", mPackageId);
    param.put("number", "10");
    param.put("page", mPage + "");
    mNetManager.request(MothlyPayApi.class, mGson)
        .getMothlyPaySecList(param)
        .compose(new HttpCallback<MothlyPaySecondEntity>() {
          @Override public void onResponse(MothlyPaySecondEntity response) {
            if (response != null) {
              callback(MOTHLYPAY_CALL_SUCCESS, response);
            }
          }

          @Override public void onFailure(Throwable e) {
            super.onFailure(e);
          }
        });
  }
}
