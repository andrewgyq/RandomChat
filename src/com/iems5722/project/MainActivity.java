package com.iems5722.project;

import java.io.IOException;
import java.lang.reflect.Field;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends Activity {
	
	private Button button;
	private EditText editText;
	private String serverUrl = "http://52.74.25.92:3000/";
	private Activity activity;
	private int CONNECTION_TIMEOUT = 5000;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		// show overflow button
        setOverflowButtonAlways();
        // hide app icon
		getActionBar().setDisplayShowHomeEnabled(false);
		
		activity = this;
		button = (Button) findViewById(R.id.button);
		editText = (EditText) findViewById(R.id.nickname);
		
		button.setOnClickListener(new View.OnClickListener() {
		    @Override
		    public void onClick(View v) {
		    	String nickname = editText.getText().toString().trim();
		    	if (TextUtils.isEmpty(nickname)) {
		    		Toast.makeText(MainActivity.this, "Please input your nickname!", Toast.LENGTH_LONG).show();
			        return;
			    }  
		    	connectServer();
		    }

		});
	}
	
	
	protected void connectServer() {
		new AsyncTask< Void, Void, String>(){
			
			ProgressDialog dialog = new ProgressDialog(activity);
			
			@Override
		    protected void onPreExecute() {
		        dialog.setMessage("Connecting to server...");
		        dialog.show();
		    }
			
			@Override
			protected String doInBackground(Void... params) {
				final HttpParams httpParams = new BasicHttpParams();
			    HttpConnectionParams.setConnectionTimeout(httpParams, CONNECTION_TIMEOUT);
				HttpClient client = new DefaultHttpClient(httpParams);
				HttpGet request = new HttpGet(serverUrl);
				HttpResponse response = null;
				try {
					response = client.execute(request);
				} catch (ClientProtocolException e) {
					e.printStackTrace();
				} catch (IOException e) {
					return "false";
				}
				
				HttpEntity entity = response.getEntity();
				
				String targetUrl = null;
				try {
					targetUrl = EntityUtils.toString(entity);
				} catch (ParseException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
				return targetUrl;
			}
			
			@Override
		    protected void onPostExecute( String targetUrl) {
		        super.onPostExecute(targetUrl);
		        dialog.hide();
		        if("false".equals(targetUrl)){
		        	Toast.makeText(MainActivity.this, "Server is not avaliable!", Toast.LENGTH_LONG).show();
		        	return;
		        }
		        
		        Intent myIntent = new Intent(MainActivity.this, ChatActivity.class);
		        myIntent.putExtra("nickname", editText.getText().toString().trim());
		        MainActivity.this.startActivity(myIntent);
		    }
			
		}.execute(null, null, null);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu){
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	private void setOverflowButtonAlways(){
		try{
			ViewConfiguration config = ViewConfiguration.get(this);
			Field menuKey = ViewConfiguration.class
					.getDeclaredField("sHasPermanentMenuKey");
			menuKey.setAccessible(true);
			menuKey.setBoolean(config, false);
		} catch (Exception e){
			e.printStackTrace();
		}
	}
	
	public boolean onOptionsItemSelected (MenuItem item){
		switch(item.getItemId()){  
        case R.id.action_exit:  
        	activity.finish();
            break;  
        }  
        return true;
	}

}
