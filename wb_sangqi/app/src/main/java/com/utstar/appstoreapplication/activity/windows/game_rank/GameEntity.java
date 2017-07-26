package com.utstar.appstoreapplication.activity.windows.game_rank;

/**
 * Created by JesseHev on 2016/12/21.
 */

public class GameEntity {

  public int id;
  public int selectTypeId;
  public int number;
  public int page;

  public GameEntity(int id, int selectTypeId, int page, int number) {
    this.id = id;
    this.selectTypeId = selectTypeId;
    this.page = page;
    this.number = number;
  }
}
