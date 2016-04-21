package com.allwinner.theatreplayer.launcher.transformers;

import android.support.v4.view.ViewPager;
import android.view.View;

import com.nineoldandroids.view.ViewHelper;

public class FadePageTransformer implements ViewPager.PageTransformer {
	public void transformPage(View view, float position) {
		if (position < -1) {
			ViewHelper.setAlpha(view, 0);
		} else if (position <= 0) {
			ViewHelper.setAlpha(view, 1 + position);

		} else if (position <= 1) {
			ViewHelper.setAlpha(view, 1 - position);

		} else {
			ViewHelper.setAlpha(view, 0);
		}
	}

}