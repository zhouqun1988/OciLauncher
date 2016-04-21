package com.allwinner.theatreplayer.launcher.view;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;

import android.content.Context;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.allwinner.theatreplayer.launcher.LauncherApp;
import com.allwinner.theatreplayer.launcher.LauncherModel;
import com.allwinner.theatreplayer.launcher.Log;
import com.allwinner.theatreplayer.launcher.R;
import com.allwinner.theatreplayer.launcher.data.AppInfo;
import com.allwinner.theatreplayer.launcher.data.ApplicationInfo;
import com.allwinner.theatreplayer.launcher.data.Constants;

public class GridViewAdatper extends BaseAdapter {
	private Context mContext;
	private LauncherModel mLauncherModel = null;
	private LayoutInflater mInflater;
	private PackageManager mPManager;
	private ArrayList<AppInfo> mAppInfoList;
	private GridViewItemClickListener mGridViewItemClickListener;
	private CellViewTouchListener mTouchListener = new CellViewTouchListener();
	private OnClickListener mClickListener = new View.OnClickListener() {

		@Override
		public void onClick(View view) {
			ViewHolder holder = (ViewHolder) view.getTag();
			int position = holder.getPosition();
			
			AppInfo appInfo = mAppInfoList.get(position);
			
			Log.d("OnClickListener view = "+view);
			Log.d("OnClickListener view Id = "+view.getId());
			Log.d("OnClickListener position = "+position);
			Log.d("OnClickListener holder.getAppName: "+holder.getAppName());
			Log.d("OnClickListener holder.getClass: "+holder.getClass());
			
			Log.i("start appInfo: "+appInfo.appName);
            Log.i("start packageName: "+appInfo.packageName);
            mLauncherModel.startThirdApk(appInfo.packageName);
//			if (appInfo.pkg.equals(Constants.PACKAGE_GALLERY)) {
//				if (appInfo.title.equals(mContext.getResources().getString(
//						R.string.app_local_gallery))) {
//					mLauncherModel.startThirdApk(appInfo.pkg, "gallery");
//				} else {
//					mLauncherModel.startThirdApk(appInfo.pkg, "movie");
//				}
//			} else {
//				mLauncherModel.startThirdApk(appInfo.pkg);
//			}
			
			//mGridViewItemClickListener.dismissWindow();
		}
	};

	public GridViewAdatper(Context context, ArrayList<AppInfo> appList,
			GridViewItemClickListener listener) {
		mContext = context;
		mGridViewItemClickListener = listener;
		mInflater = LayoutInflater.from(context);
		mPManager = context.getPackageManager();
		mLauncherModel = LauncherApp.getInstance().getModel();
		mAppInfoList = appList;
	}

	public int getCount() {
		return mAppInfoList.size();
	}

	public Object getItem(int arg0) {
		return arg0;
	}

	public long getItemId(int arg0) {
		return arg0;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		Log.i("position = "+position);
		if (convertView != null && convertView.getTag() != null) {
			holder = (ViewHolder) convertView.getTag();
		} else {
			convertView = mInflater.inflate(R.layout.gridview_adapter, null);
			holder = new ViewHolder(convertView, position);
			convertView.setTag(holder);
			//convertView.setOnTouchListener(mTouchListener);
			//convertView.setOnClickListener(mClickListener);
			convertView.setClickable(false);
		}

		AppInfo appInfo = mAppInfoList.get(position);

		ImageView appIcon = holder.getAppIcon();
		setShortcutIconByPkg(appIcon, appInfo.packageName);
//    		if (appInfo.appIcon != null) {
//    			Drawable drawable = null;
//    			if (appInfo.fromsystem) {
//    				drawable =getShortcutIconFromSys(appInfo.icon);
//    				if(drawable!=null){
//    					appIcon.setImageDrawable(drawable);
//    				}
//    			}
//    			if(drawable==null){
//    				drawable = getShortcutIconByPath(appIcon, appInfo.icon);
//    			}			
//    			if(drawable!=null){
//    				appIcon.setImageDrawable(drawable);	
//    			}else{
//    				setShortcutIconByPkg(appIcon, appInfo.pkg);
//    			}
//    		} else if (appInfo.pkg != null) {
//    			setShortcutIconByPkg(appIcon, appInfo.pkg);
//    		}
		TextView appName = holder.getAppName();
		setShortcutNameByPkg(appName, appInfo.packageName, position);
//		if (appInfo.title != null) {
//			setShortcutNameByString(appName, appInfo.title);
//		} else if (appInfo.pkg != null) {
//			setShortcutNameByPkg(appName, appInfo.pkg);
//		}

		return convertView;
	}

	
	public final class ViewHolder {
		public int position;
		public View baseView;
		public ImageView appIcon;
		public TextView appName;

		public ViewHolder(View view, int position) {
			baseView = view;
			this.position = position;
		}

		public int getPosition() {
			return position;
		}

		public ImageView getAppIcon() {
			if (appIcon == null) {
				ImageView imageView = (ImageView) baseView
						.findViewById(R.id.app_icon);
				appIcon = imageView;
			}
			return appIcon;
		}

		public TextView getAppName() {
			if (appName == null) {
				TextView textView = (TextView) baseView
						.findViewById(R.id.app_name);
				appName = textView;
			}
			return appName;
		}
	}

	@SuppressWarnings("deprecation")
	private Drawable getShortcutIconFromSys(String icon){
		Drawable drawable = null;
		try {
			File mFile=new File(Constants.PATH_SYSTEM_ETC+icon);
	        if (mFile.exists()) {
	            Bitmap bitmap=BitmapFactory.decodeFile(Constants.PATH_SYSTEM_ETC+icon);
	             drawable =new BitmapDrawable(bitmap);
	        }else{
	        	int iconID = mContext.getResources().getIdentifier(
	    				icon, "drawable", mContext.getPackageName());
	    		 drawable = mContext.getResources().getDrawable(iconID);
	        }
		} catch (Exception e) {
			// TODO: handle exception
		}
		
		return drawable;
	}
	
	private void setShortcutIconByPkg(ImageView appIcon, String pkg) {
		try {
			android.content.pm.ApplicationInfo info = mPManager
					.getApplicationInfo(pkg, 0);
			appIcon.setImageDrawable(mPManager.getApplicationIcon(info));
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
	}

	@SuppressWarnings("deprecation")
	private Drawable getShortcutIconByPath(ImageView appIcon, String path) {
		Bitmap bitmap=null;
		try {
			InputStream is = mContext.getClassLoader().getResourceAsStream(path);
			bitmap = BitmapFactory.decodeStream(is);	
		} catch (Exception e) {
			// TODO: handle exception
		}
		if(bitmap==null){
			return null;
		}
		return new BitmapDrawable(bitmap);
	}

	private void setShortcutNameByString(TextView appName, String name) {
		appName.setText(name);
	}

	private void setShortcutNameByPkg(TextView appName, String pkg) {
		try {
			android.content.pm.ApplicationInfo info = mPManager
					.getApplicationInfo(pkg, 0);
			String title = (String) mPManager.getApplicationLabel(info);
			appName.setText(title);
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	private void setShortcutNameByPkg(TextView appName, String pkg, int pos) {
        try {
            android.content.pm.ApplicationInfo info = mPManager
                    .getApplicationInfo(pkg, 0);
            String title = (String) mPManager.getApplicationLabel(info);
            appName.setText(pos+":"+title);
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
    }
//118 12114
	public interface GridViewItemClickListener {
		public void dismissWindow();
	}
}
