package com.allwinner.theatreplayer.launcher.activity;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.ActionBar.LayoutParams;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.ContentObserver;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.allwinner.theatreplayer.launcher.LauncherApp;
import com.allwinner.theatreplayer.launcher.LauncherModel;
import com.allwinner.theatreplayer.launcher.LoadApplicationInfo;
import com.allwinner.theatreplayer.launcher.LoadLauncherConfig;
import com.allwinner.theatreplayer.launcher.PreferencesHelper;
import com.allwinner.theatreplayer.launcher.R;
import com.allwinner.theatreplayer.launcher.data.Category;
import com.allwinner.theatreplayer.launcher.data.CellInfo;
import com.allwinner.theatreplayer.launcher.data.CellViews;
import com.allwinner.theatreplayer.launcher.data.Constants;
import com.allwinner.theatreplayer.launcher.data.PosterInfo;
import com.allwinner.theatreplayer.launcher.service.ClearMemoryService;
import com.allwinner.theatreplayer.launcher.service.OrientatorService;
import com.allwinner.theatreplayer.launcher.service.UpdateService;
import com.allwinner.theatreplayer.launcher.transformers.FadePageTransformer;
import com.allwinner.theatreplayer.launcher.util.DownloadPicTask;
import com.allwinner.theatreplayer.launcher.util.DownloadTask;
import com.allwinner.theatreplayer.launcher.util.HttpConnectionUtil;
import com.allwinner.theatreplayer.launcher.util.HttpConnectionUtil.HttpMethod;
import com.allwinner.theatreplayer.launcher.util.ImageUtils;
import com.allwinner.theatreplayer.launcher.util.JsonParser;
import com.allwinner.theatreplayer.launcher.util.LayoutTraverserUtil;
import com.allwinner.theatreplayer.launcher.util.MD5;
import com.allwinner.theatreplayer.launcher.util.SharedPreUtil;
import com.allwinner.theatreplayer.launcher.util.Utils;
import com.allwinner.theatreplayer.launcher.view.CellViewTouchListener;
import com.allwinner.theatreplayer.launcher.view.CustomToast;

