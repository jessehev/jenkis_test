package com.utstar.appstoreapplication.activity.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * lt
 * 站内信-时间工具内
 */
public class TimeUtil {
  public static String getTimeStamp(long nowTime) {
    String timeStamp = "";
    SimpleDateFormat format = null;
    Calendar today = Calendar.getInstance();
    today.set(today.get(Calendar.YEAR), today.get(Calendar.MONTH), today.get(Calendar.DAY_OF_MONTH),
        0, 0, 0);
    long todayTime = today.getTime().getTime();
    long todayTime2 = todayTime + 12 * 3600 * 1000;
    if (nowTime >= todayTime2)//下午
    {
      format = new SimpleDateFormat("h:mm");
      timeStamp = format.format(new Date(nowTime)) + " PM";
    } else if (nowTime < todayTime2 && nowTime >= todayTime) {
      format = new SimpleDateFormat("h:mm");
      timeStamp = format.format(new Date(nowTime)) + " AM";
    } else {
      format = new SimpleDateFormat("M月d号");
      timeStamp = format.format(new Date(nowTime));
    }
    return timeStamp;
  }

  public static String getTimeStamp(String nowTime) {
    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    Calendar today = Calendar.getInstance();
    //2016-12-29 00:00:00
    //2017-01-02 00:00:00
    today.set(today.get(Calendar.YEAR), today.get(Calendar.MONTH), today.get(Calendar.DAY_OF_MONTH),
        0, 0, 0);
    long todayTime = today.getTime().getTime();    //今天凌晨
    long time = 0;
    String timeStamp = "";
    try {
      time = format.parse(nowTime).getTime();
      if (time >= todayTime) {//今天
        format = new SimpleDateFormat("HH:mm");
        timeStamp = format.format(new Date(time));
      } else if (time >= todayTime - 24 * 3600 * 1000 && time < todayTime) {  //昨天
        timeStamp = "昨天";
      } else if (time >= todayTime - 24 * 3600 * 1000 * 2
          && time < todayTime - 24 * 3600 * 1000) {// 二天前
        timeStamp = "2天前";
      } else if (time >= todayTime - 24 * 3600 * 1000 * 3
          && time < todayTime - 24 * 3600 * 1000 * 2) { //三天前
        timeStamp = "3天前";
      } else if (time >= todayTime - 24 * 3600 * 1000 * 4
          && time < todayTime - 24 * 3600 * 1000 * 3) { //四天前
        timeStamp = "4天前";
      } else if (time >= todayTime - 24 * 3600 * 1000 * 5
          && time < todayTime - 24 * 3600 * 1000 * 4) {   //五天前
        timeStamp = "5天前";
      } else if (time >= todayTime - 24 * 3600 * 1000 * 6
          && time < todayTime - 24 * 3600 * 1000 * 5) {  //六天前
        timeStamp = "6天前";
      } else if (time >= todayTime - 24 * 3600 * 1000 * 7
          && time < todayTime - 24 * 3600 * 1000 * 6) { //七天前
        timeStamp = "7天前";
      } else {
        format = new SimpleDateFormat("M月d日");
        timeStamp = format.format(new Date(time));
      }
    } catch (ParseException e) {
      return nowTime;
    }
    return timeStamp;
  }
}
