package com.utstar.appstoreapplication.activity.windows.system;

import android.content.Context;
import android.databinding.ViewDataBinding;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.utstar.appstoreapplication.activity.commons.net.BasicDeserializer;
import com.utstar.appstoreapplication.activity.commons.net.HttpCallback;
import com.utstar.appstoreapplication.activity.entity.net_entity.SysMessageEntity;
import com.utstar.appstoreapplication.activity.entity.net_entity.SysMessageListEntity;
import com.utstar.appstoreapplication.activity.interfaces.net_interface.SysMsgApi;
import com.utstar.appstoreapplication.activity.manager.NetManager;
import com.utstar.appstoreapplication.activity.windows.base.BaseModule;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by JesseHev on 2016/12/9.
 */
public class SystemModule extends BaseModule<ViewDataBinding> {
  private static final int SYS_MSG_CALL_SUCCESS = 1101;
  static final int SYS_MSGS_CALL_SUCCESS = 0X1101;
  static final int SYS_DETAIL_CALL_SUCCESS = 0X1100;

  public SystemModule(Context context) {
    super(context);
  }

  public void getSysMsgTotal() {
    Map<String, String> params = new HashMap<>();
    params.put("number", "10");
    params.put("page", "1");
    Gson gson = new GsonBuilder().registerTypeAdapter(new TypeToken<SysMessageListEntity>() {
    }.getType(), new BasicDeserializer<SysMessageListEntity>()).create();
    NetManager.getInstance()
        .request(SysMsgApi.class, gson)
        .getSysMsgList(params)
        .compose(new HttpCallback<SysMessageListEntity>() {
          @Override public void onResponse(SysMessageListEntity result) {
            if (result != null && result.getSize() > 0) {
              callback(SYS_MSG_CALL_SUCCESS, result);
            }
          }

          @Override public void onFailure(Throwable e) {
            super.onFailure(e);
          }
        });
  }

  /**
   * 站内信数据
   */
  public void getSysList(int page) {
    Map<String, String> params = new HashMap<>();
    params.put("number", "10");
    params.put("page", page + "");
    Gson gson = new GsonBuilder().registerTypeAdapter(new TypeToken<SysMessageListEntity>() {
    }.getType(), new BasicDeserializer<SysMessageListEntity>()).create();
    mNetManager.request(SysMsgApi.class, gson)
        .getSysMsgList(params)
        .compose(new HttpCallback<SysMessageListEntity>() {
          @Override public void onResponse(SysMessageListEntity result) {
            callback(SYS_MSGS_CALL_SUCCESS, result);
          }
        });
  }

  /**
   * 站内信详细
   */
  public void showDetail(SysMessageEntity model) {
    Map<String, String> param = new HashMap<>();
    param.put("messageid", model.getMessageid() + "");
    param.put("flag", model.getFlag() + "");
    Gson gson = new GsonBuilder().registerTypeAdapter(new TypeToken<SysMessageEntity>() {
    }.getType(), new BasicDeserializer<SysMessageEntity>()).create();
    mNetManager.getInstance()
        .request(SysMsgApi.class, gson)
        .getSysMsgDetail(param)
        .compose(new HttpCallback<SysMessageEntity>() {
          @Override public void onResponse(SysMessageEntity result) {
            callback(SYS_DETAIL_CALL_SUCCESS, result);
          }

          @Override public void onFailure(Throwable e) {
            super.onFailure(e);
          }
        });
  }
}
