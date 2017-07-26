package com.utstar.appstoreapplication.activity.utils;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Environment;
import android.os.StatFs;
import android.text.TextUtils;
import android.text.format.Formatter;
import android.util.Log;
import com.arialyy.frame.util.show.FL;
import com.arialyy.frame.util.show.L;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.UUID;

/**
 * Created by Aria.Lao on 2017/2/24.
 */

public class MacUtil {

  /**
   * 获取机身剩余内存
   */
  public static double getCurStCap(Context context) {
    String curSystemCap = getRomAvailableSize(context);
    String curStCapUnit = curSystemCap.substring(curSystemCap.length() - 2, curSystemCap.length());
    String curStCap = curSystemCap.substring(0, curSystemCap.length() - 2);
    if (!TextUtils.isEmpty(curStCap)) {
      curStCap = curStCap.replaceAll(" ", "");
    }
    double curSysCap = Double.parseDouble(curStCap);
    if (curStCapUnit.equals("GB") || curStCapUnit.equals("G")) {
      curSysCap = curSysCap * 1024;
    }
    return curSysCap;
  }

  //获取可用运存大小
  public static long getAvailMemory(Context context) {
    // 获取android当前可用内存大小
    ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
    ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
    am.getMemoryInfo(mi);
    //mi.availMem; 当前系统的可用内存
    //return Formatter.formatFileSize(context, mi.availMem);// 将获取的内存大小规格化
    //System.out.println("可用内存---->>>" + mi.availMem / (1024 * 1024));
    //FL.d(TAG, "可用内存---->>>" + mi.availMem / (1024 * 1024));
    return mi.availMem / (1024 * 1024);
  }

  //获取总运存大小
  public static long getTotalMemory() {
    String str1 = "/proc/meminfo";// 系统内存信息文件
    String str2;
    String[] arrayOfString;
    long initial_memory = 0;
    try {
      FileReader localFileReader = new FileReader(str1);
      BufferedReader localBufferedReader = new BufferedReader(localFileReader, 8192);
      str2 = localBufferedReader.readLine();// 读取meminfo第一行，系统总内存大小
      arrayOfString = str2.split("\\s+");
      for (String num : arrayOfString) {
        Log.i(str2, num + "\t");
      }
      initial_memory =
          Integer.valueOf(arrayOfString[1]).intValue() * 1024;// 获得系统总内存，单位是KB，乘以1024转换为Byte
      localBufferedReader.close();
    } catch (IOException e) {
      L.e(FL.getExceptionString(e));
    }
    //return Formatter.formatFileSize(context, initial_memory);// Byte转换为KB或者MB，内存大小规格化
    //System.out.println("总运存--->>>" + initial_memory / (1024 * 1024));
    //FL.d(TAG, "总运存--->>>" + initial_memory / (1024 * 1024));
    return initial_memory / (1024 * 1024);
  }

  public static String getLocalMacAddress(Context context) {
    String uuid = "";
    if (TextUtils.isEmpty(uuid)) {
      uuid = getLocalEthernetMacAddress();
      if (TextUtils.isEmpty(uuid)) {
        uuid = getMAc(context);
        if (TextUtils.isEmpty(uuid)) {
          uuid = getSN();
          if (TextUtils.isEmpty(uuid)) {
            uuid = UUID.randomUUID().toString();
          }
        }
      }
    }

    return uuid;
  }

  /**
   * 获得机身可用内存
   */
  private static String getRomAvailableSize(Context context) {
    File path = Environment.getDataDirectory();
    StatFs stat = new StatFs(path.getPath());
    long blockSize = stat.getBlockSize();
    long availableBlocks = stat.getAvailableBlocks();
    return Formatter.formatFileSize(context, blockSize * availableBlocks);
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
