package com.mermix.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mermix.R;
import com.mermix.custom.CustomFragment;
import com.mermix.model.common.Pojo;
import com.mermix.ui.DataRetrieve;
import com.mermix.ui.activities.MainActivity;
import com.mermix.utils.Common;
import com.mermix.utils.net.args.FeedListArgs;
import com.mermix.utils.net.args.UrlArgs;

/**
 * Created by vasilis on 2/24/16.
 */
public class Home extends CustomFragment implements DataRetrieve {


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        Common.log("Home onCreateView");
        View v = inflater.inflate(R.layout.home, null);

        setHasOptionsMenu(false);
        setTouchNClick(v.findViewById(R.id.btnSearch));
        setTouchNClick(v.findViewById(R.id.btnSubmit));
        setTouchNClick(v.findViewById(R.id.btnMapSearch));

        return v;
    }


    @Override
    public void onStart() {
        super.onStart();
        Common.log("Home onStart");
    }

    @Override
    public void onStop() {
        super.onStop();
        Common.log("Home onStop");
    }
    /* (non-Javadoc)
     * @see android.support.v4.app.Fragment#onPause()
     */
    @Override
    public void onPause()
    {
        Common.log("Home onPause");
        super.onPause();
    }

    /* (non-Javadoc)
     * @see android.support.v4.app.Fragment#onDestroy()
     */
    @Override
    public void onDestroy()
    {
        Common.log("Home onDestroy");
        super.onDestroy();
    }


    @Override
    public void onClick(View v) {
        super.onClick(v);
        if (v.getId() == R.id.btnSearch) {
            ((MainActivity) getActivity()).launchFragment(2);
        }
        if (v.getId() == R.id.btnSubmit) {
            ((MainActivity) getActivity()).launchFragment(4);
        }
        if (v.getId() == R.id.btnMapSearch) {
            ((MainActivity) getActivity()).launchFragment(3);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        //    getMenuInflater().inflate(R.menu.feed, menu);
        //    menu.findItem(R.id.menu_sort).setVisible(false);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void updateUI(Pojo apiResponseData) {

    }

    @Override
    public void startRequestService(UrlArgs urlArgs) {

    }
}
