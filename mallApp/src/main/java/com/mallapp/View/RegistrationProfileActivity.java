package com.mallapp.View;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.google.gson.reflect.TypeToken;
import com.mallapp.Adapters.PlaceAutoCompleteAdapter;
import com.mallapp.Application.MallApplication;
import com.mallapp.Constants.ApiConstants;
import com.mallapp.Constants.AppConstants;
import com.mallapp.Constants.GlobelProfile;
import com.mallapp.Constants.SocialSharingConstants;
import com.mallapp.Model.UserProfileModel;
import com.mallapp.SharedPreferences.DataHandler;
import com.mallapp.utils.AppUtils;
import com.mallapp.utils.RegistrationController;
import com.mallapp.Model.FacebookProfileModel;
import com.mallapp.Model.PlaceAutoCompleteModel;
import com.mallapp.Model.UserLocationModel;
import com.mallapp.Model.UserProfile;
import com.mallapp.ServicesApi.RequestType;
import com.mallapp.SharedPreferences.SharedPreferenceManager;
import com.mallapp.imagecapture.ImageImportHelper;
import com.mallapp.imagecapture.ImageLoader;
import com.mallapp.imagecapture.Image_Scaling;
import com.mallapp.layouts.ParentDialog;
import com.mallapp.listeners.RegistrationUserListener;
import com.mallapp.socialsharing.SessionStore;
import com.mallapp.utils.GetCurrentDate;
import com.mallapp.utils.SharedInstance;
import com.mallapp.utils.Utils;

import org.json.JSONObject;

import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class RegistrationProfileActivity extends Activity implements RegistrationUserListener {

	//private static final String TAG = RegistrationProfileActivity.class.getSimpleName();

	private ImageImportHelper mImageImportHelper;
	private ImageView profileImageView;
	private EditText nameEditText, emailEditText;
	private TextView DOBEditText, locationTextView;
	private Date dateOfBirthday;

	//private String profile_image_array;
	private Button continueButton, syncFbButton;
	private RadioGroup gender_group;
	private RadioButton genderRadioButton, male, female;
	String nameString, DOBString, locationString, emailString, genderRadioButtonText;

	private static String FB_APP_ID = SocialSharingConstants.FB_APP_ID;

//	Facebook facebook = new Facebook(FB_APP_ID);

	Bitmap profile_image_bitmap = null;

	String profile_image_path= null;

	public ImageLoader imageLoader;
	com.android.volley.toolbox.ImageLoader volleyImageLoader;
	private RegistrationController controller;
	private String requestType;




	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.registration_profile);
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

		/*ActionBar actionBar = getActionBar();
		actionBar.hide();*/

		init();
		mImageImportHelper 	= ImageImportHelper.getInstance(RegistrationProfileActivity.this);




