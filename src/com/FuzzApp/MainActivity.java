package com.FuzzApp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import com.FuzzApp.R;

import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.widget.ProgressBar;
import android.widget.Toast;

public class MainActivity extends FragmentActivity {

	final String ERROR_CONNECTION = "error_connection";				//ERROR FLAG FOR TESTING CONNECTION IN WORKER THREAD
	final String STATUS_NO_JSON = "error_json";						//STATUS FLAGS SENT FROM WORKER THREAD
	final String STATUS_OK = "ok";									//TO UI THREAD


	ActionBar actionBar;
	private PagerAdapter1 mPagerAdapter;							//VIEW PAGE ADAPTER FOR TEXT/IMAGE/BOTH FRAGMENTS
	static JSONObject jObj = null;
    static String json = "";
    URL url = null;
    HttpURLConnection urlConnection = null;
    InputStream in = null;
    ArrayList<String> text_data_list= new ArrayList<String>();		//LOCAL PERSISTANCE OF OBJECTS TO POPULATE
    ArrayList<Bitmap> image_data_list= new ArrayList<Bitmap>();		//THE LIST FRAGMENTS
    ArrayList<dataItem> item_list= new ArrayList<dataItem>();		//DATA ITEM FOR BOTH IMAGE AND TEXT
    private ProgressBar mProgress;
     
    
    
	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		mProgress = (ProgressBar) findViewById(R.id.progress_bar);					//START INDETERMINATE PROGRESS BAR
		mProgress.setVisibility(0);													//TO WAIT FOR CONNECTION OR ERROR
	
		new ConnectionTask().execute();												//EXECUTE THE WORK OF RETREIVING DATA FROM 	
																					//THE NETWORK
		
