package com.util;

/**
 * This is the database open helper. One database helper class operates
 * on one database. More tables can be added here.
 */
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper{
	private static final String DBNAME = "cognisense.db";
	private static final int VERSION = 1;
	public static final String INFO_TABLE = "INFO";

	public DatabaseHelper(Context context) {
		super(context, DBNAME, null, VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("CREATE TABLE IF NOT EXISTS " + INFO_TABLE +
				" (SERVICETYPE TEXT PRIMARY KEY NOT NULL," +
				"SERVICEID TEXT," +
				"INTENTFILTER TEXT);");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS INFO"); 
		onCreate(db);
	}
}
