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

import com.arialyy.aria.core.DownloadEntity;
import com.arialyy.aria.core.task.Task;

/**
 * Created by lyy on 2016/9/20.
 * 取消命令
 */
class CancelCmd extends IDownloadCmd {

  CancelCmd(String target, DownloadEntity entity) {
    super(target, entity);
  }

  CancelCmd(DownloadEntity entity) {
    super(entity);
  }

  @Override public void executeCmd() {
    Task task = mQueue.getTask(mEntity);
    if (task == null) {
      task = mQueue.createTask(mTargetName, mEntity);
    }
    if (task != null) {
      if (mTargetName != null) {
        task.setTargetName(mTargetName);
      }
      mQueue.cancelTask(task);
    }
  }
}