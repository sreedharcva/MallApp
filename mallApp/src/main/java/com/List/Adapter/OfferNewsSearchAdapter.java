package com.List.Adapter;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.mallapp.Constants.AppConstants;
import com.mallapp.Model.MallActivitiesModel;
import com.mallapp.Model.Offers_News;
import com.mallapp.View.OffersDetailActivity;
import com.mallapp.View.R;
import com.mallapp.cache.AppCacheManager;
import com.mallapp.imagecapture.ImageLoader;
import com.mallapp.utils.GlobelOffersNews;

public class OfferNewsSearchAdapter extends ArrayAdapter<MallActivitiesModel> {

	//private static final String TAG = Offers_News_Adapter.class.getSimpleName();

	private Context 			context;
	private Activity			activity;
	String 						audience_type;
	private MallActivitiesModel offer_obj;
	
	ArrayList<MallActivitiesModel> 			offer_news_search;
	String endorsement_clicked_type;
	public ImageLoader imageLoader;
	
	public OfferNewsSearchAdapter(Context context, Activity activti, int textViewResourceId, ArrayList<MallActivitiesModel> array) {
		
		super(context,  textViewResourceId);
		this.context = context;
		this.activity= activti;
		this.offer_news_search	= array;
		imageLoader	= new ImageLoader(context);
	}

	public String getAudience_type() {
		return audience_type;
	}

	public void setAudience_type(String audience_type) {
		this.audience_type = audience_type;
		
	}

	@Override
	public int getCount() {
		return offer_news_search.size();
	}

	@Override
	public MallActivitiesModel getItem(int position) {
		return offer_news_search.get(position);
	}

	@Override
	public long getItemId(int position) {
		return (position);
	}

	@Override
	public int getPosition(MallActivitiesModel item) {
		return super.getPosition(item);
	}

	static class ViewHolder {
		TextView title, decs, center_name, shome_name;
		ImageButton is_fav;
		ImageView back_image;
		//RelativeLayout r1;
	}

	@SuppressLint("InflateParams")
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		
		final ViewHolder holder;
		View view= convertView;
		
		if (convertView == null) {
			LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			view = mInflater.inflate(R.layout.list_item_offers,null);
			
			holder = new ViewHolder();
			holder.title 		= (TextView) view.findViewById(R.id.title);
			holder.decs 		= (TextView) view.findViewById(R.id.center_city);
			holder.center_name 	= (TextView) view.findViewById(R.id.center_name);
			holder.shome_name 	= (TextView) view.findViewById(R.id.shop_name);
			holder.is_fav		= (ImageButton) view.findViewById(R.id.fav_center);
			holder.back_image	= (ImageView) view.findViewById(R.id.center_image);

			view.setTag(holder);
		}else {
			holder = (ViewHolder) view.getTag();
		}
///////////////////////////////////////////////////////////////////////////////////////////		

		offer_obj= getItem(position);
		imageLoader.DisplayImage(AppConstants.PREF_URI_KEY, holder.back_image);
		
		holder.title.setText(offer_obj.getActivityName());
		holder.decs.setText(offer_obj.getBriefText());
		holder.center_name.setText(offer_obj.getStartDate());
		holder.shome_name.setText(offer_obj.getMallName());
		
		final boolean fav	= offer_obj.isFav();
		if(fav)
			holder.is_fav.setImageResource(R.drawable.offer_fav_p);
		else
			holder.is_fav.setImageResource(R.drawable.offer_fav);
		
		
		holder.is_fav.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				offer_obj= getItem(position);
				if(!offer_obj.isFav()){
					holder.is_fav.setImageResource(R.drawable.offer_fav_p);
					offer_obj.setFav(true);
					AppCacheManager.updateOffersNews(context, offer_obj,position);
				}else{
					holder.is_fav.setImageResource(R.drawable.offer_fav);
					offer_obj.setFav(false);
					AppCacheManager.updateOffersNews(context, offer_obj, position);
				}
			}
		});
		
		view.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {

				offer_obj= getItem(position);
				GlobelOffersNews.offer_obj_mall= offer_obj;
				Intent intent= new Intent(activity, OffersDetailActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_MULTIPLE_TASK);                     
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				activity.getApplication().startActivity(intent);
				
//				if(endorsement_clicked_type!=null && 
//						endorsement_clicked_type.equals(
//								Offers_News_Constants.ENDORSEMENT_CLICK_TYPE)){
//					end_obj= getItem(position);
//					Globel_Endorsement.end_obj_chat= end_obj;
//					Globel_Endorsement.endorsement_click_type= null;
//					Intent resultIntent = new Intent();
//					resultIntent.putExtra("result","some result");
//					activity.setResult(Endorsement_Detail_Chat.REQUEST_CODE_FOR_ENDORSEMENT, resultIntent);
//					activity.finish();
//					
//				}else{
//					end_obj= getItem(position);//endorsement_filter.get(position);
//					Globel_Endorsement.end_obj= end_obj;
//					Intent intent= new Intent(activity, Endorsement_Overview_Detail.class);
//					intent.setFlags(Intent.FLAG_ACTIVITY_MULTIPLE_TASK);                     
//					intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//					activity.getApplication().startActivity(intent);
//				}
			}
		});
		return view;
	}

}
