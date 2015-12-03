package com.realestate.model.sqlite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.realestate.utils.Common;
import com.realestate.utils.Constants;

/**
 * Created on 03/12/2015
 * Description:
 * Implement one SQLiteOpenHelper class for all sqlite tables,
 * check why here: http://blog.foxxtrot.net/2009/01/a-sqliteopenhelper-is-not-a-sqlitetablehelper.html
 *
 * Singleton pattern implementation.
 * Instantiate NodesOpenHelper using Application's context instead of Activity's one,
 * in order to avoid context leaks when Activity is destroyed.
 * check:
 * singleton pattern, http://stackoverflow.com/a/9286006
 */
public class AppOpenHelper extends SQLiteOpenHelper {
	private static AppOpenHelper dbHelperInstance = null;
	private Context dbHelperContext;

	public AppOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
		super(context, name, factory, version);
		Common.log("AppOpenHelper constructor");
		dbHelperContext = context;
	}

	public static AppOpenHelper getInstance(Context context){
		if (dbHelperInstance == null) {
			dbHelperInstance = new AppOpenHelper(context.getApplicationContext(), Constants.SQLITE.DBNAME, null, Constants.SQLITE.DBVERSION);
		}
		return dbHelperInstance;
	}

	@Override
	public void onCreate(SQLiteDatabase sqLiteDatabase) {
		Common.log("AppOpenHelper onCreate");
		//TERMS
		sqLiteDatabase.execSQL("CREATE TABLE " + Constants.SQLITE.TABLES.TERMS + " "
				+ "(" + Constants.SQLITE.COLUMNS.VOCABULARY + " TEXT NOT NULL, "
				+ Constants.SQLITE.COLUMNS.NAME + " TEXT NOT NULL, "
				+ Constants.SQLITE.COLUMNS.TID + " INTEGER PRIMARY KEY)");
		//NODES
		sqLiteDatabase.execSQL("CREATE TABLE " + Constants.SQLITE.TABLES.NODES +" "
				+"("
				+Constants.SQLITE.COLUMNS.NID+" INTEGER PRIMARY KEY,"
				+Constants.SQLITE.COLUMNS.TITLE+" TEXT NOT NULL, "
				+Constants.SQLITE.COLUMNS.BODY+" TEXT , "
				+Constants.SQLITE.COLUMNS.COORDINATES+" TEXT , "
				+Constants.SQLITE.COLUMNS.IMAGES+" TEXT"
				+")");
	}

	@Override
	public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
		Common.log("AppOpenHelper onUpgrade");
		sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + Constants.SQLITE.TABLES.TERMS);
		sqLiteDatabase.execSQL("DROP TABLE IF EXISTS "+Constants.SQLITE.TABLES.NODES);
		onCreate(sqLiteDatabase);
	}
}
