package com.utstar.appstoreapplication.activity.db.sdcard;

import java.lang.reflect.Field;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Aria.Lao on 2017/5/31.
 */

class OrmUtil {

  /**
   * 获取类里面的所在字段
   */
  static Field[] getFields(Class clazz) {
    Field[] fields = null;
    fields = clazz.getDeclaredFields();
    if (fields == null || fields.length == 0) {
      Class superClazz = clazz.getSuperclass();
      if (superClazz != null) {
        fields = getFields(superClazz);
      }
    }
    return fields;
  }

  /**
   * 获取类里面的指定对象，如果该类没有则从父类查询
   */
   static Field getField(Class clazz, String name) {
    Field field = null;
    try {
      field = clazz.getDeclaredField(name);
    } catch (NoSuchFieldException e) {
      try {
        field = clazz.getField(name);
      } catch (NoSuchFieldException e1) {
        if (clazz.getSuperclass() == null) {
          return field;
        } else {
          field = getField(clazz.getSuperclass(), name);
        }
      }
    }
    if (field != null) {
      field.setAccessible(true);
    }
    return field;
  }

  /**
   * 检查sql的expression是否合法
   */
  static void checkSqlExpression(String... expression) {
    if (expression.length == 0) {
      throw new IllegalArgumentException("sql语句表达式不能为null");
    }
    if (expression.length == 1) {
      throw new IllegalArgumentException("表达式需要写入参数");
    }
    String where = expression[0];
    if (!where.contains("?")) {
      throw new IllegalArgumentException("请在where语句的'='后编写?");
    }
    Pattern pattern = Pattern.compile("\\?");
    Matcher matcher = pattern.matcher(where);
    int count = 0;
    while (matcher.find()) {
      count++;
    }
    if (count < expression.length - 1) {
      throw new IllegalArgumentException("条件语句的?个数不能小于参数个数");
    }
    if (count > expression.length - 1) {
      throw new IllegalArgumentException("条件语句的?个数不能大于参数个数");
    }
  }

  /**
   * 获取对象名
   *
   * @param obj 对象
   * @return 对象名
   */
  static String getClassName(Object obj) {
    String arrays[] = obj.getClass().getName().split("\\.");
    return arrays[arrays.length - 1];
  }

  static String getClassName(Class clazz) {
    String arrays[] = clazz.getName().split("\\.");
    return arrays[arrays.length - 1];
  }

}
