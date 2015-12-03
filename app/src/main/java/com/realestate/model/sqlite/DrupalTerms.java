package com.realestate.model.sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

import com.realestate.model.SQLiteTerm;
import com.realestate.utils.Common;
import com.realestate.utils.Constants;

import java.util.ArrayList;
import java.util.List;

/**
 * Created on 22/09/2015
 * Description:
 * sqlite implementation to store drupal terms
 * drupal terms will be used as filters in searching equipment
 */
public class DrupalTerms {

	private AppOpenHelper openHelper;
	private SQLiteDatabase dbInstance;

	public DrupalTerms(Context context) {
		Common.log("DrupalTerms constructor");
		//this.openHelper = new AppOpenHelper(context, Constants.SQLITE.DBNAME, null, Constants.SQLITE.DBVERSION);
		this.openHelper = AppOpenHelper.getInstance(context);
		this.dbInstance = this.openHelper.getWritableDatabase();
	}

	public void closeConnection(){
		this.dbInstance.close();
	}

	public void deleteAllTerms(){
		Common.log("DrupalTerms deleteAllTerms");
		Cursor cur = this.dbInstance.rawQuery(""+
				"select DISTINCT tbl_name from sqlite_master where tbl_name = '"+Constants.SQLITE.TABLES.TERMS+"'", null);
		if(cur != null) {
			this.dbInstance.execSQL("delete from " + Constants.SQLITE.TABLES.TERMS);
			cur.close();
		}
	}

	public void deleteTerm(String termName){
		Common.log("DrupalTerms deleteTerm");
		String[] args = {termName};
		int deleteResult = this.dbInstance.delete(Constants.SQLITE.TABLES.TERMS, Constants.SQLITE.COLUMNS.NAME+"=?", args);
		if(deleteResult > 0){
			Common.log("SQLite: delete term " + termName);
		}
	}

	public void updateTerm(SQLiteTerm SQLiteTerm){
		Common.log("DrupalTerms updateTerm");
		ContentValues content = new ContentValues();
		String[] args = {Integer.toString(SQLiteTerm.getTid())};
		content.put(Constants.SQLITE.COLUMNS.NAME, SQLiteTerm.getName());
		int update = this.dbInstance.update(Constants.SQLITE.TABLES.TERMS, content, Constants.SQLITE.COLUMNS.TID + "=?", args);
	}

	public void insertTerm(SQLiteTerm SQLiteTerm){
		Common.log("DrupalTerms insertTerm");
		ContentValues content = new ContentValues();
		content.put(Constants.SQLITE.COLUMNS.TID, SQLiteTerm.getTid());
		content.put(Constants.SQLITE.COLUMNS.NAME, SQLiteTerm.getName());
		content.put(Constants.SQLITE.COLUMNS.VOCABULARY, SQLiteTerm.getVocabulary());
		this.dbInstance.insert(Constants.SQLITE.TABLES.TERMS, null, content);
	}

	public List<SQLiteTerm> getVocabularyTerms(String vocabulary) {
		Common.log("DrupalTerms getVocabularyTerms of " + vocabulary);
		ArrayList<SQLiteTerm> SQLiteTerms = new ArrayList<SQLiteTerm>();
		String[] args = {vocabulary};
		try {
			Cursor cur = this.dbInstance.rawQuery("" +
					" SELECT " + Constants.SQLITE.COLUMNS.TID + "," + Constants.SQLITE.COLUMNS.NAME +
					" FROM " + Constants.SQLITE.TABLES.TERMS +
					" WHERE " + Constants.SQLITE.COLUMNS.VOCABULARY + " = ? ", args);
			while (cur.moveToNext()) {
				SQLiteTerms.add(new SQLiteTerm(cur.getInt(0), cur.getString(1), vocabulary));
			}
			cur.close();
		} catch (SQLiteException e) {
			Common.logError("SQLiteException @ DrupalTerms getVocabularyTerms:" + e.getMessage());
		}
		return SQLiteTerms;
	}
}
