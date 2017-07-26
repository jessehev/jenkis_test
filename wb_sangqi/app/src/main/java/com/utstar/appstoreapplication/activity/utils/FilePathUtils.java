/*
 * $Revision: $
 * $Date: $
 * $Id: $
 * ====================================================================
 * Copyright © 2012 Beijing seeyon software Co..Ltd..All rights reserved.
 *
 * This software is the proprietary information of Beijing seeyon software Co..Ltd.
 * Use is subject to license terms.
 */
package com.utstar.appstoreapplication.activity.utils;

import android.content.Context;
import android.os.Environment;
import android.webkit.MimeTypeMap;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * 文件路径获取类，主要是创建 ，获取，文件路径，如：下载，临时文件夹等
 *
 * @author Administrator
 * @version 1.0
 * @since JDK 1.5
 */
public class FilePathUtils {
  public static final int MEDIA_TYPE_IMAGE = 1;
  public static final int MEDIA_TYPE_RECODE = 2;
  private static final String APPLICATION_FOLDER = "GameBar2";
  private static final String TEMP_FOLDER = "temp";
  private static final String DOWNLOAD_FOLDER = "downloads";
  private static final String ROM_FOLDER = "roms";

  /**
   * 得到临时文件夹，在退出程序时有可能会删除该文件夹
   */
  public static File getTempFolder() {
    File root = getApplicationFolder();
    if (root != null) {
      File folder = new File(root, TEMP_FOLDER);
      if (!folder.exists()) {
        folder.mkdir();
      }
      return folder;
    } else {
      return null;
    }
  }

  /**
   * 获取应用文件夹 /
   *
   * @return The application folder.
   */
  public static File getApplicationFolder() {
    File root = Environment.getExternalStorageDirectory();
    if (root.canWrite()) {
      File folder = new File(root, APPLICATION_FOLDER);
      if (!folder.exists()) {
        folder.mkdir();
      }
      return folder;
    } else {
      return null;
    }
  }

  /**
   * 得到临时文件夹路径，在退出程序时有可能会删除该文件夹
   */
  public static String getTempFolderPath() {
    File root = getApplicationFolder();
    if (root != null) {
      File folder = new File(root, TEMP_FOLDER);
      if (!folder.exists()) {
        folder.mkdir();
      }
      return folder.toString();
    } else {
      return null;
    }
  }

  public static File getRomloadFolder(Context context) {
    File root = getApplicationFolder(context);
    if (root != null) {
      File folder = new File(root, ROM_FOLDER);
      if (!folder.exists()) {
        folder.mkdir();
        chmodFile(folder.getAbsolutePath());
      }
      return folder;
    } else {
      return null;
    }
  }

  public synchronized static File getApplicationFolder(Context mContext) {
    File root = null;
    if (judgeIsExistSdCard()) {
      //            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
      //            {
      //                root = mContext.getExternalFilesDir("Files");
      //            } else
      //            {
      //                root = Environment.getExternalStorageDirectory();
      //            }
      root = Environment.getExternalStorageDirectory();
    } else {
      root = mContext.getFilesDir();
    }
    chmodFile(root.getAbsolutePath());
    if (root != null) {
      File folder = new File(root, APPLICATION_FOLDER);
      if (!folder.exists()) {
        folder.mkdirs();
        chmodFile(folder.getAbsolutePath());
      }
      return folder;
    } else {
      root.toString();
      return null;
    }
  }

