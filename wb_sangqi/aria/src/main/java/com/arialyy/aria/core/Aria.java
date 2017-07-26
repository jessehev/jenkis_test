/*
 * Copyright (C) 2016 AriaLyy(https://github.com/AriaLyy/Aria)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.arialyy.aria.core;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Application;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.Service;
import android.content.Context;
import android.os.Build;
import com.arialyy.aria.core.scheduler.OnSchedulerListener;
import com.arialyy.aria.core.task.Task;

/**
 * Created by lyy on 2016/12/1.
 * https://github.com/AriaLyy/Aria
 * Aria启动，管理全局任务
 * <pre>
 *   <code>
 *   //启动下载
 *   Aria.whit(this)
 *       .load(DOWNLOAD_URL)     //下载地址，必填
 *       //文件保存路径，必填
 *       .setDownloadPath(Environment.getExternalStorageDirectory().getPath() + "/test.apk")
 *       .setDownloadName("test.apk")    //文件名，必填
 *       .start();
 *   </code>
 * </pre>
 */
@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1) public class Aria {
  /**
   * 预处理完成
   */
  public static final String ACTION_PRE       = "ACTION_PRE";
  /**
   * 下载开始前事件
   */
  public static final String ACTION_POST_PRE  = "ACTION_POST_PRE";
  /**
   * 开始下载事件
   */
  public static final String ACTION_START     = "ACTION_START";
  /**
   * 恢复下载事件
   */
  public static final String ACTION_RESUME    = "ACTION_RESUME";
  /**
   * 正在下载事件
   */
  public static final String ACTION_RUNNING   = "ACTION_RUNNING";
  /**
   * 停止下载事件
   */
  public static final String ACTION_STOP      = "ACTION_STOP";
  /**
   * 取消下载事件
   */
  public static final String ACTION_CANCEL    = "ACTION_CANCEL";
  /**
   * 下载完成事件
   */
  public static final String ACTION_COMPLETE  = "ACTION_COMPLETE";
  /**
   * 下载失败事件
   */
  public static final String ACTION_FAIL      = "ACTION_FAIL";
  /**
   * 下载实体
   */
  public static final String ENTITY           = "DOWNLOAD_ENTITY";
  /**
   * 位置
   */
  public static final String CURRENT_LOCATION = "CURRENT_LOCATION";
  /**
   * 速度
   */
  public static final String CURRENT_SPEED    = "CURRENT_SPEED";

  private Aria() {
  }

  /**
   * 接受Activity、Service、Application
   */
  public static AMReceiver whit(Context context) {
    //if (context == null) throw new IllegalArgumentException("context 不能为 null");
    checkNull(context);
    if (context instanceof Activity
        || context instanceof Service
        || context instanceof Application) {
      return AriaManager.getInstance(context).get(context);
    } else {
      throw new IllegalArgumentException("这是不支持的context");
    }
  }

  /**
   * 处理Fragment、或者DialogFragment
   */
  public static AMReceiver whit(Fragment fragment) {
    checkNull(fragment);
    return AriaManager.getInstance(
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.M ? fragment.getContext()
            : fragment.getActivity()).get(fragment);
  }

  /**
   * 处理Dialog
   */
  public static AMReceiver whit(Dialog dialog) {
    checkNull(dialog);
    return AriaManager.getInstance(dialog.getContext()).get(dialog);
  }

  private static void checkNull(Object obj) {
    if (obj == null) throw new IllegalArgumentException("不能传入空对象");
  }

  /**
   * 处理通用事件
   */
  public static AriaManager get(Context context) {
    if (context == null) throw new IllegalArgumentException("context 不能为 null");
    if (context instanceof Activity
        || context instanceof Service
        || context instanceof Application) {
      return AriaManager.getInstance(context);
    } else {
      throw new IllegalArgumentException("这是不支持的context");
    }
  }

  /**
   * 处理Dialog的通用任务
   */
  public static AMReceiver get(Dialog dialog) {
    checkNull(dialog);
    return AriaManager.getInstance(dialog.getContext()).get(dialog);
  }

  public static class SimpleSchedulerListener implements OnSchedulerListener {

    @Override public void onTaskPre(Task task) {

    }

    @Override public void onTaskResume(Task task) {

    }

    @Override public void onTaskStart(Task task) {

    }

    @Override public void onTaskStop(Task task) {

    }

    @Override public void onTaskCancel(Task task) {

    }

    @Override public void onTaskFail(Task task) {

    }

    @Override public void onTaskComplete(Task task) {

    }

    @Override public void onTaskRunning(Task task) {

    }
  }
}
