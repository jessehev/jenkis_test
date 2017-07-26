package com.utstar.appstoreapplication.activity.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

/**
 * Created by Aria.Lao on 2017/4/6.
 */

public class CommonUtil {

  public static Intent setData(Context context, Intent intent) {
    Uri.Builder builder = new Uri.Builder();
    builder.scheme(context.getPackageName());
    Uri uri = builder.build();
    intent.setData(uri);
    return intent;
  }
}
