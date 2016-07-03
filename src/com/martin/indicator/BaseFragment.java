package com.martin.indicator;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

@SuppressLint("ValidFragment")
public class BaseFragment extends Fragment {

	String title;
	public BaseFragment() {
	}
	public BaseFragment(String title) {
		this.title = title;
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		TextView view=new TextView(getActivity());
		view.setText(title);
		view.setGravity(Gravity.CENTER);
		view.setTextSize(16);
		return view;
	}

}
