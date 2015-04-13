package com.iems5722.project;

import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;
import com.github.nkzawa.emitter.Emitter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

public class ChatActivity extends Activity {
	
	private Button button;
	private EditText editText;
	private ListView  chatView;
	private Activity  activity;
	private ArrayList<ChatMessage>  list = new ArrayList<ChatMessage>();
	private String nickName;
	
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
	    			String date = getDate();
    		    	int receiveLayout = R.layout.receiver_layout;  
    		        ChatMessage receiveMessage = new ChatMessage(date, (String)args[0], receiveLayout);  
    		        list.add(receiveMessage);
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
	    			 activity.setTitle(nickName + "  online user: " + args[0]);
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
	    			 activity.setTitle(nickName + "  online user: " + args[0]);
	    		 }
	    	});
	    }
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.activity_chat);
		//getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.titlebtn);  
		activity = this;
		Intent intent = getIntent();
		nickName = intent.getStringExtra("nickname");
		
		
		button = (Button) findViewById(R.id.Button);
		button.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				attemptSend();
			}
		});
		
		mSocket.on("new message", onNewMessage);
		mSocket.on("new user", onNewUser);
		mSocket.on("disconnect", onDisconnect);
		mSocket.emit("new user", "");
		mSocket.connect();
	}
	
	private void attemptSend() {
	    
		editText = (EditText) findViewById(R.id.EditText);
		
		String message = editText.getText().toString().trim();
	    if (TextUtils.isEmpty(message)) {
	        return;
	    }  
				
//		int sendLayout = R.layout.sender_layout;  
//		ChatMessage sendMessage = new ChatMessage(date, message, sendLayout);  
//        list.add(sendMessage);  
//        
//        chatView.setAdapter(new ChatMessageViewAdapter(ChatActivity.this,list));  
        editText.setText("");  
        mSocket.emit("new message", message);
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

}
