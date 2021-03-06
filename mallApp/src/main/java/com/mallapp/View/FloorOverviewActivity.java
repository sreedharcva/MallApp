package com.mallapp.View;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.List.Adapter.FloorOverviewAdapter;
import com.mallapp.Constants.ApiConstants;
import com.mallapp.Constants.Offers_News_Constants;
import com.mallapp.Model.FloorOverViewModel;
import com.mallapp.listeners.FloorsDataListener;
import com.mallapp.utils.VolleyNetworkUtil;

import java.util.ArrayList;

public class FloorOverviewActivity extends SlidingDrawerActivity implements View.OnClickListener, FloorsDataListener {

    private TextView heading;
    ListView floor_list;
    private ImageButton open_navigation, open_drawer;
    VolleyNetworkUtil volleyNetworkUtil;
    String url, mallPlaceId;
    FloorOverviewAdapter floorOverviewAdapter;

    LinearLayout error_layout, rootLayout;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_floor_overview);
        init();

        mallPlaceId = getIntent().getStringExtra(Offers_News_Constants.MALL_PLACE_ID);
        url = ApiConstants.GET_MALL_FLOOR_URL_KEY + mallPlaceId;
        volleyNetworkUtil = new VolleyNetworkUtil(this);
        volleyNetworkUtil.GetFloorOverview(url, this);
    }

    void init() {

        error_layout	= (LinearLayout) findViewById(R.id.error_layout);

        floor_list = (ListView) findViewById(R.id.list_floor);
        open_navigation = (ImageButton) findViewById(R.id.back);
        open_drawer = (ImageButton) findViewById(R.id.navigation_drawer);
        heading				= (TextView) 			findViewById(R.id.heading);

        open_navigation.setOnClickListener(this);
        open_drawer.setOnClickListener(this);
        open_navigation.setVisibility(View.GONE);
        open_drawer.setVisibility(View.VISIBLE);
        heading.setText(getResources().getString(R.string.heading_floors));
    }

    @Override
    public void onClick(View v) {

        if (open_navigation.getId() == v.getId()) {
            DashboardTabFragmentActivity.uiHandler.sendEmptyMessage(1);
            finish();

        } else if (open_drawer.getId() == v.getId()) {
            SlidingDrawerActivity.uiHandler.sendEmptyMessage(1);
        }
    }

    @Override
    public void onDataReceived(ArrayList<FloorOverViewModel> floorOverViewModels) {
        if (floorOverViewModels.size()>0){
            floorOverviewAdapter = new FloorOverviewAdapter(getApplicationContext(),this, R.layout.list_floor_view, floorOverViewModels);
            floor_list.setAdapter(floorOverviewAdapter);
        }else {
            String serverError = context.getResources().getString(R.string.floor_error_message);
            Toast.makeText(context, serverError, Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void OnError() {
        error_layout.setVisibility(View.VISIBLE);
        floor_list.setVisibility(View.GONE);
    }
}
