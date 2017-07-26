package com.utstar.appstoreapplication.activity.utils;

/**
 * Created by JesseHev on 2017/4/10.
 */

public class PageUtil {
  public static  int getPage(double totalSizi, int pageSize) {
    return (int) Math.ceil(totalSizi / pageSize);
  }
}
