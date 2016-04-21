package com.allwinner.theatreplayer.launcher.view;

import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;

public class CellViewTouchListener implements OnTouchListener {
	private static final float PRESSED_SCALE = 0.95F;
	private AnimationSet mDownAnimatorSet, mUpAnimatorSet;
	private ViewOnTouchAnimationTask mViewOnTouchAnimationTask = new ViewOnTouchAnimationTask();
	
	public CellViewTouchListener(){
		
	}
	
	@Override
	public boolean onTouch(View view, MotionEvent event) {
		View mView = view;
		if(mView instanceof iLinearLayout){
			
		}else{
			mView = (View) mView.getParent();
		}
		switch (event.getAction()) {
		
		case MotionEvent.ACTION_DOWN:
			mView.removeCallbacks(mViewOnTouchAnimationTask);
			mViewOnTouchAnimationTask.setView(mView);
			mView.postDelayed(mViewOnTouchAnimationTask, 100L);
			break;
		case MotionEvent.ACTION_UP:
		case MotionEvent.ACTION_CANCEL:
			mView.removeCallbacks(mViewOnTouchAnimationTask);
			
			onViewTouched(mView, false);
			break;
		}
		return false;
	}
	
	class ViewOnTouchAnimationTask implements Runnable {
		private View view;

		public void setView(View view) {
			this.view = view;
		}

		@Override
		public void run() {
			onViewTouched(view, true);
		}
	}
	
	private void onViewTouched(View view, boolean actionDown) {
		playAnimationOnTouch(view, actionDown);
		view.invalidate();
	}
	
	private void playAnimationOnTouch(View view, boolean actionDown) {
		if (mDownAnimatorSet == null) {
			mDownAnimatorSet = new AnimationSet(true);
			mDownAnimatorSet.setDuration(100L);
			mDownAnimatorSet.setFillAfter(true);
			mUpAnimatorSet = new AnimationSet(true);
			mUpAnimatorSet.setDuration(100L);
			mDownAnimatorSet.setFillAfter(true);
			ScaleAnimation scaleAnimation1 = new ScaleAnimation(1.0F,
					PRESSED_SCALE, 1.0F, PRESSED_SCALE, 1, 0.5F, 1, 0.5F);
			mDownAnimatorSet.addAnimation(scaleAnimation1);
			ScaleAnimation scaleAnimation2 = new ScaleAnimation(PRESSED_SCALE,
					1.0F, PRESSED_SCALE, 1.0F, 1, 0.5F, 1, 0.5F);
			mUpAnimatorSet.addAnimation(scaleAnimation2);
		}
		view.clearAnimation();
		if (actionDown) {
			view.startAnimation(mDownAnimatorSet);
		} else {
			view.startAnimation(mUpAnimatorSet);
		}
	}
}
