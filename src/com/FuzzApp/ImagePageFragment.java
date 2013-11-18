package com.FuzzApp;


import java.util.ArrayList;  

import com.FuzzApp.R;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

public class ImagePageFragment extends Fragment{
	
	static ArrayList<Bitmap> data_list;
	
	
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState){
		if (container == null){
			return null;
		}
		View v = (LinearLayout)inflater.inflate(R.layout.list_page, container, false);
		ListView listView = (ListView) v.findViewById(R.id.listView1);
		mAdapter adapter = new mAdapter(getActivity(), R.layout.image_layout, data_list);
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(list_listener);
		return v;
	}
	
	
	public static final ImagePageFragment newInstance(ArrayList<Bitmap> image_data_list){
		ImagePageFragment f = new ImagePageFragment();
		data_list = image_data_list;
		return f;
	}
	
	private OnItemClickListener list_listener = new OnItemClickListener(){

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			Intent intent = new Intent(getActivity(), FuzzyWebActivity.class);
			startActivityForResult(intent, 0);										//START ACTIVITY FOR RESULT
																					//SO DONT HAVE TO RECREATE
																					//MAIN ACTIVITY EVERY TIME
			
		}
		
	};

}
