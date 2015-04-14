package com.iems5722.project;

import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

public class P2PChatActivity extends Activity {
	private Activity  activity;
	private Button button;
	private EditText editText;
	private Socket mSocket;
	private String nickName;
	private ListView  chatView;
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
		activity = this;
		
		Intent intent = getIntent();
		nickName = intent.getStringExtra("nickname");
		
		button = (Button) findViewById(R.id.P2PButton);
		button.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				attemptSend();
			}
		});
		
		mSocket.on("private", onPrivate);
		mSocket.connect();
	}
	
	private void attemptSend() {
	    
		editText = (EditText) findViewById(R.id.P2PEditText);
		
		String message = editText.getText().toString().trim();
	    if (TextUtils.isEmpty(message)) {
	        return;
	    }  
	    message = nickName + ": " + message;
        editText.setText("");  
        chatView = (ListView) findViewById(R.id.ChatView);
		String date = getDate();
		ChatMessage chatMessage = new ChatMessage(date, message, R.layout.sender_layout); 
        list.add(chatMessage);
        chatView.setAdapter(new ChatMessageViewAdapter(P2PChatActivity.this,list)); 
        mSocket.emit("private", message);
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
	    			String date = getDate();
	    			String message = (String) args[0];
	    			ChatMessage chatMessage = new ChatMessage(date, message, R.layout.receiver_layout);
    		        list.add(chatMessage);
    		        chatView.setAdapter(new ChatMessageViewAdapter(P2PChatActivity.this,list));
	    		 }
	    	});
	    }
	};
	
	@Override
	public void onDestroy() {
	    super.onDestroy();
	    mSocket.disconnect();
	    mSocket.off("private", onPrivate);
	}
}
