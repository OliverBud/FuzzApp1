package com.FuzzApp;

import android.graphics.Bitmap;

//HOLDER CLASS FOR A DATA ITEM THAT CAN EITHER BE A STRING OR AN IMAGE BITMAP
public class dataItem {
	public Bitmap image;
	public String text;
	
	public dataItem(Bitmap bm){
		this.image = bm;
		this.text = null;
	}
	
	public dataItem(String text){
		this.text = text;
		this.image = null;
	}

}
