package com.realestate.model.sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;

import com.realestate.model.SQLiteNode;
import com.realestate.model.sqlite.AppOpenHelper;
import com.realestate.utils.Common;
import com.realestate.utils.Constants;

/**
 * Created on 15/11/2015
 * Description:
 * Layer to store drupal nodes retrieved from REST API in SQLite.
 */
public class DrupalNodes {

	private AppOpenHelper openHelper;
	private SQLiteDatabase dbInstance;

	public DrupalNodes(Context context) {
		//this.openHelper = new AppOpenHelper(context, Constants.SQLITE.DBNAME, null, Constants.SQLITE.DBVERSION);
		this.openHelper = AppOpenHelper.getInstance(context);
	}

	public void insertNode(SQLiteNode node){
		Common.log("DrupalNodes insertNode");
		try {
			this.dbInstance = this.openHelper.getWritableDatabase();
			ContentValues content = new ContentValues();
			content.put(Constants.SQLITE.COLUMNS.NID, node.getNid());
			content.put(Constants.SQLITE.COLUMNS.TITLE, node.getTitle());
			content.put(Constants.SQLITE.COLUMNS.BODY, node.getBody());
			content.put(Constants.SQLITE.COLUMNS.COORDINATES, Common.doubleArr2String(node.getCoordinates(), Constants.CONCATDELIMETER));
			content.put(Constants.SQLITE.COLUMNS.IMAGES, node.getImages());
			this.dbInstance.insert(Constants.SQLITE.TABLES.NODES, null, content);
			this.dbInstance.close();
		}
		catch(SQLiteException e){
			Common.logError("SQLiteException @ DrupalNodes insertNode:" + e.getMessage());
		}
	}

	public SQLiteNode getNode(int nid) {
		Common.log("DrupalNodes getNode");
		SQLiteNode node = null;
		Cursor cur;
		try {
			this.dbInstance = this.openHelper.getReadableDatabase();
			cur = this.dbInstance.query(Constants.SQLITE.TABLES.NODES,
					new String[]{
							Constants.SQLITE.COLUMNS.NID,
							Constants.SQLITE.COLUMNS.TITLE,
							Constants.SQLITE.COLUMNS.BODY,
							Constants.SQLITE.COLUMNS.COORDINATES,
							Constants.SQLITE.COLUMNS.IMAGES
					},
					Constants.SQLITE.COLUMNS.NID + "=?",
					new String[]{String.valueOf(nid)},
					null, null, null, null);
			if (cur != null && cur.moveToFirst()) {
				node = new SQLiteNode(
						Integer.parseInt(cur.getString(0)),
						cur.getString(1),
						cur.getString(2),
						Common.stringToDoubleArr(cur.getString(3), Constants.CONCATDELIMETER),
						cur.getString(4),
						(float) -1);
			}
			cur.close();
			this.dbInstance.close();
		}
		catch(SQLiteException e){
			Common.logError("SQLiteException @ DrupalNodes getNode:" + e.getMessage());
		}
		return node;
	}

	public void clearNodes(){
		Common.log("DrupalNodes clearNodes");
		this.dbInstance = this.openHelper.getWritableDatabase();
		Cursor cur = this.dbInstance.rawQuery(""+
				"select DISTINCT tbl_name from sqlite_master where tbl_name = '"+Constants.SQLITE.TABLES.NODES+"'", null);
		if(cur != null) {
			this.dbInstance.execSQL("delete from " + Constants.SQLITE.TABLES.NODES);
			cur.close();
		}
		this.dbInstance.close();
	}

}