  public synchronized static void chmodFile(String path) {
    String[] cmd = { "chmod", "777", path };
    Process process = null;
    InputStream errorStream = null;
    InputStream inputStream = null;
    try {
      process = Runtime.getRuntime().exec(cmd);
      byte[] buffer = new byte[64];
      int len = -1;
      process = Runtime.getRuntime().exec(cmd);
      inputStream = process.getInputStream();
      while ((len = inputStream.read(buffer)) > 0) {

      }
      errorStream = process.getErrorStream();
      len = -1;
      while ((len = errorStream.read(buffer)) > 0) {

      }
      buffer = null;
    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      try {
        if (errorStream != null) {
          errorStream.close();
        }
        if (inputStream != null) {
          inputStream.close();
        }
        if (process != null) {
          process.destroy();
        }
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }

  /**
   * 判断有无Sd卡
   */
  public static boolean judgeIsExistSdCard() {
    return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
  }

  /**
   * 建立一个 下载 文件
   *
   * @param module 模块名称 可以为 pic.hander 将建立pic/hander
   * @param fileName 文件名字 XX.txt
   */
  public static File getDownloadFile(String module, String fileName) {
    File basePath = getDownloadFolder();
    String sp = File.separator;
    if (module != null && !module.equals("")) {
      module = module.replace(".", sp);
      basePath = new File(basePath, module);
      if (!basePath.exists()) {
        basePath.mkdirs();
      }
    }
    File fileDowload = new File(basePath, fileName);
    // if (!fileDowload.exists()) {
    // fileDowload.mkdir();
    // }
    return fileDowload;
  }

  /**
   * 获取下载文件夹 /download
   *
   * @return The application download folder.
   */
  public static File getDownloadFolder() {
    File root = getApplicationFolder();
    if (root != null) {
      File folder = new File(root, DOWNLOAD_FOLDER);
      if (!folder.exists()) {
        folder.mkdir();
      }
      return folder;
    } else {
      return null;
    }
  }

  public synchronized static File getDownloadFile(String module, String fileName,
      Context mContext) {
    File basePath = getDownloadFolder(mContext);
    String sp = File.separator;
    if (module != null && !module.equals("")) {
      module = module.replace(".", sp);
      basePath = new File(basePath, module);
      if (!basePath.exists()) {
        basePath.mkdirs();
        chmodFile(basePath.getAbsolutePath());
      }
    }
    File fileDowload = new File(basePath, fileName);
    // if (!fileDowload.exists()) {
    // fileDowload.mkdirs();
    // }
    chmodFile(fileDowload.getAbsolutePath());
    return fileDowload;
  }

  public synchronized static File getDownloadFolder(Context mContext) {
    File root = getApplicationFolder(mContext);
    if (root != null) {
      File folder = new File(root, DOWNLOAD_FOLDER);
      if (!folder.exists()) {
        folder.mkdirs();
        chmodFile(folder.getAbsolutePath());
      }
      return folder;
    } else {
      root.toString();
      return null;
    }
  }

  /**
   * 根据路径 判断文件是否存在
   *
   * @param path 模块名称 aa.bb.cc
   * @param fileName 文件名称
   */
  public static boolean isExistFile(String path, String fileName) {
    String sp = File.separator;
    if (path != null && !path.equals("")) {
      path = path.replace(".", sp);
      fileName = path + sp + fileName;
    }

    File file = new File(fileName);
    return file.exists();
  }

  /**
   * 判断文件是否存在
   */
  public static boolean checkFileExists(String path) {
    File file = new File(path);
    if (file.exists()) {
      return true;
    }
    return false;
  }

  /**
   * 的到文件的放置路径
   *
   * @param aModuleName 模块名字
   */
  public static String getPath(String aModuleName) {
    String sp = File.separator;
    String modulePath;
    modulePath = aModuleName.replace(".", sp);
    File root = getApplicationFolder();
    File dirpath = new File(root, modulePath);
    dirpath.mkdirs();
    return dirpath.getPath();
  }

  /**
   * 根据文件名称来获取 MimeType
   *
   * @param name 带后缀名的文件名字
   */
  public static String getMimeTypeForName(String name) {
    if (name == null || "".equals(name)) {
      return "";
    }
    String suffix = name.substring(name.lastIndexOf(".") + 1);
    String conType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(suffix);
    if (conType == null || "".equals(conType)) {
      if ("AMR".compareTo(suffix) == 0) {
        conType = "audio/amr";
      } else {
        conType = "audio/amr";
      }
    }
    return conType;
  }

  /**
   * 根据文件名称来获取 后缀名
   *
   * @param name 带后缀名的文件名字
   */
  public static String getFileSuffix(String name) {
    if (name == null || "".equals(name)) {
      return "";
    }
    String suffix = name.substring(name.lastIndexOf(".") + 1);

    return suffix;
  }

  /***
   * 删除文件夹下所有文件
   */
  public static void deleteFile(String aModuleName) {
    String filePath = getPath(aModuleName);
    File file = new File(filePath);
    if (file.exists()) {
      if (file.isFile()) {
        file.delete();
      } else if (file.isDirectory()) {
        File files[] = file.listFiles();
        for (int i = 0; i < files.length; i++) {
          deleteFile(files[i]);
        }
      }
      file.delete();
    }
  }

  /***
   * 删除文件夹下所有文件
   */
  public static void deleteFile(File file) {
    if (file.exists()) {
      if (file.isFile()) {
        file.delete();
      } else if (file.isDirectory()) {
        File files[] = file.listFiles();
        for (int i = 0; i < files.length; i++) {
          deleteFile(files[i]);
        }
      }
      file.delete();
    }
  }

  /**
   * 删除下载文件的文件夹
   */
  public static long deleteDownloadFolderFile() {
    long allSize = 0;
    File downloadFolder = getDownloadFolder();
    allSize = deleteFile(downloadFolder, allSize);
    return allSize;
  }

  /**
   * 删除所有文件并返回删除文件的大小
   */
  public static long deleteAllFile() {
    long allSize = 0;
    File gml = getApplicationFolder();

    if (!gml.exists()) {
      return 0;
    }
    final File root = new File(gml.getAbsolutePath() + System.currentTimeMillis());
    gml.renameTo(root);
    if (root.exists()) {
      if (root.isFile()) {
        allSize = allSize + root.length();
        root.delete();
      } else if (root.isDirectory()) {
        File files[] = root.listFiles();
        for (int i = 0; i < files.length; i++) {
          allSize = deleteFile(files[i], allSize);
        }
        root.delete();
      }
    }
    return allSize;
  }

  private static long deleteFile(File file, long orgSize) {
    if (file.exists()) {
      if (file.isFile()) {
        orgSize = orgSize + file.length();
        //				final File to = new File(file.getAbsolutePath() + System.currentTimeMillis());
        //				file.renameTo(to);
        //				to.delete();

        file.delete();
      } else if (file.isDirectory()) {// 是文件夹 递归循环
        if (!"skin".equals(file.getName())) {
          File files[] = file.listFiles();
          for (int i = 0; i < files.length; i++) {
            orgSize = deleteFile(files[i], orgSize);
          }
          //					final File to = new File(file.getAbsolutePath() + System.currentTimeMillis());
          //					file.renameTo(to);
          //					to.delete();
          file.delete();
        }
      }
    }
    return orgSize;
  }
}