//		k/6DY49SZoyBXA7o/zbYuetdL78=
//		try {
//			PackageInfo info = getPackageManager().getPackageInfo("com.crowdeye.View",
//					PackageManager.GET_SIGNATURES);
//			for (Signature signature : info.signatures) {
//				MessageDigest md = MessageDigest.getInstance("SHA");
//				md.update(signature.toByteArray());
//				Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
//			}
//		} catch (NameNotFoundException e) {
//
//		} catch (NoSuchAlgorithmException e) {
//		}	//06-17 14:48:41.501: D/KeyHash:(4000): k/6DY49SZoyBXA7o/zbYuetdL78=

	}


	private void init() {

		nameEditText = (EditText) findViewById(R.id.user_name);
		emailEditText = (EditText) findViewById(R.id.user_email);
		locationTextView = (TextView) findViewById(R.id.user_location);
		DOBEditText = (TextView) findViewById(R.id.user_dob);

		continueButton = (Button) findViewById(R.id.continue_save);
		syncFbButton = (Button) findViewById(R.id.synce_facebook);

		profileImageView= (ImageView) findViewById(R.id.profile_image_edit);

		male 			= (RadioButton) findViewById(R.id.male);
		female 			= (RadioButton) findViewById(R.id.female);
		gender_group 	= (RadioGroup) findViewById(R.id.radioGroup1);

		imageLoader		= new ImageLoader(getApplicationContext());

//		Bitmap profile_bitmap 	= BitmapFactory.decodeResource(getResources(), R.drawable.parallax_avatar);
//		Image_Scaling.setRoundedImgeToImageView(getApplicationContext(), profileImageView, profile_bitmap);
//		profile_image_bitmap = profile_bitmap;

		controller = new RegistrationController(this);
		requestType = RequestType.GET_USER_PROFILE;
		controller.getUserProfile(ApiConstants.PROFILE_GET_URL_KEY,this);
		setUserProfile();

		DOBEditText.setOnClickListener(dobEditTextListener);
		locationTextView.setOnClickListener(locationTextViewListener);
		profileImageView.setOnClickListener(profileImageListener);
//		syncFbButton.setOnClickListener(loginButtonListener);
		continueButton.setOnClickListener(continueButtonListener);
	}


	private void selectProfileImage(String dialogTitle) {
		final CharSequence[] options = {"Take Photo", "Choose from Gallery", "Cancel"};
		AlertDialog.Builder builder = new AlertDialog.Builder(RegistrationProfileActivity.this);
		builder.setTitle(dialogTitle);
		builder.setItems(options, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int item) {

				if (options[item].equals("Take Photo")) {
					dispatchTakePictureIntent(ImageImportHelper.ACTION_TAKE_PHOTO_IMAGEVIEW);
				} else if (options[item].equals("Choose from Gallery")) {
					Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
					intent.setType("image/*");
					startActivityForResult(intent, ImageImportHelper.ACTION_TAKE_PHOTO_FROM_GALLERY);
				} else if (options[item].equals("Cancel")) {
					dialog.dismiss();
				}
			}
		});
		builder.show();
	}

	// New function for captureImageIntent
	private void dispatchTakePictureIntent(int actionCode) {
		mImageImportHelper.dispatchTakeAndSavePictureIntent(this, actionCode);
	}


	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == ImageImportHelper.ACTION_TAKE_PHOTO_IMAGEVIEW) {
			if (resultCode == RESULT_OK) {


				final Bitmap bitmapSelectedImage = (Bitmap) data.getExtras().get("data");

				profile_image_bitmap = bitmapSelectedImage;
				//bitmapSelectedImage = Image_Scaling.getImageOrintation(bitmapSelectedImage, picturePath);
				Image_Scaling.setRoundedImgeToImageView(getApplicationContext(), profileImageView, bitmapSelectedImage);


			}

		} else if (requestCode == ImageImportHelper.ACTION_TAKE_PHOTO_FROM_GALLERY) {

			Log.e("", "image from gallery ");

			if (resultCode == RESULT_OK) {


				try {

					Uri selectedImageUri = data.getData();
					String picturePath 	= getPath(selectedImageUri);

					Log.e("", "image from gallery path " + picturePath);
					//SharedPreferenceManager.saveString(RegistrationProfileActivity.this, GlobelProfile.profile_image, profile_image_array);
					Bitmap bitmapSelectedImage = BitmapFactory.decodeFile(picturePath);

					if (bitmapSelectedImage == null) {
						selectProfileImage("Re-Select Picture");
						return;
					}
					Log.e("", "image from gallery not null");
					profile_image_bitmap = bitmapSelectedImage;
					bitmapSelectedImage = Image_Scaling.getImageOrintation(bitmapSelectedImage, picturePath);
					Image_Scaling.setRoundedImgeToImageView(getApplicationContext(), profileImageView, bitmapSelectedImage);



				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}else{
//			facebook.authorizeCallback(requestCode, resultCode, data);
		}
	}


	public String getPath(Uri uri) {
		String[] projection = { MediaStore.Images.Media.DATA };
		Cursor cursor = getContentResolver().query(uri, projection, null, null, null);
		int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
		cursor.moveToFirst();
		return cursor.getString(column_index);
	}


	private int day;
	private int month;
	private int year;



	private void show_date() {
		if (dateOfBirthday == null) {
			final Calendar c = Calendar.getInstance();
			Log.d("","date default:"+c.getTime());

			year = c.get(Calendar.YEAR);
			month = c.get(Calendar.MONTH);
			day = c.get(Calendar.DAY_OF_MONTH);
			dateOfBirthday = c.getTime();
		}
		else{
			final  Calendar c = Calendar.getInstance();
			c.setTime(dateOfBirthday);
			Log.d("", "user date:" + c.getTime());

			year = c.get(Calendar.YEAR);
			month = c.get(Calendar.MONTH);
			day = c.get(Calendar.DAY_OF_MONTH);


		}

		// Launch Date Picker Dialog
		DatePickerDialog dpd = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
			@Override
			public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

				if ( view.isShown() ) {

					SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
					Calendar calendar = Calendar.getInstance();
					calendar.set(year, monthOfYear, dayOfMonth);

					System.out.println("Date : " + sdf.format(calendar.getTime()));
					System.out.println("Date : " + year + monthOfYear + dayOfMonth);

					Calendar c = Calendar.getInstance();
					c.set(year,monthOfYear,dayOfMonth);

					dateOfBirthday = c.getTime();

					DOBEditText.setText("" + sdf.format(calendar.getTime()));
					monthOfYear = monthOfYear + 1;
					// locationTextView.requestFocus();
				}
				// String dateS = String.format("%02d", monthOfYear);
				// String date_d = String.format("%02d", dayOfMonth);
			}
		}, year, month, day);

		Calendar maxDate = Calendar.getInstance();
		maxDate.set(Calendar.YEAR, 2000 );
		dpd.getDatePicker().setMaxDate(maxDate.getTimeInMillis());

		// dpd.getDatePicker().




		dpd.show();
	}


	protected void saveUserProfile() {

		nameString = nameEditText.getText().toString();
		emailString = emailEditText.getText().toString();
		locationString = locationTextView.getText().toString();

		DOBString = DOBEditText.getText().toString();
		int selectedId = gender_group.getCheckedRadioButtonId();

		genderRadioButton = (RadioButton) findViewById(selectedId);
		genderRadioButtonText = genderRadioButton.getText().toString();

		UserProfileModel userProfile = null;

//		if(SharedInstance.getInstance().getSharedHashMap().containsKey(AppConstants.PROFILE_DATA)) {
//			userProfile = (UserProfileModel) SharedInstance.getInstance().getSharedHashMap().get(AppConstants.PROFILE_DATA);
			userProfile = (UserProfileModel) DataHandler.getObjectPreferences(AppConstants.PROFILE_DATA, UserProfileModel.class);
//		}

		if(userProfile == null)
			userProfile = new UserProfileModel();

		userProfile.setFullName(nameString);
		userProfile.setEmail(emailString);
		userProfile.setDefaultLocationName(locationString);
		userProfile.setDeviceType("2");

		if(genderRadioButtonText.trim().startsWith("M")|| genderRadioButtonText.trim().startsWith("m"))
			userProfile.setGender("Male");
		else
			userProfile.setGender("Female");

		if(DOBString != null && DOBString.length()>0)
		{
			userProfile.setDOB(DOBString);
		}

		if(profile_image_bitmap != null){
//			String user_profile_image = encodeTobase64(profile_image_bitmap);
			userProfile.setImageBase64String(Image_Scaling.encodeTobase64(profile_image_bitmap));
		}
		requestType = com.mallapp.ServicesApi.RequestType.UPDATE_USER_PROFILE;

		controller.updateUserProfile(userProfile,this);

	}

	@Override
	public void onDataReceived(UserProfileModel userProfile) {

		if(requestType.equals(RequestType.UPDATE_USER_PROFILE)) {

			//SharedPreferenceUserProfile.SaveUserProfile(userProfile, getApplicationContext());
			SharedInstance.getInstance().getSharedHashMap().put(AppConstants.PROFILE_DATA, userProfile);
			DataHandler.updatePreferences(AppConstants.PROFILE_DATA, userProfile);

			Toast.makeText(getApplicationContext(), "Registration Successful!", Toast.LENGTH_LONG).show();

//			Intent tabIntent = new Intent(RegistrationProfileActivity.this, DashBoardCrowedEye.class);

			Intent intent = new Intent(RegistrationProfileActivity.this, Select_Favourite_Center.class);
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
				finishAffinity();
			}
			startActivity(intent);

		}else if(requestType.equals(RequestType.GET_USER_PROFILE)){

			if (userProfile != null) {

				if (userProfile.getFullName()== null || userProfile.getFullName() == ("null")) {
					nameEditText.setText("");
				} else {
					//Log.e("", "not null user nameEditText  "+ user_profile.getName());
					nameEditText.setText(userProfile.getFullName());
					userProfile.setFullName(userProfile.getFullName());
				}

				if (userProfile.getEmail() == null || userProfile.getEmail() == "null")
					emailEditText.setText("");
				else {
					emailEditText.setText(userProfile.getEmail());
					userProfile.setEmail(userProfile.getEmail());
				}

				if (userProfile.getDOB() == null || userProfile.getDOB() == "null")
					DOBEditText.setText("");
				else {
					String dob[] = userProfile.getDOB().split("T");
					DOBEditText.setText(dob[0]);
					userProfile.setDOB(dob[0]);
				}
				if (userProfile.getDefaultLocationName() == null || userProfile.getDefaultLocationName() == "null")
					DOBEditText.setText("");
				else {

					locationTextView.setText(userProfile.getDefaultLocationName());
					AppUtils.CityLatLong(RegistrationProfileActivity.this, userProfile.getDefaultLocationName());
				}

				if (userProfile.getImageURL() == null || userProfile.getImageURL()== "null")
					;
				else {
					profile_image_path = userProfile.getImageURL();
					userProfile.setFullName(userProfile.getFullName());
					Log.e("profile_image_path ", "profile_image_path = " + profile_image_path);

					// Retrieves an image specified by the URL, displays it in the UI.
					ImageRequest request = new ImageRequest(profile_image_path,
							new Response.Listener<Bitmap>() {
								@Override
								public void onResponse(Bitmap bitmap) {
									imageLoader.DisplayImage(profile_image_path, profileImageView, true);
								}
							}, 0, 0, null,
							new Response.ErrorListener() {
								public void onErrorResponse(VolleyError error) {
								}
							});
// Access the RequestQueue through your singleton class.
					MallApplication.getInstance().addToRequestQueue(request);
				}

				String gender = userProfile.getGender();
				if (gender != null && (gender.startsWith("male") || gender.startsWith("Male"))) {
					male.setChecked(true);
					//userProfile.setGender_male(true);
					userProfile.setGender("male");
				} else {
					female.setChecked(true);
					//userProfile.setGender_male(true);
					userProfile.setGender("female");
				}
				userProfile.setDeviceType(userProfile.getDeviceType());
				//userProfile.set(userProfile.getVersion());
				SharedInstance.getInstance().getSharedHashMap().put(AppConstants.PROFILE_DATA, userProfile);
				DataHandler.updatePreferences(AppConstants.PROFILE_DATA, userProfile);
				//SharedPreferenceUserProfile.SaveUserProfile(userProfile, getApplicationContext());

			}else {
				locationTextView.setText( userProfile.getDefaultLocationName() );
			}
		}

	}

	@Override
	public void onConnectionError() {

	}


	Dialog location_dialog;
	private ListView listView;

	protected void select_location() {

		location_dialog = new ParentDialog(RegistrationProfileActivity.this);
		location_dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		location_dialog.setContentView(R.layout.add_location);
		location_dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);

		Button confirm = (Button) location_dialog.findViewById(R.id.cancel);
		Button done = (Button) location_dialog.findViewById(R.id.done);

		UserLocationModel userLocationModel = null;

