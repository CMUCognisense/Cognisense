package com.util;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * This is the database open helper. One database helper class operates
 * on one database. More tables can be added here.
 *	
 * @author Pengcheng
 */

public class DatabaseHelper extends SQLiteOpenHelper{
	private static final String DBNAME = "cognisense.db";
	private static final int VERSION = 1;
	public static final String INFO_TABLE = "INFO";
	public static final String HOME_TABLE = "HOME";
	public static final String FLOOR_TABLE = "FLOOR";
	public static final String ROOM_TABLE = "ROOM";
	public static final String USERTAG_TABLE = "USERTAG";
	
	public DatabaseHelper(Context context) {
		super(context, DBNAME, null, VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// create registration information table
		db.execSQL("CREATE TABLE IF NOT EXISTS " + INFO_TABLE +
				" (SERVICETYPE TEXT PRIMARY KEY NOT NULL," +
				"SERVICEID TEXT," +
				"INTENTFILTER TEXT);");
		
		// create home table 
		db.execSQL("CREATE TABLE IF NOT EXISTS " + HOME_TABLE +
				" (HOME TEXT PRIMARY KEY NOT NULL);");
		
		// create floor table
		db.execSQL("CREATE TABLE IF NOT EXISTS " + FLOOR_TABLE +
				" (FLOOR TEXT PRIMARY KEY NOT NULL);");
		
		// create room table
		db.execSQL("CREATE TABLE IF NOT EXISTS " + ROOM_TABLE +
				" (ROOM TEXT PRIMARY KEY NOT NULL);");
		
		// create user tag table
		db.execSQL("CREATE TABLE IF NOT EXISTS " + USERTAG_TABLE +
				" (USERTAG TEXT PRIMARY KEY NOT NULL);");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// drop all of the tables
		db.execSQL("DROP TABLE IF EXISTS " + INFO_TABLE); 
		db.execSQL("DROP TABLE IF EXISTS " + HOME_TABLE); 
		db.execSQL("DROP TABLE IF EXISTS " + FLOOR_TABLE); 
		db.execSQL("DROP TABLE IF EXISTS " + ROOM_TABLE); 
		db.execSQL("DROP TABLE IF EXISTS " + USERTAG_TABLE); 
		onCreate(db);
	}
}