@SuppressLint("SimpleDateFormat")
public class LaunchActivity extends BaseActivity implements OnClickListener,
		OnPageChangeListener {
	private PreferencesHelper mDBHelper;
	private ViewPager mViewPager;
	private ArrayList<View> mPageViews;
	private LinearLayout mTopTextLayout;
	private TextView mTopTextView[];
	private ImageView mTopFocusLine;
	private ArrayList<CellViews> mCellViews;
	private LauncherModel mLauncherModel = null;
	private Handler mHandler = new Handler();
	private SimpleDateFormat mWeekdayFormat;
	private SimpleDateFormat mDateFormat;
	private DateFormatChangeObserver mDateFormatChangeObserver;
	private CellViewTouchListener mTouchListener = new CellViewTouchListener();
	private boolean mLiveAppAuthorized = false;
	private LinearLayout mFirstScreen, mSecondScreen, mThirdScreen,
			mFourthScreen, mFifthScreen;
	private int mPageCount = 4;
	public static final int TYPE_NURSERY_RHYMES = 0;
	public static final int TYPE_OTHER = TYPE_NURSERY_RHYMES + 1;

	private final BroadcastReceiver mHomeKeyEventReceiver = new HomeKeyEventBroadCastReceiver();
	private final BroadcastReceiver mAppBroadcastReceiver = new AppBroadcastReceiver();
	private boolean bIsShowing = false;

	private ArrayList<Category> mCategoryList = null;
	private ArrayList<CellInfo> mCellInfoList = null;
	private int mViewPageIndex = 0;
	private String Key_Int = "key_int";
	private int mSaveInt = -1;
	private ExecutorService LIMITED_TASK_EXECUTOR;
	private Map<String, DownloadPicTask> listTask = new Hashtable<String, DownloadPicTask>();
	public static final String PREFS_KEY = "com.allwinner.theatreplayer.launcher.activity.prefs";
	private int startLeft = 0;
	private boolean mIsFiveLayout;
	private LayoutTraverserUtil mLayoutTraverserUtil = null;
	private LayoutInflater inflater = null;
	private LinearLayout.LayoutParams mMatchLayoutParams = null;

	public static final int DOWNLOADAPK= 10;
	public static final int CHECKLICENSE= 11;
	
	public static final int CHECKLICENSE_FAIL= 12;
	public static final int CHECKLICENSE_SUC= 13;
	public final String TAG = "jim";
	DownloadTask mDownloadTask;
	//private boolean mIsFirstRunQQ = false;
	private boolean mIsFirstWindowFocus = true;
	private PagerAdapter mPagerAdapter = new PagerAdapter() {

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == arg1;
		}

		@Override
		public int getCount() {
			return mPageViews.size();
		}

		@Override
		public void destroyItem(View container, int position, Object object) {
			((ViewPager) container).removeView(mPageViews.get(position));
		}

		@Override
		public CharSequence getPageTitle(int position) {
			return null;
		}

		@Override
		public Object instantiateItem(View container, int position) {
			((ViewPager) container).addView(mPageViews.get(position));
			return mPageViews.get(position);
		}
	};

	@Override
	public void onSaveInstanceState(Bundle outState) {
		outState.putInt(Key_Int, mViewPageIndex);

	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getScreenSizeOfDevice2();
		setContentView(R.layout.activity_view_pager);
		if (savedInstanceState != null) {
			mSaveInt = savedInstanceState.getInt(Key_Int);
		}
		mLauncherModel = LauncherApp.getInstance().getModel();
		bIsShowing = true;
		LIMITED_TASK_EXECUTOR = (ExecutorService) Executors
				.newFixedThreadPool(4);
		initData();
		initViewPager();
		initViews();
		setViewListener();
		resgisterAllReceiver();
		startUpdateService();
		startOrientatorService();
		startClearMemoryService();

		if (LoadLauncherConfig.loadConfigFromSysXml(this)) {
			mLiveAppAuthorized = true;//open live mask
			mDBHelper.saveLiveAuthorized(true);
		}
		mViewPager.setPageTransformer(true, new FadePageTransformer());
		
		//检测升级
		
	}
	
	private void initData() {
		mCategoryList = LoadApplicationInfo.loadCellInfoFromSysXml(this);
		if (mCategoryList == null || mCategoryList.size() == 0) {
			CustomToast.showToast(this, R.string.data_incorrect);
			return;
		}
		if (mCellInfoList == null) {
			mCellInfoList = new ArrayList<CellInfo>();
		} else {
			mCellInfoList.clear();
		}		
		Category category = null;
		for (int i = 0; i < mCategoryList.size(); i++) {
			category = mCategoryList.get(i);
			if (category != null) {
				mCellInfoList.addAll(category.cellInfoList);
			}
		}
		for (CellInfo obj: mCellInfoList) {
//			Log.i("jim", "obj.title = "+obj.title);
//			Log.i("jim", "obj.title = "+obj.type);
//			Log.i("jim", "obj.title = "+obj.packageName);
		}
		LauncherApp.gCellInfoList = mCellInfoList;
		
	}

	@SuppressLint("InflateParams")
	private void initViewPager() {
		// mCellInfoList = new ArrayList<CellInfo>();
		inflater = LayoutInflater.from(this);
		mCellViews = new ArrayList<CellViews>();
		mPageViews = new ArrayList<View>();
		mLayoutTraverserUtil = new LayoutTraverserUtil();
		mMatchLayoutParams = new LinearLayout.LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		Category category = null;
		int categoryCount = mCategoryList.size();
//		Log.i("jim", "categoryCount size = "+categoryCount);
		
		for (int i = 0; i < categoryCount; i++) {
			category = mCategoryList.get(i);
			if (category != null) {
				addTopTextView(category.headline, categoryCount, i);
				addScreenLayout(category.type, category.cellInfoList.size());
				
//				Log.i("jim", "category.headline = "+category.headline);
//				Log.i("jim", "category.type = "+category.type);
//				Log.i("jim", "category.cellInfoList.size() = "+category.cellInfoList.size());
				// mCellInfoList.addAll(category.cellInfoList);
			}
		}
		mCellViews = mLayoutTraverserUtil.getCellViewsList();
		mPageCount = mPageViews.size();
		mViewPager = (ViewPager) findViewById(R.id.viewpager);
		mViewPager.setAdapter(mPagerAdapter);
		mViewPager.setOffscreenPageLimit(mPageCount);
		mViewPager.setOnPageChangeListener(this);
	}

	private void addScreenLayout(int type, int count) {
		switch (type) {
		case 0: {
			//第一页
			mFirstScreen = (LinearLayout) inflater.inflate(
					R.layout.activity_launch_screen1, null);

			LinearLayout screen_layout = null;
			if (count == 9) {
				screen_layout = (LinearLayout) inflater.inflate(
						R.layout.launch_screen_app2, null);
			} else {
				screen_layout = (LinearLayout) inflater.inflate(
						R.layout.launch_screen_app, null);
			}
			if (mMatchLayoutParams != null) {
				mFirstScreen.addView(screen_layout, mMatchLayoutParams);
				mLayoutTraverserUtil.addLayout(mFirstScreen);
				mPageViews.add(mFirstScreen);
			}
		}
			break;
		case 1: {
			//奥谷奇第一页
			mSecondScreen = (LinearLayout) inflater.inflate(
					R.layout.activity_launch_screen2, null);
			LinearLayout screen5_layout = null;
			
//			Log.i("jim", "case = 1 count = "+count);
			
			if(count == 3){
				screen5_layout = (LinearLayout) inflater.inflate(
						R.layout.launch_screen4_three, null);
			}
			else if (count == 4) {
				screen5_layout = (LinearLayout) inflater.inflate(
						R.layout.launch_screen4_four, null);
			} else if (count == 5) {
				screen5_layout = (LinearLayout) inflater.inflate(
						R.layout.launch_screen2_five, null);
			} else if (count == 6) {
				screen5_layout = (LinearLayout) inflater.inflate(
						R.layout.launch_screen2_six, null);
			} else if (count == 7) {
				screen5_layout = (LinearLayout) inflater.inflate(
						R.layout.launch_screen2_seven, null);
			}
			if (mMatchLayoutParams != null) {
				mSecondScreen.addView(screen5_layout, mMatchLayoutParams);
				mLayoutTraverserUtil.addLayout(mSecondScreen);
				mPageViews.add(mSecondScreen);
			}
		}
			break;
		case 2: {
			//第三页，我们的是第二页
			LinearLayout screen5_layout = null;
			if(count == 3){
				mThirdScreen = (LinearLayout) inflater.inflate(
						R.layout.launch_screen4_three, null);
			}
			else if (count == 4) {
				mThirdScreen = (LinearLayout) inflater.inflate(
						R.layout.launch_screen4_four, null);
			} else if (count == 5) {
				mThirdScreen = (LinearLayout) inflater.inflate(
						R.layout.launch_screen2_five, null);
			} else if (count == 6) {
				mThirdScreen = (LinearLayout) inflater.inflate(
						R.layout.launch_six_average, null);
			} else if (count == 7) {
				mThirdScreen = (LinearLayout) inflater.inflate(
						R.layout.launch_screen2_seven, null);
			} else{
				mThirdScreen = (LinearLayout) inflater.inflate(R.layout.activity_launch_screen3, null);
			}
			//
			mLayoutTraverserUtil.addLayout(mThirdScreen);
			mPageViews.add(mThirdScreen);
		}
			break;
		case 3: {
			mFourthScreen = (LinearLayout) inflater.inflate(
					R.layout.activity_launch_screen4, null);
			LinearLayout screen3_layout = null;
			if (count == 6) {
				mIsFiveLayout = true;
				screen3_layout = (LinearLayout) inflater.inflate(
						R.layout.launch_screen4_six, null);
			} else if (count == 5) {
				mIsFiveLayout = true;
				screen3_layout = (LinearLayout) inflater.inflate(
						R.layout.launch_screen4_five, null);
			} else {
				mIsFiveLayout = false;
				screen3_layout = (LinearLayout) inflater.inflate(
						R.layout.launch_screen4_four, null);
			}
			if (mMatchLayoutParams != null) {
				if (screen3_layout != null) {
					mFourthScreen.addView(screen3_layout, mMatchLayoutParams);
				}

			}

			mLayoutTraverserUtil.addLayout(mFourthScreen);
			mPageViews.add(mFourthScreen);

		}
			break;
		case 4: {
			mFifthScreen = (LinearLayout) inflater.inflate(
					R.layout.activity_launch_screen5, null);
			mLayoutTraverserUtil.addLayout(mFifthScreen);
			mPageViews.add(mFifthScreen);

		}
			break;
		default:
			break;
		}
	}

	private void addTopTextView(String title, int count, int index) {
		if (mTopTextLayout == null) {
			mTopTextLayout = (LinearLayout) findViewById(R.id.top_text_layout);
		}
		if (mTopFocusLine == null) {
			mTopFocusLine = (ImageView) findViewById(R.id.top_focus_line);
		}
		LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		layoutParams.leftMargin = (int) getResources().getDimensionPixelSize(
				R.dimen.navigation_home_to_left);
		LinearLayout.LayoutParams layoutParams2 = new LinearLayout.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		layoutParams2.leftMargin = (int) getResources().getDimensionPixelSize(
				R.dimen.navigation_home_margin_left);
		if (mTopTextView == null) {
			mTopTextView = new TextView[count];
		}

		TextView textView = new TextView(this);
		textView.setText(title);
//		if (index == 0) {
//			textView.setTextColor(getResources().getColor(R.color.blue));
//			mTopTextLayout.addView(textView, layoutParams);
//		} else {

			textView.setTextColor(getResources().getColor(R.color.white));
			if (mPageCount < 5) {
				mTopTextLayout.addView(textView, layoutParams2);
			} else {
				mTopTextLayout.addView(textView, layoutParams);
			}
//		}
		textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources()
				.getDimensionPixelSize(R.dimen.view_page_text_size));
		mTopTextView[index] = textView;
		if (mTopTextView[index] != null) {
			mTopTextView[index].setTag(index);
			mTopTextView[index].setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					int tag = (Integer) arg0.getTag();
					mViewPager.setCurrentItem(tag);

				}
			});
		}
	}

	private void initViews() {
		mDBHelper = PreferencesHelper.getInstance(getApplicationContext());
		mLiveAppAuthorized = mDBHelper.getLiveAuthorized();
		setCellDate();
	}

	@SuppressWarnings("unchecked")
	private void setCellDate() {
		if (mCellInfoList == null || mCellViews == null) {
			return;
		}
		int listCount = mCellInfoList.size();
		if (Constants.CELL_COUNT >= listCount) {
			CellInfo cellInfo = null;
			CellViews cellViews = null;
			ArrayList<CellInfo> cloneList = (ArrayList<CellInfo>) mCellInfoList
					.clone();
			
//			Log.i("jim", "cloneList.size() = "+cloneList.size());
			for (int i = 0; i < listCount; i++) {

				cellInfo = cloneList.get(i);
				cellViews = mCellViews.get(i);				
				if (cellInfo != null) {
					if (cellViews != null && cellViews.iLayout != null) {
						if (cellInfo.type == 4) {
							cellViews.iLayout.setVisibility(View.INVISIBLE);
						} else if (cellInfo.type == 3) {
							cellViews.iLayout.setVisibility(View.GONE);
						}
					}
					if (cellInfo.type == 4
							&& !LoadApplicationInfo.isInstalled(this,
									cellInfo.packageName)) {
						mCellInfoList.remove(cellInfo);
					}
				}
			}
			cloneList.clear();
			cloneList = null;
			listCount = mCellInfoList.size();
		}

		for (int i = 0; i < listCount; i++) {
			CellInfo cellInfo = mCellInfoList.get(i);
			CellViews cellViews = mCellViews.get(i);
			if (cellViews != null && cellViews.iLayout != null) {
				cellViews.iLayout.setTag(cellInfo);
				cellViews.iLayout.setVisibility(View.VISIBLE);
				if (!mIsFiveLayout
						&& cellInfo.type == 3
						&& !LoadApplicationInfo.isInstalled(this,
								cellInfo.packageName)) {
					cellViews.iLayout.setVisibility(View.GONE);
				}

				if (cellInfo.backgroundPic != null
						&& !cellInfo.backgroundPic.equals("")) {
					BitmapDrawable bitmapDrawable = (BitmapDrawable) ImageUtils
							.getShortcutIconFromSys(this,
									cellInfo.backgroundPic);
					
					if (bitmapDrawable != null) {
						Bitmap bitmap = bitmapDrawable.getBitmap();
						cellViews.iLayout.setImageBitmap(bitmap);
					}

				} else if (cellInfo.backgroundColour != null
						&& !cellInfo.backgroundColour.equals("")) {
					cellViews.iLayout.setBackgroundColor(Color
							.parseColor(cellInfo.backgroundColour));
				}

//				if (i != 0) {
//					if(cellInfo.packageName.equals("com.vst.live.allwinner")){
//						Log.i("jim", "55555555555======"+cellInfo.packageName);
//						
//					}
					Drawable iconDrawable = null;
					if (cellInfo.icon != null && !cellInfo.icon.equals("")) {
						iconDrawable = ImageUtils.getShortcutIconFromSys(this,
								cellInfo.icon);
					}
					if (iconDrawable == null && cellInfo.type == 4) {
						iconDrawable = ImageUtils.getAppIcon(
								cellInfo.packageName, this);
					}
					if (iconDrawable != null && cellViews.iconView != null) {
						cellViews.iconView.setImageDrawable(iconDrawable);
					}
					if (cellViews.textView != null) {
						cellViews.textView.setText(cellInfo.title);
					}
//				}
				
				if (cellViews.imgView != null) {
					Bitmap bitmap = ImageUtils.loadImageFromLocat(this,
							cellInfo.className);
					if (bitmap == null) {
						bitmap = ImageUtils.getBitmapByPicName(this, "img_"
								+ cellInfo.className);
					}

					if (bitmap != null) {
						cellViews.imgView.setImageBitmap(bitmap);
					}
				}
			}
		}
	}

	@SuppressLint("ClickableViewAccessibility")
	private void setViewListener() {

//		mWeatherContainer.setOnClickListener(this);
//		mWeatherContainer.setOnTouchListener(mTouchListener);
//		CellInfo weatherCellInfo = new CellInfo();
//		weatherCellInfo.packageName = Constants.PACKAGE_WEATHER;
//		mWeatherContainer.setTag(weatherCellInfo);
//		mDateTimeContainer.setOnClickListener(this);
//		mDateTimeContainer.setOnTouchListener(mTouchListener);
//		CellInfo dataTimeCellInfo = new CellInfo();
//		mDateTimeContainer.setTag(dataTimeCellInfo);
//		dataTimeCellInfo.packageName = Constants.PACKAGE_CALENDAR;
		for (int i = 0; i < mCellViews.size(); i++) {
			CellViews cellViews = mCellViews.get(i);
			if (cellViews.iLayout != null) {
				cellViews.iLayout.setOnClickListener(this);
				//cellViews.iLayout.setOnTouchListener(mTouchListener);
			}
		}
	}

	private void startUpdateService() {
		Intent startUpdateIntent = new Intent(this, UpdateService.class);
		startService(startUpdateIntent);		
	}

	private void startOrientatorService() {
		Intent intent = new Intent(this, OrientatorService.class);
		startService(intent);
	}

	private void startClearMemoryService() {
		Intent intent = new Intent(this, ClearMemoryService.class);
		startService(intent);
	}

	@Override
	protected void onPause() {
		super.onPause();
		bIsShowing = false;

	};

	@Override
	protected void onResume() {
		super.onResume();
		bIsShowing = true;
	}

	@Override
	protected void onStop() {
		super.onStop();
		if (LauncherApp.gIsFirstRunQQ && OrientatorService.mIsClose) {
			OrientatorService.forceLandscapeMode();
			LauncherApp.gIsFirstRunQQ = false;
		}
	}

	@Override
	protected void onDestroy() {
		unregisterAllReceiver();
		super.onDestroy();
	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		if (hasFocus && mSaveInt > 0 && mViewPageIndex != mSaveInt
				&& mViewPager != null) {
			if (mSaveInt < mViewPager.getChildCount()) {
				mViewPager.setCurrentItem(mSaveInt);
				mSaveInt = -1;
			}
		}
		if (hasFocus && mIsFirstWindowFocus) {
			mIsFirstWindowFocus = false;
		}
		super.onWindowFocusChanged(hasFocus);
	}

	@Override
	public void onBackPressed() {

	}

	@Override
	public void onClick(View view) {
		CellInfo cellInfo = (CellInfo) view.getTag();
		
//		Log.i("jim", "1111111111111=======cellInfo.packageName = "+cellInfo.packageName);
		
		
		if (cellInfo != null && mLauncherModel != null) {
//			Log.i("jim", "22222222222=======cellInfo.packageName = "+cellInfo.packageName);
			if (cellInfo.packageName.equals(Constants.PACKAGE_QQ)
					&& SharedPreUtil.isFirstRunQQ(Constants.PACKAGE_QQ)) {
				LauncherApp.gIsFirstRunQQ = true;
				SharedPreUtil.saveQQVersionNum(Constants.PACKAGE_QQ);
				if (!OrientatorService.mIsClose) {
					OrientatorService.closeOrientatorMode();
				}
			} else if (cellInfo.portrait.equals("true")) {
				if (LoadApplicationInfo.isInstalled(this, cellInfo.packageName)) {
					if (!OrientatorService.mIsClose) {
						OrientatorService.closeOrientatorMode();
					}
				}

			} else {
				if (OrientatorService.mIsClose) {
					OrientatorService.forceLandscapeMode();
				}
			}
//			Log.i("jim", "3333333=======cellInfo.packageName = "+cellInfo.packageName);
			if (cellInfo.packageName.equals(Constants.PACKAGE_VODTYPE)) {
				mLauncherModel.startVstByType(cellInfo.className,
						Constants.PACKAGE_VODTYPE);
//				Log.i("jim", "4444444444444=======cellInfo.packageName = "+cellInfo.packageName);
				
			} else if (cellInfo.packageName
					.equals(Constants.PACKAGE_VST_RECORD)
					|| cellInfo.packageName
							.equals(Constants.PACKAGE_VST_SETTING)) {
				mLauncherModel.startActivityByAction(cellInfo.packageName);
//				Log.i("jim", "5555555555=======cellInfo.packageName = "+cellInfo.packageName);
			} else if (cellInfo.packageName.equals(Constants.PACKAGE_GALLERY)) {
				if (cellInfo.className.equals(Constants.PACKAGE_CAMERA)) {
//					Log.i("jim", "66666666666=======cellInfo.packageName = "+cellInfo.packageName);
					try {
						ComponentName componentName = new ComponentName(
								Constants.PACKAGE_GALLERY,
								Constants.PACKAGE_CAMERA);
						Intent intent = new Intent();
						intent.setComponent(componentName);
						startActivity(intent);
					} catch (Exception e) {
						Toast.makeText(this,
								cellInfo.packageName + " not found",
								Toast.LENGTH_SHORT).show();
					}
				} else {
					mLauncherModel.startThirdApk(Constants.PACKAGE_GALLERY,
							cellInfo.className);
//					Log.i("jim", "7777777777777777=======cellInfo.packageName = "+cellInfo.packageName);
				}

			}
			else if(cellInfo.packageName.equals(Constants.PACKAGE_OCOCCI_VIDEO)){
//				Log.i("jim", "AAAAAAAAAAAA=======cellInfo.packageName = "+cellInfo.packageName);
				//if(cellInfo.className.equals("tv")){					
					
					Intent intent = new Intent(Intent.ACTION_MAIN);  
					intent.addCategory(Intent.CATEGORY_LAUNCHER);              
					ComponentName cn = new ComponentName(Constants.PACKAGE_OCOCCI_VIDEO, "com.ococci.video.activity.WelcomeActivity");             
					intent.putExtra("video_request_flag", cellInfo.className);
					intent.setComponent(cn);  
					startActivity(intent); 
					
				//}
			} else if(cellInfo.packageName.equals
			        (Constants.PACKAGE_HEALTH)){
				Intent intent = new Intent();
			    intent.setAction("com.vst.allwinner.intent.action.ChannelActivity");
			    intent.putExtra("cid",cellInfo.className);//
			    startActivity(intent);
			} else if( cellInfo.packageName.equals(Constants.PACKAGE_CHILDREN) ){
				
				Intent intent = new Intent();
				intent.setAction("myvst.intent.action.children.list.v2");
				intent.putExtra("uuid","424C4C7347456F6F1CF462");
				intent.putExtra("playerIndex",1);
				startActivity(intent);
			}			
			else if (cellInfo.className != null
					&& !cellInfo.className.equals("")) {
				mLauncherModel.startActivity(cellInfo.packageName,
						cellInfo.className);
				
//				Log.i("jim", "88888888888=======cellInfo.packageName = "+cellInfo.packageName);
			} else if (cellInfo.packageName.equals(Constants.PACKAGE_LIVE)) {
//				Log.i("jim", "999999999999999=======cellInfo.packageName = "+cellInfo.packageName);
				if (mLiveAppAuthorized) {
					mLauncherModel.startThirdApk(cellInfo.packageName);
				} else {
					CustomToast.showToast(this, R.string.live_unauthorized);
				}
			} else if(cellInfo.packageName.equals
			        ("com.allwinner.theatreplayer.launcher.AllAppActivity")){
//			    Log.i("Trim", cellInfo.packageName);
			    
			    Intent intent = new Intent(LaunchActivity.this, LocalAppActivity.class);
			    startActivity(intent);
			}else if(cellInfo.packageName.equals
			        ("com.android.settings")){
				//判断有没有R16的settings，如果有就直接执行，如果没有执行默认
				if(Utils.isPkgInstalled(LaunchActivity.this, Constants.PACKAGE_SETTINGS)){
					mLauncherModel.startThirdApk(Constants.PACKAGE_SETTINGS);
				}else{
					mLauncherModel.startThirdApk(cellInfo.packageName);
				}
			}
			else {
//				Log.i("Trim", cellInfo.packageName);
				mLauncherModel.startThirdApk(cellInfo.packageName);
			}
		}
	}

	private void resgisterAllReceiver() {
		registerHomeKeyEventReceiver();
		registerAppBroadcastReceiver();
	}

	private void unregisterAllReceiver() {
		unregisterReceiver(mHomeKeyEventReceiver);
		unregisterReceiver(mAppBroadcastReceiver);
	}

	private void registerHomeKeyEventReceiver() {
		IntentFilter HomeKeyfilter = new IntentFilter();
		HomeKeyfilter.addAction(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
		registerReceiver(mHomeKeyEventReceiver, HomeKeyfilter);
	}

//	private void registerUpdateWeatherReceiver() {
//		IntentFilter filter = new IntentFilter();
//		filter.addAction("com.allwinner.theatreplayer.weather.UPDATE_WEATHER");
//		filter.addAction("com.allwinner.action.TP_AUTHORIZE_LIVE");
//		registerReceiver(mReceiver, filter);
//	}

	private void registerAppBroadcastReceiver() {
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(UpdateService.APKDOWNSUC);
		intentFilter.addAction(UpdateService.APKONCHECKED);
		intentFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
		registerReceiver(mAppBroadcastReceiver, intentFilter);
	}

	Handler myHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
				case  DOWNLOADAPK:
					Log.i("jim", "开始下载");
					if(mDownloadTask!=null){
						mDownloadTask.reset();
					}
					mDownloadTask = new DownloadTask(LaunchActivity.this, new Handler());
					mDownloadTask.start();			
					break;
				case CHECKLICENSE:
					//检测一下是否存在被验证过的
					File file = new File(Utils.sdcardPathQQ
							+ "/.acted");
					if(!file.exists()){
//						Log.i(TAG, "launcher .acted 不存在");
						//开启验证
						new Thread(checkRunLauncher).start();
					}else{
						//存在就启动检测成功后启动检测下载apk
//						Log.i(TAG, "launcher .acted 存在 启动检测更新");
						Intent startUpdateIntent = new Intent(LaunchActivity.this, UpdateService.class);
						startService(startUpdateIntent);		
					}
					break;
				case CHECKLICENSE_FAIL:
					//未注册 弹出大屏通知
//					Log.i(TAG, "launcher CHECKLICENSE_FAIL 验证失败，弹出对话框");
					openDialog();	
					break;
				case CHECKLICENSE_SUC:
					//成功后启动检测下载apk
//					Log.i(TAG, "launcher CHECKLICENSE_SUC 验证成功，开启下载检测");
					Intent startUpdateIntent = new Intent(LaunchActivity.this, UpdateService.class);
					startService(startUpdateIntent);		
					break;
			}
		}
	};
	
	public void openDialog() {
//		Dialog dialog = null;
//		Builder builder = new Dialog.Builder(this).setTitle("验证失败").setMessage("请联系开发商");
//		dialog = builder.create();
//		dialog.show();
		
		Dialog dialog = new Dialog(this,R.style.CustomDialog);   
		dialog.setContentView(R.layout.launcher_fail);
		dialog.setOnKeyListener(keylistener);
		dialog.setCanceledOnTouchOutside(false);
		dialog.setCancelable(false);
		dialog.show();
	}
	
	OnKeyListener keylistener = new DialogInterface.OnKeyListener(){
        public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
            if (keyCode==KeyEvent.KEYCODE_BACK&&event.getRepeatCount()==0)
            {
             return true;
            }
            else
            {
             return false;
            }
        }
    } ;
	
	
	/**
	 * 检测
	 */
	Runnable checkRunLauncher = new Runnable() {
		@Override
		public void run() {
			HttpPost post = new HttpPost(com.iotqcloud.tools.Utils.SERVER_CHECK_LAUNCHER);
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("service_type", "launcher"));
			String para_serial = "mac="+Utils.getLocalMacAddress()+";device_name="+Utils.getDeviceNameForConfigSystem();
			params.add(new BasicNameValuePair("para_serial", para_serial));
//			
//			String deviceName = Utils.getDeviceNameForConfigSystem();
//			String imei = Utils.getDeviceIMEI(context);
//			String mac = Utils.getLocalMacAddress();
//			Log.i("jim", "设备device name = "+deviceName);
//			Log.i("jim", "设备imei = "+imei);
//			Log.i("jim", "设备mac = "+mac);
			
			
			HttpParams httpParameters = new BasicHttpParams();
			HttpConnectionParams.setSoTimeout(httpParameters, Utils.CHECK_TIMEOUT);
			HttpClient httpClient = new DefaultHttpClient(httpParameters);
			try {
				post.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
				HttpResponse response = httpClient.execute(post);
				if (response.getStatusLine().getStatusCode() == 200) {
					HttpEntity entity = response.getEntity();
					String data = EntityUtils.toString(entity, "UTF-8");					
					if(data.equals("suc")){
						Utils.saveUpdateJson(data, Utils.sdcardPathQQ + "/.acted");
						Message message = new Message();   
			            message.what = CHECKLICENSE_SUC;
			            myHandler.sendMessage(message);
					}else{
		                Message message = new Message();   
			            message.what = CHECKLICENSE_FAIL;
			            myHandler.sendMessage(message);
					}
				} else {
					Log.i(TAG, "1: response status:  "
							+ response.getStatusLine().getStatusCode());

				}
			} catch (Exception e) {
				Log.i(TAG, "Exception ERROR_UNKNOWN" + e.getMessage());
			}
		}
	};
	
	
	class HomeKeyEventBroadCastReceiver extends BroadcastReceiver {

		static final String SYSTEM_REASON = "reason";
		static final String SYSTEM_HOME_KEY = "homekey";

		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (action.equals(Intent.ACTION_CLOSE_SYSTEM_DIALOGS)) {
				String reason = intent.getStringExtra(SYSTEM_REASON);
				if (reason != null) {
					if (reason.equals(SYSTEM_HOME_KEY)) {
						if (bIsShowing) {
							if (mViewPager != null) {
								mViewPager.setCurrentItem(0);
							}
						}

					}
				}
			}
		}
	}

	class AppBroadcastReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (action.equals("android.intent.action.PACKAGE_ADDED")
					|| action.equals("android.intent.action.PACKAGE_REMOVED")) {
				Log.e("Launcher", "AppBroadcastReceiver action=" + action);
				initData();
				setCellDate();
			}			
			else if (intent.getAction().equals(UpdateService.APKONCHECKED)) {
				File file = new File(Utils.DOWNLOAD_PATH);
				if(file.exists()){
					//如果已经存在，对比下服务器的md5和本地的md5是否一样，一样就不下载了，不一样就删除，在下载
					String jsondatalocal;
					String md5 = "";
					try {
						jsondatalocal = Utils.readFile(Utils.sdcardPathQQ
								+ "/update.json");
						// 获取URL
						JSONObject jsonobj = new JSONObject(jsondatalocal);
						md5 = jsonobj.getString("md5");
					} catch (Throwable e) {
						 Log.d(TAG,""+e.getMessage());
					}
					
		            if (!MD5.checkMd5(md5, Utils.DOWNLOAD_PATH)) {
		                //如果md5值不一样，告诉他继续取下载
		                Message message = new Message();   
			            message.what = DOWNLOADAPK;
			            myHandler.sendMessage(message);
		            }else{
		            	//md5值一样，直接弹出来
		            	Intent i = new Intent(Intent.ACTION_VIEW);
						i.setDataAndType(Uri.parse("file://" + Utils.DOWNLOAD_PATH),
								"application/vnd.android.package-archive");
						i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						startActivity(i);
						if(mDownloadTask!=null && mDownloadTask.getRunningStatus() == 1){
							mDownloadTask.reset();
						}
		            }
				
				}else{
					//本地不存在，肯定去下载了
					Message message = new Message();   
					message.what = DOWNLOADAPK;
		            myHandler.sendMessage(message);
				}
			} else if (intent.getAction().equals(UpdateService.APKDOWNSUC)) {
				Intent i = new Intent(Intent.ACTION_VIEW);
				i.setDataAndType(Uri.parse("file://" + Utils.DOWNLOAD_PATH),
						"application/vnd.android.package-archive");
				i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(i);
				if(mDownloadTask!=null && mDownloadTask.getRunningStatus() == 1){
					mDownloadTask.reset();
				}
			}
			else if(action.equals(ConnectivityManager.CONNECTIVITY_ACTION) && Utils.checkConnectivity(context)){
//				Log.i(TAG, "launcher 开始检测");
				Message message = new Message();   
	            message.what = CHECKLICENSE;
	            myHandler.sendMessage(message);
			}
		}
	};

	private void setDateFormat() {
		String pattern = "MM" + getResources().getString(R.string.month) + "dd"
				+ getResources().getString(R.string.day);
		mDateFormat = new SimpleDateFormat(pattern);
	}

	private void setWeekdayFormat() {
		mWeekdayFormat = new SimpleDateFormat("EEEE");
	}

	class DateFormatChangeObserver extends ContentObserver {
		public DateFormatChangeObserver() {
			super(new Handler());
		}

		@Override
		public void onChange(boolean selfChange) {
			setDateFormat();
		}
	}

	private void highlightCurDot(int oldIndex, int newIndex) {

		Log.i("jim", "oldIndex = "+oldIndex+">>>>newIndex = "+newIndex);
		if (mTopTextView == null || mTopFocusLine == null || newIndex < 0
				|| newIndex > mPageCount - 1 || oldIndex == newIndex) {
			return;
		}
		mTopTextView[oldIndex].setTextColor(this.getResources().getColor(
				R.color.grey));
		mTopTextView[newIndex].setTextColor(this.getResources().getColor(
				R.color.blue));
		int moveDistance = mTopTextView[newIndex].getLeft()
				- mTopTextView[oldIndex].getLeft();
		TranslateAnimation animation = new TranslateAnimation(startLeft,
				startLeft + moveDistance, 0, 0);
		startLeft += moveDistance;
		animation.setDuration(300);
		animation.setFillAfter(true);
		mTopFocusLine.startAnimation(animation);
	}

	@Override
	public void onPageScrollStateChanged(int arg0) {

	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {

	}

	@Override
	public void onPageSelected(int arg0) {
		highlightCurDot(mViewPageIndex, arg0);
		mViewPageIndex = arg0;

	}

	private void getScreenSizeOfDevice2() {  
	    Point point = new Point();  
	    getWindowManager().getDefaultDisplay().getRealSize(point);  
	    DisplayMetrics dm = getResources().getDisplayMetrics();  
	    double x = Math.pow(point.x/ dm.xdpi, 2);  
	    double y = Math.pow(point.y / dm.ydpi, 2);  
	    double screenInches = Math.sqrt(x + y); 
	    
	    point = new Point();  
	    getWindowManager().getDefaultDisplay().getSize(point);  
	    
	    Log.i("Trim", "screenInches: "+screenInches+" point.x = "+point.x+" point.y = "+ point.y);
	    if (screenInches > 9 && point.x == 800) {
			setTheme(R.style.AppBaseThemeForV9);
		}
	}
	
	private void startDownloadThread() {
		new Thread(new Runnable() {

			@Override
			public void run() {
				LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
				map.put("channel", "91vst");
				map.put("version", "671");
				String result = null;
				HttpConnectionUtil conn = new HttpConnectionUtil();
				result = conn.syncConnect(Constants.VST_HOME_URL, map,
						HttpMethod.POST);
				ArrayList<PosterInfo> posterInfoList = JsonParser
						.parsePosterInfo(result);
				SharedPreferences sp = LaunchActivity.this
						.getSharedPreferences(PREFS_KEY, Context.MODE_PRIVATE);
				for (int i = 0; i < mCellInfoList.size(); i++) {
					CellInfo cellInfo = mCellInfoList.get(i);
					for (int j = 0; j < posterInfoList.size(); j++) {
						PosterInfo posterInfo = posterInfoList.get(j);
						if (cellInfo != null && posterInfo != null) {
							if (cellInfo.title.equals(posterInfo.title)
									&& cellInfo.className
											.equals(posterInfo.value)) {
								if (posterInfo.img != null
										&& !posterInfo.img.equals("")) {
									String strImgAdrr = sp.getString(
											posterInfo.value, "");
									if (!strImgAdrr.equals(posterInfo.img)) {
										DownloadPicTask asyncTask = new DownloadPicTask(
												LaunchActivity.this,
												posterInfo.value,
												posterInfo.img,
												mHandlerDownload);

										listTask.put(posterInfo.value,
												asyncTask);
										asyncTask.executeOnExecutor(
												LIMITED_TASK_EXECUTOR,
												posterInfo.img);
									}

								}
							}
						}
					}
				}

			}
		}).start();
	}

	@SuppressLint("HandlerLeak")
	private final Handler mHandlerDownload = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			int nMsg = msg.what;
			if (nMsg == 1) {
				Boolean result = (Boolean) msg.obj;
				DownloadPicTask asyncTask = null;
				String strTag = String.valueOf(msg.arg1);
				if (strTag == null || strTag.equals("")) {
					return;
				}
				if (listTask != null) {
					asyncTask = listTask.get(strTag);
				}
				if (result) {

					if (asyncTask != null) {
						SharedPreferences sp = LaunchActivity.this
								.getSharedPreferences(PREFS_KEY,
										Context.MODE_PRIVATE);
						sp.edit().putString(strTag, asyncTask.getPath());
						for (int i = 0; i < mCellInfoList.size(); i++) {
							CellInfo cellInfo = mCellInfoList.get(i);
							if (cellInfo.className.equals(strTag)) {
								CellViews cellViews = mCellViews.get(i);
								ImageView cellImg = cellViews.imgView;
								if (cellImg != null) {
									Bitmap bitmap = ImageUtils
											.loadImageFromLocat(
													LaunchActivity.this, strTag);
									if (bitmap != null) {
										cellImg.setImageBitmap(bitmap);
									}
								}
							}
						}
					}
					if (listTask != null) {
						listTask.remove(strTag);
					}
					if (listTask == null || listTask.size() <= 0) {
						Utils.log("No picture to download");
						return;
					}
				} else {
					if (asyncTask != null) {
						asyncTask.onCancelled();
						String tag = asyncTask.getTag();
						String picUrl = asyncTask.getPath();
						asyncTask = null;
						asyncTask = new DownloadPicTask(LaunchActivity.this,
								tag, picUrl, mHandlerDownload);
						asyncTask.executeOnExecutor(LIMITED_TASK_EXECUTOR);
					}
				}
			}
		}
	};
}
