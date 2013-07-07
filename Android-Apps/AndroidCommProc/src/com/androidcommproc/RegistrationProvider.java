package com.androidcommproc;
/**
 * This class exposes the interface to other applications so that other
 * applications are able to operate on the common database on the phone
 */
import com.util.DatabaseHelper;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.util.Log;

public class RegistrationProvider extends ContentProvider{
	SQLiteDatabase db;
	private String TAG = "ContentProvider";

	private static final String AUTHORITY = "com.commproc.provider";
	private static final String INFO_BASE_PATH = "INFO";
	public static final Uri INFO_CONTENT_URI = Uri.parse("content://com.commproc.provider/" + INFO_BASE_PATH);
	public static final int INFOS = 1;
	public static final int INFO = 2;	
	private static final UriMatcher uriMatcher;
	private	DatabaseHelper dbHelper; 

	// This is a uri matcher. New matching uri can be added here.
	// INFO and INFOS are the return code for the matching
	static {
		uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
		uriMatcher.addURI(AUTHORITY, INFO_BASE_PATH, INFOS);
		uriMatcher.addURI(AUTHORITY, INFO_BASE_PATH + "/*", INFO);
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
		switch (uriMatcher.match(uri)) {
		case INFOS:
			//do nothing
			break;
		default:
			throw new IllegalArgumentException("Unsupported URI: " + uri);
		}
		long id = dbHelper.getWritableDatabase().insert(DatabaseHelper.INFO_TABLE, null, values);

		return Uri.parse(INFO_CONTENT_URI + "/" + id);
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
		queryBuilder.setTables(DatabaseHelper.INFO_TABLE);
		
		switch (uriMatcher.match(uri)) {
		case INFOS:
			break;
		case INFO:
			queryBuilder.appendWhere("SERVICETYPE='"
					+ uri.getLastPathSegment() + "'");
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
