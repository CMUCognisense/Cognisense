package com.androidcommproc;

import com.util.DatabaseHelper;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.util.Log;

/**
 * This class exposes the interface to other applications so that other
 * applications are able to operate on the common database on the phone
 * 
 * @author Pengcheng
 */

public class RegistrationProvider extends ContentProvider{
	SQLiteDatabase db;
	private String TAG = "ContentProvider";

	private static final String AUTHORITY = "com.commproc.provider";
	private static final String INFO_BASE_PATH = "INFO";
	private static final String HOME_BASE_PATH = "HOME";
	private static final String FLOOR_BASE_PATH = "FLOOR";
	private static final String ROOM_BASE_PATH = "ROOM";
	private static final String USERTAG_BASE_PATH = "USERTAG";
	public static final Uri INFO_CONTENT_URI = Uri.parse("content://com.commproc.provider/" + INFO_BASE_PATH);
	public static final Uri HOME_CONTENT_URI = Uri.parse("content://com.commproc.provider/" + HOME_BASE_PATH);
	public static final Uri FLOOR_CONTENT_URI = Uri.parse("content://com.commproc.provider/" + FLOOR_BASE_PATH);
	public static final Uri ROOM_CONTENT_URI = Uri.parse("content://com.commproc.provider/" + ROOM_BASE_PATH);
	public static final Uri USERTAGE_CONTENT_URI = Uri.parse("content://com.commproc.provider/" + USERTAG_BASE_PATH);
	public static final int INFOS = 1;
	public static final int INFO = 2;	
	public static final int HOMES = 3;
	public static final int FLOORS = 4; 
	public static final int ROOMS = 5;
	public static final int USERTAGS = 6;
	private static final UriMatcher uriMatcher;
	private	DatabaseHelper dbHelper;

	// This is a uri matcher. New matching uri can be added here.
	// INFO and INFOS are the return code for the matching
	static {
		uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
		uriMatcher.addURI(AUTHORITY, INFO_BASE_PATH, INFOS);
		uriMatcher.addURI(AUTHORITY, INFO_BASE_PATH + "/*", INFO);
		uriMatcher.addURI(AUTHORITY, HOME_BASE_PATH, HOMES);
		uriMatcher.addURI(AUTHORITY, FLOOR_BASE_PATH, FLOORS);
		uriMatcher.addURI(AUTHORITY, ROOM_BASE_PATH, ROOMS);
		uriMatcher.addURI(AUTHORITY, USERTAG_BASE_PATH, USERTAGS);
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		//TODO
		Log.e(TAG, "delete is currently not supported");
		return 0;
	}

	@Override
	public String getType(Uri uri) {
		//TODO
		Log.e(TAG, "getType is currently not supported");
		return null;
	}

	/**
	 * This method expose the insertion interface to other applications
	 * so that other applications can insert new data to the database 
	 * using the content resolver
	 */
	@Override
	public Uri insert(Uri uri, ContentValues values) {
		String tableName;
		switch (uriMatcher.match(uri)) {
		case INFOS:
			tableName = DatabaseHelper.INFO_TABLE;
			break;
		case HOMES:
			tableName = DatabaseHelper.HOME_TABLE;
			break;
		case FLOORS:
			tableName = DatabaseHelper.FLOOR_TABLE;
			break;
		case ROOMS:
			tableName = DatabaseHelper.ROOM_TABLE;
			break;
		case USERTAGS:
			tableName = DatabaseHelper.USERTAG_TABLE;
			break;
		default:
			throw new IllegalArgumentException("Unsupported URI: " + uri);
		}
		long id = dbHelper.getWritableDatabase().insert(tableName, null, values);

		return Uri.parse("content://com.commproc.provider/" + tableName + "/" + id);
	}

	/**
	 * This method will be called when the application is started before
	 * onCreate of the application is called
	 */
	@Override
	public boolean onCreate() {
		// create the cognisense database
		dbHelper = new DatabaseHelper(getContext());

		return (dbHelper == null) ? false : true;
	}

	/**
	 * This method exposes query interface to other application so that
	 * other application can get data from the database using content resolver
	 */
	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
		
		switch (uriMatcher.match(uri)) {
		case INFOS:
			queryBuilder.setTables(DatabaseHelper.INFO_TABLE);
			break;
		case INFO:
			queryBuilder.setTables(DatabaseHelper.INFO_TABLE);
			queryBuilder.appendWhere("SERVICETYPE='"
					+ uri.getLastPathSegment() + "'");
			break;
		case HOMES:
			queryBuilder.setTables(DatabaseHelper.HOME_TABLE);
			break;
		case FLOORS:
			queryBuilder.setTables(DatabaseHelper.FLOOR_TABLE);
			break;
		case ROOMS:
			queryBuilder.setTables(DatabaseHelper.ROOM_TABLE);
			break;
		case USERTAGS:
			queryBuilder.setTables(DatabaseHelper.USERTAG_TABLE);
			break;
		default:
			throw new IllegalArgumentException("Unknown URI " + uri);
		}
		Cursor cursor = queryBuilder.query(dbHelper.getReadableDatabase(),
				projection, selection, selectionArgs, null, null, sortOrder);
		return cursor;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		//TODO
		Log.e(TAG, "update is currently not supported");
		return 0;
	}
}
