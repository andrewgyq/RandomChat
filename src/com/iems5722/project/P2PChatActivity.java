package com.iems5722.project;

import java.io.ByteArrayOutputStream;
import java.lang.reflect.Field;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Base64;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

public class P2PChatActivity extends Activity {
	private Activity  activity;
	private Button button;
	private EditText editText;
	private Socket mSocket;
	private String nickName;
	private ListView  chatView;
	private Button imageButton;
	private static final int CAMERA_SELECT = 1;
	private ArrayList<ChatMessage>  list = new ArrayList<ChatMessage>();
	
	{
	    try {
	        mSocket = IO.socket("http://52.74.25.92:3000");
	    } catch (URISyntaxException e) {}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_p2p_chat);
		 // show overflow button
        setOverflowButtonAlways();
        // hide app icon
		getActionBar().setDisplayShowHomeEnabled(false);
		activity = this;
		activity.setTitle("Private Chat");
		Intent intent = getIntent();
		nickName = intent.getStringExtra("nickname");
		
		imageButton = (Button) findViewById(R.id.p2pimage);
		imageButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				sendImage();
			}
		});
		
		button = (Button) findViewById(R.id.P2PButton);
		button.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				attemptSend();
			}
		});
		
		mSocket.on("private", onPrivate);
		mSocket.on("private image", onPrivateImage);
		mSocket.on("private leave", onPrivateLeave);
		mSocket.connect();
	}
	
	private void attemptSend() {
	    
		editText = (EditText) findViewById(R.id.P2PEditText);
		
		String message = editText.getText().toString().trim();
	    if (TextUtils.isEmpty(message)) {
	    	Toast.makeText(activity, "Please input message!", Toast.LENGTH_LONG).show();
	        return;
	    }  
        editText.setText("");  
        StringBuffer sb = new StringBuffer(nickName);
        sb.append(":");
        sb.append(message);
        mSocket.emit("private", sb.toString());
        
        chatView = (ListView) findViewById(R.id.ChatView);
        
        StringBuffer messagesb = new StringBuffer(nickName);
        messagesb.append(" (");
        messagesb.append(getDate());
        messagesb.append("): ");
		ChatMessage chatMessage = new ChatMessage(messagesb.toString(), 
				message, null, R.layout.sender_layout);
        list.add(chatMessage);
        chatView.setAdapter(new ChatMessageViewAdapter(P2PChatActivity.this,list));
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
                    mSocket.emit("private image", nickName + ":" + imageDataString);
                    
                    chatView = (ListView) findViewById(R.id.ChatView);
	    			StringBuffer sb = new StringBuffer(nickName);
	    			sb.append(" (");
	    			sb.append(getDate());
	    			sb.append("): ");
	    			ChatMessage chatMessage = new ChatMessage(sb.toString(), 
    		        		null, photo, R.layout.sender_layout); ;
	    		    list.add(chatMessage);
	    		    chatView.setAdapter(new ChatMessageViewAdapter(P2PChatActivity.this,list));
	    		    
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
	
	private Emitter.Listener onPrivate = new Emitter.Listener() {
	    @Override
	    public void call(final Object... args) {
	    	activity.runOnUiThread(new Runnable() {
	    		 @Override
	             public void run() {
	    			 chatView = (ListView) findViewById(R.id.ChatView);
		    			String message = (String) args[0];
		    			StringBuffer sb = new StringBuffer(message.split(":")[0]);
		    			
		    			sb.append(" (");
		    			sb.append(getDate());
		    			sb.append("): ");
		    			
		    			ChatMessage chatMessage = new ChatMessage(sb.toString(), 
			    				message.split(":")[1], null, R.layout.receiver_layout);
	    		        list.add(chatMessage);
	    		        chatView.setAdapter(new ChatMessageViewAdapter(P2PChatActivity.this,list));
	    		 }
	    	});
	    }
	};
	
	private Emitter.Listener onPrivateImage = new Emitter.Listener() {
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
	    			 ChatMessage chatMessage = new ChatMessage(sb.toString(), 
     		        		null, bitmap, R.layout.receiver_layout); ;
	    		     list.add(chatMessage);
	    		     chatView.setAdapter(new ChatMessageViewAdapter(P2PChatActivity.this,list)); 
	    		 }
	    	});
	    }
	};
	
	private Emitter.Listener onPrivateLeave = new Emitter.Listener() {
	    @Override
	    public void call(final Object... args) {
	    	activity.runOnUiThread(new Runnable() {
	    		 @Override
	             public void run() {
	    			 if(nickName.equals((String)args[0])){
	    				 return;
	    			 }else{
	    				 Toast.makeText(activity,  (String)args[0] + " is disconnected", Toast.LENGTH_LONG).show();
	    				 mSocket.off("private", onPrivate);
	    				 activity.finish();
	    			 }
	    		 }
	    	});
	    }
	};
	
	
	@Override
	public void onDestroy() {
	    super.onDestroy();
	    mSocket.off("private", onPrivate);
	    activity.finish();
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
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu){
		getMenuInflater().inflate(R.menu.p2pchat, menu);
		return true;
	}
	
	public boolean onOptionsItemSelected (MenuItem item){
		switch(item.getItemId()){  
        case R.id.action_exit:  
        	mSocket.emit("private leave", nickName);
        	mSocket.off("private", onPrivate);
    	    activity.finish();
            break;  
        }  
        return true;
	}
}
