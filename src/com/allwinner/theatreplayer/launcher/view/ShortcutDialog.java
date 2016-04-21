package com.allwinner.theatreplayer.launcher.view;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TextView;

import com.allwinner.theatreplayer.launcher.LauncherApp;
import com.allwinner.theatreplayer.launcher.LauncherModel;
import com.allwinner.theatreplayer.launcher.PreferencesHelper;
import com.allwinner.theatreplayer.launcher.R;
import com.allwinner.theatreplayer.launcher.data.CellInfo;
import com.allwinner.theatreplayer.launcher.data.Constants;
import com.allwinner.theatreplayer.launcher.util.ImageUtils;
import com.allwinner.theatreplayer.launcher.util.SharedPreUtil;

public class ShortcutDialog extends Dialog implements View.OnClickListener {
	private Window mWindow;
	private final int ANIM_DURATION = 300;
	private final int DELAY_DIVISION = 100;
	private final int CELL_NUM = 6;
	private final int WHOLE_ANIM_TIME = DELAY_DIVISION * CELL_NUM + 2
			* ANIM_DURATION;
	private long mLastAnimStartTime = 0;
	private Context mContext;
	private LauncherModel mLauncherModel;
	public boolean isShowing = false;
	private AlphaAnimation mBgDimAnimation;
	private AlphaAnimation mBgLightAnimation;
	private Animation[] mPushInAnim = new Animation[CELL_NUM];
	private Animation[] mPushOutAnim = new Animation[CELL_NUM];
	private TableLayout mTabLayout = null;
	private LinearLayout[] mCell = new LinearLayout[CELL_NUM];
	private int mMsgId = -1;
	private ImageView mCellImage;
	private TextView mCellText;
	private CellInfo mCellInfo = null;
	private CellViewTouchListener mTouchListener = new CellViewTouchListener();
	