		actionBar = getActionBar();													//INITIALIZE ACTION BAR		
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);				//WITH TABS
		ActionBar.TabListener tabListener = new ActionBar.TabListener() {
			@Override
			public void onTabReselected(Tab tab, FragmentTransaction arg1) {
					
			}
			@Override
			public void onTabSelected(Tab tab, FragmentTransaction arg1) {
				ViewPager mViewPager = (ViewPager)findViewById(R.id.viewpager);		
				mViewPager.setCurrentItem(tab.getPosition());
			}

			@Override
			public void onTabUnselected(Tab arg0, FragmentTransaction arg1) {

			}
		};
	
		String[] TabNames = {"text", "image", "both"};								//SET TAB NAMES/LISTENERS
	    for (int i = 0; i < 3; i++) {
	        actionBar.addTab(
	                actionBar.newTab()
	                        .setText(TabNames[i])
	                        .setTabListener(tabListener));
	    }   
	}
		
	//FUNCTION CALLED FROM ONPOSTEXECUTE IN THE THE ASYNC TASK
	//FROM THE UI THREAD WITH CONNECTION STATUS
	
	public void outsideFunction(String status){

		
		if (status.equals(STATUS_NO_JSON)){
			int duration = Toast.LENGTH_LONG;
			String text = "bad connection";
			Toast toast = Toast.makeText(this, text, duration);
			toast.show();
		}
		mProgress.setVisibility(4);									//HIDE THE INTEDTERMINANT PROGRESS BAR
		this.initializePaging();									//POPULATE VIEWPAGER WITH FRAGMENTS
	}
	
	
	
	public void initializePaging(){
		
		
		 List<Fragment> fragments = new Vector<Fragment>();	 								//ADD THE FRAGMENTS CORRESPONDING
		 fragments.add(TextPageFragment.newInstance(text_data_list));						//TO THE THREE DIFFERENT LISTS
		 fragments.add(ImagePageFragment.newInstance(image_data_list));
		 fragments.add(BothPageFragment.newInstance(item_list));
		 
		 this.mPagerAdapter  = new PagerAdapter1(super.getSupportFragmentManager(), fragments);	 
		 ViewPager pager = (ViewPager)super.findViewById(R.id.viewpager);
	     pager.setAdapter(this.mPagerAdapter);
	     pager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
	         @SuppressLint("NewApi")
			 @Override
	         public void onPageSelected(int position) {										//LETS TABS KNOW WHEN VIEW HAS BEEN SWIPED
	             actionBar.setSelectedNavigationItem(position);
	         }
	     });
	}
	
	
	//THE WORKER CLASS WHERE WE WILL MAKE ALL HTTP CONNECTIONS,
	//CREATE THREE SEPARATE LISTS FOR DATA, AND SEND DATA BACK TO UI THREAD
	
	@TargetApi(Build.VERSION_CODES.GINGERBREAD)
	@SuppressLint("NewApi")
	private class ConnectionTask extends AsyncTask<Object, Object, Object>{

		String status;
		final String TYPE_TEXT = "text";
		final String TYPE_IMAGE = "image";
		final String COL_TYPE = "type";
		final String COL_DATA = "data";
		final String COL_ID = "id";
		
		//CALLED FROM UI THREAD UPON COMPLETION. MAKES THE CALL TO THE OUTSIDE FUNCTION
		@Override
		protected void onPostExecute(Object object){
			outsideFunction(status);
		}
		
		
		//CALLED IN THE WORKER THREAD. DO CONNECTIONS HERE
		@Override
		protected Object doInBackground(Object... arg0) {
			 
			status = STATUS_OK;
			
			final ArrayList<String> text_data_list_task= new ArrayList<String>();
		    final ArrayList<Bitmap> image_data_list_task= new ArrayList<Bitmap>();
		    final ArrayList<dataItem> item_list_task= new ArrayList<dataItem>();
			
		    
		    //GET THE JSON DOCUMENT FROM THE SERVER. IF NO SUCH DOCUMENT WAS RECIEVED, RETURN FROM
		    //WORKER THREAD FUNCTION WITH AN ERROR FLAG
	        final String jSON = getJSON("http://dev.fuzzproductions.com/MobileTest/test.json", 1000);
	        	if (jSON.equals(ERROR_CONNECTION)){				
	        		status = STATUS_NO_JSON;
	        		return null;
	        	}
  
	        JSONArray jArray = null;
	        try {
				jArray = new JSONArray(jSON);			
			} catch (JSONException e) {
				e.printStackTrace();
			}
	        
	        ArrayList<BigInteger> id_list = new ArrayList<BigInteger>();        
	        for (int i = 0; i < jArray.length(); i ++){	        				//ITERATE THROUGH THE OBJECTS IN THE ARRAY OF 
	        																	//DATA FROM THE JSON DOCUMENT
	        	try {
					JSONObject oneObject = jArray.getJSONObject(i);
					String id_string = oneObject.getString(COL_ID);
					BigInteger id = new BigInteger(id_string);				
					if (id_list.contains(id)){									//ENSURES UNIQUE ID
						break;
					}
					else{
						id_list.add(id);
					}
		
					String type = oneObject.getString(COL_TYPE);				
					if (type.equals(TYPE_TEXT)){								//IF STRING, LOAD DATA DIRECTLY FROM THE JSON

						String data_text = oneObject.getString(COL_DATA);
						if (data_text.equals("")){
							data_text = "NO TEXT TO LOAD";
						}
						text_data_list_task.add(data_text);
						item_list_task.add(new dataItem(data_text));

					}
					if (type.equals(TYPE_IMAGE)){								//IF IMAGE MUST OPEN NEW CONNECTION
						
						String data_url_text = oneObject.getString(COL_DATA);
						Bitmap data_bitmap = null;
						InputStream stream = null;
						try {
							
							URL data_url = new URL(data_url_text);
							HttpURLConnection httpConnection = null;
							try {
								
								httpConnection = (HttpURLConnection) data_url.openConnection();
				                httpConnection.setRequestMethod("GET");
				                httpConnection.connect();			 
				                if (httpConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
				                    stream = httpConnection.getInputStream();
				                    if (stream == null){
				                    	continue;
				                    }
				                }
				                else{
				                	continue;
				                }
				                
				            } catch (Exception ex) {
				                ex.printStackTrace();
				            }
							
							BitmapFactory.Options bmOptions = new BitmapFactory.Options();			//CREATE NEW BITMAP TO STORE
				            bmOptions.inSampleSize = 1;
							data_bitmap = BitmapFactory.decodeStream(stream, null, bmOptions);		//DECODE THE INPUT STREAM FROM THE
																									//HTTP CONNECTION
							
							if (data_bitmap == null){												//IF THERE IS NO IMAGE DATA
								Log.d("decoding stream", "unable to decode");
								continue;
							}
							httpConnection.disconnect();
							
						} catch (MalformedURLException e) {
							e.printStackTrace();
						}
						image_data_list_task.add(data_bitmap);										
						item_list_task.add(new dataItem(data_bitmap));			
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}	        	
	        }
	        
	        runOnUiThread(new Runnable(){							//RUNS ON UI THREAD FROM WORKER THREAD 
	        	
				@Override
				public void run() {									//COPY THE DATA FROM THE WORKER THREAD TO		
					text_data_list = text_data_list_task;			//THE MAIN THREAD
					image_data_list = image_data_list_task;
					item_list = item_list_task;	
				}  	
	        });	
			return null;
		}
	
		//RUNS WITHIN THE WORKER THREAD
		public String getJSON(String url, int timeout) {
		    try {	    	
		        URL u = new URL(url);
		        InputStream is;
		        HttpURLConnection c = (HttpURLConnection) u.openConnection();
		        c.setRequestMethod("GET");
		        c.setRequestProperty("Content-length", "0");
		        c.setUseCaches(false);
		        c.setAllowUserInteraction(false);
		        c.setConnectTimeout(timeout);
		        c.setReadTimeout(timeout);        
		        is = c.getInputStream();
		        int status = c.getResponseCode();
		        c.connect();

		        switch (status) {
		            case 200:
		            case 201:
		                BufferedReader br = new BufferedReader(new InputStreamReader(is));
		                StringBuilder sb = new StringBuilder();
		                String line;
		                while ((line = br.readLine()) != null) {
		                    sb.append(line+"\n");
		                }
		                br.close();
		                c.disconnect();
		                return sb.toString();
		            default:
		            	Log.d("search","not ok");
		            	c.disconnect();
		            	return ERROR_CONNECTION;					//RETURN ERROR IF THE RESULT CODE WAS ANYTHING
		            												//BUT A 200 NUMBER
		        }	        

		    } catch (MalformedURLException ex) {					
		    	Log.e("inService", "malformed URL");
		    } catch (UnknownHostException e) {						//ERROR IF THERE IS NO NETWORK CONNECTION
	            Log.d("ukhe","Check Internet Connection!!!");
	            return ERROR_CONNECTION;
		    } catch (IOException e) {
		    	Log.d("IO","IO!");		    	
				e.printStackTrace();
		    	Log.d("IO","still doing stuff");		    	
				return ERROR_CONNECTION;
			} 
		    return null;
		}
	}

	//INFALTE THE ACTION BAR
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
