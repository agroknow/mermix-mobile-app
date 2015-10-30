package com.realestate.ui.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.realestate.model.SQLiteTerm;

import java.util.List;

/**
 * Created on 27/09/2015
 * Description:
 * extend ArrayAdapter to insert SQLiteTerms in spinners
 */
public class SpinnerTermAdapter extends ArrayAdapter<SQLiteTerm> {

	private Context context;
	private List<SQLiteTerm> SQLiteTerms;
	private int resource;
	public SpinnerTermAdapter(Context context, int resource, List<SQLiteTerm> SQLiteTerms) {
		super(context, resource, SQLiteTerms);

		this.resource = resource;
		this.context = context;
		this.SQLiteTerms = SQLiteTerms;
	}

	@Override
	public int getCount() {
		return SQLiteTerms.size();
	}

	@Override
	public SQLiteTerm getItem(int position) {
		return SQLiteTerms.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		TextView lbl = (TextView) super.getView(position, convertView, parent);
		lbl.setText(SQLiteTerms.get(position).getName());
		return lbl;
	}

	@Override
	public View getDropDownView(int position, View convertView, ViewGroup parent) {
		TextView lbl = (TextView) super.getDropDownView(position, convertView, parent);
		lbl.setText(SQLiteTerms.get(position).getName());
		return lbl;
	}
}
