package com.FuzzApp;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.MenuItem;
import android.webkit.WebView;


//ACTIVITY TO DISPLAY FUZZ PRODUCTION WEB PAGE AFTER A LIST ITEM IS CLICKED
public class FuzzyWebActivity extends Activity{
	
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		WebView webview = new WebView(this);				//LET THE WEBVIEW TAKE THE WHOLE SCREEN
		setContentView(webview);							//NO NEED FOR AN XML LAYOUT
		webview.loadUrl("http://fuzzproductions.com/");
		
		final ActionBar actionBar = getActionBar();			//MAKE ACTION BAR WITH UP NAVIGATION
		 actionBar.setDisplayHomeAsUpEnabled(true);
		
	}
	
	//WHEN HOME BUTTON IN ACTION BAR IS PRESSED
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
	    case android.R.id.home:
	        finish();										//FINISH THE ACTIVITY. SINCE ACTIVITY IS CALLED FOR
	        												//RESULT FROM MAIN ACTIVITY, MAIN WILL NOT HAVE TO RECREATE
	        return true;
	    }
		return super.onOptionsItemSelected(item);
	}

}
