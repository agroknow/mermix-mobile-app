package com.mermix.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.mermix.R;
import com.mermix.custom.CustomActivity;
import com.mermix.ui.fragments.EquipmentDetail;
import com.mermix.utils.Common;
import com.mermix.utils.Constants;


public class EquipmentDetailActivity extends CustomActivity {

	/* (non-Javadoc)
 * @see com.food.custom.CustomActivity#onCreate(android.os.Bundle)
 */
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		Common.log("EquipmentDetail onCreate");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_searchresult);

		addFragment();
	}

	/**
	 * Attach the appropriate MapViewer fragment with activity.
	 */
	private void addFragment()
	{
		getSupportFragmentManager().beginTransaction()
				.replace(R.id.content_frame, new EquipmentDetail()).commit();
	}
    /* (non-Javadoc)
	 * @see android.app.Activity#onOptionsItemSelected(android.view.MenuItem)
	 */
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        if (item.getItemId() == android.R.id.home)
        {
            finish();
            return true;
        }
        else if (item.getItemId() == R.id.menu_locate)
            startActivity(new Intent(this, MapViewActivity.class));
        else if (item.getItemId() == R.id.menu_search) {
			Intent i = new Intent(this, MainActivity.class);
			i.putExtra(Constants.INTENTVARS.FRAGMENTPOS, 1);
			startActivity(i);
			finish();
			return true;
		}
			//TODO start MainActivity with intent variable the 'Search' fragment's position to start
        return super.onOptionsItemSelected(item);
    }

}
