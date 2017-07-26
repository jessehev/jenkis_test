package com.utstar.appstoreapplication.activity.utils;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.text.TextUtils;
import com.arialyy.frame.util.FileUtil;
import com.arialyy.frame.util.StringUtil;
import com.arialyy.frame.util.show.L;
import com.utstar.appstoreapplication.activity.entity.db_entity.EpgEntity;
import com.utstar.appstoreapplication.common.db.UserIDProvider;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.UUID;

/**
 * Created by Aria.Lao on 2017/2/24.
 */

public class LauncherUtil {

  /**
   * 保存用户
   */
  public static void saveAccountAndChannel(Context context, String account) {
    EpgEntity entity = EpgUserUtil.getUserEntity();
    entity.setWbAccount(account);
    EpgUserUtil.saveUserEntity(entity);
    FileWriter fileWriter = null;
    BufferedWriter bufferedWriter = null;
    try {
      // 改为false为覆盖写入
      File file = new File(context.getFilesDir().getPath() + "/GameBar2/userId.txt");
      //File file = new File(
      //    FilePathUtils.getApplicationFolder(getContext().getApplicationContext()).getAbsolutePath()
      //        + "/userId"
      //        + ".txt");
      FileUtil.createFile(file.getPath());
      fileWriter = new FileWriter(file, false);
      bufferedWriter = new BufferedWriter(fileWriter);
      bufferedWriter.write(account);
      // bufferedWriter.newLine();
      bufferedWriter.flush();
      fileWriter.close();
      bufferedWriter.close();
    } catch (IOException e) {
      e.printStackTrace();
    }

    UserIDProvider.DatabaseHelper dbHelper = new UserIDProvider.DatabaseHelper(context);
    SQLiteDatabase db = dbHelper.getWritableDatabase();
    // 清空表数据，再插入
    String channel = StringUtil.getApplicationMetaData(context, "channel");
    db.delete(UserIDProvider.TABLE_NAME, null, null);
    ContentValues values = new ContentValues();
    values.put("_id", 1);
    values.put("tvAccount", account + "," + channel);
    EpgEntity epgEntity = EpgUserUtil.getUserEntity();
    values.put("epgUserId", epgEntity.getEpgUserId());
    values.put("epgToken", epgEntity.getEpgSessionId());
    values.put("epgServer", epgEntity.getEpgServer());
    db.insert(UserIDProvider.TABLE_NAME, null, values);
    db.close();
  }

  public static String getLocalMacAddress(Context context) {
    String uuid = getLocalEthernetMacAddress();
    if (TextUtils.isEmpty(uuid)) {
      uuid = getMAc(context);
      if (TextUtils.isEmpty(uuid)) {
        uuid = getSN();
        if (TextUtils.isEmpty(uuid)) {
          uuid = UUID.randomUUID().toString();
        }
      }
    }

    return uuid;
  }

  public static String getLocalEthernetMacAddress() {
    String mac = null;
    try {
      Enumeration localEnumeration = NetworkInterface.getNetworkInterfaces();

      while (localEnumeration.hasMoreElements()) {
        NetworkInterface localNetworkInterface = (NetworkInterface) localEnumeration.nextElement();
        String interfaceName = localNetworkInterface.getDisplayName();

        if (interfaceName == null) {
          continue;
        }

        if (interfaceName.equals("eth0")) {
          // MACAddr = convertMac(localNetworkInterface
          // .getHardwareAddress());
          mac = convertToMac(localNetworkInterface.getHardwareAddress());
          if (mac.startsWith("0:")) {
            mac = "0" + mac;
          }
          break;
        }
        byte[] address = localNetworkInterface.getHardwareAddress();
        // Log.i("dell--mac--", "mac=" + address.toString());
        for (int i = 0; (address != null && i < address.length); i++) {
          L.i("Debug", String.format("  : %x", address[i]));
        }
      }
    } catch (SocketException e) {
      e.printStackTrace();
    }
    return mac;
  }

  private static String getSN() {
    String ret;
    try {
      Method systemProperties_get =
          Class.forName("android.os.SystemProperties").getMethod("get", String.class);
      if ((ret = (String) systemProperties_get.invoke(null, "ro.serialno")) != null) return ret;
    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }
    return "";
  }

  private static String convertToMac(byte[] mac) {
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < mac.length; i++) {
      byte b = mac[i];
      int value = 0;
      if (b >= 0 && b <= 16) {
        value = b;
        sb.append("0").append(Integer.toHexString(value));
      } else if (b > 16) {
        value = b;
        sb.append(Integer.toHexString(value));
      } else {
        value = 256 + b;
        sb.append(Integer.toHexString(value));
      }
      if (i != mac.length - 1) {
        sb.append(":");
      }
    }
    return sb.toString();
  }

  @SuppressLint("HardwareIds") private static String getMAc(Context context) {
    WifiManager wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
    WifiInfo info = wifi.getConnectionInfo();
    if (info != null) {
      return info.getMacAddress();
    } else {
      return null;
    }
  }
}
