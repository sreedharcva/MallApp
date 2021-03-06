package com.mallapp.View;

import java.io.IOException;
import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTabHost;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.DrawerLayout.DrawerListener;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.List.Adapter.NavDrawerListAdapter;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.chatdbserver.xmpp.IMManager;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.mallapp.Application.MallApplication;
import com.mallapp.Constants.ApiConstants;
import com.mallapp.Constants.AppConstants;
import com.mallapp.Constants.GlobelWebURLs;
import com.mallapp.Constants.MainMenuConstants;
import com.mallapp.Constants.Offers_News_Constants;
import com.mallapp.Fragments.CardTabFragments;
import com.mallapp.Fragments.MessagesTabFragments;
import com.mallapp.Fragments.OffersTabFragment;
import com.mallapp.Fragments.ProfileTabFragment;
import com.mallapp.Fragments.RewardsTabFragments;
import com.mallapp.Model.NavDrawerItem;
import com.mallapp.Model.UserProfileModel;
import com.mallapp.SharedPreferences.DataHandler;
import com.mallapp.SharedPreferences.SharedPreferenceUserProfile;
import com.mallapp.utils.AppUtils;
import com.mallapp.utils.Log;
import com.mallapp.utils.Utils;
import com.mallapp.utils.VolleyNetworkUtil;
import com.readystatesoftware.viewbadger.BadgeView;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

@SuppressLint("InflateParams")
public class DashboardTabFragmentActivity extends FragmentActivity implements OnItemClickListener {

    private static DrawerLayout mDrawerLayout;
    private static ListView mDrawerList;
    FragmentTabHost mTabHost;

    // nav drawer title
    //private CharSequence mDrawerTitle;

    // used to store app title
    //private CharSequence mTitle;

    // slide menu items
    private String[] navMenuTitles;
    private String[] navMenuIcons;

    private ArrayList<NavDrawerItem> navDrawerItems;
    private NavDrawerListAdapter adapter;
    Fragment fragment = null;
    private static ImageView drawer_logo;
    private static TextView drawer_text;
    static Context context;
    GoogleCloudMessaging gcmObj;
    String regId;

    UserProfileModel user_profile;
    RequestQueue mRequestQueue;
    IMManager imManager;
    BadgeView badgeChat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dashboard_activity);
//        ActionBar actionBar = getActionBar();
//		actionBar.hide();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        context = getApplicationContext();
        regId = DataHandler.getStringPreferences(AppConstants.PREF_GCM_DEVICE_ID);
        if (regId.equals("")) {
            gcmObj = GoogleCloudMessaging.getInstance(context);
            registerWithGcm(context);
            user_profile = (UserProfileModel) DataHandler.getObjectPreferences(AppConstants.PROFILE_DATA, UserProfileModel.class);
            mRequestQueue = Volley.newRequestQueue(MallApplication.appContext);
            imManager = IMManager.getIMManager(getApplicationContext());
            IM_SingUp();
        }
        initDrawer();
        initTabs();
    }

    @SuppressLint("InflateParams")
    private void initDrawer() {
        navDrawerItems = new ArrayList<NavDrawerItem>();
        getNavList();

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.list_slidermenu);

        View view1 = getLayoutInflater().inflate(R.layout.drawer_header_list_item, null);
        View view2 = getLayoutInflater().inflate(R.layout.drawer_footer_list_item, null);

        drawer_logo = (ImageView) view1.findViewById(R.id.selected_center_logo);
        drawer_text = (TextView) view1.findViewById(R.id.side_menu_header);

        mDrawerList.addHeaderView(view1);
        mDrawerList.addFooterView(view2);

        adapter = new NavDrawerListAdapter(getApplicationContext(), this, navDrawerItems);
        mDrawerList.setAdapter(adapter);
        mDrawerList.setOnItemClickListener(this);
        mDrawerLayout.setDrawerListener(mDrawerListener);

        // first position