	@SuppressLint("HandlerLeak")
	private Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			mMsgId = msg.what;
		}

	};

	private Runnable mAfterAnimationAction = new Runnable() {

		@Override
		public void run() {
			dismiss();
			switch (mMsgId) {
			case R.id.cell1:
				if(mCellInfo!=null&&mCellInfo.packageName!=null&&!mCellInfo.equals("")){
					mLauncherModel.startThirdApk(mCellInfo.packageName);
				}
				break;
			case R.id.cell2:
				mLauncherModel.startThirdApk(Constants.PACKAGE_NET_RADIO);
				break;
			case R.id.cell3:
				mLauncherModel.startThirdApk(Constants.PACKAGE_VOD);
				break;
			case R.id.cell4:
				mLauncherModel.startThirdApk(Constants.PACKAGE_WIFI_ANALYZE);
				break;
			case R.id.cell5:
				PreferencesHelper dbHelper = PreferencesHelper
						.getInstance(mContext);
				boolean liveAppAuthorized = dbHelper.getLiveAuthorized();
				if (liveAppAuthorized) {
					mLauncherModel.startThirdApk(Constants.PACKAGE_LIVE);
				} else {
					CustomToast.showToast(mContext, R.string.live_unauthorized);
				}
				break;
			case R.id.cell6:
				mLauncherModel.startThirdApk(Constants.PACKAGE_SETTINGS);
				break;
			case 0:
			default:
				break;
			}
		}
	};

	public ShortcutDialog(Context context) {
		super(context, R.style.DialogShortcut);
		mContext = context;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.shortcut_layout);
		mLauncherModel = LauncherApp.getInstance().getModel();
		setDialogStyle();
		initViews();
		initShowAnimation();
		initDismissAnimation();
	}

	@Override
	public void dismiss() {
		super.dismiss();
		isShowing = false;
	}

	private void initViews() {
		if(LauncherApp.gCellInfoList!=null){
			for(CellInfo cellInfo:LauncherApp.gCellInfoList){
				if(cellInfo!=null){
					if(cellInfo.packageName.equals(Constants.PACKAGE_OLDER_ZONE)
							||cellInfo.packageName.equals(Constants.PACKAGE_HEALTH)){
						mCellInfo = cellInfo;
						break;
					}
				}
			}
		}

			mCellImage = (ImageView)findViewById(R.id.cell1_image);
			mCellText = (TextView)findViewById(R.id.cell1_text);
			mCellText.setText(mCellInfo.title);
			if (mCellInfo.icon != null && !mCellInfo.icon.equals("")) {
				Drawable iconDrawable = ImageUtils.getShortcutIconFromSys(mContext,
						mCellInfo.icon);
				mCellImage.setImageDrawable(iconDrawable);
			}

		mTabLayout = (TableLayout) findViewById(R.id.shortcut_tab);
		mCell[0] = (LinearLayout) findViewById(R.id.cell1);
		mCell[1] = (LinearLayout) findViewById(R.id.cell2);
		mCell[2] = (LinearLayout) findViewById(R.id.cell3);
		mCell[3] = (LinearLayout) findViewById(R.id.cell4);
		mCell[4] = (LinearLayout) findViewById(R.id.cell5);
		mCell[5] = (LinearLayout) findViewById(R.id.cell6);

		for (int i = 0; i < CELL_NUM; i++) {
			mCell[i].setOnTouchListener(mTouchListener);
			mCell[i].setOnClickListener(this);
		}
	}

	private void setDialogStyle() {
		setCanceledOnTouchOutside(true);
		setCancelable(true);
		mWindow = getWindow();
		mWindow.setType((WindowManager.LayoutParams.TYPE_SYSTEM_ALERT));
		WindowManager.LayoutParams lp = mWindow.getAttributes();
		lp.width = WindowManager.LayoutParams.MATCH_PARENT;
		lp.height = WindowManager.LayoutParams.MATCH_PARENT;
		mWindow.setAttributes(lp);
	}

	private void initShowAnimation() {
		mBgDimAnimation = new AlphaAnimation(0f, 1f);
		mBgDimAnimation.setDuration(ANIM_DURATION);

		for (int i = 0; i < CELL_NUM; i++) {
			mPushInAnim[i] = AnimationUtils.loadAnimation(
					LauncherApp.getInstance(), R.anim.push_left_in);
			mPushInAnim[i].setStartOffset(ANIM_DURATION + DELAY_DIVISION * i);
			mPushInAnim[i].setDuration(ANIM_DURATION);
			mPushInAnim[i].setFillAfter(true);
			mPushInAnim[i].setFillBefore(false);
			mPushInAnim[i].setFillEnabled(true);
		}
	}

	private void initDismissAnimation() {
		mBgLightAnimation = new AlphaAnimation(1f, 0f);
		mBgLightAnimation.setDuration(ANIM_DURATION);
		mBgLightAnimation.setStartOffset(DELAY_DIVISION * CELL_NUM
				+ ANIM_DURATION);

		for (int i = 0; i < CELL_NUM; i++) {
			mPushOutAnim[i] = AnimationUtils.loadAnimation(
					LauncherApp.getInstance(), R.anim.push_left_out);
			mPushOutAnim[i].setStartOffset(DELAY_DIVISION * i);
			mPushOutAnim[i].setDuration(ANIM_DURATION);
			mPushOutAnim[i].setFillAfter(true);
			mPushOutAnim[i].setFillBefore(false);
			mPushOutAnim[i].setFillEnabled(true);
		}
	}

	private void showAnimation() {
		mTabLayout.clearAnimation();
		mTabLayout.startAnimation(mBgDimAnimation);

		for (int i = 0; i < CELL_NUM; i++) {
			mCell[i].clearAnimation();
			mCell[i].startAnimation(mPushInAnim[i]);
		}

		mLastAnimStartTime = System.currentTimeMillis();
	}

	private void dismissAnimation() {
		for (int i = 0; i < CELL_NUM; i++) {
			mCell[i].clearAnimation();
			mCell[i].startAnimation(mPushOutAnim[i]);
		}

		mTabLayout.clearAnimation();
		mTabLayout.startAnimation(mBgLightAnimation);

		mLastAnimStartTime = System.currentTimeMillis();
	}

	public void showThisDialog() {
		if (isInAnimation()) {
			return;
		}

		isShowing = true;
		super.show();
		showAnimation();
	}

	public void dismissThisDialog() {
		if (isInAnimation()) {
			return;
		}
		dismissAnimation();
		Message msg = mHandler.obtainMessage();
		msg.what = 0;
		mHandler.dispatchMessage(msg);
		mHandler.postDelayed(mAfterAnimationAction, WHOLE_ANIM_TIME);
	}

	public boolean isInAnimation() {
		long currentTime = System.currentTimeMillis();
		if ((currentTime - mLastAnimStartTime) > WHOLE_ANIM_TIME) {
			return false;
		}
		return true;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (event.getAction() == MotionEvent.ACTION_DOWN && !isInAnimation()) {
			dismissThisDialog();
		}
		return true;
	}

	@Override
	public void onClick(View view) {
		dismissAnimation();

		Message msg = mHandler.obtainMessage();

		int id = view.getId();
		switch (id) {
		case R.id.cell1:
			msg.what = R.id.cell1;
			break;
		case R.id.cell2:
			msg.what = R.id.cell2;
			break;
		case R.id.cell3:
			msg.what = R.id.cell3;
			break;
		case R.id.cell4:
			msg.what = R.id.cell4;
			break;
		case R.id.cell5:
			msg.what = R.id.cell5;
			break;
		case R.id.cell6:
			msg.what = R.id.cell6;
			break;
		}

		mHandler.dispatchMessage(msg);
		mHandler.postDelayed(mAfterAnimationAction, WHOLE_ANIM_TIME);
	}

}
