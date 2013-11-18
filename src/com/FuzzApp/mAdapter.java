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

//ADAPTER FOR THE IMAGE LIST
public class mAdapter extends ArrayAdapter<Bitmap>{
	
	Context context;
	int resource;
	List<Bitmap> data;

	public mAdapter(Context context,int resource, List<Bitmap> data) {
		super(context, resource, data);
		this.context = context;
		this.resource = resource;
		this.data = data;
	
	}
	
	//EACH TIME LIST NEEDS A NEW VIEW FOR A DATA ITEM
	@Override
	public View getView(int position, View convertView, ViewGroup parent){
		View row = convertView;
		holder holder = null;
		
		if (row == null){															//CHECK IF WE CAN RECYCLE VIEWS
			LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            row = inflater.inflate(R.layout.image_layout,null);
            holder = new holder();
            holder.image_view = (ImageView) row.findViewById(R.id.image_view);
            row.setTag(holder);
		
		}
		else{
			 holder = (holder)row.getTag();
		}
		
		holder.image_view.setImageBitmap((android.graphics.Bitmap) data.get(position));		//SET THE PROPER IMAGE FOR THE VIEW
		return row;
		
	}
	
	//HOLDER CLASS FOR THE IMAGE VIEW
	static class holder{
		ImageView image_view;
	}
}
