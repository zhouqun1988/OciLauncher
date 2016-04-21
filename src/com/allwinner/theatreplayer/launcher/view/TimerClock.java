package com.allwinner.theatreplayer.launcher.view;

import java.io.InputStream;
import java.util.TimeZone;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.text.format.Time;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RemoteViews.RemoteView;

import com.allwinner.theatreplayer.launcher.R;

@SuppressLint("HandlerLeak")
@RemoteView
public class TimerClock extends View {
	private Time mCalendar;
	private Bitmap mDial;
	private BitmapDrawable mDialDrawable;
	private BitmapDrawable mHourHandDrawable;
	private BitmapDrawable mMinuteHandDrawable;
	private BitmapDrawable mSecondHandDrawable;
	private int mDialWidth;
	private int mDialHeight;
	private boolean mAttached = false;
	private float mHours;
	private float mMinutes;
	private float mSeconds;
	private String mTimeZone;

	public String getTime_zone() {
		return mTimeZone;
	}

	public void setTime_zone(String timeZone) {
		mTimeZone = timeZone;
	}

	private boolean mChanged;

	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1:
				invalidate();
				break;
			}
			super.handleMessage(msg);
		}
	};

	private boolean isRun = false;

	private void run() {
		mClockHandler.postDelayed(mClockRunnable, 100);
	}

	private Handler mClockHandler = new Handler();

	private Runnable mClockRunnable = new Runnable() {

		public void run() {
			Message message = new Message();
			message.what = 1;
			mHandler.sendMessage(message);
			mClockHandler.postDelayed(this, 100);
		}

	};

	public TimerClock(Context context) {
		this(context, null);
	}

	public TimerClock(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	@SuppressWarnings("deprecation")
	public TimerClock(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);

		mCalendar = new Time();
		mTimeZone = mCalendar.timezone;

		Resources r = this.getContext().getResources();
		InputStream is = null;

		is = r.openRawResource(R.drawable.clock);
		mDialDrawable = new BitmapDrawable(is);
		mDial = mDialDrawable.getBitmap();

		is = r.openRawResource(R.drawable.clock_hour_hands);
		mHourHandDrawable = new BitmapDrawable(is);

		is = r.openRawResource(R.drawable.clock_minute_hands);
		mMinuteHandDrawable = new BitmapDrawable(is);

		is = r.openRawResource(R.drawable.clock_second_hands);
		mSecondHandDrawable = new BitmapDrawable(is);

		mDialWidth = mDialDrawable.getIntrinsicWidth();
		mDialHeight = mDialDrawable.getIntrinsicHeight();
	}

	@Override
	protected void onAttachedToWindow() {
		super.onAttachedToWindow();

		if (!mAttached) {
			mAttached = true;

			IntentFilter filter = new IntentFilter();

			// filter.addAction(Intent.ACTION_TIME_TICK);
			filter.addAction(Intent.ACTION_TIME_CHANGED);
			filter.addAction(Intent.ACTION_TIMEZONE_CHANGED);

			getContext().registerReceiver(mIntentReceiver, filter, null,
					mHandler);
		}

		mCalendar = new Time();
		onTimeChanged();
	}

	@Override
	protected void onDetachedFromWindow() {
		super.onDetachedFromWindow();
		if (mAttached) {
			getContext().unregisterReceiver(mIntentReceiver);
			mAttached = false;
		}
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int widthMode = MeasureSpec.getMode(widthMeasureSpec);
		int widthSize = MeasureSpec.getSize(widthMeasureSpec);
		int heightMode = MeasureSpec.getMode(heightMeasureSpec);
		int heightSize = MeasureSpec.getSize(heightMeasureSpec);

		float hScale = 1.0f;
		float vScale = 1.0f;
		if (widthMode != MeasureSpec.UNSPECIFIED && widthSize < mDialWidth) {
			hScale = (float) widthSize / (float) mDialWidth;
		}

		if (heightMode != MeasureSpec.UNSPECIFIED && heightSize < mDialHeight) {
			vScale = (float) heightSize / (float) mDialHeight;
		}

		float scale = Math.min(hScale, vScale);

		setMeasuredDimension(
				resolveSize((int) (mDialWidth * scale), widthMeasureSpec),
				resolveSize((int) (mDialHeight * scale), heightMeasureSpec));
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		mChanged = true;
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		if (!isRun) {
			run();
			isRun = true;
			return;
		}
		onTimeChanged();
		boolean changed = mChanged;
		if (changed) {
			mChanged = false;
		}

		int availableWidth = mDial.getWidth();
		int availableHeight = mDial.getHeight();

		int x = availableWidth / 2;
		int y = availableHeight / 2;

		final Drawable dial = mDialDrawable;
		int w = dial.getIntrinsicWidth();
		int h = dial.getIntrinsicHeight();

		boolean scaled = false;

		if (availableWidth < w || availableHeight < h) {
			scaled = true;
			float scale = Math.min((float) availableWidth / (float) w,
					(float) availableHeight / (float) h);
			canvas.save();
			canvas.scale(scale, scale, x, y);
		}

		if (changed) {
			dial.setBounds(x - (w / 2), y - (h / 2), x + (w / 2), y + (h / 2));
		}
		dial.draw(canvas);
		canvas.save();

		canvas.rotate(mHours / 12.0f * 360.0f, x, y);
		final Drawable hourHand = mHourHandDrawable;
		if (changed) {
			w = hourHand.getIntrinsicWidth();
			h = hourHand.getIntrinsicHeight();
			hourHand.setBounds(x - (w / 2), y - (h * 2 / 3), x + (w / 2), y
					+ (h / 3));
		}
		hourHand.draw(canvas);

		canvas.restore();
		canvas.save();
		canvas.rotate(mMinutes / 60.0f * 360.0f, x, y);

		final Drawable minuteHand = mMinuteHandDrawable;
		if (changed) {
			w = minuteHand.getIntrinsicWidth();
			h = minuteHand.getIntrinsicHeight();
			// minuteHand.setBounds(x - (w / 2), y - (h * 4 / 5), x + (w / 2), y
			// + (h / 5));
			// fangwei, adjust minute point hand
			minuteHand.setBounds(x - (w / 2), y - (h * 4 / 5) + 5, x + (w / 2),
					y + (h / 5) + 5);
		}
		minuteHand.draw(canvas);
		canvas.restore();

		canvas.save();
		canvas.rotate(mSeconds / 60.0f * 360.0f, x, y);

		final Drawable scendHand = mSecondHandDrawable;
		if (changed) {
			w = scendHand.getIntrinsicWidth();
			h = scendHand.getIntrinsicHeight();
			int size = (h / 4) + (h % 4);// jolyxie,calculate second hand point.
			// scendHand.setBounds(x - (w / 2), y - h, x + (w / 2), y);
			// fangwei, adjust second point hand
			// scendHand.setBounds(x - (w / 2), y - h + 19, x + (w / 2), y +
			// 19);
			scendHand.setBounds(x - (w / 2), y - h + size, x + (w / 2), y
					+ size);
		}
		scendHand.draw(canvas);
		canvas.restore();

		if (scaled) {
			canvas.restore();
		}
	}

	private void onTimeChanged() {
		mCalendar.setToNow();

		int hour = mCalendar.hour;
		int minute = mCalendar.minute;
		int second = mCalendar.second;

		// if(mSeconds == second){
		mSeconds += 0.1f;
		// }else{
		// mSeconds = second;
		// }

		mMinutes = minute + second / 60.0f;
		mHours = hour + mMinutes / 60.0f;

		mChanged = true;
	}

	private final BroadcastReceiver mIntentReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String tz = "";
			if (intent.getAction().equals(Intent.ACTION_TIMEZONE_CHANGED)) {
				tz = intent.getStringExtra("time-zone");
				mCalendar = new Time(TimeZone.getTimeZone(tz).getID());
				mTimeZone = mCalendar.timezone;
			}
			onTimeChanged();
			invalidate();
		}
	};

}
