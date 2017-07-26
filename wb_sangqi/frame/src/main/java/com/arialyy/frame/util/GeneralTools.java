package com.arialyy.frame.util;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Environment;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.Random;

/**
 * Created by Richard
 */
public class GeneralTools {

  /**
   * 获取应用本地路径
   */
  public static File getAppDir() {
    File storyageDir = Environment.getExternalStorageDirectory();
    File cacheDir = new File(storyageDir, "SMC");
    if (!cacheDir.exists()) {
      cacheDir.mkdir();
    }
    return cacheDir;
  }

  /**
   * 返回WIFI的IP与MAC
   *
   * @return IP与MAC的数组，下标0为IP，下标1为MAC
   */
  public static String[] getWifiIpAndMac(Context context) {
    WifiManager wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
    WifiInfo info = wifi.getConnectionInfo();
    int ipValue = info.getIpAddress();
    String ip = getIpString(ipValue);
    String mac = info.getMacAddress();
    String[] result = new String[2];
    result[0] = ip;
    result[1] = mac;
    return result;
  }

  public static String getIpString(int ipValue) {
    int value1 = ipValue & 0xff;
    int value2 = (ipValue >> 8) & 0xff;
    int value3 = (ipValue >> 16) & 0xff;
    int value4 = (ipValue >> 24) & 0xff;
    StringBuilder sb = new StringBuilder();
    sb.append(value1)
        .append(".")
        .append(value2)
        .append(".")
        .append(value3)
        .append(".")
        .append(value4)
        .append(".");
    return sb.toString();
  }

  /**
   * 返回WIFI的IP与MAC
   *
   * @return IP与MAC的数组，下标0为IP，下标1为MAC
   * @throws SocketException
   */
  public static String[] getIpAndMac() throws SocketException {
    return getIpAndMac("eth0");
  }

  /**
   * 返回WIFI的IP与MAC
   *
   * @param networkName 网卡名
   * @return 返回WIFI的IP与MAC
   * @throws SocketException
   */
  public static String[] getIpAndMac(String networkName) throws SocketException {
    NetworkInterface networkInterface = NetworkInterface.getByName(networkName);
    if (networkInterface != null) {
      return getIpAndMac(networkInterface);
    } else {
      return null;
    }
    //
    //        String[] result = null;
    //        Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
    //        while (networkInterfaces.hasMoreElements()) {
    //            networkInterface = networkInterfaces.nextElement();
    //            String name = networkInterface.getName();
    //            if (name.equals(networkName)) {
    //                return getIpAndMac(networkInterface);
    //            } else if (networkInterface.isUp() && result == null) {
    //                result = getIpAndMac(networkInterface);
    //            }
    //        }
    //        return result;
  }

  /**
   * 获取指定网卡的IP与MAC
   *
   * @throws SocketException
   */
  public static String[] getIpAndMac(NetworkInterface networkInterface) throws SocketException {
    String ip = getIp(networkInterface);
    String mac = getMac(networkInterface);
    String[] result = new String[2];
    result[0] = ip;
    result[1] = mac;
    return result;
  }

  private static String getIp(NetworkInterface networkInterface) {
    Enumeration<InetAddress> addresses = networkInterface.getInetAddresses();
    while (addresses.hasMoreElements()) {
      InetAddress address = addresses.nextElement();
      if (address instanceof Inet4Address) {
        return address.getHostAddress();
      }
    }
    return null;
  }

  private static String getMac(NetworkInterface networkInterface) throws SocketException {
    byte[] macByte = networkInterface.getHardwareAddress();
    if (macByte == null) {
      return null;
    }
    StringBuilder sb = new StringBuilder();
    int len = macByte.length;
    for (int i = 0; i < len; i++) {
      int value = macByte[i] & 0xff;
      String hexValue = Integer.toHexString(value);
      if (value < 16) {
        sb.append("0");
      }
      sb.append(hexValue);
      if (i < (len - 1)) {
        sb.append(":");
      }
    }
    return sb.toString();
  }

  /**
   * 转换成十六进制字符串
   */
  public static String byte2hex(byte[] b) {
    String[] HEX_LETTERS = new String[] {
        "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "A", "B", "C", "D", "E", "F"
    };
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < b.length; i++) {
      int value = (b[i] >> 4) & 0x0f;
      sb.append(HEX_LETTERS[value]);
      value = b[i] & 0x0f;
      sb.append(HEX_LETTERS[value]);
    }
    return sb.toString();
  }

  public static int hex2Int(String value) throws Exception {
    if (value.length() > 8) {
      throw new Exception("无法转换(" + value + ")为整形");
    }
    int len = value.length();
    int tmpIntValue = 0;
    for (int i = 0; i < len; i++) {
      char c = value.charAt(i);
      if ('a' == c || 'A' == c) {
        tmpIntValue = (tmpIntValue | 10);
      } else if ('b' == c || 'B' == c) {
        tmpIntValue = (tmpIntValue | 11);
      } else if ('c' == c || 'C' == c) {
        tmpIntValue = (tmpIntValue | 12);
      } else if ('d' == c || 'D' == c) {
        tmpIntValue = (tmpIntValue | 13);
      } else if ('e' == c || 'E' == c) {
        tmpIntValue = (tmpIntValue | 14);
      } else if ('f' == c || 'F' == c) {
        tmpIntValue = (tmpIntValue | 15);
      }
    }
    return tmpIntValue;
  }

  //public static String getAuthenticator(String password, String encryToken, String userName,
  //    String stbId, String ip, String mac) throws UnsupportedEncodingException {
  //  long randomValue = new Random(99999999).nextLong();
  //  String randomStr = Long.toString(randomValue);
  //  for (int i = randomStr.length(); i < 8; i++) {
  //    randomStr = "0" + randomStr;
  //  }
  //
  //  String value = randomStr
  //      + "$"
  //      + encryToken
  //      + "$"
  //      + userName
  //      + "$"
  //      + stbId
  //      + "$"
  //      + ip
  //      + "$"
  //      + mac
  //      + "$Reserved$CTC";
  //  for (int i = password.length(); i < 24; i++) {
  //    password += "0";
  //  }
  //
  //  byte[] input = DES.encrypt(password.getBytes("ASCII"), value.getBytes(), DES.ALGORITHM_TRIPLE);
  //  return byte2hex(input);
  //}
}
