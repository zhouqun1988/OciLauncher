package com.allwinner.theatreplayer.launcher.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;

import com.allwinner.theatreplayer.launcher.data.Constants;

public class ImageUtils {
	public static void setMirrorImg(final View view, final View mirrorView) {
		ViewTreeObserver vto = view.getViewTreeObserver();
		vto.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
			@SuppressWarnings("deprecation")
			public boolean onPreDraw() {
				view.getViewTreeObserver().removeOnPreDrawListener(this);
				view.layout(0, 0, view.getWidth(), view.getHeight());
				view.buildDrawingCache();
				mirrorView.setBackgroundDrawable(new BitmapDrawable(
						createCutReflectedImage(view.getDrawingCache(), 0)));
				return true;
			}
		});
	}

	public static Bitmap createCutReflectedImage(Bitmap paramBitmap,
			int paramInt) {
		final int reflectImageHeight = 50;
		int i = paramBitmap.getWidth();
		int j = paramBitmap.getHeight();
		final int reflectionGap = 1;
		Bitmap localBitmap2 = null;
		if (j <= paramInt + reflectImageHeight) {
			localBitmap2 = null;
		} else {
			Matrix localMatrix = new Matrix();
			localMatrix.preScale(1.0F, -1.0F);
			Bitmap localBitmap1 = Bitmap.createBitmap(paramBitmap, 0, j
					- reflectImageHeight - paramInt, i, reflectImageHeight,
					localMatrix, true);
			localBitmap2 = Bitmap.createBitmap(i, reflectImageHeight,
					Bitmap.Config.ARGB_8888);
			Canvas localCanvas = new Canvas(localBitmap2);
			localCanvas.drawBitmap(localBitmap1, 0.0F, reflectionGap, null);
			LinearGradient localLinearGradient = new LinearGradient(0.0F, 0.0F,
					0.0F, localBitmap2.getHeight(), -2130706433, 16777215,
					TileMode.CLAMP);
			Paint localPaint = new Paint();
			localPaint.setShader(localLinearGradient);
			localPaint.setXfermode(new PorterDuffXfermode(Mode.DST_IN));
			localCanvas.drawRect(0.0F, 0.0F, i, localBitmap2.getHeight()
					+ reflectionGap, localPaint);
			if (!localBitmap1.isRecycled())
				localBitmap1.recycle();

			localMatrix = null;
			localCanvas = null;
			localPaint = null;
			System.gc();
		}
		return localBitmap2;
	}

	@SuppressWarnings("deprecation")
	public static Drawable getShortcutIconFromSys(Context context,
			String picName) {
		Drawable drawable = null;
		try {
			File mFile = new File(Constants.PATH_SYSTEM_ETC + picName);
			if (mFile.exists()) {
				Bitmap bitmap = BitmapFactory
						.decodeFile(Constants.PATH_SYSTEM_ETC + picName);
				if (bitmap != null)
					drawable = new BitmapDrawable(bitmap);
			} else {
				int iconID = context.getResources().getIdentifier(picName,
						"drawable", context.getPackageName());
				drawable = context.getResources().getDrawable(iconID);
			}
		} catch (Exception e) {
		}

		return drawable;
	}

	public static Drawable getShortcutIconByPkg(Context context, String pkg) {
		android.content.pm.ApplicationInfo info = null;
		Drawable drawable = null;
		try {
			info = context.getPackageManager().getApplicationInfo(pkg, 0);
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		if (info != null) {
			drawable = context.getPackageManager().getApplicationIcon(info);
		}
		return drawable;
	}

	public static Drawable getAppIcon(String packageName, Context context) {
		if (context == null) {
			return null;
		}
		PackageManager manager = context.getPackageManager();
		if (manager == null) {
			return null;
		}
		Drawable icon = null;
		if (packageName == null || packageName.equals("")) {
			return null;
		}
		Intent intent = manager.getLaunchIntentForPackage(packageName);
		if (intent == null) {
			return null;
		}
		ComponentName componentName = intent.getComponent();
		if (componentName == null) {
			intent = null;
			return null;
		}
		PackageInfo packageInfo = null;
		try {
			packageInfo = manager.getPackageInfo(
					componentName.getPackageName(), 0);

			if (packageInfo != null && packageInfo.applicationInfo.enabled) {
				icon = packageInfo.applicationInfo.loadIcon(manager);
			}
		} catch (NameNotFoundException e) {
			Log.e("ImageUtils", "getPackInfo failed for package "
					+ componentName.getPackageName());
		}
		componentName = null;
		intent = null;
		packageInfo = null;
		return icon;
	}

	public static Bitmap toRoundCorner(Bitmap bitmap, int pixels) {
		Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
				bitmap.getHeight(), Config.ARGB_8888);
		Canvas canvas = new Canvas(output);
		final int color = 0xff424242;
		final Paint paint = new Paint();
		final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
		final RectF rectF = new RectF(rect);
		final float roundPx = pixels;
		paint.setAntiAlias(true);
		canvas.drawARGB(0, 0, 0, 0);
		paint.setColor(color);
		canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		canvas.drawBitmap(bitmap, rect, rect, paint);
		return output;
	}

	public static Bitmap loadImageFromLocat(Context context, String name) {
		Bitmap bt = null;
		try {
			String strPath = context.getFilesDir().toString();
			File file = new File(strPath, name);
			if (file.exists()) {
				FileInputStream inStream = new FileInputStream(file);
				bt = BitmapFactory.decodeStream(inStream);
				inStream.close();
			} else {
				file.delete();
			}
		} catch (FileNotFoundException e) {

			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return bt;
	}

	public static Bitmap getBitmapByPicName(Context context, String picName) {
		int resID = context.getResources().getIdentifier(picName, "drawable",
				context.getPackageName());
		Bitmap image = null;
		if (resID > 0) {
			image = BitmapFactory.decodeResource(context.getResources(), resID);
		}
		return image;
	}
}
