package com.realestate.ui.components.multispinner;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.AttributeSet;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.realestate.R;
import com.realestate.utils.Constants;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

/**
 * Created on 14/01/2016
 * Description:
 */
public class MultiSpinner extends Spinner implements DialogInterface.OnMultiChoiceClickListener, DialogInterface.OnCancelListener {

	private List<String> items;
	private boolean[] selected;
	private String defaultText;
	private MultiSpinnerListener listener;
	private TreeMap<String, Boolean> treeMapItems;

	public MultiSpinner(Context context) {
		super(context);
	}

	public MultiSpinner(Context arg0, AttributeSet arg1) {
		super(arg0, arg1);
	}

	public MultiSpinner(Context arg0, AttributeSet arg1, int arg2) {
		super(arg0, arg1, arg2);
	}

	@Override
	public void onClick(DialogInterface dialog, int which, boolean isChecked) {
		selected[which] = isChecked;
	}

	@Override
	public void onCancel(DialogInterface dialog) {
		// refresh textitems on spinner
		StringBuilder spinnerBuffer = new StringBuilder();
		for (int i = 0; i < items.size(); i++) {
			if (selected[i]) {
				spinnerBuffer.append(items.get(i));
				spinnerBuffer.append(Constants.CONCATDELIMETER);
			}
		}

		String spinnerText = "";
		spinnerText = spinnerBuffer.toString();
		int delimLen = Constants.CONCATDELIMETER.length();
		if (spinnerText.length() > delimLen) {
			spinnerText = spinnerText.substring(0, spinnerText.length() - delimLen);
		} else {
			spinnerText = defaultText;
		}

		ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(),
				//R.layout.textview_for_spinner,
				android.R.layout.simple_list_item_checked,
				new String[]{spinnerText});
		setAdapter(adapter);
		if (selected.length > 0) {
			listener.onItemsSelected(selected);
		}

	}

	@Override
	public boolean performClick() {
		AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
		builder.setTitle(defaultText);
		builder.setMultiChoiceItems(items.toArray(new CharSequence[items.size()]), selected, this);
		builder.setPositiveButton(android.R.string.ok,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.cancel();
					}
				});
		builder.setOnCancelListener(this);
		builder.show();
		return true;
	}

	/**
	 * Sets items to this spinner.
	 *
	 * @param items    A TreeMap where the keys are the values to display in the spinner
	 *                 and the value the initial selected state of the key.
	 * @param listener A MultiSpinnerListener.
	 */
	public void setItems(TreeMap<String, Boolean> items,
						 String spinnerTitle,
						 MultiSpinnerListener listener) {
		this.treeMapItems = items;
		this.items = new ArrayList<>(items.keySet());
		this.listener = listener;
		this.defaultText = spinnerTitle != Constants.SPINNERITEMS.EMPTYTERM.NAME ? spinnerTitle : getResources().getString(R.string.selectItems);

		List<Boolean> values = new ArrayList<>(this.treeMapItems.values());
		this.selected = new boolean[values.size()];
		for (int i = 0; i < items.size(); i++) {
			this.selected[i] = values.get(i);
		}

		// all text on the spinner
		ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(),
				//R.layout.textview_for_spinner,
				android.R.layout.simple_list_item_checked,
				new String[]{defaultText});
		setAdapter(adapter);

		// Set Spinner Text
		onCancel(null);
	}

	/**
	 * resets user selections
	 */
	public void resetSelections() {
		for (int i = 0; i < this.items.size(); i++) {
			this.selected[i] = false;
		}

		ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(),
				android.R.layout.simple_list_item_checked,
				new String[]{this.defaultText});
		setAdapter(adapter);
	}

	public interface MultiSpinnerListener {
		void onItemsSelected(boolean[] selected);
	}
}
