package com.utstar.appstoreapplication.common.db;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import com.utstar.appstoreapplication.activity.utils.EpgUserUtil;

public class UserIDProvider extends ContentProvider {

  private static final String Provider_Name =
      "com.utstar.appstoreapplication.common.db.UserIDProvider";
  static final Uri CONTENT_URI = Uri.parse("content://" + Provider_Name);
  public static final String TABLE_NAME = "accountinfo";
  private static final UriMatcher uriMatcher;
  private static final String DBNAME = "db_userinfo";
  private static final int VERSION_CODE = 3;

  static {
    uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    //        uriMatcher.addURI(Provider_Name, "books", BOOKS);
    //        uriMatcher.addURI(Provider_Name, "books/#", BOOK_ID);
  }

  SQLiteDatabase db;

  @Override public boolean onCreate() {
    Context context = getContext();
    DatabaseHelper dbHelper = new DatabaseHelper(context);
    db = dbHelper.getWritableDatabase();
    return db != null;
  }

  @Override
  public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
      String sortOrder) {
    int i = uri.compareTo(CONTENT_URI);
    if (i != 0) {
      return null;
    }
    SQLiteQueryBuilder sqlBuilder = new SQLiteQueryBuilder();
    sqlBuilder.setTables(TABLE_NAME);
    Cursor c = sqlBuilder.query(db, projection, selection, selectionArgs, null, null, sortOrder);
    c.setNotificationUri(getContext().getContentResolver(), uri);

    return c;
  }

  @Override public String getType(Uri uri) {
    return null;
  }

  @Override public Uri insert(Uri uri, ContentValues values) {
    return null;
  }

  @Override public int delete(Uri uri, String selection, String[] selectionArgs) {
    return 0;
  }

  @Override
  public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
    return 0;
  }

  public static class DatabaseHelper extends SQLiteOpenHelper {

    public DatabaseHelper(Context context) {
      super(context, DBNAME, null, VERSION_CODE);
    }

    @Override public void onCreate(SQLiteDatabase db) {
      //创建表的SQL语句
      String create_table = "create table "
          + TABLE_NAME
          + "(_id integer primary key, tvAccount text, vspcode text, epgUserId text, epgToken text, epgServer text)";
      db.execSQL(create_table);
    }

    @Override public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
      if (newVersion > oldVersion) {
        update(db);
      }
    }

    private void update(SQLiteDatabase db) {
      Cursor c = db.query(TABLE_NAME, null, null, null, null, null, null);
      String account = "", vspCode = "";
      while (c.moveToNext()) {
        account = c.getString(c.getColumnIndex("tvAccount"));
        vspCode = c.getString(c.getColumnIndex("vspcode"));
      }
      String dropTable = "drop table " + TABLE_NAME;
      db.execSQL(dropTable);

      String updateTable = "create table "
          + TABLE_NAME
          + "(_id integer primary key,tvAccount text,vspcode text, epgUserId text, epgToken text, epgServer text)";
      db.execSQL(updateTable);
      ContentValues values = new ContentValues();
      values.put("_id", 1);
      values.put("tvAccount", account);
      values.put("vspcode", vspCode);
      values.put("epgUserId", "");
      values.put("epgToken", "");
      values.put("epgServer", "");
      db.insert(TABLE_NAME, null, values);
      c.close();
    }
  }
}
