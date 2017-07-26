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

package com.arialyy.aria.core.command;

import com.arialyy.aria.core.DownloadManager;
import com.arialyy.aria.core.queue.ITaskQueue;
import com.arialyy.aria.util.CheckUtil;
import com.arialyy.aria.util.CommonUtil;
import com.arialyy.aria.core.DownloadEntity;

/**
 * Created by lyy on 2016/8/22.
 * 下载命令
 */
public abstract class IDownloadCmd {
  ITaskQueue mQueue;
  DownloadEntity mEntity;
  String TAG;
  String mTargetName;

  /**
   * @param entity 下载实体
   */
  IDownloadCmd(DownloadEntity entity) {
    this(null, entity);
  }

  /**
   * @param targetName 产生任务的对象名
   */
  IDownloadCmd(String targetName, DownloadEntity entity) {
    if (!CheckUtil.checkDownloadEntity(entity)) {
      return;
    }
    mTargetName = targetName;
    mEntity = entity;
    TAG = CommonUtil.getClassName(this);
    mQueue = DownloadManager.getInstance().getTaskQueue();
  }

  /**
   * 执行命令
   */
  public abstract void executeCmd();
}