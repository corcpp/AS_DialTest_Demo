package com.example.wang.as_dialtest_demo;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

/**
 * Created by wang on 2016/2/5.
 */
public class MyDataBaseHelper extends SQLiteOpenHelper{

    public static final String DATABASE_NAME = "DialTest.db";
    public static final int DATABASE_VERSION = 1;

    public static final String CONTACTS_TABLE_NAME = "Record";
    public static final String CONTACTS_COLUMN_ID = "id";
    public static final String CONTACTS_COLUMN_TIME = "time";
    public static final String CONTACTS_COLUMN_NETWORK = "network";
    public static final String CONTACTS_COLUMN_AUTHN = "authn";
    public static final String CONTACTS_COLUMN_RESULT = "result";

    public static final String CREATE_RECORD_TABLE = "create table Record ("
            + "id integer primary key autoincrement, "
            + "time integer, "
            + "network text, "
            + "authn text, "
            + "result integer"
            + " )";

    private Context mContext;

    private static MyDataBaseHelper instance;

    /**
     * 单例数据库
     * @param context
     * @return
     */
    public synchronized static MyDataBaseHelper getHelper(Context context) {
        if (instance == null) {
            instance = new MyDataBaseHelper(context);
        }
        return instance;
    }

    private MyDataBaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        mContext = context;
    }

    public MyDataBaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        mContext = context;
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_RECORD_TABLE);
        Toast.makeText(mContext, "Create database succeeded", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        Toast.makeText(mContext, "update database succeeded", Toast.LENGTH_SHORT).show();
        db.execSQL("drop table if exists Record");
        onCreate(db);
    }

    @Override
    public synchronized void close() {
        super.close();
    }
}
