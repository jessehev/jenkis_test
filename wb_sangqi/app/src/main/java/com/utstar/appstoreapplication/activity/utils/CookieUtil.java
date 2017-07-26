package com.utstar.appstoreapplication.activity.utils;

/**
 * Created by “Aria.Lao” on 2016/9/9.
 */
public class CookieUtil {
  public static final String C_sSession_Share = "SessionShare";
  public static final String C_sSession_Key = "C_sSession_Key";

  public static synchronized String getCookies() {
    //String cookies = HttpRequestExecutor.cookies;
    //if (TextUtils.isEmpty(cookies)){
    //    L.e("CookieUtil", "cookie 为null，即将从缓存读cookie");
    //    CacheUtil util = new CacheUtil(GameStopApplication.getInstance(), false);
    //    cookies = util.getStringCache(HttpRequestExecutor.COOKIE_KEY);
    //    util.close();
    //}
    //return cookies;
    return "";
  }
}
