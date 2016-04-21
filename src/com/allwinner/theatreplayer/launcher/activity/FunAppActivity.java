package com.allwinner.theatreplayer.launcher.activity;

import java.sql.Array;
import java.util.ArrayList;

import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;

import com.allwinner.theatreplayer.launcher.LoadApplicationInfo;
import com.allwinner.theatreplayer.launcher.R;
import com.allwinner.theatreplayer.launcher.data.ApplicationInfo;
import com.allwinner.theatreplayer.launcher.data.Constants;
import com.allwinner.theatreplayer.launcher.view.GridViewAdatper;
import com.allwinner.theatreplayer.launcher.view.GridViewAdatper.GridViewItemClickListener;

public class FunAppActivity extends BaseActivity implements OnPageChangeListener,
		GridViewItemClickListener {
	
	private ViewPager mViewPager;//页视图 控件
	private ArrayList<View> mPageViews;//存放View的数组
	private ArrayList<ApplicationInfo> mAppInfoList;//存放ApplicationInfo的数组
	private int mPageCount = 0;//多少个页
	private LinearLayout mDotLayout = null;
	private ImageView[] mDots;
	private PagerAdapter mPagerAdapter = new PagerAdapter() {//页 适配器

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
			LinearLayout screenLayout = (LinearLayout) mPageViews.get(position);
			screenLayout.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View view) {
					FunAppActivity.this.finish();
				}
			});
			
			int appNumInScreen = ((position + 1) < mPageCount) ? Constants.CELL_COUNT_PRE_SCREEN
					: (mAppInfoList.size() - (mPageCount - 1)
							* Constants.CELL_COUNT_PRE_SCREEN);
			
			ArrayList<ApplicationInfo> appList = new ArrayList<ApplicationInfo>();
			
			for (int i = 0; i < appNumInScreen; i++) {
				appList.add(mAppInfoList.get(position*Constants.CELL_COUNT_PRE_SCREEN + i));
			}
			setGridViewStyle(screenLayout, appList);
			((ViewPager) container).addView(screenLayout);
			return screenLayout;
		}

	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_fun);
		getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT,
				WindowManager.LayoutParams.MATCH_PARENT);

//		String xmlPath = Constants.PATH_SYSTEM_ETC+Constants.DEFAULT_FUN_APP;
//		mAppInfoList = LoadApplicationInfo.loadAppInfoFromSysXml(this, xmlPath);
//		if (mAppInfoList == null || mAppInfoList.size() == 0) {
//			mAppInfoList = LoadApplicationInfo.loadApplicationInfoFromXml(this,
//					Constants.APP_TYPE_FUN);
//		}
		
		
		if (mAppInfoList == null || mAppInfoList.size() == 0) {
			return;
		}

		int gameCount = mAppInfoList.size();
		mPageCount = gameCount / (Constants.CELL_COUNT_PRE_SCREEN)
				+ ((gameCount % Constants.CELL_COUNT_PRE_SCREEN) > 0 ? 1 : 0);

		initViews();

		initDots();

		highlightCurDot(0);
	}

	private void initViews() {
		mPageViews = new ArrayList<View>();
		LayoutInflater inflater = LayoutInflater.from(this);
		for (int i = 0; i < mPageCount; i++) {
			LinearLayout layout = (LinearLayout) inflater.inflate(
					R.layout.gridview_layout, null);
			mPageViews.add(layout);
		}

		mViewPager = (ViewPager) findViewById(R.id.viewpager);
		mViewPager.setAdapter(mPagerAdapter);
		mViewPager.setOnPageChangeListener(this);
	}

	private void setGridViewStyle(LinearLayout screenLayout,
			ArrayList<ApplicationInfo> appList) {
		GridView gridView = (GridView) screenLayout.findViewById(R.id.gridview);
		int mColumnWidth = (int) getResources()
				.getDimension(R.dimen.cell_width);
		int mColumnHeight = (int) getResources().getDimension(
				R.dimen.cell_height);
		LayoutParams lp = (LayoutParams) gridView.getLayoutParams();
		int numColumns = Constants.CELL_COUNT_PRE_SCREEN / 2;
		lp.width = mColumnWidth * numColumns;
		lp.height = mColumnHeight * 2;
		gridView.setNumColumns(numColumns);
		//GridViewAdatper gridViewAdatper = new GridViewAdatper(this, appList,this);
		//gridView.setAdapter(gridViewAdatper);
	}

	private void initDots() {
		if (mPageCount <= 1) {
			return;
		}

		mDotLayout = (LinearLayout) findViewById(R.id.dot_layout);
		mDots = new ImageView[mPageCount];
		for (int i = 0; i < mPageCount; i++) {
			ImageView image = new ImageView(this);
			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
					24, 24);
			params.setMargins(2, 0, 2, 0);
			//image.setBackgroundResource(R.drawable.dot_normal);
			mDotLayout.addView(image, params);
			mDots[i] = image;
		}
	}

	private void highlightCurDot(int positon) {
		if (mDots == null || positon < 0 || positon > mPageCount - 1) {
			return;
		}
		for (int i = 0; i < mPageCount; i++) {
			if (positon == i) {
				//mDots[i].setBackgroundResource(R.drawable.dot_selected);
			} else {
				//mDots[i].setBackgroundResource(R.drawable.dot_normal);
			}
		}
	}

	@Override
	public void onPageScrollStateChanged(int state) {

	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {

	}

	@Override
	public void onPageSelected(int arg0) {
		highlightCurDot(arg0);
	}

	@Override
	public void dismissWindow() {
		//finish();
	}

}
