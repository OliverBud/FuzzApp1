package com.FuzzApp;


import java.util.List;

import android.app.FragmentManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;

//BOILERPLATE ADAPTER FOR POPULATING VIEWPAGER WITH FRAGMENTS
public class PagerAdapter1 extends FragmentPagerAdapter{


	private List<Fragment> fragments;
	
	
	public PagerAdapter1(android.support.v4.app.FragmentManager fm, List<Fragment> fragments){
		super(fm);
		this.fragments = fragments;
	}


	@Override
	public Fragment getItem(int position) {
		return this.fragments.get(position);
	}


	@Override
	public int getCount() {
		return this.fragments.size();
	}
	
	@Override
	public float getPageWidth(int position){
		float fart = (float) 1;
		return fart;
	}
}

