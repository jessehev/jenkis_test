package com.utstar.appstoreapplication.activity.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import android.util.Log;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;

/**
 * Created by Richard
 */
public class LogcatService extends Service {

  @Override public int onStartCommand(Intent intent, int flags, int startId) {
    new Thread() {
      @Override public void run() {
        try {
          Log.d("=====", "================================start===========================");
          Runtime runtime = Runtime.getRuntime();
          runtime.exec("logcat -c");
          Process process = runtime.exec("logcat -f /mnt/sdcard/wanba_log.log");
          //printCMD(process);
          process.waitFor();
          Log.d("=====", "================================end===========================");
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    }.start();

    return super.onStartCommand(intent, flags, startId);
  }

  @Nullable @Override public IBinder onBind(Intent intent) {
    return null;
  }

  public static void printCMD(Process process) {
    try {
      InputStream errorStream = process.getErrorStream();
      System.out.println("---------errorStream---------");
      printStream(errorStream);
    } catch (IOException e) {
      e.printStackTrace();
    }
    try {
      InputStream inputStream = process.getInputStream();
      System.out.println("---------inputStream---------");
      printStream(inputStream);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public static void printStream(InputStream in) throws IOException {
    if (in == null) {
      return;
    }
    //ByteArrayOutputStream cache = new ByteArrayOutputStream();
    FileOutputStream out = new FileOutputStream("/mnt/sdcard/log_" + new Date() + ".log");
    byte[] buffer = new byte[1024];
    int len = in.read(buffer);
    int count = len;
    while (len != -1) {
      //cache.write(buffer, 0, len);
      System.out.println("len:" + len + "/count:" + count);
      out.write(buffer, 0, len);
      len = in.read(buffer);
      count+=len;
      if(count >1024*1024){
        break;
      }
    }
    //System.out.println(cache.toString());
    in.close();
    //cache.close();
    out.close();
  }
}
