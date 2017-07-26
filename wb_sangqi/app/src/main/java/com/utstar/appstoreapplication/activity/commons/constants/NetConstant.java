package com.utstar.appstoreapplication.activity.commons.constants;

import android.text.TextUtils;
import com.arialyy.frame.util.StringUtil;
import com.arialyy.frame.util.show.FL;
import com.utstar.appstoreapplication.activity.BuildConfig;
import com.utstar.appstoreapplication.activity.tinker.BaseApp;
import java.io.IOException;
import java.util.Properties;

/**
 * Created by “Aria.Lao” on 2016/10/25.
 */
public class NetConstant {
  private static final String TEMP = BuildConfig.DEBUG ? "http://192.168.5.3:8080/wbManager/"
      : "http://192.168.5.156:80/wbManager/";
  //public static String BASE_URL = TEMP;
  public static String BASE_URL = "http://192.168.5.3:8080/wbManager/";

  /**
   * 读取 assets/ips.properties文件中的ip
   * modify by Aria.Lao
   */
  static {
    try {
      Properties p = StringUtil.loadConfig(BaseApp.application.getAssets().open("ips.properties"));
      BASE_URL = p.getProperty("cd_test_ip", TEMP);
      //BASE_URL = p.getProperty("hb_test_ip", TEMP);
      //BASE_URL = p.getProperty("hb_ys_ip", TEMP);
      //BASE_URL = p.getProperty("mq", TEMP);
      //BASE_URL = p.getProperty("jian_shu", TEMP);
      //BASE_URL = p.getProperty("hb_release_ip", TEMP);
      //BASE_URL = p.getProperty("dev_ip2", TEMP);
      //BASE_URL = p.getProperty("zzz", TEMP);
    } catch (IOException e) {
      FL.e("NetConstant", FL.getExceptionString(e));
    } finally {
      if (TextUtils.isEmpty(BASE_URL)) {
        BASE_URL = TEMP;
      }
    }
    //BASE_URL = "http://192.168.5.3:8080/wbManager/";
  }
}
