package com.multicast;

import java.sql.*;
import java.util.HashSet;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class SQLiteJDBC
{
	SQLiteDatabase db;

	public SQLiteJDBC(Context context) {

		try {
			db = context.openOrCreateDatabase("test", 0, null);
			db.execSQL("CREATE TABLE IF NOT EXISTS DEVICES " +
					"(ID TEXT PRIMARY KEY     NOT NULL);");
			Log.e("SQLite","Opened database successfully");

		} catch ( Exception e ) {
			Log.e("SQLite", e.getClass().getName() + ": " + e.getMessage() );
			//System.exit(0);
		}


	}

	public void insert(String macAddress) {
		//String sql = "INSERT INTO DEVICES (ID)" +
		//               "VALUES ('"+macAddress+"');";
		try {
			ContentValues values = new ContentValues();
			values.put("ID", macAddress);
			db.insertOrThrow("DEVICES", null, values);
		} catch (SQLException e) {
			Log.e("SQLite", e.getClass().getName() + ": " + e.getMessage() );
		}

	}

	public HashSet<String> query() {
		Cursor c = db.rawQuery("SELECT * FROM DEVICES;", null);
		HashSet<String> idlist = new HashSet<String>(c.getCount());
		
		if(c.moveToFirst())
		{
			while(!c.isAfterLast())
			{
				idlist.add(c.getString(0));
				c.moveToNext();
			}
		}
		return idlist;
	}

	public int queryNumber() {
		Cursor c = db.rawQuery("SELECT * FROM DEVICES;", null);
		return c.getCount();
	}

}
