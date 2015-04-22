package com.iems5722.project;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;
import com.github.nkzawa.emitter.Emitter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Base64;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewConfiguration;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

public class ChatActivity extends Activity {
	
	private Button button;
	private Button imageButton;
	private EditText editText;
	private ListView  chatView;
	private Activity  activity;
	private ArrayList<ChatMessage>  list = new ArrayList<ChatMessage>();
	private String nickName;
	private static final int CAMERA_SELECT = 1;
	
	private Socket mSocket;
	{
	    try {
	        mSocket = IO.socket("http://52.74.25.92:3000");
	    } catch (URISyntaxException e) {}
	}
	
	private Emitter.Listener onNewMessage = new Emitter.Listener() {
	    @Override
	    public void call(final Object... args) {
	    	activity.runOnUiThread(new Runnable() {
	    		 @Override
	             public void run() {
	    			chatView = (ListView) findViewById(R.id.ChatView);
	    			String message = (String) args[0];
	    			StringBuffer sb = null;
	    			int layout;
	    			if(nickName.equals(message.split(":")[0])){
	    				sb = new StringBuffer(nickName);
	    				layout = R.layout.sender_layout;
	    			}else{
	    				sb = new StringBuffer(message.split(":")[0]);
	    				layout = R.layout.receiver_layout;
	    			}
	    			
	    			sb.append(" (");
	    			sb.append(getDate());
	    			sb.append("): ");
	    			
	    			ChatMessage chatMessage = new ChatMessage(sb.toString(), 
		    				message.split(":")[1], null, layout);
    		        list.add(chatMessage);
    		        chatView.setAdapter(new ChatMessageViewAdapter(ChatActivity.this,list)); 
	    		 }
	    	});
	    }
	};
	
	private Emitter.Listener onNewImage = new Emitter.Listener() {
	    @Override
	    public void call(final Object... args) {
	    	activity.runOnUiThread(new Runnable() {
	    		 @Override
	             public void run() {
	    			 chatView = (ListView) findViewById(R.id.ChatView);
	    			 String message = (String)args[0];
	    			 StringBuffer sb = new StringBuffer(message.split(":")[0]);
	    			 sb.append(" (");
	    			 sb.append(getDate());
	    			 sb.append("): ");
	    			 byte[] imageByte = Base64.decode(message.split(":")[1], Base64.DEFAULT);
	    			 Bitmap bitmap = BitmapFactory.decodeByteArray(imageByte, 0, imageByte.length);
	    			 ChatMessage chatMessage = null;
	    			 if(nickName.equals(message.split(":")[0]))
	    				 chatMessage = new ChatMessage(sb.toString(), 
	    						 null, bitmap, R.layout.sender_layout); 
	    			 else
	    				 chatMessage = new ChatMessage(sb.toString(), 
	    						 null, bitmap, R.layout.receiver_layout);
	    		     list.add(chatMessage);
	    		     chatView.setAdapter(new ChatMessageViewAdapter(ChatActivity.this,list)); 
	    		 }
	    	});
	    }
	};
	
	private Emitter.Listener onNewUser = new Emitter.Listener() {
	    @Override
	    public void call(final Object... args) {
	    	activity.runOnUiThread(new Runnable() {
	    		 @Override
	             public void run() {
	    			 activity.setTitle("Online User: " + args[0]);
	    		 }
	    	});
	    }
	};
	