//        fragment = new DashBoardFragmentTabs(uiHandler);
//		getSupportFragmentManager().beginTransaction().replace(R.id.frame_container, fragment).commit();
//		mDrawerLayout.closeDrawer(mDrawerList);

    }

    private void getNavList() {
        navMenuTitles = getResources().getStringArray(R.array.nav_drawer_items);
        navMenuIcons = getResources().getStringArray(R.array.nav_drawer_icons);
        for (int i = 0; i < navMenuTitles.length; i++) {
            String image_nam = navMenuIcons[i];
            int res = getResources().getIdentifier(image_nam, "drawable", getPackageName());
            navDrawerItems.add(new NavDrawerItem(navMenuTitles[i], res));
        }
    }


    private DrawerListener mDrawerListener = new DrawerListener() {
        @Override
        public void onDrawerStateChanged(int status) {
            drawerLogoCondtions();
        }

        @Override
        public void onDrawerSlide(View view, float slideArg) {

        }

        @Override
        public void onDrawerOpened(View view) {
            mDrawerLayout.openDrawer(mDrawerList);
//			drawerLogoCondtions();

        }

        @Override
        public void onDrawerClosed(View view) {
            mDrawerLayout.closeDrawer(mDrawerList);
        }
    };


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
                            long id) {
        displayView(position);
    }

    private void displayView(int position) {
        // update the main content by replacing fragments
        //fragment= null;
        switch (position) {
            case 1: {

                MainMenuConstants.uiHandler = uiHandler;
                DashboardTabFragmentActivity.uiHandler.sendEmptyMessage(1);
                //finish();
                Intent activity = new Intent(DashboardTabFragmentActivity.this, DashboardTabFragmentActivity.class);
                //Bundle b = new Bundle();
                //b.putString("favourite", subcatagory);
                //activity.putExtras(b);
                startActivity(activity);
            }
            break;

            case 2:
                if (MainMenuConstants.SELECTED_CENTER_NAME != null
                        && MainMenuConstants.SELECTED_CENTER_NAME.length() > 0
                        && !MainMenuConstants.SELECTED_CENTER_NAME.equals("all")
                        && !MainMenuConstants.SELECTED_CENTER_NAME.equals(MainMenuConstants.AUDIENCE_FILTER_ALL)) {
                    if (Utils.isInternetAvailable(context)) {
                        MainMenuConstants.uiHandler = uiHandler;
                        DashboardTabFragmentActivity.uiHandler.sendEmptyMessage(1);
                        Intent activity = new Intent(DashboardTabFragmentActivity.this, MallDetailActivity.class);
                        activity.putExtra(Offers_News_Constants.MALL_PLACE_ID, AppUtils.MallIdSelection(context, OffersTabFragment.pos));
                        startActivity(activity);
                    } else {
                        AppUtils.internetDialog(this, getResources().getString(R.string.no_internet), getResources().getString(R.string.network_error));
                    }
                } else
                    showdailog();

                break;

            case 3:
                if (MainMenuConstants.SELECTED_CENTER_NAME != null
                        && MainMenuConstants.SELECTED_CENTER_NAME.length() > 0
                        && !MainMenuConstants.SELECTED_CENTER_NAME.equals("all")
                        && !MainMenuConstants.SELECTED_CENTER_NAME.equals(MainMenuConstants.AUDIENCE_FILTER_ALL)) {

                    if (Utils.isInternetAvailable(context)) {
                        MainMenuConstants.uiHandler = uiHandler;
                        Intent activity = new Intent(DashboardTabFragmentActivity.this, ShopMainMenuActivity.class);
                        activity.putExtra(Offers_News_Constants.MALL_PLACE_ID, AppUtils.MallIdSelection(context, OffersTabFragment.pos));
                        startActivity(activity);
                    } else {
                        AppUtils.internetDialog(this, getResources().getString(R.string.no_internet), getResources().getString(R.string.network_error));
                    }
                } else
                    showdailog();

                break;


            case 4:
                if (MainMenuConstants.SELECTED_CENTER_NAME != null
                        && MainMenuConstants.SELECTED_CENTER_NAME.length() > 0
                        && !MainMenuConstants.SELECTED_CENTER_NAME.equals("all")
                        && !MainMenuConstants.SELECTED_CENTER_NAME.equals(MainMenuConstants.AUDIENCE_FILTER_ALL)) {

                    if (Utils.isInternetAvailable(context)) {
                        MainMenuConstants.uiHandler = uiHandler;
                        DashboardTabFragmentActivity.uiHandler.sendEmptyMessage(1);
                        Intent activity = new Intent(DashboardTabFragmentActivity.this, RestaurantMainMenuActivity.class);
                        activity.putExtra(Offers_News_Constants.MALL_PLACE_ID, AppUtils.MallIdSelection(context, OffersTabFragment.pos));
                        startActivity(activity);
                    } else {
                        AppUtils.internetDialog(this, getResources().getString(R.string.no_internet), getResources().getString(R.string.network_error));
                    }
                } else
                    showdailog();

                break;

            case 5:
                if (MainMenuConstants.SELECTED_CENTER_NAME != null
                        && MainMenuConstants.SELECTED_CENTER_NAME.length() > 0
                        && !MainMenuConstants.SELECTED_CENTER_NAME.equals("all")
                        && !MainMenuConstants.SELECTED_CENTER_NAME.equals(MainMenuConstants.AUDIENCE_FILTER_ALL)) {

                    if (Utils.isInternetAvailable(context)) {
                        MainMenuConstants.uiHandler = uiHandler;
                        DashboardTabFragmentActivity.uiHandler.sendEmptyMessage(1);
                        Intent activity = new Intent(DashboardTabFragmentActivity.this, ServicesMainMenuActivity.class);
                        activity.putExtra(Offers_News_Constants.MALL_PLACE_ID, AppUtils.MallIdSelection(context, OffersTabFragment.pos));
                        startActivity(activity);
                    } else {
                        AppUtils.internetDialog(this, getResources().getString(R.string.no_internet), getResources().getString(R.string.network_error));
                    }
                } else
                    showdailog();
                break;


            case 6:
                if (MainMenuConstants.SELECTED_CENTER_NAME != null
                        && MainMenuConstants.SELECTED_CENTER_NAME.length() > 0
                        && !MainMenuConstants.SELECTED_CENTER_NAME.equals("all")
                        && !MainMenuConstants.SELECTED_CENTER_NAME.equals(MainMenuConstants.AUDIENCE_FILTER_ALL)) {

                    if (Utils.isInternetAvailable(context)) {
                        MainMenuConstants.uiHandler = uiHandler;
                        DashboardTabFragmentActivity.uiHandler.sendEmptyMessage(1);
                        Intent activity = new Intent(DashboardTabFragmentActivity.this, FloorOverviewActivity.class);
                        activity.putExtra(Offers_News_Constants.MALL_PLACE_ID, AppUtils.MallIdSelection(context, OffersTabFragment.pos));
                        activity.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(activity);
                        finish();
                    } else {
                        AppUtils.internetDialog(this, getResources().getString(R.string.no_internet), getResources().getString(R.string.network_error));
                    }
                } else
                    showdailog();
                break;

            case 7: {

                MainMenuConstants.uiHandler = uiHandler;
                DashboardTabFragmentActivity.uiHandler.sendEmptyMessage(1);
                Intent activity = new Intent(DashboardTabFragmentActivity.this, DiscountCalculatorActivity.class);
                activity.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(activity);
            }
            break;


            case 8: {

                if (Utils.isInternetAvailable(context)) {
                    MainMenuConstants.uiHandler = uiHandler;
                    DashboardTabFragmentActivity.uiHandler.sendEmptyMessage(1);
                    Intent activity = new Intent(DashboardTabFragmentActivity.this, FavouritesMainMenuActivity.class);
                    activity.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(activity);
                    finish();
                } else {
                    AppUtils.internetDialog(this, getResources().getString(R.string.no_internet), getResources().getString(R.string.network_error));
                }
            }
            break;

            default:
                break;
        }
    }


    private void initTabs() {

        mTabHost = (FragmentTabHost) findViewById(android.R.id.tabhost);
        mTabHost.setup(this, getSupportFragmentManager(), android.R.id.tabcontent);
        mTabHost.setup(this, getSupportFragmentManager(), R.id.tabcontent2);

        // Home Tab
        View view1 = LayoutInflater.from(DashboardTabFragmentActivity.this).inflate(R.layout.tab_indicator_offers, null);
        View view2 = LayoutInflater.from(DashboardTabFragmentActivity.this).inflate(R.layout.tab_indicator_messages, null);
        View view3 = LayoutInflater.from(DashboardTabFragmentActivity.this).inflate(R.layout.tab_indicator_cards, null);
//		View view4 = LayoutInflater.from(DashboardTabFragmentActivity.this).inflate(R.layout.tab_indicator_rewards, null);
        View view5 = LayoutInflater.from(DashboardTabFragmentActivity.this).inflate(R.layout.tab_indicator_profile, null);

//		View view1 = getLayoutInflater().inflate(R.layout.tab_indicator_offers, 	mTabHost, false);
//		View view2 = getLayoutInflater().inflate(R.layout.tab_indicator_messages, 	mTabHost, false);
//		View view3 = getLayoutInflater().inflate(R.layout.tab_indicator_cards, 		mTabHost, false);
//		View view4 = getLayoutInflater().inflate(R.layout.tab_indicator_rewards, 	mTabHost, false);
//		View view5 = getLayoutInflater().inflate(R.layout.tab_indicator_profile, 	mTabHost, false);

        mTabHost.addTab(mTabHost.newTabSpec(getResources().getString(R.string.tabs_offer)).setIndicator(view1), OffersTabFragment.class, null);
        mTabHost.addTab(mTabHost.newTabSpec(getResources().getString(R.string.tabs_message)).setIndicator(view2), MessagesTabFragments.class, null);
        mTabHost.addTab(mTabHost.newTabSpec(getResources().getString(R.string.tabs_cards)).setIndicator(view3), CardTabFragments.class, null);
//		mTabHost.addTab(mTabHost.newTabSpec("Rewards")	.setIndicator(view4),RewardsTabFragments.class, null);
        mTabHost.addTab(mTabHost.newTabSpec(getResources().getString(R.string.tabs_profile)).setIndicator(view5), ProfileTabFragment.class, null);

    }

    private void showdailog() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

        alertDialogBuilder.setTitle(getResources().getString(R.string.center_select_title));

        alertDialogBuilder.setMessage(getResources().getString(R.string.center_select_message));

        alertDialogBuilder.setPositiveButton(getResources().getString(R.string.ok),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {

                    }
                });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }


    @SuppressLint("HandlerLeak")
    public static Handler uiHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    Log.e("handler", "Handler works");
                    Log.e("", "	MainMenuConstants.SELECTED_CENTER_NAME = " + MainMenuConstants.SELECTED_CENTER_NAME);
                    if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
                        mDrawerLayout.closeDrawer(mDrawerList);
                    } else {

                        if (MainMenuConstants.SELECTED_CENTER_NAME != null
                                && MainMenuConstants.SELECTED_CENTER_NAME.length() > 0
                                && !MainMenuConstants.SELECTED_CENTER_NAME.equals("all")
                                && !MainMenuConstants.SELECTED_CENTER_NAME.equals(MainMenuConstants.AUDIENCE_FILTER_ALL)) {

                            drawer_text.setVisibility(View.GONE);
                            drawer_logo.setVisibility(View.VISIBLE);
                            setCenter_logo(MainMenuConstants.SELECTED_CENTER_LOGO, context);

                        } else {
                            drawer_text.setVisibility(View.VISIBLE);
                            drawer_logo.setVisibility(View.GONE);
                        }


                        mDrawerLayout.openDrawer(mDrawerList);
                    }
                    break;
            }
        }

        ;

    };


    public static void setCenter_logo(String center_logo, Context context) {

		/*int mDstWidth 	= context.getResources().getDimensionPixelSize(R.dimen.center_logo_width);
        int mDstHeight 	= context.getResources().getDimensionPixelSize(R.dimen.center_logo_height);
		
		int imageResource 	= context.getResources().getIdentifier(center_logo, "drawable", context.getPackageName());
		Drawable d 			= context.getResources().getDrawable(imageResource);
		Bitmap bitmap 		= ((BitmapDrawable) d).getBitmap();
		
		d = new BitmapDrawable(context.getResources(), Bitmap.createScaledBitmap(bitmap, mDstWidth,mDstHeight, true));
		drawer_logo.setImageDrawable(d);*/
        Picasso.with(context).load(center_logo).placeholder(R.drawable.mallapp_placeholder).error(R.drawable.mallapp_placeholder).into(drawer_logo);

    }

    public static void drawerLogoCondtions() {
        if (MainMenuConstants.SELECTED_CENTER_NAME != null
                && MainMenuConstants.SELECTED_CENTER_NAME.length() > 0
                && !MainMenuConstants.SELECTED_CENTER_NAME.equals("all")
                && !MainMenuConstants.SELECTED_CENTER_NAME.equals(MainMenuConstants.AUDIENCE_FILTER_ALL)) {

            drawer_text.setVisibility(View.GONE);
            drawer_logo.setVisibility(View.VISIBLE);
            setCenter_logo(MainMenuConstants.SELECTED_CENTER_LOGO, context);

        } else {
            drawer_text.setVisibility(View.VISIBLE);
            drawer_logo.setVisibility(View.GONE);
        }
    }

    private void registerWithGcm(final Context con) {

        new AsyncTask<Void, Void, String>() {

            @Override
            protected String doInBackground(Void... params) {
                String msg = "";
                try {

                    if (gcmObj == null) {
                        gcmObj = GoogleCloudMessaging.getInstance(con);
                    }
                    regId = gcmObj.register(AppConstants.GCM_PROJECT_ID);
                    msg = "Registration ID :" + regId;
                    android.util.Log.e("RegID", "" + msg);

                } catch (IOException ex) {
                    msg = "Error :" + ex.getMessage();
                }
                return msg;
            }

            @Override
            protected void onPostExecute(String msg) {
                if (!TextUtils.isEmpty(regId)) {
                    DataHandler.updatePreferences(AppConstants.PREF_GCM_DEVICE_ID, regId);
                    try {
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("UserId", SharedPreferenceUserProfile.getUserId(context));
                        jsonObject.put("DeviceId", regId);
                        jsonObject.put("DeviceType", "2");
                        VolleyNetworkUtil volleyNetworkUtil = new VolleyNetworkUtil(context);
                        volleyNetworkUtil.PostNotSet(ApiConstants.POST_DEVICE_IDENTITY, jsonObject);
                    } catch (Exception ex) {

                    }
                    android.util.Log.e("GCM ", "response");
                } else {
                    android.util.Log.e("GCM E", "error ");
                }
            }
        }.execute(null, null, null);
    }

    public void IM_SingUp() {

        String url = "http://ec2-52-29-132-13.eu-central-1.compute.amazonaws.com:9090/userservice?user=" + GlobelWebURLs.ce_user + user_profile.getUserId() + "&pass=" + user_profile.getUserId();
        Log.e("Post Url", "" + url);
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                url, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                Log.e("IM sign up", response.toString());
                JSONObject status = null;
                String code = "";
                try {
                    status = response.getJSONObject("status");
                    code = status.getString("code");
                    if (code.equals("200")) {
                        IM_Company_Register();
                    }


                } catch (JSONException e) {
                    e.printStackTrace();

                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.e("Error", "Error: " + error.getMessage());

            }
        });

        // Adding request to request queue
        mRequestQueue.add(jsonObjReq);
    }

    public void IM_Company_Register() {
        String url = "http://ec2-52-29-132-13.eu-central-1.compute.amazonaws.com:9090/companyservice?cmd=addusr&companyid=" + GlobelWebURLs.ce_company + "&username=" + GlobelWebURLs.ce_user + user_profile.getUserId();
        Log.e("Post Url", "" + url);

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                url, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                Log.e("IM sign up Company", response.toString());
                JSONObject status = null;
                String code = "";
                try {
                    status = response.getJSONObject("status");
                    code = status.getString("code");
                    if (code.equals("200")) {
                        imManager.InitIMManager(GlobelWebURLs.IM_SERVER, GlobelWebURLs.ce_user + user_profile.getUserId(), user_profile.getUserId());
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.e("Error", "Error: " + error.getMessage());

            }
        });
        mRequestQueue.add(jsonObjReq);
    }

}
