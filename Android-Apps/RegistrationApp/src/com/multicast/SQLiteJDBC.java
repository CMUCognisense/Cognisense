package com.multicast;

import java.util.HashSet;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

/**
 * This is the SQLite JDBC for manipulating the database for 
 * reliable multicast.
 * 
 * @author Parth
 */
public class SQLiteJDBC{
	SQLiteDatabase db;

	public SQLiteJDBC(Context context) {
		try {
			db = context.openOrCreateDatabase("test", 0, null);
			db.execSQL("CREATE TABLE IF NOT EXISTS DEVICES " +
					"(ID TEXT PRIMARY KEY     NOT NULL);");
			Log.e("SQLite","Opened database successfully");

		} catch ( Exception e ) {
			Log.e("SQLite", e.getClass().getName() + ": " + e.getMessage() );
		}
	}

	/**
	 * Insert a replier's mac address into the databse
	 * @param macAddress
	 */
	public void insert(String macAddress) {
		try {
			ContentValues values = new ContentValues();
			values.put("ID", macAddress);
			db.insertOrThrow("DEVICES", null, values);
		} catch (SQLException e) {
			Log.e("SQLite", e.getClass().getName() + ": " + e.getMessage() );
		}
	}

	/**
	 * Query the database to get all the mac addresses
	 * @return A hashset of repliers' mac addresses
	 */
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

	/**
	 * Query the database to get the number of devices that are found last time.
	 * @return Number of devices replied last time
	 */
	public int queryNumber() {
		Cursor c = db.rawQuery("SELECT * FROM DEVICES;", null);
		return c.getCount();
	}
}
