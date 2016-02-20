package com.example.wang.as_dialtest_demo;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
/**
 * Created by wang on 2016/2/5.
 */
public class DBManager {

    private MyDataBaseHelper mDbHelper;

    private SQLiteDatabase mDb;

    private static DBManager instance;

    public static DBManager getInstance() {
        if (instance == null) {
            throw new IllegalStateException(DBManager.class.getSimpleName() +
                    " is not initialized, call initialize(..) method first.");
        }
        return instance;
    }

    public static synchronized void initialize(Context context ) {
        if (instance == null) {
            instance = new DBManager(context);
        }
    }


    private DBManager(Context context)
    {
        mDbHelper =  MyDataBaseHelper.getHelper(context);
        // 因为getWritableDatabase内部调用了mContext.openOrCreateDatabase(mName, 0,mFactory);
        // 所以要确保context已初始化,我们可以把实例化DBManager的步骤放在Activity的onCreate里
        mDb = mDbHelper.getReadableDatabase();
    }

    public void add(List<Record> records)
    {
        // 采用事务处理，确保数据完整性
        mDb.beginTransaction(); // 开始事务
        try
        {
            for (Record record : records)
            {
                ContentValues values = new ContentValues();
                values.put(MyDataBaseHelper.CONTACTS_COLUMN_TIME, new Date().getTime());
                values.put(MyDataBaseHelper.CONTACTS_COLUMN_NETWORK, record.getNetwork());
                values.put(MyDataBaseHelper.CONTACTS_COLUMN_AUTHN, record.getAuthn());
                values.put(MyDataBaseHelper.CONTACTS_COLUMN_RESULT, record.getResult());
                mDb.insert(MyDataBaseHelper.CONTACTS_TABLE_NAME, null, values);
            }
            mDb.setTransactionSuccessful(); // 设置事务成功完成
        }
        finally
        {
            mDb.endTransaction(); // 结束事务
        }
    }

    /**
     * 插入一条记录
     * @param record
     */
    public void insert(Record record) {

        if(instance == null) {
            return;
        }

        ContentValues values = new ContentValues();
        values.put(MyDataBaseHelper.CONTACTS_COLUMN_TIME, record.getTime());
        values.put(MyDataBaseHelper.CONTACTS_COLUMN_NETWORK, record.getNetwork());
        values.put(MyDataBaseHelper.CONTACTS_COLUMN_AUTHN, record.getAuthn());
        values.put(MyDataBaseHelper.CONTACTS_COLUMN_RESULT, record.getResult());
        mDb.insert(MyDataBaseHelper.CONTACTS_TABLE_NAME, null, values);
    }

    /**
     * 更新time时间点的记录
     * @param time
     * @param record
     */
    public void update(long time, Record record) {
        ContentValues values = new ContentValues();
        values.put(MyDataBaseHelper.CONTACTS_COLUMN_TIME, new Date().getTime());
        values.put(MyDataBaseHelper.CONTACTS_COLUMN_NETWORK, record.getNetwork());
        values.put(MyDataBaseHelper.CONTACTS_COLUMN_AUTHN, record.getAuthn());
        values.put(MyDataBaseHelper.CONTACTS_COLUMN_RESULT, record.getResult());
        mDb.update(MyDataBaseHelper.CONTACTS_TABLE_NAME, values, "time = ?", new String[]{time + ""});
    }

    /**
     * 删除time时间点的记录
     * @param time
     */
    public void delete(long time) {
        mDb.delete(MyDataBaseHelper.CONTACTS_TABLE_NAME, "time = ?", new String[]{time + ""});
    }

    /**
     * 查询并返回所有记录
     * @return
     */
    public List<Record> query() {

        if(instance == null || mDb == null) {
            return new ArrayList<Record>();
        }

        List<Record> records = new ArrayList<>();
        Cursor cursor = mDb.query(MyDataBaseHelper.CONTACTS_TABLE_NAME, null, null, null, null, null,null);
        if(cursor.moveToFirst()) {
            do {

                int id = cursor.getInt(cursor.getColumnIndex(MyDataBaseHelper.CONTACTS_COLUMN_ID));
                long time = cursor.getLong(cursor.getColumnIndex(MyDataBaseHelper.CONTACTS_COLUMN_TIME));
                Log.w("time", time + ")");
                String network = cursor.getString(cursor.getColumnIndex(MyDataBaseHelper.CONTACTS_COLUMN_NETWORK));
                String authn = cursor.getString(cursor.getColumnIndex(MyDataBaseHelper.CONTACTS_COLUMN_AUTHN));
                int result = cursor.getInt(cursor.getColumnIndex(MyDataBaseHelper.CONTACTS_COLUMN_RESULT));

                Record record = new Record(id, time, network, authn, result);
                records.add(record);

            } while(cursor.moveToNext());
        }
        cursor.close();

        return records;
    }

    /**
     * 关闭数据库，不要轻易调用，除非你确保不再使用。（容易在多线程出错）
     */
    public synchronized void closeDB()
    {
        // 释放数据库资源
        mDbHelper.close();
    }

}
