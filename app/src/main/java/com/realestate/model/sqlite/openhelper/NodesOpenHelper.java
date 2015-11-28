package com.realestate.model.sqlite.openhelper;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.realestate.utils.Common;
import com.realestate.utils.Constants;

/**
 * Created on 15/11/2015
 * Description:
 * Singleton pattern implementation.
 * Instantiate NodesOpenHelper using Application's context instead of Activity's one,
 * in order to avoid context leaks when Activity is destroyed.
 * check:
 * singleton pattern, http://stackoverflow.com/a/9286006
 */
public class NodesOpenHelper extends SQLiteOpenHelper{
	private static NodesOpenHelper dbHelperInstance = null;
	private Context dbHelperContext;

	private NodesOpenHelper(Context context) {
		super(context, Constants.SQLITE.DBNAME, null, Constants.SQLITE.DBVERSION);
		Common.log("NodesOpenHelper constructor");
		dbHelperContext = context;
	}

	public static NodesOpenHelper getInstance(Context context){
		if (dbHelperInstance == null) {
			dbHelperInstance = new NodesOpenHelper(context.getApplicationContext());
		}
		return dbHelperInstance;
	}

	@Override
	public void onCreate(SQLiteDatabase sqLiteDatabase) {
		Common.log("NodesOpenHelper onCreate");
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
		Common.log("NodesOpenHelper onUpgrade");
		sqLiteDatabase.execSQL("DROP TABLE IF EXISTS "+Constants.SQLITE.TABLES.NODES);
		onCreate(sqLiteDatabase);
	}
}