	private Emitter.Listener onDisconnect = new Emitter.Listener() {
	    @Override
	    public void call(final Object... args) {
	    	activity.runOnUiThread(new Runnable() {
	    		 @Override
	             public void run() {
	    			 Toast.makeText(activity, "Someone is disconnected!", Toast.LENGTH_LONG).show();
	    			 activity.setTitle("Online User: " + args[0]);
	    		 }
	    	});
	    }
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        // show overflow button
        setOverflowButtonAlways();
        // hide app icon
		getActionBar().setDisplayShowHomeEnabled(false);
		
		activity = this;
		Intent intent = getIntent();
		nickName = intent.getStringExtra("nickname");
		
		imageButton = (Button) findViewById(R.id.image);
		imageButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				sendImage();
			}
		});
		
		button = (Button) findViewById(R.id.Button);
		button.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				attemptSend();
			}
		});
		
		mSocket.on("new message", onNewMessage);
		mSocket.on("new image", onNewImage);
		mSocket.on("new user", onNewUser);
		mSocket.on("disconnect", onDisconnect);
		mSocket.emit("new user", "");
		mSocket.connect();
	}
	
	private void attemptSend() {
	    
		editText = (EditText) findViewById(R.id.EditText);
		
		String message = editText.getText().toString().trim();
	    if (TextUtils.isEmpty(message)) {
	    	Toast.makeText(activity, "Please input message!", Toast.LENGTH_LONG).show();
	        return;
	    }  
				
        editText.setText("");  
        StringBuffer sb = new StringBuffer(nickName);
        sb.append(":");
        sb.append(message);
        mSocket.emit("new message", sb.toString());
	}
	
	private void sendImage() {
		Intent album = new Intent(Intent.ACTION_GET_CONTENT);
		album.setType("image/*");
		startActivityForResult(album, CAMERA_SELECT);
		
	}
	
	@Override  
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {  
        super.onActivityResult(requestCode, resultCode, data); 
        if (resultCode == RESULT_OK) {  
            switch (requestCode) {  
            case CAMERA_SELECT:  
            	ContentResolver resolver = getContentResolver(); 
                // image address  
                Uri imgUri = data.getData();  
                try {
                	Bitmap photo = MediaStore.Images.Media.getBitmap(resolver,  
                            imgUri);
                	ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                	photo.compress(Bitmap.CompressFormat.JPEG, 90, outputStream );
                    byte imageData[] = outputStream.toByteArray();
                    // Converting Image byte array into Base64 String
                    String imageDataString = Base64.encodeToString(imageData, Base64.DEFAULT);
                    mSocket.emit("new image", nickName + ":" + imageDataString);
                }catch (Exception e){
                	e.printStackTrace();
                }
                break;  
            }  
        }  
    }  

	@SuppressLint("SimpleDateFormat")
	protected String getDate() {
		SimpleDateFormat sdf = new SimpleDateFormat("MM-dd HH:mm");
		Date date = new Date();
		return sdf.format(date);
	}
	
	@Override
	public void onDestroy() {
	    super.onDestroy();

	    mSocket.disconnect();
	    mSocket.off("new message", onNewMessage);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu){
		getMenuInflater().inflate(R.menu.chat, menu);
		return true;
	}
	
	public boolean onOptionsItemSelected (MenuItem item){
		switch(item.getItemId()){  
        case R.id.action_private_chat:  
            startPrivateChat();
            break;  
        case R.id.action_exit:  
        	exit();
            break;  
        }  
        return true;
	}
	
	private void exit(){
		activity.finish();
		onDestroy();
	}
	
	private void startPrivateChat() {
		new AsyncTask< Void, Void, String>(){
			ProgressDialog dialog = new ProgressDialog(activity);
			
			@Override
		    protected void onPreExecute() {
		        dialog.setMessage("Matching, please wait!");
		        dialog.show();
		    }
			
			@Override
			protected String doInBackground(Void... params) {
				HttpResponse response = null;
				String targetUrl = "";
				int requestCount = 0;
				HttpClient client = new DefaultHttpClient();
				HttpGet request = new HttpGet("http://52.74.25.92:3000/private");
				
				while("false".equals(targetUrl.trim()) || targetUrl == ""){
					requestCount = requestCount + 1;
					
					try {
						response = client.execute(request);
					} catch (ClientProtocolException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}
					
					HttpEntity entity = response.getEntity();
					
					try {
						targetUrl = EntityUtils.toString(entity);
						Thread.sleep(1000);
						if(requestCount == 10)
							return "false";
						
					} catch (ParseException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				
				return targetUrl;
			}
			
			@Override
		    protected void onPostExecute( String targetUrl) {
		        super.onPostExecute(targetUrl);
		        if (dialog.isShowing()) {
		            dialog.dismiss();
		        }
		        if("false".equals(targetUrl)){
		        	Toast.makeText(activity, "No one can match!", Toast.LENGTH_LONG).show();
		        	return;
		        }
		        
	    	   	Intent myIntent = new Intent(ChatActivity.this, P2PChatActivity.class);
		       	myIntent.putExtra("targetUrl", targetUrl);
		       	myIntent.putExtra("nickname", nickName);
		       	ChatActivity.this.startActivity(myIntent);
		    }
			
		}.execute(null, null, null);
		
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

}