//		if(SharedInstance.getInstance().getSharedHashMap().containsKey(AppConstants.USER_LOCATION)) {
//			userLocationModel = (UserLocationModel) SharedInstance.getInstance().getSharedHashMap().get(AppConstants.USER_LOCATION);
			userLocationModel = (UserLocationModel) DataHandler.getObjectPreferences(AppConstants.USER_LOCATION, UserLocationModel.class);

//		}

		final PlaceAutoCompleteAdapter adapter1	= new PlaceAutoCompleteAdapter(this,R.id.item_code_list,userLocationModel);
		AutoCompleteTextView autoCompView	= (AutoCompleteTextView) location_dialog.findViewById(R.id.autoComplete);
		//autoCompView.setAdapter(adapter1);

		//autoCompView.setOnItemClickListener(locationItemClickListener);

		listView	= (ListView) location_dialog.findViewById(R.id.search_list);

		autoCompView.addTextChangedListener(new MyTextWatcher(adapter1));

		listView.setAdapter(adapter1);

		listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				PlaceAutoCompleteModel item = (PlaceAutoCompleteModel) parent.getItemAtPosition(position);
				locationTextView.setText("" + item.getDescription());
				AppUtils.CityLatLong(RegistrationProfileActivity.this,item.getDescription());
				location_dialog.dismiss();
			}
		});



		confirm.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				location_dialog.dismiss();
			}
		});


		done.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				location_dialog.dismiss();
			}
		});

		location_dialog.show();
	}



	private View.OnClickListener profileImageListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			selectProfileImage("Add Photo!");
		}
	};

	private View.OnClickListener continueButtonListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			if (nameEditText.getText().toString().trim().length() <= 0) {
				Toast toast = Toast.makeText(getApplicationContext(), getResources().getString(R.string.register_error_name_message), Toast.LENGTH_LONG);
				toast.setGravity(Gravity.CENTER| Gravity.CENTER_HORIZONTAL, 0, 0);
				toast.show();
				return;
			}else if (emailEditText.getText().toString() == null || emailEditText.getText().length() <= 0) {
				Toast toast = Toast.makeText(getApplicationContext(), getResources().getString(R.string.register_error_email_message), Toast.LENGTH_LONG);
				toast.setGravity(Gravity.CENTER | Gravity.CENTER_HORIZONTAL, 0, 0);
				toast.show();
				return;
			}else if(!Utils.isValidEmailAddress(emailEditText.getText().toString())){
				Toast toast = Toast.makeText(getApplicationContext(), getResources().getString(R.string.register_error_email_validity_message), Toast.LENGTH_LONG);
				toast.setGravity(Gravity.CENTER | Gravity.CENTER_HORIZONTAL, 0, 0);
				toast.show();
				return;
			}
			/**
			 * save user nameEditText in shared prefernece
			 */
			saveUserProfile();
		}
	};

	private View.OnClickListener locationTextViewListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			select_location();
		}
	};

	private View.OnClickListener dobEditTextListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			show_date();
		}
	};


	@Override
	protected void onResume() {
		super.onResume();
	}



	public static Bitmap decodeBase64(String input) {
		byte[] decodedByte = Base64.decode(input, 0);
		return BitmapFactory.decodeByteArray(decodedByte, 0, decodedByte.length);
	}


	private void setUserProfile() {

		volleyImageLoader = MallApplication.getInstance().getImageLoader();

//		if(SharedInstance.getInstance().getSharedHashMap().containsKey(AppConstants.USER_LOCATION)) {
//			UserLocationModel userLocationModel = (UserLocationModel) SharedInstance.getInstance().getSharedHashMap().get(AppConstants.USER_LOCATION);
			UserLocationModel userLocationModel = (UserLocationModel) DataHandler.getObjectPreferences(AppConstants.USER_LOCATION, UserLocationModel.class);
			String userLocation = userLocationModel.getCityName()+","+ userLocationModel.getCountryName();
			locationTextView.setText(userLocation);
//		}



		if(SharedInstance.getInstance().getSharedHashMap().containsKey(AppConstants.FACEBOOK_DATA)) { // hide the facebook sync button if already registered with facebook




			String imageUrl = "https://graph.facebook.com/#/picture?type=large";

//			FacebookProfileModel facebookProfileModel = (FacebookProfileModel) SharedInstance.getInstance().getSharedHashMap().get(AppConstants.FACEBOOK_DATA);
			FacebookProfileModel facebookProfileModel = (FacebookProfileModel) DataHandler.getObjectPreferences(AppConstants.FACEBOOK_DATA, FacebookProfileModel.class);

			if(facebookProfileModel.getId()!= null) {

				imageUrl = imageUrl.replace("#", facebookProfileModel.getId());

				SharedPreferenceManager.saveString(RegistrationProfileActivity.this, GlobelProfile.profile_image, imageUrl);

				Log.d("Image Loading", "imageUrl: " + imageUrl);

				volleyImageLoader.get(imageUrl, new com.android.volley.toolbox.ImageLoader.ImageListener() {

					@Override
					public void onErrorResponse(VolleyError error) {
						Log.e("Image Loading error", "Image Load Error: " + error.getMessage());
					}

					@Override
					public void onResponse(com.android.volley.toolbox.ImageLoader.ImageContainer response, boolean arg1) {
						if (response.getBitmap() != null) {
							// load image into imageview
							profile_image_bitmap = response.getBitmap();
							Image_Scaling.setRoundedImgeToImageView(getApplicationContext(), profileImageView, profile_image_bitmap);
							//profileImageView.setImageBitmap(response.getBitmap());
						}
					}
				});


				syncFbButton.setVisibility(View.INVISIBLE);

				if (facebookProfileModel.getName() != null)
					nameEditText.setText(facebookProfileModel.getName());

				if (facebookProfileModel.getEmail() != null)
					emailEditText.setText(facebookProfileModel.getEmail());

				if (facebookProfileModel.getBirthday() != null)
					DOBEditText.setText(facebookProfileModel.getBirthday());

				String gender = facebookProfileModel.getGender();
				if (gender != null)  {
					if(gender.startsWith("male") || gender.startsWith("Male"))
					male.setChecked(true);
				} else {
					female.setChecked(true);
				}

				SharedInstance.getInstance().getSharedHashMap().remove(AppConstants.FACEBOOK_DATA);
			}
		}
		else if(SharedInstance.getInstance().getSharedHashMap().containsKey(AppConstants.PROFILE_DATA))
		{
//				UserProfileModel userProfile = (UserProfileModel) SharedInstance.getInstance().getSharedHashMap().get(AppConstants.PROFILE_DATA);
				UserProfileModel userProfile = (UserProfileModel) DataHandler.getObjectPreferences(AppConstants.PROFILE_DATA, UserProfileModel.class);

			if(userProfile!= null){

				if (userProfile.getFullName() != null && userProfile.getFullName().length() > 0 )
					nameEditText.setText(userProfile.getFullName());

				if(userProfile.getEmail()!= null )
					emailEditText.setText( userProfile.getEmail() ) ;

				if(userProfile.getDOB()!= null) {
//					String unixDate = Utils.convertUnixDate(Long.parseLong(userProfile.getDOB()),"MMM dd, yyyy");
//					dateOfBirthday = GetCurrentDate.StringToDate("MMM dd, yyyy", unixDate);
					Log.d("date","date of birthday:"+dateOfBirthday);
					DOBEditText.setText(userProfile.getDOB());
				}

				// String gender = userProfile.getGender();
				if(userProfile.getGender()!= null && userProfile.getGender().equalsIgnoreCase("male")){
					male.setChecked(true);
				}else{
					female.setChecked(true);
				}

				String imageUrl = userProfile.getFileName();
				if(imageUrl!=null && imageUrl.length() > 0) {
					volleyImageLoader.get(imageUrl, new com.android.volley.toolbox.ImageLoader.ImageListener() {

						@Override
						public void onErrorResponse(VolleyError error) {
							Log.e("Image Loading error", "Image Load Error: " + error.getMessage());
						}

						@Override
						public void onResponse(com.android.volley.toolbox.ImageLoader.ImageContainer response, boolean arg1) {
							if (response.getBitmap() != null) {
								// load image into imageview
								profile_image_bitmap = response.getBitmap();
								Image_Scaling.setRoundedImgeToImageView(getApplicationContext(), profileImageView, profile_image_bitmap);

							}
						}
					});
				}

			}


		}

//		if(success)
//			startProfileActivity();
//		else
//			Toast.makeText(getApplicationContext(), getResources().getString(R.string.access_code_country_7), Toast.LENGTH_LONG).show();
	}


	private String changeBirthdayDateFormat(String encode_date) {
		//SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ", Locale.US);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
		SimpleDateFormat sdf_1 = new SimpleDateFormat("MMM dd , yyyy", Locale.ENGLISH);

		Date date = null;
		try {
			date = sdf.parse(encode_date);
			encode_date= sdf_1.format(date);

			Log.e("", "date of encodes string after parsing"+ date);
			Log.e("", "date of encodes string after parsing"+ encode_date);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} //<------ here `+` or `-`
//		System.out.println(date);
		return encode_date;
	}


	public class MyTextWatcher implements TextWatcher {
		private PlaceAutoCompleteAdapter lAdapter;

		public MyTextWatcher(PlaceAutoCompleteAdapter lAdapter) {
			this.lAdapter = lAdapter;
		}

		@Override
		public void beforeTextChanged(CharSequence cs, int start, int count, int after) {


			lAdapter.getFilter().filter(cs.toString().toLowerCase(), new Filter.FilterListener() {
				public void onFilterComplete(int count) {
					com.mallapp.utils.Log.d("log", "result count:" + count);
					if (count == 0) {
						listView.setVisibility(View.GONE);
					}
					else{
						listView.setVisibility(View.VISIBLE);
					}
				}
			});

		}

		@Override
		public void onTextChanged(CharSequence cs, int start, int before, int count) {
			com.mallapp.utils.Log.d("onTextChanged length:", "text:" + cs.toString() + "legnth:" + cs.length());
		}

		@Override
		public void afterTextChanged(Editable cs) {

			com.mallapp.utils.Log.d("text length:", "text:" + cs.toString() + "legnth:" + cs.length());
			lAdapter.getFilter().filter(cs.toString().toLowerCase(), new Filter.FilterListener() {
				public void onFilterComplete(int count) {
					com.mallapp.utils.Log.d("log", "result count:" + count);
					if (count == 0) {
						listView.setVisibility(View.GONE);
					} else {
						listView.setVisibility(View.VISIBLE);
					}
				}
			});

		}
	}
}