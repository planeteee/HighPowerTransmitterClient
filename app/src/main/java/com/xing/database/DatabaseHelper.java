package com.xing.database;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "hptc.db";
    private static final int DATABASE_VERSION = 1;


    // 构造函数
    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // 创建表
    @Override
    public void onCreate(SQLiteDatabase db) {
        //DeviceStatusData
        String CREATE_TABLE = "CREATE TABLE " + DeviceStatusData.TABLE_NAME + " ("
                + DeviceStatusData.COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + DeviceStatusData.COLUMN_DEVICE_ID + " TEXT, "
                + DeviceStatusData.COLUMN_VOLTAGE + " TEXT, "
                + DeviceStatusData.COLUMN_CURRENT + " TEXT, "
                + DeviceStatusData.COLUMN_TEMP + " TEXT, "
                + DeviceStatusData.COLUMN_BATTERY + " TEXT, "
                + DeviceStatusData.COLUMN_POWER + " TEXT, "
                + DeviceStatusData.COLUMN_GPS + " TEXT)";

        db.execSQL(CREATE_TABLE);
    }

    // 升级数据库时调用
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + DeviceStatusData.TABLE_NAME);
        onCreate(db);
    }

}
