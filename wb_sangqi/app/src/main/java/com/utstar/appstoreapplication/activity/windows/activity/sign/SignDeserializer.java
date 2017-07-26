package com.utstar.appstoreapplication.activity.windows.activity.sign;

import com.google.gson.Gson;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.utstar.appstoreapplication.activity.utils.JsonCodeAnalysisUtil;
import java.lang.reflect.Type;

/**
 * 签到Gson描述
 *
 * Created by JesseHev on 2017/1/19.
 *
 * @param <T> 服务器数据实体
 */
public class SignDeserializer<T> implements JsonDeserializer<T> {
  @Override
  public T deserialize(JsonElement element, Type typeOfT, JsonDeserializationContext context)
      throws JsonParseException {
    JsonObject root = element.getAsJsonObject();
    if (JsonCodeAnalysisUtil.isSuccess(root)) {
      return new Gson().fromJson(root.get("object"), typeOfT);
    } else {
      throw new IllegalStateException(root.get("rltmsg").getAsString());
    }
  }
}