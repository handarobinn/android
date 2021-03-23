package com.Test;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

public class DBManager {

        private DatabaseHelper dbHelper;

        private Context context;

        private SQLiteDatabase database;

        public DBManager(Context c) {
            context = c;
        }

        public DBManager open() throws SQLException {
            dbHelper = new DatabaseHelper(context);
            database = dbHelper.getWritableDatabase();
            return this;
        }

        public void close() {
            dbHelper.close();
        }

        public void insert(boolean isSend, String msg) {
            ContentValues contentValues = new ContentValues();
            contentValues.put(DatabaseHelper.isSend, isSend);
            contentValues.put(DatabaseHelper.msg, msg);
            database.insert(DatabaseHelper.TABLE_NAME, null, contentValues);
        }

        public Cursor fetch() {
            String[] columns = new String[] { DatabaseHelper._ID, DatabaseHelper.isSend, DatabaseHelper.msg };
            Cursor cursor = database.query(DatabaseHelper.TABLE_NAME, columns, null, null, null, null, null);
            if (cursor != null) {
                cursor.moveToFirst();
            }
            return cursor;

        }

        public ArrayList<Message> getlist(){
            Cursor cursor = fetch();
            ArrayList<Message> mArrayList = new ArrayList<Message>();

            cursor.moveToFirst();
            while(!cursor.isAfterLast()) {
                Message message = new Message();
                message.setMsg_id(cursor.getLong(cursor.getColumnIndex( DatabaseHelper._ID)));
                message.setSend(cursor.getInt(cursor.getColumnIndex( DatabaseHelper.isSend)) > 0);
                message.setMessage(cursor.getString(cursor.getColumnIndex( DatabaseHelper.msg)));
                mArrayList.add(message); //add the item
                cursor.moveToNext();
            }
             return  mArrayList;
        }

        public int update(long _id, boolean isSend, String msg) {
            ContentValues contentValues = new ContentValues();
            contentValues.put(DatabaseHelper.isSend, isSend);
            contentValues.put(DatabaseHelper.msg, msg);
            int i = database.update(DatabaseHelper.TABLE_NAME, contentValues, DatabaseHelper._ID + " = " + _id, null);
            return i;
        }

        public void delete(long _id) {
            database.delete(DatabaseHelper.TABLE_NAME, DatabaseHelper._ID + "=" + _id, null);
        }

        public int getLastAddedRowId() {
            String queryLastRowInserted = "select last_insert_rowid()";

            final Cursor cursor = database.rawQuery(queryLastRowInserted, null);
            int _idLastInsertedRow = 0;
            if (cursor != null) {
                try {
                    if (cursor.moveToFirst()) {
                        _idLastInsertedRow = cursor.getInt(0);
                    }
                } finally {
                    cursor.close();
                }
            }

            return _idLastInsertedRow;

        }

        public int getVersion(){
            return database.getVersion();
        }

}
