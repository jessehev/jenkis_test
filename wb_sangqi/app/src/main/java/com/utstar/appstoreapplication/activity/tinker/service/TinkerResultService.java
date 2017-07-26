/*
 * Tencent is pleased to support the open source community by making Tinker available.
 *
 * Copyright (C) 2016 THL A29 Limited, a Tencent company. All rights reserved.
 *
 * Licensed under the BSD 3-Clause License (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 *
 * https://opensource.org/licenses/BSD-3-Clause
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is
 * distributed on an "AS IS" basis, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.utstar.appstoreapplication.activity.tinker.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.widget.Toast;
import com.arialyy.frame.util.AndroidUtils;
import com.arialyy.frame.util.SharePreUtil;
import com.arialyy.frame.util.show.FL;
import com.tencent.tinker.lib.service.DefaultTinkerResultService;
import com.tencent.tinker.lib.service.PatchResult;
import com.tencent.tinker.lib.util.TinkerLog;
import com.tencent.tinker.lib.util.TinkerServiceInternals;
import com.tencent.tinker.loader.shareutil.SharePatchFileUtil;
import com.utstar.appstoreapplication.activity.BuildConfig;
import com.utstar.appstoreapplication.activity.commons.constants.KeyConstant;
import com.utstar.appstoreapplication.activity.service.HotUpdateService;
import com.utstar.appstoreapplication.activity.tinker.BaseApp;
import com.utstar.appstoreapplication.activity.tinker.util.Utils;
import com.utstar.appstoreapplication.activity.utils.DownloadHelpUtil;
import java.io.EOFException;
import java.io.File;

/**
 * optional, you can just use DefaultTinkerResultService we can restart process when we are at
 * background or screen off Created by zhangshaowen on 16/4/13.
 */
public class TinkerResultService extends DefaultTinkerResultService {
  private static final String TAG = "Tinker.TinkerResultService";
  public static final String UPDATE_SUCCESS = "com.wanba.tinker.update.success";
  public static final String UPDATE_FAIL = "com.wanba.tinker.update.fail";

  @Override public void onPatchResult(final PatchResult result) {
    if (result == null) {
      TinkerLog.e(TAG, "TinkerResultService received null result!!!!");
      return;
    }
    TinkerLog.i(TAG, "TinkerResultService receive result: %s", result.toString());

    //first, we want to kill the recover process
    TinkerServiceInternals.killTinkerPatchServiceProcess(getApplicationContext());

    Handler handler = new Handler(Looper.getMainLooper());
    handler.post(() -> {
      String tinkerId = null;
      try {
        tinkerId = SharePreUtil.getString(KeyConstant.PRE_NAME, BaseApp.context,
            KeyConstant.PRE.TEMP_TINKER_ID);
      } catch (Exception e) {
        FL.d(TAG, "读取TINKER_TEMP_ID 失败");
      }

      if (result.isSuccess) {
        SharePreUtil.putString(KeyConstant.PRE_NAME, BaseApp.context, KeyConstant.PRE.TINKER_ID,
            TextUtils.isEmpty(tinkerId) ? BuildConfig.TINKER_ID : tinkerId);
        SharePreUtil.removeKey(KeyConstant.PRE_NAME, BaseApp.context,
            KeyConstant.PRE.TEMP_TINKER_MD5);
        //Toast.makeText(getApplicationContext(), "资源文件完成，玩吧游戏平台即将重启", Toast.LENGTH_LONG).show();
        if (BuildConfig.DEBUG) {
          Toast.makeText(getApplicationContext(), "资源文件完成，玩吧游戏平台即将重启", Toast.LENGTH_LONG).show();
        }
      } else {
        //Toast.makeText(getApplicationContext(), "资源更新失败", Toast.LENGTH_LONG).show();
        File hotRes = new File(DownloadHelpUtil.getHotUpdateResPath());
        if (hotRes.exists()) {
          hotRes.delete();
        }
        //Toast.makeText(getApplicationContext(), "资源更新失败", Toast.LENGTH_LONG).show();
        if (BuildConfig.DEBUG) {
          Toast.makeText(getApplicationContext(), "资源更新失败", Toast.LENGTH_LONG).show();
        }
        FL.d(TAG, "资源更新失败，temp_id = " + tinkerId);
      }
    });
    // is success and newPatch, it is nice to delete the raw file, and restart at once
    // for old patch, you can't delete the patch file
    Intent intent = new Intent();
    if (result.isSuccess) {
      intent.setAction(UPDATE_SUCCESS);
      File rawFile = new File(result.rawPatchFilePath);
      if (rawFile.exists()) {
        TinkerLog.i(TAG, "save delete raw patch file");
        SharePatchFileUtil.safeDeleteFile(rawFile);
      }
      //not like TinkerResultService, I want to restart just when I am at background!
      //if you have not install tinker this moment, you can use TinkerApplicationHelper api
      if (checkIfNeedKill(result)) {
        if (Utils.isBackground()) {
          TinkerLog.i(TAG, "it is in background, just restart process");
          restartProcess();
        } else {
          //we can wait process at background, such as onAppBackground
          //or we can restart when the screen off
          TinkerLog.i(TAG, "tinker wait screen to restart process");
          new ScreenState(getApplicationContext(), new ScreenState.IOnScreenOff() {
            @Override public void onScreenOff() {
              restartProcess();
            }
          });
        }
      } else {
        TinkerLog.i(TAG, "I have already install the newly patch version!");
      }
    } else {
      intent.setAction(UPDATE_FAIL);
    }
    BaseApp.application.sendBroadcast(intent);
  }

  /**
   * you can restart your process through service or broadcast
   */
  private void restartProcess() {
    TinkerLog.i(TAG, "app is background now, i can kill quietly");
    //you can send service or broadcast intent to restart your process
    //android.os.Process.killProcess(android.os.Process.myPid());
  }

  static class ScreenState {
    interface IOnScreenOff {
      void onScreenOff();
    }

    ScreenState(Context context, final IOnScreenOff onScreenOffInterface) {
      IntentFilter filter = new IntentFilter();
      filter.addAction(Intent.ACTION_SCREEN_OFF);
      context.registerReceiver(new BroadcastReceiver() {

        @Override public void onReceive(Context context, Intent in) {
          String action = in == null ? "" : in.getAction();
          TinkerLog.i(TAG, "ScreenReceiver action [%s] ", action);
          if (Intent.ACTION_SCREEN_OFF.equals(action)) {

            context.unregisterReceiver(this);

            if (onScreenOffInterface != null) {
              onScreenOffInterface.onScreenOff();
            }
          }
        }
      }, filter);
    }
  }
}
