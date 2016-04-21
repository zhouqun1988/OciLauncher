package com.allwinner.theatreplayer.launcher.util;

import java.util.ArrayList;

import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.allwinner.theatreplayer.launcher.data.CellViews;
import com.allwinner.theatreplayer.launcher.view.iLinearLayout;

public class LayoutTraverserUtil {
	private ArrayList<CellViews> mCellViews = null;

	public LayoutTraverserUtil() {
		mCellViews = new ArrayList<CellViews>();
	}

	public ArrayList<CellViews> getCellViewsList() {
		return mCellViews;
	}

	public void addLayout(View layout) {
		findViewTraversal(layout);
	}

	private void findViewTraversal(View layout) {

		for (int i = 0; i < ((ViewGroup) layout).getChildCount(); i++) {
			View v = ((ViewGroup) layout).getChildAt(i);
			if (v instanceof iLinearLayout) {
				getiLinerLayout(v);
			} else {
				findViewTraversal(v);
			}
		}

	}

	private void getiLinerLayout(View layout) {
		CellViews cellViews = new CellViews();
		cellViews.iLayout = (iLinearLayout) layout;		
//		if (mCellViews.size() != 0) {
			int count = ((ViewGroup) layout).getChildCount();
			
			for (int i = 0; i < count; i++) {
				View v = ((ViewGroup) layout).getChildAt(i);
				if (v instanceof ImageView) {
					cellViews.iconView = (ImageView) v;
				} else if (v instanceof ImageButton) {
					cellViews.imgView = (ImageButton) v;
				} else if (v instanceof TextView) {
					cellViews.textView = (TextView) v;
				} else {
					int sonCount = ((ViewGroup) v).getChildCount();
					if (sonCount > 0) {
						for (int j = 0; j < sonCount; j++) {
							View sonView = ((ViewGroup) v).getChildAt(j);
							if (sonView instanceof ImageButton) {
								cellViews.imgView = (ImageButton) sonView;
								break;
							}
						}
					}
				}
			}
//		}
		mCellViews.add(cellViews);

	}
}
