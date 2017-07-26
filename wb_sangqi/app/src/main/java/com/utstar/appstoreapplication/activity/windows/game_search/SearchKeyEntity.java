package com.utstar.appstoreapplication.activity.windows.game_search;

/**
 * Created by Aria.Lao on 2016/12/19.
 */
final class SearchKeyEntity {
  public int num = -1;
  public char[] key;
  public String icon;

  public SearchKeyEntity(int num) {
    this.num = num;
  }

  public SearchKeyEntity(int num, char[] key) {
    this.num = num;
    this.key = key;
  }

  public SearchKeyEntity(int num, String icon) {
    this.num = num;
    this.icon = icon;
  }
}
