package com.allwinner.theatreplayer.launcher.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.widget.LinearLayout;

public class iLinearLayout extends LinearLayout {

	private RectF mRectf = null;
	private Bitmap bitmap;
	private int mColour = -1;
	private static int mRadius = 5;
	private Bitmap mDstB = null;

	private Rect bitmapRect = new Rect();
	private PaintFlagsDrawFilter pdf = new PaintFlagsDrawFilter(0,
			Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);
	private Paint paint = new Paint();
	{
		paint.setStyle(Paint.Style.FILL);
		paint.setFlags(Paint.ANTI_ALIAS_FLAG);
		paint.setAntiAlias(true);
	}

	private PorterDuffXfermode xfermode = new PorterDuffXfermode(
			PorterDuff.Mode.MULTIPLY);

	public iLinearLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public iLinearLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	public iLinearLayout(Context context) {
		super(context);
		init();
	}

	private void init() {
		try {
			if (android.os.Build.VERSION.SDK_INT >= 11) {
				setLayerType(LAYER_TYPE_SOFTWARE, null);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void setImageBitmap(Bitmap bitmap) {
		this.bitmap = bitmap;
	}

	public void setBackgroundColor(int colour) {
		this.mColour = colour;
	}

	private Bitmap makeDst(int w, int h) {
		Bitmap bm = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
		Canvas c = new Canvas(bm);
		Paint p = new Paint(Paint.ANTI_ALIAS_FLAG);
		p.setColor(Color.parseColor("#ffffffff"));
		c.drawRoundRect(new RectF(0, 0, w, h), mRadius, mRadius, p);
		return bm;
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		if (bitmap != null) {
			if (null == mDstB) {
				mDstB = makeDst(getWidth(), getHeight());
			}

			bitmapRect.set(0, 0, getWidth(), getHeight());
			canvas.save();
			canvas.setDrawFilter(pdf);
			canvas.drawBitmap(mDstB, 0, 0, paint);
			paint.setXfermode(xfermode);
			canvas.drawBitmap(bitmap, null, bitmapRect, paint);
			paint.setXfermode(null);
			canvas.restore();
		} else if (mColour != -1) {
			if (mRectf == null) {
				canvas.setDrawFilter(pdf);
				paint.setColor(mColour);
				mRectf = new RectF();
				mRectf.left = 0;
				mRectf.top = 0;
				mRectf.right = getWidth();
				mRectf.bottom = getHeight();
			}

			canvas.drawRoundRect(mRectf, mRadius, mRadius, paint);
		}
	}

}