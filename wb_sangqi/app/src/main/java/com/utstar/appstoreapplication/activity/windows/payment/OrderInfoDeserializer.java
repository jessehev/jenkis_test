package com.utstar.appstoreapplication.activity.windows.payment;

import com.google.gson.Gson;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.utstar.appstoreapplication.activity.entity.net_entity.OrderInfoEntity;
import com.utstar.appstoreapplication.activity.utils.JsonCodeAnalysisUtil;
import java.lang.reflect.Type;

/**
 * Created by “Aria.Lao” on 2016/10/26.
 * 游戏详情数据解析
 */
final class OrderInfoDeserializer implements JsonDeserializer<OrderInfoEntity> {
  @Override public OrderInfoEntity deserialize(JsonElement element, Type typeOfT,
      JsonDeserializationContext context) throws JsonParseException {
    JsonObject root = element.getAsJsonObject();
    if (JsonCodeAnalysisUtil.isSuccess(root)) {
      return new Gson().fromJson(root.get("object").getAsJsonObject().get("list"), typeOfT);
    } else {
      throw new IllegalStateException(root.get("rltmsg").getAsString());
    }
  }
}