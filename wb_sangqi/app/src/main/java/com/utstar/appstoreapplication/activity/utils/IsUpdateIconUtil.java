package com.utstar.appstoreapplication.activity.utils;

import android.content.Intent;
import com.utstar.appstoreapplication.activity.entity.common_entity.UpdateIconEntity;
import com.utstar.appstoreapplication.activity.manager.TurnManager;

/**
 * Created by Aria.Lao on 2017/2/14.
 * 刷新图片判断工具
 */
public class IsUpdateIconUtil {

  //public static UpdateIconEntity createResultEntity(){
  //
  //}

  public static boolean isUpdateIcon(Intent intent) {
    UpdateIconEntity entity = intent.getParcelableExtra(TurnManager.UPDATE_ICON_KEY);
    if (entity == null) {
      return intent.getBooleanExtra(TurnManager.PAY_SUCCESS_KEY, false) || intent.getBooleanExtra(
          TurnManager.UPDATE_KEY, false);
    }
    if (entity.isBuySuccess || !entity.isAuthSuccess) {
      return true;
    }
    return false;
  }

  public static boolean isBuySuccess(Intent intent) {
    return intent.getBooleanExtra(TurnManager.PAY_SUCCESS_KEY, false);
  }
}
