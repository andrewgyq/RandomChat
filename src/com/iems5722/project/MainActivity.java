package com.iems5722.project;

import java.io.IOException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends Activity {
	
	private Button button;
	private TextView textView;
	private String serverUrl = "http://54.169.108.112:3000/";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		button = (Button) findViewById(R.id.button);
		
		button.setOnClickListener(new View.OnClickListener() {
		    @Override
		    public void onClick(View v) {
		    	findMatchingUser();
		    }

		});
	}
	
	
	protected void findMatchingUser() {
		new AsyncTask< Void, Void, String>(){

			@Override
			protected String doInBackground(Void... params) {
				HttpClient client = new DefaultHttpClient();
				
				HttpGet request = new HttpGet(serverUrl);
				HttpResponse response = null;
				try {
					response = client.execute(request);
				} catch (ClientProtocolException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
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
		        textView = (TextView) findViewById(R.id.targetUrl);
		        textView.setText(targetUrl);
		    }
			
		}.execute(null, null, null);
	}

}
