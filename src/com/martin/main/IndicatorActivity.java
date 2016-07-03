package com.martin.main;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;

import com.martin.view.R;
import com.martin.view.Indicator;
import com.martin.view.Indicator.OnPagerChangeTo;

public class IndicatorActivity extends FragmentActivity {

	ViewPager viewPager;
	Indicator indicator;
	List<Fragment> fragments=new ArrayList<Fragment>();
	List<String> titles=Arrays.asList("手机","邮箱","用户名");
	FragmentPagerAdapter pagerAdapter;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_index);
		viewPager=(ViewPager) findViewById(R.id.viewPager);
		indicator=(Indicator) findViewById(R.id.indicator);
		initData();
		setListener();
	}

	private void initData() {
		for (int i = 0; i <3; i++) {
			fragments.add(new BaseFragment(titles.get(i)));
		}
		pagerAdapter=new FragmentPagerAdapter(getSupportFragmentManager()) {
			
			@Override
			public int getCount() {
				return titles.size();
			}
			
			@Override
			public Fragment getItem(int position) {
				return fragments.get(position);
			}
		};
		viewPager.setAdapter(pagerAdapter);
	}
	private void setListener() {
		viewPager.setOnPageChangeListener(new OnPageChangeListener() {
			
			@Override
			public void onPageSelected(int arg0) {
			}
			
			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
				indicator.scroll(arg0, arg1);
			}
			
			@Override
			public void onPageScrollStateChanged(int arg0) {
			}
		});
		indicator.setOnPagerChangeTo(new OnPagerChangeTo() {
			
			@Override
			public void onPagerTo(int position) {
				viewPager.setCurrentItem(position,false);
			}
		});
	}
}
