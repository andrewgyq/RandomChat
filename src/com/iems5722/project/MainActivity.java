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
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends Activity {
	
	private Button button;
	private EditText editText;
	private String serverUrl = "http://52.74.25.92:3000/";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
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
		        
		        Intent myIntent = new Intent(MainActivity.this, ChatActivity.class);
		        myIntent.putExtra("nickname", editText.getText().toString().trim());
		        MainActivity.this.startActivity(myIntent);
		    }
			
		}.execute(null, null, null);
	}

}
