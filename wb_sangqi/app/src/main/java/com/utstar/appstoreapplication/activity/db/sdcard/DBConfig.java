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
package com.utstar.appstoreapplication.activity.db.sdcard;

import android.text.TextUtils;
import com.utstar.appstoreapplication.activity.entity.db_entity.GameRecord;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Aria.Lao on 2017/4/6.
 * 数据库配置信息
 */
class DBConfig {
  static Map<String, Class> mapping = new HashMap<>();
  static String DB_NAME;
  static int VERSION = 2;

  static {
    if (TextUtils.isEmpty(DB_NAME)) {
      DB_NAME = "record_db";
    }
    if (VERSION == -1) {
      VERSION = 1;
    }
  }

  static {
    mapping.put("GameRecord", GameRecord.class);
  }
}
