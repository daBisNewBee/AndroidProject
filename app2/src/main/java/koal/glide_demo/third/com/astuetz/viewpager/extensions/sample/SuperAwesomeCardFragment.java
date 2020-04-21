/*
 * Copyright (C) 2013 Andreas Stuetz <andreas.stuetz@gmail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package koal.glide_demo.third.com.astuetz.viewpager.extensions.sample;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.TextView;

import koal.glide_demo.CustomViewpager;
import koal.glide_demo.R;
import koal.glide_demo.utlis.BaseUtil;

public class SuperAwesomeCardFragment extends Fragment {

	private static final String ARG_POSITION = "position";

	private int position;
	private CustomViewpager mCustomViewpager;

	public static SuperAwesomeCardFragment newInstance(int position, CustomViewpager vp) {
		SuperAwesomeCardFragment f = new SuperAwesomeCardFragment();
		f.mCustomViewpager = vp;
		Bundle b = new Bundle();
		b.putInt(ARG_POSITION, position);
		f.setArguments(b);
		return f;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		position = getArguments().getInt(ARG_POSITION);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		int height = LayoutParams.MATCH_PARENT;
		int color = R.color.colorAccent;

		Log.d("todo", "onCreateView: pos:" + position);
		View root = null;
		if (position == 0) {
			root = inflater.inflate(R.layout.fra_100, container, false);
//			height = BaseUtil.dp2px(getContext(), 100);
//			color = R.color.colorPrimary;
		} else if (position == 1) {
			root = inflater.inflate(R.layout.fra_200, container, false);
//			height = BaseUtil.dp2px(getContext(), 200);
//			color = R.color.colorPrimaryDark;
		} else if (position == 2) {
			root = inflater.inflate(R.layout.fra_300, container, false);
//			height = BaseUtil.dp2px(getContext(), 300);
//			color = R.color.black;
		}
		mCustomViewpager.setObjectForPosition(root, position);
		return root;

		/*
		LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, height);
		FrameLayout fl = new FrameLayout(getActivity());
		fl.setLayoutParams(params);
//		fl.setBackgroundColor(color);

		final int margin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8, getResources()
				.getDisplayMetrics());

		TextView v = new TextView(getActivity());
		params.setMargins(margin, margin, margin, margin);
		v.setLayoutParams(params);
		v.setGravity(Gravity.CENTER);
//		v.setBackgroundResource(R.mipmap.background_card);
		v.setText("CARD " + position);

		fl.addView(v);
		return fl;
		 */
	}

}