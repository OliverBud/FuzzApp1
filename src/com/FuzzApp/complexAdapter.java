package com.FuzzApp;

import java.util.List;

import com.FuzzApp.R;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;


//ADAPTER TO POPULATE THE EVERYTHING LIST WITH EITHER IMAGES OR STRINGS
public class complexAdapter extends ArrayAdapter<dataItem>{
	
	private static final int TYPE_IMAGE = 0;
	private static final int TYPE_TEXT = 1;

	Context context;
	int resource;
	List<dataItem> data;

	public complexAdapter(Context context, int resource, List<dataItem> data) {
		super(context, resource, data);
		this.context = context;
		this.resource = resource;
		this.data = data;
	
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent){
		View row = convertView;
		holder holder = null;															//CHECK TO RECYCLE VIEW
		
		int type = getItemViewType(position);
		
		if (row == null){
			LayoutInflater inflater = ((Activity)context).getLayoutInflater();
			holder = new holder();
			switch(type){																//SWITCH ON TYPE OF DATA ITEM
																						//CHOOSE WHICH LAYOUT TO INFLATE
					
				case TYPE_IMAGE:															
					row = inflater.inflate(R.layout.image_layout,null);			
					holder.image_view = (ImageView) row.findViewById(R.id.image_view);
					break;
				
				case TYPE_TEXT:	
					row = inflater.inflate(android.R.layout.simple_list_item_1, null);
					holder.text_view = (TextView) row.findViewById(android.R.id.text1);
					break;
			}
            row.setTag(holder);
		
		}
		else{
			 holder = (holder)row.getTag();
		}
		
		
		switch(type){						//SET THE APPROPRIATE DATA THROUGH THE HOLDER
			case TYPE_IMAGE:
				holder.image_view.setImageBitmap((android.graphics.Bitmap) data.get(position).image);
				break;
				
			case TYPE_TEXT:
				holder.text_view.setText(data.get(position).text);
				break;
		}
		
		return row;
		
	}
	
	//NUMBER OF TYPES OF DIFFERENT VIEWS
	@Override
	public int getViewTypeCount(){
		return 2;
	}
	
	//GET THE APPROPRIATE ITEM TYPE FOR DATA ITEM IN LIST AT POSITION
	@Override
	public int getItemViewType(int position){
		if (data.get(position).text == null)
			return TYPE_IMAGE;			
		return TYPE_TEXT;
		
	}
	
	//HOLDER CLASS FOR BOTH TYPES OF LAYOUTS IN VIEWS
	static class holder{
		ImageView image_view;
		TextView text_view;
	}
}
