package tw.brad.apps.cloudfitness.java_class.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by EdLo on 2018/3/15.
 * 自定義的DB輔助
 * onCreate後便建立兩個table
 */

public class MyDBHelper extends SQLiteOpenHelper {
    // DB名稱
    public static final String dbN_ame = "cloud_fitness";
    // 建立Users資料表的SQL語法
    private static final String createTableUsers =
            "CREATE TABLE IF NOT EXISTS users (" +
            "_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "email TEXT NOT NULL, " +
            "password TEXT NOT NULL, " +
            "first_name TEXT NOT NULL, " +
            "last_name TEXT NOT NULL, " +
            "birth_date TEXT NOT NULL, "+
            "height_in REAL , " +
            "height_ft INTEGER , " +
            "height_cm REAL , " +
            "weight_lb REAL , " +
            "weight_kg REAL , "+
            "gender TEXT NOT NULL, "+
            "unit_type INTEGER NOT NULL, "+
            "activity_level INTEGER NOT NULL, " +
            "fb_id INTEGER );";

    // 建立Record資料表的SQL語法
    private static final String createTableRecord =
            "CREATE TABLE IF NOT EXISTS record (" +
            "_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "datetime INTEGER , " +
            "weight REAL , " +
            "bmi REAL , " +
            "body_fat REAL , " +
            "body_water REAL , " +
            "muscle_mass REAL , " +
            "bone_mass REAL, " +
            "v_fat REAL , " +
            "user_id INTEGER NOT NULL);";

    public MyDBHelper(Context context, String name,
                      SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //建立資料表(如果不存在的話)
        db.execSQL(createTableUsers);
        db.execSQL(createTableRecord);
    }

    //資料庫升級 目前用不到
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // 刪除原有的表格
        db.execSQL("DROP TABLE IF EXISTS record");
        db.execSQL("DROP TABLE IF EXISTS users");
        // 呼叫onCreate建立新版的表格
        onCreate(db);
    }
}
